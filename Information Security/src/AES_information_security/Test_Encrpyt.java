package AES_information_security;

import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Test_Encrpyt {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Input Citizen ID (Căn cước công dân) (16 characters long)
        System.out.println("Enter Citizen ID (Căn cước công dân) (16 digits long):");
        String cccd = scanner.nextLine();
        if (cccd == null || cccd.length() != 16) {
            System.out.println("Citizen ID must be exactly 16 digits.");
            return;
        }

        // Step 2: Use Citizen ID directly as AES Key (16 characters = 16 bytes)
        byte[] keyBytes = cccd.getBytes(); // Use the Citizen ID as the key (16 bytes)
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // Step 3: Encrypt Name using the Citizen ID (CCCD)
        System.out.println("Enter Full Name:");
        String fullName = scanner.nextLine();

        if (fullName == null || fullName.isEmpty()) {
            System.out.println("Full Name is required.");
            return;
        }

        System.out.println("Enter Date of Birth (DD/MM/YYYY):");
        String dob = scanner.nextLine();

        System.out.println("Enter Hometown:");
        String hometown = scanner.nextLine();

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(fullName.getBytes("UTF-8"));  // Specify UTF-8 encoding

// Save encrypted data to file
        try (FileOutputStream fos = new FileOutputStream("encrypted_data.txt")) {
            fos.write(encryptedData);
        }

// Step 6: Decrypt user input (with UTF-8 charset)
        Cipher decipher = Cipher.getInstance("AES");
        decipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = decipher.doFinal(encryptedData);
        String decryptedData = new String(decryptedBytes, "UTF-8");  // Specify UTF-8 decoding

        System.out.println("Decrypted Data: " + decryptedData);

        // Step 6: Display Encrypted Data (in Hexadecimal or Base64 for readability)
        String encryptedString = bytesToHex(encryptedData);
        System.out.println("Encrypted Full Name (Hex): " + encryptedString);

        // Optionally, store this data in a SQL database (if needed)
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-SLUGK2O\\MSSQLSERVER01:1433;databaseName=AES_Database;encrypt=true;trustServerCertificate=true", "sa", "1");
            String sql = "INSERT INTO users (cccd, full_name, dob, hometown, encrypted_name) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cccd); // Citizen ID (CCCD)
            stmt.setString(2, fullName); // Full Name
            stmt.setString(3, dob); // Date of Birth
            stmt.setString(4, hometown); // Hometown
            stmt.setBytes(5, encryptedData); // Encrypted Full Name
            stmt.executeUpdate();
            System.out.println("Encrypted data saved to database.");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    // Utility method to convert byte array to Hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
