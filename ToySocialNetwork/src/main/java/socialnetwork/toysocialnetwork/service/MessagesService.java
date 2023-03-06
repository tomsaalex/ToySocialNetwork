package socialnetwork.toysocialnetwork.service;

import socialnetwork.toysocialnetwork.domain.Message;
import socialnetwork.toysocialnetwork.repository.database.DBMessageRepository;
import socialnetwork.toysocialnetwork.utils.events.ChangedEventType;
import socialnetwork.toysocialnetwork.utils.events.Event;
import socialnetwork.toysocialnetwork.utils.events.MessagesChangedEvent;
import socialnetwork.toysocialnetwork.utils.observer.Observable;
import socialnetwork.toysocialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class MessagesService implements Observable<Event> {
    private DBMessageRepository messageRepository;
    private final List<Observer<Event>> observerList = new ArrayList<>();

    public MessagesService(DBMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Iterable<Message> serviceFindAll()
    {
        return messageRepository.findAll();
    }

    public Iterable<Message> serviceFindAllBetweenTwoUsers(Long user1ID, Long user2ID)
    {
        return messageRepository.findAllBetweenTwoUsers(user1ID, user2ID);
    }

    public boolean serviceAddMessage(Long senderID, Long receiverID, String content)
    {
        Message message = new Message(senderID, receiverID, content);

        Message messageResult = messageRepository.save(message);

        if(messageResult == null)
        {
            notifyObservers(new MessagesChangedEvent(ChangedEventType.ADD, message));
            return true;
        }
        return false;
    }

    @Override
    public void addObserver(Observer<Event> e) {
        observerList.add(e);
    }

    @Override
    public void removeObserver(Observer<Event> e) {
        observerList.remove(e);
    }

    @Override
    public void notifyObservers(Event event) {
        for(Observer<Event> o: observerList)
        {
            o.update(event);
        }
    }
}
