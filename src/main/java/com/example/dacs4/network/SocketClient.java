package com.example.dacs4.network;

import com.example.dacs4.App;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public void connect(String ip, int port) throws Exception {
        socket = new Socket(ip, port);
        System.out.println("Connected to server: " + socket);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void send(String json) throws Exception {
        writer.write(json);
        writer.newLine();
        writer.flush();
    }

    public String receive() throws Exception {
        return reader.readLine();
    }

    public void close() throws Exception {
        socket.close();
    }
}
