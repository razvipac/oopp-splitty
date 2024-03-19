package client;
/**
 * This class is supposed to work as a Language in its own, with the ability to have
 * some getters and setters
 */
public class LanguageOption {
    /**
     * Enumerates supported languages.
     */
    public enum Language {
        ENGLISH, DUTCH
    }

    private Language language;

    /**
     * Constructs a LanguageOption with the specified language.
     *
     * @param language The language option.
     */
    public LanguageOption(Language language) {
        this.language = language;
    }

    /**
     * Constructs a LanguageOption with the default language (English).
     */
    public LanguageOption() {
        this.language = Language.ENGLISH;
    }

    /**
     * Returns a string representation of the language.
     *
     * @return The string representation of the language.
     */
    @Override
    public String toString() {
        if (this.language.equals(Language.ENGLISH)) {
            return "English";
        }
        if (this.language.equals(Language.DUTCH)) {
            return "Dutch";
        }
        return "";
    }

    /**
     * Retrieves the language option.
     *
     * @return The language option.
     */
    public Language getLanguage() {
        return this.language;
    }
}
