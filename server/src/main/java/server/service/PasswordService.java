package server.service;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final String password;

    @SuppressWarnings("checkstyle:MemberName")
    private final SecureRandom RANDOM = new SecureRandom();
    @SuppressWarnings("checkstyle:MemberName")
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz!@#$";

    /**
     * Constructs a PasswordService instance
     *
     * @param passwordEncoder Password encoder
     */
    @Autowired
    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        password = generatePassword(20);
        System.out.println("Admin password: " + password);
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
     * Retrieves the server password
     *
     * @return The server password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Hashes the provided password using BCryptPasswordEncoder
     *
     * @param toBeHashed The password to hash
     * @return The hashed password
     */
    public String hashPassword(String toBeHashed) {
        return passwordEncoder.encode(toBeHashed);
    }

    /**
     * Checks whether the given String matches password
     * @param input The entered password
     * @return True iff input matches password, false otherwise
     */
    public Boolean doesPasswordMatch(String input) {
        return passwordEncoder.matches(input, hashPassword(password));
    }

}
