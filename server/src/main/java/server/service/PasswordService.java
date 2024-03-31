package server.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordService {

    private final String password;

    @SuppressWarnings("checkstyle:MemberName")
    private final SecureRandom RANDOM = new SecureRandom();

    @SuppressWarnings("checkstyle:MemberName")
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz!@#$";

    /**
     * Constructs a PasswordService instance
     */
    public PasswordService() {
        password = generatePassword(20);
        // Prints server password
        System.out.println("\nAdmin password: " + password + "\n");
    }

    /**
     * Generates a random, strong password to be used by the admin
     *
     * @param length The length of the password
     * @return Randomly generated password as String
     */
    public String generatePassword(int length) {
        if(length < 5) length = 5;

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return password.toString();
    }

    /**
     * Checks whether the given String matches password
     * @param input The entered password
     * @return True iff input matches password, false otherwise
     */
    public Boolean doesPasswordMatch(String input) {
        return password.equals(input);
    }

}
