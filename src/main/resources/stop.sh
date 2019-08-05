#!/bin/bash  
PID=$(ps -ef | grep route.jar | grep -v grep | awk '{ print $2 }')  
if [ -z "$PID" ]  
then  
    echo route is already stopped  
else  
    echo route stop successs!
    kill $PID  
fi