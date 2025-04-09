package Du_an_ve_bien_so_xe;

import javax.crypto.SecretKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.Scanner;

public class ReportEncryption {

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
                    System.out.println("Invalid format. Please enter Date of Birth in format DD-MM-YYYY:");
                } else if ("Cancel".equalsIgnoreCase(yearOfBirth)) {
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
            String KeyLicense = ReportProcessor.adjustKey(licensePlate);
            if (KeyLicense == null) return;

            // Generate AES Key
            SecretKey secretKey = ReportProcessor.generateKeyFromLicensePlate(KeyLicense);

            System.out.println("Generated AES Key (Hex): " + bytesToHex(secretKey.getEncoded()));

            ReportProcessor.printEncryptedData(fullName, idCard, bankAccount, licensePlate, contactPhone, secretKey);

            // Save to Database
            saveToDatabase(fullName, idCard, contactPhone, bankAccount, licensePlate, yearOfBirth, reportTime, vehicleType, KeyLicense);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void saveToDatabase(String fullName, String idCard, String contactPhone, String bankAccount, String licensePlate,
                                       String yearOfBirth, String reportTime, String vehicleType, String Key) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO reports (full_name, id_card, bank_account, license_plate, year_of_birth, report_time, vehicle_type, contact_phone, encrypted_full_name, encrypted_id_card, encrypted_bank_account, encrypted_license_plate, encrypted_contact_phone, secret_key) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Encrypt data
            SecretKey secretKey = ReportProcessor.generateKeyFromLicensePlate(Key);
            stmt.setString(1, fullName);
            stmt.setString(2, idCard);
            stmt.setString(3, bankAccount);
            stmt.setString(4, licensePlate);
            stmt.setString(5, yearOfBirth);
            stmt.setString(6, reportTime);
            stmt.setString(7, vehicleType);
            stmt.setString(8, contactPhone);
            stmt.setString(9, Base64.getEncoder().encodeToString(ReportProcessor.encryptData(fullName, secretKey)));
            stmt.setString(10, Base64.getEncoder().encodeToString(ReportProcessor.encryptData(idCard, secretKey)));
            stmt.setString(11, Base64.getEncoder().encodeToString(ReportProcessor.encryptData(bankAccount, secretKey)));
            stmt.setString(12, Base64.getEncoder().encodeToString(ReportProcessor.encryptData(licensePlate, secretKey)));
            stmt.setString(13, Base64.getEncoder().encodeToString(ReportProcessor.encryptData(contactPhone, secretKey)));
            stmt.setString(14, Key);

            stmt.executeUpdate();
            System.out.println("Encrypted data and key saved to database.");
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
