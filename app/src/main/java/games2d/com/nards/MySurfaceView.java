package games2d.com.nards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int SELECT_TYPE_FIGURE_DISABLE_SELECT = 0x00;
    public static final int SELECT_TYPE_FIGURE_ONLY_WHITE = 0x01;
    public static final int SELECT_TYPE_FIGURE_ONLY_BLACK = 0x02;
    public static final int SELECT_TYPE_FIGURE_WHITE_AND_BLACK = 0x03;
    public static final int SELECT_TYPE_FIGURE_ONLY_NONE = 0x04;
    public static final int SELECT_TYPE_FIGURE_WHITE_OR_NONE = 0x05;
    public static final int SELECT_TYPE_FIGURE_BLACK_OR_NONE = 0x06;

    public static final int XOD_WHITE = 0x110;
    public static final int XOD_BLACK = 0x112;

    public static final int DESIGN_BOADR_TYPE_1 = 0x10;
    public static final int DESIGN_BOADR_TYPE_2 = 0x11;

    public static final int FIGIRE_TYPE_1 = 0x1;
    public static final int FIGIRE_TYPE_2 = 0x2;

    public static final int PLAN_TYPE_PATH_1 = 0x1;
    public static final int PLAN_TYPE_PATH_2 = 0x2;
    public static final int PLAN_TYPE_PATH_3 = 0x3;
    public static final int PLAN_TYPE_PATH_4 = 0x4;
    public static final int PLAN_TYPE_PATH_11 = 0x11;
    public static final int PLAN_TYPE_PATH_22 = 0x22;
    // -- TOUCH
    public static final int PLAN_TYPE_PATH_33 = 0x33;
    public static final int PLAN_TYPE_PATH_44 = 0x44;
    public static final int MODE_COLOR_WHITE = 0x15;
    public static final int MODE_COLOR_BLACK = 0x16;
    public static final int FIGURE_COLOR_NONE = 0x09;
    public static final int FIGURE_COLOR_WHITE = 0x10;
    public static final int FIGURE_COLOR_BLACK = 0x12;

    public static final int LOCAL_POS__LEFT_TOP = 0x1;
    public static final int LOCAL_POS__LEFT_BOTTOM = 0x2;
    public static final int LOCAL_POS__RIGHT_TOP = 0x3;
    // ---------------------------------------------------------------------------------
    public static final int LOCAL_POS__RIGHT_BOTTOM = 0x4;
    // ++ TOUCH
    private static final long pressedTimeTreshold = 700;    //Если касание длилось меньше 700 мс, то курсор будет выведен ??
    public int mode_select_figure = SELECT_TYPE_FIGURE_DISABLE_SELECT;
    public int current_xod = XOD_WHITE;
    public List<CellValues> _cells = new ArrayList<>();
    public List<PlanInfo> slot_animation_dice = new ArrayList<>();
    public boolean action_animation_dice = false;
    public boolean action_animation_fly_figure = false;
    public int selected_cell = 0;
    public List<Integer> selected_points = new ArrayList<>();
    public List<Integer> selected_points_end_xods_found = new ArrayList<>();
    public List<Integer> disable_select_pos_cell = new ArrayList<>();
    public List<Integer> selected_alternative_xods = new ArrayList<>();
    public List<Integer> selected_alternative_xods_red = new ArrayList<>();
    Paint paint_select_point = new Paint();
    Paint paint_select_point2 = new Paint();
    Paint paint_select_point3 = new Paint();
    Paint paint_select_point4 = new Paint();
    Paint paint_black = new Paint();
    Paint g_paint_1 = new Paint();
    Paint g_paint_2 = new Paint();
    Paint g_paint_3 = new Paint();
    private float lastX;
    private float lastY; // координаты последнего события
    private float _screen_offset_w = 0;
    private float _screen_offset_h = 100;
    private float _last_screen_offset_w = 0;
    private float _last_screen_offset_h = 0;
    private float distance = 0; // Расстояние между пальцами при зуме
    private boolean firstMulti = true;   // Показывает, произошло ли multitouch событие при данном вызове функции или еще при предыдущем
    private boolean pressed = false;  // Показывает, а был ли пальчик
    private boolean zoomTrans = false;  // Транзакция зуммирования (во избежание перемещения сразу после масштабирования, когда пальцы
    // отрываешь)
    private boolean canMove = false;
    private boolean _stop_draw = false;
    private DrawManager drawLoopThread = null;
    private SurfaceHolder holder;
    private int w_canvas = 0;
    private int w2_canvas = 0;
    private int h_canvas = 0;
    private int h2_canvas = 0;
    private Object synch_create_fly_figure = new Object();
    private Object synch_create_fly_figure_out_board = new Object();
    private boolean pause = false;
    private int design_boadr_type = DESIGN_BOADR_TYPE_2;
    private int figure_type = FIGIRE_TYPE_1;
    private float _scale_px = 0;
    private float scale_in_pr = 0; // процент
    private boolean is_first_draw = true;
    private float scale = 1;
    private int width_open_part_board = 300;
    private int padding_out = 5;
    private int padding_top = 30;
    private int padding_bottom = 0;
    private int padding_center = 1;
    private int padding_inner = 7;
    private int padding_inner_2 = 8;
    private int offset_x = 0;
    private int offset_y = 0;
    private int step_cells = 3;
    private int step_cells_2 = 3;
    private String text_print_top_left = "";
    private Bitmap top_left_image_icon = null;
    private PointPP left_center_board;
    private PointPP right_center_board;
    private PointPP left_top_board;
    private PointPP left_bottom_board;
    private PointPP right_top_board;
    private PointPP right_bottom_board;
    private int peak_size = 250;
    private int step_x = 0;
    private int step_x_2 = 0;
    private float radius_1 = 0;
    private float radius2_1 = 0;
    private int mode_color_board = 0;
    private Bitmap _bk_2 = null;
    private Bitmap _select_figure = null;
    private Bitmap _select_alternative_xod = null;
    private Bitmap _select_alternative_xod_red = null;
    private Bitmap _figure_white = null;
    private Bitmap _figure_black = null;
    private Bitmap _bk = null;
    private Bitmap _c1_1 = null;
    private Bitmap _c1_2 = null;
    private Bitmap _c1_3 = null;
    private Bitmap _c1_4 = null;
    private Bitmap _c1_5 = null;
    private Bitmap _c1_6 = null;
    private Bitmap _c1_7 = null;
    private Bitmap _c1_8 = null;
    private Bitmap _c1_9 = null;
    private Bitmap _c1_10 = null;
    private Bitmap _c1_11 = null;
    private Bitmap _c1_12 = null;
    private Bitmap _c1_13 = null;
    private Bitmap _c1_14 = null;
    private Bitmap _c1_15 = null;
    private Bitmap _c1_16 = null;
    private Bitmap _c1_17 = null;
    private Bitmap _c1_18 = null;
    private Bitmap _c1_v2_1 = null;
    private Bitmap _c1_v2_2 = null;
    private Bitmap _c1_v2_3 = null;
    private Bitmap _c1_v6_1 = null;
    private Bitmap _c1_v6_2 = null;
    private Bitmap _c1_v6_3 = null;
    private Bitmap _c1_v6_4 = null;
    private Bitmap _c1_v3_1 = null;
    private Bitmap _c1_v3_2 = null;
    private Bitmap _c1_v5_1 = null;
    private Bitmap _c1_v5_2 = null;
    private Bitmap _c1_v5_3 = null;
    private Bitmap _c1_v5_4 = null;
    private Bitmap _c1_v1_1 = null;
    private Bitmap _c1_v1_2 = null;
    private Bitmap _c1_v1_3 = null;
    private Bitmap _c1_v4_1 = null;
    private Bitmap _c1_v4_2 = null;
    private Bitmap _c1_v4_3 = null;
    private Bitmap _c2_1 = null;
    private Bitmap _c2_2 = null;
    private Bitmap _c2_3 = null;
    private Bitmap _c2_4 = null;
    private Bitmap _c2_5 = null;
    private Bitmap _c2_6 = null;
    private Bitmap _c2_7 = null;
    private Bitmap _c2_8 = null;
    private Bitmap _c2_9 = null;
    private Bitmap _c2_10 = null;
    private Bitmap _c2_11 = null;
    private Bitmap _c2_12 = null;
    private Bitmap _c2_13 = null;
    private Bitmap _c2_14 = null;
    private Bitmap _c2_15 = null;
    private Bitmap _c2_16 = null;
    private Bitmap _c2_17 = null;
    private Bitmap _c2_18 = null;
    private Bitmap _c2_v1_1 = null;
    private Bitmap _c2_v1_2 = null;
    private Bitmap _c2_v1_3 = null;
    private Bitmap _c2_v1_4 = null;
    private Bitmap _c2_v2_1 = null;
    private Bitmap _c2_v2_2 = null;
    private Bitmap _c2_v2_3 = null;
    private Bitmap _c2_v2_4 = null;
    private Bitmap _c2_v3_1 = null;
    private Bitmap _c2_v3_2 = null;
    private Bitmap _c2_v4_1 = null;
    private Bitmap _c2_v4_2 = null;
    private Bitmap _c2_v4_3 = null;
    private Bitmap _c2_v5_1 = null;
    private Bitmap _c2_v5_2 = null;
    private Bitmap _c2_v5_3 = null;
    private Bitmap _c2_v5_4 = null;
    private Bitmap _c2_v6_1 = null;

    ///--------------------------------------------------------------------
    private Bitmap _c2_v6_2 = null;
    private Bitmap _c2_v6_3 = null;

    ///--------------------------------------------------------------------
    private Bitmap _img_left_exit_figure = null;
    private Bitmap _img_right_exit_figure = null;

    ///--------------------------------------------------------------------
    private Bitmap _img_left_exit_figure_select = null;
    private Bitmap _img_right_exit_figure_select = null;
    private PlanAnimationFigure fly_figure = null;
    private PlanAnimationFigureOutBoard fly_figure_out_board = null;
    private Typeface _fontApp;
    private int h2 = 0;
    private long ts_animation_select = 0;
    private boolean flag_draw = false;
    private Paint paint_text_clock_white = new Paint();
    private Rect bound_clock_white = new Rect();
    private Paint paint_selected_cell_1 = new Paint();
    private Paint paint_selected_cell_2 = new Paint();
    private Paint paint_text_print_top_left = new Paint();
    private Rect bound_paint_text_print_top_left = new Rect();
    private Paint paint_text_count_figures_1 = new Paint();
    private Paint paint_text_count_figures_2 = new Paint();
    private Paint paint_text_count_figures_1_c = new Paint();
    private Paint paint_text_count_figures_2_c = new Paint();
    private Rect bound_count_figures = new Rect();
    private List<CImageButton> list_buttons = new ArrayList<>();

    private boolean needRebuildBackground = true;
    private Bitmap background_big_img = null;


    public MySurfaceView(Context context, int _mode_color_board, int _figure_type)
    {
        super(context);

        //_fontApp = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
        _fontApp = Typeface.createFromAsset(getContext().getAssets(), "fonts/Monitorca-Bd.ttf");
        mode_color_board = _mode_color_board;

        figure_type = _figure_type;

        holder = getHolder();
        holder.addCallback(this);
        _scale_px = ((MainActivity) getContext()).get_scale_px();

        width_open_part_board *= _scale_px;
        padding_out *= _scale_px;
        padding_center *= _scale_px;
        padding_inner *= _scale_px;
        padding_inner_2 *= _scale_px;
        padding_top *= _scale_px;
        padding_bottom *= _scale_px;
        peak_size *= _scale_px;

        offset_x *= _scale_px;
        offset_y *= _scale_px;

        paint_text_clock_white.setTypeface(_fontApp);
        paint_text_clock_white.setTextSize(8 * _scale_px);
        paint_text_clock_white.setColor(Color.parseColor("#ff902d"));

        paint_selected_cell_1.setStyle(Paint.Style.STROKE);
        paint_selected_cell_1.setColor(Color.parseColor("#ffffff"));
        paint_selected_cell_1.setStrokeWidth(1 * _scale_px);

        paint_selected_cell_2.setStyle(Paint.Style.STROKE);
        paint_selected_cell_2.setColor(Color.parseColor("#cccccc"));
        paint_selected_cell_2.setStrokeWidth(1 * _scale_px);

        //------------------------------------------------------------

        paint_text_count_figures_1.setStyle(Paint.Style.FILL);
        paint_text_count_figures_1.setTextSize(13 * _scale_px);
        paint_text_count_figures_1.setColor(Color.parseColor("#000000"));
        paint_text_count_figures_1.setTypeface(_fontApp);
        paint_text_count_figures_1.setAntiAlias(true);

        paint_text_count_figures_2.setStyle(Paint.Style.STROKE);
        paint_text_count_figures_2.setTextSize(13 * _scale_px);
        paint_text_count_figures_2.setStrokeWidth(2.0f * _scale_px);
        paint_text_count_figures_2.setColor(Color.parseColor("#ffffff"));
        paint_text_count_figures_2.setTypeface(_fontApp);
        paint_text_count_figures_2.setAntiAlias(true);


        paint_text_count_figures_1_c.setStyle(Paint.Style.FILL);
        paint_text_count_figures_1_c.setTextSize(13 * _scale_px);
        paint_text_count_figures_1_c.setColor(Color.parseColor("#000000"));
        paint_text_count_figures_1_c.setTypeface(_fontApp);

        paint_text_count_figures_2_c.setStyle(Paint.Style.STROKE);
        paint_text_count_figures_2_c.setTextSize(13 * _scale_px);
        paint_text_count_figures_2_c.setStrokeWidth(2.0f * _scale_px);
        paint_text_count_figures_2_c.setColor(Color.parseColor("#eeeeee"));
        paint_text_count_figures_2_c.setTypeface(_fontApp);


        paint_select_point.setStyle(Paint.Style.FILL);
        paint_select_point.setColor(Color.parseColor("#0cae49"));
        paint_select_point.setAntiAlias(true);

        paint_select_point2.setStyle(Paint.Style.FILL);
        paint_select_point2.setColor(Color.parseColor("#166ac0"));
        paint_select_point2.setAntiAlias(true);

        paint_select_point3.setStyle(Paint.Style.FILL);
        paint_select_point3.setColor(Color.parseColor("#f4e296"));
        paint_select_point3.setAntiAlias(true);

        paint_select_point4.setStyle(Paint.Style.FILL);
        paint_select_point4.setColor(Color.parseColor("#1d8700"));
        paint_select_point4.setAntiAlias(true);

        paint_black.setStyle(Paint.Style.STROKE);
        paint_black.setColor(Color.parseColor("#000000"));
        paint_black.setStrokeWidth(2);
        paint_black.setAntiAlias(true);

        paint_text_print_top_left.setColor(Color.WHITE);
        paint_text_print_top_left.setTypeface(_fontApp);
        paint_text_print_top_left.setTextSize(18 * _scale_px);
        paint_text_print_top_left.setAntiAlias(true);

        this.drawLoopThread = new DrawManager(this);


    }

    /**
     * @param found_pos 1..24 num
     * @return
     */
    private CellValues get_values_from_pos(int found_pos)
    {
        CellValues result = null;

        for (int i = 0; i < _cells.size(); i++)
        {
            if (_cells.get(i).pos == found_pos)
            {
                result = _cells.get(i);
                break;
            }
        }

        return result;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        this._stop_thread();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        this._run_thread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    private void _stop_thread()
    {
        boolean retry = true;
        this._stop_draw = true;
        this.drawLoopThread.setRunning(false);
        while (retry)
        {
            try
            {
                this.drawLoopThread.join();
                retry = false;
            } catch (InterruptedException e)
            {
            }
        }
    }

    private void _run_thread()
    {
        this._stop_draw = false;
        this.drawLoopThread.setRunning(true);

        if (this.drawLoopThread.getState() == Thread.State.TERMINATED)
        {
            this.drawLoopThread = new DrawManager(this);
            this.drawLoopThread.setRunning(true);
            this.drawLoopThread.start();
        }
        else if (this.drawLoopThread.getState() != Thread.State.RUNNABLE)
        {
            this.drawLoopThread.start();
        }
    }

    //=========================================================================================================
    public boolean onTouchEvent(final MotionEvent event)
    {
        if (pause)
        {
            return true;
        }

        if (event.getPointerCount() >= 2)
        {
            try
            {
                zoomTrans = true;       //Начали транзакцию масштабирования
                PointPP p1 = new PointPP(event.getX(event.getPointerId(0)), event.getY(event.getPointerId(0)));
                PointPP p2 = new PointPP(event.getX(event.getPointerId(1)), event.getY(event.getPointerId(1)));

                if (firstMulti)
                {
                    distance = PointPP.distance(p1, p2);
                    firstMulti = false;
                }
                else
                {
                    float newDistance = PointPP.distance(p1, p2);
                    float deltaZoom = newDistance - distance;
                    float inc_zoom = deltaZoom / 1000;
                    boolean stop_foom = false;

                    distance = newDistance;

                    if ((inc_zoom < 0 && scale_in_pr >= 20)
                            || (inc_zoom > 0 && scale_in_pr <= 170)
                            )
                    {
                        scale += inc_zoom;

                        scale_in_pr = 100 * scale;

                        Log.i("TAG", "scale: " + String.valueOf(scale));
                    }
                }
            } catch (java.lang.IllegalArgumentException ex)
            {
                return false;
            }
        }
        else
        {
            //Log.i("TAG", String.valueOf(firstMulti) );

            firstMulti = true;

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN: // нажатие на экран

                    if (!zoomTrans)
                    {
                        lastX = event.getX();
                        lastY = event.getY();

                        this._last_screen_offset_h = this._screen_offset_h;
                        this._last_screen_offset_w = this._screen_offset_w;

                        this._on_touch_event(event.getX(), event.getY());

                        pressed = true;
                        canMove = true;
                    }

                    if (!zoomTrans && pressed && (event.getDownTime() > pressedTimeTreshold))
                    {
                        float mousePos_x = event.getX() * (1 / scale);
                        float mousePos_y = event.getY() * (1 / scale);


                    }

                    break;

                case MotionEvent.ACTION_MOVE: //движение по экрану

                    if (canMove && !zoomTrans)
                    {
                        this._screen_offset_w = this._last_screen_offset_w + ((event.getX() - lastX) * (1 / scale));
                        this._screen_offset_h = this._last_screen_offset_h + ((event.getY() - lastY) * (1 / scale));
                    }

                    if (event.getDownTime() > pressedTimeTreshold)
                    {
                        pressed = false;
                    }

                    break;

                case MotionEvent.ACTION_UP: // отжатие

                    if (canMove && !zoomTrans)
                    {
                        //renderer.savePos(event.getX() - lastX, event.getY() - lastY);
                    }

                    if (pressed && !zoomTrans)
                    {
                        //renderer.setPoint(event.getX(), event.getY());
                        //setCoordsToTextView(renderer.getGL_X(event.getX()), renderer.getGL_Y(event.getY()));
                    }

                    on_touch_up();

                    pressed = false;
                    canMove = false;
                    zoomTrans = false;

                    break;

                case MotionEvent.ACTION_OUTSIDE:
                    pressed = false;
                    canMove = false;
                    zoomTrans = false;

                    //Log.i("TAG", "Outside");
                    break;

                case MotionEvent.ACTION_CANCEL:
                    pressed = false;
                    canMove = false;
                    zoomTrans = false;
                    break;

                default:
                    return false; // событие не обработано
            }
        }
        return true; // событие обработано
    }

    // ========================================================================================================
    public void on_touch_up()
    {
        for (int k1 = 0; k1 < list_buttons.size(); k1++)
        {
            list_buttons.get(k1).setStatus(CImageButton.STATUS_NONE);
        }
    }

    // ========================================================================================================
    public boolean _on_touch_event(float x, float y)
    {
        //Log.i("TAG", "_on_touch_event x: " + String.valueOf(x) + " y:" + String.valueOf(y));

        if (this.is_first_draw)
        {
            return true;
        }

        if (action_animation_dice || action_animation_fly_figure)
        {
            return true;
        }

        for (int k1 = 0; k1 < list_buttons.size(); k1++)
        {
            if (list_buttons.get(k1).display
                    && list_buttons.get(k1).test_pressed(x, y))
            {
                list_buttons.get(k1).setStatus(CImageButton.STATUS_PRESSED);
                list_buttons.get(k1).event_pressed();
                return true;
            }
            else
            {
                list_buttons.get(k1).setStatus(CImageButton.STATUS_NONE);
            }
        }

        if (mode_select_figure != SELECT_TYPE_FIGURE_DISABLE_SELECT)
        {
            CellValues cv = get_num_selected_cell_x_y_touch((int) x, (int) y);

            if (mode_select_figure == SELECT_TYPE_FIGURE_WHITE_AND_BLACK)
            {
                if (disable_select_pos_cell.indexOf(cv.pos) != -1)
                {
                    return true;
                }

                //selected_cell = cv.pos;

                callback_select_cell(cv);
                return true;
            }
            else if (mode_select_figure == SELECT_TYPE_FIGURE_ONLY_WHITE
                    && cv != null
                    && cv.color_type == FIGURE_COLOR_WHITE)
            {
                if (disable_select_pos_cell.indexOf(cv.pos) != -1)
                {
                    return true;
                }

                //selected_cell = cv.pos;

                callback_select_cell(cv);
                return true;
            }
            else if (mode_select_figure == SELECT_TYPE_FIGURE_ONLY_BLACK
                    && cv != null
                    && cv.color_type == FIGURE_COLOR_BLACK)
            {
                if (disable_select_pos_cell.indexOf(cv.pos) != -1)
                {
                    return true;
                }

                //selected_cell = cv.pos;

                callback_select_cell(cv);
                return true;
            }
            else if (mode_select_figure == SELECT_TYPE_FIGURE_WHITE_OR_NONE
                    && cv != null
                    && (cv.color_type == FIGURE_COLOR_WHITE || cv.color_type == FIGURE_COLOR_NONE))
            {
                if (disable_select_pos_cell.indexOf(cv.pos) != -1)
                {
                    return true;
                }

                //selected_cell = cv.pos;

                callback_select_cell(cv);
            }
            else if (mode_select_figure == SELECT_TYPE_FIGURE_BLACK_OR_NONE
                    && cv != null
                    && (cv.color_type == FIGURE_COLOR_BLACK || cv.color_type == FIGURE_COLOR_NONE))
            {
                if (disable_select_pos_cell.indexOf(cv.pos) != -1)
                {
                    return true;
                }

                //selected_cell = cv.pos;

                callback_select_cell(cv);
                return true;
            }
            else if (mode_select_figure == SELECT_TYPE_FIGURE_ONLY_NONE
                    && cv != null
                    && cv.color_type == FIGURE_COLOR_NONE)
            {
                if (disable_select_pos_cell.indexOf(cv.pos) != -1)
                {
                    return true;
                }

                //selected_cell = cv.pos;

                callback_select_cell(cv);
                return true;
            }
            else
            {
                callback_no_select();
            }
        }

        return true;
    }

    public void disable_select_pos_cell_add(Integer v)
    {
        this.disable_select_pos_cell.add(v);
    }

    public void disable_select_pos_cell_clear()
    {
        this.disable_select_pos_cell.clear();
    }

    private List<Integer> get_scale_image_size(int img_w, int img_h, int max_w, int max_h)
    {
        List<Integer> result = new ArrayList<Integer>();
        Integer h = img_h, w = img_w;

        if (w > max_w)
        {
            h = (int) (max_w * h / (float) w);
            w = max_w;
        }

        if (h > max_h)
        {
            w = (int) (max_h * w / (float) h);
            h = max_h;
        }

        result.add(w);
        result.add(h);

        return result;
    }

    private PointPP get_perpendicular_vector(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        // first convert line to normalized unit vector
        double dx = x2 - x1;
        double dy = y2 - y1;
        double mag = Math.sqrt(dx * dx + dy * dy);
        dx /= mag;
        dy /= mag;

        return new PointPP((float) (dy), (float) (-dx));

        // translate the point and get the dot product
        /*double lambda = (dx * (x3 - x1)) + (dy * (y3 - y1));
        return new PointPP( (float)( (dx * lambda) + x1 ), (float)( (dy * lambda) + y1) );*/
    }

    public void set_text_print_top_left(String v)
    {
        text_print_top_left = v;
    }

    public void set_icon_print_top_left(Bitmap img)
    {
        top_left_image_icon = img;
    }

    private PointPP get_center_point(double x1, double y1, double x2, double y2)
    {
        return new PointPP((float) ((x1 + x2) / 2f), (float) ((y1 + y2) / 2f));
    }

    public void OnDestroy()
    {
        this.drawLoopThread.setRunning(false);
    }

    private List<PointPP> generateBezierPath(PointPP origin, PointPP destination, PointPP control1, PointPP control2, int segments)
    {
        ArrayList<PointPP> pointsForReturn = new ArrayList<PointPP>();

        float t = 0;
        for (int i = 0; i < segments; i++)
        {
            PointPP p = new PointPP((float) (Math.pow(1 - t, 3) * origin.getx() + 3.0f * Math.pow(1 - t, 2) * t * control1.getx() + 3.0f *
                    (1 - t) * t * t
                    * control2.getx() + t * t * t * destination.getx())
                    , (float) (Math.pow(1 - t, 3) * origin.gety() + 3.0f * Math.pow(1 - t, 2) * t * control1.gety() + 3.0f * (1 - t) * t * t * control2.gety() + t * t *
                    t * destination.gety()));
            t += 1.0f / segments;
            pointsForReturn.add(p);
        }
        pointsForReturn.add(destination);
        return pointsForReturn;
    }

    public void addPlanAnimationInSlot(PlanInfo add)
    {
        this.slot_animation_dice.add(add);

        action_animation_dice = true;
    }

    public void clearPlanAnimationSlot()
    {
        this.slot_animation_dice.clear();
    }
    //=========================================================================================================

    public PlanInfo create_plan_and_path_draw(int need_num, long id, long time_animation, int type_path)
    {
        PlanInfo result = null;

        if (need_num >= 1 && need_num <= 6)
        {
            result = new PlanInfo();

            result._plan_draw = new ArrayList<>();

            if (need_num == 1)
            {
                if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_1)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_11)
                        )
                {
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_2);
                    result._plan_draw.add(_c1_3);
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_v1_1);
                    result._plan_draw.add(_c1_v1_2);
                    result._plan_draw.add(_c1_v1_3);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_2)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_22)
                        )
                {
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_2);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_v1_1);
                    result._plan_draw.add(_c2_v1_2);
                    result._plan_draw.add(_c2_v1_3);
                    result._plan_draw.add(_c2_v1_4);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_3)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_33)
                        )
                {
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_2);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_v1_1);
                    result._plan_draw.add(_c2_v1_2);
                    result._plan_draw.add(_c2_v1_3);
                    result._plan_draw.add(_c2_v1_4);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_4)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_44)
                        )
                {
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_3);
                    result._plan_draw.add(_c1_2);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_v1_1);
                    result._plan_draw.add(_c1_v1_2);
                    result._plan_draw.add(_c1_v1_3);
                }


            }
            else if (need_num == 2)
            {
                if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_1)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_11)
                        )
                {
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_9);
                    result._plan_draw.add(_c1_10);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_v2_1);
                    result._plan_draw.add(_c1_v2_2);
                    result._plan_draw.add(_c1_v2_3);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_2)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_22)
                        )
                {
                    /*result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_2);*/
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_9);
                    result._plan_draw.add(_c2_10);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_v2_1);
                    result._plan_draw.add(_c2_v2_2);
                    result._plan_draw.add(_c2_v2_3);
                    result._plan_draw.add(_c2_v2_4);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_3)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_33)
                        )
                {
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_10);
                    result._plan_draw.add(_c2_9);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_v2_1);
                    result._plan_draw.add(_c2_v2_2);
                    result._plan_draw.add(_c2_v2_3);
                    result._plan_draw.add(_c2_v2_4);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_4)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_44)
                        )
                {
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_10);
                    result._plan_draw.add(_c1_9);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_v2_1);
                    result._plan_draw.add(_c1_v2_2);
                    result._plan_draw.add(_c1_v2_3);
                }

            }
            else if (need_num == 3)
            {
                /*_plan_draw.add(_c1_1);
                _plan_draw.add(_c1_2);
                _plan_draw.add(_c1_3);
                _plan_draw.add(_c1_4);
                _plan_draw.add(_c1_5);*/

                if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_1)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_11)
                        )
                {
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_9);
                    result._plan_draw.add(_c1_10);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_2);
                    result._plan_draw.add(_c1_3);
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_v3_1);
                    result._plan_draw.add(_c1_v3_2);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_2)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_22)
                        )
                {
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_9);
                    result._plan_draw.add(_c2_10);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_2);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_v3_1);
                    result._plan_draw.add(_c2_v3_2);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_3)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_33)
                        )
                {
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_2);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_10);
                    result._plan_draw.add(_c2_9);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_v3_1);
                    result._plan_draw.add(_c2_v3_2);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_4)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_44)
                        )
                {
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_3);
                    result._plan_draw.add(_c1_2);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_10);
                    result._plan_draw.add(_c1_9);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_v3_1);
                    result._plan_draw.add(_c1_v3_2);
                }

            }
            else if (need_num == 4)
            {
                if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_1)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_11)
                        )
                {
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_2);
                    result._plan_draw.add(_c1_3);
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_9);
                    result._plan_draw.add(_c1_10);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_v4_1);
                    result._plan_draw.add(_c1_v4_2);
                    result._plan_draw.add(_c1_v4_3);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_2)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_22)
                        )
                {
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_2);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_9);
                    result._plan_draw.add(_c2_10);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_v4_1);
                    result._plan_draw.add(_c2_v4_2);
                    result._plan_draw.add(_c2_v4_3);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_3)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_33)
                        )
                {
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_10);
                    result._plan_draw.add(_c2_9);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_2);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_v4_1);
                    result._plan_draw.add(_c2_v4_2);
                    result._plan_draw.add(_c2_v4_3);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_4)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_44)
                        )
                {
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_10);
                    result._plan_draw.add(_c1_9);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_3);
                    result._plan_draw.add(_c1_2);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_v4_1);
                    result._plan_draw.add(_c1_v4_2);
                    result._plan_draw.add(_c1_v4_3);
                }

            }
            else if (need_num == 5)
            {
                if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_1)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_11)
                        )
                {
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_2);
                    result._plan_draw.add(_c1_3);
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_v5_1);
                    result._plan_draw.add(_c1_v5_2);
                    result._plan_draw.add(_c1_v5_3);
                    result._plan_draw.add(_c1_v5_4);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_2)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_22)
                        )
                {
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_2);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_v5_1);
                    result._plan_draw.add(_c2_v5_2);
                    result._plan_draw.add(_c2_v5_3);
                    result._plan_draw.add(_c2_v5_4);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_3)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_33)
                        )
                {
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_3);
                    result._plan_draw.add(_c2_2);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_v5_1);
                    result._plan_draw.add(_c2_v5_2);
                    result._plan_draw.add(_c2_v5_3);
                    result._plan_draw.add(_c2_v5_4);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_4)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_44)
                        )
                {
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_4);
                    result._plan_draw.add(_c1_3);
                    result._plan_draw.add(_c1_2);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_v5_1);
                    result._plan_draw.add(_c1_v5_2);
                    result._plan_draw.add(_c1_v5_3);
                    result._plan_draw.add(_c1_v5_4);
                }

            }
            else if (need_num == 6)
            {
                /*_plan_draw.add(_c1_1);
                _plan_draw.add(_c1_2);
                _plan_draw.add(_c1_3);
                _plan_draw.add(_c1_4);*/

                if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_1)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_11)
                        )
                {
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_9);
                    result._plan_draw.add(_c1_10);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_v6_1);
                    result._plan_draw.add(_c1_v6_2);
                    result._plan_draw.add(_c1_v6_3);
                    result._plan_draw.add(_c1_v6_4);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_2)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_22)
                        )
                {
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_9);
                    result._plan_draw.add(_c2_10);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_1);
                    result._plan_draw.add(_c2_v6_1);
                    result._plan_draw.add(_c2_v6_2);
                    result._plan_draw.add(_c2_v6_3);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_3)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_33)
                        )
                {
                    result._plan_draw.add(_c2_18);
                    result._plan_draw.add(_c2_17);
                    result._plan_draw.add(_c2_16);
                    result._plan_draw.add(_c2_15);
                    result._plan_draw.add(_c2_14);
                    result._plan_draw.add(_c2_13);
                    result._plan_draw.add(_c2_12);
                    result._plan_draw.add(_c2_11);
                    result._plan_draw.add(_c2_10);
                    result._plan_draw.add(_c2_9);
                    result._plan_draw.add(_c2_8);
                    result._plan_draw.add(_c2_7);
                    result._plan_draw.add(_c2_6);
                    result._plan_draw.add(_c2_5);
                    result._plan_draw.add(_c2_4);
                    result._plan_draw.add(_c2_v6_1);
                    result._plan_draw.add(_c2_v6_2);
                    result._plan_draw.add(_c2_v6_3);
                }
                else if (
                        (type_path == MySurfaceView.PLAN_TYPE_PATH_4)
                                || (type_path == MySurfaceView.PLAN_TYPE_PATH_44)
                        )
                {
                    result._plan_draw.add(_c1_1);
                    result._plan_draw.add(_c1_18);
                    result._plan_draw.add(_c1_17);
                    result._plan_draw.add(_c1_16);
                    result._plan_draw.add(_c1_15);
                    result._plan_draw.add(_c1_14);
                    result._plan_draw.add(_c1_13);
                    result._plan_draw.add(_c1_12);
                    result._plan_draw.add(_c1_11);
                    result._plan_draw.add(_c1_10);
                    result._plan_draw.add(_c1_9);
                    result._plan_draw.add(_c1_8);
                    result._plan_draw.add(_c1_7);
                    result._plan_draw.add(_c1_6);
                    result._plan_draw.add(_c1_5);
                    result._plan_draw.add(_c1_v6_1);
                    result._plan_draw.add(_c1_v6_2);
                    result._plan_draw.add(_c1_v6_3);
                    result._plan_draw.add(_c1_v6_4);
                }

            }

            result.step_draw = time_animation / result._plan_draw.size();


            PointPP[] _data = new PointPP[3];

            if (type_path == MySurfaceView.PLAN_TYPE_PATH_1)
            {
                PointPP _center_p = get_center_point(left_center_board.getx()
                        , left_center_board.gety()
                        , left_center_board.getx()
                        , left_bottom_board.gety());

                PointPP _t = get_perpendicular_vector(
                        left_center_board.getx()
                        , left_center_board.gety()
                        , left_center_board.getx()
                        , left_bottom_board.gety()
                        , _center_p.getx()
                        , _center_p.gety()
                );

                _data[0] = new PointPP(left_center_board.getx(), left_bottom_board.gety());
                _data[1] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 70 / _t.Length()
                        , _center_p.gety() + _t.gety() / _t.Length() * 70 / _t.Length());
                _data[2] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 150 / _t.Length(), left_center_board.gety());
            }
            else if (type_path == MySurfaceView.PLAN_TYPE_PATH_2)
            {
                PointPP _center_p = get_center_point(left_center_board.getx()
                        , left_center_board.gety()
                        , left_center_board.getx()
                        , left_bottom_board.gety());

                PointPP _t = get_perpendicular_vector(
                        left_center_board.getx()
                        , left_center_board.gety()
                        , left_center_board.getx()
                        , left_bottom_board.gety()
                        , _center_p.getx()
                        , _center_p.gety()
                );

                _data[0] = new PointPP(left_center_board.getx(), left_bottom_board.gety());
                _data[1] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 50 / _t.Length()
                        , _center_p.gety() - _t.gety() / _t.Length() * 50 / _t.Length());
                _data[2] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 120 / _t.Length(), left_center_board.gety());
            }
            else if (type_path == MySurfaceView.PLAN_TYPE_PATH_3)
            {
                PointPP _center_p = get_center_point(left_center_board.getx()
                        , left_center_board.gety()
                        , left_center_board.getx()
                        , left_top_board.gety());

                PointPP _t = get_perpendicular_vector(
                        left_center_board.getx()
                        , left_top_board.gety()
                        , left_center_board.getx()
                        , left_center_board.gety()

                        , _center_p.getx()
                        , _center_p.gety()
                );

                _data[0] = new PointPP(left_center_board.getx(), left_top_board.gety());
                _data[1] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 70 / _t.Length()
                        , _center_p.gety() + _t.gety() / _t.Length() * 70 / _t.Length());
                _data[2] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 150 / _t.Length(), left_center_board.gety());
            }
            else if (type_path == MySurfaceView.PLAN_TYPE_PATH_4)
            {
                PointPP _center_p = get_center_point(left_center_board.getx()
                        , left_center_board.gety()
                        , left_center_board.getx()
                        , left_top_board.gety());

                PointPP _t = get_perpendicular_vector(
                        left_center_board.getx()
                        , left_top_board.gety()
                        , left_center_board.getx()
                        , left_center_board.gety()

                        , _center_p.getx()
                        , _center_p.gety()
                );

                _data[0] = new PointPP(left_center_board.getx(), left_top_board.gety());
                _data[1] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 50 / _t.Length()
                        , _center_p.gety() - _t.gety() / _t.Length() * 50 / _t.Length());
                _data[2] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 120 / _t.Length(), left_center_board.gety());
            }
            ///--------------------------------------------------------------------------
            else if (type_path == MySurfaceView.PLAN_TYPE_PATH_11)
            {
                PointPP _center_p = get_center_point(right_center_board.getx()
                        , right_center_board.gety()
                        , right_center_board.getx()
                        , right_bottom_board.gety());

                PointPP _t = get_perpendicular_vector(
                        right_center_board.getx()
                        , right_center_board.gety()
                        , right_center_board.getx()
                        , right_bottom_board.gety()
                        , _center_p.getx()
                        , _center_p.gety()
                );

                _data[0] = new PointPP(right_center_board.getx(), left_bottom_board.gety());
                _data[1] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 70 / _t.Length()
                        , _center_p.gety() + _t.gety() / _t.Length() * 70 / _t.Length());
                _data[2] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 150 / _t.Length(), left_center_board.gety());
            }
            else if (type_path == MySurfaceView.PLAN_TYPE_PATH_22)
            {
                PointPP _center_p = get_center_point(right_center_board.getx()
                        , right_center_board.gety()
                        , right_center_board.getx()
                        , right_bottom_board.gety());

                PointPP _t = get_perpendicular_vector(
                        right_center_board.getx()
                        , right_center_board.gety()
                        , right_center_board.getx()
                        , right_bottom_board.gety()
                        , _center_p.getx()
                        , _center_p.gety()
                );

                _data[0] = new PointPP(right_center_board.getx(), left_bottom_board.gety());
                _data[1] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 50 / _t.Length()
                        , _center_p.gety() - _t.gety() / _t.Length() * 50 / _t.Length());
                _data[2] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 120 / _t.Length(), left_center_board.gety());
            }
            else if (type_path == MySurfaceView.PLAN_TYPE_PATH_33)
            {
                PointPP _center_p = get_center_point(right_center_board.getx()
                        , right_center_board.gety()
                        , right_center_board.getx()
                        , left_top_board.gety());

                PointPP _t = get_perpendicular_vector(
                        right_center_board.getx()
                        , left_top_board.gety()
                        , right_center_board.getx()
                        , right_center_board.gety()

                        , _center_p.getx()
                        , _center_p.gety()
                );

                _data[0] = new PointPP(right_center_board.getx(), left_top_board.gety());
                _data[1] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 70 / _t.Length()
                        , _center_p.gety() + _t.gety() / _t.Length() * 70 / _t.Length());
                _data[2] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 150 / _t.Length(), right_center_board.gety());
            }
            else if (type_path == MySurfaceView.PLAN_TYPE_PATH_44)
            {
                PointPP _center_p = get_center_point(right_center_board.getx()
                        , right_center_board.gety()
                        , right_center_board.getx()
                        , left_top_board.gety());

                PointPP _t = get_perpendicular_vector(
                        right_center_board.getx()
                        , left_top_board.gety()
                        , right_center_board.getx()
                        , right_center_board.gety()

                        , _center_p.getx()
                        , _center_p.gety()
                );

                _data[0] = new PointPP(right_center_board.getx(), left_top_board.gety());
                _data[1] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 50 / _t.Length()
                        , _center_p.gety() - _t.gety() / _t.Length() * 50 / _t.Length());
                _data[2] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 120 / _t.Length(), right_center_board.gety());
            }

            result._path_points = generateBezierPath(_data[0], _data[2], _data[1], _data[1], result._plan_draw.size() - 1);

            //result.last_draw = null;

            result.animation_dice_index = 0;

            //action_animation = true;

            result.plan_num = need_num;
            result.id = id;
        }

        return result;
    }

    private boolean is_cell_top(int pos)
    {
        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            if (pos == item.pos)
            {
                return item.is_top_in_board;
            }
        }
        return false;
    }

    public CellValues get_cell(int pos)
    {
        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            if (pos == item.pos)
            {
                return item;
            }
        }

        return null;
    }

    public CellValues get_cell_pos_white(int pos_white)
    {
        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            if (pos_white == item.pos_white)
            {
                return item;
            }
        }

        return null;
    }

    public CellValues get_cell_pos_black(int pos_black)
    {
        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            if (pos_black == item.pos_black)
            {
                return item;
            }
        }

        return null;
    }

    private PointPP get_pos_cell(int pos)
    {
        PointPP result = null;

        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            if (pos == item.pos)
            {
                if (item.count_figures > 0)
                {
                    float step_draw = step_x * 0.46f * 2;

                    if (item.count_figures * radius_1 * 2 > h2)
                    {
                        step_draw = (h2 - radius_1) / (item.count_figures);
                    }


                    if (item.is_top_in_board)
                    {
                        result = new PointPP(item.x_pos, item.y_pos + step_draw * (item.count_figures - 1));
                    }
                    else
                    {
                        result = new PointPP(item.x_pos, item.y_pos - step_draw * (item.count_figures - 1));
                    }
                }
                else
                {
                    result = new PointPP(item.x_pos, item.y_pos);
                }


                break;
            }
        }

        return result;
    }

    private CellValues get_num_selected_cell_x_y_touch(int x, int y)
    {
        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            float step_draw = step_x * 0.46f * 2;

            if (item.count_figures * radius_1 * 2 > h2)
            {
                step_draw = (h2 - radius_1) / (item.count_figures);
            }

            if (item.is_top_in_board)
            {
                if (
                        x > item.x_pos - radius_1
                                && y > item.y_pos - radius_1
                                && x < item.x_pos + radius_1
                                && y < item.y_pos + radius_1 + ((item.count_figures > 0) ? (step_draw * (item.count_figures - 1)) : 0)
                        )
                {
                    return item;
                }
            }
            else
            {
                if (
                        x < item.x_pos + radius_1
                                && y < item.y_pos + radius_1
                                && x > item.x_pos - radius_1
                                && y > item.y_pos - radius_1 - ((item.count_figures > 0) ? (step_draw * (item.count_figures - 1)) : 0)
                        )
                {
                    return item;
                }
            }
        }

        return null;
    }

    public void create_animation_figure(int from, int to, long time, int count_segments)
    {
        synchronized (synch_create_fly_figure)
        {
            fly_figure = new PlanAnimationFigure();
            CellValues c_from = this.get_cell(from);

            /*if( c_from.count_figures == 0 )
            {
                fly_figure = null;
                return;
            }*/

            fly_figure.from = from;
            fly_figure.to = to;

            if (c_from.color_type == FIGURE_COLOR_BLACK)
            {
                fly_figure.b = _figure_black;
            }
            else if (c_from.color_type == FIGURE_COLOR_WHITE)
            {
                fly_figure.b = _figure_white;
            }

            fly_figure._path_points = from_to(from, to, count_segments);

            fly_figure.step_draw = time / count_segments;
        }
    }

    public void create_animation_figure_out_board(int from, float to_pos_x, float to_pos_y, long time, int count_segments)
    {
        synchronized (synch_create_fly_figure_out_board)
        {
            fly_figure_out_board = new PlanAnimationFigureOutBoard();
            CellValues c_from = this.get_cell(from);

            fly_figure_out_board.from = from;

            if (c_from.color_type == FIGURE_COLOR_BLACK)
            {
                fly_figure_out_board.b = _figure_black;
            }
            else if (c_from.color_type == FIGURE_COLOR_WHITE)
            {
                fly_figure_out_board.b = _figure_white;
            }

            fly_figure_out_board._path_points = from_to2(from, to_pos_x, to_pos_y, count_segments);

            fly_figure_out_board.step_draw = time / count_segments;
        }
    }

    private List<PointPP> from_to2(int from, float to_pos_x, float to_pos_y, int count_segments)
    {
        List<PointPP> result = null;

        CellValues _p1 = this.get_cell(from);
        //CellValues _p2 = this.get_cell( to );

        Paint paint = new Paint();
        paint.setColor(Color.RED);

        PointPP _center_p, _t;
        PointPP p1 = this.get_pos_cell(from);
        PointPP p2 = new PointPP(to_pos_x, to_pos_y);

        int _s = 190;

        if (_p1.is_top_in_board == true)
        {
            if (p1.gety() == p2.gety())
            {
                _s = 190;
            }
            else if (p1.gety() > p2.gety())
            {
                _s = 190;
            }
            else if (p1.gety() < p2.gety())
            {
                _s = 190;
            }
        }

        _center_p = get_center_point(
                p1.getx()
                , p1.gety()
                , p2.getx()
                , p2.gety()
        );

        _t = get_perpendicular_vector(
                p1.getx()
                , p1.gety()
                , p2.getx()
                , p2.gety()
                , _center_p.getx()
                , _center_p.gety()
        );


        /*canvas.drawLine(
                _center_p.getx()
                , _center_p.gety()
                , _center_p.getx() + _t.getx() / _t.Length() * _s / _t.Length()
                , _center_p.gety() + _t.gety() / _t.Length() * _s / _t.Length()
                , paint
        );*/


        {
            PointPP[] _data = new PointPP[3];

            _data[0] = new PointPP(p1.getx(), p1.gety());
            _data[1] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * _s / _t.Length()
                    , _center_p.gety() + _t.gety() / _t.Length() * _s / _t.Length());
            _data[2] = new PointPP(p2.getx(), p2.gety());

            result = generateBezierPath(_data[0], _data[2], _data[1], _data[1], count_segments);

            /*for (int m = 0; m < result.size() - 1; m++)
            {
                canvas.drawLine(
                        result.get(m).getx()
                        , result.get(m).gety()
                        , result.get(m + 1).getx()
                        , result.get(m + 1).gety()
                        , paint
                );
            }*/
        }

        return result;
    }

    private List<PointPP> from_to(/*Canvas canvas,*/ int from, int to, int count_segments)
    {
        List<PointPP> result = null;

        CellValues _p1 = this.get_cell(from);
        CellValues _p2 = this.get_cell(to);

        Paint paint = new Paint();
        paint.setColor(Color.RED);

        PointPP _center_p, _t;
        PointPP p1 = this.get_pos_cell(from);
        PointPP p2 = this.get_pos_cell(to);

        int _s = 190;

        if (_p1.is_top_in_board == true && _p2.is_top_in_board == true)
        {
            if (p1.gety() == p2.gety())
            {
                _s = 190;
            }
            else if (p1.gety() > p2.gety())
            {
                _s = 190;
            }
            else if (p1.gety() < p2.gety())
            {
                _s = 190;
            }
        }
        else if (_p1.is_top_in_board == false && _p2.is_top_in_board == false)
        {
            if (p1.gety() == p2.gety())
            {
                _s = 190;
            }
            else if (p1.gety() > p2.gety())
            {
                _s = 190;
            }
            else if (p1.gety() < p2.gety())
            {
                _s = 190;
            }
        }
        else if ((_p1.is_top_in_board && !_p2.is_top_in_board) || (_p2.is_top_in_board && !_p1.is_top_in_board))
        {
            if (_p1.board_local_pos == LOCAL_POS__LEFT_TOP && _p2.board_local_pos == LOCAL_POS__LEFT_BOTTOM)
            {
                _s = 50;
            }
            else if (_p1.board_local_pos == LOCAL_POS__LEFT_TOP && _p2.board_local_pos == LOCAL_POS__RIGHT_BOTTOM)
            {
                _s = 80;
            }
            else if (_p1.board_local_pos == LOCAL_POS__RIGHT_TOP && _p2.board_local_pos == LOCAL_POS__LEFT_BOTTOM)
            {
                _s = -80;
            }
            else if (_p1.board_local_pos == LOCAL_POS__RIGHT_TOP && _p2.board_local_pos == LOCAL_POS__RIGHT_BOTTOM)
            {
                _s = -50;
            }
            else if (_p1.board_local_pos == LOCAL_POS__LEFT_BOTTOM && _p2.board_local_pos == LOCAL_POS__RIGHT_TOP)
            {
                _s = 80;
            }
            else if (_p1.board_local_pos == LOCAL_POS__LEFT_BOTTOM && _p2.board_local_pos == LOCAL_POS__LEFT_TOP)
            {
                _s = 50;
            }
            else if (_p1.board_local_pos == LOCAL_POS__RIGHT_BOTTOM && _p2.board_local_pos == LOCAL_POS__LEFT_TOP)
            {
                _s = -80;
            }
            else if (_p1.board_local_pos == LOCAL_POS__RIGHT_BOTTOM && _p2.board_local_pos == LOCAL_POS__RIGHT_TOP)
            {
                _s = -50;
            }
        }

        _center_p = get_center_point(
                p1.getx()
                , p1.gety()
                , p2.getx()
                , p2.gety()
        );

        _t = get_perpendicular_vector(
                p1.getx()
                , p1.gety()
                , p2.getx()
                , p2.gety()
                , _center_p.getx()
                , _center_p.gety()
        );


        /*canvas.drawLine(
                _center_p.getx()
                , _center_p.gety()
                , _center_p.getx() + _t.getx() / _t.Length() * _s / _t.Length()
                , _center_p.gety() + _t.gety() / _t.Length() * _s / _t.Length()
                , paint
        );*/


        {
            PointPP[] _data = new PointPP[3];

            _data[0] = new PointPP(p1.getx(), p1.gety());
            _data[1] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * _s / _t.Length()
                    , _center_p.gety() + _t.gety() / _t.Length() * _s / _t.Length());
            _data[2] = new PointPP(p2.getx(), p2.gety());

            result = generateBezierPath(_data[0], _data[2], _data[1], _data[1], count_segments);

            /*for (int m = 0; m < result.size() - 1; m++)
            {
                canvas.drawLine(
                        result.get(m).getx()
                        , result.get(m).gety()
                        , result.get(m + 1).getx()
                        , result.get(m + 1).gety()
                        , paint
                );
            }*/
        }

        return result;
    }

    public void set_selected_cell(int pos)
    {
        selected_cell = pos;
    }

    public void clear_selected_cell()
    {
        selected_cell = 0;
    }

    public void clear_select_points()
    {
        selected_points.clear();

        Log.d("TAG", android.util.Log.getStackTraceString(new Exception()));
    }

    public void add_point_select_points(Integer p)
    {
        selected_points.add(p);
    }

    public void clear_select_end_points()
    {
        selected_points_end_xods_found.clear();
    }

    public void add_point_select_end_points(Integer p)
    {
        selected_points_end_xods_found.add(p);
    }

    public void slot_animation_dice_add_exec_num_dice(int num)
    {
        for (int i = 0; i < slot_animation_dice.size(); i++)
        {
            PlanInfo item = slot_animation_dice.get(i);

            if (item.plan_num == num && item.exec_xod == false)
            {
                item.last_draw = item.last_draw_original;//MainActivity.changeBitmapContrastBrightness(item.last_draw, 1.6f, -12);

                item.exec_xod = true;
                break;
            }
        }
    }

    public boolean select_points_exists_pos(int pos)
    {
        for (int i = 0; i < selected_points.size(); i++)
        {
            if (selected_points.get(i) == pos)
            {
                return true;
            }
        }

        return false;
    }

    public CImageButton getUiBtn(int id)
    {
        for (int i = 0; i < list_buttons.size(); i++)
        {
            if (list_buttons.get(i).getId() == id)
            {
                return list_buttons.get(i);
            }
        }
        return null;
    }

    public void show_btn_id(int id)
    {
        for (int i = 0; i < list_buttons.size(); i++)
        {
            if (list_buttons.get(i).getId() == id)
            {
                list_buttons.get(i).display = true;
                break;
            }
        }
    }

    public void hide_btn_id(int id)
    {
        for (int i = 0; i < list_buttons.size(); i++)
        {
            if (list_buttons.get(i).getId() == id)
            {
                list_buttons.get(i).display = false;
                break;
            }
        }
    }

    public void hide_all_btns()
    {
        for (int i = 0; i < list_buttons.size(); i++)
        {
            list_buttons.get(i).display = false;
        }
    }

    public boolean slot_dice_animation_all_stop()
    {
        boolean all_stop = true;

        for (int k0 = 0; k0 < slot_animation_dice.size(); k0++)
        {
            PlanInfo item0 = slot_animation_dice.get(k0);

            if (item0.stop_animation == false)
            {
                all_stop = false;
                break;
            }
        }

        return all_stop;
    }

    private void buildBackgroundImage()
    {
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Paint paint3 = new Paint();

        paint.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint3.setAntiAlias(true);

        paint.setColor(Color.BLACK);

        background_big_img = Bitmap.createBitmap(w_canvas, h_canvas, Bitmap.Config.ARGB_8888);
        Canvas _canvas = new Canvas(background_big_img);

        _canvas.drawRect(0, 0, w_canvas, h_canvas, paint);

        /// обводка и фон

        _canvas.drawRect(w2_canvas + offset_x - width_open_part_board - padding_center
                , 0 + offset_y + padding_top
                , w2_canvas + offset_x - padding_center
                , h_canvas + offset_y - padding_bottom
                , g_paint_1
        );

        _canvas.drawRect(w2_canvas + offset_x + padding_center
                , 0 + offset_y + padding_top
                , w2_canvas + offset_x + width_open_part_board + padding_center
                , h_canvas + offset_y - padding_bottom
                , g_paint_1
        );

        _canvas.drawRect(w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner
                , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                , w2_canvas + offset_x - padding_center - padding_inner
                , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                , g_paint_2
        );

        _canvas.drawRect(w2_canvas + offset_x + padding_center + padding_inner
                , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                , w2_canvas + offset_x + width_open_part_board + padding_center - padding_inner
                , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                , g_paint_2
        );

        // 12 лунок

        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.parseColor("#f4e296"));

        for (int i = 0; i < 6; i++)
        {
            _canvas.drawCircle(
                    w2_canvas + offset_x + step_cells * (i) + step_cells_2 - width_open_part_board - padding_center + padding_inner
                    , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                    , radius_1
                    , g_paint_2
            );

            _canvas.drawCircle(
                    w2_canvas + offset_x + step_cells * (i) + step_cells_2 + padding_center + padding_inner
                    , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                    , radius_1
                    , g_paint_2
            );

            //-------------------

            _canvas.drawCircle(
                    w2_canvas + offset_x + step_cells * (i) + step_cells_2 - width_open_part_board - padding_center + padding_inner
                    , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                    , radius_1
                    , g_paint_2
            );

            _canvas.drawCircle(
                    w2_canvas + offset_x + step_cells * (i) + step_cells_2 + padding_center + padding_inner
                    , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                    , radius_1
                    , g_paint_2
            );
        }

        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.parseColor("#f4e296"));

        _canvas.drawRect(w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + radius_1
                , 0 + offset_y + padding_top + padding_inner_2 + radius_1 - radius_1 / 3
                , w2_canvas + offset_x - padding_center - padding_inner - radius_1
                , 0 + offset_y + padding_top + padding_inner_2 + radius_1 * 2
                , g_paint_2
        );

        _canvas.drawRect(w2_canvas + offset_x + padding_center + padding_inner + radius_1
                , 0 + offset_y + padding_top + padding_inner_2 + radius_1 - radius_1 / 3
                , w2_canvas + offset_x + width_open_part_board + padding_center - padding_inner - radius_1
                , 0 + offset_y + padding_top + padding_inner_2 + radius_1 * 2
                , g_paint_2
        );

        _canvas.drawRect(w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + radius_1
                , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1 - radius_1 / 3
                , w2_canvas + offset_x - padding_center - padding_inner - radius_1
                , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1 + radius_1 / 3
                , g_paint_2
        );

        _canvas.drawRect(w2_canvas + offset_x + padding_center + padding_inner + radius_1
                , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1 - radius_1 / 3
                , w2_canvas + offset_x + width_open_part_board + padding_center - padding_inner - radius_1
                , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1 + radius_1 / 3
                , g_paint_2
        );

        _canvas.drawBitmap(_bk_2
                , left_center_board.getx() - _bk_2.getWidth() / 2
                , left_center_board.gety() - _bk_2.getHeight() / 2
                , null);

        _canvas.drawBitmap(_bk_2
                , right_center_board.getx() - _bk_2.getWidth() / 2
                , right_center_board.gety() - _bk_2.getHeight() / 2
                , null);

        if (design_boadr_type == DESIGN_BOADR_TYPE_1)
        {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#f3cc87"));

            paint2.setStyle(Paint.Style.FILL);
            paint2.setColor(Color.parseColor("#af9079"));

            Paint _draw_current = paint;

            Path path = null;

            for (int i = 0; i < 6; i++)
            {
                if (i % 2 == 0)
                {
                    _draw_current = paint;
                }
                else
                {
                    _draw_current = paint2;
                }

                _canvas.drawCircle(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                        , radius_1
                        , _draw_current
                );

                path = new Path();
                path.moveTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2 - radius_1
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2 + radius_1
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1 + ((i == 2 || i == 3) ? peak_size * 0.7f : peak_size) + ((i
                                == 0 || i == 5) ? peak_size * 0.2f : 0)
                );
                _canvas.drawPath(path, _draw_current);


                _canvas.drawCircle(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                        , radius_1
                        , _draw_current
                );


                path = new Path();
                path.moveTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2 - radius_1
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2 + radius_1
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1 + ((i == 2 || i == 3) ? peak_size * 0.7f : peak_size)
                                + ((i == 0 || i == 5) ? peak_size * 0.2f : 0)
                );
                _canvas.drawPath(path, _draw_current);

                //-------------------

                if (i % 2 == 0)
                {
                    _draw_current = paint2;
                }
                else
                {
                    _draw_current = paint;
                }

                _canvas.drawCircle(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                        , radius_1
                        , _draw_current
                );

                path = new Path();
                path.moveTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2 - radius_1
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2 + radius_1
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1 - ((i == 2 || i == 3) ? peak_size * 0.7f : peak_size)
                                - ((i == 0 || i == 5) ? peak_size * 0.2f : 0)
                );
                _canvas.drawPath(path, _draw_current);

                _canvas.drawCircle(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                        , radius_1
                        , _draw_current
                );

                path = new Path();
                path.moveTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2 - radius_1
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2 + radius_1
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1 - ((i == 2 || i == 3) ? peak_size * 0.7f : peak_size)
                                - ((i == 0 || i == 5) ? peak_size * 0.2f : 0)
                );
                _canvas.drawPath(path, _draw_current);
            }
        }
        else if (design_boadr_type == DESIGN_BOADR_TYPE_2)
        {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#f3cc87"));

            paint2.setStyle(Paint.Style.FILL);
            paint2.setColor(Color.parseColor("#af9079"));

            paint3.setStyle(Paint.Style.FILL);
            paint3.setColor(Color.parseColor("#f4e296"));

            Paint _draw_current = paint;

            Path path = null;

            for (int i = 0; i < 6; i++)
            {
                if (i % 2 == 0)
                {
                    _draw_current = paint;
                }
                else
                {
                    _draw_current = paint2;
                }

                path = new Path();
                path.moveTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2 - radius_1
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2 + radius_1
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1 + ((i == 2 || i == 3) ? peak_size * 0.7f : peak_size) + ((i
                                == 0 || i == 5) ? peak_size * 0.2f : 0)
                );
                _canvas.drawPath(path, _draw_current);

                _canvas.drawCircle(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                        , radius_1
                        , paint3
                );

                path = new Path();
                path.moveTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2 - radius_1
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2 + radius_1
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1 + ((i == 2 || i == 3) ? peak_size * 0.7f : peak_size)
                                + ((i == 0 || i == 5) ? peak_size * 0.2f : 0)
                );
                _canvas.drawPath(path, _draw_current);

                _canvas.drawCircle(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , 0 + offset_y + padding_top + padding_inner_2 + radius_1
                        , radius_1
                        , paint3
                );

                //-------------------

                if (i % 2 == 0)
                {
                    _draw_current = paint2;
                }
                else
                {
                    _draw_current = paint;
                }

                path = new Path();
                path.moveTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2 - radius_1
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2 + radius_1
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1 - ((i == 2 || i == 3) ? peak_size * 0.7f : peak_size)
                                - ((i == 0 || i == 5) ? peak_size * 0.2f : 0)
                );
                _canvas.drawPath(path, _draw_current);

                _canvas.drawCircle(
                        w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                        , radius_1
                        , paint3
                );

                path = new Path();
                path.moveTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2 - radius_1
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2 + radius_1
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                );
                path.lineTo(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1 - ((i == 2 || i == 3) ? peak_size * 0.7f : peak_size)
                                - ((i == 0 || i == 5) ? peak_size * 0.2f : 0)
                );
                _canvas.drawPath(path, _draw_current);

                _canvas.drawCircle(
                        w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2
                        , h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1
                        , radius_1
                        , paint3
                );
            }
        }
        // петли

        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.parseColor("#f2cf63"));

        _canvas.drawRect(w2_canvas + offset_x - padding_center - 2
                , 0 + offset_y + padding_top + padding_inner_2 + 30
                , w2_canvas + offset_x + padding_center + 2
                , 0 + offset_y + padding_top + padding_inner_2 + 150
                , g_paint_3
        );

        _canvas.drawRect(w2_canvas + offset_x - padding_center - 2
                , h_canvas + offset_y - padding_bottom - padding_inner_2 - 30
                , w2_canvas + offset_x + padding_center + 2
                , h_canvas + offset_y - padding_bottom - padding_inner_2 - 150
                , g_paint_3
        );

        /// draw text number cells

        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            String s_pos = String.valueOf(item.pos);// + "-" + String.valueOf(item.pos_white) + "-" + String.valueOf(item.pos_black);

            paint_text_clock_white.getTextBounds(s_pos, 0, s_pos.length(), bound_clock_white);

            if (item.is_top_in_board)
            {
                _canvas.drawText(s_pos
                        , item.x_pos - bound_clock_white.width() / 2
                        , item.y_pos - radius_1 - 3//+ bound_clock_white.height() / 2
                        , paint_text_clock_white);
            }
            else
            {
                _canvas.drawText(s_pos
                        , item.x_pos - bound_clock_white.width() / 2
                        , item.y_pos + radius_1 + 3 + bound_clock_white.height()
                        , paint_text_clock_white);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (this._stop_draw)
        {
            return;
        }

        super.onDraw(canvas);

        if (this.is_first_draw)
        {
            this.is_first_draw = false;

            w_canvas = canvas.getWidth();
            h_canvas = canvas.getHeight();

            w2_canvas = w_canvas / 2;
            h2_canvas = h_canvas / 2;

            width_open_part_board = w2_canvas - 2 * padding_out;

            step_x = (width_open_part_board - 2 * padding_inner) / 7;

            step_cells = (width_open_part_board - 2 * padding_inner) / 6;
            step_cells_2 = step_cells / 2;

            //step_add2 = (( width_open_part_board - 2 * padding_inner ) - step_x * 6 ) / 2;

            step_x_2 = step_x / 2;

            radius_1 = step_x / 2;
            radius2_1 = radius_1 / 2;

            h2 = (int) ((h_canvas - padding_top - padding_inner_2 - radius_1) - (0 + padding_bottom + padding_inner_2 + radius_1));

            peak_size = h2 / 2 - h2 / 10;

            h2 /= 2;


            _bk = BitmapFactory.decodeResource(getResources(), R.drawable.bk_menu);

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bk1);
            _bk_2 = bmp.createScaledBitmap(bmp
                    , (int) ((width_open_part_board - 2 * padding_inner - padding_top - padding_bottom) / 2)
                    , (int) ((width_open_part_board - 2 * padding_inner - padding_top - padding_bottom) / 2)
                    , true);

            left_center_board = new PointPP(w2_canvas + offset_x - padding_center - width_open_part_board / 2
                    , (0 + offset_y + padding_top) + ((h_canvas + offset_y - padding_bottom) - (0 + offset_y + padding_top)) / 2);

            right_center_board = new PointPP(w2_canvas + offset_x + padding_center + width_open_part_board / 2
                    , (0 + offset_y + padding_top) + ((h_canvas + offset_y - padding_bottom) - (0 + offset_y + padding_top)) / 2);

            //----------------------

            left_top_board = new PointPP(
                    w2_canvas + offset_x - padding_center - width_open_part_board
                    , (0 + offset_y + padding_top)
            );

            left_bottom_board = new PointPP(
                    w2_canvas + offset_x - padding_center - width_open_part_board
                    , (0 + offset_y + padding_top) + ((h_canvas + offset_y - padding_bottom) - (0 + offset_y + padding_top))
            );

            right_top_board = new PointPP(
                    w2_canvas + offset_x + padding_center + width_open_part_board
                    , (0 + offset_y + padding_top)
            );

            right_bottom_board = new PointPP(
                    w2_canvas + offset_x + padding_center + width_open_part_board
                    , (0 + offset_y + padding_top) + ((h_canvas + offset_y - padding_bottom) - (0 + offset_y + padding_top))
            );

            //---------------------

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.figure_white);

            float pr_c = (step_x * 0.46f * 2) * 100f / bmp.getWidth();

            if (figure_type == FIGIRE_TYPE_1)
            {
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.figure_white);
                _figure_white = bmp.createScaledBitmap(bmp
                        , (int) (step_x * 0.53f * 2)
                        , (int) (step_x * 0.53f * 2)
                        , true);

                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.figure_black);
                _figure_black = bmp.createScaledBitmap(bmp
                        , (int) (step_x * 0.53f * 2)
                        , (int) (step_x * 0.53f * 2)
                        , true);
            }
            else
            {
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.figure_white_1);
                _figure_white = bmp.createScaledBitmap(bmp
                        , (int) (step_x * 0.5f * 2)
                        , (int) (step_x * 0.5f * 2)
                        , true);

                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.figure_black_1);
                _figure_black = bmp.createScaledBitmap(bmp
                        , (int) (step_x * 0.5f * 2)
                        , (int) (step_x * 0.5f * 2)
                        , true);
            }

            List<Integer> new_size = null;

            float scale_pr = 20.5f;//step_x * ((100 - 80) / 100.0f);

            //----------------------------------------------------------------------------

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_1);
            _c1_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr / 100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_2);
            _c1_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr / 100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_3);
            _c1_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_4);
            _c1_4 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_5);
            _c1_5 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_6);
            _c1_6 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_7);
            _c1_7 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_8);
            _c1_8 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_9);
            _c1_9 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_10);
            _c1_10 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_11);
            _c1_11 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_12);
            _c1_12 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_13);
            _c1_13 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_14);
            _c1_14 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_15);
            _c1_15 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_16);
            _c1_16 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_17);
            _c1_17 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_18);
            _c1_18 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v2_1);
            _c1_v2_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v2_2);
            _c1_v2_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v2_3);
            _c1_v2_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v6_1);
            _c1_v6_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v6_2);
            _c1_v6_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v6_3);
            _c1_v6_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v6_4);
            _c1_v6_4 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v3_1);
            _c1_v3_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v3_2);
            _c1_v3_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v5_1);
            _c1_v5_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v5_2);
            _c1_v5_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v5_3);
            _c1_v5_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v5_4);
            _c1_v5_4 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v1_1);
            _c1_v1_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v1_2);
            _c1_v1_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v1_3);
            _c1_v1_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v4_1);
            _c1_v4_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v4_2);
            _c1_v4_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c1_v4_3);
            _c1_v4_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_1);
            _c2_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_2);
            _c2_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_3);
            _c2_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_4);
            _c2_4 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_5);
            _c2_5 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_6);
            _c2_6 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_7);
            _c2_7 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_8);
            _c2_8 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_9);
            _c2_9 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_10);
            _c2_10 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_11);
            _c2_11 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_12);
            _c2_12 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_13);
            _c2_13 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_14);
            _c2_14 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_15);
            _c2_15 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_16);
            _c2_16 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_17);
            _c2_17 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_18);
            _c2_18 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v1_1);
            _c2_v1_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v1_2);
            _c2_v1_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v1_3);
            _c2_v1_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v1_4);
            _c2_v1_4 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v2_1);
            _c2_v2_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v2_2);
            _c2_v2_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v2_3);
            _c2_v2_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v2_4);
            _c2_v2_4 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v3_1);
            _c2_v3_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v3_2);
            _c2_v3_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v4_1);
            _c2_v4_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v4_2);
            _c2_v4_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v4_3);
            _c2_v4_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v5_1);
            _c2_v5_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v5_2);
            _c2_v5_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v5_3);
            _c2_v5_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v5_4);
            _c2_v5_4 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);


            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v6_1);
            _c2_v6_1 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v6_2);
            _c2_v6_2 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.c2_v6_3);
            _c2_v6_3 = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////

            scale_pr = 80.0f;

            //----------------------------------------------

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.out_of_borad_left);
            _img_left_exit_figure = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.out_of_borad_left_select);
            _img_left_exit_figure_select = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight
                    () * scale_pr /
                    100f), true);

            CImageButton _ui_btn = new CImageButton(
                    1
                    , w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner
                    , (int) (left_center_board.gety() - _img_left_exit_figure.getHeight() / 2)
                    , _img_left_exit_figure
                    , _img_left_exit_figure_select
            )
            {
                @Override
                public void callback_pressed()
                {
                    callback_press_btn(this.getId());
                }
            };

            list_buttons.add(_ui_btn);

            //----------------------------------------------

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.out_of_borad_right);
            _img_right_exit_figure = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight() * scale_pr /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.out_of_borad_right_select);
            _img_right_exit_figure_select = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale_pr / 100f), (int) (bmp.getHeight
                    () * scale_pr /
                    100f), true);

            CImageButton _ui_btn1 = new CImageButton(
                    2
                    , w2_canvas + offset_x + width_open_part_board + padding_center - padding_inner - _img_right_exit_figure.getWidth()
                    , (int) (left_center_board.gety() - _img_right_exit_figure.getHeight() / 2)
                    , _img_right_exit_figure
                    , _img_right_exit_figure_select
            )
            {
                @Override
                public void callback_pressed()
                {
                    callback_press_btn(this.getId());
                }
            };

            list_buttons.add(_ui_btn1);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.select);
            _select_figure = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * pr_c / 100f), (int) (bmp.getHeight() * pr_c /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.select_alternative_xod);
            _select_alternative_xod = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * pr_c / 100f), (int) (bmp.getHeight() * pr_c /
                    100f), true);

            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.select_alternative_xod_red);
            _select_alternative_xod_red = bmp.createScaledBitmap(bmp, (int) (bmp.getWidth() * pr_c / 100f), (int) (bmp.getHeight() * pr_c /
                    100f), true);

            int pos_num = 24;
            int real_pos_num = 24;
            int real_pos_num_2 = 24;

            if (mode_color_board == MODE_COLOR_BLACK)
            {
                real_pos_num = 12;
            }

            if (mode_color_board == MODE_COLOR_WHITE)
            {
                real_pos_num_2 = 12;
            }

            _cells.clear();

            for (int i = 5; i >= 0; i--)
            {
                CellValues _c = new CellValues();

                _c.pos = pos_num;
                _c.color_type = FIGURE_COLOR_NONE;
                _c.count_figures = 0;
                _c.x_pos = w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2;
                _c.y_pos = 0 + offset_y + padding_top + padding_inner_2 + radius_1;
                _c.is_top_in_board = true;
                _c.pos_white = real_pos_num;
                _c.pos_black = real_pos_num_2;
                _c.board_local_pos = LOCAL_POS__RIGHT_TOP;

                _cells.add(_c);

                pos_num -= 1;

                real_pos_num -= 1;
                real_pos_num_2 -= 1;
            }

            for (int i = 5; i >= 0; i--)
            {
                CellValues _c = new CellValues();

                _c.pos = pos_num;
                _c.color_type = FIGURE_COLOR_NONE;
                _c.count_figures = 0;
                _c.x_pos = w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2;
                _c.y_pos = 0 + offset_y + padding_top + padding_inner_2 + radius_1;
                _c.is_top_in_board = true;
                _c.pos_white = real_pos_num;
                _c.pos_black = real_pos_num_2;
                _c.board_local_pos = LOCAL_POS__LEFT_TOP;

                _cells.add(_c);

                pos_num -= 1;

                real_pos_num -= 1;
                real_pos_num_2 -= 1;
            }

            if (mode_color_board == MODE_COLOR_BLACK)
            {
                real_pos_num = 24;
            }

            if (mode_color_board == MODE_COLOR_WHITE)
            {
                real_pos_num_2 = 24;
            }

            for (int i = 0; i < 6; i++)
            {
                CellValues _c = new CellValues();

                _c.pos = pos_num;
                _c.color_type = FIGURE_COLOR_NONE;
                _c.count_figures = 0;
                _c.x_pos = w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner + step_cells * (i) + step_cells_2;
                _c.y_pos = h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1;
                _c.pos_white = real_pos_num;
                _c.pos_black = real_pos_num_2;
                _c.board_local_pos = LOCAL_POS__LEFT_BOTTOM;

                _cells.add(_c);

                pos_num -= 1;

                real_pos_num -= 1;
                real_pos_num_2 -= 1;
            }

            for (int i = 0; i < 6; i++)
            {
                CellValues _c = new CellValues();

                _c.pos = pos_num;
                _c.color_type = FIGURE_COLOR_NONE;
                _c.count_figures = 0;
                _c.x_pos = w2_canvas + offset_x + padding_center + padding_inner + step_cells * (i) + step_cells_2;
                _c.y_pos = h_canvas + offset_y - padding_bottom - padding_inner_2 - radius_1;
                _c.pos_white = real_pos_num;
                _c.pos_black = real_pos_num_2;
                _c.board_local_pos = LOCAL_POS__RIGHT_BOTTOM;

                _cells.add(_c);

                pos_num -= 1;

                real_pos_num -= 1;
                real_pos_num_2 -= 1;
            }

            if (mode_color_board == MODE_COLOR_BLACK)
            {
                CellValues _t = get_values_from_pos(24);
                _t.count_figures = 15;
                _t.color_type = FIGURE_COLOR_BLACK;


                _t = get_values_from_pos(12);
                _t.count_figures = 15;
                _t.color_type = FIGURE_COLOR_WHITE;/**/


                /*CellValues _t = get_values_from_pos(18);
                _t.count_figures = 15;
                _t.color_type = FIGURE_COLOR_WHITE;


                _t = get_values_from_pos(6);
                _t.count_figures = 15;
                _t.color_type = FIGURE_COLOR_BLACK;/**/
            }
            else
            {
                CellValues _t = get_values_from_pos(24);
                _t.count_figures = 15;
                _t.color_type = FIGURE_COLOR_WHITE;


                _t = get_values_from_pos(12);
                _t.count_figures = 15;
                _t.color_type = FIGURE_COLOR_BLACK;/**/

                /*CellValues _t = get_values_from_pos(7);
                _t.count_figures = 15;
                _t.color_type = FIGURE_COLOR_WHITE;


                _t = get_values_from_pos(18);
                _t.count_figures = 15;
                _t.color_type = FIGURE_COLOR_BLACK;/**/
            }


            selected_cell = 0;
            selected_points.clear();
            selected_points_end_xods_found.clear();

            callback_onInitDraw();

            g_paint_1.setColor(Color.BLACK);
            g_paint_1.setStyle(Paint.Style.FILL);
            g_paint_1.setColor(Color.parseColor("#904909"));
            g_paint_1.setAntiAlias(true);

            g_paint_2.setColor(Color.BLACK);
            g_paint_2.setStyle(Paint.Style.FILL);
            g_paint_2.setColor(Color.parseColor("#f4e296"));
            g_paint_2.setAntiAlias(true);

            g_paint_3.setColor(Color.BLACK);
            g_paint_3.setStyle(Paint.Style.FILL);
            g_paint_3.setColor(Color.parseColor("#f2cf63"));
            g_paint_3.setAntiAlias(true);


        } /// - first draw

        if (needRebuildBackground)
        {
            buildBackgroundImage();
            needRebuildBackground = false;
        }

        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Paint paint3 = new Paint();

        paint.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint3.setAntiAlias(true);

        paint.setColor(Color.BLACK);

        canvas.drawRect(0, 0, w_canvas, h_canvas, paint);

        canvas.drawBitmap(background_big_img, 0, 0, null);

        /*if( ! MainActivity.isTablet(this.getContext()) )
        {
            for (int y = 0, height = h_canvas; y < height; y += _bk.getHeight())
            {
                for (int x = 0, width = w_canvas; x < width; x += _bk.getWidth())
                {
                    canvas.drawBitmap(_bk, x, y, null);
                }
            }
        }*/


        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.parseColor("#904909"));
