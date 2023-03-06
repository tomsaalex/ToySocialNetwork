package socialnetwork.toysocialnetwork.utils.events;

import socialnetwork.toysocialnetwork.domain.User;

public class UsersChangedEvent implements Event{
    ChangedEventType eventType;

    User data, oldData;

    public UsersChangedEvent(ChangedEventType eventType, User data) {
        this.eventType = eventType;
        this.data = data;
    }

    public UsersChangedEvent(ChangedEventType eventType, User oldData, User data)
    {
        this.eventType = eventType;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangedEventType getEventType() {
        return eventType;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
