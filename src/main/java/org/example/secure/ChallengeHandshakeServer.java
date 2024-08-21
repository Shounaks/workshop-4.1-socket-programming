package org.example.secure;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class ChallengeHandshakeServer {
    private static final int PORT = 12345;
    private static final String SECRET = "shared_secret";

    private static final String USERNAME = "Shounak";


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new ServerThread(socket).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static class ServerThread extends Thread {
        private Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (InputStream input = socket.getInputStream();
                 OutputStream output = socket.getOutputStream();
                 DataInputStream reader = new DataInputStream(input);
                 DataOutputStream writer = new DataOutputStream(output)) {

                // Step 0 : Read Username
                String username = reader.readUTF();
                System.out.println("USERNAME: " + username + " trying to log in... Sending Challenge");

                // Step 1: Generate and send challenge
                byte[] challenge = new byte[16];
                new Random().nextBytes(challenge);
                writer.write(challenge);
                writer.flush();

                // Step 2: Receive and verify response
                byte[] response = new byte[32];
                reader.readFully(response);

                byte[] expectedResponse = computeHash(challenge, SECRET);
                if (Arrays.equals(expectedResponse, response) && USERNAME.equals(username)) {
                    writer.writeUTF("Authentication successful");
                    writer.flush();

                    // Send items after successful authentication
                    String[] items = {"item1", "item2", "item3"};
                    writer.writeInt(items.length);
                    for (String item : items) {
                        writer.writeUTF(item);
                    }
                    writer.flush();
                } else {
                    writer.writeUTF("Authentication failed");
                    writer.flush();
                }

            } catch (IOException | NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private byte[] computeHash(byte[] challenge, String secret) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(challenge);
            md.update(secret.getBytes());
            return md.digest();
        }
    }
}