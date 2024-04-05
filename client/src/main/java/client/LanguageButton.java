//package client;
//import javafx.scene.control.Button;
//import javafx.scene.control.ContextMenu;
//import javafx.scene.control.MenuItem;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.VBox;
//
//import java.util.ArrayList;
//import java.util.List;
////todo class need complete refactoring
//public class LanguageButton extends Button {
//    private static LanguageOption currentLanguage;
//    private static List<LanguageOption> availableLanguages;
//    private static LanguageManager languageManager;
//    /**
//     * @return the language chosen by the user
//     */
//    public static LanguageOption getCurrentLanguage() {
//        return LanguageButton.currentLanguage;
//    }
//    /**
//     * Constructs a LanguageButton.
//     * @param path path to the user configuration file
//     */
//    public LanguageButton(String path) {
//        initialize(path);
//    }
//
//    /**
//     * Initializes the language button.
//     */
//    private void initialize(String path) {
//        languageManager = new LanguageManager(path);
//        setGraphic(createFlagIcon()); // Set the initial flag icon
//        setOnMouseClicked(event -> handleMouseClicked(event));
//        loadAvailableLanguages();
//        loadCurrentLanguage(); // Load the persisted language choice
//    }
//
//
//    /**
//     * Loads available languages.
//     */
//    private void loadAvailableLanguages() {
//        availableLanguages = new ArrayList<>();
//        availableLanguages.add(new LanguageOption(LanguageOption.Language.ENGLISH));
//        availableLanguages.add(new LanguageOption(LanguageOption.Language.DUTCH));
//        availableLanguages.add(new LanguageOption(LanguageOption.Language.ROMANIAN));
//
//    }
//
//    /**
//     * Loads the current language.
//     */
//    private void loadCurrentLanguage() {
//        //check code of loadLanguage for more details. In case no language exists in the preferences
//        //the algorithm defaults to english
//        currentLanguage = languageManager.loadLanguage();
//        updateFlagIcon();
//    }
//
//    /**
//     * Creates a flag icon image view.
//     *
//     * @return The flag icon image view.
//     */
//    private ImageView createFlagIcon() {
//        return LanguageManager.createFlagIcon(currentLanguage);
//    }
//
//    /**
//     * Updates the flag icon.
//     */
//    private void updateFlagIcon() {
//        setGraphic(createFlagIcon());
//    }
//
//    /**
//     * Handles mouse clicked event.
//     *
//     * @param event The mouse event.
//     */
//    private void handleMouseClicked(javafx.scene.input.MouseEvent event) {
//        if (event.getButton() == MouseButton.PRIMARY) {
//            showLanguageMenu(event);
//        }
//    }
//
//    /**
//     * Shows the language menu.
//     */
//    private void showLanguageMenu(javafx.scene.input.MouseEvent event) {
//        ContextMenu contextMenu = new ContextMenu();
//
//        // Add menu items for each available language
//        for (LanguageOption languageOption : availableLanguages) {
//
//            MenuItem menuItem =
//                    new MenuItem(languageManager.get(languageOption,languageOption.toString()));
//
//            menuItem.setGraphic(new VBox(LanguageManager.createFlagIcon(languageOption)));
//            menuItem.setOnAction(actionEvent -> selectLanguage(languageOption));
//            contextMenu.getItems().add(menuItem);
//        }
//
//        // Show the context menu below the button
//        contextMenu.show(this, event.getScreenX(), event.getScreenY());
//
//        // Set a listener to hide the menu when the user clicks outside of it
//        contextMenu.setOnHidden(hiddenEvent -> setGraphic(createFlagIcon()));
//    }
//
//    /**
//     * Selects a language.
//     *
//     * @param languageOption is the language you want to switch to
//     */
//    private void selectLanguage(LanguageOption languageOption) {
//        currentLanguage = languageOption;
//        languageManager.saveLanguage(currentLanguage);
//        updateFlagIcon();
//        //this.setText(languageManager.get("Test"));
//    }
//}