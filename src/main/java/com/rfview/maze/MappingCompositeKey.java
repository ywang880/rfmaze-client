package com.rfview.maze;

public class MappingCompositeKey implements Comparable<MappingCompositeKey> {

    private final String user;
    private final String hardware;

    public MappingCompositeKey(String user, String hardware) {
        super();
        this.user = (user== null)? "" : user;
        this.hardware = (hardware ==null)? "" : hardware;
    }

    public String getUser() {
        return user;
    }

    public String getHardware() {
        return hardware;
    }

    @Override
    public int compareTo(MappingCompositeKey o) {        
        if ((user.compareTo(o.getUser()) == 0) && (hardware.compareTo(o.getHardware()) == 0)) {
            return 0;
        } else {
            return 1;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MappingCompositeKey) || obj == null) {
            return false;
        }
        MappingCompositeKey key = (MappingCompositeKey)obj;
        return (user.equals(key.getUser()) && hardware.equals(key.getHardware()));
    }
    
    @Override
    public int hashCode() {
        return user.hashCode()+hardware.hashCode();
    }
}
