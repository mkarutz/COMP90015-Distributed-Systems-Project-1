#!/bin/sh
gnome-terminal -t server0 -e "java -jar build/libs/Server.jar -lp 4000 -slp 4001 -d"
sleep 2
gnome-terminal -t server1 -e "java -jar build/libs/Server.jar -lp 4010 -slp 4011 -rh localhost -rp 4001 -d"
sleep 2
gnome-terminal -t server2 -e "java -jar build/libs/Server.jar -lp 4020 -slp 4021 -rh localhost -rp 4001 -d"
