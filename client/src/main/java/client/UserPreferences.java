package client;

public class UserPreferences extends LanguageOption {
    //in case of future preferences, add them here
    private Language preferredLanguage;

    /**
     * Constructs user preferences with the specified preferred language.
     *
     * @param preferredLanguage The preferred language.
     */
    public UserPreferences(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    /**
     * Default constructor initializing preferences with English language.
     */
    public UserPreferences() {
        this.preferredLanguage = Language.ENGLISH;
    }

    /**
     * Retrieves the preferred language.
     *
     * @return The preferred language.
     */
    public Language getPreferredLanguage() {
        return this.preferredLanguage;
    }

    /**
     * Sets the preferred language.
     *
     * @param preferredLanguage The preferred language to set.
     */
    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
