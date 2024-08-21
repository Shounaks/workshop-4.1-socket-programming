package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static org.example.CredentialsManager.*;

public class Client {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        initializeClient();
//        initializeClientWithSHA256();
    }

    private static void initializeClient() throws IOException {
        Socket client = new Socket("localhost", 8080);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        DataInputStream in = new DataInputStream(client.getInputStream());
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your Username: ");
        String username = sc.nextLine();
        System.out.println("Enter your Password: ");
        String password = sc.nextLine();
//        out.writeUTF(USERNAME.str + ":" + PASSWORD.str);
        out.writeUTF(username + ":" + password);
        out.flush();
        while (true) {
            String s = in.readUTF();
            if (s.contains("[Server]")) {
                System.out.println(s);
                break;
            }
        }
        out.close();
        client.close();
    }

    private static void initializeClientWithSHA256() throws IOException, NoSuchAlgorithmException {
        Socket client = new Socket("localhost", 8080);
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        out.writeUTF(USERNAME.str + ":" + computeHash(PASSWORD.str, "SECRET_SAUCE"));
        out.flush();
        out.close();
        client.close();
    }
}
