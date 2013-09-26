#!/bin/bash
rm -f *.class
javac -cp ".:sqlite-jdbc-3.7.2.jar:JXGrabKey.jar" Main.java