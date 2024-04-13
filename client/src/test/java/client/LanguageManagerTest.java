package client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class LanguageManagerTest {
    LanguageManager languageManager;
    @BeforeEach
    void setUP(){
        String path = "src/test/resources/test.json";
        String path2 = "src/test/resources/test2.json";
        this.languageManager = new LanguageManager(path,path2);
    }
    @Test
    void checkFileExist() {
        String path = "src/test/resources/test.json";
        File file = new File(path);
        assertTrue(file.exists());
    }
    @Test
    void saveDutch(){

        languageManager.saveLanguage(new LanguageOption(LanguageOption.Language.DUTCH));
        LanguageOption languageOption= languageManager.loadLanguage();
        assertEquals(LanguageOption.Language.DUTCH, languageOption.getLanguage());
    }
    @Test
    void saveEnglish(){
        languageManager.saveLanguage(new LanguageOption(LanguageOption.Language.ENGLISH));
        LanguageOption languageOption= languageManager.loadLanguage();
        assertEquals(LanguageOption.Language.ENGLISH, languageOption.getLanguage());
    }

    @Test
    void loadSaveLanguage() {
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
        LanguageOption languageOptionEnglish = new LanguageOption(LanguageOption.Language.ENGLISH);
        LanguageOption languageOptionDutch = new LanguageOption(LanguageOption.Language.DUTCH);

        assertEquals("English",languageManager.get(languageOptionEnglish,"English"));
        assertEquals("Dutch",languageManager.get(languageOptionEnglish,"Dutch"));
        assertEquals("Engels",languageManager.get(languageOptionDutch,"English"));
        assertEquals("Nederlands",languageManager.get(languageOptionDutch,"Dutch"));
    }
    @Test
    void testEquals(){
        String path1 = "src/test/resources/test.json";
        LanguageManager languageManager1 = new LanguageManager(path1,path1);

        String path2 = "src/test/resources/test.json";
        LanguageManager languageManager2 = new LanguageManager(path2,path2);
        assertEquals(languageManager1,languageManager2);
    }

    @Test
    void testNotEquals(){
        String path1 = "src/test/resources/test.json";
        LanguageManager languageManager1 = new LanguageManager(path1,path1);

        String path2 = "src/test/resources/test2.json";
        LanguageManager languageManager2 = new LanguageManager(path2,path1);
        assertNotEquals(languageManager1,languageManager2);
    }

    @Test
    void testHash(){
        String path1 = "src/test/resources/test.json";
        LanguageManager languageManager1 = new LanguageManager(path1, path1);

        String path2 = "src/test/resources/test.json";
        LanguageManager languageManager2 = new LanguageManager(path2, path2);
        assertEquals(languageManager1.hashCode(),languageManager2.hashCode());
    }

    @Test
    void defaultToEnglish(){
        String path1 = "src//test/resources/test69420.json";
        LanguageManager languageManager1 = new LanguageManager(path1, path1);
        LanguageOption lo = new LanguageOption(LanguageOption.Language.ENGLISH);
        assertEquals(lo ,languageManager1.loadLanguage());
    }
}