package socialnetwork.toysocialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.controller.LoginController;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.toysocialnetwork.domain.validators.MessageValidator;
import socialnetwork.toysocialnetwork.domain.validators.UserValidator;
import socialnetwork.toysocialnetwork.repository.database.DBFriendshipRepository;
import socialnetwork.toysocialnetwork.repository.database.DBMessageRepository;
import socialnetwork.toysocialnetwork.repository.database.DBUsersRepository;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.MessagesService;
import socialnetwork.toysocialnetwork.service.UsersService;

import java.io.IOException;

public class MainGUI extends Application {

    User loggedInUser;
    DBUsersRepository dbUsersRepository;
    DBFriendshipRepository dbFriendshipsRepository;
    DBMessageRepository dbMessageRepository;
    UsersService usersService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;

    private void initView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 325, 475);

        LoginController loginController = fxmlLoader.getController();
        loginController.setupController(usersService, friendshipsService, messagesService, stage);

        stage.setScene(scene);
        stage.setTitle("Log in");
    }

    @Override
    public void start(Stage stage) throws IOException {
        dbUsersRepository = new DBUsersRepository(new UserValidator(), "jdbc:postgresql://localhost:5432/MAP_Labs", "postgres", "postgres");
        dbFriendshipsRepository = new DBFriendshipRepository(new FriendshipValidator(), "jdbc:postgresql://localhost:5432/MAP_Labs", "postgres", "postgres");
        dbMessageRepository = new DBMessageRepository(new MessageValidator(), "jdbc:postgresql://localhost:5432/MAP_Labs", "postgres", "postgres");
        usersService = new UsersService(dbFriendshipsRepository, dbUsersRepository);
        friendshipsService = new FriendshipsService(dbFriendshipsRepository, dbUsersRepository);
        messagesService = new MessagesService(dbMessageRepository);

        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        //secondStage.initStyle(StageStyle.UNDECORATED);
        secondStage.setTitle("ABC");

        initView(stage);
        initView(secondStage);

        secondStage.show();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}