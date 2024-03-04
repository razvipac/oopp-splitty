package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageManager {
    private static final String PREFERENCES_FILE_PATH = "userSettings/userPreferences.json";
    public static List<Locale> getSupportedLocales() {
        return Arrays.asList(Locale.ENGLISH, new Locale("nl", "NL"));
    }

    public static UserPreferences loadPreferences() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(PREFERENCES_FILE_PATH), new TypeReference<UserPreferences>() {});
        } catch (IOException e) {
            return new UserPreferences(); // Default preferences if the file doesn't exist or there's an issue reading it
        }
    }

    public static Locale loadSelectedLocale() {
        UserPreferences preferences = loadPreferences();
        return (preferences != null) ? preferences.getPreferredLanguage() : null;
    }

    public static void saveSelectedLocale(Locale selectedLocale) {
        // Implement logic to save the selected locale to storage
        // For example, write to a configuration file or preferences.
    }

    public static Image getFlagImage(Locale locale) {
        // Implement logic to map the given locale to the corresponding flag image
        // For example, use a map of locale to flag image resources.
        return null;
    }
}
