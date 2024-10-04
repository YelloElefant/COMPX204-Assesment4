import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.*;
import java.util.*;

public class MyTLSFileServer {
    public static void main(String[] args) {
        int port = 9999;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory
                    .getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
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