package client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageOptionTest {
    @Test
    void testToStringDutch() {
        LanguageOption languageOption = new LanguageOption(LanguageOption.Language.DUTCH);
        assertEquals("Dutch", languageOption.toString());

    }
    void testToStringEnglish(){
        LanguageOption languageOption = new LanguageOption(LanguageOption.Language.ENGLISH);
        assertEquals("English", languageOption.toString());
    }
    @Test
    void getLanguage() {
        LanguageOption languageOption = new LanguageOption(LanguageOption.Language.DUTCH);
        assertEquals(LanguageOption.Language.DUTCH, languageOption.getLanguage());

    }
}