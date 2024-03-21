package client;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import java.util.ArrayList;
import java.util.List;
//todo class need complete refactoring
public class LanguageButton extends Button {
    private static LanguageOption currentLanguage;
    private static List<LanguageOption> availableLanguages;
    private static LanguageManager languageManager;
    /**
     * @return the language chosen by the user
     */
    public static LanguageOption getCurrentLanguage() {
        return LanguageButton.currentLanguage;
    }
    /**
     * Constructs a LanguageButton.
     */
    public LanguageButton() {
        initialize();
    }

    /**
     * Initializes the language button.
     */
    private void initialize() {
        languageManager = new LanguageManager("" +
                "src\\main\\resources\\userSettings\\userPreferences.json");
        setGraphic(createFlagIcon()); // Set the initial flag icon
        setOnMouseClicked(event -> handleMouseClicked(event));
        loadAvailableLanguages();
        loadCurrentLanguage(); // Load the persisted language choice
        this.setText(languageManager.get("Test"));
    }

    /**
     * Loads available languages.
     */
    private void loadAvailableLanguages() {
        availableLanguages = new ArrayList<>();
        availableLanguages.add(new LanguageOption(LanguageOption.Language.ENGLISH));
        availableLanguages.add(new LanguageOption(LanguageOption.Language.DUTCH));

    }

    /**
     * Loads the current language.
     */
    private void loadCurrentLanguage() {
        //check code of loadLanguage for more details. In case no language exists in the preferences
        //the algorithm defaults to english
        currentLanguage = languageManager.loadLanguage();
        updateFlagIcon();
    }

    /**
     * Creates a flag icon image view.
     *
     * @return The flag icon image view.
     */
    private ImageView createFlagIcon() {
        //getFlagImage currently returns null
        Image flagImage = LanguageManager.getFlagImage(currentLanguage);
        ImageView imageView = new ImageView(flagImage);
        imageView.setFitWidth(20);
        imageView.setFitHeight(15);
        return imageView;
    }

    /**
     * Updates the flag icon.
     */
    private void updateFlagIcon() {
        setGraphic(createFlagIcon());
    }

    /**
     * Handles mouse clicked event.
     *
     * @param event The mouse event.
     */
    private void handleMouseClicked(javafx.scene.input.MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            showLanguageMenu();
        }
    }

    /**
     * Shows the language menu.
     */
    private void showLanguageMenu() {
        ContextMenu contextMenu = new ContextMenu();

        // Add menu items for each available language
        for (LanguageOption languageOption : availableLanguages) {
            MenuItem menuItem =
                    new MenuItem(languageManager.get(languageOption,languageOption.toString()));
            menuItem.setOnAction(actionEvent -> selectLanguage(languageOption));
            contextMenu.getItems().add(menuItem);
        }

        // Show the context menu below the button
        contextMenu.show(this, 0, getHeight());

        // Set a listener to hide the menu when the user clicks outside of it
        contextMenu.setOnHidden(hiddenEvent -> setGraphic(createFlagIcon()));
    }

    /**
     * Selects a language.
     *
     * @param languageOption is the language you want to switch to
     */
    private void selectLanguage(LanguageOption languageOption) {
        currentLanguage = languageOption;
        languageManager.saveLanguage(currentLanguage);
        updateFlagIcon();
        this.setText(languageManager.get("Test"));
    }
}