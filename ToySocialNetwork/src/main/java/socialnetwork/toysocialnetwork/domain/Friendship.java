package socialnetwork.toysocialnetwork.domain;

import socialnetwork.toysocialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Long>{
    Long u1ID, u2ID;
    FriendshipStatus status;
    FriendshipSender sender;
    LocalDateTime friendsFrom;

    //TODO: remove this constructor after the refactoring is done
    public Friendship(Long u1ID, Long u2ID, LocalDateTime friendsFrom) {
        this.u1ID = u1ID;
        this.u2ID = u2ID;
        this.friendsFrom = LocalDateTime.parse(friendsFrom.format(Constants.DEFAULT_DATE_TIME_FORMATTER), Constants.DEFAULT_DATE_TIME_FORMATTER) ;
        this.sender = null;
        this.status = null;
    }

    //TODO - replace this after the refactoring is done. It needs sender and status.
    public Friendship(Long u1ID, Long u2ID) {
        this.u1ID = u1ID;
        this.u2ID = u2ID;
        this.friendsFrom = LocalDateTime.parse(LocalDateTime.now().format(Constants.DEFAULT_DATE_TIME_FORMATTER), Constants.DEFAULT_DATE_TIME_FORMATTER); // TODO I hope this works

        this.sender = null;
        this.status = null;
    }

    public Friendship(Long u1ID, Long u2ID, FriendshipSender sender, LocalDateTime friendsFrom) {
        this.u1ID = u1ID;
        this.u2ID = u2ID;
        this.friendsFrom = LocalDateTime.parse(friendsFrom.format(Constants.DEFAULT_DATE_TIME_FORMATTER), Constants.DEFAULT_DATE_TIME_FORMATTER) ;
        this.sender = sender;
        this.status = FriendshipStatus.PENDING;
    }

    public Friendship(Long u1ID, Long u2ID, LocalDateTime friendsFrom, FriendshipSender sender, FriendshipStatus status) {
        this.u1ID = u1ID;
        this.u2ID = u2ID;
        this.status = status;
        this.sender = sender;
        this.friendsFrom = LocalDateTime.parse(friendsFrom.format(Constants.DEFAULT_DATE_TIME_FORMATTER), Constants.DEFAULT_DATE_TIME_FORMATTER) ;
    }

    public Friendship(Long u1ID, Long u2ID, FriendshipStatus status, FriendshipSender sender) {
        this.u1ID = u1ID;
        this.u2ID = u2ID;
        this.status = status;
        this.sender = sender;
        this.friendsFrom = LocalDateTime.parse(LocalDateTime.now().format(Constants.DEFAULT_DATE_TIME_FORMATTER), Constants.DEFAULT_DATE_TIME_FORMATTER);
    }

    public Long getU1ID() {
        return u1ID;
    }
    public Long getU2ID() {
        return u2ID;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public FriendshipSender getSender() {
        return sender;
    }

    public void setFriendsFrom(LocalDateTime newFriendsFrom)
    {
        this.friendsFrom = newFriendsFrom;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public void setSender(FriendshipSender sender) {
        this.sender = sender;
    }

    /**
     * Converts an object of type Friendship to a string, displaying its ID, firstName, lastName and the list of its friends' IDs.
     */
    @Override
    public String toString()
    {
        return "ID User1: " + getU1ID() + " | ID User2: " + getU2ID() + " | Friends From: " + getFriendsFrom();
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;

        if(!(o instanceof Friendship))
            return false;

        Friendship f = (Friendship) o;

        boolean equals1 = Objects.equals(f.getU1ID(), this.getU1ID()) && Objects.equals(f.getU2ID(), this.getU2ID());
        boolean equals2 = Objects.equals(f.getU2ID(), this.getU1ID()) && Objects.equals(f.getU1ID(), this.getU2ID());

        return equals1 || equals2;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getU1ID(), getU2ID());
    }
}
