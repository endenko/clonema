package AES_information_security;

import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Encrpyt_buyerid {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Input Buyer ID (16 characters long)
        System.out.println("Enter Buyer ID (16 characters long):");
        String buyerId = scanner.nextLine();
        if (buyerId == null || buyerId.length() != 16) {
            System.out.println("Buyer ID must be exactly 16 characters.");
            System.out.println("    ");
            System.out.println("Enter Buyer ID (16 characters long):");
            buyerId = scanner.nextLine();
        }

        // Step 2: Use Buyer ID directly as AES Key (16 characters = 16 bytes)
        byte[] keyBytes = buyerId.getBytes();
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // Step 3: Display Secret Key as the Buyer ID
        System.out.println("Generated Secret Key (Buyer ID): " + buyerId);

        // Step 4: Simulate other user inputs (for encryption)
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

        System.out.println("Enter Phone Number:");
        String phoneNumber = scanner.nextLine();

        String userInput = String.format("Item ID: %s, Item Name: %s, Price: %s, Buyer Name: %s, Buyer ID: %s, Phone: %s",
                itemId, itemName, price, buyerName, buyerId, phoneNumber);

        // Step 5: Encrypt user input
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(userInput.getBytes());

        // Save encrypted data to file
        try (FileOutputStream fos = new FileOutputStream("encrypted_data.txt")) {
            fos.write(encryptedData);
        }

        System.out.println("Encrypted data saved to 'encrypted_data.txt'.");

        // Step 6: Store encrypted data and secret key in SQL database
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
            stmt.setString(8, buyerId); // Secret Key (buyerId)
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
