package com.server;

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

public class Server{
    public static void main(String[] args) {
        try {
            // Create the https server to port 8001 with default logger
            HttpsServer server = HttpsServer.create(new InetSocketAddress(8001),0);
            
            // Create sslContext
            SSLContext sslContext = coordinateServerSSLContext(args[0], args[1]);

            // Configure the https server
            server.setHttpsConfigurator (new HttpsConfigurator(sslContext) {
                @Override
                public void configure (HttpsParameters params) {
                    InetSocketAddress remote = params.getClientAddress();
                    SSLContext c = getSSLContext();
                    SSLParameters sslparams = c.getDefaultSSLParameters();
                    params.setSSLParameters(sslparams);
                }
            });

            // Create basic authenticator
            UserAuthenticator authenticator = new UserAuthenticator();

            // Create http contexts
            HttpContext coordinateContext = server.createContext("/coordinates", new CoordinatesHandler());
            coordinateContext.setAuthenticator(authenticator);

            HttpContext commentContext = server.createContext("/comment", new CommentHandler());
            commentContext.setAuthenticator(authenticator);

            server.createContext("/registration", new RegistrationHandler(authenticator));
            
            // Create a default executor
            server.setExecutor(Executors.newCachedThreadPool());
            
            // Open the database connection
            CoordinateDatabase.getInstance().open("coordinate.db");
            
            // Start the server
            server.start();

            Console input = System.console();
            boolean running = true;
            while (running) {
                String inpuString = input.readLine();
                if (inpuString.equals("/quit")) {
                    running = false;
                    
                    // Stop the server
                    server.stop(3);
                    // Close the database connection
                    CoordinateDatabase.getInstance().close();
                }
            }

        } catch (FileNotFoundException e) {
            // Certificate file not found!
            System.out.println("Certificate not found!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SSLContext coordinateServerSSLContext(String keystore, String password) throws Exception {
        char[] passphrase = password.toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystore), passphrase);
            
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);
            
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
            
        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return ssl;
    }
}
