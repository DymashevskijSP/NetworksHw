package org.example;
import java.io.*;
import java.net.*;
public class Client {
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    public void start(String ip, int port, String filename) throws IOException {
        clientSocket = new Socket(ip, port);
        out = clientSocket.getOutputStream();
        out.write(("GET /?filename=" + filename + " HTTP/1.1\n").getBytes());
        in = clientSocket.getInputStream();
        out.flush();
        System.out.println("aboba");
        out.flush();
        in.transferTo(System.out);
        out.close();
        in.close();
        clientSocket.close();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    public static void main(String[] args) throws IOException {
        if(args.length < 2){
            return;
        }
        int port = Integer.parseInt(args[0]);
        String filename = args[1];
        Client client = new Client();
        client.start("127.0.0.1", port, filename);
        client.stopConnection();
    }
}
