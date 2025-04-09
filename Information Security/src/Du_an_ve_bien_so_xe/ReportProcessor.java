package Du_an_ve_bien_so_xe;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class ReportProcessor {

    public static SecretKey generateKeyFromLicensePlate(String Key) {
        byte[] keyBytes = Key.getBytes();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static byte[] encryptData(String data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data.getBytes());
    }

    public static void printEncryptedData(String fullName, String idCard, String bankAccount, String licensePlate, String contactPhone, SecretKey secretKey) throws Exception {
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

    public static String adjustKey(String licensePlate) {
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
                return null;
            }
            break;
        } while (true);
        System.out.println("Generated Key: " + Key);
        return Key;
    }
}
