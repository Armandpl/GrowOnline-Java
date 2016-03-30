import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class System_Function
{
    public static String getCurrentProfile()
    {
        String url = "jdbc:mysql://"+var.db_host+"/"+var.db_name;
        String login = var.db_username;
        String passwd = var.db_psswd;
        Connection cn=null;

        Statement stmt = null;
        String query = "SELECT Name FROM profile WHERE ID='0';";
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            cn= DriverManager.getConnection(url, login, passwd);
            stmt = cn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
            {
                return rs.getString("Name");
            }
        } catch (SQLException e ) {e.printStackTrace();}
        catch (ClassNotFoundException e) {e.printStackTrace();}
        finally
        {
            if (stmt != null)
            {
                try {stmt.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return "";
    }
    public static void getProfile()
    {
        String url = "jdbc:mysql://"+var.db_host+"/"+var.db_name;
        String login = var.db_username;
        String passwd = var.db_psswd;
        Connection cn=null;

        Statement stmt = null;
        String query = "SELECT * FROM profiles WHERE Name='"+getCurrentProfile()+"'";
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            cn= DriverManager.getConnection(url, login, passwd);
            stmt = cn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
            {
                Status.Name = rs.getString("Name");
                Status.Sunrise = rs.getTime("Sunrise");
                Status.Sunset = rs.getTime("Sunset");
                Status.Interval = rs.getTime("Interval");
                Status.Working_Time = rs.getTime("Working_Time");
                Status.Tank_Capacity = rs.getDouble("Tank_Capacity");
                Status.Pump_Flow = rs.getDouble("Pump_Flow");
                Status.Watering_Hour = rs.getTime("Watering_Hour");
                Status.Water_Amount = rs.getDouble("Water_Amount");
                Status.Temperature = rs.getDouble("Temperature");
                Status.Humidity = rs.getDouble("Humidity");
                Status.Water_Days[0]= rs.getInt("Monday") == 1;
                Status.Water_Days[1]= rs.getInt("Tuesday") == 1;
                Status.Water_Days[2]= rs.getInt("Wednesday") == 1;
                Status.Water_Days[3]= rs.getInt("Thursday") == 1;
                Status.Water_Days[4]= rs.getInt("Friday") == 1;
                Status.Water_Days[5]= rs.getInt("Saturday") == 1;
                Status.Water_Days[6]= rs.getInt("Sunday") == 1;
            }
        } catch (SQLException e ) {e.printStackTrace();}
        catch (ClassNotFoundException e) {e.printStackTrace();}
        finally
        {
            if (stmt != null)
            {
                try {stmt.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }

    public static boolean isDay()
    {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {now = format.parse(format.format(now));} catch (ParseException e) {e.printStackTrace();}//on récupère uniquement les heures et les minutes
        if(Status.Sunrise== Status.Sunset){return false;}
        return now.after(Status.Sunrise)&&now.before(Status.Sunset);
    }

    public static boolean isWaterDay()
    {
        GregorianCalendar calendar =new GregorianCalendar();
        calendar.setTime(new Date());
        int today = calendar.get(calendar.DAY_OF_WEEK);

        if(today==GregorianCalendar.MONDAY&& Status.Water_Days[0]){return true;}
        else if(today==GregorianCalendar.TUESDAY&& Status.Water_Days[1]){return true;}
        else if(today==GregorianCalendar.WEDNESDAY&& Status.Water_Days[2]){return true;}
        else if(today==GregorianCalendar.THURSDAY&& Status.Water_Days[3]){return true;}
        else if(today==GregorianCalendar.FRIDAY&& Status.Water_Days[4]){return true;}
        else if(today==GregorianCalendar.SATURDAY&& Status.Water_Days[5]){return true;}
        else if(today==GregorianCalendar.SUNDAY&& Status.Water_Days[6]){return true;}
        return false;

    }

    public static boolean isFanOn()
    {
        ArrayList<Date> ON = new ArrayList<Date>();
        ArrayList<Date> OFF = new ArrayList<Date>();

        SimpleDateFormat formater = new SimpleDateFormat("HH:mm");
        String[] s = formater.format(Status.Interval).split(":");
        int Hour_I = Integer.parseInt(s[0]);
        int Min_I = Integer.parseInt(s[1]);

        s = formater.format(Status.Working_Time).split(":");
        int Hour_W = Integer.parseInt(s[0]);
        int Min_W = Integer.parseInt(s[1]);

        if( Hour_W==0&&Min_W==0 ){return false;}
        if( Hour_I==0&&Min_I==0 ){return true;}

        int current=0;
        while (current<1440)
        {
            try
            {
                Date on = formater.parse(current/60+":"+current%60);
                ON.add(on);
            } catch (ParseException e) {e.printStackTrace();}

            try
            {
                Date off = formater.parse( (((current+Min_W)/60)+Hour_W) + ":" + (current+Min_W)%60);
                if(current+Hour_W*60+Min_W>=1440){OFF.add(formater.parse("23:59"));}
                else{OFF.add( off );}
            } catch (ParseException e) {e.printStackTrace();}
            current+=Hour_I*60+Hour_W*60+Min_I+Min_W;
        }

        formater = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        try {now = formater.parse(formater.format(now));} catch (ParseException e) {e.printStackTrace();}//on récupère uniquement les heures et les minutes et les secondes
        for (int i=0; i<ON.size()-1;i++)
        {
            if( now.after(ON.get(i)) && now.before(OFF.get(i)) )
            {
                return true;
            }
        }
        return  false;
    }

    public static String getDATETIME()
    {
        Date now = new Date();
        //SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formater = new SimpleDateFormat("yyyy");
        String annee=formater.format(now);
        formater = new SimpleDateFormat("MM");
        String month=formater.format(now);//pour pallier au décalage crée par le fait que l'on compte soit à partir de 0 soit à partir de 1
        formater = new SimpleDateFormat("-dd HH:mm:ss");

        return annee+"-"+month+formater.format(now);
    }

    public static String[] getTempHum()
    {
        try
        {
            String result = exec_result("./lol_dht22/loldht");
            return new String[]{reg.s("(Temperature = )(.*?)(\\*C)", result)[0].replace("Temperature = ", "").replace("*C", "").replace(" ", ""),reg.s("(Humidity = )(.*?)(%)", result)[0].replace("Humidity = ", "").replace("%", "").replace(" ", "")};
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        return new String[]{"error","error"};
    }

    private static boolean procDone(Process p)
    {
        try
        {
            int v = p.exitValue();
            return true;
        }
        catch (IllegalThreadStateException e) {}
        return false;
    }

    public static String exec_result(String command)
    {
        try
        {
            String result = "";
            Process p = null;
            try
            {
                p = Runtime.getRuntime().exec(command);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int count = 0;
            while (!procDone(p))
            {
                try
                {
                    String s;
                    while ((s = stdInput.readLine()) != null)
                    {
                        count++;
                        result = result + s + "\n";
                    }
                }
                catch (IOException e) {}
            }
            try
            {
                stdInput.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return result;
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        return "";
    }

    public static void exec(String command)
    {
        try
        {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(command);
            p.waitFor();
        }
        catch (Exception e)
        {
            Debug.println("erreur d'execution " + command + e.toString());
        }
    }
}
