import java.sql.Time;
import java.util.Date;

public class Status
{
    public static String Name;
    public static Time Sunrise;
    public static Time Sunset;
    public static Time Interval;
    public static Time Working_Time;
    public static double Tank_Capacity;
    public static double Pump_Flow;
    public static Time Watering_Hour;
    public static double Water_Amount;
    public static double Temperature;
    public static double Humidity;
    public static boolean[] Water_Days=new boolean[7];
    public static double LastTemp=0;
    public static double LastHum;
}
