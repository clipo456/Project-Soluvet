package Controller;

import java.util.*;
import java.util.function.Consumer;

public class EventBus {
    private static final EventBus instance = new EventBus();
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new HashMap<>();

    private EventBus() {} // Private constructor for singleton

    public static EventBus getInstance() {
        return instance;
    }

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public <T> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<?>> listeners = subscribers.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        List<Consumer<?>> listeners = subscribers.get(event.getClass());
        if (listeners != null) {
            for (Consumer<?> listener : new ArrayList<>(listeners)) {
                ((Consumer<T>) listener).accept(event);
            }
        }
    }
}