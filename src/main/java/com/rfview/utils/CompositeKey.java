package com.rfview.utils;

final public class CompositeKey implements Comparable<CompositeKey> {

    private final String user;
    private final String hardware;

    private CompositeKey(String user, String hardware) {
        super();
        this.user = user;
        this.hardware = hardware;
    }

    public String getUser() {
        return user;
    }

    public String getHadrware() {
        return hardware;
    }

    public static CompositeKey key(String user, String hardware) {
        return new CompositeKey(user, hardware);
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof CompositeKey)) {
            return false;
        }
        
        CompositeKey key = (CompositeKey)obj;
        return (user.equals(key.getUser()) && hardware.equals(key.getHadrware()));
    }
    
    public int hashCode() {
        return user.hashCode() + hardware.hashCode();
    }
    
    @Override
    public String toString() {
        return "CompositeKey [user=" + user + ", hadrware=" + hardware + "]";
    }

    @Override
    public int compareTo(CompositeKey o) {
        if ((user.compareTo(o.getUser()) == 0) && (hardware.compareTo(o.getHadrware()) == 0)) {
            return 0;
        } else {
            return 1;
        }
    }
}
