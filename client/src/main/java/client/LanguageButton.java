package client;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.util.List;
import java.util.Locale;

public class LanguageButton extends Button {

    private Locale currentLanguage;
    private List<Locale> availableLanguages;

    public LanguageButton() {
        initialize();
    }

    private void initialize() {
        setGraphic(createFlagIcon()); // Set the initial flag icon
        setOnMouseClicked(event -> handleMouseClicked(event));
        loadAvailableLanguages();
        loadCurrentLanguage(); // Load the persisted language choice
    }

    private void loadAvailableLanguages() {
        // TODO: Load available languages from the application
        // availableLanguages = LanguageManager.getSupportedLocales();
    }

    private void loadCurrentLanguage() {
        // TODO: Load the persisted language choice from storage
        // currentLanguage = LanguageManager.loadSelectedLocale();
        // If not persisted, set a default language.
        if (currentLanguage == null) {
            currentLanguage = Locale.getDefault();
        }
        updateFlagIcon();
    }

    private ImageView createFlagIcon() {
        // TODO: Create an ImageView with the flag icon for the current language
        Image flagImage = LanguageManager.getFlagImage(currentLanguage);
        ImageView imageView = new ImageView(flagImage);
        imageView.setFitWidth(20);
        imageView.setFitHeight(15);
        return imageView;
    }

    private void updateFlagIcon() {
        // TODO: Update the flag icon based on the current language
        setGraphic(createFlagIcon());
    }

    private void handleMouseClicked(javafx.scene.input.MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            showLanguageMenu();
        }
    }

    private void showLanguageMenu() {
        ContextMenu contextMenu = new ContextMenu();

        // Add menu items for each available language
        for (Locale locale : availableLanguages) {
            MenuItem menuItem = new MenuItem(locale.getDisplayLanguage());
            menuItem.setOnAction(actionEvent -> selectLanguage(locale));
            contextMenu.getItems().add(menuItem);
        }

        // Show the context menu below the button
        contextMenu.show(this, 0, getHeight());

        // Set a listener to hide the menu when the user clicks outside of it
        contextMenu.setOnHidden(hiddenEvent -> setGraphic(createFlagIcon()));
    }

    private void selectLanguage(Locale selectedLocale) {
        currentLanguage = selectedLocale;

        // TODO: Save the selected language to storage for persistence
        // LanguageManager.saveSelectedLocale(currentLanguage);

        updateFlagIcon();
    }
}