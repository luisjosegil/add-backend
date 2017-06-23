import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.FileReader;
import java.io.IOException;

import java.util.concurrent.TimeUnit;

import java.util.Scanner;

import java.util.ArrayList;
import java.util.Arrays;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class FunctionalException extends Exception {} 
class TechnicalException extends Exception {} 

class DDBB_Queries {
/**
 * Class with every SQL query required for DDBB creation and DATA manipulation
 *
 * @author LJ Gil
 * 
*/

	private boolean strict_data_to_insert	= true;

	private String label_View_SQL 		= "__LABEL_VW_SQL__";
	private String label_Extra_Fields 	= "__LABEL_EXTRA_FIELDS__";
	private String label_parent_View_SQL 	= "__LABEL_PARENT_VIEW__";

	String table_base 	= "FLIGHTS_NO_SCALE";
	String vw_1_scale 	= "FLIGHTS_1_SCALE";
	String vw_2_scales 	= "FLIGHTS_2_SCALES";
	String vw_3_scales 	= "FLIGHTS_3_SCALES";
	String vw_4_scales 	= "FLIGHTS_4_SCALES";
	String vw_5_scales 	= "FLIGHTS_5_SCALES";

	String label_origin 	= "ORIGIN";
	String label_dest 	= "DESTINATION";
	String label_cost 	= "COST";
	
	String label_Scale1	= "SCALE1";
	String label_Scale2	= "SCALE2";
	String label_Scale3	= "SCALE3";
	String label_Scale4	= "SCALE4";
	String label_Scale5	= "SCALE5";

	String origin_value = "";
	String destination_value = "";
	String cost_value = "";

	String scale1_value = "";
	String scale2_value = "";
	String scale3_value = "";
	String scale4_value = "";
	String scale5_value = "";

	private String str_sql_generic_view = "CREATE VIEW " + label_View_SQL + " AS "
		+ "SELECT DATA_A." + label_origin + " as " + label_origin + ","
		+ label_Extra_Fields
		+ "DATA_B." + label_dest + " as " + label_dest + ","
		+ "(DATA_A." + label_cost + "+DATA_B." + label_cost + ")"
		+ " AS " + label_cost + " " 
		+ "FROM " + label_parent_View_SQL + " AS DATA_A  "
		+ "JOIN "
		+ "(SELECT " + label_origin + "," + label_dest + "," + label_cost + " "
		+ "FROM " + table_base +")  AS DATA_B "
		+ "ON DATA_B." + label_origin + "=DATA_A." + label_dest;

	void SetOrigin( String str ) {
		origin_value = str;
	}
	void SetDestination( String str ) {
		destination_value = str;
	}
	void SetCost( String str ) {
		cost_value = str;
	}
	void SetScale1( String str ) {
		scale1_value = str;
	}
	void SetScale2( String str ) {
		scale2_value = str;
	}
	void SetScale3( String str ) {
		scale3_value = str;
	}
	void SetScale4( String str ) {
		scale4_value = str;
	}
	void SetScale5( String str ) {
		scale5_value = str;
	}

	String error_TABLE_EXISTS() {
		return "already exists";
	}

	// Creation of Data Structures
	String sql_create_table() {
	/**
	 * Returns SQL command to create Data table
	 *
	 * @return sql sentence for TABLE creation
	*/
		String str_sql = "CREATE TABLE " + table_base + " (";
		str_sql += label_origin + " varchar(5) NOT NULL,";  
		str_sql += label_dest + " varchar(5) NOT NULL,";
		str_sql += label_cost + " int NOT NULL,";
		str_sql += "constraint pk_FLIGHTS primary key (";
		str_sql += label_origin + "," + label_dest +") )";
		return str_sql;
	}
	String wipe_TABLE_Data() {
	/**
	 * Returns SQL command to wipe Data table
	 *
	 * @return sql sentence to wipe Data table 
	*/
		String str_sql = "TRUNCATE TABLE " + table_base;
		return str_sql;

	}

	private String replace_in_VW_SQL(
			String name_new_View, 
			String new_fields, 
			String name_parent_View ) {
	/**
	 * Modifies GENERIC_SQL_VIEW with a new name, new fields and
	 * a new parent data Object.
	 * 
	 * @param name of the view 
	 * @param new fields to be added (SQL format)
	 * @param parent DDBB object
	 *
	 * @return sql sentence to wipe Data table 
	*/
		
		String str_sql = "" + str_sql_generic_view;
		str_sql = str_sql.replace(	label_View_SQL, 
						name_new_View);
		str_sql = str_sql.replace(	label_Extra_Fields,
						new_fields);
		str_sql = str_sql.replace(	label_parent_View_SQL,
						name_parent_View); 
		return str_sql;
	}
	private String sql_rename_field_and_comma( String old_name, String new_name ) {
	/**
	 * returns SQL sentence to rename DDBB field 
	 * 
	 * @param name to replace  
	 * @param new name
	 *
	 * @return sql sentence to rename DDBB field 
	*/
		return "" + old_name + " as " + new_name + ", ";
	}

	String sql_create_View_1_scale() {
	/**
	 * returns SQL VIEW for flights with 1 scale 
	 * 
	 * @return sql sentence to create SQL View 
	*/
		//String new_fields = "DATA_A.DEST as SCALE1,";
		String new_fields = sql_rename_field_and_comma( 
			"DATA_A." + label_dest, label_Scale1 );
		return replace_in_VW_SQL(	vw_1_scale, 
						new_fields, 
						table_base );
	}
	String sql_create_View_2_scales() {
	/**
	 * returns SQL VIEW for flights with 2 scales 
	 * 
	 * @return sql sentence to create SQL View 
	*/
		String new_fields = sql_rename_field_and_comma( 
			"DATA_A." + label_Scale1, label_Scale1 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_dest,	label_Scale2 );

		return replace_in_VW_SQL(	vw_2_scales, 
						new_fields, 
						vw_1_scale );
	}
	String sql_create_View_3_scales() {
	/**
	 * returns SQL VIEW for flights with 3 scales 
	 * 
	 * @return sql sentence to create SQL View 
	*/
		//"DATA_A.SCALE1 as SCALE1,DATA_A.SCALE2 as SCALE2,DATA_A.DEST as SCALE3,";
		String new_fields = sql_rename_field_and_comma( 
			"DATA_A." + label_Scale1, label_Scale1 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_Scale2, label_Scale2 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_dest,	label_Scale3 );
		return replace_in_VW_SQL(	vw_3_scales, 
						new_fields, 
						vw_2_scales );
	} 
	String sql_create_View_4_scales() {
	/**
	 * returns SQL VIEW for flights with 4 scales 
	 * 
	 * @return sql sentence to create SQL View 
	*/
		String new_fields = sql_rename_field_and_comma( 
			"DATA_A." + label_Scale1, label_Scale1 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_Scale2, label_Scale2 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_Scale3, label_Scale3 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_dest,	label_Scale4 );
		return replace_in_VW_SQL(	vw_4_scales, 
						new_fields, 
						vw_3_scales );
	} 
	String sql_create_View_5_scales() {
	/**
	 * returns SQL VIEW for flights with 5 scales 
	 * 
	 * @return sql sentence to create SQL View 
	*/
		String new_fields = sql_rename_field_and_comma( 
			"DATA_A." + label_Scale1, label_Scale1 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_Scale2, label_Scale2 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_Scale3, label_Scale3 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_Scale4, label_Scale4 );
		new_fields += sql_rename_field_and_comma(	
			"DATA_A." + label_dest,	label_Scale5 );
		return replace_in_VW_SQL(	vw_4_scales, 
						new_fields, 
						vw_3_scales );
	}

