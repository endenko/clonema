package Du_an_ve_bien_so_xe;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.Scanner;
public class Ifsomethingtrouble {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // User Input
            System.out.println("Enter Reporter Full Name:");
            String fullName = scanner.nextLine();

            System.out.println("Enter ID Card Number:");
            String idCard = scanner.nextLine();

            System.out.println("Enter Date of Birth (DD-MM-YYYY):");
            String yearOfBirth;
            do {
                yearOfBirth = scanner.nextLine();
                if (!yearOfBirth.matches("\\d{2}-\\d{2}-\\d{4}")) {
                    System.out.println("Invalid format. Please enter Date of Birth in format YYYY-MM-DD:");
                }
                else if ("Cancel".equalsIgnoreCase(yearOfBirth)) {
                    System.out.println("You cancelled the report!");
                    return;
                } else {
                    break;
                }
            } while (true);

            System.out.println("Enter Contact Phone Number:");
            String contactPhone = scanner.nextLine();

            System.out.println("Enter Bank Account Number:");
            String bankAccount = scanner.nextLine();

            System.out.println("Enter Report Date and Time:");
            String reportTime = scanner.nextLine();

            System.out.println("Enter Vehicle Type:");
            String vehicleType = scanner.nextLine();

            System.out.println("Enter License Plate (Key for Encryption):");
            String licensePlate;
            do {
                licensePlate = scanner.nextLine();
                if ("Cancel".equalsIgnoreCase(licensePlate)) {
                    System.out.println("Operation canceled by the user.");
                    return;
                }
                if (licensePlate.length() < 16) {
                    System.out.println("License plate must be at least 16 characters long. Please try again or type 'cancel' to exit.");
                }
            } while (licensePlate.length() < 16);

            // Validate and Adjust License Plate Key
            String KeyLicense = adjustKey(licensePlate);

            // Generate AES Key
            SecretKey secretKey = generateKeyFromLicensePlate(KeyLicense);

            System.out.println("Generated AES Key (Hex): " + bytesToHex(secretKey.getEncoded()));

            printEncryptedData(fullName, idCard, bankAccount, licensePlate,contactPhone, secretKey);

            // Encrypt and Save Data
            String Key = KeyLicense;
            storeEncryptedDataToDatabase(fullName, idCard, contactPhone ,bankAccount, licensePlate, yearOfBirth, reportTime, vehicleType, Key);

            System.out.println("All operations completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static String adjustKey(String licensePlate) {
        String Key = licensePlate;
        do {
            if (Key.equals("Cancel")) {
                System.out.println("Operation canceled by the user.");
                throw new IllegalArgumentException("Operation canceled");
            }
            if (Key.replaceAll("\\D", "").length() >= 16) {
                // Use first 16 characters of letters as key
                Key = Key.replaceAll("\\d", "").substring(0, 16);
            } else if (Key.replaceAll("\\D", "").length() + Key.replaceAll("\\d", "").length() >= 16) {
                // Interleave letters and numbers to form the key
                StringBuilder interleaved = new StringBuilder();
                String letters = Key.replaceAll("\\d", "");
                String digits = Key.replaceAll("\\D", "");

                int maxLength = Math.max(letters.length(), digits.length());
                for (int i = 0; i < maxLength; i++) {
                    if (i < letters.length()) interleaved.append(letters.charAt(i));
                    if (i < digits.length()) interleaved.append(digits.charAt(i));
                }
                Key = interleaved.substring(0, 16);
            } else {
                System.out.println("Invalid Key. Please re-enter License Plate:");
                Scanner scanner = new Scanner(System.in);
                Key = scanner.nextLine();
                continue;
            }
            break;
        } while (true);
        System.out.println("Generated Key: " + Key);
        return Key;
    }

    private static SecretKey generateKeyFromLicensePlate(String Key) {
        byte[] keyBytes = Key.getBytes();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static byte[] encryptData(String data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data.getBytes());
    }

    private static void printEncryptedData(String fullName, String idCard, String bankAccount, String licensePlate, String contactPhone, SecretKey secretKey) throws Exception {
        byte[] encryptedFullName = encryptData(fullName, secretKey);
        System.out.println("Encrypted Full Name (AES): " + Base64.getEncoder().encodeToString(encryptedFullName));

        byte[] encryptedIdCard = encryptData(idCard, secretKey);
        System.out.println("Encrypted ID Card (AES): " + Base64.getEncoder().encodeToString(encryptedIdCard));

        byte[] encryptedBankAccount = encryptData(bankAccount, secretKey);
        System.out.println("Encrypted Bank Account (AES): " + Base64.getEncoder().encodeToString(encryptedBankAccount));

        byte[] encryptedLicensePlate = encryptData(licensePlate, secretKey);
        System.out.println("Encrypted License Plate (AES): " + Base64.getEncoder().encodeToString(encryptedLicensePlate));

        byte[] encryptedContactPhone = encryptData(contactPhone, secretKey);
        System.out.println("Encrypted Contact Phone (AES): " + Base64.getEncoder().encodeToString(encryptedContactPhone));

    }

    private static void storeEncryptedDataToDatabase(
            String fullName, String idCard, String bankAccount, String licensePlate,
            String yearOfBirth, String reportTime, String vehicleType, String contactPhone, String Key) throws Exception {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=SecurityDB;encrypt=true;trustServerCertificate=true", "sa", "1");

            // Encrypt data
            SecretKey secretKey = generateKeyFromLicensePlate(Key);
            byte[] encryptedFullName = encryptData(fullName, secretKey);
            byte[] encryptedIdCard = encryptData(idCard, secretKey);
            byte[] encryptedBankAccount = encryptData(bankAccount, secretKey);
            byte[] encryptedLicensePlate = encryptData(licensePlate, secretKey);
            byte[] encryptedContactPhone = encryptData(contactPhone, secretKey);

            // Prepare SQL statement
            String sql = "INSERT INTO reports (full_name, id_card, bank_account, license_plate, year_of_birth, report_time, vehicle_type, contact_phone, encrypted_full_name, encrypted_id_card, encrypted_bank_account, encrypted_license_plate, encrypted_contact_phone, secret_key) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, fullName);
            stmt.setString(2, idCard);
            stmt.setString(3, bankAccount);
            stmt.setString(4, licensePlate);
            stmt.setString(5, yearOfBirth);
            stmt.setString(6, reportTime);
            stmt.setString(7, vehicleType);
            stmt.setString(8, contactPhone);
            stmt.setString(9, Base64.getEncoder().encodeToString(encryptedFullName));
            stmt.setString(10, Base64.getEncoder().encodeToString(encryptedIdCard));
            stmt.setString(11, Base64.getEncoder().encodeToString(encryptedBankAccount));
            stmt.setString(12, Base64.getEncoder().encodeToString(encryptedLicensePlate));
            stmt.setString(13, Base64.getEncoder().encodeToString(encryptedContactPhone));
            stmt.setString(14, Key); // Save the Key from adjustKey directly

            stmt.executeUpdate();
            System.out.println("Encrypted data and key saved to database.");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }



    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
