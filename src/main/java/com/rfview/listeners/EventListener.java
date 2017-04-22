package com.rfview.listeners;

import java.io.Serializable;

import com.rfview.events.Event;

public interface EventListener extends Serializable {

    public void handleEvent(Event e);  

}
