package socialnetwork.toysocialnetwork.utils.observer;


import socialnetwork.toysocialnetwork.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);

    void removeObserver(Observer<E> e);

    void notifyObservers(E e);
}
