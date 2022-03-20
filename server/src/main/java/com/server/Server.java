package com.server;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        final String dbName = "coordinate.db";
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
            HttpContext coordinateContext = server.createContext(
                "/coordinates", new CoordinatesHandler());
            coordinateContext.setAuthenticator(authenticator);

            HttpContext commentContext = server.createContext("/comment", new CommentHandler());
            commentContext.setAuthenticator(authenticator);

            server.createContext("/registration", new RegistrationHandler(authenticator));
            
            // Create a default executor
            server.setExecutor(Executors.newCachedThreadPool());
            
            // Open the database connection
            CoordinateDatabase.getInstance().open(dbName);
            
            // Start the server
            server.start();

            Console input = System.console();
            boolean running = true;
            while (running) {
                String inpuString = input.readLine();
                if (inpuString.equals("/quit")) {
                    running = false;
                    
                    System.out.println("Stopping server...");
                    // Stop the server
                    server.stop(3);
                    // Close the database connection
                    CoordinateDatabase.getInstance().close();

                    System.out.println("Server stopped");
                } else if (inpuString.equals("/backup")) {
                    // Stop the server and save a backup of the database
                    
                    running = false;

                    System.out.println("Stopping server...");
                    // Stop the server
                    server.stop(3);
                    
                    // Close the database connection
                    CoordinateDatabase.getInstance().close();

                    File dbFile = new File(".", dbName);
                    Path originalsPath = dbFile.toPath();
                    String backupPath = originalsPath.toString() + ".bak";
                    Files.copy(dbFile.toPath(), Paths.get(backupPath),
                        StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("Server stopped");
                    System.out.println("Database backup saved to " + backupPath);
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

    private static SSLContext coordinateServerSSLContext(String keystore, String password)
            throws Exception {
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
