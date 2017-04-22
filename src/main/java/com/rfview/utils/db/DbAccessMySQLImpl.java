package com.rfview.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.rfview.MatrixLabel;
import com.rfview.conf.Assignment;
import com.rfview.maze.Server;
import com.rfview.maze.User;
import com.rfview.utils.Constants;

public class DbAccessMySQLImpl {

    private final static DbAccessMySQLImpl instance = new DbAccessMySQLImpl();
    private final Logger LOGGER = Logger.getLogger(DbAccessMySQLImpl.class.getName());

    private DbAccessMySQLImpl() {
    }

    public static DbAccessMySQLImpl getInstance() {
        return instance;
    }

    private Connection connect() throws NamingException, SQLException {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/rfmaze");
        return ds.getConnection();
    }

    public List<Server> queryServers() throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        Statement stat = null;

        List<Server> servers = new ArrayList<Server>();
        try {
            conn = connect();
            stat = conn.createStatement();
            rs = stat.executeQuery("SELECT * FROM servers");
            while (rs.next()) {
                servers.add(new Server(rs.getString("name"), rs.getString("type"), rs.getString("ip"), rs
                        .getInt("port")));
            }
            return servers;
        } catch (NamingException e) {
            throw new SQLException();
        } finally {
            closeConnection(conn);
            closeResultset(rs);
            closeStatement(stat);
        }
    }

    public List<User> queryUsers() throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        Statement stat = null;
        List<User> users = new ArrayList<User>();
        try {
            conn = connect();
            stat = conn.createStatement();
            rs = stat.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setPassword(rs.getString("password"));
                user.setFirstname(rs.getString("firstname"));
                user.setLastname(rs.getString("lastname"));
                user.setOrganization(rs.getString("organization"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setExpiration(rs.getString("expiration"));
                users.add(user);
            }
            return users;

        } catch (NamingException e) {
            throw new SQLException();
        } finally {
            closeConnection(conn);
            closeResultset(rs);
            closeStatement(stat);
        }
    }

    public User queryUser(String id) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        Statement stat = null;
        User user = new User();

        try {
            conn = connect();
            stat = conn.createStatement();
            rs = stat.executeQuery("SELECT * FROM users WHERE id='" + id + "'");
            while (rs.next()) {
                user.setId(rs.getString("id"));
                user.setPassword(rs.getString("password"));
                user.setFirstname(rs.getString("firstname"));
                user.setLastname(rs.getString("lastname"));
                user.setOrganization(rs.getString("organization"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setExpiration(rs.getString("expiration"));
            }
            return user;
        } catch (NamingException e) {
            throw new SQLException();
        } finally {
            closeConnection(conn);
            closeResultset(rs);
            closeStatement(stat);
        }
    }

    public void updateUser(User user) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        Statement stat = null;
        int rowCount = 0;

        try {
            conn = connect();
            stat = conn.createStatement();
            rs = stat.executeQuery("SELECT count(id) FROM users WHERE id='" + user.getId() + "'");
            while (rs.next()) {
                rowCount = rs.getInt(1);
            }
        } catch (NamingException e) {
            throw new SQLException();
        } finally {
            closeResultset(rs);
            closeStatement(stat);
        }

        PreparedStatement stmt = null;
        try {
            if (rowCount > 0) {
                String SQL = "UPDATE users SET password=?,firstname=?,lastname=?,organization=?,email=?,phone=?,expiration=? WHERE id=?";
                stmt = conn.prepareStatement(SQL);
                stmt.setString(1, user.getPassword());
                stmt.setString(2, user.getFirstname());
                stmt.setString(3, user.getLastname());
                stmt.setString(4, user.getOrganization());
                stmt.setString(5, user.getEmail());
                stmt.setString(6, user.getPhone());
                stmt.setString(7, user.getExpiration());
                stmt.setString(8, user.getId());
                stmt.executeUpdate();
            } else {
                String SQL = "INSERT INTO users VALUES(?,?,?,?,?,?,?,?)";
                stmt = conn.prepareStatement(SQL);
                stmt.setString(1, user.getId());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getFirstname());
                stmt.setString(4, user.getLastname());
                stmt.setString(5, user.getOrganization());
                stmt.setString(6, user.getEmail());
                stmt.setString(7, user.getPhone());
                stmt.setString(8, user.getExpiration());
                stmt.executeUpdate();
            }
        } finally {
            closeConnection(conn);
            closeStatement(stmt);
        }
    }

    public void deleteUser(String id) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            stat.execute("DELETE FROM users WHERE id='" + id + "'");
            stat.execute("DELETE FROM assignments WHERE user='" + id +"'");
        } catch (NamingException e) {
            throw new SQLException("Failed to delete user " + id);
        } finally {
            closeStatement(stat);
        }
    }

    public void renameAssignment(String newName, String oldName) throws SQLException {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = connect();
            stat = conn.prepareStatement("UPDATE assignments SET hardware=? WHERE hardware=?");
            stat.setString(1, newName);
            stat.setString(2, oldName);
            stat.execute();
        } catch (NamingException e) {
            throw new SQLException("Failed to delete user");
        } finally {
            closeStatement(stat);
        }
    }
    
    public void updateAssignment(String hardware, String user, Assignment assignment) throws SQLException {
        LOGGER.debug("Update assignment, hardware="+hardware + ",user="+user+" assignment="+ assignment);
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT rows, cols FROM assignments WHERE hardware='" + 
                            hardware + "' AND user='" + user + "'";
            rs = stat.executeQuery(SQL);
            if (!rs.next()) {
                SQL = "INSERT INTO assignments VALUES('" + hardware + "', '" + user + "', '" + 
                        assignment.getRows() + "', '" + assignment.getCols() + "')" ;
            } else {
                String rows = rs.getString(1);
                String columns = rs.getString(2);
                if (assignment.isRowupdate() && assignment.isColupdate()) {
                    String mergedRows = mergeData(rows, assignment.getRows());
                    String mergedCols = mergeData(columns, assignment.getCols());
                    SQL = "UPDATE assignments SET rows='" + mergedRows + 
                            "', cols='" + mergedCols + "' WHERE hardware='" + hardware + "' AND user='" + user + "'";
                } else if (assignment.isRowupdate()) {
                    String mergedRows = mergeData(rows, assignment.getRows());
                    SQL = "UPDATE assignments SET rows='" + mergedRows + "' WHERE hardware='" + 
                            hardware + "' AND user='" + user + "'";
                } else if (assignment.isColupdate()) {
                    String mergedCols = mergeData(columns, assignment.getCols());
                    SQL = "UPDATE assignments SET cols='" + mergedCols + "' WHERE hardware='" + 
                            hardware + "' AND user='" + user + "'";
                }
                LOGGER.info("Update DB record " + SQL);
            }
            stat.execute(SQL);
        } catch (NamingException e) {
            LOGGER.error(e);
            throw new SQLException();
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }

    public void freeAssignments(String hardware, String user, Assignment assignment) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT hardware FROM assignments WHERE hardware='" + 
                            hardware + "' AND user='" + user + "'";
            rs = stat.executeQuery(SQL);
            if (assignment.isRowupdate() && assignment.isColupdate()) {
               SQL = "UPDATE assignments SET rows='" + assignment.getRows() + 
                           "', cols='" + assignment.getCols() + "' WHERE hardware='" + hardware + "' AND user='" + user + "'";
            } else if (assignment.isRowupdate()) {
               SQL = "UPDATE assignments SET rows='" + assignment.getRows() + "' WHERE hardware='" + 
                    hardware + "' AND user='" + user + "'";
               LOGGER.info("Update DB record " + SQL);
            } else if (assignment.isColupdate()) {
               SQL = "UPDATE assignments SET cols='" + assignment.getCols() + "' WHERE hardware='" + 
                           hardware + "' AND user='" + user + "'";
                  LOGGER.info("Update DB record " + SQL);
            }
            stat.execute(SQL);
        } catch (NamingException e) {
            LOGGER.error(e);
            throw new SQLException();
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public Assignment getAssignment(String hardware, String user) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL;
            if ((user==null) || user.trim().isEmpty() || ("*".endsWith(user.trim()))) {
                SQL = "SELECT * FROM assignments WHERE hardware='" + hardware + "'";
            } else {
                SQL = "SELECT * FROM assignments WHERE hardware='" + hardware + "' AND user='" + user + "'";
            }
            LOGGER.debug("SQL statement = " + SQL);
            rs = stat.executeQuery(SQL);
            Assignment result = new Assignment();
            while (rs.next()) {
                String username = rs.getString(2);
                String rows = rs.getString(3);
                String cols = rs.getString(4);
                result.addReserved(username, sorting(rows), sorting(cols));
            }
            return result;
        } catch (NamingException e) {
            LOGGER.error(e);
            throw new SQLException();
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public List<String> getAssignedOutputUsers(String hardware) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT user FROM assignments WHERE hardware='" + hardware + "' AND COALESCE (cols, '') <> ''";
            LOGGER.debug("SQL statement = " + SQL);
            rs = stat.executeQuery(SQL);
            List<String> result = new ArrayList<String>();
            while (rs.next()) {
                String username = rs.getString(1);
                result.add(username);
            }
            return result;
        } catch (NamingException e) {
            LOGGER.error(e);
            throw new SQLException();
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public Assignment getAssignment(String user) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT * FROM assignments WHERE user='" + user + "'";
            LOGGER.debug("SQL statement = " + SQL);
            rs = stat.executeQuery(SQL);
            Assignment result = new Assignment();
            while (rs.next()) {
                result.addHardware(rs.getString(1));
                String username = rs.getString(2);
                String rows = rs.getString(3);
                String cols = rs.getString(4);
                result.addReserved(username, sorting(rows), sorting(cols));
            }
            return result;
        } catch (NamingException e) {
            LOGGER.error(e);
            throw new SQLException();
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public String[] getAssignedInputOuts(String hardware, String user) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        String[] result= new String[2];
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT rows, cols FROM assignments WHERE hardware='" + hardware + "' AND user='" + user + "'";
            LOGGER.debug("SQL statement = " + SQL);
            rs = stat.executeQuery(SQL);

            while (rs.next()) {
                String rows = rs.getString(1);
                String cols = rs.getString(2);
                result[0] = rows;
                result[1] = cols;
            }
            return result;
        } catch (NamingException e) {
            LOGGER.error(e);
            throw new SQLException();
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public boolean hasAssignedInputs(String hardware) throws SQLException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT cols FROM assignments WHERE hardware='" + hardware + "'";
            LOGGER.debug("SQL statement = " + SQL);
            rs = stat.executeQuery(SQL);
            String cols = null;
            while (rs.next()) {
                cols = rs.getString(1);
            }
            return ((cols!=null) && !cols.isEmpty());
        } catch (NamingException e) {
            LOGGER.error(e);
            throw new SQLException();
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public int getNextAvailablePort() {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT port,available from matrix_ports";
            rs = stat.executeQuery(SQL);
            int numPorts=0;
            while (rs.next()) {
                int port = rs.getInt(1);
                int availabe = rs.getInt(2);
                LOGGER.info("port = " + port + " availabe = " + availabe);
                if (availabe == 0) {
                    return port;
                }
                numPorts++;
            }
            if (numPorts==0) {
                return 29010;
            } else {
                return 29010 + numPorts * 10;
            }
 
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
        return 0;
    }

    public int getPort(String hardware) {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT port FROM matrix_ports WHERE hardware='"+hardware+"'";
            rs = stat.executeQuery(SQL);
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
        return 0;
    }
    
    public void updatePort(int port, String hardware) {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT port FROM matrix_ports WHERE port=" + port;
            rs = stat.executeQuery(SQL);
            if (!rs.next()) {
                SQL = "INSERT INTO matrix_ports VALUES("+port+"," + 1+",'" + hardware +"')";
            } else {
                SQL = "UPDATE matrix_ports SET available=1 WHERE port=" + port;
            }
            stat.execute(SQL);
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public void reclaimPort(String hardware) {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "UPDATE matrix_ports SET available=0 WHERE hardware='" + hardware +"'";           
            LOGGER.debug("Execure SQL " + SQL);
            stat.execute(SQL);
            
            SQL = "DELETE FROM assignments WHERE hardware='" + hardware +"'";
            LOGGER.debug("Execure SQL " + SQL);
            stat.execute(SQL);
            
            SQL = "DELETE FROM matrix_labels WHERE hardware='" + hardware +"'";
            LOGGER.debug("Execure SQL " + SQL);
            stat.execute(SQL);
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public void deleteLabels(String hardware) {
        Connection conn = null;
        PreparedStatement stat = null;
        String SQL = "DELETE FROM matrix_labels WHERE hardware=?";
        
        try {
            conn = connect();
            stat = conn.prepareStatement(SQL);            
            stat.setString(1, hardware);
            stat.execute();
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeStatement(stat);
            closeConnection(conn);
        }   
    }
    
    public void deleteAssignment(String user, String hardware) {
        Connection conn = null;
        PreparedStatement stat = null;
        String SQL = "DELETE FROM assignments WHERE hardware=? AND user=?";
        
        try {
            conn = connect();
            stat = conn.prepareStatement(SQL);            
            stat.setString(1, hardware);
            stat.setString(2, user);
            stat.execute();
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeStatement(stat);
            closeConnection(conn);
        }
    }

    public void deleteAssignmentIfEmpty(String user, String hardware) {
               
        Connection conn = null;
        PreparedStatement stat = null;
        String SQL = "DELETE FROM assignments WHERE hardware=? AND user=?";
        
        try {
            Assignment a = getAssignment(hardware, user);
            if (a.isRowsEmpty() && a.isColsEmpty()) {    
                conn = connect();
                stat = conn.prepareStatement(SQL);            
                stat.setString(1, hardware);
                stat.setString(2, user);
                stat.execute();
            }
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public void insertLabels(String hardware, String[] inputs, String[] inputDesc, String[] outputs, String[] outputsDesc) {
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        String SQL = "INSERT INTO matrix_labels values (?,?,?,?,?)";
        
        try {
            conn = connect();
            stat = conn.prepareStatement(SQL);
            conn.setAutoCommit(false);
            
            for (int i = 0; i < inputs.length; i++) {
                stat.setInt(1, i+1);
                stat.setString(2, hardware);
                stat.setString(3, "input");
                stat.setString(4, inputs[i]);
                stat.setString(5, inputDesc[i]);                
                stat.addBatch();                
            }

            for (int i = 0; i < outputs.length; i++) {
                stat.setInt(1, i+1);
                stat.setString(2, hardware);
                stat.setString(3, "output");
                stat.setString(4, outputs[i]);
                stat.setString(5, outputsDesc[i]);
                stat.addBatch();
            }
            
            stat.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
 
        }
    }

    public void insertDefaultLabels(String hardware, int numInputs, int numOutputs) {
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        String SQL = "INSERT INTO matrix_labels values (?,?,?,?,?)";
        
        try {
            conn = connect();
            stat = conn.prepareStatement(SQL);
            
            for (int i = 0; i < numInputs; i++) {
                stat.setInt(1, i+1);
                stat.setString(2, hardware);
                stat.setString(3, "input");
                stat.setString(4, "Input_" + (i+1));
                stat.setString(5, "Input_" + (i+1) + " Details");
                stat.addBatch();
            }

            for (int i = 0; i < numOutputs; i++) {
                stat.setInt(1, i+1);
                stat.setString(2, hardware);
                stat.setString(3, "output");
                stat.setString(4, "Output_" + (i+1));
                stat.setString(5, "Output_" + (i+1) + " Details");
                stat.addBatch();
            }
            
            stat.executeBatch();            
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public List<MatrixLabel> queryInputLabels(String hardware) {
        return queryLabels(hardware, Constants.INPUTS);
    }

    public List<MatrixLabel> queryOutputLabels(String hardware) {
        return queryLabels(hardware, Constants.OUTPUTS);
    }
    
    public List<MatrixLabel> queryLabels(String hardware, String type) {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        List<MatrixLabel> result = new ArrayList<MatrixLabel>();
        try {
            conn = connect();
            stat = conn.createStatement();
            String SQL = "SELECT id, label, description FROM matrix_labels WHERE hardware='"+hardware+"' AND type='" + type + "'";
            rs = stat.executeQuery(SQL);
            while (rs.next()) {
                MatrixLabel ml = new MatrixLabel(rs.getInt(1), rs.getString(2), rs.getString(3));
                result.add(ml);
            }
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
        return result;
    }

    @SuppressWarnings("resource")
    public void reAssignColumn(String column, String hardware, String from, String to) {
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        String column1=null;
        String column2=null;
        String data1=null;
        String data2=null;
        try {
            conn = connect();
            String SQL = "SELECT cols FROM assignments WHERE hardware=? AND user=?";
            stat = conn.prepareStatement(SQL);
            stat.setString(1, hardware);
            stat.setString(2, from);
            rs = stat.executeQuery();            
            while (rs.next()) {
                column1 = rs.getString(1);
            }
            
            closeStatement(stat);
            stat = conn.prepareStatement(SQL);
            stat.setString(1, hardware);
            stat.setString(2, to);
            rs = stat.executeQuery();            
            while (rs.next()) {
                column2 = rs.getString(1);
            }
            
            if (column1!=null) {
                String tokens[] = column1.split(",");
                data1 =  (tokens.length == 1)? "" : arrayToString(column, tokens);
            }
            
            if ((column2==null) || column2.trim().isEmpty()) {
                data2 = column;
            } else {
                data2 = column2+","+column;
                String[] dataTokens = data2.split(",");
                Arrays.sort(dataTokens, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        Integer v1 = Integer.parseInt(o1);
                        Integer v2 = Integer.parseInt(o2);
                        return v1.compareTo(v2);
                    }
                });
                data2 = arrayToString(dataTokens);
            }
                        
            ////
            closeStatement(stat);
            SQL = "UPDATE assignments SET cols=? WHERE hardware=? AND user=?";
            stat = conn.prepareStatement(SQL);
            stat.setString(1, data1);
            stat.setString(2, hardware);
            stat.setString(3, from);
            stat.execute();
            
            ///// 
            closeStatement(stat);
            if (column2==null) {
                SQL = "INSERT INTO assignments VALUES(?,?,?,?)";                
                stat = conn.prepareStatement(SQL);
                stat.setString(1, hardware);
                stat.setString(2, to);
                stat.setString(3, "");
                stat.setString(4, data2);
            } else {
                SQL = "UPDATE assignments SET cols=? WHERE hardware=? AND user=?";                
                stat = conn.prepareStatement(SQL);
                stat.setString(1, data2);
                stat.setString(2, hardware);
                stat.setString(3, to);  
            }
            stat.execute();
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }

    public void freeColumn(String column, String hardware, String from) {
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        String column1=null;
        String data1=null;
        try {
            conn = connect();
            String SQL = "SELECT cols FROM assignments WHERE hardware=? AND user=?";
            stat = conn.prepareStatement(SQL);
            stat.setString(1, hardware);
            stat.setString(2, from);
            rs = stat.executeQuery();            
            while (rs.next()) {
                column1 = rs.getString(1);
            }
                        
            if (column1!=null) {
                String tokens[] = column1.split(",");
                data1 = (tokens.length == 1)? "" : arrayToString(column, tokens);
                
                closeStatement(stat);
                SQL = "UPDATE assignments SET cols=? WHERE hardware=? AND user=?";
                stat = conn.prepareStatement(SQL);
                stat.setString(1, data1);
                stat.setString(2, hardware);
                stat.setString(3, from);
                stat.execute();
                              
                stat.execute();
            }
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    private String arrayToString(String column, String[] tokens) {
        StringBuilder sb = new StringBuilder();
        boolean first=true;
        for (String s : tokens) {
            if (!s.equals(column)) {
                if (first) {
                    sb.append(s);
                    first=false;
                } else {
                    sb.append(",").append(s);
                }
            }
        }
        return sb.toString();
    }
  
    private String arrayToString(String[] tokens) {
        StringBuilder sb = new StringBuilder();
        boolean first=true;
        for (String s : tokens) {
            if (first) {
                sb.append(s);
                first=false;
            } else {
                sb.append(",").append(s);
            }
        }
        return sb.toString();
    }
    
    @SuppressWarnings("resource")
    public void reAssignRow(String row, String hardware, String from, String to) {
        LOGGER.debug("reassign row " + row + " from " + from + " to "+ to + " hardware = " + hardware);
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        String row1=null;
        String row2=null;
        String data1=null;
        String data2=null;
        try {
            conn = connect();
            String SQL = "SELECT rows FROM assignments WHERE hardware=? AND user=?";
            stat = conn.prepareStatement(SQL);
            stat.setString(1, hardware);
            stat.setString(2, from);
            rs = stat.executeQuery();            
            while (rs.next()) {
                row1 = rs.getString(1);
            }
            
            closeStatement(stat);
            stat = conn.prepareStatement(SQL);
            stat.setString(1, hardware);
            stat.setString(2, to);
            rs = stat.executeQuery();            
            while (rs.next()) {
                row2 = rs.getString(1);
            }
            
            if (row1!=null) {
                String tokens[] = row1.split(",");
                data1 =  (tokens.length == 1)? "" : arrayToString(row, tokens);
            }
            
            if ((row2==null) || row2.trim().isEmpty()) {
                data2 = row;
            } else {
                data2 = row+","+row2;
                String[] dataTokens = data2.split(",");
                Arrays.sort(dataTokens, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        Integer v1 = Integer.parseInt(o1);
                        Integer v2 = Integer.parseInt(o2);
                        return v1.compareTo(v2);
                    }
                });
                data2 = arrayToString(dataTokens);
            }

            ////
            closeStatement(stat);
            SQL = "UPDATE assignments SET rows=? WHERE hardware=? AND user=?";
            stat = conn.prepareStatement(SQL);
            stat.setString(1, data1);
            stat.setString(2, hardware);
            stat.setString(3, from);
            stat.execute();
            
            ///// 
            closeStatement(stat);
            if (row2==null) {
                SQL = "INSERT INTO assignments VALUES(?,?,?,?)";                
                stat = conn.prepareStatement(SQL);
                stat.setString(1, hardware);
                stat.setString(2, to);
                stat.setString(3, "");
                stat.setString(4, data2);
            } else {
                SQL = "UPDATE assignments SET rows=? WHERE hardware=? AND user=?";                
                stat = conn.prepareStatement(SQL);
                stat.setString(1, data2);
                stat.setString(2, hardware);
                stat.setString(3, to);  
            }
            stat.execute();
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public void freeRow(String row, String hardware, String from) {
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        String row1=null;
        String data1=null;
        try {
            conn = connect();
            String SQL = "SELECT rows FROM assignments WHERE hardware=? AND user=?";
            stat = conn.prepareStatement(SQL);
            stat.setString(1, hardware);
            stat.setString(2, from);
            rs = stat.executeQuery();            
            while (rs.next()) {
                row1 = rs.getString(1);
            }

            if (row1!=null) {
                String tokens[] = row1.split(",");
                data1 = (tokens.length == 1)? "" : arrayToString(row, tokens);
                
                closeStatement(stat);
                SQL = "UPDATE assignments SET rows=? WHERE hardware=? AND user=?";
                stat = conn.prepareStatement(SQL);
                stat.setString(1, data1);
                stat.setString(2, hardware);
                stat.setString(3, from);
                stat.execute();
                              
                stat.execute();
            }
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }
    
    public void share(String row, String hardware, String to) {
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        String currentRow=null;
        try {
            conn = connect();
            String SQL = "SELECT rows FROM assignments WHERE hardware=? AND user=?";
            stat = conn.prepareStatement(SQL);
            stat.setString(1, hardware);
            stat.setString(2, to);
            rs = stat.executeQuery();            
            while (rs.next()) {
                currentRow = rs.getString(1);
            }
                        
            if (currentRow!=null) {
                String tokens[] = currentRow.split(",");
                for (String s : tokens) {
                    if (s.trim().equals(row.trim())) {
                       return; 
                    }
                }
                String[] tmpString = (row +","+currentRow).split(",");
                sort(tmpString);
                
                closeStatement(stat);
                SQL = "UPDATE assignments SET rows=? WHERE hardware=? AND user=?";
                stat = conn.prepareStatement(SQL);
                stat.setString(1, arrayToString(tmpString));
                stat.setString(2, hardware);
                stat.setString(3, to);
                stat.execute();
                              
                stat.execute();
            }
        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }

    @SuppressWarnings("resource")
    public void updateLabels(String hardware, int newInputs, int newOutputs) {
        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        int numInputs=0;
        int numOutputs=0;
        
        try {
            conn = connect();
            String SQL = "SELECT count(type) FROM matrix_labels WHERE hardware=? and type=?";
            stat = conn.prepareStatement(SQL);
            stat.setString(1, hardware);
            stat.setString(2, "input");
            rs = stat.executeQuery();            
            while (rs.next()) {
                numInputs = rs.getInt(1);
            }

            stat.setString(1, hardware);
            stat.setString(2, "output");
            rs = stat.executeQuery();            
            while (rs.next()) {
                numOutputs = rs.getInt(1);
            }

            if (newInputs > numInputs) {
                SQL = "INSERT INTO matrix_labels values (?,?,?,?,?)";
                conn = connect();
                stat = conn.prepareStatement(SQL);
                conn.setAutoCommit(false);
                    
                for (int i = numInputs; i < newInputs; i++) {
                    stat.setInt(1, i+1);
                    stat.setString(2, hardware);
                    stat.setString(3, "input");
                    stat.setString(4, "Input_" + (i+1));
                    stat.setString(5, "Input_" + (i+1) + " Details");
                    stat.addBatch();               
                }                    
                stat.executeBatch();
                conn.commit();
            } else if (newInputs < numInputs) {
                conn.setAutoCommit(true);
                SQL = "DELETE FROM matrix_labels WHERE hardware=? AND type='input' AND id > ?";
                stat = conn.prepareStatement(SQL);
                stat.setString(1, hardware);
                stat.setInt(2, newInputs);
                stat.execute();
            }

            if (newOutputs > numOutputs) {
                SQL = "INSERT INTO matrix_labels values (?,?,?,?,?)";
                conn = connect();
                stat = conn.prepareStatement(SQL);
                conn.setAutoCommit(false);
                    
                for (int i = numOutputs; i < newOutputs; i++) {
                    stat.setInt(1, i+1);
                    stat.setString(2, hardware);
                    stat.setString(3, "output");
                    stat.setString(4, "Output_" + (i+1));
                    stat.setString(5, "Output_" + (i+1) + " Details");
                    stat.addBatch();
                }                    
                stat.executeBatch();
                conn.commit();
            } else if (newOutputs < numOutputs) {
                conn.setAutoCommit(true);
                SQL = "DELETE FROM matrix_labels WHERE hardware=? AND type='output' AND id > ?";
                stat = conn.prepareStatement(SQL);
                stat.setString(1, hardware);
                stat.setInt(2, newOutputs);
                stat.execute();
            }

        } catch (NamingException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            closeResultset(rs);
            closeStatement(stat);
            closeConnection(conn);
        }
    }

    private String mergeData(String currentData, String newData) {
        if ((currentData == null) || currentData.trim().isEmpty() || currentData.equalsIgnoreCase("null")) {
            return newData;
        }
        
        if ((newData==null) || newData.trim().isEmpty() || newData.equalsIgnoreCase("null")) {
            return currentData;
        }
        
        String [] data = (newData + ","+currentData).split(",");
        Arrays.sort(data, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
            }
        });
        
        return arrayToString(data) ;
    }
    
    private String sorting(String inputData) {
        if ((inputData==null) || inputData.isEmpty()) {
            return inputData;
        }
        
        String[] dataTokens = inputData.split(",");
        if (dataTokens.length < 2) {
            return inputData;
        }
        
        Arrays.sort(dataTokens, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {                
                Integer d1 = Integer.parseInt(o1);
                Integer d2 = Integer.parseInt(o2);
                return d1.compareTo(d2);
            }
        });
        
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < dataTokens.length; i++) {
            buffer.append(dataTokens[i]);
            if (i < dataTokens.length-1) {
                buffer.append(",");
            }
        }
        return buffer.toString();        
    }
    
    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    private void closeResultset(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
    }

    private void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
            }
        }
    }
    
    private void sort(String[] data) {
        Arrays.sort(data, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                Integer v1 = Integer.valueOf(o1);
                Integer v2 = Integer.valueOf(o2);
                return v1.compareTo(v2);
            }            
        });
    }
}
