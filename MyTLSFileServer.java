import javax.net.ssl.*;
import java.io.*;
import java.security.*;

public class MyTLSFileServer {

    // use java key store and load the keystore file server.jks

    private static SSLServerSocketFactory getSSF() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore ks = KeyStore.getInstance("JKS");

            // password for the keystore file using console password
            System.out.print("Enter password for the keystore: ");

            // password for the keystore file
            char[] passphrase = System.console().readPassword();

            ks.load(new FileInputStream("server.jks"), passphrase);
            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory ssf = ctx.getServerSocketFactory();

            return ssf;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        int port = 9999;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        try {
            SSLServerSocketFactory sslServerSocketFactory = getSSF();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            String[] enabledProtocols = { "TLSv1.2", "TLSv1.3" };

            sslServerSocket.setEnabledProtocols(enabledProtocols);
            System.out.println("Server started at port " + port);

            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("Client connected");
                new Thread(new ClientHandler(sslSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final SSLSocket sslSocket;

        public ClientHandler(SSLSocket sslSocket) {
            this.sslSocket = sslSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(sslSocket.getOutputStream(), true);
                String fileName = reader.readLine();
                System.out.println("Client requested file: " + fileName);
                File file = new File(fileName);
                if (file.exists()) {
                    writer.println("File found");
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        writer.println(line);
                    }
                    fileReader.close();
                } else {
                    writer.println("File not found");
                }
                sslSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}