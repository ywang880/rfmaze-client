package com.rfview.events;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class MatrixDataEvent implements Event, Serializable {

    private static final long serialVersionUID = -1242821016362043241L;
    private final static AtomicLong seq = new AtomicLong();
    private final EventType type = EventType.MATRIX_DATA;
    
    public EventType getType() {
        return type;
    }

    private final String source;
    private final Object eventData;
    
    
    public MatrixDataEvent(String source, Object eventData) {
        super();
        this.source = source;
        this.eventData = eventData;
        seq.incrementAndGet();
    }

    public static AtomicLong getSeq() {
        return seq;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Object getEventData() {
        return eventData;
    }    
}
