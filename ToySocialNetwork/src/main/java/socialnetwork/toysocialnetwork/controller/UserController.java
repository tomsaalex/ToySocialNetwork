package socialnetwork.toysocialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.domain.dto.FriendshipDTO;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.UsersService;
import socialnetwork.toysocialnetwork.utils.Constants;
import socialnetwork.toysocialnetwork.utils.events.UsersChangedEvent;
import socialnetwork.toysocialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserController implements Observer<UsersChangedEvent> {

    @FXML
    public TableColumn usersListFirstNameColumn;
    @FXML
    public TableColumn usersListLastNameColumn;
    @FXML
    public TableView usersListTableView;

    @FXML
    public TableColumn friendsListFirstNameColumn;
    @FXML
    public TableColumn friendsListLastNameColumn;
    @FXML
    public TableColumn friendsListDateColumn;
    @FXML
    public TableView friendsListTableView;

    @FXML
    public Label usernameLabel;


    Stage mainStage;
    User loggedInUser;
    UsersService usersService;
    FriendshipsService friendshipsService;

    ObservableList<User> fullUsersList = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> fullFriendsList = FXCollections.observableArrayList();

    private void updateControllerFields(UsersService usersService, FriendshipsService friendshipsService, User newLoggedInUser, Stage mainStage)
    {
        this.usersService = usersService;
        this.friendshipsService = friendshipsService;
        this.loggedInUser = newLoggedInUser;
        this.mainStage = mainStage;
    }
    private void updateControllerUI()
    {
        usernameLabel.setText(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
    }
    public void setupController(UsersService usersService, FriendshipsService friendshipsService, User newLoggedInUser, Stage mainStage)
    {
        updateControllerFields(usersService, friendshipsService, newLoggedInUser, mainStage);
        updateControllerUI();
        updateUsersList();
        updateFriendsList();
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void setUsersService(UsersService usersService)
    {
        this.usersService = usersService;
    }
    public void setFriendshipsService(FriendshipsService friendshipsService)
    {
        this.friendshipsService = friendshipsService;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    void updateUsersList()
    {
        fullUsersList.clear();
        for (User u: usersService.serviceFindAll()) {
            fullUsersList.add(u);
        }

        usersListTableView.setItems(fullUsersList);

        usersListFirstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        usersListLastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

    }

    void updateFriendsList()
    {
        fullFriendsList.clear();
        for(FriendshipDTO f: friendshipsService.serviceFindAllWithNames())
        {
            if(!Objects.equals(f.getUser2Id(), loggedInUser.getId()) && !Objects.equals(f.getUser1Id(), loggedInUser.getId()))
                continue;
            if(Objects.equals(f.getUser1Id(), loggedInUser.getId()))
            {
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

        friendsListDateColumn.setCellFactory(column -> new TableCell<FriendshipDTO, LocalDateTime>(){
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(Constants.DEFAULT_DATE_TIME_FORMATTER.format(item));
                    }
                };
        });
    }

    @Override
    public void update(UsersChangedEvent usersChangedEvent) {
        updateUsersList();
    }
}
