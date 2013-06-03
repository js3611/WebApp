#!/bin/bash

tomcat7 stop
cd src
make install
cd ..
tomcat7 start
