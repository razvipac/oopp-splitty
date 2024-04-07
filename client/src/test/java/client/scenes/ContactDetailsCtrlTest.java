package client.scenes;

import client.utils.ServerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.*;
import static org.mockito.Mockito.*;

class ContactDetailsCtrlTest {
    private ContactDetailsCtrl contactDetailsCtrl;
    @BeforeEach
    void setUp(){
        MainCtrl mainCtrl = mock(MainCtrl.class);
        ServerUtils serverUtils = mock(ServerUtils.class);
        contactDetailsCtrl = new ContactDetailsCtrl(mainCtrl,serverUtils);
        contactDetailsCtrl.setErrorText();
    }
    @Test
    void inputIsInvalidTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String input = "invalid input";
        String regex = "NL12 XXXX 0123 4567 89";
        String errorMessage = "Please enter a valid BIC";

        // Use reflection to get the private method
        Method method = ContactDetailsCtrl.class.getDeclaredMethod("inputIsInvalid", String.class, String.class,
                String.class);
        method.setAccessible(true); // Allow invoking private method
        boolean result = (boolean) method.invoke(contactDetailsCtrl, input, regex, errorMessage);
        assertTrue(result); // Assert the result
    }

    @Test
    void inputIsNotInvalidTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String input = "NL12 XXXX 0123 4567 89";
        String regex = "^NL\\d{2}\\s[A-Z0-9]{4}\\s\\d{4}\\s\\d{4}\\s\\d{2}$";
        String errorMessage = "Please enter a valid BIC";

        // Use reflection to get the private method
        Method method = ContactDetailsCtrl.class.getDeclaredMethod("inputIsInvalid", String.class, String.class,
                String.class);
        method.setAccessible(true); // Allow invoking private method
        boolean result = (boolean) method.invoke(contactDetailsCtrl, input, regex, errorMessage);
        assertFalse(result); // Assert the result
    }
}