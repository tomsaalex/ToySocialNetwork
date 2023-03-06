package socialnetwork.toysocialnetwork.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.MainGUI;
import socialnetwork.toysocialnetwork.domain.Message;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.MessagesService;
import socialnetwork.toysocialnetwork.service.UsersService;
import socialnetwork.toysocialnetwork.utils.events.Event;
import socialnetwork.toysocialnetwork.utils.events.FriendshipsChangedEvent;
import socialnetwork.toysocialnetwork.utils.events.MessagesChangedEvent;
import socialnetwork.toysocialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.Objects;

public class MessagingWindowController implements Observer<Event> {
    public VBox chatLogVBox;
    public TableView friendsTableView;
    public TableColumn friendsTableFirstNameColumn;
    public TableColumn friendsTableLastNameColumn;
    public Label chattingFriendNameLabel;
    public TextField sendMessageTextField;
    public ScrollPane chatScrollPane;

    ObservableList<User> friendsObservableList = FXCollections.observableArrayList();

    private UsersService usersService;
    private FriendshipsService friendshipsService;
    private MessagesService messagesService;

    private User loggedInUser;
    private User selectedUser;
    Stage mainStage;



    void updateChatUI()
    {
        if(selectedUser == null)
            return;
        chattingFriendNameLabel.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());

        updateMessagePanel();
    }

    void listenToScrollPaneSizeChanges()
    {
        chatLogVBox.heightProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldvalue, Object newValue) {

                chatScrollPane.setVvalue((Double)newValue );
            }
        });
    }
    void listenToFriendSelectionChanges()
    {
        friendsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue observable, User oldValue, User newValue) {
                if(newValue == null)
                    return;

                selectedUser = newValue;
                updateChatUI();
            }
        });
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
        listenToFriendSelectionChanges();
        listenToScrollPaneSizeChanges();

        messagesService.addObserver(this);
        friendshipsService.addObserver(this);
    }

    void updateFriendsList()
    {
        friendsObservableList.clear();

        for (Long uID : usersService.serviceFindAllRelatedUsers(loggedInUser.getId())) {
            User foundUser = usersService.serviceGetUserByID(uID);
            if(foundUser != null)
                friendsObservableList.add(foundUser);
        }

        friendsTableView.setItems(friendsObservableList);
        friendsTableFirstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        friendsTableLastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
    }

    private void updateMessagePanel() {
        if(selectedUser.getId() == null)
        {
            return;
        }

        chatLogVBox.getChildren().clear();
        Iterable<Message> messagesBetweenUsers = messagesService.serviceFindAllBetweenTwoUsers(loggedInUser.getId(), selectedUser.getId());
        for(Message m: messagesBetweenUsers)
        {
            addMessageBubble(m);
        }
    }

    @FXML
    void sendMessage()
    {
        if(selectedUser == null)
            return;

        messagesService.serviceAddMessage(loggedInUser.getId(), selectedUser.getId(), sendMessageTextField.getText());
        sendMessageTextField.clear();
    }

    @FXML
    void addMessageBubble(Message message)
    {
        VBox outerVBox = new VBox();
        HBox innerHBox = new HBox();
        Text reallyInnerText = new Text(message.getContent());

        reallyInnerText.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 14));
        reallyInnerText.setFill(Color.WHITE);
        reallyInnerText.setWrappingWidth(190);
        reallyInnerText.setTextAlignment(TextAlignment.CENTER);

        outerVBox.getChildren().add(innerHBox);

        if(Objects.equals(message.getReceiverID(), loggedInUser.getId())) {
            outerVBox.setAlignment(Pos.CENTER_LEFT);
            innerHBox.setStyle("-fx-background-color: #ff0000");
        }
        else {
            outerVBox.setAlignment(Pos.CENTER_RIGHT);
            innerHBox.setStyle("-fx-background-color: #00ff00");
        }

        innerHBox.getChildren().add(reallyInnerText);


        innerHBox.setAlignment(Pos.CENTER);
        innerHBox.setMinHeight(reallyInnerText.getBoundsInLocal().getHeight());

        innerHBox.setPrefWidth(190);
        innerHBox.setMinWidth(190);
        innerHBox.setMaxWidth(190);



        VBox.setMargin(outerVBox, new Insets(15, 0, 5, 5));
        chatLogVBox.getChildren().add(outerVBox);
    }

    @Override
    public void update(Event event) {
        if(event instanceof FriendshipsChangedEvent)
        {
            updateFriendsList();
        }
        else if(event instanceof MessagesChangedEvent)
        {
            updateMessagePanel();
        }
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
