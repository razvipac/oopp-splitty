package server.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;

@Service
public class PasswordService {

    private char[] password;
    @SuppressWarnings("checkstyle:MemberName")
    private final SecureRandom RANDOM = new SecureRandom();

    @SuppressWarnings("checkstyle:MemberName")
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz!@#$";

    /**
     * Creates PasswordService instance
     */
    public PasswordService() {
        password = generatePassword(20);
    }

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
     * Retrieves the stored password (as a clone, so that the original can't be modified)
     * @return The stored password
     */
    public char[] getPassword() {
        if(password == null) return null;

        char[] clone = new char[password.length];
        System.arraycopy(password, 0, clone, 0, password.length);
        return password;
    }

}
