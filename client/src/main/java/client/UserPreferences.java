package client;

public class UserPreferences {
    private enum Language {
        ENGLISH, DUTCH
    }
    //in case of future preferences, add them here
    private Language preferredLanguage;

    public UserPreferences(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    public UserPreferences(){
        this.preferredLanguage = Language.ENGLISH;
    }

    public Language getPreferredLanguage() {
        return this.preferredLanguage;
    }

    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}