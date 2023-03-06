package socialnetwork.toysocialnetwork.utils.events;

import socialnetwork.toysocialnetwork.domain.Friendship;

public class FriendshipsChangedEvent implements Event{
    ChangedEventType eventType;
    Friendship newFriendship, oldFriendship;

    public FriendshipsChangedEvent(ChangedEventType eventType, Friendship newFriendship, Friendship oldFriendship) {
        this.eventType = eventType;
        this.newFriendship = newFriendship;
        this.oldFriendship = oldFriendship;
    }

    public FriendshipsChangedEvent(ChangedEventType eventType, Friendship newFriendship) {
        this.eventType = eventType;
        this.newFriendship = newFriendship;
    }

    public ChangedEventType getEventType() {
        return eventType;
    }

    public Friendship getNewFriendship() {
        return newFriendship;
    }

    public Friendship getOldFriendship() {
        return oldFriendship;
    }
}
