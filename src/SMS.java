import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;

public class SMS
{
  public static void startThread()//fonction qu'on lance au démrage, elle s'occupera d'écrire la température et l'humidité dans la bdd
  {
      Debug.println("Démarage du thread de notifications SMS");
                  new Thread() { public void run()
                  {
                      int frequency = 36000;//le temps entre chaque tick
                      long lastExec=0;//dernier éxécution de la fonction
                      while(true)
                      {
                          if(lastExec+frequency<=System.currentTimeMillis())
                          {
                              SMS.tick();
                              lastExec=System.currentTimeMillis();
                          }
          }
      }}.start();
  }
  public static void tick()//fonction pour écrire la température et l'humidité dans la DB
  {
      boolean temp=false;
      boolean hum=false;
      if(Math.abs(Status.Temperature-Status.LastTemp)>2){temp=true;}//temperature superieure ou inferieure à la température souhaitée de 2°C
      if(Math.abs(Status.Humidity-Status.LastHum)>10){hum=true;}//temperature superieure ou inferieure à la température souhaitée de 10%
      String notification="";
      if(temp){notification+="GrowOnline\nProblème de température dans l'espace de culture\nTempérature souhaitée:"+Status.Temperature+"\nTempérature actuelle:"+Status.LastTemp;}
     /* if(hum){
          notification+=(temp?"":"Growonline\n");
          notification+="Problème d'humidité dans l'espace de culture" +
              "\nHumidité souhaitée:"+Status.Humidity+
              "\nHumidité actuelle:"+Status.LastHum;}*/
      if(!notification.equals("")){sendSMSNotification(notification);}
  }
    public static void sendSMSNotification(String txt)
    {
        String url = "jdbc:mysql://"+var.db_host+"/"+var.db_name;
        String login = var.db_username;
        String passwd = var.db_psswd;
        Connection cn=null;
        Statement st=null;
        Statement stmt = null;
        String query = "SELECT * FROM users";
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            cn= DriverManager.getConnection(url, login, passwd);
            stmt = cn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
            {
                if(rs.getInt("alertsms")==1)
                {
                    sendSMS(txt,rs.getString("apikey"));
                }
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
    public static void sendSMS(String txt, String url)
    {
        try
        {
            String httpsURL = url+URLEncoder.encode(txt, "UTF-8");
            URL myurl = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
            con.setRequestMethod("GET");

            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);
            in.close();

        }catch (IOException e){e.printStackTrace();}
    }
}
