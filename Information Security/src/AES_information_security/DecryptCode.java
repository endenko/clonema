package AES_information_security;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
public class DecryptCode {
        public static void main(String[] args) throws Exception {
            // Step 1: Generate AES Key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Use 256-bit key
            SecretKey secretKey = keyGen.generateKey();

            // Convert the key to a string for demonstration purposes
            String keyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            System.out.println("Generated AES Key (Base64):");
            System.out.println(keyString);

            // Step 2: Encrypt a sample input
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Item Name to Encrypt:");
            String itemName = scanner.nextLine();
            scanner.close();

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(itemName.getBytes());

            // Convert encrypted data to Base64 for demonstration
            String encryptedString = Base64.getEncoder().encodeToString(encryptedData);
            System.out.println("Encrypted Data (Base64):");
            System.out.println(encryptedString);

            // Step 3: Decrypt the data
            // Convert the key string back to a SecretKey
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedString));

            System.out.println("Decrypted Item Name:");
            System.out.println(new String(decryptedData));
        }
    }
