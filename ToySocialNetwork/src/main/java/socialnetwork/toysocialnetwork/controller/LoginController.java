package socialnetwork.toysocialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.MainGUI;
import socialnetwork.toysocialnetwork.controller.message_alerts.MessageAlert;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.MessagesService;
import socialnetwork.toysocialnetwork.service.UsersService;

import java.io.IOException;
import java.net.URL;

public class LoginController {
    @FXML
    public TextField usernameTextField;
    @FXML
    public TextField passwordTextField;

    private UsersService usersService;
    private FriendshipsService friendshipsService;
    private MessagesService messagesService;

    Stage mainStage;

    public void setupController(UsersService usersService, FriendshipsService friendshipsService, MessagesService messagesService, Stage mainStage)
    {
        this.usersService = usersService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.mainStage = mainStage;
    }

    /**
     * Loads the main menu window of the application.
     * @param foundUser The user logged into the application.
     * @throws IOException If the given FXML file cannot be loaded.
     */
    private void loadMainMenu(User foundUser) throws IOException {
        URL url = MainGUI.class.getResource("views/main-menu-view.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);

        Scene userScene = new Scene(fxmlLoader.load(), 300, 300);

        MainMenuController menuController = fxmlLoader.getController();
        menuController.setupController(usersService, friendshipsService, messagesService, foundUser, mainStage);

        mainStage.setScene(userScene);
    }

    @FXML
    private void loadRegisterWindow() throws IOException {
        URL url = MainGUI.class.getResource("views/register-view.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);

        Scene registerScene = new Scene(fxmlLoader.load(), 325, 525);

        RegisterController registerController = fxmlLoader.getController();
        registerController.setupController(usersService, friendshipsService, mainStage);

        mainStage.setScene(registerScene);
    }



    /**
     * Tries to log in a user with the username given in usernameTextField.
     */
    @FXML
    private void onLoginButtonClick()
    {
        String userName = usernameTextField.getCharacters().toString();
        User foundUser = usersService.serviceGetUserByUsername(userName);

        if(foundUser == null)
            MessageAlert.showErrorMessage(mainStage, "No user matching the username was found.");
        else
        {
            try{
                loadMainMenu(foundUser);
            }catch (IOException ioe)
            {
                MessageAlert.showErrorMessage(mainStage, "Login failed");
            }
        }
    }

}
