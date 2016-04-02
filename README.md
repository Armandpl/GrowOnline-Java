# GrowOnline
[This tutorial is a work in progress, you can expect it to be finished by the end of next week]

First of all, if you don't know what GrowOnline is, go check out our [website](http://growonline.fr).

## Hardware
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

At this point you may be a bit confused on how to put this all together, so we've made a quick [video](https://vimeo.com/156953965) that should give you and idea of where to start.

After building this, you'll need to install the software.

## Software
Start with installing **raspbian** on your raspberry.
#### Raspberry Pi Configuration

Connect to the raspberry pi via ssh and type `raspi-config`

Select **expand filesystem**, then **finish** and finally **reboot** `sudo reboot`

Now, plug-in your wifi dongle and configure the wifi by editing wpa_supplicant.conf

 `sudo nano /etc/wpa_supplicant/wpa_supplicant.conf`

Add these lines:
```
network={
    ssid="Your_SSID"
    psk="Your_wifi_password"
}
```

Then, make sure your raspberry pi is on time `sudo dpkg-reconfigure tzdata`

Change your root password `sudo passwd root`

Update the raspberry pi:

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

`make`

Make sure you can read data from the probe by typing `sudo ./loldht`

Finally, install jsvc, it will let our java program act as a deamon
`sudo apt-get install jsvc`

#### The Web Interface
Now that your raspberry pi is set and all the dependencies installed,
