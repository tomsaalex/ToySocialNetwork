package socialnetwork.toysocialnetwork.utils.events;

import socialnetwork.toysocialnetwork.domain.Message;

public class MessagesChangedEvent implements Event{
    ChangedEventType eventType;

    Message data, oldData;

    public MessagesChangedEvent(ChangedEventType eventType, Message data) {
        this.eventType = eventType;
        this.data = data;
    }

    public MessagesChangedEvent(ChangedEventType eventType, Message data, Message oldData) {
        this.eventType = eventType;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangedEventType getEventType() {
        return eventType;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
