if exist *.class del *.class
javac Main.java && java -classpath ".;sqlite-jdbc-3.7.2.jar;jintellitype-1.3.8.jar" Main