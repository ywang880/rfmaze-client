package com.rfview.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rfview.exceptions.InvalidConfigurationException;
import com.rfview.management.MazeserverManagement;
import com.rfview.maze.Server;
import com.rfview.utils.Constants;

public class MatrixConfig {
    private static final String COMMENTS = "This is an RFMAZE configuration file. Format should not be changed";
    private static final String NEW_LINE = "\n";
    private static final String CONF_DIR= MazeserverManagement.getInstance().getConfigureDir();
    
    private String matrixName;
    private String matrixType;
    private int serverSocketPort;
    private int matrixInputs;
	private int matrixOutputs;
    private String matrixIp;
    private String matrixIp2;
    private String serverIp;
    private int hwPort;
    private int hwPort2;
    private String quintechType;
    private int matrixSocketPort;
    private int matrixControlPort;
    private int maxAttn;
    private int minAttn;
    private int stepDb;
    private boolean invertInputOutput=false;
    private final Map<String, String> types = new HashMap<String, String>();
    
    private static final MatrixConfig instance = new MatrixConfig();
    private Logger logger = Logger.getLogger(MatrixConfig.class.getName());
    
    private MatrixConfig() {
    }
     
    public enum TYPE {
       ROW,
       COL,
       ROW_LABEL,
       COL_LABEL
    }
    
    public static MatrixConfig getInstance() {
        return instance;
    }
        
    public String getMatrixName() {
        return matrixName;
    }

    public void setMatrixName(String matrixName) {
        this.matrixName = matrixName;
    }

    public String getMatrixType() {
        return matrixType;
    }

    public void setMatrixType(String matrixType) {
        this.matrixType = matrixType;
    }

    public int getServerSocketPort() {
        return serverSocketPort;
    }

    public void setServerSocketPort(int serverSocketPort) {
        this.serverSocketPort = serverSocketPort;
    }

    public int getMatrixInputs() {
        return matrixInputs;
    }

    public void setMatrixInputs(int matrixInputs) {
        this.matrixInputs = matrixInputs;
    }

    public int getMatrixOutputs() {
        return matrixOutputs;
    }

    public void setMatrixOutputs(int matrixOutputs) {
        this.matrixOutputs = matrixOutputs;
    }

    public String getMatrixIp() {
        return matrixIp;
    }

    public void setMatrixIp(String matrixIp) {
        this.matrixIp = matrixIp;
    }

    public int getMatrixSocketPort() {
        return matrixSocketPort;
    }

    public void setMatrixSocketPort(int matrixSocketPort) {
        this.matrixSocketPort = matrixSocketPort;
    }
    public int getHwPort() {
        return hwPort;
    }

    public void setHwPort(int hwPort) {
        this.hwPort = hwPort;
    }
    public int getMaxAttn() {
        return maxAttn;
    }

    public void setMaxAttn(int maxAttn) {
        this.maxAttn = maxAttn;
    }

    public int getMinAttn() {
        return minAttn;
    }

    public void setMinAttn(int minAttn) {
        this.minAttn = minAttn;
    }

    public int getStepDb() {
        return stepDb;
    }

    public void setStepDb(int stepDb) {
        this.stepDb = stepDb;
    }

    public boolean isInvertInputOutput() {
        return invertInputOutput;
    }

    public void setInvertInputOutput(boolean invertInputOutput) {
        this.invertInputOutput = invertInputOutput;
    }
    public int getMatrixControlPort() {
        return matrixControlPort;
    }

