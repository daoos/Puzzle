#!/bin/bash
cur=$(pwd)
cd /root/graduation
echo bash stop.sh
source stop.sh
echo bash start.sh
source start.sh
cd $cur
