package org.example.secure;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChallengeHandshakeClient {
    private static final String USERNAME = "Shounak";
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final String SECRET = "shared_secret";

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream();
             DataInputStream reader = new DataInputStream(input);
             DataOutputStream writer = new DataOutputStream(output)) {

            // Step 0: Send Username
            writer.writeUTF(USERNAME);
            writer.flush();

            // Step 1: Receive challenge from server
            byte[] challenge = new byte[16];
            reader.readFully(challenge);

            // Step 2: Compute and send response
            byte[] response = computeHash(challenge, SECRET);
            writer.write(response);
            writer.flush();

            // Step 3: Receive server response
            String serverResponse = reader.readUTF();
            System.out.println("Server response: " + serverResponse);


            if ("Authentication successful".equals(serverResponse)) {
                // Receive items from server
                int itemCount = reader.readInt();
                for (int i = 0; i < itemCount; i++) {
                    String item = reader.readUTF();
                    System.out.println("Received item: " + item);
                }
            }

        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    private static byte[] computeHash(byte[] challenge, String secret) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(challenge);
        md.update(secret.getBytes());
        return md.digest();
    }
}