/*
        /// обводка и фон

        canvas.drawRect(w2_canvas + offset_x - width_open_part_board - padding_center
                , 0 + offset_y + padding_top
                , w2_canvas + offset_x - padding_center
                , h_canvas + offset_y - padding_bottom
                , g_paint_1
        );

        canvas.drawRect(w2_canvas + offset_x + padding_center
                , 0 + offset_y + padding_top
                , w2_canvas + offset_x + width_open_part_board + padding_center
                , h_canvas + offset_y - padding_bottom
                , g_paint_1
        );*/

        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.parseColor("#f4e296"));



        // select point end found xods
        if (selected_points_end_xods_found.size() > 0)
        {
            for (int i = 0; i < _cells.size(); i++)
            {
                CellValues item = _cells.get(i);

                if (selected_points_end_xods_found.indexOf(item.pos) != -1)
                {
                    if (item.is_top_in_board)
                    {
                        /*Path path = new Path();
                        path.moveTo(
                                item.x_pos - radius_1
                                , item.y_pos
                        );
                        path.lineTo(
                                item.x_pos + radius_1
                                , item.y_pos
                        );
                        path.lineTo(
                                item.x_pos
                                , item.y_pos + ((item.pos == 15 || item.pos == 16 || item.pos == 21 || item.pos == 22) ? peak_size * 0.7f : peak_size)
                                        + ((item.pos == 13 || item.pos == 18 || item.pos == 19 || item.pos == 24) ? peak_size * 0.2f
                                        : 0)
                        );
                        canvas.drawPath(path, paint_select_point);

                        canvas.drawCircle(
                                item.x_pos
                                , item.y_pos
                                , radius_1
                                , paint_select_point3
                        );*/

                        if (item.count_figures == 0)
                        {
                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos
                                    , radius_1 / 4
                                    , paint_select_point4
                            );

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos
                                    , radius_1 / 4 + 1
                                    , paint_black
                            );

                        }
                        else
                        {
                            float step_draw = step_x * 0.46f * 2;

                            if (item.count_figures * radius_1 * 2 > h2)
                            {
                                step_draw = (h2 - radius_1) / (item.count_figures);
                            }

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos + step_draw * (item.count_figures - 1) + radius_1 + radius_1 / 4
                                    , radius_1 / 4
                                    , paint_select_point4
                            );

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos + step_draw * (item.count_figures - 1) + radius_1 + radius_1 / 4
                                    , radius_1 / 4 + 1
                                    , paint_black
                            );
                        }
                    }
                    else
                    {
                        /*Path path = new Path();
                        path.moveTo(
                                item.x_pos - radius_1
                                , item.y_pos
                        );
                        path.lineTo(
                                item.x_pos + radius_1
                                , item.y_pos
                        );
                        path.lineTo(
                                item.x_pos
                                , item.y_pos - ((item.pos == 10 || item.pos == 9 || item.pos == 4 || item.pos == 3) ? peak_size * 0.7f : peak_size)
                                        - ((item.pos == 12 || item.pos == 7 || item.pos == 6 || item.pos == 1) ? peak_size * 0.2f : 0)
                        );
                        canvas.drawPath(path, paint_select_point);

                        canvas.drawCircle(
                                item.x_pos
                                , item.y_pos
                                , radius_1
                                , paint_select_point3
                        );*/

                        if (item.count_figures == 0)
                        {
                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos
                                    , radius_1 / 4
                                    , paint_select_point4
                            );

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos
                                    , radius_1 / 4 + 1
                                    , paint_black
                            );
                        }
                        else
                        {
                            float step_draw = step_x * 0.46f * 2;

                            if (item.count_figures * radius_1 * 2 > h2)
                            {
                                step_draw = (h2 - radius_1) / (item.count_figures);
                            }

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos - step_draw * (item.count_figures - 1) - radius_1 - radius_1 / 4
                                    , radius_1 / 4
                                    , paint_select_point4
                            );

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos - step_draw * (item.count_figures - 1) - radius_1 - radius_1 / 4
                                    , radius_1 / 4 + 1
                                    , paint_black
                            );
                        }
                    }

                    //break;
                }
            }
        }

        // select point
        if (selected_points.size() > 0)
        {
            for (int i = 0; i < _cells.size(); i++)
            {
                CellValues item = _cells.get(i);

                if (selected_points.indexOf(item.pos) != -1)
                {
                    if (item.is_top_in_board)
                    {
                        Path path = new Path();
                        path.moveTo(
                                item.x_pos - radius_1
                                , item.y_pos
                        );
                        path.lineTo(
                                item.x_pos + radius_1
                                , item.y_pos
                        );
                        path.lineTo(
                                item.x_pos
                                , item.y_pos + ((item.pos == 15 || item.pos == 16 || item.pos == 21 || item.pos == 22) ? peak_size * 0.7f : peak_size)
                                        + ((item.pos == 13 || item.pos == 18 || item.pos == 19 || item.pos == 24) ? peak_size * 0.2f
                                        : 0)
                        );
                        canvas.drawPath(path, paint_select_point);

                        canvas.drawCircle(
                                item.x_pos
                                , item.y_pos
                                , radius_1
                                , paint_select_point3
                        );

                        if (item.count_figures == 0)
                        {
                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos
                                    , radius_1 / 4
                                    , paint_select_point2
                            );

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos
                                    , radius_1 / 4 + 1
                                    , paint_black
                            );

                        }
                        else
                        {
                            float step_draw = step_x * 0.46f * 2;

                            if (item.count_figures * radius_1 * 2 > h2)
                            {
                                step_draw = (h2 - radius_1) / (item.count_figures);
                            }

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos + step_draw * (item.count_figures - 1) + radius_1 + radius_1 / 4
                                    , radius_1 / 4
                                    , paint_select_point2
                            );

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos + step_draw * (item.count_figures - 1) + radius_1 + radius_1 / 4
                                    , radius_1 / 4 + 1
                                    , paint_black
                            );
                        }


                    }
                    else
                    {
                        Path path = new Path();
                        path.moveTo(
                                item.x_pos - radius_1
                                , item.y_pos
                        );
                        path.lineTo(
                                item.x_pos + radius_1
                                , item.y_pos
                        );
                        path.lineTo(
                                item.x_pos
                                , item.y_pos - ((item.pos == 10 || item.pos == 9 || item.pos == 4 || item.pos == 3) ? peak_size * 0.7f : peak_size)
                                        - ((item.pos == 12 || item.pos == 7 || item.pos == 6 || item.pos == 1) ? peak_size * 0.2f : 0)
                        );
                        canvas.drawPath(path, paint_select_point);

                        canvas.drawCircle(
                                item.x_pos
                                , item.y_pos
                                , radius_1
                                , paint_select_point3
                        );

                        if (item.count_figures == 0)
                        {
                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos
                                    , radius_1 / 4
                                    , paint_select_point2
                            );

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos
                                    , radius_1 / 4 + 1
                                    , paint_black
                            );
                        }
                        else
                        {
                            float step_draw = step_x * 0.46f * 2;

                            if (item.count_figures * radius_1 * 2 > h2)
                            {
                                step_draw = (h2 - radius_1) / (item.count_figures);
                            }

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos - step_draw * (item.count_figures - 1) - radius_1 - radius_1 / 4
                                    , radius_1 / 4
                                    , paint_select_point2
                            );

                            canvas.drawCircle(
                                    item.x_pos
                                    , item.y_pos - step_draw * (item.count_figures - 1) - radius_1 - radius_1 / 4
                                    , radius_1 / 4 + 1
                                    , paint_black
                            );
                        }


                    }

                    //break;
                }
            }
        }

        /// draw figures
        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            if (item.count_figures > 0)
            {
                float step_draw = step_x * 0.46f * 2;

                if (item.count_figures * radius_1 * 2 > h2)
                {
                    step_draw = (h2 - radius_1) / (item.count_figures);
                }

                if (item.is_top_in_board)
                {
                    for (int j = 0; j < item.count_figures; j++)
                    {
                        canvas.drawBitmap(item.color_type == FIGURE_COLOR_BLACK ? _figure_black : _figure_white
                                , item.x_pos - _figure_black.getWidth() / 2
                                , item.y_pos - _figure_black.getHeight() / 2 + step_draw * j
                                , null);
                    }

                    if (item.count_figures > 4)
                    {
                        String _v = String.valueOf(item.count_figures);

                        paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos + step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_2);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos + step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_1);
                    }
                }
                else
                {
                    for (int j = 0; j < item.count_figures; j++)
                    {
                        canvas.drawBitmap(item.color_type == FIGURE_COLOR_BLACK ? _figure_black : _figure_white
                                , item.x_pos - _figure_black.getWidth() / 2
                                , item.y_pos - _figure_black.getHeight() / 2 - step_draw * j
                                , null);
                    }

                    if (item.count_figures > 4)
                    {
                        String _v = String.valueOf(item.count_figures);

                        paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos - step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_2);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos - step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_1);
                    }
                }


            }
        }

        ///--------------------------------------------------------------
        /// ALTERNATIVE XODS
        for (int i = 0, _len = _cells.size(); i < _len; i++)
        {
            CellValues item = _cells.get(i);

            if (this.selected_alternative_xods.indexOf(item.pos) != -1
                    && item.count_figures > 0)
            {
                float step_draw = step_x * 0.46f * 2;

                if (item.count_figures * radius_1 * 2 > h2)
                {
                    step_draw = (h2 - radius_1) / (item.count_figures);
                }

                //----------------------------------------------------------

                if (item.is_top_in_board)
                {
                    /**/
                    canvas.drawBitmap(
                            _select_alternative_xod
                            , item.x_pos - _select_alternative_xod.getWidth() / 2
                            , item.y_pos + step_draw * (item.count_figures - 1) - _select_alternative_xod.getHeight() / 2
                            , null);

                    canvas.drawBitmap(item.color_type == FIGURE_COLOR_BLACK ? _figure_black : _figure_white
                            , item.x_pos - _figure_black.getWidth() / 2
                            , item.y_pos - _figure_black.getHeight() / 2 + step_draw * (item.count_figures - 1)
                            , null);

                    if (item.count_figures > 4)
                    {
                        String _v = String.valueOf(item.count_figures);

                        paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos + step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_2);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos + step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_1);
                    }

                    /*canvas.drawCircle(
                            item.x_pos
                            , item.y_pos + step_draw * (item.count_figures - 1)
                            , radius_1
                            , ( flag_draw )?paint_selected_cell_1:paint_selected_cell_2
                    );*/
                }
                else
                {
                    /**/
                    canvas.drawBitmap(
                            _select_alternative_xod
                            , item.x_pos - _select_alternative_xod.getWidth() / 2
                            , item.y_pos - step_draw * (item.count_figures - 1) - _select_alternative_xod.getHeight() / 2
                            , null);

                    canvas.drawBitmap(item.color_type == FIGURE_COLOR_BLACK ? _figure_black : _figure_white
                            , item.x_pos - _figure_black.getWidth() / 2
                            , item.y_pos - _figure_black.getHeight() / 2 - step_draw * (item.count_figures - 1)
                            , null);

                    if (item.count_figures > 4)
                    {
                        String _v = String.valueOf(item.count_figures);

                        paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos - step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_2);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos - step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_1);
                    }

                        /*canvas.drawCircle(
                                item.x_pos
                                , item.y_pos - step_draw * (item.count_figures - 1)
                                , radius_1
                                , ( flag_draw )?paint_selected_cell_1:paint_selected_cell_2
                        );*/
                }
                //-------------------------------------
            }
        }

        /// RED XODS
        for (int i = 0; i < _cells.size(); i++)
        {
            CellValues item = _cells.get(i);

            if (this.selected_alternative_xods_red.indexOf(item.pos) != -1
                    && item.count_figures > 0)
            {
                float step_draw = step_x * 0.46f * 2;

                if (item.count_figures * radius_1 * 2 > h2)
                {
                    step_draw = (h2 - radius_1) / (item.count_figures);
                }

                //----------------------------------------------------------

                if (item.is_top_in_board)
                {
                    /**/
                    canvas.drawBitmap(
                            _select_alternative_xod_red
                            , item.x_pos - _select_alternative_xod_red.getWidth() / 2
                            , item.y_pos + step_draw * (item.count_figures - 1) - _select_alternative_xod_red.getHeight() / 2
                            , null);

                    canvas.drawBitmap(item.color_type == FIGURE_COLOR_BLACK ? _figure_black : _figure_white
                            , item.x_pos - _figure_black.getWidth() / 2
                            , item.y_pos - _figure_black.getHeight() / 2 + step_draw * (item.count_figures - 1)
                            , null);

                    if (item.count_figures > 4)
                    {
                        String _v = String.valueOf(item.count_figures);

                        paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos + step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_2);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos + step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_1);
                    }

                        /*canvas.drawCircle(
                                item.x_pos
                                , item.y_pos + step_draw * (item.count_figures - 1)
                                , radius_1
                                , ( flag_draw )?paint_selected_cell_1:paint_selected_cell_2
                        );*/
                }
                else
                {
                    /**/
                    canvas.drawBitmap(
                            _select_alternative_xod_red
                            , item.x_pos - _select_alternative_xod_red.getWidth() / 2
                            , item.y_pos - step_draw * (item.count_figures - 1) - _select_alternative_xod_red.getHeight() / 2
                            , null);

                    canvas.drawBitmap(item.color_type == FIGURE_COLOR_BLACK ? _figure_black : _figure_white
                            , item.x_pos - _figure_black.getWidth() / 2
                            , item.y_pos - _figure_black.getHeight() / 2 - step_draw * (item.count_figures - 1)
                            , null);

                    if (item.count_figures > 4)
                    {
                        String _v = String.valueOf(item.count_figures);

                        paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos - step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_2);

                        canvas.drawText(_v
                                , item.x_pos - bound_count_figures.width() / 2
                                , item.y_pos - step_draw * (item.count_figures - 1) +
                                        bound_count_figures.height() / 2
                                , paint_text_count_figures_1);
                    }

                        /*canvas.drawCircle(
                                item.x_pos
                                , item.y_pos - step_draw * (item.count_figures - 1)
                                , radius_1
                                , ( flag_draw )?paint_selected_cell_1:paint_selected_cell_2
                        );*/
                }
                //-------------------------------------
            }
        }

        ///--------------------------------------------------------------
        /// SELECTED FIGURE
        if (selected_cell > 0)
        {
            for (int i = 0; i < _cells.size(); i++)
            {
                CellValues item = _cells.get(i);

                if (item.pos == selected_cell && item.count_figures > 0)
                {
                    if (System.currentTimeMillis() - ts_animation_select > 900)
                    {
                        ts_animation_select = System.currentTimeMillis();
                        flag_draw = !flag_draw;
                    }

                    float step_draw = step_x * 0.46f * 2;

                    if (item.count_figures * radius_1 * 2 > h2)
                    {
                        step_draw = (h2 - radius_1) / (item.count_figures);
                    }

                    if (item.is_top_in_board)
                    {
                        /**/

                        canvas.drawBitmap(
                                _select_figure
                                , item.x_pos - _select_figure.getWidth() / 2
                                , item.y_pos + step_draw * (item.count_figures - 1) - _select_figure.getHeight() / 2
                                , null);

                        canvas.drawBitmap(item.color_type == FIGURE_COLOR_BLACK ? _figure_black : _figure_white
                                , item.x_pos - _figure_black.getWidth() / 2
                                , item.y_pos - _figure_black.getHeight() / 2 + step_draw * (item.count_figures - 1)
                                , null);

                        if (item.count_figures > 4)
                        {
                            String _v = String.valueOf(item.count_figures);

                            paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                            canvas.drawText(_v
                                    , item.x_pos - bound_count_figures.width() / 2
                                    , item.y_pos + step_draw * (item.count_figures - 1) +
                                            bound_count_figures.height() / 2
                                    , paint_text_count_figures_2);

                            canvas.drawText(_v
                                    , item.x_pos - bound_count_figures.width() / 2
                                    , item.y_pos + step_draw * (item.count_figures - 1) +
                                            bound_count_figures.height() / 2
                                    , paint_text_count_figures_1);
                        }

                        /*canvas.drawCircle(
                                item.x_pos
                                , item.y_pos + step_draw * (item.count_figures - 1)
                                , radius_1
                                , ( flag_draw )?paint_selected_cell_1:paint_selected_cell_2
                        );*/
                    }
                    else
                    {
                        /**/

                        canvas.drawBitmap(
                                _select_figure
                                , item.x_pos - _select_figure.getWidth() / 2
                                , item.y_pos - step_draw * (item.count_figures - 1) - _select_figure.getHeight() / 2
                                , null);

                        canvas.drawBitmap(item.color_type == FIGURE_COLOR_BLACK ? _figure_black : _figure_white
                                , item.x_pos - _figure_black.getWidth() / 2
                                , item.y_pos - _figure_black.getHeight() / 2 - step_draw * (item.count_figures - 1)
                                , null);

                        if (item.count_figures > 4)
                        {
                            String _v = String.valueOf(item.count_figures);

                            paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                            canvas.drawText(_v
                                    , item.x_pos - bound_count_figures.width() / 2
                                    , item.y_pos - step_draw * (item.count_figures - 1) +
                                            bound_count_figures.height() / 2
                                    , paint_text_count_figures_2);

                            canvas.drawText(_v
                                    , item.x_pos - bound_count_figures.width() / 2
                                    , item.y_pos - step_draw * (item.count_figures - 1) +
                                            bound_count_figures.height() / 2
                                    , paint_text_count_figures_1);
                        }

                        /*canvas.drawCircle(
                                item.x_pos
                                , item.y_pos - step_draw * (item.count_figures - 1)
                                , radius_1
                                , ( flag_draw )?paint_selected_cell_1:paint_selected_cell_2
                        );*/
                    }

                    /*canvas.drawCircle(
                              item.x_pos
                            , item.y_pos
                            , radius_1
                            , ( flag_draw )?paint_selected_cell_1:paint_selected_cell_2
                    );*/
                    break;
                }
            }
        }

        /*canvas.drawBitmap(_img_left_exit_figure
                , w2_canvas  + offset_x - width_open_part_board - padding_center - _img_left_exit_figure.getWidth() / 2
                , left_center_board.gety() - _img_left_exit_figure.getHeight() / 2
                , null
        );

        canvas.drawBitmap(_img_right_exit_figure
                , w2_canvas + offset_x + width_open_part_board + padding_center - _img_right_exit_figure.getWidth() / 2
                , left_center_board.gety() - _img_right_exit_figure.getHeight() / 2
                , null
        );*/

        /*canvas.drawBitmap(_img_left_exit_figure
                , w2_canvas + offset_x - width_open_part_board - padding_center + padding_inner// + _img_left_exit_figure.getWidth() / 2
                , left_center_board.gety() - _img_left_exit_figure.getHeight() / 2
                , null
        );

        canvas.drawBitmap(_img_right_exit_figure
                , w2_canvas + offset_x + width_open_part_board + padding_center - padding_inner - _img_right_exit_figure.getWidth()// / 2
                , left_center_board.gety() - _img_right_exit_figure.getHeight() / 2
                , null
        );*/

        for (int k1 = 0; k1 < list_buttons.size(); k1++)
        {
            list_buttons.get(k1).draw(canvas);
        }

        ///--------------------------------------------------------------

        //paint.setColor(Color.parseColor("#ffffff"));

        /*canvas.drawLine(   left_center_board.getx()
                         , left_center_board.gety()
                         , left_center_board.getx()
                         , h_canvas
                         , paint );*/
        /*-----------
        PointPP _center_p = get_center_point( left_center_board.getx()
                , left_center_board.gety()
                , left_center_board.getx()
                , left_bottom_board.gety() );

        PointPP _t = get_perpendicular_vector(
                  left_center_board.getx()
                , left_center_board.gety()
                , left_center_board.getx()
                , h_canvas
                , _center_p.getx()
                , _center_p.gety()
                );*/

        /*PointPP _t = new PointPP(
                  (float)( _center_p.getx() * Math.cos( 5 * Math.PI / 180 ) - _center_p.gety() * Math.sin( 5 * Math.PI / 180 ) )
                , (float)( _center_p.gety() * Math.cos( 5 * Math.PI / 180 ) + _center_p.getx() * Math.sin( 5 * Math.PI / 180 ) )
        );*/

        /*paint.setColor(Color.parseColor("#ff0000"));

        //x' = xcosa - ysina
        //y' = xsina + ycosa

        canvas.drawLine(
                  _center_p.getx()
                , _center_p.gety()
                , _center_p.getx() + _t.getx() / _t.Length() * 70 / _t.Length()
                , _center_p.gety() + _t.gety() / _t.Length() * 70 / _t.Length()
                , paint
        );*/

        /*canvas.drawLine(
                  _center_p.getx()
                , _center_p.gety()
                , _t.getx()
                , _t.gety()
                , paint
        );*/

        /*{

            PointPP[] _data = new PointPP[3];

            _data[0] = new PointPP(left_center_board.getx(), left_bottom_board.gety());
            _data[1] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 70 / _t.Length()
                    , _center_p.gety() + _t.gety() / _t.Length() * 70 / _t.Length());
            _data[2] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 150 / _t.Length(), left_center_board.gety());

            //Bezier _b = new Bezier( _data );

            List<PointPP> _k = generateBezierPath(_data[0], _data[2], _data[1], _data[1], 5);

            for (int m = 0; m < _k.size() - 1; m++)
            {
                canvas.drawLine(
                        _k.get(m).getx()
                        , _k.get(m).gety()
                        , _k.get(m + 1).getx()
                        , _k.get(m + 1).gety()
                        , paint
                );
            }
        }

        {

            PointPP[] _data = new PointPP[3];

            _data[0] = new PointPP(left_center_board.getx(), left_bottom_board.gety());
            _data[1] = new PointPP(  _center_p.getx() - _t.getx() / _t.Length() * 50 / _t.Length()
                                   , _center_p.gety() - _t.gety() / _t.Length() * 50 / _t.Length());
            _data[2] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 120 / _t.Length(), left_center_board.gety());

            //Bezier _b = new Bezier( _data );

            List<PointPP> _k = generateBezierPath(_data[0], _data[2], _data[1], _data[1], 5);

            for (int m = 0; m < _k.size() - 1; m++)
            {
                canvas.drawLine(
                        _k.get(m).getx()
                        , _k.get(m).gety()
                        , _k.get(m + 1).getx()
                        , _k.get(m + 1).gety()
                        , paint
                );
            }
        }

        _center_p = get_center_point( left_center_board.getx()
                                    , left_center_board.gety()
                                    , left_center_board.getx()
                                    , left_top_board.gety() );

        {

            PointPP[] _data = new PointPP[3];

            _data[0] = new PointPP(left_center_board.getx(), left_top_board.gety());
            _data[1] = new PointPP(  _center_p.getx() - _t.getx() / _t.Length() * 50 / _t.Length()
                    , _center_p.gety() - _t.gety() / _t.Length() * 50 / _t.Length());
            _data[2] = new PointPP(_center_p.getx() - _t.getx() / _t.Length() * 120 / _t.Length(), left_center_board.gety());

            //Bezier _b = new Bezier( _data );

            List<PointPP> _k = generateBezierPath(_data[0], _data[2], _data[1], _data[1], 5);

            for (int m = 0; m < _k.size() - 1; m++)
            {
                canvas.drawLine(
                        _k.get(m).getx()
                        , _k.get(m).gety()
                        , _k.get(m + 1).getx()
                        , _k.get(m + 1).gety()
                        , paint
                );
            }
        }

        {

            PointPP[] _data = new PointPP[3];

            _data[0] = new PointPP(left_center_board.getx(), left_top_board.gety());
            _data[1] = new PointPP(  _center_p.getx() + _t.getx() / _t.Length() * 70 / _t.Length()
                    , _center_p.gety() + _t.gety() / _t.Length() * 70 / _t.Length());
            _data[2] = new PointPP(_center_p.getx() + _t.getx() / _t.Length() * 150 / _t.Length(), left_center_board.gety());

            //Bezier _b = new Bezier( _data );

            List<PointPP> _k = generateBezierPath(_data[0], _data[2], _data[1], _data[1], 5);

            for (int m = 0; m < _k.size() - 1; m++)
            {
                canvas.drawLine(
                        _k.get(m).getx()
                        , _k.get(m).gety()
                        , _k.get(m + 1).getx()
                        , _k.get(m + 1).gety()
                        , paint
                );
            }
        }*/

        /*canvas.drawLine(
                  left_top_board.getx()
                , left_top_board.gety()
                , right_bottom_board.getx()
                , right_bottom_board.gety()
                , paint
        );

        canvas.drawLine(
                  left_bottom_board.getx()
                , left_bottom_board.gety()
                , right_top_board.getx()
                , right_top_board.gety()
                , paint
        );*/

        //List<PointPP> path_fly = this.from_to(canvas, 24, 9, 20);
        boolean f0 = false;

        synchronized (synch_create_fly_figure)
        {
            if (fly_figure != null)
            {
                if (System.currentTimeMillis() - fly_figure.time_last_draw > fly_figure.step_draw)
                {
                    fly_figure.time_last_draw = System.currentTimeMillis();
                    fly_figure.animation_pos_index += 1;
                }

                if (fly_figure.animation_pos_index < fly_figure._path_points.size())
                {
                    PointPP _tmp_1 = fly_figure._path_points.get(fly_figure.animation_pos_index);

                    canvas.drawBitmap(
                            fly_figure.b
                            , _tmp_1.getx() - fly_figure.b.getWidth() / 2
                            , _tmp_1.gety() - fly_figure.b.getHeight() / 2
                            , null);

                    if (fly_figure.animation_pos_index == fly_figure._path_points.size() - 1)
                    {
                        //action_animation = false;

                        int __from = fly_figure.from;
                        int __to = fly_figure.to;

                        fly_figure = null;

                        action_animation_fly_figure = false;

                        // полет фигуры
                        callback_stop_animation_fly(__from, __to);

                        //f0 = true;

                        //if( slot_dice_animation_all_stop() )
                        {

                        }
                    }
                }
            }
        }

        synchronized (synch_create_fly_figure_out_board)
        {
            if (fly_figure_out_board != null)
            {
                if (System.currentTimeMillis() - fly_figure_out_board.time_last_draw > fly_figure_out_board.step_draw)
                {
                    fly_figure_out_board.time_last_draw = System.currentTimeMillis();
                    fly_figure_out_board.animation_pos_index += 1;
                }

                if (fly_figure_out_board.animation_pos_index < fly_figure_out_board._path_points.size())
                {
                    PointPP _tmp_1 = fly_figure_out_board._path_points.get(fly_figure_out_board.animation_pos_index);

                    canvas.drawBitmap(
                            fly_figure_out_board.b
                            , _tmp_1.getx() - fly_figure_out_board.b.getWidth() / 2
                            , _tmp_1.gety() - fly_figure_out_board.b.getHeight() / 2
                            , null);

                    if (fly_figure_out_board.animation_pos_index == fly_figure_out_board._path_points.size() - 1)
                    {
                        //action_animation = false;

                        int _from = fly_figure_out_board.from;

                        fly_figure_out_board = null;

                        action_animation_fly_figure = false;

                        // полет фигуры
                        callback_stop_animation_fly_out_board(_from);

                        //f0 = true;



                        /*if(    slot_animation_dice.size() == 0
                            || slot_dice_animation_all_stop() )*/
                        {

                        }
                    }
                }
            }
        }

        for (int k = 0; k < slot_animation_dice.size(); k++)
        {
            PlanInfo item = slot_animation_dice.get(k);

            /*if( ! item.stop_animation )
            {
                f = true;
            }*/

            if (item._path_points != null
                    && item.animation_dice_index < item._path_points.size()
                    )
            {
                if (System.currentTimeMillis() - item.time_last_draw > item.step_draw)
                {
                    item.time_last_draw = System.currentTimeMillis();
                    item.animation_dice_index += 1;
                }

                if (item.animation_dice_index < item._path_points.size())
                {
                    PointPP _tmp_1 = item._path_points.get(item.animation_dice_index);
                    Bitmap _tmp_2 = item._plan_draw.get(item.animation_dice_index);

                    canvas.drawBitmap(
                            _tmp_2
                            , _tmp_1.getx() - _tmp_2.getWidth() / 2
                            , _tmp_1.gety() - _tmp_2.getHeight() / 2
                            , null);

                    if (item.animation_dice_index == item._path_points.size() - 1)
                    {
                        item.last_draw = _tmp_2;
                        item.last_draw_original = _tmp_2;
                        item.last_point = _tmp_1;

                        if (item.exec_xod == false)
                        {
                            item.last_draw = MainActivity.changeBitmapContrastBrightness(item.last_draw, 1.6f, 7);
                        }

                        if (item.stop_animation == false) // 1 раз
                        {
                            // окончание анимации кубика
                            callback_stop_animation_dice(item.plan_num, item.id);
                        }

                        item.stop_animation = true;

                        boolean all_stop = true;

                        for (int k0 = 0; k0 < slot_animation_dice.size(); k0++)
                        {
                            PlanInfo item0 = slot_animation_dice.get(k0);

                            if (item0.stop_animation == false)
                            {
                                all_stop = false;
                                break;
                            }
                        }

                        if (all_stop)
                        {
                            callback_stop_animation_all_dice();
                            action_animation_dice = false;
                        }
                    }
                }

            }

            if (item.last_draw != null)
            {
                canvas.drawBitmap(
                        item.last_draw
                        , item.last_point.getx() - item.last_draw.getWidth() / 2
                        , item.last_point.gety() - item.last_draw.getHeight() / 2
                        , null);

                String _v = String.valueOf(item.plan_num);

                paint_text_count_figures_2.getTextBounds(_v, 0, _v.length(), bound_count_figures);

                if (item.exec_xod == false)
                {
                    canvas.drawText(_v
                            , item.last_point.getx() - bound_count_figures.width() / 2
                            , item.last_point.gety() + bound_count_figures.height() / 2
                            , paint_text_count_figures_2);

                    canvas.drawText(_v
                            , item.last_point.getx() - bound_count_figures.width() / 2
                            , item.last_point.gety() + bound_count_figures.height() / 2
                            , paint_text_count_figures_1);
                }
                else
                {
                    canvas.drawText(_v
                            , item.last_point.getx() - bound_count_figures.width() / 2
                            , item.last_point.gety() + bound_count_figures.height() / 2
                            , paint_text_count_figures_2_c);

                    canvas.drawText(_v
                            , item.last_point.getx() - bound_count_figures.width() / 2
                            , item.last_point.gety() + bound_count_figures.height() / 2
                            , paint_text_count_figures_1_c);
                }


            }

        }

        /*if(        ! f
                && slot_animation_dice.size() > 0 )
        {
            if( action_animation == true )
            {


                action_animation = false;
            }
        }

        if(        ! f /*&& slot_animation_dice.size() > 0 &&*//*
                && fly_figure == null
                && fly_figure_out_board == null )
        {
            if( action_animation == true )
            {
                if( ! f0 )
                {
                    callback_stop_animation();
                }
            }

            action_animation = false;
        }*/

        ///-----------------------------------------------------------------------------------------------------
        /// отрисовка контуров для касания и реации на касания
        /*for(int i = 0; i < _cells.size(); i++)
        {
                CellValues item = _cells.get(i);

                float step_draw = step_x * 0.46f * 2;

                if( item.count_figures * radius_1 * 2 > h2 )
                {
                    step_draw = (h2 - radius_1 ) / ( item.count_figures );
                }

                if( item.is_top_in_board )
                {
                    canvas.drawRect(
                              item.x_pos - radius_1
                            , item.y_pos - radius_1
                            , item.x_pos + radius_1
                            , item.y_pos + radius_1 + ( (item.count_figures > 0) ? (step_draw * (item.count_figures - 1)): 0 )
                            , paint_selected_cell_2
                    );
                }
                else
                {
                    canvas.drawRect(
                              item.x_pos + radius_1
                            , item.y_pos + radius_1
                            , item.x_pos - radius_1
                            , item.y_pos - radius_1 - ( (item.count_figures > 0) ? (step_draw * (item.count_figures - 1)): 0 )
                            , paint_selected_cell_2
                    );

                }
        }*/

        if (top_left_image_icon != null)
        {
            canvas.drawBitmap(top_left_image_icon
                    , left_top_board.getx()
                    , left_top_board.gety() - top_left_image_icon.getHeight() - 3 * _scale_px
                    , null);
        }

        if (text_print_top_left.trim().length() > 0)
        {
            paint_text_print_top_left.getTextBounds(text_print_top_left, 0, text_print_top_left.length(), bound_paint_text_print_top_left);

            canvas.drawText(text_print_top_left
                    , left_top_board.getx() + (top_left_image_icon == null ? 0 : top_left_image_icon.getWidth() * 1.1f) //-
                    // bound_paint_text_print_top_left.width() / 2
                    , left_top_board.gety() - (7 * _scale_px) // - bound_paint_text_print_top_left.height()
                    , paint_text_print_top_left);

        }
    }

    public void callback_stop_animation_all_dice() {}
    public void callback_stop_animation_dice(int need_num, long id) {}
    public void callback_stop_animation_fly(int from_cell_pos, int to_cell_pos) {}
    public void callback_stop_animation_fly_out_board(int from_cell_pos) {}
    ///----------------------------------------------------------------------------------------------------
    //public void callback_end_game(int value, int current_xod, int mode_game) {}
    //public void callback_stop_animation() {}

    public void callback_select_cell(CellValues cv) {}

    public void callback_onInitDraw() {}

    public void callback_press_btn(int id) {}

    public void callback_no_select() {}

    class CellValues
    {
        int pos          /// номер по порядку для сетевой игры
        , pos_white    /// позиция путь для белых
        , pos_black;   /// позиция путь для черных
        int count_figures = 0;
        int color_type;
        float x_pos;
        float y_pos;
        boolean is_top_in_board;
        int board_local_pos;
    }

    public class PlanInfo
    {
        public boolean exec_xod = false;
        public Bitmap last_draw = null;
        public Bitmap last_draw_original = null;
        public PointPP last_point = null;
        List<Bitmap> _plan_draw = null;
        List<PointPP> _path_points = null;
        long step_draw = 0;
        long time_last_draw = 0;
        long id;
        int animation_dice_index = 0;
        int plan_num = 0;  /// номер кубика
        boolean stop_animation = false;
    }

    public class PlanAnimationFigure
    {
        Bitmap b;
        List<PointPP> _path_points = null;
        long step_draw = 0;
        long time_last_draw = 0;
        int animation_pos_index = 0;
        int from, to;
    }

    public class PlanAnimationFigureOutBoard
    {
        Bitmap b;
        List<PointPP> _path_points = null;
        long step_draw = 0;
        long time_last_draw = 0;
        int animation_pos_index = 0;
        int from;
    }
    /////////////////////////////////////////////////////////////////////////////////
    /// CALLBACKS ///
}
