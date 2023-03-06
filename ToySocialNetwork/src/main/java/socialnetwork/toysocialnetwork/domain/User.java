package socialnetwork.toysocialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String userName;


    private List<Long> friends;

    public User(String firstName, String lastName, String userName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        friends = new ArrayList<>();
    }

    public List<Long> getFriends() {
        return friends;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFriends(List<Long> newFriends)
    {
        this.friends = newFriends;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * Adds a given user's ID to another user's inner friend list.
     * @param newFriendID = The user ID to be added to the friend list.
     */
    public boolean addFriend(Long newFriendID)
    {
        if (Objects.equals(this.getId(), newFriendID))
            return false;

        if (newFriendID == null)
            return false;

        return friends.add(newFriendID);
    }

    /**
     * Removes a user with a given ID from this user's friend list.
     * @param friendIDToRemove = The ID of the user to be removed from the friend list.
     */
    public boolean removeFriend(Long friendIDToRemove) {
        return friends.remove(friendIDToRemove);
    }

    /**
     * Converts an object of type user to a string, displaying its ID, firstName, lastName and the list of its friends' IDs.
     */
    @Override
    public String toString() {
        return "ID: " + getId() + " | FN: " + firstName + " | LN: " + lastName + " | UN: " + userName + " | Friends: " + friends +". ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;

        User u = (User) o;

        return this.getFirstName().equals(u.getFirstName()) && this.getLastName().equals(u.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }

}