	// Data Insertion
	private String sub_sql_format(	String str_origin, 
					String str_destination, 
					String str_cost) {
	/**
	 * returns sub-SQL to insert data 
	 * 
	 * @param Origin 
	 * @param Destination 
	 * @param Cost 
	 * @return returns sub-SQL to insert data 
	*/
		return "(\'"+str_origin+"\',\'"+str_destination+"\',"+str_cost+")";
	}
	String sql_insert( 	String str_origin, 
				String str_destination, 
				String str_cost ) {
	/**
	 * returns SQL sentence to insert a dataset 
	 * 
	 * @param Origin 
	 * @param Destination 
	 * @param Cost 
	 * @return returns SQL sentence to insert a dataset 
	*/
		String str_sql = "INSERT INTO " + table_base + " "
			+ "(" + label_origin + "," + label_dest + "," + label_cost + ") VALUES ";
		if (strict_data_to_insert) {
			// One data set -> one DDBB register
			str_sql += sub_sql_format( str_origin, str_destination, str_cost);
		} else {
			// One data set -> two-way trip to DDBB
			str_sql += sub_sql_format( str_origin, str_destination, str_cost) + ",";
			str_sql += sub_sql_format( str_destination, str_origin, str_cost);	
		}
		return str_sql;
	} 

	// Data Search
	String sql_select_flight_no_scales() {
	/**
	 * returns SQL sentence to retrieve flights with no scales
	 * orderd by cost 
	 * 
	 * @return returns SQL sentence to retrieve flights with no scales 
	*/
		String str_sql = "SELECT * FROM "; 
		str_sql += table_base + " ";
		str_sql += "WHERE " + label_origin + "='" + origin_value + "' ";
		str_sql += "AND " + label_dest + "='" + destination_value + "' ";
		str_sql += "ORDER BY " + label_cost + " ASC";
		return str_sql;
	}
	private String sql_add_extra_airport( String field, String value ){
	/**
	 * returns sub-SQL sentence to add scale in flight search
	 * 
	 * @param Scale DDBB tag 
	 * @param Scale 
	 * @return returns sub-SQL sentence to add scale in flight search 
	*/
		String str_sql = "AND " + field + "='" + value + "' ";
		return str_sql;
	}
	private String sql_add_airport_if_not_null( String field, String value ){
	/**
	 * returns sub-SQL sentence to add scale in flight search
	 * if scale is not null
	 * 
	 * @param Origin 
	 * @param Destination 
	 * @return returns SQL sentence to retrieve flights with no scales 
	*/
		if ( value != null ) {
			return sql_add_extra_airport( field, value );
		}
		return "";
	}
	private String sql_AddCostIfNotNull( String str_cost ){
	/**
	 * returns sub-SQL sentence to add cost condition in flight search
	 * if scale is not null
	 * 
	 * @param Origin 
	 * @param Destination 
	 * @return returns SQL sentence to retrieve flights with no scales 
	*/
	//String label_cost 	= "COST";
		if ( str_cost != null ) {
			return "AND " + label_cost + "<" + str_cost + " " ;
		}
		return "";
	}

	String sql_select_flight_1_scale() { 
	/**
	 * returns SQL sentence to retrieve flights with 1 scale
	 * scales can be set to null, or not null
	 * orderd by cost 
	 * 
	 * @param Origin 
	 * @param Scale1 
	 * @param Destination 
	 * @param Cost 
	 * @return returns SQL sentence to retrieve flights with no scales 
	*/
		// ORDERD BY COST
		String str_sql = "SELECT * FROM "; 
		str_sql += vw_1_scale + " ";
		str_sql += "WHERE " + label_origin + "='" + origin_value + "' ";
		str_sql += sql_add_extra_airport(label_dest,destination_value);
		str_sql += sql_add_airport_if_not_null( label_Scale1, scale1_value );
		str_sql += sql_AddCostIfNotNull( cost_value );
		str_sql += "ORDER BY COST ASC";
		return str_sql;
	}
	String sql_select_flight_2_scales() { 
	/**
	 * returns SQL sentence to retrieve flights with 2 scales
	 * scales can be set to null, or not null
	 * orderd by cost 
	 * 
	 * @return returns SQL sentence to retrieve flights with no scales 
	*/
		String str_sql = "SELECT * FROM "; 
		str_sql += vw_2_scales + " ";
		str_sql += "WHERE " + label_origin + "='" + origin_value + "' ";
		str_sql += sql_add_extra_airport(label_dest,destination_value);
		str_sql += sql_add_airport_if_not_null( label_Scale1, scale1_value );
		str_sql += sql_add_airport_if_not_null( label_Scale2, scale2_value );
		str_sql += sql_AddCostIfNotNull( cost_value );
		str_sql += "ORDER BY COST ASC";
		return str_sql;
	}
	String sql_select_flight_3_scales() { 
	/**
	 * returns SQL sentence to retrieve flights with 3 scales
	 * scales can be set to null, or not null
	 * orderd by cost 
	 * 
	 * @return returns SQL sentence to retrieve flights with no scales 
	*/
		// ORDERD BY COST
		String str_sql = "SELECT * FROM "; 
		str_sql += vw_3_scales + " ";
		str_sql += "WHERE " + label_origin + "='" + origin_value + "' ";
		str_sql += sql_add_extra_airport(label_dest,destination_value);
		str_sql += sql_add_airport_if_not_null( label_Scale1, scale1_value );
		str_sql += sql_add_airport_if_not_null( label_Scale2, scale2_value );
		str_sql += sql_add_airport_if_not_null( label_Scale3, scale3_value );
		str_sql += sql_AddCostIfNotNull( cost_value );
		str_sql += "ORDER BY COST ASC";
		return str_sql;
	}
	String sql_select_flight_4_scales() { 
	/**
	 * returns SQL sentence to retrieve flights with 4 scales
	 * scales can be set to null, or not null
	 * orderd by cost 
	 * 
	 * @return returns SQL sentence to retrieve flights with no scales 
	*/
		String str_sql = "SELECT * FROM "; 
		str_sql += vw_4_scales + " ";
		str_sql += "WHERE " + label_origin + "='" + origin_value + "' ";
		str_sql += sql_add_extra_airport(label_dest,destination_value);
		str_sql += sql_add_airport_if_not_null( label_Scale1, scale1_value );
		str_sql += sql_add_airport_if_not_null( label_Scale2, scale2_value );
		str_sql += sql_add_airport_if_not_null( label_Scale3, scale3_value );
		str_sql += sql_add_airport_if_not_null( label_Scale4, scale4_value );
		str_sql += sql_AddCostIfNotNull( cost_value );
		str_sql += "ORDER BY COST ASC";
		return str_sql;
	}
	String sql_select_flight_5_scales() { 
	/**
	 * returns SQL sentence to retrieve flights with 4 scales
	 * scales can be set to null, or not null
	 * orderd by cost 
	 * 
	 * @return returns SQL sentence to retrieve flights with no scales 
	*/
		String str_sql = "SELECT * FROM "; 
		str_sql += vw_5_scales + " ";
		str_sql += "WHERE " + label_origin + "='" + origin_value + "' ";
		str_sql += sql_add_extra_airport(label_dest,destination_value);
		str_sql += sql_add_airport_if_not_null( label_Scale1, scale1_value );
		str_sql += sql_add_airport_if_not_null( label_Scale2, scale2_value );
		str_sql += sql_add_airport_if_not_null( label_Scale3, scale3_value );
		str_sql += sql_add_airport_if_not_null( label_Scale4, scale4_value );
		str_sql += sql_add_airport_if_not_null( label_Scale5, scale5_value );
		str_sql += sql_AddCostIfNotNull( cost_value );
		str_sql += "ORDER BY COST ASC";
		return str_sql;
	}
}

