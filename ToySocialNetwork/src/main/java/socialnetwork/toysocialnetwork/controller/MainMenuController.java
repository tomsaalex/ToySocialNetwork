package socialnetwork.toysocialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.MainGUI;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.MessagesService;
import socialnetwork.toysocialnetwork.service.UsersService;

import java.io.IOException;
import java.net.URL;

public class MainMenuController {

    public Button logoutButton;
    Stage mainStage;
    User loggedInUser;
    UsersService usersService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;

    @FXML
    public Label usernameLabel;

    /**
     * Initializes the fields that need external data.
     * @param usersService A reference to the service responsible for users.
     * @param friendshipsService A reference to the service responsible for friendships.
     * @param newLoggedInUser A reference to the User currently logged in.
     * @param mainStage A reference to the stage currently in use by the application.
     */
    private void updateControllerFields(UsersService usersService, FriendshipsService friendshipsService, MessagesService messagesService, User newLoggedInUser, Stage mainStage)
    {
        this.usersService = usersService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.loggedInUser = newLoggedInUser;
        this.mainStage = mainStage;
    }

    /**
     * Used to adjust the default UI of this window according to the controller (e.g. Customizing test based on the user).
     */
    private void updateInitialControllerUI()
    {
        usernameLabel.setText(loggedInUser.getFirstName());
        logoutButton.setStyle("-fx-background-color: #ff0000;");
    }

    /**
     * Handles the initial setup of the controller.
     * @param usersService A reference to the service responsible for users.
     * @param friendshipsService A reference to the service responsible for friendships.
     * @param loggedInUser A reference to the User currently logged in.
     * @param mainStage A reference to the stage currently in use by the application.
     */
    public void setupController(UsersService usersService, FriendshipsService friendshipsService, MessagesService messagesService, User loggedInUser, Stage mainStage)
    {
        updateControllerFields(usersService, friendshipsService, messagesService, loggedInUser, mainStage);
        updateInitialControllerUI();
    }

    /**
     * Used to transition from the main menu to the loggedInUser's list of friends.
     */
    @FXML
    private void loadFriendsListWindow() throws IOException {
        URL url = MainGUI.class.getResource("views/friends-list-view.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);

        Scene friendsListScene = new Scene(fxmlLoader.load(), 610, 400);

        FriendsListController friendsListController = fxmlLoader.getController();
        friendsListController.setupController(usersService, friendshipsService, messagesService, loggedInUser, mainStage);

        mainStage.setScene(friendsListScene);
    }

    /** Loads the friend request management window.
     * @throws IOException if the given FXML file cannot be loaded.
     */
    @FXML
    void loadRequestsManagementWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("views/friend-requests-view.fxml"));

        Scene requestManagementScene = new Scene(fxmlLoader.load(), 600, 360);

        FriendRequestsController friendRequestsController = fxmlLoader.getController();
        friendRequestsController.setupController(usersService, friendshipsService, messagesService, loggedInUser, mainStage);

        mainStage.setScene(requestManagementScene);
    }

    @FXML
    void loadLoginWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("views/login-view.fxml"));

        Scene loginScene = new Scene(fxmlLoader.load(), 325, 475);

        LoginController loginController = fxmlLoader.getController();
        loginController.setupController(usersService, friendshipsService, messagesService, mainStage);

        mainStage.setScene(loginScene);
    }

    @FXML
    void loadMessagingWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("views/messaging-view.fxml"));

        Scene messagingScene = new Scene(fxmlLoader.load(), 630, 340);

        MessagingWindowController messagingWindowController = fxmlLoader.getController();
        messagingWindowController.setupController(usersService, friendshipsService, messagesService, loggedInUser, mainStage);
        mainStage.setScene(messagingScene);
    }
}
