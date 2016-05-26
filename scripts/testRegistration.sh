#!/bin/sh
gnome-terminal -t server1 -e "java -jar build/libs/Server.jar -lp 4000 -slp 4001 -d"
sleep 2
gnome-terminal -t server2 -e "java -jar build/libs/Server.jar -rh localhost -rp 4001 -d -lp 4010 -slp 4011 -secure"
sleep 2
gnome-terminal -t server3 -e "java -jar build/libs/Server.jar -rh localhost -rp 4011 -d -lp 4020 -slp 4021 -secure"
sleep 2
gnome-terminal -t client1 -e "java -jar build/libs/Client.jar -rh localhost -rp 4021 -u aaron -secure"
sleep 2
gnome-terminal -t server4 -e "java -jar build/libs/Server.jar -rh localhost -rp 4001 -d -lp 4040 -slp 4041 -secure"