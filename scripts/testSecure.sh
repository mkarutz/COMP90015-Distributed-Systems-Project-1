#!/bin/sh
gnome-terminal -e "java -jar build/libs/Server.jar -lp 4000 -slp 4001 -d"
sleep 1
gnome-terminal -e "java -jar build/libs/Server.jar -rh localhost -rp 4001 -d -lp 4010 -slp 4011 -secure"
sleep 2
gnome-terminal -e "java -jar build/libs/Server.jar -rh localhost -rp 4010 -d -lp 4020 -slp 4021"
sleep 2
gnome-terminal -e "java -jar build/libs/Server.jar -rh localhost -rp 4011 -d -lp 4030 -slp 4031 -secure"
# sleep 1
# gnome-terminal -e "java -jar build/libs/Client.jar -rh localhost -rp 4035"
# sleep 1
# gnome-terminal -e "java -jar build/libs/Client.jar -rh localhost -rp 4035 -u aaron"
# sleep 1
# gnome-terminal -e "java -jar build/libs/Client.jar -rh localhost -rp 4035"
