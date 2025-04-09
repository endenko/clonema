package AES_information_security;

import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Code {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Enter Plain Text
        System.out.println("Enter Plain Text to Encrypt:");
        String plainText = scanner.nextLine();

        if (plainText == null || plainText.isEmpty()) {
            System.out.println("Plain Text is required. Please provide a valid value.");
            return;
        }

        // Step 2: Select Mode
        System.out.println("Select Mode: CBC or ECB");
        String mode = scanner.nextLine().toUpperCase();

        if (!mode.equals("CBC") && !mode.equals("ECB")) {
            System.out.println("Invalid mode selected. Please select either CBC or ECB.");
            return;
        }

        // Step 3: Enter Secret Key
        System.out.println("Enter Secret Key (Base64 encoded, 128, 192, or 256 bits):");
        String secretKeyBase64 = scanner.nextLine();
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        // Step 4: Enter Initialization Vector (if CBC mode)
        IvParameterSpec iv = null;
        if (mode.equals("CBC")) {
            System.out.println("Enter Initialization Vector (16 characters, 128 bits):");
            String initializationVector = scanner.nextLine();
            if (initializationVector.length() != 16) {
                System.out.println("Invalid IV length. Must be exactly 16 characters.");
                return;
            }
            iv = new IvParameterSpec(initializationVector.getBytes());
        }

        // Step 5: Output Format
        System.out.println("Specify Output Format: Base64 or Hex");
        String outputFormat = scanner.nextLine().toUpperCase();

        if (!outputFormat.equals("BASE64") && !outputFormat.equals("HEX")) {
            System.out.println("Invalid output format. Please select either Base64 or Hex.");
            return;
        }

        // Step 6: Encrypt the Plain Text
        Cipher cipher;
        if (mode.equals("CBC")) {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        } else {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }

        byte[] encryptedData = cipher.doFinal(plainText.getBytes());

        // Convert encrypted data to the specified format
        String encryptedString;
        if (outputFormat.equals("BASE64")) {
            encryptedString = Base64.getEncoder().encodeToString(encryptedData);
        } else {
            encryptedString = bytesToHex(encryptedData);
        }

        // Output the encrypted data
        System.out.println("Encrypted Data:");
        System.out.println(encryptedString);
    }

    // Utility method to convert bytes to Hex
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
