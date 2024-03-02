package client.utils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PreferencesManager {

    private static final String PREFERENCES_FILE_PATH = "userPreferences.json";

    public static UserPreferences loadPreferences() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(PREFERENCES_FILE_PATH), UserPreferences.class);
        } catch (IOException e) {
            return new UserPreferences(); // Default preferences if the file doesn't exist or there's an issue reading it
        }
    }

    public static Locale loadSelectedLocale() {
        UserPreferences preferences = loadPreferences();
        return (preferences != null) ? preferences.getPreferredLanguage() : null;
    }
}

