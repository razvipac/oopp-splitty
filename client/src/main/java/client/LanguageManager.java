package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
//import java.util.List;
//import java.util.Locale;

public class LanguageManager {
    private static final String PREFERENCES_FILE_PATH = "userSettings/userPreferences.json";

    /**
     * @return the current language stored in the PREFERENCES_FILE_PATH address
     */
    public static LanguageOption loadLanguage() {
        File file = new File(PREFERENCES_FILE_PATH);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file, new TypeReference<LanguageOption>() {
            });
        } catch (IOException e) {
            return new LanguageOption();
            // Default preferences if the file doesn't exist or there's an issue reading it
        }
    }

    /**
     * This method saves a language to the config file
     *
     * @param language is the language option that we want to save in the config file
     */
    public static void saveLanguage(LanguageOption language) {
        File file = new File(PREFERENCES_FILE_PATH);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, language);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }

    }

    /**
     * @param language is the language that we want to get the flag for
     * @return an appropriate flag associated with this language
     */
    public static Image getFlagImage(LanguageOption language) {
        return null;
    }

    /**
     * @param language The language option representing the current language.
     * @param key      takes a key and we want to
     * @return its associated value from the json file corresponding to the current language in use
     */

    public static String get(LanguageOption language, String key) {
        File file = new File("userSettings/English.json");
        if (language.getLanguage().equals(LanguageOption.Language.DUTCH)) {
            file = new File("userSettings/Dutch.json");

        }
        try {
            // Read JSON from file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);

            // Get the value associated with the key
            return rootNode.get(key).asText();

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
