package games2d.com.nards;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sk on 18.03.16.
 */
public class QueueNetworkBluetooth
{
    public static Integer NET_WORK__OUTPUT_PRIORITY_HIGH_2  =  9;
    public static Integer NET_WORK__OUTPUT_PRIORITY_HIGH    =  10;
    public static Integer NET_WORK__OUTPUT_PRIORITY_MEDIUM  =  50;
    public static Integer NET_WORK__OUTPUT_PRIORITY_LOW     =  100;

    public final static byte LIMIT_COUNT_SENDED  = 5;
    private final static long INTERVAL_SEND       = 3100; // in millisecond

    // буффер входяших комманд
    // регистрирует номера обработанных пакетов с сервера
    // регистрация нужна чтобы исключить повторную обработку
    private List<Long> input_requests_packets = new ArrayList<>();

    // буффер на отправку команд
    private class OutputData
    {
        long timestamp; /// 8 байт
        byte count_sended = 0;
        long time_sendned_last = 0;

        String cmd;
        ArrayList<String> params;
        byte[] add_buffer;
        int priority;
        int limit_send = LIMIT_COUNT_SENDED;

        boolean wait_send = false;
    }

    private List<OutputData> output_requests_pakets = new ArrayList<OutputData>();

    static class theLock extends Object { }
    static public theLock lockObject = new theLock();
    static public theLock lockInput  = new theLock();
    static public theLock thread_output_block  = new theLock();

    private BluetoothService network = null;

    private ThreadOutput thread_output = new ThreadOutput();

    //private boolean clear_all = false;

    public QueueNetworkBluetooth(BluetoothService _network)
    {
        network = _network;
        thread_output.start();
    }

    public void OnDestroy()
    {
        if( thread_output != null )
        {
            thread_output.running = false;
            thread_output = null;
        }
    }

    public void okSend(long timestamp)
    {
        synchronized (lockObject)
        {
            for(int i = 0; i < output_requests_pakets.size(); i++)
            {
                if( output_requests_pakets.get(i).timestamp == timestamp )
                {
                    output_requests_pakets.remove(i);
                    break;
                }
            }
        }
    }

