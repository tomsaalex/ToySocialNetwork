package socialnetwork.toysocialnetwork.service;

import socialnetwork.toysocialnetwork.domain.Friendship;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.repository.Repository;
import socialnetwork.toysocialnetwork.repository.database.DBUsersRepository;
import socialnetwork.toysocialnetwork.utils.events.UsersChangedEvent;
import socialnetwork.toysocialnetwork.utils.observer.Observable;
import socialnetwork.toysocialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * The information expert for the users.
 */
public class UsersService implements Observable<UsersChangedEvent> {

    private final DBUsersRepository usersRepository;
    private final Repository friendshipsRepository;

    private final List<Observer<UsersChangedEvent>> observers = new ArrayList<>();

    public UsersService(Repository friendshipsRepository, DBUsersRepository usersRepository) {
        this.friendshipsRepository = friendshipsRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * Finds all the users that the given user is related to, either by an accepted friend request, or by a pending one.
     * @param userID The ID of the given user.
     * @return An iterable over the IDs of all the users respecting the requirement.
     */
    public Iterable<Long> serviceFindAllRelatedUsers(Long userID)
    {
        return usersRepository.findUsersRelatedTo(userID);
    }

    /**
     * Returns all the users in the application.
     * @return an iterable over all the users in the application.
     */
    public Iterable<User> serviceFindAll()
    {
        return usersRepository.findAll();
    }

    /**
     * Manages all the logic for adding a user into the application.
     * @param firstName The first name of the user to add.
     * @param lastName The last name of the user to add.
     * @return true, if the addition succeeds. false, if it fails.
     */
    public boolean serviceAddUser(String firstName, String lastName, String userName) {
        User newUser = new User(firstName, lastName, userName);
        //newUser.setId(usersRepository.getNextID());

        return usersRepository.save(newUser) == null;

    }

    /**
     * Encapsulates all the logic for removing a user from the application.
     * @param userID The ID of the user to remove.
     * @return false, if the removal fails. true, if it succeeds.
     */
    public boolean serviceDeleteUser(long userID) {
        Iterable<Friendship> friendships = friendshipsRepository.findAll();

        List<Friendship> friendshipList = new ArrayList<>();

        friendships.forEach(friendshipList::add);

        for(Friendship f: friendshipList)
        {
            if(f.getU1ID() == userID || f.getU2ID() == userID)
                friendshipsRepository.delete(f.getId());

        }

        return usersRepository.delete(userID) == null;
    }

    /**
     * Gets the user from the repository who has the given username.
     * @param userName The username searched for.
     * @return The user with the given username, if it exists. null, otherwise.
     */
    public User serviceGetUserByUsername(String userName)
    {
        return usersRepository.findByUsername(userName);
    }

    /**
     * Gets the user from the repository who has the given ID.
     * @param ID The ID we're searching for
     * @return The user with the given ID, if it exists. null, otherwise.
     */
    public User serviceGetUserByID(Long ID)
    {
        return usersRepository.findOne(ID);
    }

    /**
     * Manages all the logic for updating a user that already exists in the application.
     * @param userID The id of the user you want to update
     * @param newFirstName The new first name you want to give the user.
     * @param newLastName The new last name you want to give the user.
     * @return true, if the user was updated successfully. false, if it fails.
     */
    public boolean serviceUpdateUser(Long userID, String newFirstName, String newLastName, String newUserName)
    {
        User incompleteUserValue = new User(newFirstName, newLastName, newUserName);

        User existingUser = usersRepository.findOne(userID);

        if(existingUser == null)
        {
            return false;
        }

        incompleteUserValue.setId(existingUser.getId());
        incompleteUserValue.setFriends(existingUser.getFriends());

        return usersRepository.update(incompleteUserValue) == null;
    }

    /**
     * Debug command returning the entire content of the repo of users.
     * @return An iterable going over all the users.
     */
    public Iterable<User> serviceDebugPrintAllUsers() {
        return usersRepository.findAll();
    }

    public void addObserver(Observer<UsersChangedEvent> e)
    {
        observers.add(e);
    }

    public void removeObserver(Observer<UsersChangedEvent> e)
    {
        observers.remove(e);
    }

    public void notifyObservers(UsersChangedEvent e)
    {
        observers.stream().forEach(x -> x.update(e));
    }


}
