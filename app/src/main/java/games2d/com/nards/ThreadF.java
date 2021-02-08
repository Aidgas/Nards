package games2d.com.nards;

public class ThreadF extends Thread
{
    public boolean run = true;

    @Override
    public void run()
    {
        while( run )
        {
            callback_run();
        }
    }

    public void callback_run() {}
}