    public void setMatrixControlPort(int matrixControlPort) {
        this.matrixControlPort = matrixControlPort;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getMatrixIp2() {
		return matrixIp2;
	}

	public void setMatrixIp2(String matrixIp2) {
		this.matrixIp2 = matrixIp2;
	}

	public int getHwPort2() {
		return hwPort2;
	}

	public void setHwPort2(int hwPort2) {
		this.hwPort2 = hwPort2;
	}

	public String getQuintechType() {
		return quintechType;
	}

	public void setQuintechType(String quintechType) {
		this.quintechType = quintechType;
	}
	
    public Server getServerInfo(String hardware) throws InvalidConfigurationException {
        try {
            final Properties props = loadConfigureFile(hardware);
            final String ip = props.getProperty(Constants.MATRIX_IP);
            final String port = props.getProperty(Constants.MATRIX_SOCKET_PORT);
            final String type = props.getProperty(Constants.MATRIX_TYPE);
            return new Server(hardware, type, ip, Integer.valueOf(port));
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "", e);
            throw new InvalidConfigurationException("Configuration file not found");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "", e);
            throw new InvalidConfigurationException("Configuration file not accessible");
        }
    }
    
    public Properties getConfiguration(String hardware) throws InvalidConfigurationException {
        try {
            return loadConfigureFile(hardware);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "", e);
            throw new InvalidConfigurationException("Configuration file not found");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "", e);
            throw new InvalidConfigurationException("Configuration file not accessible");
        }
    }
    
    public void generate() {
        StringBuilder sb = new StringBuilder("## " + COMMENTS + " ##").append(NEW_LINE);
        sb.append("MATRIX_NAME").append("=").append(matrixName).append(NEW_LINE);
        sb.append("matrix_type").append("=").append(getMatrixType()).append(NEW_LINE);
        sb.append("server_socket_port").append("=").append(matrixControlPort).append(NEW_LINE);
        
        if ( isTurnTable() ) {
        	sb.append("matrix_inputs").append("=").append(1).append(NEW_LINE);
        } else {
        	sb.append("matrix_inputs").append("=").append(matrixInputs).append(NEW_LINE);
        }
        
        sb.append("matrix_outputs").append("=").append(matrixOutputs).append(NEW_LINE);
        sb.append("server_ip").append("=").append(getServerIp()).append(NEW_LINE);
        sb.append("matrix_ip").append("=").append(matrixIp).append(NEW_LINE);
        sb.append("matrix_socket_port").append("=").append(getHwPort()).append(NEW_LINE);
      
        if ( isTurnTable() ) {
        	sb.append("max_angle").append("=").append(maxAttn).append(NEW_LINE);
        } else {
        	sb.append("max_attn").append("=").append(maxAttn).append(NEW_LINE);
        	sb.append("min_attn").append("=").append(minAttn).append(NEW_LINE);
        	sb.append("step_db").append("=").append(stepDb).append(NEW_LINE);
    
        	sb.append("invert_input_output").append("=").append(invertInputOutput? "yes":"no").append(NEW_LINE);
        	sb.append("debug").append("=").append("yes").append(NEW_LINE);
        	sb.append("debug2").append("=").append("yes").append(NEW_LINE);
        }
        
        if ( "K".equals(getMatrixType()) && "C".equals(getQuintechType()) ) {
	        sb.append("matrix_ip2").append("=").append(getMatrixIp2()).append(NEW_LINE);
	        sb.append("matrix_socket_port2").append("=").append(getHwPort2()).append(NEW_LINE);
	        sb.append("quintech_type").append("=").append(getQuintechType()).append(NEW_LINE);
        }
        
        if ( "K".equals(getMatrixType()) && "N".equals(getQuintechType()) ) {
            sb.append("quintech_type").append("=").append(getQuintechType()).append(NEW_LINE);
        }
        sb.append("EOF = Mandatory").append(NEW_LINE);
        
        PrintWriter writer;
        try {
            writer = new PrintWriter(new File(CONF_DIR+File.separator+matrixName+".cfg"));      
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
        }
    }
    
    public Properties loadConfigureFile(String hardware) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        InputStream is = new FileInputStream(new File(CONF_DIR + File.separator + hardware + ".cfg"));
        props.load(is);
        if (is!=null) {
            is.close();
        }        
        return props;
    }
   
    public String getType(final String hardware) {
        String type = types.get(hardware);
        if (type == null) {
            try {
                Properties props = loadConfigureFile(hardware);
                type = props.getProperty(Constants.MATRIX_TYPE);
                types.put(hardware, type);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }          
        }
        return type;
    }
    
    private boolean isTurnTable() {
    	return "T".equals(matrixType);
    }
}
