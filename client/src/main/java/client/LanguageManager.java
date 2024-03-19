package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
public class LanguageManager {
    private static final String PREFERENCES_FILE_PATH = "client\\src\\main\\resources\\userSettings\\userPreferences.json";

    /**
     * @return the current language stored in the PREFERENCES_FILE_PATH address
     */
    public static LanguageOption loadLanguage() {
        try {
            File file = new File(PREFERENCES_FILE_PATH);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file, new TypeReference<LanguageOption>() {
            });
        } catch (IOException e) {
            System.out.println("The system defaulted to english");
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
        try {
            File file = new File(PREFERENCES_FILE_PATH);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);

            // Modify the value of the "language" parameter
            if (rootNode.has("language")) {
                ((ObjectNode) rootNode).put("language", language.toString());
            }

            // Write modified JSON back to file
            objectMapper.writeValue(file, rootNode);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }

    }

    /**
     * @param language is the language that we want to get the flag for
     * @return an appropriate flag associated with this language
     */
    public static Image getFlagImage(LanguageOption language) {
        // URL = "file:\\client\\src\\main\\resources\\userSettings\\English.png";
        String URL = "file:///client/src/main/resources/userSettings/English.png";
        Image image = new Image(URL);
        return image;
    }

    /**
     * @param language The language option representing the current language.
     * @param key      takes a key and we want to
     * @return its associated value from the json file corresponding to the current language in use
     */

    public static String get(LanguageOption language, String key) {
        try {
            File file = new File(PREFERENCES_FILE_PATH);
            // Read JSON from file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);

            String languageString = rootNode.get("language").asText();
            JsonNode languageSection = rootNode.get(languageString);

            // Get the value associated with the key
            if(languageSection.has(key)){
                return languageSection.get(key).asText();
            }else{
                return "NotFound";
            }


        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * This is the function used to communicate mostly between the front end and
     * the translation interface. It will translate take the key and call the function get
     * with the current language on it.
     * !!Note that you should put the key with its corresponding values in both English and Dutch
     * parts of the userPrefences.json
     * @param key takes a key
     * @return the associated value with it in the json config file
     */
    public static String get(String key){
        return LanguageManager.get(LanguageButton.getCurrentLanguage(),key);
    }

}
