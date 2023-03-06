package socialnetwork.toysocialnetwork.domain;

import socialnetwork.toysocialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Long senderID, receiverID;
    private String content;
    private LocalDateTime timeSent;

    public Message(Long senderID, Long receiverID, String content) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.content = content;
        this.timeSent = LocalDateTime.parse(LocalDateTime.now().format(Constants.DEFAULT_DATE_TIME_FORMATTER), Constants.DEFAULT_DATE_TIME_FORMATTER);
    }

    public Message(Long senderID, Long receiverID, String content, LocalDateTime timeSent) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.content = content;
        this.timeSent = timeSent;
    }

    public Long getSenderID() {
        return senderID;
    }

    public Long getReceiverID() {
        return receiverID;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimeSent()
    {
        return timeSent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return senderID.equals(message.senderID) && receiverID.equals(message.receiverID) && content.equals(message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderID, receiverID, content);
    }
}
