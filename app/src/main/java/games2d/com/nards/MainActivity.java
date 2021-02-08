package games2d.com.nards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import games2d.com.nards.other.SearchUserItem;
import games2d.com.nards.other.Utils;

public class MainActivity extends AppCompatActivity
{
    private static String VERSION_APP    = "";
    private static String PACKAGE_NAME   = "";

    private static final int RC_SIGN_IN = 9001;

    private float _scale_px;

    public final static int SV_PAGE_GAME               = 0x21;
    public final static int SV_PAGE_MAIN_MENU          = 0x22;
    public final static int SV_PAGE_INFO               = 0x23;
    public final static int SV_PAGE_CONNECTION_SERVER  = 0x24;
    public final static int SV_PAGE_AUTH               = 0x25;
    public final static int SV_PAGE_PRIVACY_POLICY     = 0x26;
    public final static int SV_PAGE_NETWORK_MENU       = 0x27;
    public final static int SV_PAGE_NETWORK_SEARCH     = 0x28;
    public final static int SV_PAGE_NETWORK_RAITING    = 0x29;

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_FAILED = 6;
    public static final int MESSAGE_CONNECTION_LOST   = 7;
    public static final int MESSAGE_DISCONECT         = 8;

    public static final int CLIENT_OUT_CONNECT   = 1;
    public static final int CLIENT_IN_CONNECT    = 2;

    private String HOST = "";
    private int TCP_PORT_SERVER_1 = 0;
    private int TCP_PORT_SERVER_2 = 0;

    public static final String DEVICE_NAME = "device_name";
    //public static final String TOAST = "toast";

    private SessionInfo session_info = new SessionInfo();

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT      = 2;
    private static final int REQUEST_ENABLE_BT_2    = 3;

    private MySurfaceView _draw_task  = null;
    private int sv_page = 0;
    private String idToken = "";

    private Typeface _fontApp;
    private Typeface _font_2;
    private int last_count_online = 0;

    private static final int MODE_GAME__LOCAL_TWO_PLAYERS       = 0x12;
    private static final int MODE_GAME__BLUETOOTH_TWO_PLAYERS   = 0x13;
    private static final int MODE_GAME__WITH_COMPUTER           = 0x14;
    private static final int MODE_GAME__NETWORK                 = 0x15;

    private static final int ACTION_NONE                    = 0x00;
    private static final int ACTION_START_GAME              = 0x01;
    private static final int ACTION_TROW_DICE_FIRST_WHITE   = 0x02;
    private static final int ACTION_TROW_DICE_FIRST_BLACK   = 0x03;
    private static final int ACTION_XOD_WHITE               = 0x04;
    private static final int ACTION_XOD_BLACK               = 0x05;
    private static final int ACTION_TROW_DICE_WHITE         = 0x06;
    private static final int ACTION_TROW_DICE_BLACK         = 0x07;

    private ProgressDialog global_progress;
    private AlertDialog global_alertDialog;
    private AppSettings app_setting;

    private ClassNetWork network = null;

    private boolean run_amination_page_game_tv_wait = false;

    private AccountInfo accout_info = new AccountInfo();
    private ThreadF thread_run_search_search = null;

    private GoogleApiClient mGoogleApiClient;

    private HashMap<Integer, SearchUserItem> hashMapSearchUsers = new HashMap<>();

    private int type_connection_network = 0;
    List<UiItemRating> list_rating = new ArrayList<>();

    private class BluetoothDeviceInfoItem
    {
        String name, id;

        public BluetoothDeviceInfoItem(String _name, String _id)
        {
            name = _name;
            id   = _id;
        }
    }

    private class UiItemRating
    {
        public int id;
        public String first_name, last_name, puctire;
        public Bitmap img = null;
        public ImageView iv;
        public int count_games, count_wins, count_draw, count_defeats, count_mars;
    }

    private class BlueToothInfo
    {
        // Name of the connected device
        private String mConnectedDeviceName = null;
        // Array adapter for the conversation thread
        private ArrayAdapter<String> mConversationArrayAdapter;

        private List<BluetoothDeviceInfoItem> searching_devices = new ArrayList<>();

        // String buffer for outgoing messages
        private StringBuffer mOutStringBuffer;
        // Local Bluetooth adapter
        private BluetoothAdapter mBluetoothAdapter = null;
        // Member object for the chat services
        private BluetoothService mChatService = null;
    }

    private BlueToothInfo _blueToothInfo = new BlueToothInfo();

    ///-------------------------------------------------------------------------

    private class FindItemEndFigures
    {
        public int dice_index;
        public int dice_value;
        public int pos;
    }

    private class FountCountFigures
    {
        int count;
        int pos_stop;

        public FountCountFigures(int _count, int _pos_stop)
        {
            count    = _count;
            pos_stop = _pos_stop;
        }
    }

    private class FromTo
    {
        int from;
        int to;

        public FromTo(int _from, int _to)
        {
            from = _from;
            to   = _to;
        }
    }

    private class XodDetail
    {
        int color_action_xod;
        List<FromTo> values = new ArrayList<>();
    }

    private class TrownDice
    {
        int value    = 0;     // кубик сгенерированный
        boolean exec = false; // игрок сделал ход кубиком

        public TrownDice(int _value, boolean _exec)
        {
            value = _value;
            exec  = _exec;
        }
    }

    private class GameData
    {
        int mode_game;
        int mode_board;
        int current_action_cmd;
        int rand_num_dice_first_white;
        int rand_num_dice_first_black;
        private MySurfaceView _game_ui_draw  = null;

        private int first_xod_color = 0;

        private int index_dice_for_exit_board = 0;

        //int rand_dice_1; // кубик 1
        //int rand_dice_2; // кубик 2

        //boolean exec_xod_dice_1 = false; // игрок сделал ход кубиком 1
        //boolean exec_xod_dice_2 = false; // игрок сделал ход кубиком 2

        private List<TrownDice> rand_dices = new ArrayList<>();
        private List<XodDetail> xods_log   = new ArrayList<>();

        int count_xod_white = 0;
        int count_xod_black = 0;

        int next_dice_value_1 = 0;
        int next_dice_value_2 = 0;

        XodDetail current_xod_log = null;

        MySurfaceView.CellValues current_select_figure = null;
        boolean figure_selected = false;
        boolean first_xod = true;

        private Bitmap _figure_white = null;
        private Bitmap _figure_black = null;

        private boolean mode_game_computer_with_computer = false;

        public GameData()
        {
            Bitmap bmp    = BitmapFactory.decodeResource( getResources(), R.drawable.figure_white );
            _figure_white         = bmp.createScaledBitmap(   bmp
                    , (int) ( 20.0f * _scale_px )
                    , (int) ( 20.0f * _scale_px )
                    , false);

            bmp    = BitmapFactory.decodeResource( getResources(), R.drawable.figure_black );
            _figure_black         =   bmp.createScaledBitmap(   bmp
                    , (int) ( 20.0f * _scale_px )
                    , (int) ( 20.0f * _scale_px )
                    , false);
        }

        public int highlight_one_xod(int color)
        {
            List<MySurfaceView.CellValues> xods_found_cells = this.count_xods_from_color( color );

            if( xods_found_cells.size() == 1 )
            {
                this.figure_selected = false;
                this.select_cell( xods_found_cells.get(0), false );
                this._game_ui_draw.selected_cell = xods_found_cells.get(0).pos;

                return xods_found_cells.get(0).pos;
            }

            return -1;
        }

        public List<Integer> highlight_alternative_xods(int color)
        {
            List<MySurfaceView.CellValues> fods_found_cells = this.count_xods_from_color(color);

            List<Integer> res = new ArrayList<>();

            for(int i = 0; i < fods_found_cells.size(); i++)
            {
                res.add( fods_found_cells.get(i).pos );
            }

            return res;
        }

