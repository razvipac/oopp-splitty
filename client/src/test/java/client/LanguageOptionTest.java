package client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageOptionTest {

    @Test
    void testToStringDutch() {
        LanguageOption languageOption = new LanguageOption(LanguageOption.Language.DUTCH);
        assertEquals("Dutch", languageOption.toString());

    }
    @Test
    void testToStringEnglish(){
        LanguageOption languageOption = new LanguageOption(LanguageOption.Language.ENGLISH);
        assertEquals("English", languageOption.toString());
    }
    @Test
    void getLanguage() {
        LanguageOption languageOption = new LanguageOption(LanguageOption.Language.DUTCH);
        assertEquals(LanguageOption.Language.DUTCH, languageOption.getLanguage());
    }

    @Test
    void testEquals(){
        LanguageOption l1 = new LanguageOption(LanguageOption.Language.DUTCH);
        LanguageOption l2 = new LanguageOption(LanguageOption.Language.DUTCH);
        assertEquals(l1, l2);
    }
    @Test
    void testNotEquals(){
        LanguageOption l1 = new LanguageOption(LanguageOption.Language.DUTCH);
        LanguageOption l2 = new LanguageOption(LanguageOption.Language.ENGLISH);
        assertNotEquals(l1, l2);
    }
    @Test
    void testHash(){
        LanguageOption l1 = new LanguageOption(LanguageOption.Language.DUTCH);
        LanguageOption l2 = new LanguageOption(LanguageOption.Language.DUTCH);
        assertEquals(l1.hashCode(),l2.hashCode());
    }
}