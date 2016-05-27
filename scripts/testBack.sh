#!/bin/sh
gnome-terminal -t server0 -e "java -jar build/libs/Server.jar -lp 4000 -slp 4001 -d"
sleep 2
gnome-terminal -t server1 -e "java -jar build/libs/LegacyServer.jar -rh localhost -rp 4000 -lp 4010 -d"
sleep 2
gnome-terminal -t server2 -e "java -jar build/libs/LegacyServer.jar -rh localhost -rp 4010 -lp 4020 -d"
sleep 2
gnome-terminal -t server3 -e "java -jar build/libs/LegacyServer.jar -rh localhost -rp 4010 -lp 4030 -d"