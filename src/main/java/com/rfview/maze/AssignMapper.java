package com.rfview.maze;

import java.util.HashMap;
import java.util.Map;

import com.rfview.conf.Assignment;

public class AssignMapper {

    private Map<String,String> rowMap = new HashMap <String,String>();
    private Map<String,String> columnMap = new HashMap <String,String>();
    
    public AssignMapper(Assignment assignment) {
        String rows = assignment.getRows();
        String cols = assignment.getCols();
        String[] tokens1 = rows.split(",");
        String[] tokens2 = cols.split(",");
        
        int i = 1;
        for (String s : tokens1) {
            rowMap.put(s, Integer.toString(i));
            i++;
        }
        
        i = 1;
        for (String s : tokens2) {
            columnMap.put(s, Integer.toString(i));
            i++;
        }
    }
    
    public String mapRow(String val) {
        if (!rowMap.containsKey(val)) {
            return "0";
        }
        
        return rowMap.get(val);
    }
    
    public String mapColum(String val) {
        if (!columnMap.containsKey(val)) {
            return "0";
        }
        
        return columnMap.get(val);
    }
}
