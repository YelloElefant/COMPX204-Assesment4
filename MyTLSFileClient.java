import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.util.*;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class MyTLSFileClient {
    static String host = "localhost";
    static int port = 0;

    public static void main(String[] args) {
        if (args.length > 0) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            System.out.println("Usage: java MyTLSFileClient <host> <port>");
            System.exit(1);
        }

        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(sslSocket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            // set up HTTPS-style checking of HostName _before_
            // the handshake
            SSLParameters params = new SSLParameters();
            params.setEndpointIdentificationAlgorithm("HTTPS");
            sslSocket.setSSLParameters(params);

            sslSocket.startHandshake(); // explicitly starting the TLS handshake

            String fileName = args[2];
            writer.println(fileName);
            System.out.println("Requesting file: " + fileName);
            // get the X509Certificate for this session
            SSLSession session = sslSocket.getSession();
            X509Certificate cert = (X509Certificate) session.getPeerCertificates()[0];

            // extract the CommonName, and then compare
            String commonName;
            try {
                commonName = getCommonName(cert);
                System.out.println("Common Name: " + commonName);
            } catch (InvalidNameException e) {
                e.printStackTrace();
            }

            sslSocket.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    static String getCommonName(X509Certificate cert) throws InvalidNameException {
        String name = cert.getSubjectX500Principal().getName();
        LdapName ln = new LdapName(name);
        String cn = null;

        // Rdn: Relative Distinguished Name
        for (Rdn rdn : ln.getRdns())
            if ("CN".equalsIgnoreCase(rdn.getType()))
                cn = rdn.getValue().toString();
        return cn;
    }
}