        public void exec_game()
        {
            if(this._game_ui_draw == null)
            {
                return;
            }

            if( current_action_cmd == ACTION_START_GAME )
            {
                this._game_ui_draw.set_text_print_top_left(
                        "Право первого хода"
                );

                current_action_cmd = ACTION_TROW_DICE_FIRST_WHITE;

                /// бросить кубики с белой стороны
                if( mode_board == MySurfaceView.MODE_COLOR_WHITE )
                {
                    //if( (int)(Math.random() * 2) == 0 )
                    {
                        if( (int)(Math.random() * 2) == 0 )
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_num_dice_first_white, 0x01, 1, MySurfaceView
                                    .PLAN_TYPE_PATH_11);

                            _game_ui_draw.addPlanAnimationInSlot(p);
                        }
                        else
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_num_dice_first_white,  0x02, 1, MySurfaceView
                                    .PLAN_TYPE_PATH_22);
                            _game_ui_draw.addPlanAnimationInSlot(p);
                        }
                    }
                }
                else
                {
                    //if( (int)(Math.random() * 2) == 0 )
                    {
                        if( (int)(Math.random() * 2) == 0 )
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_num_dice_first_white, 0x01, 1, MySurfaceView
                                    .PLAN_TYPE_PATH_3);

                            _game_ui_draw.addPlanAnimationInSlot(p);
                        }
                        else
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_num_dice_first_white,  0x02, 1, MySurfaceView
                                    .PLAN_TYPE_PATH_4);
                            _game_ui_draw.addPlanAnimationInSlot(p);
                        }
                    }
                }
            }
            else if( current_action_cmd == ACTION_TROW_DICE_FIRST_WHITE )
            {
                current_action_cmd = ACTION_TROW_DICE_FIRST_BLACK;

                /// бросить кубики с черной стороны
                if( mode_board == MySurfaceView.MODE_COLOR_WHITE )
                {
                    //if( (int)(Math.random() * 2) == 0 )
                    {
                        if( (int)(Math.random() * 2) == 0 )
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_num_dice_first_black, 0x01, 1, MySurfaceView
                                    .PLAN_TYPE_PATH_3);

                            _game_ui_draw.addPlanAnimationInSlot(p);
                        }
                        else
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_num_dice_first_black,  0x02, 1, MySurfaceView
                                    .PLAN_TYPE_PATH_4);
                            _game_ui_draw.addPlanAnimationInSlot(p);
                        }
                    }
                }
                else
                {
                    //if( (int)(Math.random() * 2) == 0 )
                    {
                        if( (int)(Math.random() * 2) == 0 )
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_num_dice_first_black, 0x01, 1, MySurfaceView
                                    .PLAN_TYPE_PATH_11);

                            _game_ui_draw.addPlanAnimationInSlot(p);
                        }
                        else
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_num_dice_first_black,  0x02, 1, MySurfaceView
                                    .PLAN_TYPE_PATH_22);
                            _game_ui_draw.addPlanAnimationInSlot(p);
                        }
                    }
                }
            }
            else if( current_action_cmd == ACTION_TROW_DICE_FIRST_BLACK )
            {
                if( rand_num_dice_first_black > rand_num_dice_first_white )
                {
                    if(
                            ( this.mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                    || (
                                            ( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS || this.mode_game == MODE_GAME__NETWORK )
                                            && this.mode_board == MySurfaceView.MODE_COLOR_WHITE
                                    )
                                    || (
                                            this.mode_game == MODE_GAME__WITH_COMPUTER
                                            //&& this.mode_board == MySurfaceView.MODE_COLOR_WHITE
                                        )
                            )
                    {
                        current_action_cmd = ACTION_TROW_DICE_BLACK;

                        next_dice_value_1 = 4;//randInt(1, 6);
                        next_dice_value_2 = 4;//randInt(1, 6);

                        current_xod_log = new XodDetail();
                        current_xod_log.color_action_xod = ACTION_XOD_BLACK;

                        first_xod_color = MySurfaceView.FIGURE_COLOR_BLACK;

                        ByteBuffer b_buf = ByteBuffer.allocate( 2 );

                        b_buf.put( (byte) next_dice_value_1 );         // 1 byte
                        b_buf.put( (byte) next_dice_value_2 );         // 1 byte

                        if( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                        {
                            quenue_network.add("ACTION_TROW_DICE_BLACK"
                                    , null
                                    , b_buf.array()
                                    , System.currentTimeMillis()
                                    , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                    , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                        }
                        else if( this.mode_game == MODE_GAME__NETWORK )
                        {
                            network.queue_network.add("ACTION_TROW_DICE_BLACK"
                                    , null
                                    , b_buf.array()
                                    , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                    , QueueNetwork.TYPE_SEND__FORCE);
                        }
                    }
                    else
                    {
                        current_action_cmd = ACTION_NONE;
                    }
                }
                else
                {
                    if(
                            ( this.mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                    || (
                                               ( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS || this.mode_game == MODE_GAME__NETWORK )
                                            && this.mode_board == MySurfaceView.MODE_COLOR_BLACK
                            )
                                    || (
                                    this.mode_game == MODE_GAME__WITH_COMPUTER
                                            //&& this.mode_board == MySurfaceView.MODE_COLOR_BLACK
                            )
                            )
                    {
                        current_action_cmd = ACTION_TROW_DICE_WHITE;

                        next_dice_value_1 = 4;//randInt(1, 6);
                        next_dice_value_2 = 4;//randInt(1, 6);

                        current_xod_log = new XodDetail();
                        current_xod_log.color_action_xod = ACTION_XOD_WHITE;

                        first_xod_color = MySurfaceView.FIGURE_COLOR_WHITE;

                        ByteBuffer b_buf = ByteBuffer.allocate( 2 );

                        b_buf.put( (byte) next_dice_value_1 );         // 1 byte
                        b_buf.put( (byte) next_dice_value_2 );         // 1 byte

                        if( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                        {
                            quenue_network.add("ACTION_TROW_DICE_WHITE"
                                    , null
                                    , b_buf.array()
                                    , System.currentTimeMillis()
                                    , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                    , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                        }
                        else if( this.mode_game == MODE_GAME__NETWORK )
                        {
                            network.queue_network.add("ACTION_TROW_DICE_WHITE"
                                    , null
                                    , b_buf.array()
                                    , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                    , QueueNetwork.TYPE_SEND__FORCE);
                        }
                    }
                    else
                    {
                        current_action_cmd = ACTION_NONE;
                    }

                }

                this.exec_game();
            }
            else if( current_action_cmd == ACTION_TROW_DICE_WHITE )
            {
                current_action_cmd = ACTION_XOD_WHITE;

                String v_print = "Ход белых";

                this._game_ui_draw.set_text_print_top_left(
                        v_print
                );

                this._game_ui_draw.set_icon_print_top_left( _figure_white );

                rand_dices.clear();
                rand_dices.add( new TrownDice(next_dice_value_1, false) );
                rand_dices.add( new TrownDice(next_dice_value_2, false) );

                //rand_dices.add( new TrownDice(6, false) );
                //rand_dices.add( new TrownDice(6, false) );

                // удвоение очков
                if(
                        ( rand_dices.get(0).value == rand_dices.get(1).value )

                        )
                {
                    rand_dices.add( new TrownDice( rand_dices.get(0).value, false ) );
                    rand_dices.add( new TrownDice( rand_dices.get(0).value, false ) );
                }

                current_select_figure = null;
                figure_selected = false;

                _game_ui_draw.clearPlanAnimationSlot();

                if( mode_board == MySurfaceView.MODE_COLOR_WHITE )
                {
                    MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_dices.get(0).value, 0x01, 10, MySurfaceView
                            .PLAN_TYPE_PATH_11);

                    _game_ui_draw.addPlanAnimationInSlot(p);

                    MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( rand_dices.get(1).value,  0x02, 10, MySurfaceView
                            .PLAN_TYPE_PATH_22);
                    _game_ui_draw.addPlanAnimationInSlot(p2);
                }
                else
                {
                    MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_dices.get(0).value, 0x01, 10, MySurfaceView
                            .PLAN_TYPE_PATH_3);

                    _game_ui_draw.addPlanAnimationInSlot(p);

                    MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( rand_dices.get(1).value,  0x02, 10, MySurfaceView
                            .PLAN_TYPE_PATH_4);
                    _game_ui_draw.addPlanAnimationInSlot(p2);
                }
            }
            else if( current_action_cmd == ACTION_TROW_DICE_BLACK )
            {
                current_action_cmd = ACTION_XOD_BLACK;

                String v_print = "Ход черных";


                this._game_ui_draw.set_text_print_top_left(
                        v_print
                );

                this._game_ui_draw.set_icon_print_top_left( _figure_black );

                rand_dices.clear();
                rand_dices.add( new TrownDice(next_dice_value_1, false) );
                rand_dices.add( new TrownDice(next_dice_value_2, false) );

                //rand_dices.add( new TrownDice(3, false) );
                //rand_dices.add( new TrownDice(3, false) );

                if(
                        ( rand_dices.get(0).value == rand_dices.get(1).value )

                        )
                {
                    rand_dices.add( new TrownDice( rand_dices.get(0).value, false ) );
                    rand_dices.add( new TrownDice( rand_dices.get(0).value, false ) );
                }

                current_select_figure = null;
                figure_selected = false;

                _game_ui_draw.clearPlanAnimationSlot();

                if( mode_board == MySurfaceView.MODE_COLOR_WHITE )
                {
                    MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_dices.get(0).value, 0x01, 10, MySurfaceView
                            .PLAN_TYPE_PATH_3);

                    _game_ui_draw.addPlanAnimationInSlot(p);

                    MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( rand_dices.get(1).value,  0x02, 10, MySurfaceView
                            .PLAN_TYPE_PATH_4);
                    _game_ui_draw.addPlanAnimationInSlot(p2);
                }
                else
                {
                    MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( rand_dices.get(0).value, 0x01, 10, MySurfaceView
                            .PLAN_TYPE_PATH_11);

                    _game_ui_draw.addPlanAnimationInSlot(p);

                    MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( rand_dices.get(1).value,  0x02, 10, MySurfaceView
                            .PLAN_TYPE_PATH_22);
                    _game_ui_draw.addPlanAnimationInSlot(p2);
                }
            }
            else if( current_action_cmd == ACTION_XOD_WHITE )
            {
                if(
                        ( this.mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                || (
                                ( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS || this.mode_game == MODE_GAME__NETWORK )
                                        && this.mode_board == MySurfaceView.MODE_COLOR_WHITE
                        )
                                || (
                                this.mode_game == MODE_GAME__WITH_COMPUTER
                                        && this.mode_board == MySurfaceView.MODE_COLOR_WHITE
                        )
                        )
                {
                    this._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_WHITE_OR_NONE;
                }
                else
                {
                    this._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_DISABLE_SELECT;
                }

                if( mode_game_computer_with_computer )
                {
                    this._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_DISABLE_SELECT;
                }

                this._game_ui_draw.selected_alternative_xods.clear();
                this._game_ui_draw.selected_alternative_xods_red.clear();

                final List<FindItemEndFigures> find_xods_all_in_home   = find_end_of_board_figures(MySurfaceView.FIGURE_COLOR_WHITE);
                if( find_xods_all_in_home.size() > 0 )
                {
                    for(int t = 0; t < find_xods_all_in_home.size(); t++)
                    {
                        this._game_ui_draw.selected_alternative_xods_red.add( find_xods_all_in_home.get(t).pos );
                    }
                }

                if( ! exists_xods_white() && find_xods_all_in_home.size() == 0 )
                {
                    change_color_game();
                }
                else
                {
                    if(        (this.mode_game == MODE_GAME__WITH_COMPUTER && mode_board == MySurfaceView.MODE_COLOR_BLACK)
                            || (this.mode_game == MODE_GAME__WITH_COMPUTER && mode_game_computer_with_computer) )
                    {
                        if( find_xods_all_in_home.size() > 0 )
                        {
                            WaitThread wt = new WaitThread(500)
                            {
                                @Override
                                public void callback()
                                {
                                    FindItemEndFigures find = find_xods_all_in_home.size() == 1? find_xods_all_in_home.get(0) : find_xods_all_in_home.get((int) (Math.random() * (find_xods_all_in_home.size() - 1)));

                                    MySurfaceView.CellValues cell = _game_ui_draw.get_cell( find.pos );

                                    CImageButton btn_pressed = null;

                                    if( mode_board == MySurfaceView.MODE_COLOR_WHITE)
                                    {
                                        if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                                        {
                                            btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                                        }
                                        else
                                        {
                                            btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                                        }
                                    }
                                    else
                                    {
                                        if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                                        {
                                            btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                                        }
                                        else
                                        {
                                            btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                                        }
                                    }

                                    Log.d("TAG", android.util.Log.getStackTraceString(new Exception()));

                                    rand_dices.get( find.dice_index ).exec = true;

                                    _game_ui_draw.slot_animation_dice_add_exec_num_dice( find.dice_value );

                                    _game._game_ui_draw.create_animation_figure_out_board(
                                            cell.pos
                                            , btn_pressed.getCenterPosX()
                                            , btn_pressed.getCenterPosY()
                                            , 7
                                            , 15
                                    );

                                    _game._game_ui_draw.get_cell( cell.pos ).count_figures -= 1;

                                    _game._game_ui_draw.action_animation_fly_figure = true;

                                    _game._game_ui_draw.clear_select_points();
                                    _game._game_ui_draw.clear_select_end_points();
                                    _game._game_ui_draw.selected_alternative_xods.clear();
                                    _game._game_ui_draw.selected_alternative_xods_red.clear();

                                }
                            };
                            wt.start();
                        }
                        else
                        {
                            WaitThread wt = new WaitThread(500)
                            {
                                @Override
                                public void callback()
                                {
                                    //есть ли ходы на выход
                                    /*List<FindItemEndFigures> find_xods_all_in_home = find_end_of_board_figures(MySurfaceView.FIGURE_COLOR_BLACK);
                                    if( find_xods_all_in_home.size() > 0 )
                                    {
                                        FindItemEndFigures f = find_xods_all_in_home.get(0);
                                        MySurfaceView.CellValues cell = _game_ui_draw.get_cell( f.pos );
                                        CImageButton btn_pressed = null;

                                        if( mode_board == MySurfaceView.MODE_COLOR_WHITE)
                                        {
                                            if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                                            {
                                                btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                                            }
                                            else
                                            {
                                                btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                                            }
                                        }
                                        else
                                        {
                                            if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                                            {
                                                btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                                            }
                                            else
                                            {
                                                btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                                            }
                                        }

                                        rand_dices.get( f.dice_index ).exec = true;

                                        _game_ui_draw.slot_animation_dice_add_exec_num_dice( f.dice_value );

                                        _game._game_ui_draw.create_animation_figure_out_board(
                                                cell.pos
                                                , btn_pressed.getCenterPosX()
                                                , btn_pressed.getCenterPosY()
                                                , 7
                                                , 18
                                        );

                                        _game._game_ui_draw.get_cell( cell.pos ).count_figures -= 1;

                                        _game._game_ui_draw.action_animation_fly_figure = true;

                                        _game._game_ui_draw.clear_select_points();
                                        _game._game_ui_draw.clear_select_end_points();
                                        _game._game_ui_draw.selected_alternative_xods.clear();
                                        _game._game_ui_draw.selected_alternative_xods_red.clear();

                                        return;
                                    }*/

                                    List<Integer> _l = highlight_alternative_xods( MySurfaceView.FIGURE_COLOR_WHITE );

                                    int min_white = 24;
                                    int from_white = 0;
                                    boolean _find_ok = false;
                                    List<Integer> _find_in_home = new ArrayList<>();

                                    for(int i = 0; i < _l.size(); i++)
                                    {
                                        MySurfaceView.CellValues from = _game_ui_draw.get_cell(_l.get(i));

                                        if( from.pos_white <= 6 )
                                        {
                                            _find_in_home.add( _l.get(i) );
                                            continue;
                                        }

                                        _game._game_ui_draw.selected_alternative_xods.add(_l.get(i));

                                        List<Integer> _find_xods = found_xods_from_currert_cell( from );

                                        String ss = "";

                                        for (int m1 = 0; m1 < _find_xods.size(); m1++)
                                        {
                                            int _m = _game_ui_draw.get_cell( _find_xods.get(m1) ).pos_white;

                                            ss += String.valueOf( _m ) + " ";

                                            if( min_white >  _m )
                                            {
                                                min_white = _m;
                                                from_white = from.pos_white;
                                                _find_ok = true;

                                                Log.i("TAG", "from_white " + String.valueOf(from_white) + " " + String.valueOf(_m));
                                            }
                                        }

                                        Log.i("TAG", ss);
                                    }

                                    if( _find_ok == false )
                                    {
                                        for(int i = 0; i < _find_in_home.size(); i++)
                                        {
                                            MySurfaceView.CellValues from = _game_ui_draw.get_cell(_find_in_home.get(i));

                                            _game._game_ui_draw.selected_alternative_xods.add(_find_in_home.get(i));

                                            List<Integer> _find_xods = found_xods_from_currert_cell( from );

                                            String ss = "";

                                            for (int m1 = 0; m1 < _find_xods.size(); m1++)
                                            {
                                                int _m = _game_ui_draw.get_cell( _find_xods.get(m1) ).pos_white;

                                                ss += String.valueOf( _m ) + " ";

                                                if( min_white >  _m )
                                                {
                                                    min_white = _m;
                                                    from_white = from.pos_white;

                                                    Log.i("TAG", "from_white " + String.valueOf(from_white) + " " + String.valueOf(_m));
                                                }
                                            }

                                            Log.i("TAG", ss);
                                        }
                                    }

                                    figure_selected = false;
                                    current_select_figure = null;

                                    Log.i("TAG", "------------------------ " + String.valueOf( from_white ) + " -- " + String.valueOf(min_white));

                                    _game.select_cell( _game._game_ui_draw.get_cell_pos_white( from_white ), false );

                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    _game.select_cell( _game._game_ui_draw.get_cell_pos_white( min_white ), false );
                                }
                            };
                            wt.start();

                        }
                    }
                    else
                    {
                        int pos_one_xod =  highlight_one_xod( MySurfaceView.FIGURE_COLOR_WHITE );
                        if( pos_one_xod == -1 )
                        {
                            List<Integer> _l = highlight_alternative_xods( MySurfaceView.FIGURE_COLOR_WHITE );

                            for(int i = 0; i < _l.size(); i++)
                            {
                                this._game_ui_draw.selected_alternative_xods.add( _l.get(i) );

                                List<Integer> _find_xods = found_xods_from_currert_cell( _game_ui_draw.get_cell(_l.get(i)) );

                                if( _find_xods.size() > 0 )
                                {
                                    for(int m1 = 0; m1 < _find_xods.size(); m1++)
                                    {
                                        _game_ui_draw.add_point_select_end_points( _find_xods.get(m1) );
                                    }
                                }
                            }
                        }
                        else
                        {
                            List<Integer> _find_xods = found_xods_from_currert_cell( _game_ui_draw.get_cell(pos_one_xod) );

                            if( _find_xods.size() > 0 )
                            {
                                for(int m1 = 0; m1 < _find_xods.size(); m1++)
                                {
                                    _game_ui_draw.add_point_select_end_points( _find_xods.get(m1) );
                                }
                            }
                        }
                    }
                }
            }
            else if( current_action_cmd == ACTION_XOD_BLACK )
            {
                if(
                        ( this.mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                || (
                                ( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS || this.mode_game == MODE_GAME__NETWORK )
                                        && this.mode_board == MySurfaceView.MODE_COLOR_BLACK
                        )
                                || (
                                this.mode_game == MODE_GAME__WITH_COMPUTER
                                        && this.mode_board == MySurfaceView.MODE_COLOR_BLACK
                        )
                        )
                {
                    this._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_BLACK_OR_NONE;
                }
                else
                {
                    this._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_DISABLE_SELECT;
                }

                if( mode_game_computer_with_computer )
                {
                    this._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_DISABLE_SELECT;
                }

                //this._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_BLACK_OR_NONE;
                this._game_ui_draw.selected_alternative_xods.clear();
                this._game_ui_draw.selected_alternative_xods_red.clear();

                final List<FindItemEndFigures> find_xods_all_in_home   = find_end_of_board_figures(MySurfaceView.FIGURE_COLOR_BLACK);
                if( find_xods_all_in_home.size() > 0 )
                {
                    for(int t = 0; t < find_xods_all_in_home.size(); t++)
                    {
                        this._game_ui_draw.selected_alternative_xods_red.add( find_xods_all_in_home.get(t).pos );
                    }
                }

                if( ! exists_xods_black() && find_xods_all_in_home.size() == 0 )
                {
                    change_color_game();
                }
                else
                {
                    if(        (this.mode_game == MODE_GAME__WITH_COMPUTER && mode_board == MySurfaceView.MODE_COLOR_WHITE  )
                            || (this.mode_game == MODE_GAME__WITH_COMPUTER && mode_game_computer_with_computer) )
                    {
                        if( find_xods_all_in_home.size() > 0 )
                        {
                            WaitThread wt = new WaitThread(500)
                            {
                                @Override
                                public void callback()
                                {
                                    FindItemEndFigures find = find_xods_all_in_home.size() == 1? find_xods_all_in_home.get(0) : find_xods_all_in_home.get((int) (Math.random() * (find_xods_all_in_home.size() - 1)));

                                    MySurfaceView.CellValues cell = _game_ui_draw.get_cell( find.pos );

                                    CImageButton btn_pressed = null;

                                    if( mode_board == MySurfaceView.MODE_COLOR_WHITE)
                                    {
                                        if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                                        {
                                            btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                                        }
                                        else
                                        {
                                            btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                                        }
                                    }
                                    else
                                    {
                                        if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                                        {
                                            btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                                        }
                                        else
                                        {
                                            btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                                        }
                                    }

                                    Log.d("TAG", android.util.Log.getStackTraceString(new Exception()));

                                    rand_dices.get( find.dice_index ).exec = true;

                                    _game_ui_draw.slot_animation_dice_add_exec_num_dice( find.dice_value );

                                    _game._game_ui_draw.create_animation_figure_out_board(
                                            cell.pos
                                            , btn_pressed.getCenterPosX()
                                            , btn_pressed.getCenterPosY()
                                            , 7
                                            , 15
                                    );

                                    _game._game_ui_draw.get_cell( cell.pos ).count_figures -= 1;

                                    _game._game_ui_draw.action_animation_fly_figure = true;

                                    _game._game_ui_draw.clear_select_points();
                                    _game._game_ui_draw.clear_select_end_points();
                                    _game._game_ui_draw.selected_alternative_xods.clear();
                                    _game._game_ui_draw.selected_alternative_xods_red.clear();

                                }
                            };
                            wt.start();
                        }
                        else
                        {
                            WaitThread wt = new WaitThread(500)
                            {
                                @Override
                                public void callback()
                                {
                                    //есть ли ходы на выход
                                    /*List<FindItemEndFigures> find_xods_all_in_home = find_end_of_board_figures(MySurfaceView.FIGURE_COLOR_BLACK);
                                    if( find_xods_all_in_home.size() > 0 )
                                    {
                                        FindItemEndFigures f = find_xods_all_in_home.get(0);
                                        MySurfaceView.CellValues cell = _game_ui_draw.get_cell( f.pos );
                                        CImageButton btn_pressed = null;

                                        if( mode_board == MySurfaceView.MODE_COLOR_WHITE)
                                        {
                                            if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                                            {
                                                btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                                            }
                                            else
                                            {
                                                btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                                            }
                                        }
                                        else
                                        {
                                            if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                                            {
                                                btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                                            }
                                            else
                                            {
                                                btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                                            }
                                        }

                                        rand_dices.get( f.dice_index ).exec = true;

                                        _game_ui_draw.slot_animation_dice_add_exec_num_dice( f.dice_value );

                                        _game._game_ui_draw.create_animation_figure_out_board(
                                                cell.pos
                                                , btn_pressed.getCenterPosX()
                                                , btn_pressed.getCenterPosY()
                                                , 7
                                                , 18
                                        );

                                        _game._game_ui_draw.get_cell( cell.pos ).count_figures -= 1;

                                        _game._game_ui_draw.action_animation_fly_figure = true;

                                        _game._game_ui_draw.clear_select_points();
                                        _game._game_ui_draw.clear_select_end_points();
                                        _game._game_ui_draw.selected_alternative_xods.clear();
                                        _game._game_ui_draw.selected_alternative_xods_red.clear();

                                        return;
                                    }*/

                                    List<Integer> _l = highlight_alternative_xods( MySurfaceView.FIGURE_COLOR_BLACK );

                                    int min_black = 24;
                                    int from_black = 0;
                                    boolean _find_ok = false;
                                    List<Integer> _find_in_home = new ArrayList<>();

                                    for(int i = 0; i < _l.size(); i++)
                                    {
                                        MySurfaceView.CellValues from = _game_ui_draw.get_cell(_l.get(i));

                                        if( from.pos_black <= 6 )
                                        {
                                            _find_in_home.add( _l.get(i) );
                                            continue;
                                        }

                                        _game._game_ui_draw.selected_alternative_xods.add(_l.get(i));

                                        List<Integer> _find_xods = found_xods_from_currert_cell( from );

                                        String ss = "";

                                        for (int m1 = 0; m1 < _find_xods.size(); m1++)
                                        {
                                            int _m = _game_ui_draw.get_cell( _find_xods.get(m1) ).pos_black;

                                            ss += String.valueOf( _m ) + " ";

                                            if( min_black >  _m )
                                            {
                                                min_black = _m;
                                                from_black = from.pos_black;
                                                _find_ok = true;

                                                Log.i("TAG", "from_black " + String.valueOf(from_black) + " " + String.valueOf(_m));
                                            }
                                        }

                                        Log.i("TAG", ss);
                                    }

                                    if( _find_ok == false )
                                    {
                                        for(int i = 0; i < _find_in_home.size(); i++)
                                        {
                                            MySurfaceView.CellValues from = _game_ui_draw.get_cell(_find_in_home.get(i));

                                            _game._game_ui_draw.selected_alternative_xods.add(_find_in_home.get(i));

                                            List<Integer> _find_xods = found_xods_from_currert_cell( from );

                                            String ss = "";

                                            for (int m1 = 0; m1 < _find_xods.size(); m1++)
                                            {
                                                int _m = _game_ui_draw.get_cell( _find_xods.get(m1) ).pos_black;

                                                ss += String.valueOf( _m ) + " ";

                                                if( min_black >  _m )
                                                {
                                                    min_black = _m;
                                                    from_black = from.pos_black;

                                                    Log.i("TAG", "from_black " + String.valueOf(from_black) + " " + String.valueOf(_m));
                                                }
                                            }

                                            Log.i("TAG", ss);
                                        }
                                    }

                                    figure_selected = false;
                                    current_select_figure = null;

                                    Log.i("TAG", "------------------------ " + String.valueOf( from_black ) + " -- " + String.valueOf(min_black));

                                    _game.select_cell( _game._game_ui_draw.get_cell_pos_black( from_black ), false );

                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    _game.select_cell( _game._game_ui_draw.get_cell_pos_black( min_black ), false );
                                }
                            };
                            wt.start();

                        }
                    }
                    else
                    {
                        int pos_one_xod =  highlight_one_xod( MySurfaceView.FIGURE_COLOR_BLACK );
                        if( pos_one_xod == -1 )
                        {
                            List<Integer> _l = highlight_alternative_xods( MySurfaceView.FIGURE_COLOR_BLACK );

                            for(int i = 0; i < _l.size(); i++)
                            {
                                this._game_ui_draw.selected_alternative_xods.add( _l.get(i) );

                                List<Integer> _find_xods = found_xods_from_currert_cell( _game_ui_draw.get_cell(_l.get(i)) );

                                if( _find_xods.size() > 0 )
                                {
                                    for(int m1 = 0; m1 < _find_xods.size(); m1++)
                                    {
                                        _game_ui_draw.add_point_select_end_points( _find_xods.get(m1) );
                                    }
                                }
                            }
                        }
                        else
                        {
                            List<Integer> _find_xods = found_xods_from_currert_cell( _game_ui_draw.get_cell(pos_one_xod) );

                            if( _find_xods.size() > 0 )
                            {
                                for(int m1 = 0; m1 < _find_xods.size(); m1++)
                                {
                                    _game_ui_draw.add_point_select_end_points( _find_xods.get(m1) );
                                }
                            }
                        }
                    }
                }
            }
        }

        public boolean exist_no_use_dice()
        {
            for(int k = 0; k < rand_dices.size(); k++)
            {
                if( rand_dices.get(k).exec == false )
                {
                    return true;
                }
            }

            return false;
        }

        public boolean test_dice_values(int v1, int v2)
        {
            if( rand_dices.size() < 2 )
            {
                return false;
            }

            if(
                    ( rand_dices.get(0).value == v1
                            && rand_dices.get(1).value == v2 )
                            ||     ( rand_dices.get(0).value == v2
                            && rand_dices.get(1).value == v1 )
                    )
            {
                return true;
            }

            return false;
        }

        public boolean find_xods_end_of_board_from_current_cell(MySurfaceView.CellValues cv)
        {
            List<FindItemEndFigures> tmp = find_end_of_board_figures(cv.color_type);

            if( tmp.size() > 0 )
            {
                for(int i = 0; i < tmp.size(); i++)
                {
                    if( tmp.get(i).pos == cv.pos )
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        public FindItemEndFigures find_xods_end_of_board_from_current_cell_1(MySurfaceView.CellValues cv)
        {
            List<FindItemEndFigures> tmp = find_end_of_board_figures(cv.color_type);

            if( tmp.size() > 0 )
            {
                for(int i = 0; i < tmp.size(); i++)
                {
                    if( tmp.get(i).pos == cv.pos )
                    {
                        return tmp.get(i);
                    }
                }
            }

            return null;
        }

        /// найти все ходы для для фигур кто в доме и если есть свободые кубики
        public List<FindItemEndFigures> find_end_of_board_figures(int color)
        {
            List<FindItemEndFigures> result = new ArrayList<>();

            if(        ! this.allInHome(color)
                    || ! this.exist_no_use_dice() )
            {
                return result;
            }

            if( color == MySurfaceView.FIGURE_COLOR_WHITE && this.allInHome(MySurfaceView.FIGURE_COLOR_WHITE) )
            {
                List<Integer> tmp_list = new ArrayList<>();

                // найти все варианты для строго выбранных вариантов кубиков
                for(int k = 0; k < rand_dices.size(); k++)
                {
                    if(
                                /*rand_dices.get(k).value == cv.pos_white
                            &&*/ _game_ui_draw.get_cell_pos_white(rand_dices.get(k).value).count_figures > 0
                            && _game_ui_draw.get_cell_pos_white(rand_dices.get(k).value).color_type == MySurfaceView.FIGURE_COLOR_WHITE
                            && rand_dices.get(k).exec == false
                            && tmp_list.indexOf( rand_dices.get(k).value ) == -1) // не надо искать одинаковые по значение кубики
                    {
                        FindItemEndFigures tmp = new FindItemEndFigures();

                        tmp.dice_index   = k;
                        tmp.dice_value   = rand_dices.get(k).value;
                        tmp.pos          = _game_ui_draw.get_cell_pos_white(rand_dices.get(k).value).pos;

                        result.add(tmp);

                        tmp_list.add( rand_dices.get(k).value );
                    }
                }

                if(tmp_list.size() == 0)
                {
                    // найти для больших позиций
                    for (int k = 0; k < rand_dices.size(); k++) {
                        if (rand_dices.get(k).exec == false && tmp_list.indexOf(k) == -1)
                        {
                            for (int rand_dice_text_value = rand_dices.get(k).value + 1; rand_dice_text_value <= 6; rand_dice_text_value++)
                            {
                                if (
                                        this.get_count_figures_between_2(rand_dices.get(k).value, rand_dice_text_value, MySurfaceView.FIGURE_COLOR_WHITE) == 0
                                                && _game_ui_draw.get_cell_pos_white(rand_dice_text_value).count_figures > 0
                                                && _game_ui_draw.get_cell_pos_white(rand_dice_text_value).color_type == MySurfaceView.FIGURE_COLOR_WHITE
                                        )
                                {
                                    FindItemEndFigures tmp = new FindItemEndFigures();

                                    tmp.dice_index = k;
                                    tmp.dice_value = rand_dices.get(k).value;
                                    tmp.pos = _game_ui_draw.get_cell_pos_white(rand_dice_text_value).pos;

                                    result.add(tmp);

                                    tmp_list.add( rand_dices.get(k).value );
                                    break;
                                }
                            }
                        }
                        //-- if
                    }
                }

                if(tmp_list.size() == 0)
                {
                    // найти для более меньших позиций
                    for (int k = 0; k < rand_dices.size(); k++) {
                        if (rand_dices.get(k).exec == false && tmp_list.indexOf(k) == -1)
                        {
                            for (int rand_dice_text_value = rand_dices.get(k).value - 1; rand_dice_text_value > 0; rand_dice_text_value--)
                            {
                                if (this.get_count_figures_between_2(rand_dice_text_value + 1, rand_dices.get(k).value, MySurfaceView.FIGURE_COLOR_WHITE) == 0
                                        && _game_ui_draw.get_cell_pos_white(rand_dice_text_value).count_figures > 0
                                        && _game_ui_draw.get_cell_pos_white(rand_dice_text_value).color_type == MySurfaceView.FIGURE_COLOR_WHITE)
                                {
                                    FindItemEndFigures tmp = new FindItemEndFigures();

                                    tmp.dice_index = k;
                                    tmp.dice_value = rand_dices.get(k).value;
                                    tmp.pos = _game_ui_draw.get_cell_pos_white(rand_dice_text_value).pos;

                                    result.add(tmp);
                                    break;
                                }
                            }
                        }
                    }
                    //====
                }
            }
            else if( color == MySurfaceView.FIGURE_COLOR_BLACK && this.allInHome(MySurfaceView.FIGURE_COLOR_BLACK) )
            {
                List<Integer> tmp_list = new ArrayList<>();

                // найти все варианты для строго выбранных вариантов кубиков
                for(int k = 0; k < rand_dices.size(); k++)
                {
                    if(
                                /*rand_dices.get(k).value == cv.pos_white
                            &&*/ _game_ui_draw.get_cell_pos_black(rand_dices.get(k).value).count_figures > 0
                            && _game_ui_draw.get_cell_pos_black(rand_dices.get(k).value).color_type == MySurfaceView.FIGURE_COLOR_BLACK
                            && rand_dices.get(k).exec == false
                            && tmp_list.indexOf( rand_dices.get(k).value ) == -1) // не надо искать одинаковые по значение кубики
                    {
                        FindItemEndFigures tmp = new FindItemEndFigures();

                        tmp.dice_index   = k;
                        tmp.dice_value   = rand_dices.get(k).value;
                        tmp.pos          = _game_ui_draw.get_cell_pos_black(rand_dices.get(k).value).pos;

                        result.add(tmp);

                        tmp_list.add( rand_dices.get(k).value );
                    }
                }

                if(tmp_list.size() == 0)
                {
                    // найти для больших позиций
                    for (int k = 0; k < rand_dices.size(); k++) {
                        if (rand_dices.get(k).exec == false && tmp_list.indexOf(k) == -1)
                        {
                            for (int rand_dice_text_value = rand_dices.get(k).value + 1; rand_dice_text_value <= 6; rand_dice_text_value++)
                            {
                                if (
                                        this.get_count_figures_between_2(rand_dices.get(k).value, rand_dice_text_value, MySurfaceView.FIGURE_COLOR_BLACK) == 0
                                                && _game_ui_draw.get_cell_pos_black(rand_dice_text_value).count_figures > 0
                                                && _game_ui_draw.get_cell_pos_black(rand_dice_text_value).color_type == MySurfaceView.FIGURE_COLOR_BLACK
                                        )
                                {
                                    FindItemEndFigures tmp = new FindItemEndFigures();

                                    tmp.dice_index = k;
                                    tmp.dice_value = rand_dices.get(k).value;
                                    tmp.pos = _game_ui_draw.get_cell_pos_black(rand_dice_text_value).pos;

                                    result.add(tmp);

                                    tmp_list.add( rand_dices.get(k).value );
                                    break;
                                }
                            }
                        }
                        //-- if
                    }
                }

                if(tmp_list.size() == 0)
                {
                    // найти для более меньших позиций
                    for (int k = 0; k < rand_dices.size(); k++)
                    {
                        if (rand_dices.get(k).exec == false && tmp_list.indexOf(k) == -1)
                        {
                            for (int rand_dice_text_value = rand_dices.get(k).value - 1; rand_dice_text_value > 0; rand_dice_text_value--)
                            {
                                if (this.get_count_figures_between_2(rand_dice_text_value + 1, rand_dices.get(k).value, MySurfaceView.FIGURE_COLOR_BLACK) == 0
                                        && _game_ui_draw.get_cell_pos_black(rand_dice_text_value).count_figures > 0
                                        && _game_ui_draw.get_cell_pos_black(rand_dice_text_value).color_type == MySurfaceView.FIGURE_COLOR_BLACK)
                                {
                                    FindItemEndFigures tmp = new FindItemEndFigures();

                                    tmp.dice_index = k;
                                    tmp.dice_value = rand_dices.get(k).value;
                                    tmp.pos = _game_ui_draw.get_cell_pos_black(rand_dice_text_value).pos;

                                    result.add(tmp);
                                    break;
                                }
                            }
                        }
                    }
                    //====
                }
            }

            return result;
        }

        //==========================================================================================

        public void select_cell(MySurfaceView.CellValues cv, boolean opt_callback_run_xod)
        {
            //-----------------------------------------------------
            _game_ui_draw.hide_all_btns();

            if(        figure_selected == true
                    && current_select_figure != null
                    && cv.pos != current_select_figure.pos )
            {
                if(        current_action_cmd == ACTION_XOD_WHITE
                        && cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE
                        && cv.count_figures > 0
                        &&
                        ( found_xods_from_currert_cell(cv).size() > 0  ||  find_xods_end_of_board_from_current_cell(cv) )
                        && _game_ui_draw.selected_points.indexOf( cv.pos ) == -1
                  )
                {
                    current_select_figure = null;
                    figure_selected = false;
                    _game_ui_draw.clear_selected_cell();
                    _game_ui_draw.clear_select_points();
                    _game_ui_draw.clear_select_end_points();
                }
                else if(   current_action_cmd == ACTION_XOD_BLACK
                        && cv.color_type == MySurfaceView.FIGURE_COLOR_BLACK
                        && cv.count_figures > 0
                        &&
                        ( found_xods_from_currert_cell(cv).size() > 0  ||  find_xods_end_of_board_from_current_cell(cv) )
                        && _game_ui_draw.selected_points.indexOf( cv.pos ) == -1
                        )
                {
                    current_select_figure = null;
                    figure_selected = false;
                    _game_ui_draw.clear_selected_cell();
                    _game_ui_draw.clear_select_points();
                    _game_ui_draw.clear_select_end_points();
                }
            }

            //-----------------------------------------------------

            if( figure_selected == false
                //|| ( current_select_figure != null && cv.pos != current_select_figure.pos )
                    )
            {
                if(        current_action_cmd == ACTION_XOD_WHITE
                        && cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE
                        && cv.count_figures > 0
                        )
                {
                    List<Integer> find_xods         = found_xods_from_currert_cell(cv);
                    boolean find_xods_all_in_home   = find_xods_end_of_board_from_current_cell(cv);

                    if( find_xods.size() > 0 )
                    {
                        this._game_ui_draw.clear_select_points();
                        this._game_ui_draw.clear_select_end_points();

                        for(int t = 0; t < find_xods.size(); t++)
                        {
                            this._game_ui_draw.add_point_select_points( find_xods.get(t) );
                        }

                        current_select_figure = cv;
                        _game_ui_draw.set_selected_cell(cv.pos);
                        _game_ui_draw.clear_select_end_points();
                        figure_selected = true;
                    }
                    // если нет ходов на выход, то
                    else if( ! find_xods_all_in_home )
                    {
                        /// проверить все возможные ходы на предмет наличия ходов хотябы одного

                        if( ! this.exists_xods_white() ) /// если нет ходов - передача хода
                        {
                            change_color_game();
                        }
                    }

                    if( find_xods_all_in_home )
                    {
                        // найти индекс кубика
                        FindItemEndFigures find_info_dice = find_xods_end_of_board_from_current_cell_1(cv);

                        if(
                                           (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__WITH_COMPUTER )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__NETWORK )
                                )
                        {
                            if(cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE)
                            {
                                this._game_ui_draw.show_btn_id(2);
                            }
                            else
                            {
                                this._game_ui_draw.show_btn_id(1);
                            }
                        }
                        else if(
                                           (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS)
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__WITH_COMPUTER)
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__NETWORK)
                                )
                        {
                            if(cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE)
                            {
                                this._game_ui_draw.show_btn_id(1);
                            }
                            else
                            {
                                this._game_ui_draw.show_btn_id(2);
                            }
                        }

                        index_dice_for_exit_board = find_info_dice.dice_index;

                        current_select_figure = cv;
                        _game_ui_draw.set_selected_cell(cv.pos);
                        _game_ui_draw.clear_select_end_points();
                        figure_selected = true;
                        //---------------------------------------------
                    }
                }
                else if(   current_action_cmd == ACTION_XOD_BLACK
                        && cv.color_type == MySurfaceView.FIGURE_COLOR_BLACK
                        && cv.count_figures > 0
                        )
                {
                    List<Integer> find_xods         = found_xods_from_currert_cell(cv);
                    boolean find_xods_all_in_home   = find_xods_end_of_board_from_current_cell(cv);

                    if( find_xods.size() > 0
                            || ( this.allInHome(MySurfaceView.FIGURE_COLOR_BLACK)
                            && this.exist_no_use_dice() ) )
                    {
                        this._game_ui_draw.clear_select_points();
                        this._game_ui_draw.clear_select_end_points();

                        for(int t = 0; t < find_xods.size(); t++)
                        {
                            this._game_ui_draw.add_point_select_points( find_xods.get(t) );
                        }

                        current_select_figure = cv;
                        _game_ui_draw.set_selected_cell(cv.pos);
                        _game_ui_draw.clear_select_end_points();
                        figure_selected = true;
                    }
                    // если нет ходов на выход, то
                    else if( ! find_xods_all_in_home )
                    {
                        /// проверить все возможные ходы на предмет наличия ходов хотябы одного

                        if( ! this.exists_xods_black() ) /// если нет ходов - передача хода
                        {
                            change_color_game();
                        }
                    }

                    if( find_xods_all_in_home )
                    {
                        //_game_ui_draw.clear_selected_cell();
                        // найти индекс кубика
                        FindItemEndFigures find_info_dice = find_xods_end_of_board_from_current_cell_1(cv);

                        if(
                                           (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__WITH_COMPUTER )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__NETWORK )
                                )
                        {
                            if(cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE)
                            {
                                this._game_ui_draw.show_btn_id(2);
                            }
                            else
                            {
                                this._game_ui_draw.show_btn_id(1);
                            }
                        }
                        else if(
                                           (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS)
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__WITH_COMPUTER)
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__NETWORK)
                                )
                        {
                            if(cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE)
                            {
                                this._game_ui_draw.show_btn_id(1);
                            }
                            else
                            {
                                this._game_ui_draw.show_btn_id(2);
                            }
                        }

                        index_dice_for_exit_board = find_info_dice.dice_index;

                        current_select_figure = cv;
                        _game_ui_draw.set_selected_cell(cv.pos);
                        _game_ui_draw.clear_select_end_points();
                        figure_selected = true;
                        //---------------------------------------------
                    }
                }
            }
            else if( current_select_figure != null )
            {
                if( _game_ui_draw.select_points_exists_pos( cv.pos ) )
                {
                    if( opt_callback_run_xod )
                    {
                        //  ----- ----- callback select
                        if (current_select_figure.color_type == MySurfaceView.FIGURE_COLOR_WHITE) {
                            callback_run_xod(current_select_figure.color_type, current_select_figure.pos_white, cv.pos_white);
                        } else if (current_select_figure.color_type == MySurfaceView.FIGURE_COLOR_BLACK) {
                            callback_run_xod(current_select_figure.color_type, current_select_figure.pos_black, cv.pos_black);
                        }
                    }


                    int diff = 0;

                    if( current_select_figure.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                    {
                        diff = current_select_figure.pos_white - cv.pos_white;
                    }
                    else if( current_select_figure.color_type == MySurfaceView.FIGURE_COLOR_BLACK )
                    {
                        diff = current_select_figure.pos_black - cv.pos_black;
                    }

                    for(int k = 0; k < rand_dices.size(); k++)
                    {
                        if( rand_dices.get(k).value == diff
                                && rand_dices.get(k).exec == false )
                        {
                            rand_dices.get(k).exec = true;

                            _game_ui_draw.slot_animation_dice_add_exec_num_dice( rand_dices.get(k).value );

                            break;
                        }
                    }

                    /*if( rand_dice_1 == diff && exec_xod_dice_1 == false )
                    {
                        exec_xod_dice_1 = true;
                    }
                    else if( rand_dice_2 == diff && exec_xod_dice_2 == false )
                    {
                        exec_xod_dice_2 = true;
                    }*/

                    //MySurfaceView.CellValues c = _draw_task.get_cell(12);

                    _game_ui_draw.clear_select_points();
                    _game_ui_draw.clear_select_end_points();
                    _game_ui_draw.clear_selected_cell();
                    _game_ui_draw.create_animation_figure(current_select_figure.pos, cv.pos, 7, 15);

                    current_select_figure.count_figures -= 1;

                    /*if( current_select_figure.count_figures == 0 )
                    {
                        current_select_figure.color_type = MySurfaceView.FIGURE_COLOR_NONE;
                    }*/

                    _game_ui_draw.action_animation_fly_figure = true;

                    figure_selected = false;
                    current_select_figure = null;
                }
                else if( current_select_figure.pos != cv.pos )
                {
                    current_select_figure = null;
                    figure_selected = false;
                    _game_ui_draw.clear_selected_cell();
                    _game_ui_draw.clear_select_points();
                    _game_ui_draw.clear_select_end_points();
                }

                if( current_select_figure != null )
                {
                    boolean find_xods_all_in_home   = find_xods_end_of_board_from_current_cell(current_select_figure);

                    if( find_xods_all_in_home )
                    {
                        //_game_ui_draw.clear_selected_cell();
                        // найти индекс кубика
                        FindItemEndFigures find_info_dice = find_xods_end_of_board_from_current_cell_1(current_select_figure);

                        if(
                                           (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__WITH_COMPUTER )
                                        || (mode_board == MySurfaceView.MODE_COLOR_WHITE && mode_game == MODE_GAME__NETWORK )
                                )
                        {
                            if(cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE)
                            {
                                this._game_ui_draw.show_btn_id(2);
                            }
                            else
                            {
                                this._game_ui_draw.show_btn_id(1);
                            }
                        }
                        else if(
                                           (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS)
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__WITH_COMPUTER)
                                        || (mode_board == MySurfaceView.MODE_COLOR_BLACK && mode_game == MODE_GAME__NETWORK)
                                )
                        {
                            if(cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE)
                            {
                                this._game_ui_draw.show_btn_id(1);
                            }
                            else
                            {
                                this._game_ui_draw.show_btn_id(2);
                            }
                        }

                        index_dice_for_exit_board = find_info_dice.dice_index;

                        _game_ui_draw.set_selected_cell(current_select_figure.pos);
                        _game_ui_draw.clear_select_end_points();
                        figure_selected = true;
                        //---------------------------------------------
                    }
                }

            }

            //-------------------
        }

        public FountCountFigures get_count_pos_down(int current_pos_color, int ignore_pos, int color_type)
        {
            FountCountFigures result = new FountCountFigures(0, current_pos_color);

            for(int i = current_pos_color - 1; i > 0; i--)
            {
                if(i == ignore_pos)
                {
                    continue;
                }

                if( color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                {
                    if( _game_ui_draw.get_cell_pos_white(i).color_type != MySurfaceView.FIGURE_COLOR_WHITE )
                    {

                        break;
                    }
                    else
                    {
                        result.pos_stop = i;
                        result.count += 1;
                    }
                }
                else
                {
                    if( _game_ui_draw.get_cell_pos_black(i).color_type != MySurfaceView.FIGURE_COLOR_BLACK )
                    {
                        break;
                    }
                    else
                    {
                        result.pos_stop = i;
                        result.count += 1;
                    }
                }
            }

            return result;
        }

        public FountCountFigures get_count_pos_up(int current_pos_color, int ignore_pos, int color_type)
        {
            FountCountFigures result = new FountCountFigures(0, current_pos_color);

            for(int i = current_pos_color + 1; i < 25; i++)
            {
                if(i == ignore_pos)
                {
                    continue;
                }

                if( color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                {
                    if( _game_ui_draw.get_cell_pos_white(i).color_type != MySurfaceView.FIGURE_COLOR_WHITE )
                    {
                        break;
                    }
                    else
                    {
                        result.pos_stop = i;
                        result.count += 1;
                    }
                }
                else
                {
                    if( _game_ui_draw.get_cell_pos_black(i).color_type != MySurfaceView.FIGURE_COLOR_BLACK )
                    {
                        break;
                    }
                    else
                    {
                        result.pos_stop = i;
                        result.count += 1;
                    }
                }
            }

            return result;
        }

        // from > to
        // не включительно
        public int get_count_figures_between_2(int from, int to, int color_type)
        {
            int result = 0;
            int k = (from < to)?1:-1;

            if( from >= to || Math.abs( from - to ) == 1 )
            {
                return 0;
            }

            for(int i = from; i < to; i+= k)
            {
                if( color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                {
                    if( _game_ui_draw.get_cell_pos_white(i).color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                    {
                        result += 1;
                    }
                }
                else
                {
                    if( _game_ui_draw.get_cell_pos_black(i).color_type == MySurfaceView.FIGURE_COLOR_BLACK )
                    {
                        result += 1;
                    }
                }
            }

            return result;
        }

        public int get_count_figures_between(int from, int to, int color_type)
        {
            int result = 0;
            int k = (from < to)?1:-1;

            if( from == to )
            {
                return 0;
            }

            //Log.i("TAG", String.valueOf(from) + " " + String.valueOf(to));

            for(int i = from; ; i+= k)
            {
                if( color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                {
                    if( _game_ui_draw.get_cell_pos_white(i).color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                    {
                        result += 1;
                    }
                }
                else
                {
                    if( _game_ui_draw.get_cell_pos_black(i).color_type == MySurfaceView.FIGURE_COLOR_BLACK )
                    {
                        result += 1;
                    }
                }

                if(i == to)
                {
                    break;
                }
            }

            return result;
        }

        private boolean allInHome(int color)
        {
            if( color == MySurfaceView.FIGURE_COLOR_WHITE )
            {
                for( int i = 0; i < _game_ui_draw._cells.size(); i++ )
                {
                    if(        _game_ui_draw._cells.get(i).color_type == MySurfaceView.FIGURE_COLOR_WHITE
                            && _game_ui_draw._cells.get(i).count_figures > 0
                            && _game_ui_draw._cells.get(i).pos_white >= 7
                            && _game_ui_draw._cells.get(i).pos_white <= 24
                            )
                    {
                        return false;
                    }
                }
            }
            else
            {
                for( int i = 0; i < _game_ui_draw._cells.size(); i++ )
                {
                    if(        _game_ui_draw._cells.get(i).color_type == MySurfaceView.FIGURE_COLOR_BLACK
                            && _game_ui_draw._cells.get(i).count_figures > 0
                            && _game_ui_draw._cells.get(i).pos_black >= 7
                            && _game_ui_draw._cells.get(i).pos_black <= 24
                            )
                    {
                        return false;
                    }
                }
            }

            return true;
        }

        /// найти ходы для выделенной фигуры
        public List<Integer> found_xods_from_currert_cell(MySurfaceView.CellValues cv)
        {
            List<Integer> result = new ArrayList<>();

            if( cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
            {
                for(int k = 0; k < rand_dices.size(); k++ )
                {
                    if(        cv.pos_white - rand_dices.get(k).value > 0
                            && rand_dices.get(k).exec == false
                            && _game_ui_draw.disable_select_pos_cell.indexOf(cv.pos) == -1 )
                    {
                        int v1 = cv.pos_white - rand_dices.get(k).value;
                        MySurfaceView.CellValues cv1 = _game_ui_draw.get_cell_pos_white(v1);

                        if(   cv1.color_type == MySurfaceView.FIGURE_COLOR_NONE
                                || cv1.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                        {
                            boolean error_flag = false;

                            // проверка
                            /* выстраивая последовательность фишек, игрок не имеет права делать непроходимый заслон
                               перед фишками противника, если впереди этого заслона нет хотя бы одной фишки противника;
                            * */
                            if( cv1.color_type == MySurfaceView.FIGURE_COLOR_NONE ) // установка новой фигуры
                            {
                                FountCountFigures count_pos_down = this.get_count_pos_down( cv1.pos_white
                                        , (cv.count_figures == 1)?cv.pos_white:0
                                        , MySurfaceView.FIGURE_COLOR_WHITE );
                                FountCountFigures count_pos_up   = this.get_count_pos_up( cv1.pos_white
                                        , (cv.count_figures == 1)?cv.pos_white:0
                                        , MySurfaceView.FIGURE_COLOR_WHITE );

                                // поиск противоположного цвета. Если от дома до минимальной позиции нет фигур то нет хода
                                MySurfaceView.CellValues cell_stop_down = _game_ui_draw.get_cell_pos_white( count_pos_down.pos_stop );
                                int count_f = get_count_figures_between(1, cell_stop_down.pos_black, MySurfaceView.FIGURE_COLOR_BLACK);

                                //int count_f = get_count_figures_between(1, count_pos_down.pos_stop, MySurfaceView.FIGURE_COLOR_WHITE);

                                if( count_pos_down.count + count_pos_up.count >= 5 && count_f == 0 )
                                {
                                    error_flag = true;
                                }
                            }

                            if( ! error_flag )
                            {
                                result.add(cv1.pos);
                            }
                        }
                        else // лунка занята - пропуст хода
                        {
                            //rand_dices.get(k).exec = true;
                        }
                    }
                }
            }
            else if( cv.color_type == MySurfaceView.FIGURE_COLOR_BLACK )
            {
                for(int k = 0; k < rand_dices.size(); k++ )
                {
                    if(    cv.pos_black - rand_dices.get(k).value > 0
                            && rand_dices.get(k).exec == false
                            && _game_ui_draw.disable_select_pos_cell.indexOf(cv.pos) == -1 )
                    {
                        int v1 = cv.pos_black - rand_dices.get(k).value;
                        MySurfaceView.CellValues cv1 = _game_ui_draw.get_cell_pos_black(v1);

                        if(   cv1.color_type == MySurfaceView.FIGURE_COLOR_NONE
                                || cv1.color_type == MySurfaceView.FIGURE_COLOR_BLACK )
                        {
                            boolean error_flag = false;

                            // проверка
                            /* выстраивая последовательность фишек, игрок не имеет права делать непроходимый заслон
                               перед фишками противника, если впереди этого заслона нет хотя бы одной фишки противника;
                            * */
                            if( cv1.color_type == MySurfaceView.FIGURE_COLOR_NONE ) // установка новой фигуры
                            {
                                FountCountFigures count_pos_down = this.get_count_pos_down( cv1.pos_black
                                        , (cv.count_figures == 1)?cv.pos_black:0
                                        , MySurfaceView.FIGURE_COLOR_BLACK );
                                FountCountFigures count_pos_up   = this.get_count_pos_up( cv1.pos_black
                                        , (cv.count_figures == 1)?cv.pos_black:0
                                        , MySurfaceView.FIGURE_COLOR_BLACK );

                                // поиск противоположного цвета. Если от дома до минимальной позиции нет фигур то нет хода
                                MySurfaceView.CellValues cell_stop_down = _game_ui_draw.get_cell_pos_black( count_pos_down.pos_stop );
                                int count_f = get_count_figures_between(1, cell_stop_down.pos_white, MySurfaceView.FIGURE_COLOR_WHITE);

                                //int count_f = get_count_figures_between(1, count_pos_down.pos_stop, MySurfaceView.FIGURE_COLOR_WHITE);

                                if( count_pos_down.count + count_pos_up.count >= 5 && count_f == 0 )
                                {
                                    error_flag = true;
                                }
                            }

                            if( ! error_flag )
                            {
                                result.add(cv1.pos);
                            }
                        }
                        else // лунка занята - пропуст хода
                        {
                            //rand_dices.get(k).exec = true;
                        }
                    }
                }
            }

            return result;
        }

        public void stop_animation_fly_out_of_board(int from_pos)
        {
            MySurfaceView.CellValues _c1 = _game_ui_draw.get_cell(from_pos);

            if( _c1.count_figures == 0 )
            {
                _c1.color_type = MySurfaceView.FIGURE_COLOR_NONE;
            }

            this.exec_game();
        }

        public void stop_animation_fly(int from_pos, int to_pos)
        {
            _game.figure_selected = false;
            _game.current_select_figure = null;

            MySurfaceView.CellValues _c1 = _game_ui_draw.get_cell(from_pos);
            MySurfaceView.CellValues _c2 = _game_ui_draw.get_cell(to_pos);
            _c2.count_figures += 1;
            _c2.color_type = _c1.color_type;

            if( _c1.count_figures == 0 )
            {
                _c1.color_type = MySurfaceView.FIGURE_COLOR_NONE;
            }

            if( current_xod_log != null )
            {
                current_xod_log.values.add(new FromTo(from_pos, to_pos));
            }

            // это первый ход и есть ходы с головы
            //if( first_xod )
            {
                boolean find = false;

                if(current_action_cmd == ACTION_XOD_WHITE)
                {
                    for(int k = 0; k < current_xod_log.values.size(); k++)
                    {
                        if( _game_ui_draw.get_cell( current_xod_log.values.get(k).from ).pos_white == 24 )
                        {
                            find = true;
                            break;
                        }
                    }
                }
                else if(current_action_cmd == ACTION_XOD_BLACK)
                {
                    if( current_xod_log != null )
                    {
                        for(int k = 0; k < current_xod_log.values.size(); k++)
                        {
                            if( _game_ui_draw.get_cell( current_xod_log.values.get(k).from ).pos_black == 24 )
                            {
                                find = true;
                                break;
                            }
                        }
                    }

                }

                if( find )
                {
                    if(current_action_cmd == ACTION_XOD_WHITE)
                    {
                        MySurfaceView.CellValues cv_p = _game_ui_draw.get_cell_pos_white(24);

                        _game_ui_draw.disable_select_pos_cell_add( cv_p.pos );
                    }
                    else if(current_action_cmd == ACTION_XOD_BLACK)
                    {
                        MySurfaceView.CellValues cv_p = _game_ui_draw.get_cell_pos_black(24);

                        _game_ui_draw.disable_select_pos_cell_add( cv_p.pos );
                    }

                    //Log.i("TAG", "OK2");
                }
            }

            boolean f = true;

            for(int k = 0; k < rand_dices.size(); k++)
            {
                if( rand_dices.get(k).exec == false )
                {
                    f = false;
                    break;
                }
            }

            if( f )
            {
                change_color_game();
            }
            else
            {
                if( current_action_cmd == ACTION_XOD_WHITE && ! this.exists_xods_white() )
                {
                    if( count_xod_white == 0 && (
                            this.test_dice_values(3, 3)
                                    || this.test_dice_values(4, 4)
                                    || this.test_dice_values(6, 6)
                    ))
                    {
                        _game_ui_draw.disable_select_pos_cell_clear();

                        if( current_xod_log.values.size() >= 2 )
                        {
                            int count_pos_24_exec_xods = 0;

                            for(int m = 0; m < current_xod_log.values.size(); m++)
                            {
                                MySurfaceView.CellValues _fc = _game_ui_draw.get_cell( current_xod_log.values.get(m).from );

                                if( _fc.pos_white == 24 )
                                {
                                    count_pos_24_exec_xods += 1;
                                }
                            }

                            if( count_pos_24_exec_xods >= 2 )
                            {
                                MySurfaceView.CellValues cv_p = _game_ui_draw.get_cell_pos_white(24);

                                _game_ui_draw.disable_select_pos_cell_add( cv_p.pos );
                            }
                        }

                        if( this.exists_xods_white() )
                        {
                            int pos_one_xod =  highlight_one_xod( MySurfaceView.FIGURE_COLOR_WHITE );
                            if( pos_one_xod == -1 )
                            {
                                List<Integer> _l = highlight_alternative_xods( MySurfaceView.FIGURE_COLOR_WHITE );

                                for(int i = 0; i < _l.size(); i++)
                                {
                                    this._game_ui_draw.selected_alternative_xods.add( _l.get(i) );

                                    List<Integer> _find_xods = found_xods_from_currert_cell( _game_ui_draw.get_cell(_l.get(i)) );

                                    if( _find_xods.size() > 0 )
                                    {
                                        for(int m1 = 0; m1 < _find_xods.size(); m1++)
                                        {
                                            _game_ui_draw.add_point_select_end_points( _find_xods.get(m1) );
                                        }
                                    }
                                }
                            }
                            else
                            {
                                List<Integer> _find_xods = found_xods_from_currert_cell( _game_ui_draw.get_cell(pos_one_xod) );

                                if( _find_xods.size() > 0 )
                                {
                                    for(int m1 = 0; m1 < _find_xods.size(); m1++)
                                    {
                                        _game_ui_draw.add_point_select_end_points( _find_xods.get(m1) );
                                    }

                                    this.exec_game();
                                }
                            }

                            return;
                        }
                    }

                    change_color_game();
                }
                else if( current_action_cmd == ACTION_XOD_BLACK && ! this.exists_xods_black() )
                {
                    if( count_xod_black == 0 && (
                            this.test_dice_values(3, 3)
                                    || this.test_dice_values(4, 4)
                                    || this.test_dice_values(6, 6)
                    ))
                    {
                        _game_ui_draw.disable_select_pos_cell_clear();

                        if( current_xod_log.values.size() >= 2 )
                        {
                            int count_pos_24_exec_xods = 0;

                            for(int m = 0; m < current_xod_log.values.size(); m++)
                            {
                                MySurfaceView.CellValues _fc = _game_ui_draw.get_cell( current_xod_log.values.get(m).from );

                                if( _fc.pos_black == 24 )
                                {
                                    count_pos_24_exec_xods += 1;
                                }
                            }

                            if( count_pos_24_exec_xods >= 2 )
                            {
                                MySurfaceView.CellValues cv_p = _game_ui_draw.get_cell_pos_black(24);

                                _game_ui_draw.disable_select_pos_cell_add( cv_p.pos );
                            }
                        }

                        if( this.exists_xods_black() )
                        {
                            int pos_one_xod =  highlight_one_xod( MySurfaceView.FIGURE_COLOR_BLACK );
                            if( pos_one_xod == -1 )
                            {
                                List<Integer> _l = highlight_alternative_xods( MySurfaceView.FIGURE_COLOR_BLACK );

                                for(int i = 0; i < _l.size(); i++)
                                {
                                    this._game_ui_draw.selected_alternative_xods.add( _l.get(i) );

                                    List<Integer> _find_xods = found_xods_from_currert_cell( _game_ui_draw.get_cell(_l.get(i)) );

                                    if( _find_xods.size() > 0 )
                                    {
                                        for(int m1 = 0; m1 < _find_xods.size(); m1++)
                                        {
                                            _game_ui_draw.add_point_select_end_points( _find_xods.get(m1) );
                                        }
                                    }
                                }
                            }
                            else
                            {
                                List<Integer> _find_xods = found_xods_from_currert_cell( _game_ui_draw.get_cell(pos_one_xod) );

                                if( _find_xods.size() > 0 )
                                {
                                    for(int m1 = 0; m1 < _find_xods.size(); m1++)
                                    {
                                        _game_ui_draw.add_point_select_end_points( _find_xods.get(m1) );
                                    }

                                    this.exec_game();
                                }
                            }

                            return;
                        }
                    }

                    change_color_game();
                }
                else
                {
                    this.exec_game();
                }
            }
        }

        public int count_figures(int type_figure)
        {
            int res = 0;

            for(int p = 0; p < _game_ui_draw._cells.size(); p++)
            {
                if( _game_ui_draw._cells.get(p).color_type == type_figure
                        && _game_ui_draw._cells.get(p).count_figures > 0 )
                {
                    res += _game_ui_draw._cells.get(p).count_figures;
                }
            }

            return res;
        }

        public List<MySurfaceView.CellValues> count_xods_from_color(int color)
        {
            List<MySurfaceView.CellValues> res = new ArrayList<>();

            for(int p = 0; p < _game_ui_draw._cells.size(); p++)
            {
                if( _game_ui_draw._cells.get(p).color_type == color )
                {
                    List<Integer> _find_xods = found_xods_from_currert_cell(_game_ui_draw._cells.get(p));

                    if( _find_xods.size() > 0 )
                    {
                        res.add( _game_ui_draw._cells.get(p) );
                    }
                }
            }

            return res;
        }

        public boolean exists_xods_white()
        {
            if( this.count_figures( MySurfaceView.FIGURE_COLOR_WHITE ) == 0 )
            {
                Log.i("TAG", "no exists_xods_white 1");

                return false;
            }

            for(int p = 0; p < _game_ui_draw._cells.size(); p++)
            {
                if( _game_ui_draw._cells.get(p).color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                {
                    List<Integer> _find_xods = found_xods_from_currert_cell(_game_ui_draw._cells.get(p));

                    if( _find_xods.size() > 0 )
                    {
                        return true;
                    }
                }
            }

            if( this.allInHome(MySurfaceView.FIGURE_COLOR_WHITE) && this.exist_no_use_dice() )
            {
                return true;
            }

            Log.i("TAG", "no exists_xods_white 2");

            return false;
        }

        public boolean exists_xods_black()
        {
            if( this.count_figures( MySurfaceView.FIGURE_COLOR_BLACK ) == 0 )
            {
                Log.i("TAG", "no exists_xods_black 1");

                return false;
            }

            for(int p = 0; p < _game_ui_draw._cells.size(); p++)
            {
                if( _game_ui_draw._cells.get(p).color_type == MySurfaceView.FIGURE_COLOR_BLACK )
                {
                    List<Integer> _find_xods = found_xods_from_currert_cell(_game_ui_draw._cells.get(p));

                    if( _find_xods.size() > 0 )
                    {
                        return true;
                    }
                }
            }

            if( this.allInHome(MySurfaceView.FIGURE_COLOR_BLACK) && this.exist_no_use_dice() )
            {
                return  true;
            }

            Log.i("TAG", "no exists_xods_black 2");
            Log.d("TAG", android.util.Log.getStackTraceString(new Exception()));

            return false;
        }

        public void change_color_game()
        {
            //Log.i("TAG", "change_color_game");

            _game_ui_draw.clear_selected_cell();

            _game_ui_draw.hide_btn_id(1);
            _game_ui_draw.hide_btn_id(2);

            this._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_DISABLE_SELECT;

            if (current_action_cmd == ACTION_XOD_WHITE)
            {
                count_xod_white += 1;
            }
            else
            {
                count_xod_black += 1;
            }

            first_xod = false;

            // добавить в лог
            xods_log.add( current_xod_log );

            // detect end game
            if(
                    (
                            (
                                    this.first_xod_color == MySurfaceView.FIGURE_COLOR_WHITE      /// первый ход у белых
                                            && current_action_cmd == ACTION_XOD_BLACK                        /// передача хода от черных к белым
                            )
                                    ||
                                    (
                                            this.first_xod_color == MySurfaceView.FIGURE_COLOR_BLACK      /// первый ход у черных
                                                    && current_action_cmd == ACTION_XOD_WHITE                        /// передача хода от белым к черным
                                    )
                    )

                            && this.count_figures( MySurfaceView.FIGURE_COLOR_WHITE ) == 0
                            && this.count_figures( MySurfaceView.FIGURE_COLOR_BLACK ) == 0
                    )
            {
                // ничья
                callback_end_game(0);
                return;
            }
            else if(
                    current_action_cmd == ACTION_XOD_BLACK                        /// передача хода от белым к черным
                            && this.count_figures( MySurfaceView.FIGURE_COLOR_WHITE ) == 0
                            && this.count_figures( MySurfaceView.FIGURE_COLOR_BLACK ) > 0
                    )
            {
                callback_end_game(1);
                return;
            }
            else if(
                    current_action_cmd == ACTION_XOD_WHITE                        /// передача хода от белым к черным
                            && this.count_figures( MySurfaceView.FIGURE_COLOR_BLACK ) == 0
                            && this.count_figures( MySurfaceView.FIGURE_COLOR_WHITE ) > 0
                    )
            {
                callback_end_game(2);
                return;
            }


            WaitThread wt = new WaitThread( mode_game == MODE_GAME__LOCAL_TWO_PLAYERS ? 300: 0)
            {
                @Override
                public void callback()
                {
                    if (current_action_cmd == ACTION_XOD_WHITE)
                    {
                        if(
                                ( mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                        || (
                                        ( mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS || mode_game == MODE_GAME__NETWORK )
                                                && mode_board == MySurfaceView.MODE_COLOR_WHITE
                                )
                                || ( mode_game == MODE_GAME__WITH_COMPUTER )
                                )
                        {
                            current_action_cmd = ACTION_TROW_DICE_BLACK;

                            next_dice_value_1 = randInt(1, 6);
                            next_dice_value_2 = randInt(1, 6);

                            current_xod_log = new XodDetail();
                            current_xod_log.color_action_xod = ACTION_XOD_BLACK;


                            ByteBuffer b_buf = ByteBuffer.allocate( 2 );

                            b_buf.put( (byte) next_dice_value_1 );         // 1 byte
                            b_buf.put( (byte) next_dice_value_2 );         // 1 byte

                            if( mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                            {
                                quenue_network.add("ACTION_TROW_DICE_BLACK"
                                        , null
                                        , b_buf.array()
                                        , System.currentTimeMillis()
                                        , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                        , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                            }
                            else if( mode_game == MODE_GAME__NETWORK )
                            {
                                network.queue_network.add("ACTION_TROW_DICE_BLACK"
                                        , null
                                        , b_buf.array()
                                        , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                        , QueueNetwork.TYPE_SEND__FORCE);
                            }
                            /// компютер поиск хода
                            /*else if( _game.mode_game == MODE_GAME__WITH_COMPUTER )
                            {
                                if (_game.mode_board == MySurfaceView.MODE_COLOR_WHITE)
                                {
                                    Log.i("TAG", "ok");
                                }
                            }*/

                            _game_ui_draw.disable_select_pos_cell_clear();


                            _game_ui_draw.clear_select_points();
                            _game_ui_draw.clear_select_end_points();
                            _game_ui_draw.clear_selected_cell();

                            exec_game();
                        }
                        else
                        {
                            current_action_cmd = ACTION_NONE;
                        }

                    }
                    else
                    {
                        if(
                                ( mode_game == MODE_GAME__LOCAL_TWO_PLAYERS )
                                        || (
                                        ( mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS || mode_game == MODE_GAME__NETWORK )
                                                && mode_board == MySurfaceView.MODE_COLOR_BLACK
                                )
                                || ( mode_game == MODE_GAME__WITH_COMPUTER )
                                )
                        {
                            current_action_cmd = ACTION_TROW_DICE_WHITE;

                            next_dice_value_1 = randInt(1, 6);
                            next_dice_value_2 = randInt(1, 6);

                            current_xod_log = new XodDetail();
                            current_xod_log.color_action_xod = ACTION_XOD_WHITE;

                            ByteBuffer b_buf = ByteBuffer.allocate( 2 );

                            b_buf.put( (byte) next_dice_value_1 );         // 1 byte
                            b_buf.put( (byte) next_dice_value_2 );         // 1 byte

                            if( mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                            {
                                quenue_network.add("ACTION_TROW_DICE_WHITE"
                                        , null
                                        , b_buf.array()
                                        , System.currentTimeMillis()
                                        , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                        , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                            }
                            else if( mode_game == MODE_GAME__NETWORK )
                            {
                                network.queue_network.add("ACTION_TROW_DICE_WHITE"
                                        , null
                                        , b_buf.array()
                                        , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                        , QueueNetwork.TYPE_SEND__FORCE);
                            }
                            /// компютер поиск хода
                            /*else if( _game.mode_game == MODE_GAME__WITH_COMPUTER )
                            {
                                if (_game.mode_board == MySurfaceView.MODE_COLOR_BLACK)
                                {
                                    Log.i("TAG", "ok");
                                }
                            }*/

                            _game_ui_draw.disable_select_pos_cell_clear();


                            _game_ui_draw.clear_select_points();
                            _game_ui_draw.clear_select_end_points();
                            _game_ui_draw.clear_selected_cell();

                            exec_game();
                        }
                        else
                        {
                            current_action_cmd = ACTION_NONE;
                        }

                    }


                }
            };
            wt.start();

        }
        //------------------------------------------------------

        public void press_btn(int id)
        {
            Log.i("TAG", "Press btn: " + String.valueOf(id) );

            if( current_select_figure == null )
            {
                return;
            }

            CImageButton btn_pressed = _game_ui_draw.getUiBtn(id);

            _game_ui_draw.create_animation_figure_out_board(
                    current_select_figure.pos
                    , btn_pressed.getCenterPosX()
                    , btn_pressed.getCenterPosY()
                    , 7
                    , 15
            );

            current_select_figure.count_figures -= 1;

            /*if( current_select_figure.count_figures == 0 )
            {
                current_select_figure.color_type = MySurfaceView.FIGURE_COLOR_NONE;
            }*/

            _game_ui_draw.action_animation_fly_figure = true;

            _game_ui_draw.clear_select_points();
            _game_ui_draw.clear_select_end_points();
            _game_ui_draw.selected_alternative_xods.clear();
            _game_ui_draw.selected_alternative_xods_red.clear();


            ByteBuffer b_buf = ByteBuffer.allocate( 3 );

            b_buf.put( (byte) current_select_figure.color_type );  // 1 byte
            b_buf.put( (byte) (current_select_figure.color_type == MySurfaceView.FIGURE_COLOR_WHITE ? current_select_figure.pos_white : current_select_figure.pos_black) );  // 1 byte
            b_buf.put( (byte) mode_board );                         // 1 byte


            if( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
            {
                quenue_network.add("XOD_OUT_OF_BOARD"
                        , null
                        , b_buf.array()
                        , System.currentTimeMillis()
                        , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                        , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
            }
            else if( this.mode_game == MODE_GAME__NETWORK )
            {
                network.queue_network.add("XOD_OUT_OF_BOARD"
                        , null
                        , b_buf.array()
                        , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                        , QueueNetwork.TYPE_SEND__FORCE);
            }

            /*if( current_select_figure.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
            {
                for(int k = 0; k < rand_dices.size(); k++)
                {
                    if(        rand_dices.get(k).value == diff
                            && rand_dices.get(k).exec == false )
                    {
                        rand_dices.get(k).exec = true;
                        break;
                    }
                }
            }*/

            for(int k = 0; k < rand_dices.size(); k++)
            {
                if( k == index_dice_for_exit_board )
                {
                    rand_dices.get(k).exec = true;
                    _game_ui_draw.slot_animation_dice_add_exec_num_dice( rand_dices.get(k).value );

                    _game_ui_draw.clear_selected_cell();
                    break;
                }
            }

            // _game_ui_draw.slot_animation_dice_add_exec_num_dice( rand_dices.get(k).value );

            figure_selected = false;
            current_select_figure = null;
        }

        //-------------------------------------------------

        /**
         * @param status 0 - ничья, 1 - победа белых, 2 - подеба черных
         */
        public void callback_end_game(int status) {}


        public void callback_run_xod(int color, int pos_from, int pos_to) {}

    } // class

    private GameData _game = null;//new GameData();
    private QueueNetworkBluetooth quenue_network = null;

    private class WaitThread extends Thread
    {
        private long wait = 0;
        public WaitThread(long v)
        {
            super("WaitThread");
            wait = v;
        }

        @Override
        public void run()
        {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            callback();
        }

        public void callback() {}
    }

    private WaitThread time_out_connect_device  = null;
    private boolean global_bluetooth_connect_ok = false;

    //------------------------------------------------------------------------------------
    public static int randInt(int min, int max) {

        return  min + (int)( Math.random() * ((max - min) + 1) );
    }

    /*
	 * Sets the font on all TextViews in the ViewGroup. Searches
	 * recursively for all inner ViewGroups as well. Just add a
	 * check for any other views you want to set as well (EditText,
	 * etc.)
	 */
    public void setFont(ViewGroup group, Typeface font)
    {
        int count = group.getChildCount();
        View v;
        for(int i = 0; i < count; i++)
        {
            v = group.getChildAt(i);
            if(v instanceof TextView)
                ((TextView)v).setTypeface(font);
            else if(v instanceof Button)
                ((Button)v).setTypeface(font);
            else if(v instanceof ViewGroup)
                setFont((ViewGroup)v, font);
            else if(v instanceof GridView)
                setFont((ViewGroup)v, font);
            else if(v instanceof ViewSwitcher)
                setFont((ViewGroup)v, font);
            else if(v instanceof RelativeLayout)
                setFont((ViewGroup)v, font);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if( ! isTablet(this) )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        try
        {
            this.VERSION_APP = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        HOST = JniApi.f1();

        TCP_PORT_SERVER_1 = JniApi.port1();
        TCP_PORT_SERVER_2 = JniApi.port2();


        PACKAGE_NAME = getApplicationContext().getPackageName();
        app_setting = new AppSettings();

        this._scale_px = this.getResources().getDisplayMetrics().density;

        _fontApp = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        _font_2  = Typeface.createFromAsset(getAssets(), "fonts/Monitorca-Bd.ttf");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //setContentView(R.layout.activity_main);
        //show_page_game();

        show_page_main_menu();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);


        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("4536008749-nsarvib6rkgcoameqf2snfhgq2on0ais.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener()
                {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                    {
                        Log.d("TAG", "onConnectionFailed:" + connectionResult);
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_clientt
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void show_end_game_info(final int status)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_game_win_info);

                if( fl != null )
                {
                    fl.setVisibility(View.VISIBLE);

                    TextView tv = (TextView) findViewById(R.id.tv_page_game_win_title);
                    TextView tv2 = (TextView) findViewById(R.id.tv_page_game_win_title2);

                    ImageView color1 = findViewById(R.id.iv_page_game_win_color1);
                    ImageView color2 = findViewById(R.id.iv_page_game_win_color2);
                    ImageView color3 = findViewById(R.id.iv_page_game_win_color3);

                    ImageView iv_page_game_win_icon = findViewById(R.id.iv_page_game_win_icon);

                    if( status == 0 )
                    {
                        tv.setText( getString(R.string.txt30) );
                    }
                    else if( status == 1 )
                    {
                        tv.setText( getString(R.string.txt31) );

                        color1.setImageResource(R.drawable.figure_white);
                        color2.setImageResource(R.drawable.figure_white);
                        color3.setImageResource(R.drawable.figure_white);

                        if( _game.mode_board == MySurfaceView.MODE_COLOR_WHITE )
                        {
                            iv_page_game_win_icon.setVisibility(View.VISIBLE);
                        }

                        if( _game.count_figures( MySurfaceView.FIGURE_COLOR_BLACK ) == 15 )
                        {
                            tv2.setText( getString(R.string.txt33) );
                        }
                    }
                    else if( status == 2 )
                    {
                        tv.setText( getString(R.string.txt32) );

                        color1.setImageResource(R.drawable.figure_black);
                        color2.setImageResource(R.drawable.figure_black);
                        color3.setImageResource(R.drawable.figure_black);

                        if( _game.mode_board == MySurfaceView.MODE_COLOR_BLACK )
                        {
                            iv_page_game_win_icon.setVisibility(View.VISIBLE);
                        }

                        if( _game.count_figures( MySurfaceView.FIGURE_COLOR_WHITE ) == 15 )
                        {
                            tv2.setText( getString(R.string.txt33) );
                        }
                    }
                }
            }
        });
    }

    private void show_page_game_two_players()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        sv_page = SV_PAGE_GAME;
        setContentView(R.layout.page_game);

        _game = new GameData()
        {
            public void callback_end_game(final int status)
            {
                Log.i("TAG", "callback_end_game " + String.valueOf(status));

                show_end_game_info(status);
            }
        };

        _game.mode_game  = MODE_GAME__LOCAL_TWO_PLAYERS;
        _game.mode_board = MySurfaceView.MODE_COLOR_WHITE;
        _game.current_action_cmd = ACTION_START_GAME;
        _game.rand_num_dice_first_white = randInt(1, 6);

        do
        {
            _game.rand_num_dice_first_black = randInt(1, 6);
        }
        while (_game.rand_num_dice_first_white == _game.rand_num_dice_first_black);

        _draw_task = new MySurfaceView( MainActivity.this, _game.mode_board, MySurfaceView.FIGIRE_TYPE_1 )
        {
            /*@Override
            public void callback_stop_animation()
            {
                WaitThread wt = new WaitThread(500)
                {
                    @Override
                    public void callback()
                    {
                        _game.exec_game();
                    }
                };
                wt.start();
            }*/

            /*@Override
            public void callback_stop_animation_dice(int need_num, long id)
            {
            }*/

            @Override
            public void callback_no_select()
            {
                if(_game != null)
                {
                    _game.figure_selected = false;
                    _game.current_select_figure = null;
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();
                }
            }

            @Override
            public void callback_stop_animation_fly(int from_cell_pos, int to_cell_pos)
            {
                if(_game != null)
                {
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();

                    _game.stop_animation_fly(from_cell_pos, to_cell_pos);

                    //_game.exec_game();
                }
            }

            @Override
            public void callback_stop_animation_fly_out_board(int from_cell_pos)
            {
                if(_game != null)
                {
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();
                    _game._game_ui_draw.hide_all_btns();

                    _game.stop_animation_fly_out_of_board(from_cell_pos);

                    if( ! _game.exist_no_use_dice())
                    {
                        _game._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_DISABLE_SELECT;

                    }

                    //_game.exec_game();
                }
            }

            @Override
            public void callback_stop_animation_all_dice()
            {
                WaitThread wt = new WaitThread(200)
                {
                    @Override
                    public void callback()
                    {
                        if(
                                (
                                        _game.test_dice_values(1, 1)
                                                || _game.test_dice_values(2, 2)
                                                || _game.test_dice_values(3, 3)
                                                || _game.test_dice_values(4, 4)
                                                || _game.test_dice_values(5, 5)
                                                || _game.test_dice_values(6, 6)
                                )
                                        &&
                                        _game._game_ui_draw.slot_animation_dice.size() == 2
                                )
                        {
                            MySurfaceView.PlanInfo item1 = _game._game_ui_draw.slot_animation_dice.get(0);
                            MySurfaceView.PlanInfo item2 = _game._game_ui_draw.slot_animation_dice.get(1);

                            MySurfaceView.PlanInfo item1_1 = new MySurfaceView.PlanInfo();
                            MySurfaceView.PlanInfo item2_1 = new MySurfaceView.PlanInfo();

                            item1_1.plan_num = item1.plan_num;
                            item2_1.plan_num = item1.plan_num;

                            item1_1._path_points = new ArrayList<>();
                            item2_1._path_points = new ArrayList<>();

                            item1_1.animation_dice_index = 0;
                            item2_1.animation_dice_index = 0;

                            item1_1.last_draw_original = item1.last_draw;
                            item1_1.last_draw = changeBitmapContrastBrightness(item1.last_draw, 1.1f, 12);
                            item1_1.last_point = new PointPP( item1.last_point.getx()
                                    - item1.last_draw.getWidth() * 1.3f
                                    , item1.last_point.gety()
                                    + generatRandomPositiveNegitiveValue( - item1.last_draw.getHeight()/4, item1.last_draw.getHeight()/4));

                            item2_1.last_draw_original = item2.last_draw;
                            item2_1.last_draw = changeBitmapContrastBrightness(item2.last_draw, 1.1f, 12);
                            item2_1.last_point = new PointPP( item2.last_point.getx()
                                    - item2.last_draw.getWidth() * 1.3f
                                    , item2.last_point.gety()
                                    + generatRandomPositiveNegitiveValue( - item2.last_draw.getHeight()/4, item2.last_draw.getHeight()/4));

                            _game._game_ui_draw.slot_animation_dice.add(item1_1);
                            _game._game_ui_draw.slot_animation_dice.add(item2_1);
                        }

                        _game.exec_game();
                    }
                };
                wt.start();
            }

            @Override
            public void callback_onInitDraw()
            {
                _game.exec_game();
            }

            @Override
            public void callback_select_cell(CellValues cv)
            {
                if(_game != null)
                {
                    _game.select_cell(cv, true);
                }
            }

            @Override
            public void callback_press_btn(int id)
            {
                _game.press_btn(id);
            }
        };

        _game._game_ui_draw = _draw_task;

        FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_game);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
        fl.addView(_draw_task, params);

        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        ImageView iv_page_game_close_win_alert = findViewById(R.id.iv_page_game_close_win_alert);
        iv_page_game_close_win_alert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                show_page_main_menu();
            }
        });
    }

    private void show_page_game_two_players_bluetooth_network(int mode_game, int type_color, final byte[] boeard_info_1, final byte[] boeard_info_2, final byte[] boeard_info_3)
    {
        if( thread_run_search_search != null )
        {
            thread_run_search_search.run = false;
            thread_run_search_search = null;
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        sv_page = SV_PAGE_GAME;
        setContentView(R.layout.page_game);

        _game = new GameData()
        {

            @Override
            public void callback_run_xod(int color, int pos_from, int pos_to)
            {
                ByteBuffer b_buf = ByteBuffer.allocate( 3 );

                b_buf.put( (byte) color );            // 1 byte
                b_buf.put( (byte) pos_from );         // 1 byte
                b_buf.put( (byte) pos_to );           // 1 byte

                if( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                {
                    quenue_network.add("EXEC_XOD"
                            , null
                            , b_buf.array()
                            , System.currentTimeMillis()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                }
                else if(this.mode_game == MODE_GAME__NETWORK)
                {
                    network.queue_network.add("EXEC_XOD"
                            , null
                            , b_buf.array()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);
                }
            }

            @Override
            public void callback_end_game(final int status)
            {
                Log.i("TAG", "callback_end_game " + String.valueOf(status));

                show_end_game_info(status);

                ByteBuffer b_buf = ByteBuffer.allocate( 1 );

                b_buf.put( (byte) status );            // 1 byte

                if( this.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                {
                    quenue_network.add("END_GAME"
                            , null
                            , b_buf.array()
                            , System.currentTimeMillis()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                }
                else if( this.mode_game == MODE_GAME__NETWORK )
                {
                    network.queue_network.add("END_GAME"
                            , null
                            , b_buf.array()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);

                    ArrayList<String> params = new ArrayList<>();
                    params.add(String.valueOf(status));
                    params.add(String.valueOf(_game.mode_board));

                    if( status == 0 )
                    {
                        params.add(String.valueOf(0));
                    }
                    else if( status == 1 )
                    {
                        if( _game.count_figures( MySurfaceView.FIGURE_COLOR_BLACK ) == 15 )
                        {
                            params.add(String.valueOf(1));
                        }
                        else
                        {
                            params.add(String.valueOf(0));
                        }
                    }
                    else if( status == 2 )
                    {
                        if( _game.count_figures( MySurfaceView.FIGURE_COLOR_WHITE ) == 15 )
                        {
                            params.add(String.valueOf(1));
                        }
                        else
                        {
                            params.add(String.valueOf(0));
                        }
                    }

                    network.queue_network.add("END_GAME_SAVE_SERVER"
                            , params
                            , null
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);
                }
            }
        };

        //_game.mode_game  = MODE_GAME__BLUETOOTH_TWO_PLAYERS;
        _game.mode_game  = mode_game;
        _game.mode_board = type_color; //MySurfaceView.MODE_COLOR_BLACK;
        _game.current_action_cmd = ACTION_NONE;
        _game.rand_num_dice_first_white = randInt(1, 6);

        do
        {
            _game.rand_num_dice_first_black = randInt(1, 6);
        }
        while (_game.rand_num_dice_first_white == _game.rand_num_dice_first_black);

        _draw_task = new MySurfaceView( MainActivity.this, _game.mode_board, MySurfaceView.FIGIRE_TYPE_1 )
        {
            /*@Override
            public void callback_stop_animation()
            {
                WaitThread wt = new WaitThread(500)
                {
                    @Override
                    public void callback()
                    {
                        _game.exec_game();
                    }
                };
                wt.start();
            }*/

            /*@Override
            public void callback_stop_animation_dice(int need_num, long id)
            {
            }*/

            @Override
            public void callback_no_select()
            {
                if(_game != null)
                {
                    _game.figure_selected = false;
                    _game.current_select_figure = null;
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();
                }
            }

            @Override
            public void callback_stop_animation_fly(int from_cell_pos, int to_cell_pos)
            {
                if(_game != null)
                {
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();

                    _game.stop_animation_fly(from_cell_pos, to_cell_pos);

                    //_game.exec_game();
                }
            }

            @Override
            public void callback_stop_animation_fly_out_board(int from_cell_pos)
            {
                if(_game != null)
                {
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();
                    _game._game_ui_draw.hide_all_btns();

                    _game.stop_animation_fly_out_of_board(from_cell_pos);

                    if( ! _game.exist_no_use_dice())
                    {
                        _game._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_DISABLE_SELECT;

                    }

                    //_game.exec_game();
                }
            }

            @Override
            public void callback_stop_animation_all_dice()
            {
                WaitThread wt = new WaitThread(200)
                {
                    @Override
                    public void callback()
                    {
                        /// задвоеные кубики
                        if(
                            (
                                   _game.test_dice_values(1, 1)
                                || _game.test_dice_values(2, 2)
                                || _game.test_dice_values(3, 3)
                                || _game.test_dice_values(4, 4)
                                || _game.test_dice_values(5, 5)
                                || _game.test_dice_values(6, 6)
                            )
                            &&
                            _game._game_ui_draw.slot_animation_dice.size() == 2
                        )
                        {
                            MySurfaceView.PlanInfo item1 = _game._game_ui_draw.slot_animation_dice.get(0);
                            MySurfaceView.PlanInfo item2 = _game._game_ui_draw.slot_animation_dice.get(1);

                            MySurfaceView.PlanInfo item1_1 = new MySurfaceView.PlanInfo();
                            MySurfaceView.PlanInfo item2_1 = new MySurfaceView.PlanInfo();

                            item1_1.plan_num = item1.plan_num;
                            item2_1.plan_num = item1.plan_num;

                            item1_1._path_points = new ArrayList<>();
                            item2_1._path_points = new ArrayList<>();

                            item1_1.animation_dice_index = 0;
                            item2_1.animation_dice_index = 0;

                            item1_1.last_draw_original = item1.last_draw;
                            item1_1.last_draw = changeBitmapContrastBrightness(item1.last_draw, 1.1f, 12);
                            item1_1.last_point = new PointPP( item1.last_point.getx()
                                    - item1.last_draw.getWidth() * 1.3f
                                    , item1.last_point.gety()
                                    + generatRandomPositiveNegitiveValue( - item1.last_draw.getHeight()/4, item1.last_draw.getHeight()/4));

                            item2_1.last_draw_original = item2.last_draw;
                            item2_1.last_draw = changeBitmapContrastBrightness(item2.last_draw, 1.1f, 12);
                            item2_1.last_point = new PointPP( item2.last_point.getx()
                                    - item2.last_draw.getWidth() * 1.3f
                                    , item2.last_point.gety()
                                    + generatRandomPositiveNegitiveValue( - item2.last_draw.getHeight()/4, item2.last_draw.getHeight()/4));

                            _game._game_ui_draw.slot_animation_dice.add(item1_1);
                            _game._game_ui_draw.slot_animation_dice.add(item2_1);
                        }

                        _game.exec_game();
                    }
                };
                wt.start();
            }

            @Override
            public void callback_onInitDraw()
            {
                if( boeard_info_1 != null
                        && boeard_info_2 != null
                        && boeard_info_3 != null  )
                {
                    for(int i = 0; i < _game._game_ui_draw._cells.size(); i++)
                    {
                        _game._game_ui_draw._cells.get(i).count_figures = 0;
                        _game._game_ui_draw._cells.get(i).color_type = FIGURE_COLOR_NONE;
                    }


                    for(int i = 0; i < _game._game_ui_draw._cells.size(); i++)
                    {
                        if(        boeard_info_1[i] == 0
                                && boeard_info_2[i] == 0
                                && boeard_info_3[i] == 0 )
                        {
                            continue;
                        }

                        if( boeard_info_3[i] == MySurfaceView.FIGURE_COLOR_WHITE )
                        {
                            MySurfaceView.CellValues cv =  _game._game_ui_draw.get_cell_pos_white(boeard_info_1[i]);

                            cv.count_figures = boeard_info_2[i];
                            cv.color_type    = boeard_info_3[i];
                        }
                        else if( boeard_info_3[i] == MySurfaceView.FIGURE_COLOR_BLACK )
                        {
                            MySurfaceView.CellValues cv =  _game._game_ui_draw.get_cell_pos_black(boeard_info_1[i]);

                            cv.count_figures = boeard_info_2[i];
                            cv.color_type    = boeard_info_3[i];
                        }

                    }
                }

                //_game.exec_game();
            }

            @Override
            public void callback_select_cell(CellValues cv)
            {
                if(_game != null)
                {
                    _game.select_cell(cv, true);
                }
            }

            @Override
            public void callback_press_btn(int id)
            {
                _game.press_btn(id);
            }
        };

        _game._game_ui_draw = _draw_task;

        FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_game);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
        fl.addView(_draw_task, params);


        FrameLayout _fl = (FrameLayout) findViewById(R.id.fl_page_game_wait_user);
        _fl.setVisibility(View.VISIBLE);

        run_amination_page_game_tv_wait = true;
        amination_tv_page_game();

        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        ImageView iv_page_game_close_win_alert = findViewById(R.id.iv_page_game_close_win_alert);
        iv_page_game_close_win_alert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if( network == null )
                {
                    show_page_main_menu();
                }
                else
                {
                    show_page_network_menu();
                }
            }
        });
    }

    private void show_page_game_with_computer(int type_color, boolean _mode_game_computer_with_computer)
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        sv_page = SV_PAGE_GAME;
        setContentView(R.layout.page_game);

        _game = new GameData()
        {
            public void callback_end_game(final int status)
            {
                Log.i("TAG", "callback_end_game " + String.valueOf(status));

                show_end_game_info(status);
            }
        };

        _game.mode_game  = MODE_GAME__WITH_COMPUTER;
        _game.mode_board = type_color;
        _game.current_action_cmd = ACTION_START_GAME;
        _game.rand_num_dice_first_white = randInt(1, 6);
        _game.mode_game_computer_with_computer = _mode_game_computer_with_computer;

        do
        {
            _game.rand_num_dice_first_black = randInt(1, 6);
        }
        while (_game.rand_num_dice_first_white == _game.rand_num_dice_first_black);

        _draw_task = new MySurfaceView( MainActivity.this, _game.mode_board, MySurfaceView.FIGIRE_TYPE_1 )
        {
            /*@Override
            public void callback_stop_animation()
            {
                WaitThread wt = new WaitThread(500)
                {
                    @Override
                    public void callback()
                    {
                        _game.exec_game();
                    }
                };
                wt.start();
            }*/

            /*@Override
            public void callback_stop_animation_dice(int need_num, long id)
            {
            }*/

            @Override
            public void callback_no_select()
            {
                if(_game != null)
                {
                    _game.figure_selected = false;
                    _game.current_select_figure = null;
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();
                }
            }

            @Override
            public void callback_stop_animation_fly(int from_cell_pos, int to_cell_pos)
            {
                if(_game != null)
                {
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();

                    /*if( _game.mode_game != MODE_GAME__WITH_COMPUTER )
                    {
                        _game._game_ui_draw.clear_select_points();
                    }*/
                    _game._game_ui_draw.clear_select_end_points();

                    _game.stop_animation_fly(from_cell_pos, to_cell_pos);
                }
            }

            @Override
            public void callback_stop_animation_fly_out_board(int from_cell_pos)
            {
                if(_game != null)
                {
                    _game._game_ui_draw.selected_alternative_xods.clear();
                    _game._game_ui_draw.selected_alternative_xods_red.clear();
                    _game._game_ui_draw.clear_selected_cell();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();
                    _game._game_ui_draw.hide_all_btns();

                    _game.stop_animation_fly_out_of_board(from_cell_pos);

                    if( ! _game.exist_no_use_dice())
                    {
                        _game._game_ui_draw.mode_select_figure = MySurfaceView.SELECT_TYPE_FIGURE_DISABLE_SELECT;

                    }

                    //_game.exec_game();
                }
            }

            @Override
            public void callback_stop_animation_all_dice()
            {
                WaitThread wt = new WaitThread(200)
                {
                    @Override
                    public void callback()
                    {
                        if(
                                (
                                        _game.test_dice_values(1, 1)
                                                || _game.test_dice_values(2, 2)
                                                || _game.test_dice_values(3, 3)
                                                || _game.test_dice_values(4, 4)
                                                || _game.test_dice_values(5, 5)
                                                || _game.test_dice_values(6, 6)
                                )
                                        &&
                                        _game._game_ui_draw.slot_animation_dice.size() == 2
                                )
                        {
                            MySurfaceView.PlanInfo item1 = _game._game_ui_draw.slot_animation_dice.get(0);
                            MySurfaceView.PlanInfo item2 = _game._game_ui_draw.slot_animation_dice.get(1);

                            MySurfaceView.PlanInfo item1_1 = new MySurfaceView.PlanInfo();
                            MySurfaceView.PlanInfo item2_1 = new MySurfaceView.PlanInfo();

                            item1_1.plan_num = item1.plan_num;
                            item2_1.plan_num = item1.plan_num;

                            item1_1._path_points = new ArrayList<>();
                            item2_1._path_points = new ArrayList<>();

                            item1_1.animation_dice_index = 0;
                            item2_1.animation_dice_index = 0;

                            item1_1.last_draw_original = item1.last_draw;
                            item1_1.last_draw = changeBitmapContrastBrightness(item1.last_draw, 1.1f, 12);
                            item1_1.last_point = new PointPP( item1.last_point.getx()
                                    - item1.last_draw.getWidth() * 1.3f
                                    , item1.last_point.gety()
                                    + generatRandomPositiveNegitiveValue( - item1.last_draw.getHeight()/4, item1.last_draw.getHeight()/4));

                            item2_1.last_draw_original = item2.last_draw;
                            item2_1.last_draw = changeBitmapContrastBrightness(item2.last_draw, 1.1f, 12);
                            item2_1.last_point = new PointPP( item2.last_point.getx()
                                    - item2.last_draw.getWidth() * 1.3f
                                    , item2.last_point.gety()
                                    + generatRandomPositiveNegitiveValue( - item2.last_draw.getHeight()/4, item2.last_draw.getHeight()/4));

                            _game._game_ui_draw.slot_animation_dice.add(item1_1);
                            _game._game_ui_draw.slot_animation_dice.add(item2_1);
                        }

                        _game.exec_game();
                    }
                };
                wt.start();
            }

            @Override
            public void callback_onInitDraw()
            {
                _game.exec_game();
            }

            @Override
            public void callback_select_cell(CellValues cv)
            {
                if(_game != null)
                {
                    _game.select_cell(cv, true);
                }
            }

            @Override
            public void callback_press_btn(int id)
            {
                _game.press_btn(id);
            }
        };

        _game._game_ui_draw = _draw_task;

        FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_game);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
        fl.addView(_draw_task, params);

        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        ImageView iv_page_game_close_win_alert = findViewById(R.id.iv_page_game_close_win_alert);
        iv_page_game_close_win_alert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                show_page_main_menu();
            }
        });
    }

    private void show_page_network_search()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        sv_page = SV_PAGE_NETWORK_SEARCH;
        setContentView(R.layout.page_network_search);

        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        if( thread_run_search_search != null )
        {
            thread_run_search_search.run = false;
            thread_run_search_search = null;
        }

        _thread_run_search_search();
    }

    private void show_page_network_raiting()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        sv_page = SV_PAGE_NETWORK_RAITING;
        setContentView(R.layout.page_network_raiting);

        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        load_table_rating();

        FrameLayout fl = findViewById(R.id.fl_page_table_rating_user_info_close);
        fl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FrameLayout fl = findViewById(R.id.fl_table_rating_user_info);
                fl.setVisibility(View.GONE);
            }
        });
    }


    private void show_page_network_menu()
    {
        if( thread_run_search_search != null )
        {
            thread_run_search_search.run = false;
            thread_run_search_search = null;
        }

        hashMapSearchUsers.clear();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        sv_page = SV_PAGE_NETWORK_MENU;
        setContentView(R.layout.page_network_game);

        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        TableRow tr = findViewById(R.id.tr_page_network_game_create_game);
        tr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                show_page_game_two_players_bluetooth_network(
                           MODE_GAME__NETWORK
                        ,  Math.random() > 0.5 ? MySurfaceView.MODE_COLOR_WHITE : MySurfaceView.MODE_COLOR_BLACK
                        , null
                        , null
                        , null );

                //--------------------------------------------------------
                ArrayList<String> params = new ArrayList<String>();

                params.add(String.valueOf(accout_info.uid));
                params.add(String.valueOf(session_info.session_socket_id));

                network.queue_network.add("START_WAIT_GAME"
                        , params
                        , null
                        , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                        , QueueNetwork.TYPE_SEND__LONG);
            }
        });

        tr = findViewById(R.id.tr_page_network_game_search);
        tr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                show_page_network_search();
            }
        });

        tr = findViewById(R.id.tr_page_network_game_raiting);
        tr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                show_page_network_raiting();
            }
        });

    }

    private void show_page_main_menu()
    {
        if( ! isTablet(this) )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if( thread_run_search_search != null )
        {
            thread_run_search_search.run = false;
            thread_run_search_search = null;
        }

        hashMapSearchUsers.clear();

        sv_page = SV_PAGE_MAIN_MENU;
        setContentView(R.layout.page_main_menu);

        // If we're already discovering, stop it
        if (      _blueToothInfo.mBluetoothAdapter != null
                &&_blueToothInfo.mBluetoothAdapter.isDiscovering())
        {
            _blueToothInfo.mBluetoothAdapter.cancelDiscovery();
        }

        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        TextView tv_page_main_menu_h = (TextView) findViewById(R.id.tv_page_main_menu_h);
        tv_page_main_menu_h.setTypeface(_font_2);

        Button btn = (Button) findViewById(R.id.button_start_game_with_computer);
        if( btn != null )
        {
            btn.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    onClick_start_game_with_computer_long_press(null);

                    return true;
                }
            });
        }

        if( network != null )
        {
            network.OnDestroy();
            network = null;

            session_info = new SessionInfo();
        }

        TableRow tr = (TableRow) findViewById(R.id.tr_page_main_menu_share_app);
        tr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.txt_91) + " https://play.google.com/store/apps/details?id=games2d.com.nards";

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Online Radio");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.txt_8)));
            }
        });
    }

    private void show_page_privacy_policy()
    {
        sv_page = SV_PAGE_PRIVACY_POLICY;
        setContentView(R.layout.page_privacy_policy);

        ViewGroup root = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        WebView view = findViewById(R.id.wv_page_privacy_policy_data);

        view.setBackgroundColor(Color.TRANSPARENT);
        view.clearCache(true);
        view.setWebViewClient(new WebViewClient()
        {

            public void onPageFinished(WebView view, String url)
            {
                /*FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_rules_wait);

                if (fl != null)
                {
                    fl.setVisibility(View.GONE);
                }*/
            }
        });

        view.getSettings().setSupportZoom(false);

        String HTML = "";

        if (Locale.getDefault().getLanguage().equalsIgnoreCase("RU"))
        {
            HTML = readTextFromResource(R.raw.privacy_policy);
        }
        else
        {
            HTML = readTextFromResource(R.raw.privacy_policy_en);
        }

        view.loadDataWithBaseURL("file:///android_res/drawable/", HTML, "text/html", "UTF-8", null);
    }

    private void show_page_auth()
    {
        sv_page = SV_PAGE_AUTH;
        setContentView(R.layout.page_auth);

        ViewGroup root = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);

        //click_sound = MediaPlayer.create(this, R.raw.click);

        TextView tv = (TextView) findViewById(R.id.tv_page_auth_footer_text);
        tv.setText(Html.fromHtml(getString(R.string.txt_80)));
        tv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                show_page_privacy_policy();
            }
        });

        Button btn = (Button) findViewById(R.id.btn_page_auth_run_input);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*if(click_sound != null)
                {
                    click_sound.start();
                }*/

                signIn();
            }
        });
    }

    private void show_page_connection_server()
    {
        sv_page = SV_PAGE_CONNECTION_SERVER;
        setContentView(R.layout.page_connection_server);

        ViewGroup root = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        setFont(root, this._font_2);


        ImageView iv = (ImageView) findViewById(R.id.iv_page_input_anim_wait);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.animation_rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        connection_server();
    }

    private void show_page_info()
    {
        sv_page = SV_PAGE_INFO;
        setContentView(R.layout.page_info);

        WebView view = (WebView)findViewById(R.id.wv_info);

        view.getSettings().setSupportZoom(false);


        String HTML = readTextFromResource(R.raw.info_data);

        view.loadDataWithBaseURL("file:///android_res/drawable/", HTML, "text/html", "UTF-8", null);

        view.setVisibility(View.VISIBLE);
    }

    void load_table_rating()
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(String.valueOf(accout_info.uid));
        params.add(String.valueOf(session_info.session_socket_id));

        network.queue_network.add("LOAD_TABLE_RATING"
                , params
                , null
                , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                , QueueNetwork.TYPE_SEND__LONG);
    }

    // [START signIn]
    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        mGoogleApiClient.connect();
    }

    // [START signOut]
    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(Status status)
                    {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]

                        //close_app();
                        show_page_main_menu();
                    }
                });
    }
    // [END onActivityResult]

    // [START revokeAccess]
    private void revokeAccess()
    {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(Status status)
                    {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }
    // [END handleSignInResult]

    private void amination_tv_page_game()
    {
        if( ! run_amination_page_game_tv_wait )
        {
            return;
        }

        TextView tv = (TextView) findViewById(R.id.tv_page_game_wait);

        int fadeInDuration = 1000;
        int fadeOutDuration = 1000;
        int timeBetween = 500;

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        tv.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                amination_tv_page_game();
            }
            public void onAnimationRepeat(Animation animation) { }
            public void onAnimationStart(Animation animation)  { }
        });
    }

    public float get_scale_px()
    {
        return this._scale_px;
    }

    /**
     *
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static int generatRandomPositiveNegitiveValue(int min, int max)
    {
        //Random rand = new Random();
        int ii = -min + (int) (Math.random() * ((max - (-min)) + 1));
        return ii;
    }

    private String readTextFromResource(int resourceID)
    {
        InputStream raw = getResources().openRawResource(resourceID);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int i;
        try
        {
            i = raw.read();
            while (i != -1)
            {
                stream.write(i);
                i = raw.read();
            }
            raw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return stream.toString();
    }

    public void onClick_run_animation_fly(View v)
    {
        MySurfaceView.CellValues c = _draw_task.get_cell(12);
        c.count_figures -= 1;

        _draw_task.create_animation_figure(12, 20, 7, 15);
        _draw_task.action_animation_fly_figure = true;
    }

    public void onClick_run_animation_dice(View v)
    {
        _draw_task.clearPlanAnimationSlot();

        int rp = (int) (Math.random() * 4);

        if( rp == 0 )
        {
            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( randInt(1, 6), 0x01, 10, MySurfaceView
                    .PLAN_TYPE_PATH_1);

            MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( randInt(1, 6),  0x02, 10, MySurfaceView
                    .PLAN_TYPE_PATH_2);

            _draw_task.addPlanAnimationInSlot(p);
            _draw_task.addPlanAnimationInSlot(p2);
        }
        else if( rp == 1 )
        {
            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( randInt(1, 6),  0x01, 10, MySurfaceView
                    .PLAN_TYPE_PATH_11);

            MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( randInt(1, 6),  0x02, 10, MySurfaceView
                    .PLAN_TYPE_PATH_22);

            _draw_task.addPlanAnimationInSlot(p);
            _draw_task.addPlanAnimationInSlot(p2);
        }
        else if( rp == 2 )
        {
            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( randInt(1, 6),  0x01, 10, MySurfaceView
                    .PLAN_TYPE_PATH_3);

            MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( randInt(1, 6),  0x02, 10, MySurfaceView
                    .PLAN_TYPE_PATH_4);

            _draw_task.addPlanAnimationInSlot(p);
            _draw_task.addPlanAnimationInSlot(p2);
        }
        else if( rp == 3 )
        {
            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( randInt(1, 6),  0x01, 10, MySurfaceView
                    .PLAN_TYPE_PATH_33);

            MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( randInt(1, 6),  0x02, 10, MySurfaceView
                    .PLAN_TYPE_PATH_44);

            _draw_task.addPlanAnimationInSlot(p);
            _draw_task.addPlanAnimationInSlot(p2);
        }
    }

    public void onClick_run_game_on_internet(View v)
    {
        show_page_connection_server();
    }

    public void onClick_run_game_two_players(View v)
    {
        show_page_game_two_players();
    }

    public void onClick_show_page_data(View v)
    {
        show_page_info();
    }

    public void onClick_run_game_two_players_bluetooth(View v)
    {
        FrameLayout fl = (FrameLayout)findViewById(R.id.fl_page_main_menu_bluetooth_game);

        if( fl != null )
        {
            fl.setVisibility(View.VISIBLE);
        }
    }

    public void onClick_run_game_with_computer(View v)
    {
        FrameLayout fl = (FrameLayout)findViewById(R.id.fl_page_main_menu_play_with_computer);

        if( fl != null )
        {
            fl.setVisibility(View.VISIBLE);
        }
    }

    public void onClick_start_bluetooth_game_is_creator(View v)
    {
        // Get local Bluetooth adapter
        _blueToothInfo.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (_blueToothInfo.mBluetoothAdapter == null)
        {
            String title = getString(R.string.txt12);
            String yes   = getString(R.string.txt13);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
            alertDialogBuilder.setTitle( title );

            alertDialogBuilder
                    .setMessage("")
                    .setCancelable(false)
                    .setPositiveButton(yes, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog,int id)
                        {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            /*Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();*/
            return;
        }

        if ( ! _blueToothInfo.mBluetoothAdapter.isEnabled() )
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else
        {
            //if (mChatService == null) setupChat();

            RadioGroup rg = (RadioGroup) findViewById(R.id.rg_page_main_menu_select_color_game);

            int radioButtonID = rg.getCheckedRadioButtonId();
            View radioButton = rg.findViewById(radioButtonID);
            int idx = rg.indexOfChild(radioButton);

            if( idx == 0 )
            {
                show_page_game_two_players_bluetooth_network( MODE_GAME__BLUETOOTH_TWO_PLAYERS, MySurfaceView.MODE_COLOR_WHITE, null, null, null );
            }
            else
            {
                show_page_game_two_players_bluetooth_network( MODE_GAME__BLUETOOTH_TWO_PLAYERS, MySurfaceView.MODE_COLOR_BLACK, null, null, null );
            }

            // Initialize the BluetoothService to perform bluetooth connections
            _blueToothInfo.mChatService = new BluetoothService(this, mHandler);

            _blueToothInfo.mChatService.start();
        }
    }

    public void onClick_start_search_devices(View v)
    {
        // Get local Bluetooth adapter
        _blueToothInfo.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (_blueToothInfo.mBluetoothAdapter == null)
        {
            String title = getString(R.string.txt12);
            String yes   = getString(R.string.txt13);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
            alertDialogBuilder.setTitle( title );

            alertDialogBuilder
                    .setMessage("")
                    .setCancelable(false)
                    .setPositiveButton(yes, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog,int id)
                        {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            /*Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();*/
            return;
        }

        if ( ! _blueToothInfo.mBluetoothAdapter.isEnabled() )
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT_2);
            // Otherwise, setup the chat session
        }
        else
        {
            start_search_bluetooth_devices();
        }



    }

    public void onClick_start_game_with_computer(View v)
    {
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg_page_main_menu_select_color_game2);

        int radioButtonID = rg.getCheckedRadioButtonId();
        View radioButton = rg.findViewById(radioButtonID);
        int idx = rg.indexOfChild(radioButton);

        if( idx == 0 )
        {
            show_page_game_with_computer( MySurfaceView.MODE_COLOR_WHITE, false );
        }
        else
        {
            show_page_game_with_computer( MySurfaceView.MODE_COLOR_BLACK, false );
        }
    }

    public void onClick_start_game_with_computer_long_press(View v)
    {
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg_page_main_menu_select_color_game2);

        int radioButtonID = rg.getCheckedRadioButtonId();
        View radioButton = rg.findViewById(radioButtonID);
        int idx = rg.indexOfChild(radioButton);

        if( idx == 0 )
        {
            show_page_game_with_computer( MySurfaceView.MODE_COLOR_WHITE, true );
        }
        else
        {
            show_page_game_with_computer( MySurfaceView.MODE_COLOR_BLACK, true );
        }
    }

    public void OnClick_close_menu_bluetooth_game(View v)
    {
        FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_main_menu_bluetooth_game);
        if(fl != null && fl.getVisibility() == View.VISIBLE)
        {
            fl.setVisibility(View.GONE);
        }
    }

    public void OnClick_close_page_menu_play_with_computer(View v)
    {
        FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_main_menu_play_with_computer);
        if(fl != null && fl.getVisibility() == View.VISIBLE)
        {
            fl.setVisibility(View.GONE);
        }
    }

    private void start_search_bluetooth_devices()
    {
        FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_main_menu_bluetooth_game);

        if( fl != null )
        {
            fl.setVisibility(View.GONE);
        }


        fl = (FrameLayout) findViewById(R.id.fl_page_main_menu_bluetooth_search_devices);

        if( fl != null )
        {
            fl.setVisibility(View.VISIBLE);
        }

        TextView tv = (TextView) findViewById(R.id.tv_page_main_menu_title_searcching_devices);

        if( tv != null )
        {
            tv.setText( getString(R.string.txt16) );
        }

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_page_main_menu_list_searching_devices);

        if( ll != null )
        {
            ll.removeAllViews();
        }

        _blueToothInfo.searching_devices.clear();

        // Get a set of currently paired devices
        //Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        Set<BluetoothDevice> pairedDevices =_blueToothInfo.mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices)
        {
            //BluetoothClass bluetoothClass = device.getBluetoothClass();

            _blueToothInfo.searching_devices.add( new BluetoothDeviceInfoItem( device.getName(), device.getAddress() ) );

            ui_add_device_in_list( device.getName(), device.getAddress() );
        }

        // If we're already discovering, stop it
        if (_blueToothInfo.mBluetoothAdapter.isDiscovering())
        {
            _blueToothInfo.mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        _blueToothInfo.mBluetoothAdapter.startDiscovery();
    }

    private void ui_update_list_search_games()
    {
        LinearLayout ll = findViewById(R.id.ll_page_network_search_list);
        if( ll == null )
        { return; }

        for(Map.Entry<Integer, SearchUserItem> entry : hashMapSearchUsers.entrySet())
        {
            //Integer uid           = entry.getKey();
            final SearchUserItem value  = entry.getValue();

            if( value.ui_draw == null )
            {
                final LinearLayout _ll = new LinearLayout(MainActivity.this);
                _ll.setOrientation(LinearLayout.HORIZONTAL);
                _ll.setVisibility(View.GONE);
                _ll.setClickable(true);
                _ll.setBackgroundResource(R.drawable.style_select_menu_1);
                _ll.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ArrayList<String> params = new ArrayList<>();
                        params.add(String.valueOf(value.uid));

                        type_connection_network = CLIENT_OUT_CONNECT;

                        network.queue_network.add("START_GAME_FOUND_USER"
                                , params
                                , null
                                , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                , QueueNetwork.TYPE_SEND__FORCE);
                    }
                });

                _ll.setPadding((int) (10 * _scale_px), (int) (10 * _scale_px), (int) (10 * _scale_px), (int) (10 * _scale_px));

                ImageView _user_icon = new ImageView(MainActivity.this);
                _user_icon.setImageResource(R.drawable.default_avatar);

                LinearLayout.LayoutParams lp_iv_logo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (50 * _scale_px));
                lp_iv_logo.gravity = Gravity.CENTER;

                _user_icon.setLayoutParams(lp_iv_logo);
                _user_icon.setAdjustViewBounds(true);

                _ll.addView(_user_icon);

                TextView _tv = new TextView(MainActivity.this);
                _tv.setText( value.first_name + " " + value.last_name );
                _tv.setTextSize(22);
                _tv.setTypeface(_font_2);

                LinearLayout.LayoutParams lp_tv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp_tv.leftMargin = (int)( 5 * _scale_px );
                lp_tv.gravity = Gravity.CENTER;

                _tv.setLayoutParams(lp_tv);

                _ll.addView(_tv);

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        LinearLayout ll = findViewById(R.id.ll_page_network_search_list);
                        if( ll == null )
                        { return; }

                        ll.addView(_ll);
                    }
                });


                value.ui_draw = _ll;
                value.ui_icon = _user_icon;
                value.ui_uname = _tv;

                WaitThread wt = new WaitThread(0)
                {
                    @Override
                    public void callback()
                    {
                        final Bitmap b = Utils.getCroppedBitmap( Utils.getBitmapFromURL(value.picture) );

                        if (value.ui_icon != null && b != null)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    value.ui_draw.setVisibility(View.VISIBLE);
                                    value.ui_icon.setImageBitmap( b );
                                    value.ui_icon.invalidate();
                                }
                            });
                        }
                    }
                };

                wt.start();
            }
        }
    }

    private void ui_add_device_in_list(String name, String id)
    {
        TextView tv = new TextView(MainActivity.this);

        tv.setText( name );
        tv.setTextSize( 20 );
        tv.setTypeface(_font_2);
        tv.setPadding( (int)( 2 * _scale_px), (int)( 5 * _scale_px), (int)( 2 * _scale_px), (int)( 5 * _scale_px) );
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_page_main_menu_list_searching_devices);

        LinearLayout ll_add = new LinearLayout(MainActivity.this);
        ll_add.setOrientation(LinearLayout.HORIZONTAL);

        ImageView iv = new ImageView(MainActivity.this);
        iv.setImageResource(R.drawable.bluetooth_connect);
        iv.setPadding( (int)( 2 * _scale_px), (int)( 2 * _scale_px), (int)( 2 * _scale_px), (int)( 2 * _scale_px) );

        LinearLayout.LayoutParams lp_iv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_iv.gravity = Gravity.CENTER;

        iv.setLayoutParams(lp_iv);

        ll_add.addView(iv);
        ll_add.addView(tv);

        ll_add.setBackgroundResource(R.drawable.style_select_menu_1);
        ll_add.setTag( id );
        ll_add.setClickable(true);

        ll_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String device_id = (String) v.getTag();

                if (_blueToothInfo.mBluetoothAdapter.isDiscovering())
                {
                    _blueToothInfo.mBluetoothAdapter.cancelDiscovery();
                }

                global_progress = ProgressDialog.show(    MainActivity.this
                        , getString(R.string.txt18)
                        , getString(R.string.txt20)
                        , false);

                TextView tv1 = (TextView) global_progress.findViewById(android.R.id.message);
                if(tv1 != null)
                {
                    tv1.setTextColor(Color.WHITE);
                    tv1.setTypeface(_font_2);
                }

                if( _blueToothInfo.mChatService != null )
                {
                    _blueToothInfo.mChatService.stop();
                    _blueToothInfo.mChatService = null;
                }

                _blueToothInfo.mChatService = new BluetoothService(MainActivity.this, mHandler);

                _blueToothInfo.mChatService.connect( _blueToothInfo.mBluetoothAdapter.getRemoteDevice(device_id), CLIENT_OUT_CONNECT );
            }
        });

        ll.addView( ll_add );
    }

    private void ui_empty_device_list()
    {
        TextView tv = new TextView(MainActivity.this);

        tv.setText( getString(R.string.none_paired) );
        tv.setTextSize( 20 );
        tv.setTypeface(_font_2);
        tv.setPadding( (int)( 2 * _scale_px), (int)( 2 * _scale_px), (int)( 2 * _scale_px), (int)( 2 * _scale_px) );
        tv.setTextColor(Color.WHITE);

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_page_main_menu_list_searching_devices);

        ll.addView(tv);
    }

    private void ui_page_table_rating_draw_info_user(
            int ui_c
            , int ui_id
            , int count_games
            , int count_defeats
            , int count_draw
            , int count_wins
            , int count_maps
            , String fio
            , Bitmap img
    )
    {
        LinearLayout ll = findViewById(ui_id);
        if (ll == null)
        {
            return;
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        View child = getLayoutInflater().inflate(R.layout.template_info_user, null);

        setFont((ViewGroup) child, _font_2);

        ll.removeAllViews();
        ll.addView(child, lp);

        ImageView iv = findViewById(R.id.iv_template_info_user_avatar);

        iv.setImageBitmap(img);

        TextView tv1 = findViewById(R.id.tv_template_info_user_popup_total_game);
        TextView tv2 = findViewById(R.id.tv_template_info_user_popup_defeats);
        TextView tv3 = findViewById(R.id.tv_template_info_user_popup_draw);
        TextView tv4 = findViewById(R.id.tv_template_info_user_popup_wins);
        TextView tv5 = findViewById(R.id.tv_template_info_user_popup_user_name);
        TextView tv6 = findViewById(R.id.tv_template_info_user_popup_mars);

        tv1.setText(String.valueOf(count_games));
        tv2.setText(String.valueOf(count_defeats));
        tv3.setText(String.valueOf(count_draw));
        tv4.setText(String.valueOf(count_wins));
        tv6.setText(String.valueOf(count_maps));
        tv5.setText(fio);

        FrameLayout fl = findViewById(ui_c);
        fl.setVisibility(View.VISIBLE);
    }

    private void ui_draw_list_rating(List<UiItemRating> list)
    {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_page_network_raiting);

        if (ll == null)
        { return; }

        ll.setPadding(
                (int) (_scale_px * 1)
                , (int) (_scale_px * 3)
                , (int) (_scale_px * 1)
                , (int) (_scale_px * 3)
        );

        ll.removeAllViews();

        TableLayout tl = new TableLayout(MainActivity.this);

        ll.addView(tl);

        for (int i = 0; i < list.size(); i++)
        {
            TableRow TR = new TableRow(MainActivity.this);

            TR.setTag(list.get(i));
            TR.setClickable(true);
            TR.setBackgroundResource(R.drawable.style_select_menu_1);
            TR.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    UiItemRating data = (UiItemRating) v.getTag();

                    ui_page_table_rating_draw_info_user(
                              R.id.fl_table_rating_user_info
                            , R.id.fl_page_table_rating_user_info_data
                            , data.count_games
                            , data.count_defeats
                            , data.count_draw
                            , data.count_wins
                            , data.count_mars
                            , data.first_name + " " + data.last_name
                            , data.img
                    );
                }
            });

            TableRow.LayoutParams lp_1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            lp_1.gravity = Gravity.CENTER;

            TextView tv_posnum = new TextView(MainActivity.this);
            tv_posnum.setText(String.valueOf(i + 1));
            tv_posnum.setTextColor(Color.YELLOW);
            tv_posnum.setTextSize(20);
            tv_posnum.setPadding(
                    (int) (_scale_px * 10)
                    , (int) (_scale_px * 10)
                    , (int) (_scale_px * 10)
                    , (int) (_scale_px * 10)
            );
            tv_posnum.setLayoutParams(lp_1);
            tv_posnum.setTypeface(_font_2);

            TR.addView(tv_posnum);

            ImageView iv = new ImageView(MainActivity.this);

            TableRow.LayoutParams lp_2 = new TableRow.LayoutParams((int) (40 * _scale_px), (int) (40 * _scale_px));
            lp_2.gravity = Gravity.CENTER;
            iv.setLayoutParams(lp_2);

            iv.setImageResource(R.drawable.default_avatar);
            iv.setAdjustViewBounds(true);

            list.get(i).iv = iv;

            if (list.get(i).img == null)
            {
                iv.setImageResource(R.drawable.default_avatar);

                final int index = i;

                WaitThread wt = new WaitThread(0)
                {
                    @Override
                    public void callback()
                    {
                        final Bitmap b = Utils.getCroppedBitmap(Utils.getBitmapFromURL(list_rating.get(index).puctire));

                        if (b != null)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    list_rating.get(index).img = b;
                                    list_rating.get(index).iv.setImageBitmap(b);
                                }
                            });
                        }
                    }
                };
                wt.start();
            }
            else
            {
                iv.setImageBitmap(list_rating.get(i).img);
            }

            TR.addView(iv);

            TableRow.LayoutParams lp_3 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            lp_3.gravity = Gravity.CENTER;

            TextView tv_uname = new TextView(MainActivity.this);
            tv_uname.setText(list.get(i).first_name + " " + list.get(i).last_name);
            tv_uname.setTypeface(_font_2);
            tv_uname.setTextColor(Color.YELLOW);
            tv_uname.setTextSize(20);
            tv_uname.setPadding(
                    (int) (_scale_px * 10)
                    , (int) (_scale_px * 10)
                    , (int) (_scale_px * 10)
                    , (int) (_scale_px * 10)
            );
            //tv_uname.setLayoutParams(lp_3);

            TR.addView(tv_uname);

            tl.addView(TR);

            //-------------------

            TableRow tr2 = new TableRow(MainActivity.this);
            tr2.setBackgroundColor(Color.parseColor("#444444"));
            tr2.setPadding(0, 0, 0, 1);

            tl.addView(tr2);
        }

        FrameLayout fl = findViewById(R.id.fl_page_network_rating_wait);
        fl.setVisibility(View.GONE);
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    _blueToothInfo.searching_devices.add( new BluetoothDeviceInfoItem( device.getName(), device.getAddress() ) );

                    ui_add_device_in_list( device.getName(), device.getAddress() );
                }
                // When discovery is finished, change the Activity title
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                setProgressBarIndeterminateVisibility(false);
                //setTitle(R.string.select_device);

                if (_blueToothInfo.searching_devices.size() == 0)
                {
                    //String noDevices = getResources().getText(R.string.none_found).toString();
                    //mNewDevicesArrayAdapter.add(noDevices);

                    ui_empty_device_list();
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextView tv = (TextView) findViewById(R.id.tv_page_main_menu_title_searcching_devices);

                        if( tv != null )
                        {
                            tv.setText( getString(R.string.txt17) );
                        }
                    }
                });
            }
        }
    };

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGE:

                    Log.i("TAG", "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1)
                    {
                        case BluetoothService.STATE_CONNECTED:
                            //mTitle.setText(R.string.title_connected_to);
                            //mTitle.append(mConnectedDeviceName);
                            //mConversationArrayAdapter.clear();

                            if( quenue_network != null )
                            {
                                quenue_network.OnDestroy();
                                quenue_network = null;
                            }

                            quenue_network = new QueueNetworkBluetooth(_blueToothInfo.mChatService);

                            if( msg.arg2 == MainActivity.CLIENT_OUT_CONNECT )
                            {
                                if( global_progress != null )
                                {
                                    global_progress.setMessage( getText(R.string.txt21) );
                                }

                                /*byte[] b_cmd = ProtokolUtils.get_buffer_command_encrypt(
                                        "OK_SEND"
                                        , null
                                        , null
                                        , JniApi.dfp1()
                                        , System.currentTimeMillis());

                                Log.i("TAG","SEND CMD: OK_SEND");

                                _blueToothInfo.mChatService.write(b_cmd);*/

                                global_bluetooth_connect_ok = false;

                                quenue_network.add("INIT_SESSION"
                                        , null
                                        , null
                                        , System.currentTimeMillis()
                                        , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                        , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);

                                time_out_connect_device = new WaitThread(10000)
                                {
                                    @Override
                                    public void callback()
                                    {
                                        if( global_bluetooth_connect_ok )
                                        {
                                            return;
                                        }

                                        if( global_progress != null )
                                        {
                                            global_progress.dismiss();
                                            global_progress = null;
                                        }

                                        _blueToothInfo.mChatService.stop();

                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {


                                                String title = getString(R.string.txt22);
                                                String yes   = getString(R.string.txt13);

                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
                                                alertDialogBuilder.setTitle( title );

                                                alertDialogBuilder
                                                        .setMessage("")
                                                        .setCancelable(false)
                                                        .setPositiveButton(yes, new DialogInterface.OnClickListener()
                                                        {
                                                            public void onClick(DialogInterface dialog,int id)
                                                            {

                                                            }
                                                        });

                                                AlertDialog alertDialog = alertDialogBuilder.create();
                                                alertDialog.show();
                                            }
                                        });

                                    }
                                };

                                time_out_connect_device.start();
                            }

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);

                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:

                            //mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;

                case MESSAGE_WRITE:

                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;

                case MESSAGE_READ:

                    ReadCommand r = (ReadCommand)msg.obj;

                    _exec_read(r, msg.arg1);

                    //byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    //String readMessage = new String(readBuf, 0, msg.arg1);
                    //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);

                    break;

                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), "Connected to "
                    //        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_TOAST:

                    /*Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();*/

                    break;


                case MESSAGE_CONNECTION_FAILED:

                    if(global_progress != null)
                    {
                        global_progress.dismiss();
                        global_progress = null;
                    }

                    String title = getString(R.string.txt19);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
                    alertDialogBuilder.setTitle( title );

                    alertDialogBuilder
                            .setMessage("")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,int id)
                                {

                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    break;

                case MESSAGE_CONNECTION_LOST:



                    break;

                case MESSAGE_DISCONECT:

                    _blueToothInfo.mChatService.stop();
                    _blueToothInfo.mChatService.start();

                    if( msg.arg2 == MainActivity.CLIENT_IN_CONNECT )
                    {

                    }
                    else if( msg.arg2 == MainActivity.CLIENT_OUT_CONNECT )
                    {
                        if (sv_page == SV_PAGE_GAME)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    String title = getString(R.string.txt27);
                                    String ok = "OK";

                                    if (global_alertDialog != null)
                                    {
                                        global_alertDialog.dismiss();
                                        global_alertDialog = null;
                                    }

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                    alertDialogBuilder.setTitle(title);

                                    alertDialogBuilder
                                            .setMessage("")
                                            .setCancelable(false)
                                            .setPositiveButton(ok, new DialogInterface.OnClickListener()
                                            {
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                    show_page_main_menu();
                                                }
                                            });

                                    global_alertDialog = alertDialogBuilder.create();
                                    global_alertDialog.show();

                                }
                            });
                        }
                    }

                    break;
            }
        }
    };

    private void _exec_read(ReadCommand r, int type_connect)
    {
        Log.i("TAG","READ CMD: " + r.cmd);

        if( _game != null && _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
        {
            if (r.cmd.equalsIgnoreCase("OK_SEND"))  /// подтверждение
            {
                quenue_network.okSend(r.timestamp);
                return;
            }

            //----------------------------------------------------------
            String cmd = "OK_SEND";

            quenue_network.add(cmd, null, null, r.timestamp, QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM, 3);
            //----------------------------------------------------------

            if( quenue_network.foundInput(r.timestamp) )
            {
                return;
            }
            else
            {
                quenue_network.addInput(r.timestamp);
            }
        }

        if( type_connect == MainActivity.CLIENT_IN_CONNECT ) /// запросы к серверу от клиента
        {
            if(  r.cmd.equalsIgnoreCase("INIT_SESSION") )  /// подтверждение
            {
                ArrayList<String> params = new ArrayList<>();
                params.add("OK");

                // ответ клиенту
                if( _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                {
                    quenue_network.add("INIT_SESSION"
                            , params
                            , null
                            , System.currentTimeMillis()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                }
                else
                {
                    network.queue_network.add("INIT_SESSION"
                            , params
                            , null
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);
                }
            }
            else if(  r.cmd.equalsIgnoreCase("QUESTION_CONNECT") )
            {
                final String name_q = new String(r.params.get(0));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        String title = getString(R.string.txt23);
                        String yes   = getString(R.string.txt10);
                        String no    = getString(R.string.txt11);

                        if( global_alertDialog != null )
                        {
                            global_alertDialog.dismiss();
                            global_alertDialog = null;
                        }

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
                        alertDialogBuilder.setTitle( title );
                        alertDialogBuilder.setMessage( name_q );

                        alertDialogBuilder
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton(yes, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,int id)
                                    {
                                        ArrayList<String> params = new ArrayList<>();
                                        params.add("YES");

                                        // ответ клиенту
                                        if( _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                                        {
                                            quenue_network.add("QUESTION_CONNECT"
                                                    , params
                                                    , null
                                                    , System.currentTimeMillis()
                                                    , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                                    , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                                        }
                                        else
                                        {
                                            network.queue_network.add("QUESTION_CONNECT"
                                                    , params
                                                    , null
                                                    , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                                    , QueueNetwork.TYPE_SEND__FORCE);
                                        }
                                    }
                                })
                                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        ArrayList<String> params = new ArrayList<>();
                                        params.add("NO");

                                        // ответ клиенту
                                        if( _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                                        {
                                            quenue_network.add("QUESTION_CONNECT"
                                                    , params
                                                    , null
                                                    , System.currentTimeMillis()
                                                    , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                                    , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                                        }
                                        else
                                        {
                                            network.queue_network.add("QUESTION_CONNECT"
                                                    , params
                                                    , null
                                                    , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                                    , QueueNetwork.TYPE_SEND__FORCE);
                                        }
                                    }
                                });

                        global_alertDialog = alertDialogBuilder.create();
                        global_alertDialog.show();


                    }
                });
            }
            else if(  r.cmd.equalsIgnoreCase("GET_GAME_INFO") ) // отправить клиенту
            {
                int count_dice = 2;
                if( _game.current_action_cmd != ACTION_NONE )
                {
                    count_dice = _game.rand_dices.size();
                }

                ByteBuffer b_buf = ByteBuffer.allocate( 2 + 2 + 1 + 2 * count_dice + 24 * 3 );

                b_buf.put( (byte) _game.mode_board );                // 1 byte
                b_buf.put( (byte) _game.current_action_cmd );        // 1 byte
                b_buf.put( (byte) _game.count_xod_white );           // 1 byte
                b_buf.put( (byte) _game.count_xod_black );           // 1 byte

                if( _game.current_action_cmd == ACTION_NONE )
                {
                    // кубики
                    b_buf.put( (byte) 2 );                               // 1 byte
                    b_buf.put( (byte) _game.rand_num_dice_first_white ); // 1 byte
                    b_buf.put( (byte) _game.rand_num_dice_first_black ); // 1 byte
                    b_buf.put( (byte) 0 ); // 1 byte
                    b_buf.put( (byte) 0 ); // 1 byte
                }
                else
                {
                    // кубики
                    b_buf.put( (byte) _game.rand_dices.size() ); // 1 byte

                    for(int i = 0; i < _game.rand_dices.size(); i++ )
                    {
                        b_buf.put( (byte) _game.rand_dices.get(i).value ); // 1 byte
                    }

                    // статусы кубиков
                    for(int i = 0; i < _game.rand_dices.size(); i++ )
                    {
                        b_buf.put( (byte) (_game.rand_dices.get(i).exec == true ? 1 : 0 ) ); // 1 byte
                    }
                }


                for( int i = 0; i < _game._game_ui_draw._cells.size(); i++ )
                {
                    MySurfaceView.CellValues cv = _game._game_ui_draw._cells.get(i);

                    if( cv.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                    {
                        b_buf.put( (byte)cv.pos_white );         // 1 byte
                        b_buf.put( (byte)cv.count_figures );     // 1 byte
                        b_buf.put( (byte)cv.color_type );        // 1 byte
                    }
                    else if( cv.color_type == MySurfaceView.FIGURE_COLOR_BLACK )
                    {
                        b_buf.put( (byte)cv.pos_black );         // 1 byte
                        b_buf.put( (byte)cv.count_figures );     // 1 byte
                        b_buf.put( (byte)cv.color_type );        // 1 byte
                    }
                    else
                    {
                        b_buf.put( (byte)0 );     // 1 byte
                        b_buf.put( (byte)0 );     // 1 byte
                        b_buf.put( (byte)0 );     // 1 byte
                    }
                }

                // ответ клиенту
                if( _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                {
                    quenue_network.add("GET_GAME_INFO"
                            , null
                            , b_buf.array()
                            , System.currentTimeMillis()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                }
                else
                {
                    network.queue_network.add("GET_GAME_INFO"
                            , null
                            , b_buf.array()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);
                }
            }
            else if(  r.cmd.equalsIgnoreCase("GET_GAME_INFO_OK_LOAD") ) // отправить клиенту
            {
                // пришла команда на успешную загруку данных
                // ответ клиенту
                if( _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                {
                    quenue_network.add("START_GAME"
                            , null
                            , null
                            , System.currentTimeMillis()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                }
                else
                {
                    network.queue_network.add("START_GAME"
                            , null
                            , null
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);
                }

                if( _game.current_action_cmd == ACTION_NONE )
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_game_wait_user);

                            if (fl != null) {
                                fl.setVisibility(View.GONE);
                            }
                        }
                    });

                    _game.current_action_cmd = ACTION_START_GAME;
                    _game.exec_game();
                }
            }
        }
        //------------------------------------------------------------------------------------------
        else if( type_connect == MainActivity.CLIENT_OUT_CONNECT ) /// запросы от клиента к серверу
        {
            if(  r.cmd.equalsIgnoreCase("INIT_SESSION") )  /// подтверждение начала сессии
            {
                global_bluetooth_connect_ok = true;

                if( global_progress != null )
                {
                    global_progress.setMessage( getText(R.string.txt24) );
                }

                ArrayList<String> params = new ArrayList<>();
                params.add(_blueToothInfo.mBluetoothAdapter.getName());

                // ответ серверу
                if( _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                {
                    quenue_network.add("QUESTION_CONNECT"
                            , params
                            , null
                            , System.currentTimeMillis()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                }
                else
                {
                    network.queue_network.add("QUESTION_CONNECT"
                            , params
                            , null
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);
                }
            }
            else if(  r.cmd.equalsIgnoreCase("QUESTION_CONNECT") )
            {
                final String value = new String(r.params.get(0));

                if( value.equalsIgnoreCase("YES") )
                {
                    if( global_progress != null )
                    {
                        global_progress.setMessage( getText(R.string.txt26) );
                    }

                    if( _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                    {
                        quenue_network.add("GET_GAME_INFO"
                                , null
                                , null
                                , System.currentTimeMillis()
                                , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                    }
                    else
                    {
                        network.queue_network.add("GET_GAME_INFO"
                                , null
                                , null
                                , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                , QueueNetwork.TYPE_SEND__FORCE);
                    }
                }
                else
                {
                    if(global_progress != null)
                    {
                        global_progress.dismiss();
                        global_progress = null;
                    }

                    _blueToothInfo.mChatService.stop();
                    _blueToothInfo.mChatService = null;

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
                            alertDialogBuilder.setTitle( getString(R.string.txt25) );

                            alertDialogBuilder
                                    .setMessage("")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog,int id)
                                        {

                                        }
                                    });
                        }
                    });

                }
            }
            else if(  r.cmd.equalsIgnoreCase("GET_GAME_INFO") )
            {
                if(global_progress != null)
                {
                    global_progress.dismiss();
                    global_progress = null;
                }

                byte[] _data = r.data;
                int offset = 0;

                byte[] mode_board = new byte[1];
                byte[] rand_num_dice_first_white = new byte[1];
                byte[] rand_num_dice_first_black = new byte[1];
                byte[] current_action_cmd = new byte[1];
                byte[] count_dice = new byte[1];
                byte[] count_xod_white = new byte[1];
                byte[] count_xod_black = new byte[1];

                byte[] value_s = null;
                byte[] exec_s  = null;

                System.arraycopy(_data, offset, mode_board, 0, 1); offset += 1;
                System.arraycopy(_data, offset, current_action_cmd, 0, 1); offset += 1;

                System.arraycopy(_data, offset, count_xod_white, 0, 1); offset += 1;
                System.arraycopy(_data, offset, count_xod_black, 0, 1); offset += 1;

                System.arraycopy(_data, offset, count_dice, 0, 1); offset += 1;

                if( current_action_cmd[0] == ACTION_NONE )
                {
                    System.arraycopy(_data, offset, rand_num_dice_first_white, 0, 1); offset += 1;
                    System.arraycopy(_data, offset, rand_num_dice_first_black, 0, 1); offset += 1;

                    offset += 1;
                    offset += 1;
                }
                else
                {
                    value_s   = new byte[ count_dice[0] ];
                    exec_s    = new byte[ count_dice[0] ];

                    for(int k = 0; k < count_dice[0]; k++)
                    {
                        System.arraycopy(_data, offset, value_s, k, 1); offset += 1;
                    }

                    for(int k = 0; k < count_dice[0]; k++)
                    {
                        System.arraycopy(_data, offset, exec_s, k, 1); offset += 1;
                    }
                }

                if( mode_board[0] == MySurfaceView.MODE_COLOR_WHITE )
                {
                    mode_board[0] = MySurfaceView.MODE_COLOR_BLACK;
                }
                else
                {
                    mode_board[0] = MySurfaceView.MODE_COLOR_WHITE;
                }

                byte[] boeard_info_1 = new byte[24];
                byte[] boeard_info_2 = new byte[24];
                byte[] boeard_info_3 = new byte[24];

                for(int i = 0; i < 24; i++)
                {
                    System.arraycopy(_data, offset, boeard_info_1, i, 1); offset += 1; // pos
                    System.arraycopy(_data, offset, boeard_info_2, i, 1); offset += 1; // count_f
                    System.arraycopy(_data, offset, boeard_info_3, i, 1); offset += 1; // color
                }

                if( network == null )
                {
                    show_page_game_two_players_bluetooth_network(MODE_GAME__BLUETOOTH_TWO_PLAYERS, (int) mode_board[0], boeard_info_1, boeard_info_2, boeard_info_3);
                }
                else
                {
                    show_page_game_two_players_bluetooth_network(MODE_GAME__NETWORK, (int) mode_board[0], boeard_info_1, boeard_info_2, boeard_info_3);
                }

                _game.count_xod_white = count_xod_white[0];
                _game.count_xod_black = count_xod_black[0];

                _game.current_action_cmd        = current_action_cmd[0];

                if( current_action_cmd[0] == ACTION_NONE )
                {
                    _game.rand_num_dice_first_white = rand_num_dice_first_white[0];
                    _game.rand_num_dice_first_black = rand_num_dice_first_black[0];
                }
                else
                {
                    _game.rand_dices.clear();

                    for(int k = 0; k < count_dice[0]; k++)
                    {
                        TrownDice td = new TrownDice( value_s[k], ( exec_s[k] == 1 ) ? true : false );

                        _game.rand_dices.add( td );
                    }
                }

                // ответ серверу
                if( _game.mode_game == MODE_GAME__BLUETOOTH_TWO_PLAYERS )
                {
                    quenue_network.add("GET_GAME_INFO_OK_LOAD"
                            , null
                            , null
                            , System.currentTimeMillis()
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetworkBluetooth.LIMIT_COUNT_SENDED);
                }
                else
                {
                    network.queue_network.add("GET_GAME_INFO_OK_LOAD"
                            , null
                            , null
                            , QueueNetworkBluetooth.NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);
                }
            }
            else if(  r.cmd.equalsIgnoreCase("START_GAME") )
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout fl = (FrameLayout) findViewById(R.id.fl_page_game_wait_user);

                        if (fl != null) {
                            fl.setVisibility(View.GONE);
                        }
                    }
                });

                if( network == null )
                {
                    _game.mode_game = MODE_GAME__BLUETOOTH_TWO_PLAYERS;
                }
                else
                {
                    _game.mode_game = MODE_GAME__NETWORK;
                }

                if( _game.current_action_cmd == ACTION_NONE )
                {
                    _game.current_action_cmd = ACTION_START_GAME;
                    _game.exec_game();
                }
                else
                {
                    _game._game_ui_draw.clearPlanAnimationSlot();

                    if( _game.current_action_cmd == ACTION_XOD_WHITE)
                    {
                        if (_game.mode_board == MySurfaceView.MODE_COLOR_WHITE)
                        {
                            MySurfaceView.PlanInfo p = _draw_task.create_plan_and_path_draw(_game.rand_dices.get(0).value, 0x01, 10, MySurfaceView
                                    .PLAN_TYPE_PATH_11);
                            p.exec_xod = _game.rand_dices.get(0).exec;
                            _game._game_ui_draw.addPlanAnimationInSlot(p);


                            MySurfaceView.PlanInfo p2 = _draw_task.create_plan_and_path_draw(_game.rand_dices.get(1).value, 0x02, 10, MySurfaceView
                                    .PLAN_TYPE_PATH_22);
                            p2.exec_xod = _game.rand_dices.get(1).exec;
                            _game._game_ui_draw.addPlanAnimationInSlot(p2);
                        }
                        else
                        {
                            MySurfaceView.PlanInfo p = _draw_task.create_plan_and_path_draw(_game.rand_dices.get(0).value, 0x01, 10, MySurfaceView
                                    .PLAN_TYPE_PATH_3);
                            p.exec_xod = _game.rand_dices.get(0).exec;
                            _game._game_ui_draw.addPlanAnimationInSlot(p);

                            MySurfaceView.PlanInfo p2 = _draw_task.create_plan_and_path_draw(_game.rand_dices.get(1).value, 0x02, 10, MySurfaceView
                                    .PLAN_TYPE_PATH_4);
                            p2.exec_xod = _game.rand_dices.get(1).exec;
                            _game._game_ui_draw.addPlanAnimationInSlot(p2);
                        }
                    }
                    else if( _game.current_action_cmd == ACTION_XOD_BLACK)
                    {
                        if( _game.mode_board == MySurfaceView.MODE_COLOR_WHITE )
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( _game.rand_dices.get(0).value, 0x01, 10, MySurfaceView
                                    .PLAN_TYPE_PATH_3);
                            p.exec_xod = _game.rand_dices.get(0).exec;
                            _game._game_ui_draw.addPlanAnimationInSlot(p);

                            MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( _game.rand_dices.get(1).value,  0x02, 10, MySurfaceView
                                    .PLAN_TYPE_PATH_4);
                            p2.exec_xod = _game.rand_dices.get(1).exec;
                            _game._game_ui_draw.addPlanAnimationInSlot(p2);
                        }
                        else
                        {
                            MySurfaceView.PlanInfo p  = _draw_task.create_plan_and_path_draw( _game.rand_dices.get(0).value, 0x01, 10, MySurfaceView
                                    .PLAN_TYPE_PATH_11);
                            p.exec_xod = _game.rand_dices.get(0).exec;

                            _game._game_ui_draw.addPlanAnimationInSlot(p);

                            MySurfaceView.PlanInfo p2  = _draw_task.create_plan_and_path_draw( _game.rand_dices.get(1).value,  0x02, 10, MySurfaceView
                                    .PLAN_TYPE_PATH_22);
                            p2.exec_xod = _game.rand_dices.get(1).exec;
                            _game._game_ui_draw.addPlanAnimationInSlot(p2);
                        }
                    }
                }
            }
            //--------------------------------------------------
        }

        if(  r.cmd.equalsIgnoreCase("ACTION_TROW_DICE_WHITE") )
        {
            final byte[] _data = r.data;

            WaitThread wt = new WaitThread( 350 )
            {
                @Override
                public void callback()
                {
                    int offset = 0;

                    byte[] next_dice_value_1 = new byte[1];
                    byte[] next_dice_value_2 = new byte[1];

                    System.arraycopy(_data, offset, next_dice_value_1, 0, 1); offset += 1;
                    System.arraycopy(_data, offset, next_dice_value_2, 0, 1); offset += 1;

                    Log.i("TAG", String.valueOf(next_dice_value_1[0]));
                    Log.i("TAG", String.valueOf(next_dice_value_2[0]));

                    _game.current_action_cmd = ACTION_TROW_DICE_WHITE;
                    _game.next_dice_value_1  = next_dice_value_1[0];
                    _game.next_dice_value_2  = next_dice_value_2[0];

                    _game.current_xod_log = new XodDetail();
                    _game.current_xod_log.color_action_xod = ACTION_XOD_WHITE;

                    _game._game_ui_draw.disable_select_pos_cell_clear();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();
                    _game._game_ui_draw.clear_selected_cell();

                    _game.exec_game();
                }
            };

            wt.start();
        }
        else if(  r.cmd.equalsIgnoreCase("ACTION_TROW_DICE_BLACK") )
        {
            final byte[] _data = r.data;

            WaitThread wt = new WaitThread( 350 )
            {
                @Override
                public void callback()
                {
                    int offset = 0;

                    byte[] next_dice_value_1 = new byte[1];
                    byte[] next_dice_value_2 = new byte[1];

                    System.arraycopy(_data, offset, next_dice_value_1, 0, 1); offset += 1;
                    System.arraycopy(_data, offset, next_dice_value_2, 0, 1); offset += 1;

                    Log.i("TAG", String.valueOf(next_dice_value_1[0]));
                    Log.i("TAG", String.valueOf(next_dice_value_2[0]));

                    _game.current_action_cmd = ACTION_TROW_DICE_BLACK;
                    _game.next_dice_value_1  = next_dice_value_1[0];
                    _game.next_dice_value_2  = next_dice_value_2[0];

                    _game.current_xod_log = new XodDetail();
                    _game.current_xod_log.color_action_xod = ACTION_XOD_BLACK;

                    _game._game_ui_draw.disable_select_pos_cell_clear();
                    _game._game_ui_draw.clear_select_points();
                    _game._game_ui_draw.clear_select_end_points();
                    _game._game_ui_draw.clear_selected_cell();

                    _game.exec_game();
                }
            };

            wt.start();
        }
        else if(  r.cmd.equalsIgnoreCase("EXEC_XOD") ) // отправить клиенту
        {
            byte[] _data = r.data;
            int offset = 0;

            byte[] color = new byte[1];
            byte[] from  = new byte[1];
            byte[] to    = new byte[1];

            System.arraycopy(_data, offset, color, 0, 1); offset += 1;
            System.arraycopy(_data, offset, from, 0, 1); offset += 1;
            System.arraycopy(_data, offset, to, 0, 1); offset += 1;

            if( color[0] == MySurfaceView.FIGURE_COLOR_WHITE )
            {
                _game.select_cell( _game._game_ui_draw.get_cell_pos_white( from[0] ), false );
                _game.select_cell( _game._game_ui_draw.get_cell_pos_white( to[0] ), false );
            }
            else if( color[0] == MySurfaceView.FIGURE_COLOR_BLACK )
            {
                _game.select_cell( _game._game_ui_draw.get_cell_pos_black( from[0] ), false );
                _game.select_cell( _game._game_ui_draw.get_cell_pos_black( to[0] ), false );
            }
        }
        else if(  r.cmd.equalsIgnoreCase("XOD_OUT_OF_BOARD") )
        {
            byte[] _data = r.data;
            int offset = 0;

            byte[] color = new byte[1];
            byte[] pos   = new byte[1];
            byte[] _mode_board    = new byte[1];

            System.arraycopy(_data, offset, color, 0, 1); offset += 1;
            System.arraycopy(_data, offset, pos, 0, 1); offset += 1;
            System.arraycopy(_data, offset, _mode_board, 0, 1); offset += 1;

            MySurfaceView.CellValues cell = null;

            if( (int)color[0] == MySurfaceView.FIGURE_COLOR_WHITE )
            {
                cell = _game._game_ui_draw.get_cell_pos_white( pos[0] );
            }
            else
            {
                cell = _game._game_ui_draw.get_cell_pos_black( pos[0] );
            }

            CImageButton btn_pressed = null;

            if( _mode_board[0] == MySurfaceView.MODE_COLOR_WHITE)
            {
                if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                {
                    btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                }
                else
                {
                    btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                }
            }
            else
            {
                if( cell.color_type == MySurfaceView.FIGURE_COLOR_WHITE )
                {
                    btn_pressed = _game._game_ui_draw.getUiBtn( 2 );
                }
                else
                {
                    btn_pressed = _game._game_ui_draw.getUiBtn( 1 );
                }
            }

            _game._game_ui_draw.create_animation_figure_out_board(
                      cell.pos
                    , btn_pressed.getCenterPosX()
                    , btn_pressed.getCenterPosY()
                    , 7
                    , 15
            );

            _game._game_ui_draw.get_cell( cell.pos ).count_figures -= 1;

            /*if( current_select_figure.count_figures == 0 )
            {
                current_select_figure.color_type = MySurfaceView.FIGURE_COLOR_NONE;
            }*/

            _game._game_ui_draw.action_animation_fly_figure = true;

            _game._game_ui_draw.clear_select_points();
            _game._game_ui_draw.clear_select_end_points();
            _game._game_ui_draw.selected_alternative_xods.clear();
            _game._game_ui_draw.selected_alternative_xods_red.clear();
        }
        else if(  r.cmd.equalsIgnoreCase("END_GAME") )
        {
            byte[] _data = r.data;
            int offset = 0;

            byte[] status = new byte[1];

            System.arraycopy(_data, offset, status, 0, 1); offset += 1;

            show_end_game_info( status[0] );
        }

    }

    ///=============================================================================================
    private void connection_server()
    {
        if (network != null)
        {
            return;
        }

        network = new ClassNetWork(MainActivity.this, HOST, TCP_PORT_SERVER_1, session_info)
        {
            @Override
            public void callback_close_connection()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        show_page_connection_server();
                        //show_page_add_money();

                        /*TextView tv = (TextView) findViewById(R.id.textView_version);

                        if(tv != null)
                        {
                            tv.setText("v" + VERSION_APP+" beta");
                        }*/
                    }
                });
            }

            @Override
            public void callback_open_connection(int count_open_connection)
            { }

            @Override
            public void callback_open_connection2(int index_c, String v)
            {
                //Log.i("TAG", "_callback_open_connection2: " + String.valueOf(index_c) + " " + v);

                if (index_c == 1)
                {
                    if (network != null && network.queue_network != null)
                    {
                        network.clearBuffersInOut();
                        network.queue_network.cliearAll();
                    }
                    else
                    {
                        //Log.i("TAG", "callback_open_connection2 error 1");
                    }

                    if (network != null && network.queue_network != null)
                    {
                        //Log.i("TAG", "network.queue_network.add  FIRST_CMD ");

                        session_info = new SessionInfo();

                        network.queue_network.add("FIRST_CMD"
                                , null
                                , null
                                , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                , QueueNetwork.TYPE_SEND__FORCE);
                    }
                    else
                    {
                        //Log.i("TAG", "callback_open_connection2 error 2");
                    }

                }
                else
                {
                    //Log.i("TAG", "index_c == 1 error");
                }
            }

            @Override
            public int callback_on_read(ReadCommand r, InputStream input)
            {
                _exec(r, input);

                return 0;
            }

            @Override
            public boolean callback_exec2(final DataInputStream in_soket, final ReadCommand r, Socket sock, final Object param)
            {
                return _exec2(in_soket, r, sock, param);
            }

            @Override
            public void callback_on_send_echo()
            {
                //Log.i("TAG", "callback_on_send_echo");

                //if(_current_user_id > 0 )
                {
                    //ArrayList<String> params = new ArrayList<String>();

                    network.queue_network.add("ECHO"
                            , null
                            , null
                            , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                            , QueueNetwork.TYPE_SEND__FORCE);
                }
            }
        };
    }

    public void _thread_run_search_search()
    {
        if( thread_run_search_search != null )
        { thread_run_search_search = null; }

        thread_run_search_search = new ThreadF()
        {
            @Override
            public void callback_run()
            {
                network.queue_network.add("SERACH_GAMES"
                        , null
                        , null
                        , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                        , QueueNetwork.TYPE_SEND__FORCE);

                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread_run_search_search.start();
    }
    //**********************************************************************************************

    private void authGoogle()
    {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone())
        {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("TAG", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);

            /*runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    show_page_choose_gender();
                }
            });*/
        }
        else
        {
            //show_page_reg();

            // Если пользователь ранее не входил в систему на этом устройстве или срок действия входа истек,
            // эта асинхронная ветвь попытается выполнить вход в систему пользователя в автоматическом режиме.  Кросс-устройство
            // единый вход будет происходить в этой ветке.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>()
            {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult)
                {
                    //hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    void _exec(final ReadCommand r, InputStream input)
    {
        Log.i("TAG54", "< cmd : " + r.cmd + " " + String.valueOf(r.timestamp));

        if (r.cmd.equalsIgnoreCase("OK_SEND"))  /// подтверждение
        {
            network.queue_network.okSend(r.timestamp);

            return;
        }

        //Log.i("TAG", "cmd: " + r.cmd);

        //----------------------------------------------------------
        String cmd = "OK_SEND";

        network.addOutBuffer(cmd, null, null, ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_HIGH, r.timestamp);
        //----------------------------------------------------------

        if (network.queue_network.foundInput(r.timestamp))
        {
            return;
        }
        else
        {
            network.queue_network.addInput(r.timestamp);
        }

        if (r.cmd.equalsIgnoreCase("FIRST_CMD"))
        {
            if (r.data != null)
            {
                int offset = 0;
                byte[] byff = ByteBuffer.allocate(4).put(r.data, offset, 4).array();
                offset += 4;

                session_info.session_socket_id = ProtokolUtils2.fromByteArray(byff);

                //session_info.aes_key = new byte[16];
                session_info.xor_key = new byte[16];

                session_info.type_key = 1;
                //System.arraycopy(r.data, offset, session_info.aes_key, 0, 16);
                //offset += 16;
                System.arraycopy(r.data, offset, session_info.xor_key, 0, 16); offset += 16;

                network.setSessionInfo(session_info);
            }

            /*int _socket_server = Integer.parseInt(new String(r.params.get(0)));
            String _xcode_session = (new String(r.params.get(1)));

            socket_s = _socket_server;
            xcode_session = _xcode_session;


            if (_current_user_id == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //show_page_input_or_reg();
                        show_page_reg();
                    }
                });
            } else {
                send_command_init_session();

                List<DB.StructContactInfo_FromLoadServer> list_users = db._db_u2u_get_all_from_send_server();

                ArrayList<String> params = new ArrayList<String>();

                String list_contacts = "";

                for (int i = 0; i < list_users.size(); i++) {
                    if (list_users.get(i).status == 3) {
                        list_contacts += String.valueOf(list_users.get(i).user_id) + "|";
                    }
                }

                params.add(list_contacts);

                network.queue_network.add("ECHO"
                        , params
                        , null
                        , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                        , QueueNetwork.TYPE_SEND__FORCE);
            }*/

            /*runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    show_page_reg();
                }
            });*/

            authGoogle();
        }
        else if (r.cmd.equalsIgnoreCase("ECHO"))
        {
            if (r.data != null)
            {
                int offset = 0, count_read = 0;
                byte[] _len = ByteBuffer.allocate(4).array();
                System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                ByteBuffer buffer = ByteBuffer.wrap(_len);
                buffer.order(ByteOrder.BIG_ENDIAN);

                final int count_online = buffer.getInt();

                last_count_online = count_online;

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        /*TextView tv = findViewById(R.id.tv_page_search_txt1);
                        if (tv != null)
                        {
                            tv.setText( getString(R.string.txt25) + ": " + String.valueOf(count_online));
                        }*/
                    }
                });
            }
        }
        else if (r.cmd.equalsIgnoreCase("SERACH_USERS_OK"))
        {
            if (r.data != null)
            {
                int offset = 0;
                byte[] _len = ByteBuffer.allocate(4).array();
                System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                ByteBuffer buffer = ByteBuffer.wrap(_len);
                buffer.order(ByteOrder.BIG_ENDIAN);

                /*current_connection_uid = buffer.getInt();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        show_page_chat();
                    }
                });

                if( thread_run_search_search != null )
                {
                    thread_run_search_search.run = false;
                    thread_run_search_search = null;
                }*/
            }
        }
        else if (r.cmd.equalsIgnoreCase("SERACH_GAMES"))
        {
            if (r.data != null)
            {
                int offset = 0, count_read;
                short count_rooms;

                byte[] _len = ByteBuffer.allocate(2).array();
                System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                ByteBuffer buffer = ByteBuffer.wrap(_len);
                buffer.order(ByteOrder.BIG_ENDIAN);

                count_rooms = buffer.getShort();

                List<Integer> items = new ArrayList<>();

                for(int i = 0; i < count_rooms; i++)
                {
                    final SearchUserItem si = new SearchUserItem();

                    //+++++

                    _len = ByteBuffer.allocate(1).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    count_read = (int) _len[0] & 0xff;

                    _len = ByteBuffer.allocate(count_read).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    si.picture = new String(_len);

                    //+++++

                    _len = ByteBuffer.allocate(1).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    count_read = (int) _len[0] & 0xff;

                    _len = ByteBuffer.allocate(count_read).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    si.first_name = new String(_len);

                    //+++++

                    _len = ByteBuffer.allocate(1).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    count_read = (int) _len[0] & 0xff;

                    _len = ByteBuffer.allocate(count_read).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    si.last_name = new String(_len);

                    //******************************************************************************
                    _len = ByteBuffer.allocate(4).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    si.uid = buffer.getInt();

                    items.add(si.uid);

                    if( ! hashMapSearchUsers.containsKey( si.uid ) )
                    {
                        hashMapSearchUsers.put( si.uid, si );
                    }
                    else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                SearchUserItem _si = hashMapSearchUsers.get(si.uid);
                                _si.ui_draw.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                for(Map.Entry<Integer, SearchUserItem> entry : hashMapSearchUsers.entrySet())
                {
                    final Integer uid = entry.getKey();

                    if( ! items.contains( uid ) )
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                SearchUserItem _si = hashMapSearchUsers.get(uid);
                                _si.ui_draw.setVisibility(View.GONE);
                            }
                        });
                    }
                }


                ui_update_list_search_games();
            }
        }
        else if (r.cmd.equalsIgnoreCase("SERACH_GAMES"))
        {
            if (r.data != null)
            {
                int offset = 0, count_read;
                short count_rooms;

                byte[] _len = ByteBuffer.allocate(1).array();
                System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                if( _len[0] == 0 )
                {

                }
            }
        }
        else if (r.cmd.equalsIgnoreCase("START_GAME_2"))
        {
            if( _game.mode_game == MODE_GAME__NETWORK )
            {
                ReadCommand _r = new ReadCommand();
                _r.cmd = "GET_GAME_INFO";

                type_connection_network = CLIENT_IN_CONNECT;

                _exec_read(_r, CLIENT_IN_CONNECT);
            }
        }
        else if (r.cmd.equalsIgnoreCase("STOP_GAME_NETWORK_W"))
        {
            if( _game != null && _game.mode_game == MODE_GAME__NETWORK )
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme));

                        alertDialogBuilder.setMessage( getString(R.string.txt_60) );
                        // set dialog message
                        alertDialogBuilder
                                //.setMessage(body)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        show_page_network_menu();

                                        dialog.dismiss();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });

            }
        }
        else
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _exec_read(r, type_connection_network);
                }
            });
        }

        //------------------------------------------------------------------------------------------

    }

    ///----------------------------------------------------------------------------------------------------
    boolean _exec2(final DataInputStream in_soket, final ReadCommand r, Socket sock, final Object param)
    {
        //----------------------------------------------------------
        if (r.cmd.equalsIgnoreCase("OK_SEND"))  /// подтверждение
        {
            if (network != null)
            {
                // удаление из очереди отправленого запроса
                network.queue_network.okSend(r.timestamp);
            }

            return false; // читаем дальше что есть
        }
        //----------------------------------------------------------

        if (network != null)
        {
            try
            {
                //отправко подтверждаения в потоке текушем
                byte[] b_cmd = ProtokolUtils2.get_buffer_command_encrypt(
                        "OK_SEND"
                        , null
                        , null
                        , session_info
                        , r.timestamp);

                DataOutputStream output = new DataOutputStream(sock.getOutputStream());
                output.write(b_cmd);
                output.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // исключить дублирование приема
            if (network.queue_network.foundInput(r.timestamp))
            {
                return true;
            }
            else
            {
                network.queue_network.addInput(r.timestamp);
            }
        }

        if (r.cmd.equalsIgnoreCase("SERVER_INIT_SESSION"))
        {
            if (r.data != null)
            {
                int offset = 0;
                byte[] _len = ByteBuffer.allocate(1).array();

                System.arraycopy(r.data, offset, _len, 0, _len.length);
                offset += _len.length;

                int count_read = _len[0];
                _len = ByteBuffer.allocate(count_read).array();
                System.arraycopy(r.data, offset, _len, 0, _len.length);
                offset += _len.length;

                String ss = new String(_len);

                if (ss.equals("INIT_SESSION_OK"))
                {
                    _len = ByteBuffer.allocate(4).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    ByteBuffer buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    int uid = buffer.getInt();

                    accout_info.uid = uid;

                    _len = ByteBuffer.allocate(4).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    int time_last_login = buffer.getInt();

                    accout_info.time_last_login = time_last_login;

                    _len = ByteBuffer.allocate(4).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    final int count_online = buffer.getInt();


                    /*_len = ByteBuffer.allocate(2).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    cost_p1 = buffer.getShort();*/


                    _len = ByteBuffer.allocate(1).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                    int count_options = (int) _len[0] & 0xff;

                    for (int i = 0; i < count_options; i++)
                    {
                        _len = ByteBuffer.allocate(2).array();

                        System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                        buffer = ByteBuffer.wrap(_len);
                        buffer.order(ByteOrder.BIG_ENDIAN);

                        int type = buffer.getShort();

                        System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                        buffer = ByteBuffer.wrap(_len);
                        buffer.order(ByteOrder.BIG_ENDIAN);

                        int value = buffer.getShort();


                        if (type == AppSettings.OPT_MY_GENDER)
                        {
                            app_setting.my_gender = value;
                        }
                        else if (type == AppSettings.OPT_TYPE_SUBSCRIPTION)
                        {
                            app_setting.type_subscription = value;
                        }
                        else if (type == AppSettings.OPT_VIBRO)
                        {
                            app_setting.vibro = value == 1;
                        }
                        else if (type == AppSettings.OPT_SOUND)
                        {
                            app_setting.sound = value == 1;
                        }
                    }

                    //******************************************************************************
                    last_count_online = count_online;

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            show_page_network_menu();

                            /*if( app_setting.my_gender == AppSettings.OPT_VALUE_NOT_SET )
                            {
                                show_page_choose_gender();
                            }
                            else if( app_setting.my_gender == AppSettings.OPT_VALUE_GENDER__FEMALE )
                            {
                                show_page_main();
                            }
                            else if( app_setting.my_gender == AppSettings.OPT_VALUE_GENDER__MALE )
                            {
                                if( app_setting.type_subscription == AppSettings.OPT_VALUE_NOT_SET )
                                {
                                    show_page_type_subscribe();
                                }
                                else
                                {
                                    show_page_main();
                                }
                            }*/


                            /*TextView tv = findViewById(R.id.tv_page_menu_online_count);
                            if (tv != null)
                            {
                                tv.setText("ONLINE: " + String.valueOf(count_online));
                            }*/
                        }
                    });
                }
                else
                {
                    //app_setting.db.setKeyValue("init_ok", "0");
                    //connection_server();
                }
            }
        }
        else if (r.cmd.equalsIgnoreCase("SAVE_MY_OPTIONS"))
        {
            if (r.data != null)
            {
                int offset = 0;
                byte[] _len = ByteBuffer.allocate(4).array();
                System.arraycopy(r.data, offset, _len, 0, _len.length); offset += _len.length;

                ByteBuffer buffer = ByteBuffer.wrap(_len);
                buffer.order(ByteOrder.BIG_ENDIAN);

                int callback_save_opt = buffer.getInt();

                if(callback_save_opt == 1024)
                {
                    /*if (sv_page == SV_PAGE_YOU_GENDER)
                    {
                        if (app_setting.my_gender == AppSettings.OPT_VALUE_GENDER__FEMALE)
                        {

                        }
                        else if (app_setting.my_gender == AppSettings.OPT_VALUE_GENDER__MALE)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    show_page_type_subscribe();
                                }
                            });
                        }
                    }*/
                }
                else if( callback_save_opt == 1025 )
                {
                    /*if (sv_page == SV_PAGE_TYPE_SUBSCIBE)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                show_page_main();
                            }
                        });
                    }*/
                }
            }
        }
        else if (r.cmd.equalsIgnoreCase("LOAD_TABLE_RATING"))
        {
            if (r.data != null)
            {
                int offset = 0;
                byte[] _len = ByteBuffer.allocate(1).array();

                System.arraycopy(r.data, offset, _len, 0, _len.length);
                offset += _len.length;

                int count_users = _len[0];

                list_rating.clear();

                for (int i = 0; i < count_users; i++)
                {
                    _len = ByteBuffer.allocate(4).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    ByteBuffer buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    final int uid = buffer.getInt();

                    //----------------------------------------------------
                    _len = ByteBuffer.allocate(1).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    int count_read = (int) _len[0] & 0xff;
                    _len = ByteBuffer.allocate(count_read).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    String picture = new String(_len);
                    //----------------------------------------------------
                    _len = ByteBuffer.allocate(1).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    count_read = (int) _len[0] & 0xff;
                    _len = ByteBuffer.allocate(count_read).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    String first_name = new String(_len);
                    //----------------------------------------------------
                    _len = ByteBuffer.allocate(1).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    count_read = (int) _len[0] & 0xff;
                    _len = ByteBuffer.allocate(count_read).array();
                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    String last_name = new String(_len);

                    //----------------------------------------------------
                    _len = ByteBuffer.allocate(4).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    int count_games = buffer.getInt();

                    //----------------------------------------------------
                    _len = ByteBuffer.allocate(4).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    int count_wins = buffer.getInt();

                    //----------------------------------------------------
                    _len = ByteBuffer.allocate(4).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    int count_defeats = buffer.getInt();

                    //----------------------------------------------------
                    _len = ByteBuffer.allocate(4).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    int count_draw = buffer.getInt();
                    //----------------------------------------------------

                    _len = ByteBuffer.allocate(4).array();

                    System.arraycopy(r.data, offset, _len, 0, _len.length);
                    offset += _len.length;

                    buffer = ByteBuffer.wrap(_len);
                    buffer.order(ByteOrder.BIG_ENDIAN);

                    int count_mars = buffer.getInt();
                    //----------------------------------------------------

                    UiItemRating item = new UiItemRating();
                    item.id = uid;
                    item.first_name = first_name;
                    item.last_name = last_name;
                    item.puctire = picture;
                    item.count_wins = count_wins;
                    item.count_defeats = count_defeats;
                    item.count_draw = count_draw;
                    item.count_games = count_games;
                    item.count_mars  = count_mars;

                    list_rating.add(item);
                }

                network.queue_network.add("CHECK_VERSION"
                        , null
                        , null
                        , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                        , QueueNetwork.TYPE_SEND__FORCE);

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ui_draw_list_rating(list_rating);
                    }
                });
            }
        }

        return true;
    }
    //----------------------------------------------------------------------------------------

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result != null && result.isSuccess())
        {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            accout_info.personName = acct.getDisplayName();
            accout_info.personEmail = acct.getEmail();
            accout_info.personId = acct.getId();

            accout_info.fisrtName = acct.getGivenName();
            accout_info.lastName = acct.getFamilyName();

            idToken = acct.getIdToken();

            if (acct.getPhotoUrl() == null)
            {
                accout_info.personPhoto = "";//BitmapFactory.decodeResource( getResources(), R.drawable.default_avatar );
                //show_page_menu();
            }
            else
            {
                String url = acct.getPhotoUrl().toString();
                accout_info.personPhoto = url;
            }

            //app_setting.saveAccountInfo(accout_info);

            //connection_server();
            //show_page_connection_server();

            ArrayList<String> params = new ArrayList<String>();

            params.add(accout_info.personId);
            params.add(idToken);
            params.add(String.valueOf(session_info.session_socket_id));
            params.add(Locale.getDefault().getLanguage());

            network.queue_network.add("SERVER_INIT_SESSION"
                    , params
                    , null
                    , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                    , QueueNetwork.TYPE_SEND__LONG);
        }
        else
        {
            if (result.getStatus().getStatusCode() == CommonStatusCodes.NETWORK_ERROR)
            {

            }
            else if (result.getStatus().getStatusCode() == 12501)
            {
                show_page_main_menu();
            }
            else if (result.getStatus().getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED)
            {
                //signIn();
                //show_page_reg();

                //show_page_reg();

                if (result.getSignInAccount() == null)
                {
                    show_page_auth();
                }
            }
            else
            {
                Log.d("TAG", "result.getStatus():" + result.getStatus());

                show_page_auth();
            }
            /*else
            {
                //authGoogle();
                signIn();
            }*/

            //CommonStatusCodes.DEVELOPER_ERROR

            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        switch (requestCode)
        {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    // Get the device MAC address
                    /*String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);*/
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat();

                    RadioGroup rg = (RadioGroup) findViewById(R.id.rg_page_main_menu_select_color_game);

                    int radioButtonID = rg.getCheckedRadioButtonId();
                    View radioButton = rg.findViewById(radioButtonID);
                    int idx = rg.indexOfChild(radioButton);

                    if( idx == 0 )
                    {
                        show_page_game_two_players_bluetooth_network( MODE_GAME__BLUETOOTH_TWO_PLAYERS, MySurfaceView.MODE_COLOR_WHITE, null, null, null );
                    }
                    else
                    {
                        show_page_game_two_players_bluetooth_network( MODE_GAME__BLUETOOTH_TWO_PLAYERS, MySurfaceView.MODE_COLOR_BLACK, null, null, null );
                    }

                    // Initialize the BluetoothService to perform bluetooth connections
                    _blueToothInfo.mChatService = new BluetoothService(this, mHandler);

                    _blueToothInfo.mChatService.start();
                }
                else
                {
                    String title = getString(R.string.txt14);
                    String yes   = getString(R.string.txt13);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
                    alertDialogBuilder.setTitle( title );

                    alertDialogBuilder
                            .setMessage("")
                            .setCancelable(false)
                            .setPositiveButton(yes, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,int id)
                                {

                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                break;

            case REQUEST_ENABLE_BT_2:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {
                    start_search_bluetooth_devices();
                }
                else
                {
                    String title = getString(R.string.txt14);
                    String yes   = getString(R.string.txt13);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MainActivity.this );
                    alertDialogBuilder.setTitle( title );

                    alertDialogBuilder
                            .setMessage("")
                            .setCancelable(false)
                            .setPositiveButton(yes, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog,int id)
                                {

                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            FrameLayout fl = null;

            fl = (FrameLayout) findViewById(R.id.fl_page_main_menu_bluetooth_game);
            if(fl != null && fl.getVisibility() == View.VISIBLE)
            {
                fl.setVisibility(View.GONE);
                return false;
            }

            fl = (FrameLayout) findViewById(R.id.fl_page_main_menu_play_with_computer);
            if(fl != null && fl.getVisibility() == View.VISIBLE)
            {
                fl.setVisibility(View.GONE);
                return false;
            }

            fl = (FrameLayout) findViewById(R.id.fl_page_main_menu_bluetooth_search_devices);
            if(fl != null && fl.getVisibility() == View.VISIBLE)
            {
                fl.setVisibility(View.GONE);
                return false;
            }

            fl = (FrameLayout) findViewById(R.id.fl_table_rating_user_info);
            if (fl != null && fl.getVisibility() == View.VISIBLE)
            {
                fl.setVisibility(View.GONE);
                return false;
            }

            if(sv_page == SV_PAGE_NETWORK_SEARCH || sv_page == SV_PAGE_NETWORK_RAITING)
            {
                show_page_network_menu();
                return false;
            }

            if(sv_page == SV_PAGE_PRIVACY_POLICY)
            {
                show_page_auth();
                return false;
            }

            if( sv_page == SV_PAGE_GAME && _game != null && _game.mode_game == MODE_GAME__NETWORK )
            {
                String title = getString(R.string.txt_57);
                String yes   = getString(R.string.txt_58);
                String no    = getString(R.string.txt_59);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme));
                alertDialogBuilder.setTitle(title);

                alertDialogBuilder
                        //.setMessage(descr)
                        .setCancelable(false)
                        .setPositiveButton(yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                ArrayList<String> params = new ArrayList<String>();

                                params.add(String.valueOf(accout_info.uid));
                                params.add(String.valueOf(session_info.session_socket_id));

                                network.queue_network.add("STOP_WAIT_GAME"
                                        , params
                                        , null
                                        , ClassNetWork.GAME_NET_WORK__OUTPUT_PRIORITY_MEDIUM
                                        , QueueNetwork.TYPE_SEND__LONG);

                                show_page_network_menu();
                            }
                        })
                        .setNegativeButton(no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                ;

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }

            if(
                    sv_page == SV_PAGE_GAME
                 || sv_page == SV_PAGE_INFO
                 || sv_page == SV_PAGE_AUTH
                 || sv_page == SV_PAGE_NETWORK_MENU
            )
            {
                show_page_main_menu();
                return false;
            }


            android.os.Process.killProcess(android.os.Process.myPid());
        }

        return super.onKeyDown(keyCode, event);
    }


}
