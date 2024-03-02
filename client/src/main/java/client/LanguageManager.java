package client.utils;

public static class LanguageManager {
    public static List<Locale> getSupportedLocales() {
        return Arrays.asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN);
    }

    public static Locale loadSelectedLocale() {
        // Implement logic to load and return the selected locale from storage
        // For example, read a configuration file or preferences.
        return null;
    }

    public static void saveSelectedLocale(Locale selectedLocale) {
        // Implement logic to save the selected locale to storage
        // For example, write to a configuration file or preferences.
    }

    public static Image getFlagImage(Locale locale) {
        // Implement logic to map the given locale to the corresponding flag image
        // For example, use a map of locale to flag image resources.
        return null;
    }
}
