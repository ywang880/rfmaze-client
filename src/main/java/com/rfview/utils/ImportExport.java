package com.rfview.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.rfview.MatrixLabel;
import com.rfview.management.MazeserverManagement;

public class ImportExport {

    private static final String FILE_EXTENSION = ".xlsx";
    private String filename;

    private String dataInputLebel[];
    private String dataInputDesc[];

    private String dataOutputLebel[];
    private String dataOutputDesc[];

    private String hardwareName;
    
    protected final Logger logger = Logger.getLogger(ImportExport.class.getName());

    public ImportExport(String fn) {
        hardwareName = fn;
        filename = MazeserverManagement.getInstance().getConfigureDir() + File.separator + fn + FILE_EXTENSION;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getHardwareName() {
        return hardwareName;
    }

    public void setHardwareName(String hardwareName) {
        this.hardwareName = hardwareName;
    }
    
    public void save(List<MatrixLabel> inputLabels, List<MatrixLabel> outputLabels) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(hardwareName);
        sheet.setColumnWidth(0, 16*256);
        sheet.setColumnWidth(1, 16*256);
        sheet.setColumnWidth(2, 16*256);
        sheet.setColumnWidth(3, 16*256);

        int numInputs = inputLabels.size();
        int numOutputs = outputLabels.size();
        int numRows = Math.max(numInputs, numOutputs);
      
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        
        Row header_row = sheet.createRow(0);
        Cell header_cell1 = header_row.createCell(0);
        header_cell1.setCellStyle(style);
        header_cell1.setCellValue("Input Label");
                
        Cell header_cell2 = header_row.createCell(1);
        header_cell2.setCellStyle(style);
        header_cell2.setCellValue("Input Description");
          
        Cell header_cell3 = header_row.createCell(2);
        header_cell3.setCellStyle(style);
        header_cell3.setCellValue("Output Label");
          
        Cell header_cell4 = header_row.createCell(3);
        header_cell4.setCellStyle(style);
        header_cell4.setCellValue("Output Description");

        for (int i = 1; i <= numRows; i++) {
    
            String inputLabel = "";
            String inputDesc = "";
            String outputLabel = "";
            String outputDesc = "";
    
            if (i <= numInputs) {
                inputLabel = inputLabels.get(i-1).getLabel();
                inputDesc = inputLabels.get(i-1).getDescription();
            }
    
            if (i <= numOutputs) {
                outputLabel = outputLabels.get(i-1).getLabel();
                outputDesc = outputLabels.get(i-1).getDescription();
            }
          
            Row row = sheet.createRow(i);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(inputLabel);
            
            Cell cell2 = row.createCell(1);
            cell2.setCellValue(inputDesc);
              
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(outputLabel);
              
            Cell cell4 = row.createCell(3);
            cell4.setCellValue(outputDesc);
        }
      
        FileOutputStream out = null;
        try  {
            out = new FileOutputStream(MazeserverManagement.getInstance().getConfigureDir() +
                    File.separator + hardwareName + FILE_EXTENSION);
            
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "", e);
        } finally {
            if (out!=null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String[] getDataInputLebel() {
        return dataInputLebel;
    }

    public String[] getDataInputDesc() {
        return dataInputDesc;
    }

    public String[] getDataOutputLebel() {
        return dataOutputLebel;
    }

    public String[] getDataOutputDesc() {
        return dataOutputDesc;
    }
    
    public void read(File xltFile) {
        
        FileInputStream file = null;
        try {
            file = new FileInputStream(xltFile);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            setHardwareName(sheet.getSheetName());
               
            List<String> dataInputLebelList = new ArrayList<String>();
            List<String> dataInputDescList = new ArrayList<String>();

            List<String> dataOutputLebelList = new ArrayList<String>();
            List<String> dataOutputDescList = new ArrayList<String>();

            Iterator<Row> rowIterator = sheet.iterator();
            boolean firstRow = true;
            while (rowIterator.hasNext()) {
                if (firstRow) {
                    firstRow = false;
                    rowIterator.next();
                    continue;
                }

                Row row = rowIterator.next();
                String c0 = row.getCell(0).getStringCellValue();
                String c1 = row.getCell(1).getStringCellValue();
                String c2 = row.getCell(2).getStringCellValue();
                String c3 = row.getCell(3).getStringCellValue();
    
                if (!Util.isBlank(c0) && !Util.isBlank(c1)) {
                    dataInputLebelList.add(c0);
                    dataInputDescList.add(c1);
                }
    
                if (!Util.isBlank(c2) && !Util.isBlank(c3)) {
                    dataOutputLebelList.add(c2);
                    dataOutputDescList.add(c3);
                }
            }
    
            dataInputLebel = new String[dataInputLebelList.size()];
            dataInputDesc = new String[dataInputLebelList.size()];
            for (int i = 0; i < dataInputLebelList.size(); i++) {
                dataInputLebel[i] = dataInputLebelList.get(i);
                dataInputDesc[i] = dataInputDescList.get(i);
            }
    
            dataOutputLebel = new String[dataOutputDescList.size()];
            dataOutputDesc = new String[dataOutputDescList.size()];
            for (int i = 0; i < dataOutputDescList.size(); i++) {
                dataOutputLebel[i] = dataOutputLebelList.get(i);
                dataOutputDesc[i] = dataOutputDescList.get(i);
            }
        } catch (IOException e) {
            logger.warning(e.getMessage());
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                }
            }
        }
        logger.info("Done");
    }
}
