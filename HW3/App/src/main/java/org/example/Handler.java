package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Handler implements Runnable {

    private final static String NotFoundResponse = "<HTML>" +
            "<HEAD><TITLE>" + "Endpoint Not Found</TITLE></HEAD>" +
            "<BODY>Not Found(404)</BODY></HTML>";
    private final static String BadRequestResponse = "<HTML>" +
            "<HEAD><TITLE>" + "Bad request</TITLE></HEAD>" +
            "<BODY>Bad request(400)\nverify that your request is correct</BODY></HTML>";

    private final static String FileNotFoundResponse = "<HTML>" +
            "<HEAD><TITLE>" + "File Not Found</TITLE></HEAD>" +
            "<BODY>Not Found(404)</BODY></HTML>";
    private final static String WrongMethodResponse = "<HTML>" +
            "<HEAD><TITLE>" + "method not allowed</TITLE></HEAD>" +
            "<BODY>Forbidden(403)</BODY></HTML>";
    private static final Path fileDir = Paths.get("src/main/resources/");

    public Handler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Processing request...");
            String query = in.readLine();
            if (query == null) {
                sendResponse(404, "Not found", "text/html", new ByteArrayInputStream(NotFoundResponse.getBytes()));
                out.close();
                return;
            }
            String[] query_splited = query.split(" ");
            if (query_splited.length < 2) {
                sendResponse(404, "Not found", "text/html", new ByteArrayInputStream(NotFoundResponse.getBytes()));
            } else {
                if (!query_splited[0].equals("GET")) {
                    sendResponse(403, "forbidden", "text/html", new ByteArrayInputStream(WrongMethodResponse.getBytes()));
                    out.close();
                    return;
                }
                String[] params = query_splited[1].split("=");
                if (params.length < 2 || !params[0].equals("/?filename")) {
                    System.out.println(params[0]);
                    sendResponse(400, "Bad Request", "text/html", new ByteArrayInputStream(BadRequestResponse.getBytes()));
                    out.close();
                    return;
                }
                String filename = params[1];
                File f = new File(fileDir + "/" + filename);
                if (!f.exists()) {
                    sendResponse(404, "Not found", "text/html", new ByteArrayInputStream(FileNotFoundResponse.getBytes()));
                } else {
                    sendResponse(200, "OK", "text", new FileInputStream(fileDir + "/" + filename));
                }
            }
            out.flush();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Socket socket;
    private OutputStream out;
    private BufferedReader in;

    private String buildHeaders(int status_code, String status, String content_type) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.0 ");
        builder.append(status_code);
        builder.append(" ");
        builder.append(status);
        builder.append("\r\n");
        builder.append("Content-Type: ");
        builder.append(content_type);
        builder.append("\r\n");
        builder.append("\r\n");
        return builder.toString();
    }

    private void sendResponse(int status_code, String status, String content_type, InputStream content) {
        System.out.println("sending response " + status_code + " " + status);
        InputStream result = new SequenceInputStream(new ByteArrayInputStream(buildHeaders(status_code, status, content_type).getBytes()), content);
        try {
            result.transferTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Request processed");
        }
    }
}
