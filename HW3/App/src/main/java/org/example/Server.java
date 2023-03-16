package org.example;
import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private ServerSocket serverSocket;

    ThreadPoolExecutor handlers;
    public void start(int port, int threads_count) throws IOException {
        handlers = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads_count);
        serverSocket = new ServerSocket(port);
        while(true){
            handlers.execute(new Handler(serverSocket.accept()));
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        if(args.length < 2){
            return;
        }
        int threads = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[0]);
        Server server = new Server();
        server.start(port, threads);
    }
}
