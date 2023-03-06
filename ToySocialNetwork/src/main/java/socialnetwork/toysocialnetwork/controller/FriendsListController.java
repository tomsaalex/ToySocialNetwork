package socialnetwork.toysocialnetwork.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.MainGUI;
import socialnetwork.toysocialnetwork.controller.message_alerts.MessageAlert;
import socialnetwork.toysocialnetwork.domain.FriendshipStatus;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.domain.dto.FriendshipDTO;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.MessagesService;
import socialnetwork.toysocialnetwork.service.UsersService;
import socialnetwork.toysocialnetwork.utils.Constants;
import socialnetwork.toysocialnetwork.utils.events.Event;
import socialnetwork.toysocialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

public class FriendsListController implements Observer<Event> {
    public Button deleteFriendButton;
    public TableView friendsListTableView;
    public TableColumn friendsListFirstNameColumn;
    public TableColumn friendsListDateColumn;
    public TableColumn friendsListLastNameColumn;
    Stage mainStage;
    User loggedInUser;

    UsersService usersService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;

    ObservableList<FriendshipDTO> fullFriendsList = FXCollections.observableArrayList();

    FriendshipDTO selectedFriendshipDTO;

    /**
     * The update method of the Observer interface. It gets called whenever an update is sent.
     * It causes the table of users to update.
     * @param event The event that triggered the update.
     */
    @Override
    public void update(Event event) {
        updateFriendsList();
    }

    /**
     * Initializes the fields that need external data.
     *
     * @param usersService       A reference to the service responsible for users.
     * @param friendshipsService A reference to the service responsible for friendships.
     * @param newLoggedInUser    A reference to the User currently logged in.
     * @param mainStage          A reference to the stage currently in use by the application.
     */
    private void updateControllerFields(UsersService usersService, FriendshipsService friendshipsService, MessagesService messagesService, User newLoggedInUser, Stage mainStage) {
        this.usersService = usersService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.loggedInUser = newLoggedInUser;
        this.mainStage = mainStage;
    }


    /**
     * Handles the initial setup of the controller.
     *
     * @param usersService       A reference to the service responsible for users.
     * @param friendshipsService A reference to the service responsible for friendships.
     * @param loggedInUser       A reference to the User currently logged in.
     * @param mainStage          A reference to the stage currently in use by the application.
     */
    public void setupController(UsersService usersService, FriendshipsService friendshipsService, MessagesService messagesService, User loggedInUser, Stage mainStage) {
        updateControllerFields(usersService, friendshipsService, messagesService, loggedInUser, mainStage);
        updateFriendsList();
        listenToSelectionChanges();

        friendshipsService.addObserver(this);
    }

    /**
     * Adds a listener to the table of users, so that selectedFriendshipDTO always references the selected user.
     */
    private void listenToSelectionChanges() {
        friendsListTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FriendshipDTO>() {
            @Override
            public void changed(ObservableValue<? extends FriendshipDTO> observable, FriendshipDTO oldValue, FriendshipDTO newValue) {
                if (newValue == null)
                    return;

                selectedFriendshipDTO = newValue;
            }
        });
    }

    /**
     * Deletes the friendship stored in selectedFriendshipDTO, selected from the table of users.
     */
    @FXML
    private void deleteSelectedFriendship() {
        if (selectedFriendshipDTO == null) {
            MessageAlert.showErrorMessage(mainStage, "You have to select a friend to delete first");
            return;
        }

        boolean deletionResult = friendshipsService.serviceRemoveFriendship(selectedFriendshipDTO.getUser1Id(), selectedFriendshipDTO.getUser2Id());
        //if (deletionResult) {
        //    MessageAlert.showMessage(mainStage, Alert.AlertType.INFORMATION, "The deletion was performed successfully", "Deletion successful.");
        if(!deletionResult) {
            MessageAlert.showErrorMessage(mainStage, "Deletion failed");
        }
    }

    /**
     * Gets a list of all the logged-in user's friends and populates
     * the friendsListDateColumn with them.
     */
    void updateFriendsList() {
        fullFriendsList.clear();
        for (FriendshipDTO f : friendshipsService.serviceFindAllWithNames()) {
            if (!Objects.equals(f.getUser2Id(), loggedInUser.getId()) && !Objects.equals(f.getUser1Id(), loggedInUser.getId()))
                continue;

            if(f.getFriendshipStatus() != FriendshipStatus.ACCEPTED)
                continue;

            if (Objects.equals(f.getUser1Id(), loggedInUser.getId())) {
                String firstNameAux = f.getUser1FName();
                f.setUser1FName(f.getUser2FName());
                f.setUser2FName(firstNameAux);

                String lastNameAux = f.getUser1LName();
                f.setUser1LName(f.getUser2LName());
                f.setUser2LName(lastNameAux);


                Long userIDAux = f.getUser1Id();
                f.setUser1Id(f.getUser2Id());
                f.setUser2Id(userIDAux);
            }
            fullFriendsList.add(f);
        }

        friendsListTableView.setItems(fullFriendsList);

        friendsListFirstNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("user1FName"));
        friendsListLastNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("user1LName"));
        friendsListDateColumn.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));

        friendsListDateColumn.setCellFactory(column -> new TableCell<FriendshipDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(Constants.DEFAULT_DATE_TIME_FORMATTER.format(item));
                }
            }

            ;
        });
    }

    /**
     * Loads the user lookup window used for sending friend requests.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @FXML
    void loadFriendRequestWindow() throws IOException {
        URL url = MainGUI.class.getResource("views/user-lookup-view.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);

        Scene friendsListScene = new Scene(fxmlLoader.load(), 610, 400);

        UserLookupController userLookupController = fxmlLoader.getController();
        userLookupController.setupController(usersService, friendshipsService, messagesService, loggedInUser, mainStage);

        mainStage.setScene(friendsListScene);
    }

    /**
     * Loads the previous window the user was on. Is used for the "Back" button.
     * @throws IOException if the given FXML file cannot be loaded.
     */
    @FXML
    void loadPreviousWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("views/main-menu-view.fxml"));

        Scene newScene = new Scene(fxmlLoader.load(), 300, 300);

        MainMenuController mainMenuController = fxmlLoader.getController();
        mainMenuController.setupController(usersService, friendshipsService, messagesService, loggedInUser, mainStage);

        mainStage.setScene(newScene);
    }



}
