@REM This script is used to run the client
@REM It sets the truststore and truststore password
@REM It then runs the client

@REM %1 is the truststore password
@REM %2 is the server name
@REM %3 is the port number
@REM %4 is the file name

java -Djavax.net.ssl.trustStore=ca-cert.jks -Djavax.net.ssl.trustStorePassword=%1 MyTLSFileClient.java %2 %3 %4 