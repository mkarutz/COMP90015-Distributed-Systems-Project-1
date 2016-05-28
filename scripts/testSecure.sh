#!/bin/sh
gnome-terminal -e "java -jar build/libs/Server.jar -lp 4000 -slp 4001 -d"
sleep 2
gnome-terminal -e "java -jar build/libs/Client.jar -rh localhost -rp 4001 -secure"
