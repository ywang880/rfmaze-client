SET LIB_DIR=C:\opt\rfmaze\WebContent\WEB-INF\lib

java -Dcom.sun.management.jmxremote ^
-Dcom.sun.management.jmxremote.port=9890 ^
-Dcom.sun.management.jmxremote.local.only=false ^
-Dcom.sun.management.jmxremote.authenticate=false ^
-Dcom.sun.management.jmxremote.ssl=false ^
-classpath %LIB_DIR%\commons-net-3.3.jar;%LIB_DIR%\log4j-1.2.17.jar;c:\opt\rfmaze\build\classes com.rfview.maze.RFMazeserverAgent %1 %2






