class DAO {
/**
 * Data Access Class. It is the DDBB abstraction layer
 *  
 *
 * @author LJ Gil
 * 
*/

	// Data Access object.
	// In case we want to replace the DDBB, we don't need to modify any upper class
	private boolean logs = false;

	private String str_driver = "org.apache.derby.jdbc.EmbeddedDriver";
	String str_ddbbname = "dbFLIGHTS";
	
	private Connection conn = null;
	private DDBB_Queries obj_DDBB_Queries = new DDBB_Queries();

	private void log(String str) {
		if ( logs == true ) {
			System.out.println(str);
		}
	}

	void create_DDBB_connection() {
	/**
	 * Load DDBB driver 
	 *
	*/
		try {
			Class.forName(str_driver);
			log("Driver loaded");
		} catch ( java.lang.ClassNotFoundException e ) {
			
			System.out.println("Driver not loaded\n");
			System.out.println(e.toString());
		} 

	}
	void boot_DDBB() {
	/**
	 * Creates jdbc Connection Obj to javaDB database 
	 * Connection is stored within the object
	*/
		try {
			String str_conn_URL = "jdbc:derby:" + str_ddbbname + ";create=true;";	
			conn = DriverManager.getConnection(str_conn_URL);
			log("DDBB booted");
		} catch (Throwable e) {
			log("DDBB not booted");
			log(e.toString());
		}
	}
	void create_DDBB_if_not_exists() 
		throws FunctionalException, TechnicalException {
	/**
	 * Creates database structure if it doesn't exist previously. 
	 * 
	 * @throws FunctionalException if Table already exist  
	 * @throws TechnicalException with any other SQL error  
	*/
		try {
			Statement s = conn.createStatement();
			s.execute( obj_DDBB_Queries.sql_create_table() );
			log( obj_DDBB_Queries.sql_create_table() );
			log( "BASE Table created" );
			s.execute( obj_DDBB_Queries.sql_create_View_1_scale() );
			log( obj_DDBB_Queries.sql_create_View_1_scale() );
			log( "1 Scale view created");
			s.execute( obj_DDBB_Queries.sql_create_View_2_scales() );
			log( obj_DDBB_Queries.sql_create_View_2_scales() );
			log( "2 Scales view created" );
			s.execute( obj_DDBB_Queries.sql_create_View_3_scales() );
			log( obj_DDBB_Queries.sql_create_View_3_scales() );
			log( "3 Scales view created" );
			s.execute( obj_DDBB_Queries.sql_create_View_4_scales() );
			log( obj_DDBB_Queries.sql_create_View_4_scales() );
			log( "4 Scales view created" );
		} catch (SQLException e) {
			String str_msg = e.getMessage();
			if ( str_msg.contains( obj_DDBB_Queries.error_TABLE_EXISTS() ) ) {
				throw new FunctionalException();
			} 
			log( str_msg );	
			throw new TechnicalException();
		}
	}
	void wipe_DDBB_data() {
	/**
	 * Removes all data from database table 
	 * 
	*/
		try {
			Statement s = conn.createStatement();
			s.execute( obj_DDBB_Queries.wipe_TABLE_Data());
			log( obj_DDBB_Queries.wipe_TABLE_Data() );
			log( "DATA Table wiped" );
		} catch (SQLException e) {
			String str_msg = e.getMessage();
			log( str_msg );
			log( "ERROR. Table not wiped" );
		}
	}
	void create_DDBB() {
	/**
	 * Creates database and if it already exists, wipe it out. 
	 * 
	*/
		try {
			create_DDBB_if_not_exists();
			log( "DATA Structure created" );
		} catch (FunctionalException f_e) {
			wipe_DDBB_data();
		} catch (TechnicalException t_e) {
			System.out.println("ERROR. Technical error creating DDBB");
			System.exit(1);
		}
	}

	void create_data_structure() {
	/**
	 * creates connection if it didn't exist yet
	 * and creates DDBB structure 
	 * 
	*/
		if ( conn == null ) {
			log( "No conn to DDBB" );
			create_DDBB_connection();
		} 
		boot_DDBB();
		create_DDBB();
	}
	void insert_row( String str_origin, String str_dest, int cost ) {
	/**
	 * Inserts data into database (just one row) 
	 * 
	 * @param Origin 
	 * @param Destination 
	 * @param Cost 
	*/
		try {
			Statement s = conn.createStatement();
			String query = 	obj_DDBB_Queries.sql_insert(
						str_origin,
						str_dest,
						""+cost	);
			System.out.println(query);
			int result = s.executeUpdate( query );
			if ( logs ) {	System.out.println("" + result + " rows added");	}
		} catch (SQLException e) {
			String str_msg = e.getMessage();
			log( "ERROR. Data not inserted" );
			log( str_msg );
		}
	}
	void SetLogs() {
	/**
	 * Sets LOGGING to TRUE 
	 * 
	*/
		logs = true;
	}
	void SetOrigin( String str ) {
	/**
	 * Sets Origin parameter for searchs 
	 * 
	 * @param Origin 
	*/
		obj_DDBB_Queries.SetOrigin( str );
	}
	void SetDestination( String str ) {
	/**
	 * Sets Destination parameter for searchs 
	 * 
	 * @param Destination 
	*/
		obj_DDBB_Queries.SetDestination( str );
	}
	void SetCost( String str ) {
	/**
	 * Sets Cost parameter for searchs 
	 * 
	 * @param Cost 
	*/
		obj_DDBB_Queries.SetCost( str );
	}
	void SetScale1( String str ) {
	/**
	 * Sets Scale 1 parameter for searchs 
	 * 
	 * @param Scale1 
	*/
		obj_DDBB_Queries.SetScale1( str );
	}
	void SetScale2( String str ) {
	/**
	 * Sets Scale 2 parameter for searchs 
	 * 
	 * @param Scale2 
	*/
		obj_DDBB_Queries.SetScale2( str );
	}
	void SetScale3( String str ) {
	/**
	 * Sets Scale 3 parameter for searchs 
	 * 
	 * @param Scale3 
	*/
		obj_DDBB_Queries.SetScale3( str );
	}
	void SetScale4( String str ) {
	/**
	 * Sets Scale4 parameter for searchs 
	 * 
	 * @param Scale4 
	*/
		obj_DDBB_Queries.SetScale4( str );
	}
	void SetScale5( String str ) {
	/**
	 * Sets Scale5 parameter for searchs 
	 * 
	 * @param Scale5 
	*/
		obj_DDBB_Queries.SetScale5( str );
	}
	void init_data() {
	/**
	 * Sets all search params to null 
	 * 
	*/
		SetOrigin(null);
		SetDestination(null);
		SetCost(null);
		SetScale1(null);
		SetScale2(null);
		SetScale3(null);
		SetScale4(null);
		SetScale5(null);
	}

