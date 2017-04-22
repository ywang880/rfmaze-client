package com.rfview.management;

import java.io.Serializable;

import javax.management.Notification;
import javax.management.NotificationListener;

public class ClientListener implements NotificationListener, Serializable {
    private static final long serialVersionUID = -5974865513893464734L;    
    
    public void handleNotification(Notification notification, Object handback) {
        String data = (String)notification.getUserData();
        if ((data!=null) && !data.isEmpty()) {
            System.out.println("Received notification: ");
        }
    }
} 