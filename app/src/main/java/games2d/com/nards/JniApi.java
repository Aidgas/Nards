package games2d.com.nards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by root on 18.07.15.
 */
public class JniApi
{
    static
    {
        System.loadLibrary("cdata");
    }

    public static native byte[] SXOR(byte[] data);

    public static native byte[] dataEncrypt(byte[] data, String pass);

    public static native String dfp1();
    
    
    
    public static native int port1();
    public static native int port2();

    public static native int xcrc32(byte[] data);

    public static native byte[] dataEncrypt1(byte[] data);
    public static native byte[] dataDecrypt1(byte[] data);

    public static native byte[] dataEncrypt2(byte[] data, byte[] xor_key);
    public static native byte[] dataDecrypt2(byte[] data, byte[] xor_key);

    public static native String f1();
}
