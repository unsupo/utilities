package database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;

public class HSQLDB {
	public static void main(String[] args) {
		HSQLDB hs = null;
		try {
			hs = new HSQLDB("tableMetaDataDB");
			hs.executeQuery("CREATE TABLE test (num INT IDENTITY, answer VARCHAR(250));");
			hs.executeQuery("INSERT INTO test (answer) values ('this is a new answer');");
			List<Map<String, Object>> result = hs.executeQuery("select * from test;");
			System.out.println();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}finally{
			hs.stopDBServer();
		}
	}
	
	public String dbName;
	final String dbLocation = System.getProperty("user.dir")+"/src/main/resources/HSQL/";
	public HSQLDB(String dbName) throws IOException{ 
		this.dbName = dbName;
		startDBServer(dbName);
	}
	Server sonicServer;
	Connection dbConn = null;

	public void startDBServer(String dbName) {
		if(sonicServer != null){
			stopDBServer();
		}
	    HsqlProperties props = new HsqlProperties();
	    props.setProperty("server.database.0", "file:" + dbLocation + dbName+";");
	    props.setProperty("server.dbname.0", "xdb");
	    sonicServer = new org.hsqldb.Server();
	    try {
	        sonicServer.setProperties(props);
	    } catch (Exception e) {
	        return;
	    }
	    sonicServer.start();
	}

	public void stopDBServer() {
	    sonicServer.shutdown();
	}

	public Connection getDBConn() {
	    try {
	        Class.forName("org.hsqldb.jdbcDriver");
	        dbConn = DriverManager.getConnection(
	                "jdbc:hsqldb:hsql://localhost/xdb", "SA", "");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return dbConn;
	}
	
	public List<Map<String,Object>> executeQuery(String query) throws SQLException{
		if(sonicServer == null){
			startDBServer(query);
		}
		dbConn = getDBConn();
		Statement stmt = dbConn.createStatement();
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery(query);
		}catch(SQLException sql){
			if(sql.getMessage().contains("Table already exists")){
				return null;
			}else if(sql.getMessage().contains("Unexpected token: POSITION in statement")){
				rs = stmt.executeQuery(query.toUpperCase().replace("POSITION", "\"POSITION\""));
			}else{
				throw sql;
			}
		}
		List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
		while(rs.next()){
			Map<String, Object> subMap = new HashMap<String, Object>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i = 1; i<=rsmd.getColumnCount(); i++){
				subMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
			}
			results.add(subMap);
		}
		return results;
	}
	
	
	
//	public static void main(String[] args) {
//		Connection connection = null;
//		ResultSet resultSet = null;
//		Statement statement = null;
//		try {
//			Class.forName("org.hsqldb.jdbcDriver");
//			connection = DriverManager.getConnection(
//			"jdbc:hsqldb:file:C:/JavaInstallation/HSQLDB/DB", "SA", "");
//			statement = connection.createStatement();
//			resultSet = statement
//					.executeQuery("SELECT Salary FROM SALARYDETAILS WHERE Emptitle='54601A'");
//			while (resultSet.next()) {
//				System.out.println("EMPLOYEE Salary:"
//						+ resultSet.getString("Salary"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				resultSet.close();
//				statement.close();
//				connection.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