	ArrayList<DDBB_Data> retrieve_flight_no_scales() throws FunctionalException, TechnicalException {
	/**
	 * Retrieves flights information with no scales, and ordered by cost
	 * returns array of DDBB_Data objects, one for each flight
	 * 
	 * @return returns array of DDBB_Data objects 
	 * @throws FunctionalException if Table already exist  
	 * @throws TechnicalException with any other SQL error  
	*/
		String sql = obj_DDBB_Queries.sql_select_flight_no_scales();
		ArrayList<DDBB_Data> Array_Data_Objs = new ArrayList<DDBB_Data>();
		log( sql );
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				DDBB_Data Data_Obj = new DDBB_Data();
				Data_Obj.set_origin( rs.getString(obj_DDBB_Queries.label_origin) );
				Data_Obj.set_destination( rs.getString(
								obj_DDBB_Queries.label_dest) );
				Data_Obj.set_cost( rs.getInt(obj_DDBB_Queries.label_cost) );
				String str = Data_Obj.get_whole_trip();
				System.out.println(str);
				Array_Data_Objs.add( Data_Obj );	
			}
		} catch (SQLException e) {
			log( e.getMessage() );
			throw new TechnicalException();
		}
		return Array_Data_Objs;
	}
	ArrayList<DDBB_Data> retrieve_flight_1_scale() throws FunctionalException, TechnicalException {
	/**
	 * Retrieves flights information with one scale, and ordered by cost
	 * if scales are not to be used to filter the trips, set it to null
	 * returns array of DDBB_Data objects, one for each flight
	 * 
	 * @return returns array of DDBB_Data objects 
	 * @throws FunctionalException if Table already exist  
	 * @throws TechnicalException with any other SQL error  
	*/
		String sql = obj_DDBB_Queries.sql_select_flight_1_scale();
		log( sql );

		ArrayList<DDBB_Data> Array_Data_Objs = new ArrayList<DDBB_Data>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				DDBB_Data Data_Obj = new DDBB_Data();
				Data_Obj.set_origin( rs.getString(obj_DDBB_Queries.label_origin) );
				Data_Obj.set_destination( rs.getString(
								obj_DDBB_Queries.label_dest) );
				Data_Obj.set_cost( rs.getInt(obj_DDBB_Queries.label_cost) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale1) );
				String str = Data_Obj.get_whole_trip();
				log( str );
				Array_Data_Objs.add( Data_Obj );
			}
		} catch (SQLException e) {
			log( "ERROR.retrieve_flight_1_scale. Query error." );
			log( e.getMessage() );
			throw new TechnicalException();
		}
		return Array_Data_Objs;
	}
	ArrayList<DDBB_Data> retrieve_flight_2_scales() throws FunctionalException, TechnicalException {
	/**
	 * Retrieves flights information with two scales, and ordered by cost
	 * if scales are not to be used to filter the trips, set it to null
	 * 
	 * @return returns array of DDBB_Data objects 
	 * @throws FunctionalException if Table already exist  
	 * @throws TechnicalException with any other SQL error  
	*/
		String sql = obj_DDBB_Queries.sql_select_flight_2_scales();
		log( sql );
		ArrayList<DDBB_Data> Array_Data_Objs = new ArrayList<DDBB_Data>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				DDBB_Data Data_Obj = new DDBB_Data();
				Data_Obj.set_origin( rs.getString(obj_DDBB_Queries.label_origin) );
				Data_Obj.set_destination( rs.getString(
								obj_DDBB_Queries.label_dest) );
				Data_Obj.set_cost( rs.getInt(obj_DDBB_Queries.label_cost) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale1) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale2) );
				String str = Data_Obj.get_whole_trip();
				log( str );
				Array_Data_Objs.add( Data_Obj );
			}
		} catch (SQLException e) {
			log( "ERROR.retrieve_flight_2_scales. Query error." );
			log( e.getMessage() );
			throw new TechnicalException();
		}
		return Array_Data_Objs;
	}
	ArrayList<DDBB_Data> retrieve_flight_3_scales() throws FunctionalException, TechnicalException {
	/**
	 * Retrieves flights information with three scales, and ordered by cost
	 * if scales are not to be used to filter the trips, set it to null
	 * 
	 * @return returns array of DDBB_Data objects 
	 * @throws FunctionalException if Table already exist  
	 * @throws TechnicalException with any other SQL error  
	*/
		String sql = obj_DDBB_Queries.sql_select_flight_3_scales();
		log( sql );

		ArrayList<DDBB_Data> Array_Data_Objs = new ArrayList<DDBB_Data>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				DDBB_Data Data_Obj = new DDBB_Data();
				Data_Obj.set_origin( rs.getString(obj_DDBB_Queries.label_origin) );
				Data_Obj.set_destination( rs.getString(
								obj_DDBB_Queries.label_dest) );
				Data_Obj.set_cost( rs.getInt(obj_DDBB_Queries.label_cost) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale1) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale2) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale3) );
				String str = Data_Obj.get_whole_trip();
				log( str );
				Array_Data_Objs.add( Data_Obj );
			}
		} catch (SQLException e) {
			log( "ERROR.retrieve_flight_3_scales. Query error." );
			log( e.getMessage() );
			throw new TechnicalException();
		}
		return Array_Data_Objs;
	}
	ArrayList<DDBB_Data> retrieve_flight_4_scales() throws FunctionalException, TechnicalException {
	/**
	 * Retrieves flights information with four scales, and ordered by cost
	 * if scales are not to be used to filter the trips, set it to null
	 * 
	 * @return returns array of DDBB_Data objects 
	 * @throws FunctionalException if Table already exist  
	 * @throws TechnicalException with any other SQL error  
	*/
		String sql = obj_DDBB_Queries.sql_select_flight_4_scales();
		log( sql );

		ArrayList<DDBB_Data> Array_Data_Objs = new ArrayList<DDBB_Data>();
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				DDBB_Data Data_Obj = new DDBB_Data();
				Data_Obj.set_origin( rs.getString(obj_DDBB_Queries.label_origin) );
				Data_Obj.set_destination( rs.getString(
								obj_DDBB_Queries.label_dest) );
				Data_Obj.set_cost( rs.getInt(obj_DDBB_Queries.label_cost) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale1) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale2) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale3) );
				Data_Obj.add_scale( rs.getString(obj_DDBB_Queries.label_Scale4) );
				String str = Data_Obj.get_whole_trip();
				Array_Data_Objs.add( Data_Obj );
				if ( logs ) {	System.out.println(str);	}
			}
		} catch (SQLException e) {
			log( "ERROR.retrieve_flight_4_scales. Query error." );
			log( e.getMessage() );
			throw new TechnicalException();
		}
		return Array_Data_Objs;
	}
	// SECOND LEVEL SEARCH:
	ArrayList<DDBB_Data> SearchFlightWithoutBudget( String[] tokens ) 
				throws FunctionalException, TechnicalException {
	/**
	 * Retrieves flights info with up to 4 scales 
	 * we don't consider cost limitations
	 * 
	 * @param array of strings, origin, scale1,...,Destination 
	 * @return returns array of DDBB_Data objects, null if no data 
	 * @throws FunctionalException if Table already exist  
	 * @throws TechnicalException with any other SQL error  
	*/
		int num_tokens = tokens.length;
		ArrayList<DDBB_Data> Array_Objs = new ArrayList<DDBB_Data>();
		init_data();
		switch (num_tokens) {
			case 2: // Direct flight
				log("No budget. No Scales");
				SetOrigin( tokens[0] );
				SetDestination( tokens[1] );
				SetCost( null );
				Array_Objs = retrieve_flight_no_scales();
				break;
			case 3:	// Flight with 1 scale
				log("No budget. 1 Scale");
				SetOrigin( tokens[0] );
				SetScale1( tokens[1] );
				SetDestination( tokens[2] );
				SetCost( null );
				Array_Objs = retrieve_flight_1_scale();
				break;
			case 4:	// Flight with two scales
				log("No budget. 2 Scales");
				SetOrigin( tokens[0] );
				SetScale1( tokens[1] );
				SetScale2( tokens[2] );
				SetDestination( tokens[3] );
				SetCost( null );
				Array_Objs = retrieve_flight_2_scales();
				break;
			case 5:	// Flight with 3 scales
				log("No budget. 3 Scales");
				SetOrigin( tokens[0] );
				SetScale1( tokens[1] );
				SetScale2( tokens[2] );
				SetScale3( tokens[3] );
				SetDestination( tokens[4] );
				SetCost( null );
				Array_Objs = retrieve_flight_3_scales();
		
				break;
			case 6:	// Flight with 4 scales
				log("No budget. 4 Scales");
				SetOrigin( tokens[0] );
				SetScale1( tokens[1] );
				SetScale2( tokens[2] );
				SetScale3( tokens[3] );
				SetScale4( tokens[4] );
				SetDestination( tokens[5] );
				SetCost( null );
				Array_Objs = retrieve_flight_4_scales(); 
				break;
			default:
				log("Flight Info not properly filled");
				break;	
		}
		return Array_Objs;
	}
	ArrayList<DDBB_Data> SearchFlightWithBudget( String[] tokens ) 
				throws FunctionalException, TechnicalException {
	/**
	 * Retrieves flights info with up to 4 scales 
	 * 
	 * @param array of strings, origin, scale1,...,Destination 
	 * @return returns array of DDBB_Data objects, null if no data 
	 * @throws FunctionalException if Table already exist  
	 * @throws TechnicalException with any other SQL error  
	*/
		int num_tokens = tokens.length;
		ArrayList<DDBB_Data> Array_Objs = new ArrayList<DDBB_Data>();
		init_data();
		switch (num_tokens) {
			case 3: // Direct flight
				log("Budget. no scales");
				SetOrigin( tokens[0] );
				SetDestination( tokens[1] );
				SetCost( tokens[2] );
				Array_Objs = retrieve_flight_no_scales();
				break;
			case 4:	// Flight with 1 scale
				log("Budget. 1 Scales");
				SetOrigin( tokens[0] );
				SetScale1( tokens[1] );
				SetDestination( tokens[2] );
				SetCost( tokens[3] );
				Array_Objs = retrieve_flight_1_scale();
				break;
			case 5:	// Flight with two scales
				log("Budget. 2 Scales");
				SetOrigin( tokens[0] );
				SetScale1( tokens[1] );
				SetScale2( tokens[2] );
				SetDestination( tokens[3] );
				SetCost( tokens[4] );
				Array_Objs = retrieve_flight_2_scales();
				break;
			case 6:	// Flight with 3 scales
				log("Budget. 3 Scales");
				SetOrigin( tokens[0] );
				SetScale1( tokens[1] );
				SetScale2( tokens[2] );
				SetScale3( tokens[3] );
				SetDestination( tokens[4] );
				SetCost( tokens[5] );
				Array_Objs = retrieve_flight_3_scales();
				break;
			case 7:	// Flight with 4 scales
				log("Budget. 4 Scales");
				SetOrigin( tokens[0] );
				SetScale1( tokens[1] );
				SetScale2( tokens[2] );
				SetScale3( tokens[3] );
				SetScale4( tokens[4] );
				SetDestination( tokens[5] );
				SetCost( tokens[6] );
				Array_Objs = retrieve_flight_4_scales(); 
				break;
			default:
				log("Flight Info not properly filled");
				break;	
		}
		return Array_Objs;
	}
}

