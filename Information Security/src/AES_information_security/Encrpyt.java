package AES_information_security;

import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.Scanner;

public class Encrpyt {

    public static void main(String[] args) throws Exception {
        // Step 1: Generate AES Key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // Use 128-bit key for security
        SecretKey secretKey = keyGen.generateKey();

        // Ensure the secret key is exactly 16 bytes
// Ensure the secret key is exactly 16 bytes
        byte[] keyBytes = secretKey.getEncoded();
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("The secret key must be exactly 16 bytes for AES-128.");
        }

// Save the key to a file for demonstration
        saveKeyToFile("aesKey.key", secretKey);

// Convert secret key to Hexadecimal for display
        StringBuilder hexKey = new StringBuilder();
        for (byte b : keyBytes) {
            hexKey.append(String.format("%02X", b));
        }
        System.out.println("Generated Secret Key (Hex): " + hexKey.toString());

// Limit Base64 string to 16 characters if required
        String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Secret Key (Truncated Base64): " + secretKeyString);

        // Step 2: Simulate user input (HTML form-like input)
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Item ID:");
        String itemId = scanner.nextLine();
        if (itemId == null || itemId.isEmpty()) {
            System.out.println("Item ID is required. Please provide a valid value.");
            return;
        }

        System.out.println("Enter Item Name:");
        String itemName = scanner.nextLine();

        System.out.println("Enter Price:");
        String price = scanner.nextLine();

        System.out.println("Enter Buyer Name:");
        String buyerName = scanner.nextLine();

        System.out.println("Enter Buyer ID:");
        String buyerId = scanner.nextLine();

        System.out.println("Enter Phone Number:");
        String phoneNumber = scanner.nextLine();

        String userInput = String.format("Item ID: %s, Item Name: %s, Price: %s, Buyer Name: %s, Buyer ID: %s, Phone: %s",
                itemId, itemName, price, buyerName, buyerId, phoneNumber);

        // Step 3: Encrypt user input
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(userInput.getBytes());

        // Save encrypted data to file
        try (FileOutputStream fos = new FileOutputStream("encrypted_data.txt")) {
            fos.write(encryptedData);
        }

        System.out.println("Encrypted data saved to 'encrypted_data.txt'.");

        // Step 4: Store encrypted data in SQL database
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-SLUGK2O\\MSSQLSERVER01:1433;databaseName=AES_Database;encrypt=true;trustServerCertificate=true", "sa", "1");
            String sql = "INSERT INTO purchases (item_id, item_name, price, buyer_name, buyer_id, phone, encrypted_data, secret_key) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, itemId); // Item ID
            stmt.setString(2, itemName); // Item Name
            stmt.setString(3, price); // Price
            stmt.setString(4, buyerName); // Buyer Name
            stmt.setString(5, buyerId); // Buyer ID
            stmt.setString(6, phoneNumber); // Phone Number
            stmt.setBytes(7, encryptedData); // Encrypted Data
            stmt.setString(8, secretKeyString); // Secret Key (Base64)
            stmt.executeUpdate();
            System.out.println("Encrypted data and secret key saved to database.");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    // Utility method to save a key to a file
    private static void saveKeyToFile(String fileName, SecretKey key) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(key.getEncoded());
        }
    }
}
