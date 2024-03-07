package client;
public class LanguageOption {
    public enum Language {
        ENGLISH, DUTCH
    }
    Language language;

    public LanguageOption(Language language) {
        this.language = language;
    }
    public LanguageOption(){
        this.language = Language.ENGLISH;
    }

    @Override
    public String toString() {
        if(this.language.equals(Language.ENGLISH)){
            return "English";
        }
        if(this.language.equals(Language.DUTCH)){
            return "Dutch";
        }
        return "";
    }

    public Language getLanguage() {
        return this.language;
    }
}
