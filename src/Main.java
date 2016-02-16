import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main
{
    public static void main(String[] args)
    {
        Initialize();
        int frequency = 5000;//le temps entre chaque tick
        long lastExec=0;//dernier éxécution de la fonction tick
        while(true)
        {
            if(lastExec+frequency<=System.currentTimeMillis())
            {
                tick();
                lastExec=System.currentTimeMillis();
            }
        }
    }

    public static void Initialize()
    {
        Debug.println("INITIALISATION////////////////////");
        System_Function.exec("gpio mode "+var.lamp.getPin()+" out");
        System_Function.exec("gpio mode "+var.fan.getPin()+" out");
        System_Function.exec("gpio mode "+var.pump.getPin()+" out");
        //System_Function.exec("gpio mode "+var.sonde+" in");//]______trucs un peut spécial avec les pins
        System_Function.exec("gpio mode 7 in");            // ]
        var.lamp.set(false);
        var.fan.set(false);
        var.pump.set(false);
        //Debug.println("DEMARRAGE DU STREAM WEBCAM/////////////////////");
        //System_Function.exec("./home/mjpg-streamer/mjpg_streamer -i \"./home/mjpg-streamer/input_uvc.so -n -f 15 -r 480x360 -y\" -o \"./home/mjpg-streamer/output_http.so -n -w ./www\"");//j'ai foutu ça en cron, ça fonctionne mieux
        Environnement.startThread();//on démarre l'écriture de la température et de l'humidité dans la bdd
    }

    public static void tick()//fonction qui gèrera tout et qui sera éxécutée toute les x secondes
    {
        //Debug.println("RECUPERATION DU PROFIL//////////////////////");
        System_Function.getProfile();
        if(Profile.Name.equals("")||Profile.Name==null)
        {
            Debug.println("PROFIL NULL : ABANDON DU TICK");
            return;
        }
        if(System_Function.isDay())//si on est le jour
        {
            //Debug.println("JOUR");
            if(!var.lamp.getState()){var.lamp.set(true);Debug.println("ALLUMAGE DE LA LAMPE");}
            if(!var.fan.getState()){var.fan.set(true);Debug.println("ALLUMAGE DU VENTILATEUR");}
        }
        else//si on est la nuit
        {
            //Debug.println("NUIT");
            if(var.lamp.getState()){var.lamp.set(false);Debug.println("EXTINCTION DE LA LAMPE");}
            if(System_Function.isFanOn()&&!var.fan.getState()){var.fan.set(true);Debug.println("ALLUMAGE DU VENTILATEUR");}
            else if(var.fan.getState()) {var.fan.set(false);Debug.println("EXTINCTION DU VENTILATEUR");}
        }
        /////////////////////////////////////////////////
        //ARROSAGE
        //

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {now = format.parse(format.format(now));} catch (ParseException e) {e.printStackTrace();}//on récupère uniquement les heures et les minutes
        if(Profile.Water_Amount!=0&&!var.pump.getState()&&System_Function.isWaterDay()&&now.after(Profile.Watering_Hour)&&now.before(new Date(Profile.Watering_Hour.getTime()+(long)(Profile.Water_Amount/Profile.Pump_Flow*60000))))
        {
            Debug.println("ARROSAGE!");
            var.pump.set(true);
        }
        else {var.pump.set(false);}
    }
}
