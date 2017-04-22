package com.rfview.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class ColorMapping {

    private static final Map<String, Set<Range>> mapper = new HashMap<String, Set<Range>> ();
    private static final Logger logger = Logger.getLogger(ColorMapping.class.getName());
    
    
    public static String mapping(String user, int v) {
                        
        if (!mapper.containsKey(user)) {
            return defaultScheme(v);
        }
        
        for (Range r : mapper.get(user)) {
            String color = r.getColor(v);
            if (color != null) {
                return color;
            }
        }
        
        return defaultScheme(v);
    }
    
    public static void update(String user, String r1, String r2, String r3, String c1, String c2, String c3) {
        
        Set<Range> s = mapper.get(user);
        if (s == null) {
            s = new HashSet<Range>();
            mapper.put(user, s);
        }
        
        if (validate(r1)) {
            Range r = new Range(r1, c1);
            if (s.contains(r)) {
                s.remove(r);
            }
            logger.info(r.toString());
            s.add(r);
        }
                
        if (validate(r2)) {
            Range r = new Range(r2, c2);
            if (s.contains(r)) {
                s.remove(r);
            }
            logger.info(r.toString());
            s.add(r);            
        }
                
        if (validate(r3)) {
            Range r = new Range(r3, c3);
            if (s.contains(r)) {
                s.remove(r);
            }
            logger.info(r.toString());
            s.add(r);
        }
    }
    
    public static boolean validate(String r) {
        return true;
    }

    public static String defaultScheme(int v) {
        if (v <= 10) {
            return "#008800";
        } else if (v >10 && v <= 62) {
            return "#EE9933";
        } else if (v == 63) {
            return "#DD0000";
        }
  
        return "#008800";
    }
}
