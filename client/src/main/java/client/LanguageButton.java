package client;
import javafx.scene.control.Button;
//import javafx.scene.control.ContextMenu;
//import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseButton;
import java.util.ArrayList;
import java.util.List;

import java.util.Locale;
//todo class need complete refactoring
public class LanguageButton extends Button {
    private LanguageOption currentLanguage;
    private static List<LanguageOption> availableLanguages;
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
        setGraphic(createFlagIcon()); // Set the initial flag icon
        setOnMouseClicked(event -> handleMouseClicked(event));
        loadAvailableLanguages();
        loadCurrentLanguage(); // Load the persisted language choice
        this.setText(LanguageManager.get(currentLanguage,"Test"));
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
        currentLanguage = LanguageManager.loadLanguage();
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
//        if (event.getButton() == MouseButton.PRIMARY) {
//            showLanguageMenu();
//        }
    }

    /**
     * Shows the language menu.
     */
    private void showLanguageMenu() {
//        ContextMenu contextMenu = new ContextMenu();
//
//        // Add menu items for each available language
//        for (Locale locale : availableLanguages) {
//            MenuItem menuItem = new MenuItem(locale.getDisplayLanguage());
//            menuItem.setOnAction(actionEvent -> selectLanguage(locale));
//            contextMenu.getItems().add(menuItem);
//        }
//
//        // Show the context menu below the button
//        contextMenu.show(this, 0, getHeight());
//
//        // Set a listener to hide the menu when the user clicks outside of it
//        contextMenu.setOnHidden(hiddenEvent -> setGraphic(createFlagIcon()));
    }

    /**
     * Selects a language.
     *
     * @param selectedLocale The selected locale.
     */
    private void selectLanguage(Locale selectedLocale) {
//        currentLanguage = selectedLocale;
//
//        // TODO: Save the selected language to storage for persistence
//        LanguageManager.saveSelectedLocale(currentLanguage);
//
//        updateFlagIcon();
    }
}