import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Environnement
{
  public static void startThread()//fonction qu'on lance au démrage, elle s'occupera d'écrire la température et l'humidité dans la bdd
  {
      Debug.println("Démarage du thread de récupération de la température et de l'humidité");
                  new Thread() { public void run()
                  {
                      int frequency = 5000;//le temps entre chaque tick
                      long lastExec=0;//dernier éxécution de la fonction
                      while(true)
                      {
                          if(lastExec+frequency<=System.currentTimeMillis())
                          {
                              Environnement.writeTempHum();
                              lastExec=System.currentTimeMillis();
                          }
          }
      }}.start();
  }
  public static void writeTempHum()//fonction pour écrire la température et l'humidité dans la DB
  {
      String url = "jdbc:mysql://localhost/growonline";
      String login = credentials.bdd_login;
      String passwd = credentials.bdd_psswd;
      Connection cn=null;
      Statement st=null;

      String result[] = System_Function.getTempHum();
      Debug.println("Upload de la température et de l'humidité dans la BDD");
      Debug.println("Temp:"+result[0]);
      Debug.println("Hum:"+result[1]);
      Status.LastTemp=Double.parseDouble(result[0]);
      Status.LastHum=Double.parseDouble(result[1]);

      //pour pallier aux grosses erreurs de la sonde ( cf -3000 degrès... )
      if(Double.parseDouble(result[0])<-20){result[0]="0";}

      try
      {
          Class.forName("com.mysql.jdbc.Driver");
          cn= DriverManager.getConnection(url,login,passwd);
          st=cn.createStatement();
          String sql = "INSERT INTO `growonline`.`env` (`date_`, `temp`, `hum`) VALUES ('"+System_Function.getDATETIME()+"', '"+result[0]+"', '"+result[1]+"');";
          st.execute(sql);
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
