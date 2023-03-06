package socialnetwork.toysocialnetwork.domain.dto;

import socialnetwork.toysocialnetwork.domain.FriendshipSender;
import socialnetwork.toysocialnetwork.domain.FriendshipStatus;

import java.time.LocalDateTime;

public class FriendshipDTO {
    Long friendshipId;

    Long user1Id, user2Id;
    String user1FName, user2FName;
    String user1LName, user2LName;
    LocalDateTime friendsFrom;
    FriendshipSender friendshipSender;
    FriendshipStatus friendshipStatus;

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public FriendshipDTO(Long friendshipId, Long user1Id, Long user2Id, String user1FName, String user1LName, String user2FName, String user2LName, LocalDateTime friendsFrom, FriendshipStatus friendshipStatus, FriendshipSender friendshipSender) {
        this.friendshipId = friendshipId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.user1FName = user1FName;
        this.user2FName = user2FName;
        this.user1LName = user1LName;
        this.user2LName = user2LName;
        this.friendsFrom = friendsFrom;
        this.friendshipSender = friendshipSender;
        this.friendshipStatus = friendshipStatus;
    }

    @Override
    public String toString() {
        return "FriendshipDTO{" +
                "user1FName='" + user1FName + '\'' +
                ", user2FName='" + user1LName + '\'' +
                ", friendsFrom=" + friendsFrom +
                '}';
    }

    public Long getFriendshipId() {
        return friendshipId;
    }

    public Long getUser1Id() {
        return user1Id;
    }

    public Long getUser2Id() {
        return user2Id;
    }

    public String getUser1FName() {
        return user1FName;
    }

    public String getUser2FName() {
        return user2FName;
    }

    public String getUser1LName() {
        return user1LName;
    }

    public String getUser2LName() {
        return user2LName;
    }

    public FriendshipSender getFriendshipSender() {
        return friendshipSender;
    }

    public FriendshipStatus getFriendshipStatus() {
        return friendshipStatus;
    }



    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }

    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }

    public void setUser1FName(String user1FName) {
        this.user1FName = user1FName;
    }

    public void setUser2FName(String user2FName) {
        this.user2FName = user2FName;
    }

    public void setUser1LName(String user1LName) {
        this.user1LName = user1LName;
    }

    public void setUser2LName(String user2LName) {
        this.user2LName = user2LName;
    }

    public void setFriendshipSender(FriendshipSender friendshipSender) {
        this.friendshipSender = friendshipSender;
    }

    public void setFriendshipStatus(FriendshipStatus friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }
}
