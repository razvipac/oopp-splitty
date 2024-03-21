package client;
import org.junit.jupiter.api.Test;

import java.io.File;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LanguageManagerTest {
    @Test
    void checkFileExist()  {
        String path = "client\\src\\test\\java\\client\\testFile.json";
        File file = new File(path);
        assertTrue(file.exists());
    }

    @Test
    void loadSaveLanguage() {
        String path = "client\\src\\test\\java\\client\\testFile.json";
        LanguageManager languageManager = new LanguageManager(path);
        languageManager.saveLanguage(new LanguageOption());
        LanguageOption languageOption= languageManager.loadLanguage();
        assertEquals(LanguageOption.Language.ENGLISH, languageOption.getLanguage());

        languageManager.saveLanguage(new LanguageOption(LanguageOption.Language.DUTCH));
        languageOption= languageManager.loadLanguage();
        assertEquals(LanguageOption.Language.DUTCH, languageOption.getLanguage());

        languageManager.saveLanguage(new LanguageOption(LanguageOption.Language.ENGLISH));
        languageOption= languageManager.loadLanguage();
        assertEquals(LanguageOption.Language.ENGLISH, languageOption.getLanguage());

        languageManager.saveLanguage(new LanguageOption(LanguageOption.Language.DUTCH));
        languageOption= languageManager.loadLanguage();
        assertEquals(LanguageOption.Language.DUTCH, languageOption.getLanguage());

    }

    @Test
    void get() {
        String path = "client\\src\\test\\java\\client\\testFile.json";
        LanguageManager languageManager = new LanguageManager(path);
        LanguageOption languageOptionEnglish = new LanguageOption(LanguageOption.Language.ENGLISH);
        LanguageOption languageOptionDutch = new LanguageOption(LanguageOption.Language.DUTCH);

        assertEquals("English",languageManager.get(languageOptionEnglish,"English"));
        assertEquals("Dutch",languageManager.get(languageOptionEnglish,"Dutch"));
        assertEquals("Engels",languageManager.get(languageOptionDutch,"English"));
        assertEquals("Nederlands",languageManager.get(languageOptionDutch,"Dutch"));
    }
}