    private boolean found_timestamp(long timestamp)
    {
        synchronized (lockObject)
        {
            for(int i = 0; i < output_requests_pakets.size(); i++)
            {
                if( output_requests_pakets.get(i).timestamp == timestamp )
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean found_timestamp2(long timestamp)
    {
        //synchronized (lockObject)
        {
            for(int i = 0; i < output_requests_pakets.size(); i++)
            {
                if( output_requests_pakets.get(i).timestamp == timestamp )
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void cliearAll()
    {
        //synchronized (thread_output_block)
        {
            synchronized (lockObject)
            {
                output_requests_pakets.clear();
            }

            synchronized (lockInput)
            {
                input_requests_packets.clear();
            }
        }
    }



    public void add(String cmd, ArrayList<String> params, byte[] add_buffer, long timestamp, int priority, int limit_send)
    {
        OutputData out = new OutputData();
        out.cmd = cmd;
        out.params = params;

        out.timestamp = timestamp;

        /*while( found_timestamp(out.timestamp) )
        {
            out.timestamp += 1;
        }*/

        out.add_buffer = add_buffer;

        out.priority = priority;

        synchronized (lockObject)
        {
            /// поиск дублирования метки времени
            while( found_timestamp2(out.timestamp) )
            {
                Log.i("TAG", "found_timestamp2");
                out.timestamp += 100;
            }

            output_requests_pakets.add( out );

            List<OutputData> _tmp = new ArrayList<OutputData>(this.output_requests_pakets);

            for(int i=0; i < _tmp.size() - 1; i++)
            {
                for(int j=i+1; j < _tmp.size(); j++)
                {
                    if(
                            (_tmp.get(i).priority > _tmp.get(j).priority)
                                    ||
                                    (
                                            (_tmp.get(i).priority == _tmp.get(j).priority)
                                                    && (_tmp.get(i).timestamp > _tmp.get(j).timestamp)
                                    )
                            )
                    {
                        OutputData _t1 = _tmp.get(j);

                        _tmp.set(j, _tmp.get(i) );
                        _tmp.set(i, _t1);
                    }
                }
            }

            /*Log.i("!!", "------------------------------------------");
            for(int i=0; i < _tmp.size(); i++)
            {
                Log.i("!!", "::: " + _tmp.get(i).cmd + " - " + String.valueOf(_tmp.get(i).priority));
            }

            Log.i("TAG", "ADD OK: " + out.cmd + " " + String.valueOf(out.timestamp));*/

            this.output_requests_pakets = _tmp;
        }
    }

    public boolean foundInput(long timestamp)
    {
        synchronized (lockInput)
        {
            for(int i = 0; i < input_requests_packets.size(); i++)
            {
                if( input_requests_packets.get(i) == timestamp )
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void addInput(long timestamp)
    {
        synchronized (lockInput)
        {
            input_requests_packets.add(timestamp);
        }
    }

    public void stopWait(long timestamp)
    {
        synchronized (lockObject)
        {
            for(int i = 0; i < output_requests_pakets.size(); i++)
            {
                if (output_requests_pakets.get(i).timestamp == timestamp)
                {
                    output_requests_pakets.get(i).wait_send = true;
                    output_requests_pakets.get(i).time_sendned_last = System.currentTimeMillis();
                }
            }
        }
    }

    public void unStopWait(long timestamp)
    {
        synchronized (lockObject)
        {
            for(int i = 0; i < output_requests_pakets.size(); i++)
            {
                if (output_requests_pakets.get(i).timestamp == timestamp)
                {
                    output_requests_pakets.get(i).wait_send = false;
                    output_requests_pakets.get(i).time_sendned_last = 0;
                }
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------

    private class ThreadOutput extends Thread
    {
        public boolean running = true;

        @Override
        public void run()
        {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1);

            /*try
            {
                Thread.sleep(400);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }*/

            boolean sended = false;
            int add_wait = 0;

            while( running )
            {
                    try
                    {
                        Thread.sleep( 300 + add_wait * 100 );
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    add_wait = 0;

                    //synchronized (thread_output_block)
                    {
                        synchronized (lockObject)
                        {
                            if (output_requests_pakets.size() > 0)
                            {
                                for (int f = 0; f < output_requests_pakets.size(); f++)
                                {
                                    if (network == null)
                                    {
                                        break;
                                    }

                                    OutputData t = output_requests_pakets.get(f);

                                    if (       ! t.wait_send
                                            && System.currentTimeMillis() - t.time_sendned_last > INTERVAL_SEND
                                            )
                                    {
                                        t.time_sendned_last = System.currentTimeMillis();

                                        if (network != null)
                                        {
                                            Log.i("TAG", "network.addOutBuffer " + t.cmd);

                                            //network.addOutBuffer(t.cmd, t.params, t.add_buffer, t.priority, t.timestamp);

                                            byte[] b_cmd = ProtokolUtils.get_buffer_command_encrypt(
                                                      t.cmd
                                                    , t.params
                                                    , t.add_buffer
                                                    , JniApi.dfp1()
                                                    , t.timestamp);

                                            network.write(b_cmd);
                                        }


                                        t.count_sended += 1;

                                        if (t.count_sended >= t.limit_send)
                                        {
                                            //synchronized (lockObject)
                                            {
                                                //Log.i("TAG", "output_requests_pakets.remove " + t.cmd + " " + String.valueOf(t.count_sended));
                                                output_requests_pakets.remove(t);
                                            }

                                            break;
                                        }

                                        break;
                                    }

                                    if( t.cmd.equalsIgnoreCase("OK_SEND") == false )
                                    {
                                        add_wait += 1;
                                        break;
                                    }

                                } // --for
                                //========
                            }
                        }
                    }
            }
        }
    }

    /*public boolean _exec2(final DataInputStream in_soket, final ReadCommand r, Socket sock, final Object param)
    {
        Log.i("TAG", "cmd: " + r.cmd);

        return true;
    }*/
}
