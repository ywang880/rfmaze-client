package com.rfview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.rfview.conf.BroadcastConf;
import com.rfview.conf.MatrixConfig;
import com.rfview.management.MazeserverManagement;
import com.rfview.maze.Datagrid;
import com.rfview.utils.Constants;
import com.rfview.utils.ImportExport;
import com.rfview.utils.db.DbAccess;

public class AssignLabelAction extends BaseActionSupport implements ServletRequestAware {

    private static final String DELIMITOR = "%2C";
    private static final long serialVersionUID = -6798445163576134855L;
    private final BroadcastConf bConf = BroadcastConf.getInstance();
    private String action;
    private String param;
    private String inputLabels;
    private String inputDesc;
    private String outputLabels;
    private String outputDesc;
    private InputStream fileInputStream;
    private final DbAccess dbAccess = DbAccess.getInstance();
    private String hardware;
    private String createDefault="no";
    private String filename;

    private File labelImport;
    private String labelImportContentType;
    private String labelImportFileName;
    private final Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

    private static final String[] SUPPORTED_FILE_EXTENSIONS = new String[] {
        "xlsx",
        "xlsm",
        "xlsb",
        "xltx",
        "xltm",
        "xls",
        "xlt",
        "xlam",
        "xla",
        "xlw"
    };


    public String getCreateDefault() {
        return createDefault;
    }

    public void setCreateDefault(String createDefault) {
        this.createDefault = createDefault;
    }

