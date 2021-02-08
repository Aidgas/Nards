package games2d.com.nards;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.ImageView;

/**
 * Created by sk on 22.09.16.
 */
public class CImageButton
{
    public static int STATUS_NONE    = 0;
    public static int STATUS_PRESSED = 1;

    private Bitmap img_normal;
    private Bitmap img_normal_pressed;
    private int id;
    private float pos_x, pos_y;
    private int status = 0;
    public boolean display = false;

    public CImageButton(int id_btn, float _pos_x, float _pos_y, Bitmap _img_normal, Bitmap _img_pressed)
    {
        img_normal = _img_normal;
        img_normal_pressed = _img_pressed;

        pos_x = _pos_x;
        pos_y = _pos_y;

        id = id_btn;
    }

    public int getId()
    {
        return this.id;
    }

    public void setStatus(int new_status)
    {
        status = new_status;
    }

    public void setBitmapNormal(Bitmap img)
    {
        img_normal = img;
    }

    public void setBitmapPressed(Bitmap img)
    {
        img_normal_pressed = img;
    }

    public boolean test_pressed(float x, float y)
    {
        if(
                ( x > pos_x && x < pos_x + img_normal.getWidth() )
             && ( y > pos_y && y < pos_y + img_normal.getHeight() )
        )
        {
            return true;
        }

        return false;
    }

    public void event_pressed()
    {
        callback_pressed();
    }

    public void draw(Canvas canvas)
    {
        if( ! display)
        {
            return;
        }

        if( status == CImageButton.STATUS_NONE )
        {
            canvas.drawBitmap(img_normal
                    , pos_x
                    , pos_y
                    , null
            );
        }
        else
        {
            canvas.drawBitmap(img_normal_pressed
                    , pos_x
                    , pos_y
                    , null
            );
        }

    }

    public float getPosX()
    {
        return pos_x;
    }

    public float getPosY()
    {
        return pos_y;
    }

    public float getCenterPosX()
    {
        return pos_x + img_normal.getWidth() / 2;
    }

    public float getCenterPosY()
    {
        return pos_y + img_normal.getHeight() / 2;
    }

    public void callback_pressed()
    {

    }
}
