package com.finpro.frontend.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Subject pada Observer pattern. */
public class EventBus {
    private final List<GameEventListener> listeners = new CopyOnWriteArrayList<>();

    public void subscribe(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unsubscribe(GameEventListener listener) { listeners.remove(listener); }

    public void publish(GameEvent event) {
        for (GameEventListener l : listeners) {
            try {
                l.onEvent(event);
            } catch (RuntimeException ex) {
                // satu listener error tidak boleh merusak listener lain / match
                System.err.println("Listener error pada " + event.type() + ": " + ex);
            }
        }
    }

    public void publish(GameEventType type, String message, int value) {
        publish(new GameEvent(type, message, value));
    }
}
