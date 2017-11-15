package com.rfview.utils;

import java.util.HashMap;
import java.util.Map;

public class ColorMapping {

    private static final Map<String, Range[]> mapper = new HashMap<String, Range[]> ();

    public static String mapping(String user, int v) {
    	return mapping(user, false, v);
    }

    public static String mapping(String user, boolean isTopYoung, int v) {

        if (!mapper.containsKey(user)) {
            return defaultScheme(isTopYoung, v);
        }

        for (Range r : mapper.get(user)) {
            String color = r.getColor(v);
            if (color != null) {
                return color;
            }
        }
        return defaultScheme(isTopYoung, v);
    }

    public static void update(String user, String r1, String r2, String r3, String c1, String c2, String c3) {

        Range[] ranges = mapper.get(user);
        if (ranges == null) {
            ranges = new Range[3];
            mapper.put(user, ranges);
        }

        ranges[0] = new Range(r1, c1);
        ranges[1] = new Range(r2, c2);
        ranges[2] = new Range(r3, c3);
    }

    public static String defaultScheme(boolean isTopYoung, int v) {
    	if ( isTopYoung ) {
    		if (v <= 10) {
    			return Constants.COLOR.GREEN;
    		} else if (v >10 && v <= 119) {
    			return Constants.COLOR.ORANGE;
    		} else if (v == 120) {
    			return Constants.COLOR.RED;
    		}
    		return Constants.COLOR.GREEN;
    	} else {
    		  if (v <= 10) {
    			  return Constants.COLOR.GREEN;
    		  } else if (v >10 && v <= 62) {
    			  return Constants.COLOR.ORANGE;
    		  } else if (v == 63) {
    			  return Constants.COLOR.RED;
    		  }
    		  return Constants.COLOR.GREEN;
    	}
    }
}
