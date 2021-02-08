package games2d.com.nards;

import java.util.ArrayList;
import java.util.List;

public class AppSettings
{
    public static final int OPT_MY_GENDER           = 0x01;
    public static final int OPT_TYPE_SUBSCRIPTION   = 0x02;
    public static final int OPT_VIBRO               = 0x03;
    public static final int OPT_SOUND               = 0x04;
    public static final int OPT_SERACH_GENDER       = 0x05;


    public static final int OPT_VALUE_NOT_SET          = 0x00;
    public static final int OPT_VALUE_GENDER__FEMALE   = 0x01;
    public static final int OPT_VALUE_GENDER__MALE     = 0x02;

    public static final int OPT_VALUE_TYPE_SUBSCRIPTION_FREE     = 0x01;
    public static final int OPT_VALUE_TYPE_SUBSCRIPTION_PAY      = 0x02;

    public static final int OPT_VALUE_SEARCH_GENDER_ALL          = 0x01;
    public static final int OPT_VALUE_SEARCH_GENDER_FEMALE       = 0x02;
    public static final int OPT_VALUE_SEARCH_GENDER_MALE         = 0x03;

    public int my_gender          = OPT_VALUE_NOT_SET;
    public int type_subscription  = OPT_VALUE_NOT_SET;
    public boolean vibro = true;
    public boolean sound = true;
    public int serach_type = OPT_VALUE_SEARCH_GENDER_ALL;

    public AppSettings()
    {

    }

}
