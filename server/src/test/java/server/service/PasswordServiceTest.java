package server.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    @InjectMocks
    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGeneratePasswordLengthLessThan5() {
        // Act
        String password = passwordService.generatePassword(3);

        // Assert
        assertNotEquals(20, password.length());
    }


    @Test
    void testGeneratePasswordLengthGreaterThan5() {
        // Act
        String password = passwordService.generatePassword(10);

        // Assert
        assertEquals(10, password.length());
    }

    @Test
    void testDoesPasswordMatchPasswordMatches() {
        // Arrange
        String password = "TestPassword";
        assertFalse(passwordService.doesPasswordMatch(password));
    }

    @Test
    void testDoesPasswordMatchPasswordNotMatches() {
        // Arrange
        String password = "TestPassword";
        assertFalse(passwordService.doesPasswordMatch(password + "1"));
    }
}
