package socialnetwork.toysocialnetwork.utils.observer;

import socialnetwork.toysocialnetwork.utils.events.Event;

public interface Observer<E extends Event>{
    void update(E e);
}
