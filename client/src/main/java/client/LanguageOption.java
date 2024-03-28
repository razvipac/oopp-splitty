package client;

import java.util.Objects;

/**
 * This class is supposed to work as a Language in its own, with the ability to have
 * some getters and setters
 */
public class LanguageOption {
    /**
     * Enumerates supported languages.
     */
    public enum Language {
        ENGLISH, DUTCH, ROMANIAN
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
        if (this.language.equals(Language.ROMANIAN)) {
            return "Romanian";
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

    /**
     * Standard equals method
     * @param o the object to compare with
     * @return boolean value of the comparison
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (null == o || this.getClass() != o.getClass()) return false;
        final LanguageOption that = (LanguageOption) o;
        return this.language == that.language;
    }

    /**
     * Generates hashCode for LanguageOption
     * @return generated hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.language);
    }
}
