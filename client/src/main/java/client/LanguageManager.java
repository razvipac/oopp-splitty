package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Singleton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Singleton
public class LanguageManager {
    private String preferencesFilePath;
    private String configFilePath;
    private LanguageOption languageOption;
    /**
     *
     * @param preferencesFilePath initialize this to create better injection
     * @param configFilePath path to the config file
     */
    public LanguageManager(String preferencesFilePath, String configFilePath) {
        this.preferencesFilePath = preferencesFilePath;
        this.configFilePath = configFilePath;
        this.languageOption = loadLanguage();
    }

    /**
     * @return the current language stored in the PREFERENCES_FILE_PATH address
     */
    public LanguageOption loadLanguage() {
        try {
            JsonNode rootNode = getJsonNode(configFilePath);

            // Modify the value of the "language" parameter
            if (rootNode.has("language")) {
                switch (rootNode.get("language").asText()) {
                    case "Dutch":
                        return new LanguageOption(LanguageOption.Language.DUTCH);
                    case "Romanian":
                        return new LanguageOption(LanguageOption.Language.ROMANIAN);
                    case "English":
                        return new LanguageOption(LanguageOption.Language.ENGLISH);
                }
            }
        } catch (IOException e) {
            System.out.println("The system defaulted to english");
            return new LanguageOption();
            // Default preferences if the file doesn't exist or there's an issue reading it
        }
        return null;
    }

    /**
     *
     * @return the proper json node of the file
     * @throws IOException in case the file is not found
     */
    private JsonNode getJsonNode(String filePath) throws IOException {
        File file = new File(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(file);
        return rootNode;
    }

    /**
     * This method saves a language to the config file
     *
     * @param language is the language option that we want to save in the config file
     */
    public void saveLanguage(LanguageOption language) {
        try {
            File file = new File(configFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(file);

            // Modify the value of the "language" parameter
            if (rootNode.has("language")) {
                ((ObjectNode) rootNode).put("language", language.toString());
            }

            // Write modified JSON back to file
            objectMapper.writeValue(file, rootNode);
            setLanguageOption(language);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }

    }

    /**
     * @param language is the language that we want to get the flag for
     * @return an appropriate flag associated with this language
     */
    public static Image getFlagImage(LanguageOption language) {
        try {

            InputStream inputStream =
                    new FileInputStream("client/src/main/resources/userSettings/English.png");
            if (language.getLanguage() == LanguageOption.Language.DUTCH) {
                inputStream =
                        new FileInputStream("client/src/main/resources/userSettings/Dutch.png");
            }
            if(language.getLanguage() == LanguageOption.Language.ROMANIAN){
                inputStream =
                        new FileInputStream("client/src/main/resources/userSettings/Romanian.png");
            }
            return new Image(inputStream);
        }
        catch (Exception e){
            System.out.println("The image flag is not found");
            return null;
        }
    }

    /**
     * @param language The language option representing the current language.
     * @param key      takes a key and we want to
     * @return its associated value from the json file corresponding to the current language in use
     */

    public String get(LanguageOption language, String key) {
        try {
            JsonNode rootNode = getJsonNode(preferencesFilePath);

            //String languageString = rootNode.get("language").asText();
            String languageString = language.toString();
            JsonNode languageSection = rootNode.get(languageString);

            // Get the value associated with the key
            if(languageSection.has(key)){
                return languageSection.get(key).asText();
            }else{
                /**
                 * In case no key-value translation is found, the function return key
                 * This is in order to ensure that we have a bad translation only,
                 * and not a bad program
                 * Also, for english, the key-value coincide, so we basically have
                 * the english translation for it as generic :)
                 */
                return key;
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
    public String get(String key) {
        return this.get(this.languageOption, key);
    }

    /**
     * @param currentLanguage is a LanguageOption for which we want to return
     *                        a proper ImageView of its flag
     * @return a proper ImageView that looks like the language input flag.
     */
    public static ImageView createFlagIcon(LanguageOption currentLanguage) {
        //getFlagImage currently returns null
        Image flagImage =getFlagImage(currentLanguage);
        ImageView imageView = new ImageView(flagImage);
        imageView.setFitWidth(20);
        imageView.setFitHeight(15);
        return imageView;
    }
    /**
     *
     * @param o take an object  o
     * @return true iff o is a non null Language Manager
     * And the strings file paths are equals as of
     * String .equals() method
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (null == o || this.getClass() != o.getClass()) return false;
        final LanguageManager that = (LanguageManager) o;
        return Objects.equals(this.preferencesFilePath, that.preferencesFilePath);
    }

    /**
     *
     * @return a proper hashcode of the languageManager
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.preferencesFilePath);
    }

    /**
     *
     * @return the selected languageOption
     */
    public LanguageOption getLanguageOption() {
        return this.languageOption;
    }

    /**
     * Set the languageOption to the parameter
     * @param languageOption the new value of the languageOption
     */
    public void setLanguageOption(final LanguageOption languageOption) {
        this.languageOption = languageOption;
    }
}