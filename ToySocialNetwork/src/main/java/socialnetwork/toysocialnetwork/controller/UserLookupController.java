package socialnetwork.toysocialnetwork.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.MainGUI;
import socialnetwork.toysocialnetwork.controller.message_alerts.MessageAlert;
import socialnetwork.toysocialnetwork.domain.FriendshipSender;
import socialnetwork.toysocialnetwork.domain.FriendshipStatus;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.MessagesService;
import socialnetwork.toysocialnetwork.service.UsersService;
import socialnetwork.toysocialnetwork.utils.events.Event;
import socialnetwork.toysocialnetwork.utils.events.FriendshipsChangedEvent;
import socialnetwork.toysocialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.*;

public class UserLookupController implements Observer<Event> {
    public TableView userListTableView;
    public TableColumn userListFirstNameColumn;
    public TableColumn userListLastNameColumn;
    public TextField userLookupTextField;
    public Button sendRequestButton;

    ObservableList<User> listOfUsers = FXCollections.observableArrayList();

    Stage mainStage;
    User loggedInUser;
    UsersService usersService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;

    User modelSelectedUser;

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
     * Handles the initial setup of the controller.
     * @param usersService A reference to the service responsible for users.
     * @param friendshipsService A reference to the service responsible for friendships.
     * @param loggedInUser A reference to the User currently logged in.
     * @param mainStage A reference to the stage currently in use by the application.
     */
    public void setupController(UsersService usersService, FriendshipsService friendshipsService, MessagesService messagesService, User loggedInUser, Stage mainStage)
    {
        updateControllerFields(usersService, friendshipsService, messagesService, loggedInUser, mainStage);
        listenToTableSelectionChanges();
        userLookupTextField.textProperty().addListener(o -> loadListOfUsers());

        loadListOfUsers();
        friendshipsService.addObserver(this);
    }

    /**
     * It sends out a friend request to the user selected from the table of users.
     * If there is no selected user, it shows an error message.
     */
    @FXML
    void sendFriendRequest()
    {
        if(modelSelectedUser == null)
        {
            MessageAlert.showErrorMessage(mainStage, "You must select a user to send a request to.");
            return;
        }
        friendshipsService.serviceAddFriendship(loggedInUser.getId(), modelSelectedUser.getId(), FriendshipStatus.PENDING, FriendshipSender.FIRST_USER);
    }

    /**
     * Adds a listener to the table of users, so that modelSelectedUser always references the selected user.
     */
    void listenToTableSelectionChanges()
    {
        userListTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                modelSelectedUser = newValue;
            }
        });
    }

    /**
     * Gets a list of all the users that the logged-in user can send friend requests to and populates
     * the userListTableView with them.
     */
    void loadListOfUsers()
    {
        listOfUsers.clear();

        Set<Long> relatedUsers = new HashSet<>();
        usersService.serviceFindAllRelatedUsers(loggedInUser.getId()).forEach(relatedUsers::add);

        for(User u: usersService.serviceFindAll())
        {
            if(Objects.equals(u.getId(), loggedInUser.getId()) || !userMatchesFilter(u))
                continue;
            if(!relatedUsers.contains(u.getId()))
            {
                listOfUsers.add(u);
            }
        }

        userListTableView.setItems(listOfUsers);

        userListFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        userListLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));


    }

    /** TODO Move this to the Utility class
     * Checks to see if the given user matches the look-up pattern given in userLookupTextField
     * @param user
     * @return true, if user matches the look-up pattern, false, otherwise.
     */
    private boolean userMatchesFilter(User user)
    {
        String fullUserName = user.getFirstName() + " " + user.getLastName();
        String fullUserName2 = user.getLastName() + " " + user.getFirstName();

        return fullUserName.contains(userLookupTextField.getText()) || fullUserName2.contains(userLookupTextField.getText());
    }

    /**
     * Loads the previous window the user was on. Is used for the "Back" button.
     * @throws IOException if the given FXML file cannot be loaded.
     */
    @FXML
    void loadPreviousWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("views/friends-list-view.fxml"));

        Scene newScene = new Scene(fxmlLoader.load(), 610, 400);

        FriendsListController friendsListController = fxmlLoader.getController();
        friendsListController.setupController(usersService, friendshipsService, messagesService, loggedInUser, mainStage);

        mainStage.setScene(newScene);
    }

    /**
     * The update method of the Observer interface. It gets called whenever an update is sent.
     * It causes the table of users to update.
     * @param event The event that triggered the update.
     */
    @Override
    public void update(Event event) {
        if(event instanceof FriendshipsChangedEvent)
            loadListOfUsers();
    }
}
