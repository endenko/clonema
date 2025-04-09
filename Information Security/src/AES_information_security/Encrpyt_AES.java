package AES_information_security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class Encrpyt_AES {
    public static void main(String[] args) throws Exception {
        // Bước 1: Tạo key 256-bit từ mảng byte cụ thể
        byte[] keyBytes = new byte[32]; // 32 bytes = 256 bits
        for (int i = 0; i < keyBytes.length; i++) {
            keyBytes[i] = (byte) i; // Ví dụ tạo key cụ thể (tùy chỉnh theo nhu cầu)
        }
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // Bước 2: Chuỗi đầu vào để mã hóa
        String input = "0xC6FFFECAAAD643389762BCA971DBD346F8D210C9C0F4E30011EF21FAB67FED5C87515EDA45627B2589BD95AF54449176D9681770AD360497EEAD082E225A599CCDF501C13933C6953F82CA5A0779312D5EB841F28A1367D5A2C8BB71CD67990C";

        // Bước 3: Mã hóa chuỗi đầu vào
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(input.getBytes());

        // Hiển thị dữ liệu mã hóa dưới dạng Base64
        String encryptedString = java.util.Base64.getEncoder().encodeToString(encryptedData);
        System.out.println("Encrypted Data: " + encryptedString);
    }
}
