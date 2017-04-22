package com.rfview.events;

public interface Event {

    EventType getType();
    
    String getSource();

    Object getEventData();

}