class Data_Class {
/**
 * Class to collect info from File streams 
 * Data is expected to have this format: 
 * Origin-Destination-Cost
 * Example:
 * AMS-PDX-617 
 *
 * @author LJ Gil
*/

	private String str_origin;
	private String str_destination;
	private int i_cost;

	void set_data_from_stream( String stream ) {
	/**
	 * Parses data from row and stores it internally 
	 * 
	 * @param unformatted row from file 
	*/
		// Stream with format AMS-PDX-617
		try {
			String[] tokens = stream.split("-");
			set_origin( tokens[0] );
			set_destination( tokens[1] );
			set_cost( tokens[2] );
		} catch (Exception e) {
			String msg = "Technical error reading data from file";
			System.out.println(msg);
			System.exit(202);
		}
	}
	private void set_origin( String data ) {
	/**
	 * Sets origin parameter 
	 * 
	 * @param origin 
	*/
		str_origin = (String) data;
	}
	private void set_destination( String data ) {
	/**
	 * Sets destination parameter 
	 * 
	 * @param destination 
	*/
		str_destination = (String) data;
	}
	private void set_cost( String data ) throws FunctionalException {
	/**
	 * Evaluates cost format and stores it 
	 * 
	 * @param cost 
	 * @throws FunctionalException if format is not proper  
	*/
		try {	
			i_cost = Integer.parseInt(data);
		}catch(NumberFormatException e){
			throw new FunctionalException();
		} 

	}
	String get_origin() {		
	/**
	 * Retrieves origin from obj 
	 * 
	 * @return origin
	*/
		return str_origin;	
	}
	String get_destination(){	
	/**
	 * Retrieves destination from obj 
	 * 
	 * @return destination 
	*/
		return str_destination;	
	}
	int get_cost()	{		
	/**
	 * Retrieves cost from obj 
	 * 
	 * @return cost
	*/
		return i_cost;		
	} 

	String print_data() {
	/**
	 * Retrieves printable data from obj 
	 * 
	 * @return String
	*/
		return ""+str_origin+"-"+str_destination+"-"+i_cost;
	}
}

class DDBB_Data {
/**
 * Class to present Data coming from DDBB 
 *
 * @author LJ Gil
*/
	private int num_scales;
	private String origin;
	private ArrayList<String> Scales = new ArrayList<String>();
	private String destination;
	private int cost;

	void set_origin( String str ) {	
	/**
	 * Sets origin parameter 
	 * 
	 * @param origin 
	*/
		origin = "" + str;	
	}

	void add_scale(String str) {
	/**
	 * Sets or adds scale to Array 
	 * 
	 * @param scale 
	*/
		Scales.add("" + str);
	}

	void set_destination( String str ) {	
	/**
	 * Sets destination parameter 
	 * 
	 * @param destination 
	*/
		destination = "" + str;	
	}

	void set_cost( int i ) {	
	/**
	 * Sets cost parameter 
	 * 
	 * @param cost 
	*/
		cost = i;	
	}
	int get_cost() {		
	/**
	 * Gets cost parameter 
	 * 
	 * @return cost 
	*/
		return cost;	
	}
	int get_num_scales() {
	/**
	 * Gets number of scales 
	 * 
	 * @return num_scales 
	*/
		num_scales = Scales.size();
		return num_scales;
	}
	String get_whole_trip() {
	/**
	 * Gets whole trip in printable format 
	 * 
	 * @return String 
	*/
		get_num_scales();	
		String str = "" + origin;
		if ( num_scales > 0 ) {
			for ( int i=0; i<num_scales; i++ ) {
				str = str + "-" + Scales.get(i);
			}
		}
		return str + "-" + destination + "-" + cost;
	}
}


class FileToDDBB {
/**
 * Class to process file and store data into DDBB 
 *
 * @author LJ Gil
*/
	private boolean logs = false;
	private String file_not_found_err = "No such file";

