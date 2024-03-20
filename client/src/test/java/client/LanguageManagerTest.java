package client;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageManagerTest {

    @Test
    void loadSaveLanguage() {
        LanguageManager languageManager = new LanguageManager(
                "client/src/test/java/client/testFile.json");
        languageManager.saveLanguage(new LanguageOption());
        LanguageOption languageOption= languageManager.loadLanguage();
        assertEquals(LanguageOption.Language.ENGLISH, languageOption.getLanguage());

    }

    @Test
    void get() {
        LanguageManager languageManager = new LanguageManager(
                "client/src/test/java/client/testFile.json");
        LanguageOption languageOptionEnglish = new LanguageOption(LanguageOption.Language.ENGLISH);
        LanguageOption languageOptionDutch = new LanguageOption(LanguageOption.Language.DUTCH);

        assertEquals("English",languageManager.get(languageOptionEnglish,"English"));
        assertEquals("Dutch",languageManager.get(languageOptionEnglish,"Dutch"));
        assertEquals("Engels",languageManager.get(languageOptionDutch,"English"));
        assertEquals("Nederlands",languageManager.get(languageOptionDutch,"Dutch"));



    }
}