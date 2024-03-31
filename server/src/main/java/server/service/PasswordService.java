package server.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;

@Service
public class PasswordService {

    /**
     * Creates PasswordService instance
     */
    public PasswordService() {
    }

    @SuppressWarnings("checkstyle:MemberName")
    private final SecureRandom RANDOM = new SecureRandom();

    @SuppressWarnings("checkstyle:MemberName")
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz!@#$";

    /**
     * Generates a random, strong password to be used by the admin
     * @param length The length of the password
     * @return Randomly generated password as char[]
     */
    public char[] generatePassword(int length) {
        if(length > 0) {
            char[] password = new char[length];
            for (int i = 0; i < length; i++) {
                password[i] = ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length()));
            }
            return password;
        }
        else throw new IllegalArgumentException("Length of password must be bigger than 0");
    }

    /**
     * Securely clears the given char array.
     * @param chars The char array to be cleared
     */
    public void clearPassword(char[] chars) {
        Arrays.fill(chars, '\0');
    }

}
