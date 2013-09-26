#!/bin/bash
rm -f *.class
javac -cp ".:sqlite4java.jar:JXGrabKey.jar" Main.java