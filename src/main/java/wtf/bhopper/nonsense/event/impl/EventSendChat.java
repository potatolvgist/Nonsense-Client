package wtf.bhopper.nonsense.event.impl;

import wtf.bhopper.nonsense.event.Cancellable;

public class EventSendChat extends Cancellable {

    public String message;

    public EventSendChat(String message) {
        this.message = message;
    }

}
