# GrowOnline

First of all, if you don't know what GrowOnline is, go check out our [website](http://growonline.fr).

# Hardware
Now that you know what you're going to make you'll need some stuff:
- a raspberry pi (all versions should work)
- a 6 relays board
- a DHT22 sensor
- 6 outlets
- 220V to 5V or 120V to 5V adapter (capable of providing 1A)
- An USB wifi dongle
- some wires, a blade, a soldering iron, screws, bolts, a screwdriver, a drill..

Then, you want to wire the system like that:
![wiring diagramm](https://github.com/NitroOxyde/GrowOnline-Java/blob/master/GrowOnline_bb.png)

You have to connect the probe data wire to GPIO 4.
By default the lamp relay is connected to GPIO 5, the fan to GPIO 4, the pump to GPIO 3, the heater to GPIO 2 and the fogger to GPIO 1.

At this point you may be a bit confused on how to put this all together, so we've made a quick [video](https://vimeo.com/156953965) that should give you and idea of where to start.

After building this, you'll need to install the software.

# Software
Start with installing **raspbian** on your raspberry.

Then connect to the raspberry pi via ssh and type 

`sudo raspi-config`

Select **expand filesystem**, then **finish** and finally **reboot**

Now, plug-in your wifi dongle and configure the wifi by editing wpa_supplicant.conf

 `sudo nano /etc/wpa_supplicant/wpa_supplicant.conf`

Add these lines:
```
network={
    ssid="Your_SSID"
    psk="Your_wifi_password"
}
```

## Quick Install

First make sure you have root acces

`sudo su`

Then, run this command 

`dpkg-reconfigure tzdata && apt-get --assume-yes update && apt-get --assume-yes upgrade && apt-get --assume-yes install git-core && cd /home && git clone git://git.drogon.net/wiringPi && cd wiringPi && git pull origin && ./build && apt-get --assume-yes install apache2 php5 mysql-server libapache2-mod-php5 php5-mysql && cd /home && git clone https://github.com/technion/lol_dht22 && cd lol_dht22 && ./configure && make && cd /var/www/html && rm -rf * && git clone https://github.com/NitroOxyde/GrowOnline-Web-Interface . && sudo nano api/config.php && apt-get --assume-yes install jsvc && cd /home && wget http://growonline.fr/GrowOnline-Java.jar && mkdir /home/err && mkdir /home/log && cd /etc/init.d && wget http://growonline.fr/growonline && chmod 755 /etc/init.d/growonline && update-rc.d growonline defaults && reboot`


### Detailed Install

First, make sure your raspberry pi is on time 

`sudo dpkg-reconfigure tzdata`

Update it:

`sudo apt-get update`

`sudo apt-get upgrade`

#### Dependencies
First, install git `sudo apt-get install git-core`

Then, install wiring pi, this will enable us to control the relays

`cd /home`

`sudo git clone git://git.drogon.net/wiringPi`

`cd wiringPi`

`sudo git pull origin`

`sudo ./build`

Now, install the web server `sudo apt-get install apache2 php5 mysql-server libapache2-mod-php5 php5-mysql`

You will be asked to type a password for the database, write it down or remember it.

To read the temperature and the humidity from the sensor, we'll need to install the DHT22 library

`cd /home`

`sudo git clone https://github.com/technion/lol_dht22`

`cd lol_dht22`

`sudo ./configure`

`sudo make`

Make sure you can read data from the probe by typing `sudo ./loldht`

#### The Web Interface
Now that your raspberry pi is set and all the dependencies installed, you need the Web Interface.

`cd /var/www/html`

`sudo rm -rf *`

`sudo git clone https://github.com/NitroOxyde/GrowOnline-Web-Interface .`

Then edit the config file to match your database login and password. (The one you had to remember)

`sudo nano api/config.php`

#### The Java Deamon
Now, you need the java deamon to control all your physical equipment (lamp, fan, pump...)

First, install jsvc, it will let our java program act as a deamon 

`sudo apt-get install jsvc`

Then download the deamon

`cd /home`

`sudo wget http://growonline.fr/GrowOnline-Java.jar`

`sudo mkdir /home/err`

`sudo mkdir /home/log`

Now download the script to launch the deamon

`cd /etc/init.d`

`sudo wget http://growonline.fr/growonline`

`sudo chmod 755 /etc/init.d/growonline`

`sudo update-rc.d growonline defaults`

`sudo /etc/init.d/growonline start`

## You are done !

Connect to your raspberry pi through your web browser and follow the instructions !
