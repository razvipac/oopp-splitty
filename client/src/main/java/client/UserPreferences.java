package client;

import java.util.Locale;

public class UserPreferences {
    //in case of future preferences, add them here
    private Locale preferredLanguage;

    public UserPreferences(Locale preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    public UserPreferences(){}

    public Locale getPreferredLanguage() {
        return this.preferredLanguage;
    }

    public void setPreferredLanguage(Locale preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}