    public AssignLabelAction() {
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
        sessionMap.put("hardware", hardware);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LogFile[] getFilelist() {
        File f = new File(MazeserverManagement.getInstance().getConfigureDir());

        File[] files = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (String s : SUPPORTED_FILE_EXTENSIONS) {
                    if (name.endsWith(s)) {
                        return true;
                    }
                }
                return false;
            }
        });

        int i = 0;
        LogFile[] filelist = new LogFile[files.length];
        for (File theFile : files) {
            LogFile lf = new LogFile(theFile.getName(), theFile.length(), convertTime(theFile.lastModified()));
            lf.setUrl("labels.action?filename="+theFile.getName());
            filelist[i++] = lf;
        }
        return filelist;
    }

    public String execute() {
        if (sessionMap!=null) {
            String uid = (String)sessionMap.get("loginId");
            if (uid == null) {
                return "login";
            }
            this.username = uid;
        }

        try {
            if ((labelImport!=null) && (this.labelImportFileName!=null)) {
                File fileForImport = new File(mgmt.getConfigureDir(), this.labelImportFileName);
                FileUtils.copyFile(this.labelImport, fileForImport);
                logger.info("labelImport = " + this.labelImport);
                logger.info("fileForImport = " + fileForImport);
                ImportExport ie = new ImportExport("");
                ie.read(fileForImport);

                String dataInputLebel[] = ie.getDataInputLebel();
                String dataInputDesc[] = ie.getDataInputDesc();

                String dataOutputLebel[] = ie.getDataOutputLebel();
                String dataOutputDesc[] = ie.getDataOutputDesc();

                dbAccess.deleteLabels(ie.getHardwareName());
                dbAccess.insertLabels(ie.getHardwareName(), dataInputLebel, dataInputDesc, dataOutputLebel, dataOutputDesc);
                Datagrid.getInstance().setLabelsChanged(getHardware(), true);
                setHardware(getHardware());
                return SUCCESS;
            }
        } catch (Exception e) {
            logger.error(e);
            addActionError(e.getMessage());
            return SUCCESS;
        }

        setMenu("assign_label");
        filename = request.getParameter("filename");
        if ((filename!=null) && !filename.isEmpty()) {
            return download();
        }

        logger.debug("ACTION = " + action);
        if ((action==null) || action.isEmpty()) {
            setShowcontent(Constants.CONST_NO);
            return SUCCESS;
        }

        String[] tokens = action.split(" ");
        String command = tokens[0];
        if (tokens.length > 1) {
            param = tokens[1];
        }
        setHardware(param);

        setShowcontent(Constants.CONST_YES);
        List<MatrixLabel> inputLabels = dbAccess.queryInputLabels(hardware);
        List<MatrixLabel> outputLabels = dbAccess.queryOutputLabels(hardware);

        if ((inputLabels==null) || (inputLabels.isEmpty()) || (outputLabels == null) || (outputLabels.isEmpty())) {
            setWarningMessage("Labels are not assigned. Click create button to add default values!");
            createDefault="yes";
        }

        if (command.equals("reset")) {
            return SUCCESS;
        } else if (command.equals("commit")) {

            List<MatrixLabel> currentInputLabels = dbAccess.queryInputLabels(hardware);
            List<MatrixLabel> currentOutputLabels = dbAccess.queryOutputLabels(hardware);

            String dataInputLabel[] = getInputLabels().split(DELIMITOR);
            String dataInputDesc[] = getInputDesc().split(DELIMITOR);

            if ( dataInputLabel.length != dataInputDesc.length || dataInputLabel.length != currentInputLabels.size() ) {
                setErrorMessage("Missing Input Labels or description!");
                return SUCCESS;
            }

            String dataOutputLabel[] = getOutputLabels().split(DELIMITOR);
            String dataOutputDesc[] = getOutputDesc().split(DELIMITOR);
            if ( dataOutputLabel.length != dataOutputDesc.length || dataOutputLabel.length != currentOutputLabels.size() ) {
                setErrorMessage("Missing Output Labels or description!");
                return SUCCESS;
            }

            dbAccess.deleteLabels(getHardware());
            try {
                dbAccess.insertLabels(getHardware(), dataInputLabel, dataInputDesc, dataOutputLabel, dataOutputDesc);
            } catch (Exception e) {
                //rollback
                String[] inLab = new String[currentInputLabels.size()];
                String[] inDesc = new String[currentInputLabels.size()];
                int i = 0;
                for ( MatrixLabel ml : currentInputLabels ) {
                    inLab[i] = ml.getLabel();
                    inDesc [i]= ml.getDescription();
                    i++;
                }

                String[] outLab = new String[currentOutputLabels.size()];
                String [] outDesc = new String[currentOutputLabels.size()];
                i = 0;
                for ( MatrixLabel ml : currentOutputLabels ) {
                   outLab[i] = ml.getLabel();
                   outDesc [i]= ml.getDescription();
                   i++;
                }

                try {
                    dbAccess.insertLabels(getHardware(), inLab,inDesc, outLab, outDesc);
                } catch (Exception e1) {
                }
            }
            Datagrid.getInstance().setLabelsChanged(getHardware(), true);
            return SUCCESS;
        } else if (command.equals("edit")) {
            setHardware(param);
            return SUCCESS;
        } else if (command.equals("insert_default")) {
            try {
                Properties props = MatrixConfig.getInstance().loadConfigureFile(hardware);
                String str_matrix_inputs = props.getProperty(Constants.MATRIX_INPUTS, "0");
                String str_matrix_outputs = props.getProperty(Constants.MATRIX_OUTPUTS, "0");

                int matrix_inputs = Integer.parseInt(str_matrix_inputs.trim());
                int matrix_outputs = Integer.parseInt(str_matrix_outputs.trim());

                if ((matrix_inputs>0) && (matrix_outputs>0)) {
                    dbAccess.insertDefaultLabels(param, matrix_inputs, matrix_outputs);
                }
            } catch (FileNotFoundException e) {
                logger.warn("File not found exception," + e.getMessage());
            } catch (IOException e) {
                logger.warn("IO exception, " + e.getMessage());
            }
            return SUCCESS;
        } else if (command.equals("import")) {
            setHardware(param);
            return SUCCESS;
        } else if (command.equals("export")) {
            setHardware(param);
            ImportExport writer = new ImportExport(hardware);
            writer.save(inputLabels, outputLabels);
            return SUCCESS;
        } else {
            setShowcontent(Constants.CONST_NO);
            logger.warn("unsupported action " + action);
            return ERROR;
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getHardwarelist() {
        return bConf.getHardwareList();
    }

    public String getInputLabels() {
        return (inputLabels ==null)? "" : inputLabels;
    }

    public void setInputLabels(String inputLabels) {
        this.inputLabels = inputLabels;
    }

    public String getInputDesc() {

        return (inputDesc==null)? "" : inputDesc;
    }

    public void setInputDesc(String inputDesc) {
        this.inputDesc = inputDesc;
    }

    public String getOutputLabels() {
        return (outputLabels==null)? "" : outputLabels;
    }

    public void setOutputLabels(String outputLabels) {
        this.outputLabels = outputLabels;
    }

    public String getOutputDesc() {
        return (outputDesc==null)? "" : outputDesc;
    }

    public void setOutputDesc(String outputDesc) {
        this.outputDesc = outputDesc;
    }

    public List<MatrixLabel> getInputs() {
        if (hardware==null) {
            return Collections.emptyList();
        }

        return dbAccess.queryInputLabels(hardware);
    }

    public List<MatrixLabel> getOutputs() {
        if (hardware==null) {
            return Collections.emptyList();
        }
        return dbAccess.queryOutputLabels(hardware);
    }

    //////
    public InputStream getFileInputStream() {
        return fileInputStream;
    }

    public String download() {
        String filename = request.getParameter("filename");
        try {
            fileInputStream = new FileInputStream(new File(mgmt.getConfigureDir()+File.separator+filename));
            logger.info("ACTION download file = " + mgmt.getLogFolder()+File.separator+filename);
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage());
        }

        return "download";
    }

    private String convertTime(long time){
        Date date = new Date(time);
        return format.format(date);
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    //////////
    // Upload and import
    public File getLabelImport() {
        return labelImport;
    }

    public void setLabelImport(File labelImport) {
        this.labelImport = labelImport;
    }

    public String getLabelImportContentType() {
        return labelImportContentType;
    }

    public void setLabelImportContentType(String labelImportContentType) {
        this.labelImportContentType = labelImportContentType;
    }

    public String getLabelImportFileName() {
        return labelImportFileName;
    }

    public void setLabelImportFileName(String labelImportFileName) {
        this.labelImportFileName = labelImportFileName;
    }
}