	private void log(String str) {
		if ( logs == true ) {
			System.out.println(str);
		}
	}
	private void insert_into_DDBB(  DAO ddbb, Data_Class data_obj ) {
	/**
	 * Gets whole trip in printable format 
	 *
	 * @param DAO object for DDBB manipulations 
	 * @param Data_Class object with data 
	*/
		ddbb.insert_row(data_obj.get_origin(),
				data_obj.get_destination(),
				data_obj.get_cost()  );
	}
	private void log_info( Data_Class data_obj, int files_counter ) {
	/**
	 * Logs message to STDOUT, if logs are enabled 
	 *
	 * @param DAO object for DDBB manipulations 
	 * @param number of lines 
	*/
		log("-----------------");
		log("File line " + files_counter );
		log( data_obj.print_data() );
	}

	int processSTDIN( BufferedReader buffer, DAO ddbb ) {
	/**
	 * Reads from IN and stores into DDBB 
	 *
	 * @param BufferedReader to file 
	 * @param DAO object to access DDBB 
	*/
		String file_line;
		int files_counter = 0;
		ddbb.create_data_structure();	
		Data_Class data_obj;
		try {
			while ( (file_line = buffer.readLine() ) != null ) {
				data_obj = new Data_Class();
				data_obj.set_data_from_stream(file_line);		
				insert_into_DDBB( ddbb, data_obj );
				log_info( data_obj, files_counter++ );
				data_obj = null;
			}
		} catch (IOException e) {
			System.out.println( "I/O Error" );
			System.exit(10);
		}
		return files_counter;
	}
	int processFile( String fileName, DAO ddbb ) {
	/**
	 * Opens file and stores data into DDBB 
	 *
	 * @param fileName 
	 * @param DAO object to access DDBB 
	 * @return number of lines
	*/
		int files_counter = 0;
		try {
			FileInputStream f_is = new FileInputStream( fileName );
			InputStreamReader isr = new InputStreamReader(
							f_is, 
							Charset.forName("UTF-8") );
			BufferedReader br = new BufferedReader(isr);
			files_counter = processSTDIN( br, ddbb );
		} catch (IOException e) {
			String str_msg = e.getMessage();
			if ( str_msg.contains(file_not_found_err) ) {
				System.out.println( "Error. File " + fileName + " not found" );
			} else { 
				System.out.println( "processFile. I/O Error" );
				System.out.println(str_msg);
			}
			System.exit(10);
		}
		return files_counter;
	}
}

class Cli_Interface {
/**
 * User interface class 
 *
 * @author LJ Gil
*/
	private boolean logs = false;
	private Scanner scanner = new Scanner(System.in); 
	private String no_conn_found_error = "No such connection found!";
	private String unproper_incoming_data = "Incoming data not properly formatted";

	String SendMessageAndReturnAnswer( String msg ) {
	/**
	 * Presents message to User and returns answer 
	 *
	 * @param message 
	 * @return answer 
	*/
		System.out.println( msg );
		return scanner.next();
	}
	String get_filename() {
	/**
	 * Retrieves DATA filename 
	 *
	 * @return filename 
	*/
		return SendMessageAndReturnAnswer(
			"Introduce filename with data:" );
	}
	private void clearScreen() {  
	/**
	 * Clear User screen 
	*/
	    System.out.print("\033[H\033[2J");  	
		System.out.flush();  
	}


	private int show_options_and_return_chosen() {
	/**
	 * Show options to user an return election 
	 *
	 * @return user_election 
	*/
		clearScreen();
		System.out.println( "Options available for search" );
		System.out.println( "1 - Search price for a flight" );
		System.out.println( "2 - Search cheapest conn between A and B" );
		System.out.println( "3 - Search num of flights between A and B with maximum 3 stops" );
		System.out.println( "4 - Search num of flights between A and B with minimum 1 stop" );
		System.out.println( "5 - Search num of flights between A and B with exactly 1 stop" );
		System.out.println( "6 - Search num of flights between A and B with exactly 2 stops" );
		System.out.println( "7 - Search all flights between A and B which cost is below X" );
		try {
			int option = Integer.parseInt( scanner.next() );
			if ( option>0 && option<=7 ) {
				return option;
			}
			return -1;
		} catch (Exception e) {
			String str_msg = e.getMessage();
			System.out.println( str_msg );
			return -1;
		}

	}
	private void SleepForXSecs( int secs ) {
	/**
	 * Sleep for X secons 
	 *
	 * @param seconds 
	*/
		try { 
			TimeUnit.SECONDS.sleep(secs);
		} catch ( Exception e ) {
			 System.out.println( "time" );
		}
	
	}
	void UserNavigation( DAO obj_DDBB ) {
	/**
	 * User navigation control 
	 *
	 * @param DAO object to access DDBB 
	*/
		int num_secs_to_wait = 5;
		for(;;) {
			switch ( show_options_and_return_chosen() ) {
				case 1:
					int price =  AskFlightWithoutCostAndReturnPrice(
							obj_DDBB ); 
					//int price = AskFlightInfoAndReturnPrice(obj_DDBB);
					SleepForXSecs(num_secs_to_wait);
					break;
				case 2:
					FindCheapestConnBetweenTwoAirports( obj_DDBB );
					SleepForXSecs(num_secs_to_wait);
					break;
				case 3:
					FindFlightsWithMaxThreeScales( obj_DDBB );
					SleepForXSecs(num_secs_to_wait);
					break;
				case 4:
					FindFlightsWithMinOneScale( obj_DDBB );
					SleepForXSecs(num_secs_to_wait);
					break;
				case 5: FindFlightsWithOneScale( obj_DDBB );
					SleepForXSecs(num_secs_to_wait);
					break;
				case 6:	
					FindFlightsWithTwoScales( obj_DDBB );
					SleepForXSecs(num_secs_to_wait);
					break;
				case 7:
					FindFlightsWithBudgetLimitation( obj_DDBB );
					SleepForXSecs(num_secs_to_wait);
					break;
				default:
					System.out.println("Option not valid.");
					SleepForXSecs(num_secs_to_wait);
					break;

			}
		}	

	}

	private String[] SplitTextLine( String line) {
	/**
	 * Splits stream into tokens, line separated 
	 *
	 * @param stream
	 * @return array of tokens 
	*/
		String[] tokens = line.split("-");
		return tokens;
	}
	private ArrayList<DDBB_Data> try_flight_search_without_budget( DAO obj_DDBB, String[] tokens ) {
	/**
	 * Search flights without budget limitations 
	 *
	 * @param DAO object to access DDBB 
	 * @param array of tokens 
	 * @return ArrayList of DDBB_Data objects 
	*/
		ArrayList<DDBB_Data> Array_Objs = new ArrayList<DDBB_Data>();
		try {
			Array_Objs = obj_DDBB.SearchFlightWithoutBudget( tokens );
		} catch ( FunctionalException e ) {
			System.out.println("Functional exception (not managed yet)");
			System.exit(101);
		} catch ( TechnicalException e ) {
			System.out.println("Tech exception (not managed yet)");
			System.exit(201);
		}
		return Array_Objs;
	}
	private ArrayList<DDBB_Data> try_flight_search_with_budget( DAO obj_DDBB, String[] tokens ) {
	/**
	 * Search flights with budget limitations 
	 *
	 * @param DAO object to access DDBB 
	 * @param array of tokens 
	 * @return ArrayList of DDBB_Data objects 
	*/
		ArrayList<DDBB_Data> Array_Objs = new ArrayList<DDBB_Data>();
		try {
			Array_Objs = obj_DDBB.SearchFlightWithBudget( tokens );
		} catch ( FunctionalException e ) {
			System.out.println("Functional exception (not managed yet)");
			System.exit(101);
		} catch ( TechnicalException e ) {
			System.out.println("Tech exception (not managed yet)");
			System.exit(201);
		}
		return Array_Objs;
	}

