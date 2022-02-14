package com.server;

import com.sun.net.httpserver.*;

import java.net.InetSocketAddress;

public class Server{
    public static void main(String[] args) throws Exception {
        //create the http server to port 8001 with default logger
        HttpServer server = HttpServer.create(new InetSocketAddress(8001),0);
        //create context that defines path for the resource, in this case a "help"
        server.createContext("/coordinates", new ServerHttpHandler());
        // creates a default executor
        server.setExecutor(null);
        server.start();
    }
}
