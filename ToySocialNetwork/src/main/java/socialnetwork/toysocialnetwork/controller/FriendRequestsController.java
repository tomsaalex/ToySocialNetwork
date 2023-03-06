package socialnetwork.toysocialnetwork.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.MainGUI;
import socialnetwork.toysocialnetwork.controller.message_alerts.MessageAlert;
import socialnetwork.toysocialnetwork.domain.FriendshipSender;
import socialnetwork.toysocialnetwork.domain.FriendshipStatus;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.domain.dto.FriendshipDTO;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.MessagesService;
import socialnetwork.toysocialnetwork.service.UsersService;
import socialnetwork.toysocialnetwork.utils.Constants;
import socialnetwork.toysocialnetwork.utils.events.Event;
import socialnetwork.toysocialnetwork.utils.events.FriendshipsChangedEvent;
import socialnetwork.toysocialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequestsController implements Observer<Event> {

    // Friendship requests tables
    public TableView outgoingFriendshipRequestsTableView;
    public TableView incomingFriendshipRequestsTableView;

    // Incoming requests table columns
    public TableColumn incomingFriendshipRequestFirstNameColumn;
    public TableColumn incomingFriendshipRequestLastNameColumn;
    public TableColumn incomingFriendshipRequestSentOnColumn;

    // Outgoing requests table columns
    public TableColumn outgoingFriendshipRequestFirstNameColumn;
    public TableColumn outgoingFriendshipRequestLastNameColumn;
    public TableColumn outgoingFriendshipRequestSentOnColumn;

    // Buttons that operate on requests
    public Button retractFriendRequestButton;
    public Button acceptFriendRequestButton;
    public Button rejectFriendshipRequestButton;

    // Tab switching buttons
    public Button switchToIncomingButton;
    public Button switchToOutgoingButton;

    UsersService usersService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;

    User loggedInUser;
    Stage mainStage;

    FriendshipDTO selectedIncomingFriendshipDTO;
    FriendshipDTO selectedOutgoingFriendshipRequest;

    ObservableList<FriendshipDTO> incomingFriendRequestsList = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> outgoingFriendRequestsList = FXCollections.observableArrayList();

    /**
     * Initializes the fields that need external data.
     * @param usersService A reference to the service responsible for users.
     * @param friendshipsService A reference to the service responsible for friendships.
     * @param newLoggedInUser A reference to the User currently logged in.
     * @param mainStage A reference to the stage currently in use by the application.
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
     * @param usersService A reference to the service responsible for users.
     * @param friendshipsService A reference to the service responsible for friendships.
     * @param newLoggedInUser A reference to the User currently logged in.
     * @param mainStage A reference to the stage currently in use by the application.
     */
    public void setupController(UsersService usersService, FriendshipsService friendshipsService, MessagesService messagesService, User newLoggedInUser, Stage mainStage)
    {
        updateControllerFields(usersService, friendshipsService, messagesService, newLoggedInUser, mainStage);
        listenToIncomingRequestTableSelectionChanges();
        listenToOutgoingRequestTableSelectionChanges();
        updateIncomingFriendRequestsList();
        updateOutgoingFriendRequestsList();

        friendshipsService.addObserver(this);
    }

    /**
     * Adds a listener to the table of users, so that selectedFriendship always references the selected user.
     */
    void listenToIncomingRequestTableSelectionChanges()
    {
        incomingFriendshipRequestsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FriendshipDTO>() {
            @Override
            public void changed(ObservableValue<? extends FriendshipDTO> observable, FriendshipDTO oldValue, FriendshipDTO newValue) {
                if(newValue == null)
                    return;

                selectedIncomingFriendshipDTO = newValue;
                System.out.println("Selected friendship " + selectedIncomingFriendshipDTO);
            }
        });
    }

    void listenToOutgoingRequestTableSelectionChanges()
    {
        outgoingFriendshipRequestsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FriendshipDTO>() {
            @Override
            public void changed(ObservableValue observable, FriendshipDTO oldValue, FriendshipDTO newValue) {
                if(newValue == null)
                    return;

                selectedOutgoingFriendshipRequest = newValue;
                System.out.println("Selected friendship " + selectedOutgoingFriendshipRequest);
            }
        });
    }


    /**
     * Changes the layout to the one for outgoing friend requests.
     */
    @FXML
    void switchToOutgoingRequests()
    {
        incomingFriendshipRequestsTableView.setVisible(false);
        incomingFriendshipRequestsTableView.setManaged(false);

        outgoingFriendshipRequestsTableView.setVisible(true);
        outgoingFriendshipRequestsTableView.setManaged(true);

        rejectFriendshipRequestButton.setVisible(false);
        rejectFriendshipRequestButton.setManaged(false);

        acceptFriendRequestButton.setVisible(false);
        acceptFriendRequestButton.setManaged(false);

        retractFriendRequestButton.setVisible(true);
        retractFriendRequestButton.setManaged(true);

        switchToOutgoingButton.setStyle("-fx-background-color: #ff0000");
        switchToIncomingButton.setStyle("");
    }

    /**
     * Changes the layout to the one for incoming friend requests.
     */
    @FXML
    void switchToIncomingRequests()
    {
        incomingFriendshipRequestsTableView.setVisible(true);
        incomingFriendshipRequestsTableView.setManaged(true);

        outgoingFriendshipRequestsTableView.setVisible(false);
        outgoingFriendshipRequestsTableView.setManaged(false);

        rejectFriendshipRequestButton.setVisible(true);
        rejectFriendshipRequestButton.setManaged(true);

        acceptFriendRequestButton.setVisible(true);
        acceptFriendRequestButton.setManaged(true);

        retractFriendRequestButton.setVisible(false);
        retractFriendRequestButton.setManaged(false);

        switchToOutgoingButton.setStyle("");
        switchToIncomingButton.setStyle("-fx-background-color: #ff0000");
    }

    /**
     * Accepts the selected friend request from the table.
     */
    @FXML
    void acceptFriendRequest()
    {
        if(selectedIncomingFriendshipDTO == null)
        {
            MessageAlert.showErrorMessage(mainStage, "You must select a friendship from the table first.");
            return;
        }

        Long user1ID = selectedIncomingFriendshipDTO.getUser1Id();
        Long user2ID = selectedIncomingFriendshipDTO.getUser2Id();
        FriendshipSender friendshipSender = selectedIncomingFriendshipDTO.getFriendshipSender();


        boolean updateResult = friendshipsService.serviceUpdateFriendship(user1ID, user2ID, LocalDateTime.now(), friendshipSender, FriendshipStatus.ACCEPTED);

        if(!updateResult)
            MessageAlert.showErrorMessage(mainStage, "The friendship could not be accepted successfully");
    }

    /**
     * Rejects the selected friend request from the table.
     */
    @FXML
    void rejectFriendRequest()
    {
        if(selectedIncomingFriendshipDTO == null)
        {
            MessageAlert.showErrorMessage(mainStage, "You must select a friendship request from the table first.");
            return;
        }

        Long user1ID = selectedIncomingFriendshipDTO.getUser1Id();
        Long user2ID = selectedIncomingFriendshipDTO.getUser2Id();

        boolean deleteResult = friendshipsService.serviceRemoveFriendship(user1ID, user2ID);

        if(!deleteResult)
            MessageAlert.showErrorMessage(mainStage, "The friendship request could not be rejected successfully.");
    }

    /**
     * Retracts the selected friend request from the outgoing friend requests table.
     */
    @FXML
    void retractFriendRequest()
    {
        if(selectedOutgoingFriendshipRequest == null)
        {
            MessageAlert.showErrorMessage(mainStage, "You must select a friendship request from the table first");
            return;
        }

        Long user1ID = selectedOutgoingFriendshipRequest.getUser1Id();
        Long user2ID = selectedOutgoingFriendshipRequest.getUser2Id();

        boolean deleteResult = friendshipsService.serviceRemoveFriendship(user1ID, user2ID);
        if(!deleteResult)
            MessageAlert.showErrorMessage(mainStage, "The friendship request could not be retracted successfully.");
    }
    /**
     * Updates the list of incoming friend requests.
     */
    void updateIncomingFriendRequestsList()
    {
        incomingFriendRequestsList.clear();
        for (FriendshipDTO f : friendshipsService.serviceFindAllWithNames()) {
            if (!Objects.equals(f.getUser2Id(), loggedInUser.getId()) && !Objects.equals(f.getUser1Id(), loggedInUser.getId()))
                continue;

            if(f.getFriendshipStatus() == FriendshipStatus.ACCEPTED)
                continue;

            if((f.getFriendshipSender() == FriendshipSender.FIRST_USER && Objects.equals(f.getUser1Id(), loggedInUser.getId()))
            || (f.getFriendshipSender() == FriendshipSender.SECOND_USER && Objects.equals(f.getUser2Id(), loggedInUser.getId())))
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
            incomingFriendRequestsList.add(f);
        }

        incomingFriendshipRequestsTableView.setItems(incomingFriendRequestsList);

        incomingFriendshipRequestFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("user1FName"));

        incomingFriendshipRequestLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("user1LName"));

        incomingFriendshipRequestSentOnColumn.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));

        incomingFriendshipRequestSentOnColumn.setCellFactory(column -> new TableCell<FriendshipDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(Constants.DEFAULT_DATE_TIME_FORMATTER.format(item));
                }

            }
        });
    }

    /**
     * Updates the list of outgoing friend requests.
     */
    void updateOutgoingFriendRequestsList()
    {
        outgoingFriendRequestsList.clear();
        for (FriendshipDTO f : friendshipsService.serviceFindAllWithNames()) {
            if (!Objects.equals(f.getUser2Id(), loggedInUser.getId()) && !Objects.equals(f.getUser1Id(), loggedInUser.getId()))
                continue;

            if(f.getFriendshipStatus() == FriendshipStatus.ACCEPTED)
                continue;

            if((f.getFriendshipSender() == FriendshipSender.FIRST_USER && Objects.equals(f.getUser2Id(), loggedInUser.getId()))
                    || (f.getFriendshipSender() == FriendshipSender.SECOND_USER && Objects.equals(f.getUser1Id(), loggedInUser.getId())))
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
            outgoingFriendRequestsList.add(f);
        }

        outgoingFriendshipRequestsTableView.setItems(outgoingFriendRequestsList);

        outgoingFriendshipRequestFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("user1FName"));

        outgoingFriendshipRequestLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("user1LName"));

        outgoingFriendshipRequestSentOnColumn.setCellValueFactory(new PropertyValueFactory<>("friendsFrom"));

        outgoingFriendshipRequestSentOnColumn.setCellFactory(column -> new TableCell<FriendshipDTO, LocalDateTime>() {
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
     * The update method of the Observer interface. It gets called whenever an update is sent.
     * It causes the table of users to update.
     * @param event The event that triggered the update.
     */
    @Override
    public void update(Event event) {
        if(event instanceof  FriendshipsChangedEvent)
        {
            updateIncomingFriendRequestsList();
            updateOutgoingFriendRequestsList();
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