	int AskFlightWithoutCostAndReturnPrice( DAO obj_DDBB ) {
	/**
	 * Ask user for flight route, without cost, and return cost, if flight exists
	 *
	 * @param DAO object to access DDBB 
	 * @return cost
	*/
		String msg = "Introduce complete flight info: Example NUE-LHR-BOS";
		String answer = SendMessageAndReturnAnswer( msg );
		String[] tokens = SplitTextLine( answer );
		ArrayList<DDBB_Data> Array_Objs = try_flight_search_without_budget( 
							obj_DDBB,
							tokens );
		int price = -1;
		if ( Array_Objs.size() > 0 ) {
			// We chhose the firts object,
			// because the objs are arranged by price ASC
			DDBB_Data ddbb_obj = Array_Objs.get(0);
			price = ddbb_obj.get_cost();
			System.out.println( price );
		} else {
			System.out.println(no_conn_found_error);
		}
		return price;
	}
	private DDBB_Data publishCheapestTrip( ArrayList<DDBB_Data> Array_cheap_objs ) {
	/**
	 * Return cheapest trip 
	 *
	 * @param ArrayList of DDBB_Data 
	 * @return cheapest DDBB_Data
	*/
		DDBB_Data data_obj = Array_cheap_objs.get(0);
		for ( int i=1; i< Array_cheap_objs.size(); i++ ) {
			DDBB_Data new_obj = Array_cheap_objs.get(i);
			if ( data_obj.get_cost() > new_obj.get_cost() ) {
				data_obj = new_obj;
			}
		}
		return data_obj;
	}
	//System.out.println( "2 - Search cheapest conn between A and B" );
	void FindCheapestConnBetweenTwoAirports( DAO obj_DDBB ) {
	/**
	 * Ask user for flight route, and present cheapest flight info, if exists.
	 *
	 * @param DAO object to access DDBB 
	*/
		try {
			String msg = "Introduce origin and destination only: Example NUE-LHR";
			String answer = SendMessageAndReturnAnswer( msg );
			String[] tokens = SplitTextLine( answer );
			//String[] new_tokens = null;
			// Let's search this flight in every combination

			ArrayList<DDBB_Data> Array_cheap_objs = new ArrayList<DDBB_Data>();

			ArrayList<DDBB_Data> Array_Objs_no_scale =  try_flight_search_without_budget(
									obj_DDBB,tokens );
			if ( Array_Objs_no_scale.size() > 0 ) {
				if ( logs ) {	System.out.println( "ENG. No scale flight" );	}
				Array_cheap_objs.add( Array_Objs_no_scale.get(0) );
			}

			String[] new_tokens = { tokens[0], null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_1_scale =  try_flight_search_without_budget(
									obj_DDBB, new_tokens );
			if ( Array_Objs_1_scale.size() > 0 ) {
				if ( logs ) 	{	System.out.println( "ENG. 1 scale flight" );	}
				Array_cheap_objs.add( Array_Objs_1_scale.get(0) );
			}

			String[] new_tokens_2 = { tokens[0], null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_2_scale =  try_flight_search_without_budget(
									obj_DDBB, new_tokens_2 );
			if ( Array_Objs_2_scale.size() > 0 ) {
				if ( logs ) {	System.out.println( "ENG. 2 scales flight" );	}
				Array_cheap_objs.add( Array_Objs_2_scale.get(0) );
			}

			String[] new_tokens_3 = { tokens[0], null, null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_3_scale =  try_flight_search_without_budget(
									obj_DDBB, new_tokens_3 );
			if ( Array_Objs_3_scale.size() > 0 ) {
				if ( logs ) {	System.out.println( "ENG. 3 scales flight" );	}
				Array_cheap_objs.add( Array_Objs_3_scale.get(0) );
			}

			String[] new_tokens_4 = { tokens[0], null, null, null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_4_scale =  try_flight_search_without_budget(
								obj_DDBB, new_tokens_4 );
			if ( Array_Objs_4_scale.size() > 0 ) {
				if ( logs ) {	System.out.println( "ENG. 4 scales flight" );	}
				Array_cheap_objs.add( Array_Objs_4_scale.get(0) );
			}

			if ( Array_cheap_objs.size() > 0 ) {
				DDBB_Data cheapest_trip =  publishCheapestTrip( Array_cheap_objs );
				msg = cheapest_trip.get_whole_trip();	
			} else {
				msg = no_conn_found_error;
			}
			System.out.println( msg );
		} catch (Exception e) {
			System.out.println(unproper_incoming_data);
		}
	}
	void FindFlightsWithMaxThreeScales( DAO obj_DDBB ) {
	/**
	 * Ask user for flight route, and present flights info, if exists.
	 *
	 * @param DAO object to access DDBB 
	*/
		try {
			String msg = "Introduce origin and destination only: Example NUE-LHR";
			String answer = SendMessageAndReturnAnswer( msg );
			String[] tokens = SplitTextLine( answer );
			ArrayList<DDBB_Data> Array_Objs_no_scale =  try_flight_search_without_budget(
									obj_DDBB,
									tokens );
			String[] new_tokens = { tokens[0], null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_1_scale =  try_flight_search_without_budget(
									obj_DDBB,
									new_tokens );
			String[] new_tokens_2 = { tokens[0], null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_2_scale =  try_flight_search_without_budget(
									obj_DDBB,
									new_tokens_2 );
			String[] new_tokens_3 = { tokens[0], null, null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_3_scale =  try_flight_search_without_budget(
									obj_DDBB,
									new_tokens_3 );
			int num_flights = 	Array_Objs_no_scale.size()
						+ Array_Objs_1_scale.size() 
						+ Array_Objs_2_scale.size()
						+ Array_Objs_3_scale.size();
			System.out.println("" + num_flights );
		} catch (Exception e) {
			System.out.println(unproper_incoming_data);
		}

	}
	void FindFlightsWithMinOneScale( DAO obj_DDBB ) {
	/**
	 * Ask user for flight route, and present flights info, if exists.
	 *
	 * @param DAO object to access DDBB 
	*/
		try {
			String msg = "Introduce origin and destination only: Example NUE-LHR";
			String answer = SendMessageAndReturnAnswer( msg );
			String[] tokens = SplitTextLine( answer );

			String[] new_tokens = { tokens[0], null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_1_scale =  try_flight_search_without_budget(
								obj_DDBB, new_tokens );
			String[] new_tokens_2 = { tokens[0], null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_2_scale =  try_flight_search_without_budget(
								obj_DDBB, new_tokens_2 );
			String[] new_tokens_3 = { tokens[0], null, null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_3_scale =  try_flight_search_without_budget(
								obj_DDBB, new_tokens_3 );
			String[] new_tokens_4 = { tokens[0], null, null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_4_scale =  try_flight_search_without_budget(
								obj_DDBB, new_tokens_4 );
			int num_flights = 	Array_Objs_1_scale.size() 
						+ Array_Objs_2_scale.size()
						+ Array_Objs_3_scale.size()
						+ Array_Objs_4_scale.size();
			System.out.println("" + num_flights );
		} catch (Exception e) {
			System.out.println(unproper_incoming_data);
		}
	}
	void FindFlightsWithOneScale( DAO obj_DDBB ) {
	/**
	 * Ask user for flight route, and present flights info, if exists.
	 *
	 * @param DAO object to access DDBB 
	*/
		try {
			String msg = "Introduce origin and destination only: Example NUE-LHR";
			String answer = SendMessageAndReturnAnswer( msg );
			String[] tokens = SplitTextLine( answer );

			String[] new_tokens = { tokens[0], null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_1_scale =  try_flight_search_without_budget(
								obj_DDBB, new_tokens );
			int num_flights = 	Array_Objs_1_scale.size(); 
			System.out.println("" + num_flights );
		} catch (Exception e) {
			System.out.println(unproper_incoming_data);
		}
	}
	void FindFlightsWithTwoScales( DAO obj_DDBB ) {
	/**
	 * Ask user for flight route, and present flights info, if exists.
	 *
	 * @param DAO object to access DDBB 
	*/
		try {
			String msg = "Introduce flight info: Example NUE-LHR";
			String answer = SendMessageAndReturnAnswer( msg );
			String[] tokens = SplitTextLine( answer );

			String[] new_tokens_2 = { tokens[0], null, null, tokens[1] };
			ArrayList<DDBB_Data> Array_Objs_2_scale =  try_flight_search_without_budget(
								obj_DDBB, new_tokens_2 );
			int num_flights = Array_Objs_2_scale.size();
			System.out.println("" + num_flights );
		} catch (Exception e) {
			System.out.println(unproper_incoming_data);
		}
	}
	void FindFlightsWithBudgetLimitation( DAO obj_DDBB ) {
	/**
	 * Ask user for flight route, and present flights info, if exists.
	 *
	 * @param DAO object to access DDBB 
	*/
		try {
			String msg = "Introduce flight info: Example NUE-LHR-1000";
			String answer = SendMessageAndReturnAnswer( msg );
			String[] tokens = SplitTextLine( answer );
			// Let's search this flight in every combination

			ArrayList<DDBB_Data> Array_objs = new ArrayList<DDBB_Data>();

			ArrayList<DDBB_Data> Array_Objs_no_scale = 
				try_flight_search_with_budget( obj_DDBB, tokens );
			if ( Array_Objs_no_scale.size() > 0 ) {
				Array_objs.add( Array_Objs_no_scale.get(0) );
			}

			String[] new_tokens = { tokens[0], null, tokens[1], tokens[2] };
			ArrayList<DDBB_Data> Array_Objs_1_scale = 
				try_flight_search_with_budget( obj_DDBB, new_tokens );
			if ( Array_Objs_1_scale.size() > 0 ) {
				Array_objs.add( Array_Objs_1_scale.get(0) );
			}

			String[] new_tokens_2 = { tokens[0], null, null, tokens[1], tokens[2] };
			ArrayList<DDBB_Data> Array_Objs_2_scale = 
				try_flight_search_with_budget( obj_DDBB, new_tokens_2 );
			if ( Array_Objs_2_scale.size() > 0 ) {
				Array_objs.add( Array_Objs_2_scale.get(0) );
			}

			String[] new_tokens_3 = { tokens[0], null, null, null, tokens[1], tokens[2] };
			ArrayList<DDBB_Data> Array_Objs_3_scale = 
				try_flight_search_with_budget( obj_DDBB, new_tokens_3 );
			if ( Array_Objs_3_scale.size() > 0 ) {
				Array_objs.add( Array_Objs_3_scale.get(0) );
			}

			String[] new_tokens_4 = { tokens[0], null, null, null, null, tokens[1], tokens[2] };
			if ( logs ) {	System.out.println("ENG7.041");	}
			ArrayList<DDBB_Data> Array_Objs_4_scale = 
				try_flight_search_with_budget( obj_DDBB, new_tokens_4 );
			if ( Array_Objs_4_scale.size() > 0 ) {
				Array_objs.add( Array_Objs_4_scale.get(0) );
			}
			if ( Array_objs.size() > 0 ) {
				for ( int i=0; i<Array_objs.size(); i++ ) {
					DDBB_Data data_obj = Array_objs.get(i);
					msg = data_obj.get_whole_trip();	
					System.out.println( msg );
				}
			} else {
				msg = no_conn_found_error;
				System.out.println( msg );
			}	
		} catch (Exception e) {
			System.out.println(unproper_incoming_data);
			System.out.println(e.getMessage());
		}
	}
}



public class FlightsSearch {
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// Read FILE piped to STDIN
		//BufferedReader br = new BufferedReader(
		//	new InputStreamReader(System.in) );
		//String file_line;
		//int files_counter = 0;

		// Read from CLI
		Cli_Interface user_interface = new Cli_Interface();
		String filename = user_interface.get_filename();

		DAO obj_DDBB = new DAO();
		obj_DDBB.create_data_structure();	
		FileToDDBB to_ddbb_obj = new FileToDDBB();

		// Load file 
		to_ddbb_obj.processFile( filename, obj_DDBB );

		//UI
		user_interface.UserNavigation(obj_DDBB);

		// Access to DATi
/*		System.out.println("No Scales");
		obj_DDBB.SetOrigin("AMS");
		obj_DDBB.SetDestination("PDX");
		obj_DDBB.SetCost(null);
		ArrayList<DDBB_Data> Array_Objs_No_Scales = obj_DDBB.retrieve_flight_no_scales();
		System.out.println( "" + Array_Objs_No_Scales.size() + " flights with no scales");

		System.out.println("1 Scale");
		obj_DDBB.SetOrigin("AMS");
		obj_DDBB.SetDestination("AMS");
		obj_DDBB.SetScale1(null);
		obj_DDBB.SetCost(null);
		ArrayList<DDBB_Data> Array_Objs_1_Scale = obj_DDBB.retrieve_flight_1_scale();
		System.out.println( "" + Array_Objs_1_Scale.size() + " flights with 1 scale");

		System.out.println("2 Scales");
		obj_DDBB.SetOrigin("AMS");
		obj_DDBB.SetDestination("PDX");
		obj_DDBB.SetScale1(null);
		obj_DDBB.SetScale2(null);
		obj_DDBB.SetCost(null);
		ArrayList<DDBB_Data> Array_Objs_2_Scales = obj_DDBB.retrieve_flight_2_scales();
		System.out.println( "" + Array_Objs_2_Scales.size() + " flights with 2 scales");

		System.out.println("3 Scales");
		obj_DDBB.SetOrigin("AMS");
		obj_DDBB.SetDestination("ABC");
		obj_DDBB.SetScale1("PDX");
		obj_DDBB.SetScale2(null);
		obj_DDBB.SetScale3(null);
		obj_DDBB.SetCost(null);
		ArrayList<DDBB_Data> Array_Objs_3_Scales = obj_DDBB.retrieve_flight_3_scales();
		System.out.println( "" + Array_Objs_3_Scales.size() + " flights with 3 scales");

		System.out.println("4 Scales");
		obj_DDBB.SetOrigin("AMS");
		obj_DDBB.SetDestination("PDX");
		obj_DDBB.SetScale1("PDX");
		obj_DDBB.SetScale2(null);
		obj_DDBB.SetScale3(null);
		obj_DDBB.SetScale4(null);
		obj_DDBB.SetCost(null);
		ArrayList<DDBB_Data> Array_Objs_4_Scales = obj_DDBB.retrieve_flight_4_scales();
		System.out.println( "" + Array_Objs_4_Scales.size() + " flights with 4 scales");

*/

	}
}



