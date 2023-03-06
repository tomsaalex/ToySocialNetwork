package socialnetwork.toysocialnetwork.service;

import socialnetwork.toysocialnetwork.domain.Friendship;
import socialnetwork.toysocialnetwork.domain.FriendshipSender;
import socialnetwork.toysocialnetwork.domain.FriendshipStatus;
import socialnetwork.toysocialnetwork.domain.User;
import socialnetwork.toysocialnetwork.domain.dto.FriendshipDTO;
import socialnetwork.toysocialnetwork.graph.GraphManager;
import socialnetwork.toysocialnetwork.repository.database.DBFriendshipRepository;
import socialnetwork.toysocialnetwork.repository.database.DBUsersRepository;
import socialnetwork.toysocialnetwork.utils.events.ChangedEventType;
import socialnetwork.toysocialnetwork.utils.events.Event;
import socialnetwork.toysocialnetwork.utils.events.FriendshipsChangedEvent;
import socialnetwork.toysocialnetwork.utils.observer.Observable;
import socialnetwork.toysocialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The information expert for friendships.
 */
public class FriendshipsService implements Observable<Event> {
    public FriendshipsService(DBFriendshipRepository friendshipsRepository, DBUsersRepository usersRepository) {
        this.friendshipsRepository = friendshipsRepository;
        this.usersRepository = usersRepository;
        graphManager = new GraphManager();
        //loadUsersFriendLists();
    }

    private final DBFriendshipRepository friendshipsRepository;
    private final DBUsersRepository usersRepository;
    private final GraphManager graphManager;
    private final List<Observer<Event>> observers = new ArrayList<>();

    /**
     * Returns a list of DTOs of all the friendships with the names and IDs of the users involved.
     * @return A list of FriendshipDTOs.
     */
    public Iterable<FriendshipDTO> serviceFindAllWithNames()
    {
        return friendshipsRepository.findAllWithNames();
    }

    /**
     * A function used for loading all the friendships from the friendship repo into the internal friends list of the users.
     * TODO: Only affects file repositories.
     */
    private void loadUsersFriendLists() {
        for (Friendship f : friendshipsRepository.findAll()) {
            this.usersRepository.findOne(f.getU1ID()).addFriend(f.getU2ID());
            this.usersRepository.findOne(f.getU2ID()).addFriend(f.getU1ID());
        }
    }

    /**
     * Encapsulates all the logic for adding a friendship into the application.
     *
     * @param user1ID The ID of the first user of the friendship.
     * @param user2ID The ID of the second user of the friendship.
     * @return false, if the addition fails. true, if it succeeds.
     */
    public boolean serviceAddFriendship(Long user1ID, Long user2ID, FriendshipStatus status, FriendshipSender sender) {
        if (user2ID < user1ID) {
            Long aux = user1ID;
            user1ID = user2ID;
            user2ID = aux;

            if(sender == FriendshipSender.FIRST_USER)
                sender = FriendshipSender.SECOND_USER;
            else
                sender = FriendshipSender.FIRST_USER;
        }

        User user1 = usersRepository.findOne(user1ID);
        User user2 = usersRepository.findOne(user2ID);

        if (user1 == null || user2 == null || user1 == user2) {
            return false;
        }

        Friendship newFriendship = new Friendship(user1ID, user2ID, status, sender);
        //newFriendship.setId(friendshipsRepository.getNextID());
        Friendship res = friendshipsRepository.save(newFriendship);

        if (res == null) {
            if(newFriendship.getStatus() == FriendshipStatus.ACCEPTED) {
                user1.addFriend(user2.getId());
                user2.addFriend(user1.getId());
            }
            notifyObservers(new FriendshipsChangedEvent(ChangedEventType.ADD, newFriendship));
            return true;
        }

        return false;
    }

    /**
     * Encapsulates all the logic for removing a friendship from the application.
     *
     * @param user1ID The ID of the first user of the friendship.
     * @param user2ID The ID of the second user of the friendship.
     * @return false, if the removal fails. true, if it succeeds.
     */
    public boolean serviceRemoveFriendship(Long user1ID, Long user2ID) {
        if (user2ID < user1ID) {
            Long aux = user1ID;
            user1ID = user2ID;
            user2ID = aux;
        }
        User user1 = usersRepository.findOne(user1ID);
        User user2 = usersRepository.findOne(user2ID);

        if (user1 == null || user2 == null)
            return false;

        user1.removeFriend(user2.getId());

        user2.removeFriend(user1.getId());

        Friendship friendshipToSearch = new Friendship(user1ID, user2ID);

        Friendship foundFriendship = friendshipsRepository.searchByValue(friendshipToSearch);

        if(foundFriendship == null)
            return false;

        Friendship deletionResult = friendshipsRepository.delete(foundFriendship.getId());

        if(deletionResult != null)
        {
            notifyObservers(new FriendshipsChangedEvent(ChangedEventType.DELETE, foundFriendship));
            return true;
        }
        return false;

    }

    /**
     * Debug command returning the entire content of the repo of friendships.
     *
     * @return An iterable going over all the friendships.
     */
    public Iterable<Friendship> serviceDebugPrintAllFriendships() {
        return friendshipsRepository.findAll();
    }

    /**
     * Manages the logic for getting the number of connected components of the network.
     *
     * @return Returns the number of connected components found.
     */
    public int serviceCountConnectedComponents() {
        return graphManager.countConnectedComponents(usersRepository.findAll(), friendshipsRepository.findAll());
    }

    /**
     * Manages the logic for getting the most sociable community in the network (the one with the longest path in it).
     *
     * @return Returns the members of said community.
     */
    public List<User> serviceGetMostSociableCommunity() {
        List<Long> userIDs = graphManager.getLongestPathComponent(usersRepository.findAll(), friendshipsRepository.findAll());
        List<User> usersFromComponent = new ArrayList<>();

        userIDs.forEach(uID -> usersFromComponent.add(usersRepository.findOne(uID)));
        return usersFromComponent;
    }


    /**
     * Manages the service logic for updating a friendship.
     *
     * @param user1ID     The id of the first user in the friendship.
     * @param user2ID     The id of the second user in the friendship.
     * @param friendsFrom The date and time when the 2 users became friends.
     * @return true, if the update succeeds. false, if the update false.
     */
    public boolean serviceUpdateFriendship(Long user1ID, Long user2ID, LocalDateTime friendsFrom, FriendshipSender sender, FriendshipStatus status) {
        if (user2ID < user1ID) {
            Long aux = user1ID;
            user1ID = user2ID;
            user2ID = aux;
        }

        Friendship newFriendship = new Friendship(user1ID, user2ID);

        Friendship oldFriendship = friendshipsRepository.searchByValue(newFriendship);

        newFriendship.setFriendsFrom(friendsFrom);
        newFriendship.setSender(sender);
        newFriendship.setStatus(status);

        Friendship updateResult = friendshipsRepository.update(newFriendship);

        User user1 = usersRepository.findOne(user1ID);
        User user2 = usersRepository.findOne(user2ID);

        if(updateResult == null)
        {
            if(oldFriendship.getStatus() == FriendshipStatus.PENDING && newFriendship.getStatus() == FriendshipStatus.ACCEPTED)
            {
                user1.addFriend(user2.getId());
                user2.addFriend(user1.getId());
            }
            notifyObservers(new FriendshipsChangedEvent(ChangedEventType.UPDATE, updateResult, oldFriendship));
            return true;
        }

        return false;
    }

    @Override
    public void addObserver(Observer<Event> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<Event> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(Event event) {
        observers.stream().forEach(x -> x.update(event));
    }
}
