package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import static org.example.CredentialsManager.*;

public class Server {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        initializeServer();
    }

    public static void initializeServer() throws IOException, NoSuchAlgorithmException {
        try (ServerSocket server = new ServerSocket(8080)) {
            while (true) {
                Socket accept = server.accept();
                DataInputStream in = new DataInputStream(accept.getInputStream());
                String credentials = in.readUTF();
                System.out.println("[Server] Data Received -> " + credentials);
                authenticate(accept, credentials);
//                authenticateWithSHA256(credentials);
            }
        }
    }

    private static void authenticate(Socket accept, String credentials) throws IOException {
        final String[] split = credentials.split(":");
        final String username = split[0];
        final String password = split[1];
        if (USERNAME.str.equals(username) && PASSWORD.str.equals(password)) {
            System.out.println("[Server] Authenticated");
            DataOutputStream dataOutputStream = new DataOutputStream(accept.getOutputStream());
            dataOutputStream.writeUTF("[Server] Authentication Was Successful");
        } else {
            System.out.println("[Server] Invalid credentials");
            DataOutputStream dataOutputStream = new DataOutputStream(accept.getOutputStream());
            dataOutputStream.writeUTF("[Server] Authentication FAILED!!");
        }
    }
    private static void authenticateWithSHA256(String credentials) throws NoSuchAlgorithmException {
        final String[] split = credentials.split(":");
        final String username = split[0];
        final String password = split[1];
        if (USERNAME.str.equals(username) && computeHash(PASSWORD.str, "SECRET_SAUCE").equals(password)) {
            System.out.println("[Server] Authenticated");
        } else {
            System.out.println("[Server] Invalid credentials");
        }
    }
}