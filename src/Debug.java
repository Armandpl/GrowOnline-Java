import java.text.SimpleDateFormat;
import java.util.Date;

public class Debug {
    public static boolean debug = true;
    public static void println(String str)
    {
        SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        if(debug){System.out.println("["+formater.format(now)+"]"+str);}
    }
}
