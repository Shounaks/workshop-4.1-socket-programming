package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum CredentialsManager {
    USERNAME("shounakAdmin"),
    PASSWORD("WorkShop4.1AssignmentIsAwesome");

    final String str;

    CredentialsManager(String str) {
        this.str = str;
    }

    public static String computeHash(String password, String secret) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashbytes);
//        return sha2Hex.getBytes(StandardCharsets.UTF_8);
    }

    private static String bytesToHex(byte[] hashbytes) {
        final StringBuilder hexString = new StringBuilder();
        for (byte b : hashbytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
