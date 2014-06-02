package dna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class DataAccess {
	String dbfile;
	
	/**
	 * Create a new DataAccess object, which manages SQLite database access.
	 * 
	 * @param dbfile  The name of the .dna file to be accessed.
	 */
	public DataAccess(String dbfile) {
		this.dbfile = dbfile;
	}
	
	/**
	 * Establish the connection to a .dna SQLite database file.
	 * 
	 * @return The connection to the database.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	Connection getConnection() throws ClassNotFoundException, SQLException {    
		Class.forName("org.sqlite.JDBC");
		Connection conn= DriverManager.getConnection("jdbc:sqlite:" + dbfile);
		return conn;
	}
	
	/**
	 * Forget the name of the current .dna file for data access.
	 */
	public void closeFile() {
		dbfile = null;
	}
	
	/**
	 * Remember the name of a new .dna file for data access.
	 * 
	 * @param dnaFile  The file name of the new .dna file to be opened.
	 */
	public void openFile(String dnaFile) {
		dbfile = dnaFile;
	}

	/**
	 * Execute a statement on the database.
	 * 
	 * @param myStatement  A string representation of the SQL statement.
	 */
	public void executeStatement(String myStatement) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.execute(myStatement);
			statement.close();
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { statement.close(); } catch(Exception e) {}
			try { connection.close(); } catch(Exception e) {}
		}
	}
	
	/**
	 * Execute a statement on the database and get the ID of the row.
	 * 
	 * @param myStatement  A string representation of the SQL statement.
	 * @return             The ID of the row that was changed.
	 */
	public int executeStatementForId(String myStatement) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		int id = -1;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			statement.execute(myStatement);
			resultSet = statement.executeQuery(
					"SELECT sqlite3_last_insert_rowid()"
					);
			if (resultSet.next()) {
				do {
					id = resultSet.getInt(1);
				} while (resultSet.next());
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { statement.close(); } catch(Exception e) {}
			try { connection.close(); } catch(Exception e) {}
		}
		
		if (id == -1) {
			System.err.println("ID could not be retrieved.");
		}
		return(id);
	}
	
	/**
	 * Execute a query on the database.
	 * 
	 * @param myQuery  A string representation of an SQL query.
	 * @return         An array list with objects.
	 */
	public ArrayList<Object> executeQuery(String myQuery) {
		ArrayList<Object> al = new ArrayList<Object>();
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(myQuery);
			if (resultSet.next()) {
				do {
					al.add(resultSet.getObject(1));
				} while (resultSet.next());
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { statement.close(); } catch(Exception e) {}
			try { connection.close(); } catch(Exception e) {}
		}
		
		return(al);
	}
	
	/**
	 * Create the table structure in a new database file.
	 */
	public void createTables() {
		
		executeStatement(
				"CREATE TABLE IF NOT EXISTS SETTINGS(" + 
    	        "Property TEXT PRIMARY KEY, " + 
    	        "Value TEXT)"
    	        );
		
		executeStatement(
				"CREATE TABLE IF NOT EXISTS DOCUMENTS(" + 
        		"ID INTEGER NOT NULL PRIMARY KEY, " + 
        		"Title TEXT, " + 
        		"Text TEXT, " + 
        		"Date INTEGER, " + 
        		"Coder TEXT, " + 
        		"Source TEXT, " + 
        		"Notes TEXT, " + 
        		"Type TEXT)"
        		);
		
		executeStatement(
				"CREATE TABLE IF NOT EXISTS REGEX(" + 
                "Label TEXT PRIMARY KEY, " + 
                "Red INTEGER, " +  
                "Green INTEGER, " + 
                "Blue INTEGER)"
                );
		
		executeStatement(
				"CREATE TABLE IF NOT EXISTS STATEMENTTYPE(" + 
    			"Label TEXT PRIMARY KEY, " +
    			"Red INTEGER, " +
    			"Green INTEGER, " +
    			"Blue INTEGER)"
    			);
		
		executeStatement(
				"CREATE TABLE IF NOT EXISTS VARIABLES(" + 
    			"ID INTEGER NOT NULL PRIMARY KEY, " + 
    	    	"Variable TEXT, " + 
    	    	"DataType TEXT, " +
    	    	"StatementType INTEGER, " +
    	    	"FOREIGN KEY(StatementType) REFERENCES STATEMENTTYPE(ID))"
    	    	);
		
		executeStatement(
				"CREATE TABLE IF NOT EXISTS VARIABLES(" + 
		    	"ID INTEGER NOT NULL PRIMARY KEY, " + 
		    	"Variable TEXT, " + 
		    	"DataType TEXT, " +
		    	"StatementType INTEGER, " +
		    	"FOREIGN KEY(StatementType) REFERENCES STATEMENTTYPE(ID))"
				);
		
		executeStatement(
				"CREATE TABLE IF NOT EXISTS STATEMENTS(" +
    			"ID INTEGER NOT NULL PRIMARY KEY, " +
    			"Type TEXT, " +
        		"Document INTEGER, " + 
        		"Start INTEGER, " + 
        		"Stop INTEGER, " + 
        		"FOREIGN KEY(Type) REFERENCES STATEMENTTYPE(Label), " + 
        		"FOREIGN KEY(Document) REFERENCES DOCUMENTS(ID))"
        		);
	}
	
	/**
	 * Insert a statement type into the STATEMENTTYPE table.
	 * 
	 * @param label      A description of the statement type.
	 * @param red        The red RGB component of the statement type color.
	 * @param green      The green RGB component of the statement type color.
	 * @param blue       The blue RGB component of the statement type color.
	 * @param variables  A HashMap of variables and corresponding data types.
	 */
	public void insertStatementType(String label, int red, int green, int blue, 
			HashMap<String, String> variables) {
		Connection connection = null;
		Statement statement = null;
		
		try {
			connection = getConnection();
			statement = connection.createStatement();

			statement.execute(
					"INSERT INTO STATEMENTTYPE (Label, Red, Green, Blue) " + 
					"VALUES('" + label + "', " + red + ", " + green + 
					", " + blue + ")"
	        );
			
			String tabString = "CREATE TABLE IF NOT EXISTS " + label + "(" + 
					"StatementID INTEGER PRIMARY KEY";
			
			Iterator<String> keyIterator = variables.keySet().iterator();
			while (keyIterator.hasNext()){
				String key = keyIterator.next();
				String value = variables.get(key);
				String type;
				if (value.equals("short text") || value.equals("long text")) {
					type = "TEXT";
				} else if (value.equals("boolean") || value.equals("integer")) {
					type = "INTEGER";
				} else {
					type = "INTEGER";
				}
				statement.execute(
						"INSERT INTO VARIABLES (Variable, DataType, " + 
						"StatementType) VALUES('" + key + "','" + value + 
						"', '" + label + "')"
		        );
				
				tabString = tabString + ", " + key + " " + type;
			}
			
			tabString = tabString + 
					", FOREIGN KEY(StatementID) REFERENCES STATEMENTS(ID))";
			statement.execute(tabString);
			
	    	statement.close();
	    	connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("There was a problem when a new statement " +
					"definition was added. Did this statement type already " +
					"exist?");
			e.printStackTrace();
		} finally {
			try { statement.close(); } catch(Exception e) {}
			try { connection.close(); } catch(Exception e) {}
		}
	}

	/**
	 * Delete a statement type (if there are no statements of this type left).
	 * 
	 * @param label  The statement type to be removed.
	 */
	public void removeStatementType(String label) {
		ArrayList<?> al = executeQuery(
				"SELECT ID FROM STATEMENTS WHERE Type = " + label);
		if (al.size() != 0) {
			System.err.println("Statement type cannot be removed because " +
					"there are still some statements of this type.");
		} else {
			executeStatement(
					"DELETE FROM STATEMENTTYPE WHERE Label = " + label
					);
		}
	}
	
	/**
	 * Add a statement to the STATEMENTS table.
	 * 
	 * @param statementType  The type of statement (see STATEMENTTYPE table).
	 * @param doc            The ID of the corresponding document.
	 * @param start          The start caret position of the statement.
	 * @param stop           The stop caret position within the document.
	 * @return               ID of the newly created statement.
	 */
	public int addStatement(String type, int doc, int start, int stop) {
		int id = executeStatementForId(
				"INSERT INTO STATEMENTS (Type, Document, Start, Stop) " +
				"VALUES(" + type + ", " + doc + ", " + start + ", " + 
				stop + ")"
				);
		executeStatement(
				"INSERT INTO " + type + " (StatementID) VALUES (" + id + ")"
				);
		return id;
	}
	
	/**
	 * Retrieve the type of a statement as a string.
	 * 
	 * @param statementId  ID of the statement in the STATEMENTS table.
	 * @return             Type of the statement in the STATEMENTS table.
	 */
	public String getStatementType(int statementId) {
		ArrayList<?> al = executeQuery(
				"SELECT Type FROM STATEMENTS WHERE ID = " + statementId);
		@SuppressWarnings("unchecked")
		ArrayList<String> types = (ArrayList<String>) al;
		return(types.get(1));
	}
	
	/**
	 * Remove a statement from the STATEMENTS table.
	 * 
	 * @param id  The ID of the statement.
	 */
	public void removeStatement(int id) {
		String type = getStatementType(id);
		executeStatement("DELETE FROM " + type + " WHERE ID = " + id);
		executeStatement("DELETE FROM STATEMENTS WHERE ID = " + id);
	}

	/**
	 * Retrieve the data type of a variable from the VARIABLES table.
	 * 
	 * @param variableId  ID of the variable in the VARIABLES table.
	 * @return            Data type of the variable from the VARIABLES table.
	 */
	public String getDataType(int variableId) {
		ArrayList<?> al = executeQuery(
				"SELECT DataType FROM VARIABLES WHERE ID = " + variableId);
		@SuppressWarnings("unchecked")
		ArrayList<String> dataTypes = (ArrayList<String>) al;
		return(dataTypes.get(1));
	}

	/**
	 * Retrieve the name of a variable from the VARIABLES table.
	 * 
	 * @param variableId  ID of the variable in the VARIABLES table.
	 * @return            Name of the variable from the VARIABLES table.
	 */
	public String getVariableName(int variableId) {
		ArrayList<?> al = executeQuery(
				"SELECT Variable FROM VARIABLES WHERE ID = " + variableId);
		@SuppressWarnings("unchecked")
		ArrayList<String> varNames = (ArrayList<String>) al;
		return(varNames.get(1));
	}
	
	/**
	 * Change a variable entry of an existing statement. 
	 * 
	 * @param statementId  ID of the statement in the STATEMENTS table.
	 * @param variableId   ID of the variable in the VARIABLES table.
	 * @param entry        Value of the variable.
	 */
	public void changeStatement(int statementId, int variableId, Object entry) {
		String statementType = getStatementType(statementId);
		String variableName = getVariableName(variableId);
		String dataType = getDataType(variableId);
		
		String quotMark = "";
		if (dataType.equals("short text") || dataType.equals("long text")) {
			quotMark = "'";
		}
		
		executeStatement(
				"INSERT INTO " + statementType + "(" + variableName + 
				") VALUES (" + quotMark + entry + quotMark + ")"
				);
	}
	
	/**
	 * Add a regular expression to the REGEX table.
	 * 
	 * @param label  The label of the regular expression.
	 * @param red    The red RGB component of the regex color.
	 * @param green  The green RGB component of the regex color.
	 * @param blue   The blue RGB component of the regex color.
	 */
	public void addRegex(String label, int red, int green, int blue) {
		executeStatement(
				"INSERT INTO REGEX (Label, Red, Green, Blue) " + 
				"VALUES('" + label + "', " + red + ", " + green + 
				", " + blue + ")"
				);
	}
	
	/**
	 * Remove a regular expression from the REGEX table.
	 * 
	 * @param label  The label of the regular expression.
	 */
	public void removeRegex(String label) {
		executeStatement("DELETE FROM REGEX WHERE Label = " + label);
	}
	
	/**
	 * Retrieve the IDs of all statements within a document.
	 * 
	 * @param documentId  ID of the document associated with a statement.
	 * @return            ArrayList with the statement IDs.
	 */
	public ArrayList<Integer> getStatementIds(int documentId) {
		ArrayList<?> al = executeQuery(
				"SELECT ID FROM STATEMENTS WHERE Document = " + documentId);
		@SuppressWarnings("unchecked")
		ArrayList<Integer> ids = (ArrayList<Integer>) al;
		return(ids);
	}
	
	
	/**
	 * Remove a document and all statements contained in the document.
	 * 
	 * @param documentId  The ID of the document to be removed.
	 */
	public void removeDocument(int documentId) {
		ArrayList<Integer> statementIds = getStatementIds(documentId);
		for (int i = 0; i < statementIds.size(); i++) {
			removeStatement(statementIds.get(i));
		}
		executeStatement("DELETE FROM DOCUMENTS WHERE ID = " + documentId);
	}
	
	/**
	 * Add a new document to the DOCUMENTS table.
	 * 
	 * @param title   The title of the document.
	 * @param text    The text of the document.
	 * @param date    The date of the document.
	 * @param coder   Who annotated the document?
	 * @param source  What is the document source (e.g., which newspaper)?
	 * @param notes   Additional free-text notes regarding the document.
	 * @param type    Type of document (e.g., article from business section).
	 * @return        ID of the new document.
	 */
	public int addDocument(String title, String text, Date date, String coder, 
			String source, String notes, String type) {
		int intDate = (int) date.getTime();
		int id = executeStatementForId(
				"INSERT INTO DOCUMENTS (Title, Text, Date, Coder, " +
				"Source, Notes, Type) VALUES('" + title + "', '" + text + 
				"', " + intDate + ", '" + coder + "', '" + source + 
				"', '" + notes + "', '" + type + "')"
				);
		return id;
	}
}