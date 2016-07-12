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

    public static boolean checkDBExists(String dbName){

        String url = "jdbc:mysql://"+var.db_host;
        String login = var.db_username;
        String passwd = var.db_psswd;
        Connection cn=null;
        try{
            Class.forName("com.mysql.jdbc.Driver");

            cn = DriverManager.getConnection(url, login, passwd); //Open a connection

            ResultSet resultSet = cn.getMetaData().getCatalogs();

            while (resultSet.next()) {

                String databaseName = resultSet.getString(1);
                if(databaseName.equals(dbName)){
                    return true;
                }
            }
            resultSet.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
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

    public static void createDB()
    {
        String url = "jdbc:mysql://"+var.db_host;
        String login = var.db_username;
        String passwd = var.db_psswd;
        Connection cn=null;
        Statement st;

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            cn= DriverManager.getConnection(url,login,passwd);
            st=cn.createStatement();
            String sql = "-- phpMyAdmin SQL Dump\n" +
                    "-- version 3.4.11.1deb2+deb7u2\n" +
                    "-- http://www.phpmyadmin.net\n" +
                    "--\n" +
                    "-- Client: localhost\n" +
                    "-- Généré le: Mar 12 Juillet 2016 à 01:08\n" +
                    "-- Version du serveur: 5.5.47\n" +
                    "-- Version de PHP: 5.4.45-0+deb7u2\n" +
                    "\n" +
                    "SET SQL_MODE=\"NO_AUTO_VALUE_ON_ZERO\";\n" +
                    "SET time_zone = \"+00:00\";\n" +
                    "\n" +
                    "\n" +
                    "/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n" +
                    "/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;\n" +
                    "/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;\n" +
                    "/*!40101 SET NAMES utf8 */;\n" +
                    "\n" +
                    "--\n" +
                    "-- Base de données: `growonline`\n" +
                    "--\n" +
                    "\n" +
                    "CREATE DATABASE `growonline` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;\n" +
                    "USE `growonline`;"+
                    "-- --------------------------------------------------------\n" +
                    "\n" +
                    "--\n" +
                    "-- Structure de la table `env`\n" +
                    "--\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS `env` (\n" +
                    "  `Date_` datetime NOT NULL,\n" +
                    "  `Temp` double NOT NULL,\n" +
                    "  `Hum` double NOT NULL\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" +
                    "\n" +
                    "-- --------------------------------------------------------\n" +
                    "\n" +
                    "--\n" +
                    "-- Structure de la table `profile`\n" +
                    "--\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS `profile` (\n" +
                    "  `ID` int(11) NOT NULL,\n" +
                    "  `Name` text NOT NULL,\n" +
                    "  `hook` text NOT NULL,\n" +
                    "  PRIMARY KEY (`ID`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n" +
                    "\n" +
                    "--\n" +
                    "-- Contenu de la table `profile`\n" +
                    "--\n" +
                    "\n" +
                    "INSERT INTO `profile` (`ID`, `Name`, `hook`) VALUES\n" +
                    "(0, '', 'hook');\n" +
                    "\n" +
                    "-- --------------------------------------------------------\n" +
                    "\n" +
                    "--\n" +
                    "-- Structure de la table `profiles`\n" +
                    "--\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS `profiles` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `Name` text NOT NULL,\n" +
                    "  `Description` text NOT NULL,\n" +
                    "  `Sunrise` time NOT NULL,\n" +
                    "  `Sunset` time NOT NULL,\n" +
                    "  `Interval` time NOT NULL,\n" +
                    "  `Working_Time` time NOT NULL,\n" +
                    "  `Tank_Capacity` double NOT NULL,\n" +
                    "  `Pump_Flow` double NOT NULL,\n" +
                    "  `Watering_Hour` time NOT NULL,\n" +
                    "  `Water_Amount` double NOT NULL,\n" +
                    "  `Temperature` double NOT NULL,\n" +
                    "  `Humidity` double NOT NULL,\n" +
                    "  `Monday` tinyint(1) NOT NULL,\n" +
                    "  `Tuesday` tinyint(1) NOT NULL,\n" +
                    "  `Wednesday` tinyint(1) NOT NULL,\n" +
                    "  `Thursday` tinyint(1) NOT NULL,\n" +
                    "  `Friday` tinyint(1) NOT NULL,\n" +
                    "  `Saturday` tinyint(1) NOT NULL,\n" +
                    "  `Sunday` tinyint(1) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=28 ;\n" +
                    "\n" +
                    "-- --------------------------------------------------------\n" +
                    "\n" +
                    "--\n" +
                    "-- Structure de la table `users`\n" +
                    "--\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS `users` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `login` text NOT NULL,\n" +
                    "  `hash` text NOT NULL,\n" +
                    "  `email` text NOT NULL,\n" +
                    "  `mobile` text NOT NULL,\n" +
                    "  `alertemail` tinyint(1) NOT NULL,\n" +
                    "  `alertsms` tinyint(1) NOT NULL,\n" +
                    "  `admin` tinyint(1) NOT NULL,\n" +
                    "  `apikey` text NOT NULL,\n" +
                    "  `avatar` text NOT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=14 ;\n" +
                    "\n" +
                    "/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;\n" +
                    "/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;\n" +
                    "/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;\n";
            String[] commands = sql.split(";");

            try {
                for (String s : commands) {
                    st.execute(s);
                }
            } catch (Exception ex) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if ( cn != null )
                try {
            /* Fermeture de la connexion */
                    cn.close();
                } catch ( SQLException ignore ) {
            /* Si une erreur survient lors de la fermeture, il suffit de l'ignorer. */
                }
        }
    }
}
