package com.vtls.opensource.fedora.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * A generic script for debugging a Fedora RDBMS.
 */
class GenericFedoraDatabaseQuery
{
   /**
    * Performs the whole process to debug a Fedora RDBMS.
    * @param args array of the arguments
    * @throws SQLException
    */
   public static void main(String args[]) throws SQLException
   {
      Connection connection = null;

      // Load the Oracle JDBC driver.
      if(args[0].toLowerCase().equals("oracle"))
      {
         DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
         connection = DriverManager.getConnection("jdbc:oracle:thin:@" + args[1], args[2], args[3]);
      }
      else if(args[0].toLowerCase().equals("mysql"))
      {
         DriverManager.registerDriver(new com.mysql.jdbc.Driver());
         connection = DriverManager.getConnection("jdbc:mysql://" + args[1] + "?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true", args[2], args[3]);
      }
      else
      {
         DriverManager.registerDriver(new com.mckoi.JDBCDriver());
         connection = DriverManager.getConnection("jdbc:mckoi://" + args[1] + "/", args[2], args[3]);
      }

      // Create a statement
      Statement statement = connection.createStatement();

      // Execute the query and process the result set.
      ResultSet result_set = statement.executeQuery(args[4]);
      debugResultSet(result_set);

      // Close the ResultSet, Statement and Connection.
      result_set.close();
      statement.close();
      connection.close();
   }

   /**
    * Display all columns and rows in the result set.
    * @param _result_set An active {@link ResultSet} instance.
    * @throws SQLException.
    */
   static public void debugResultSet(ResultSet _result_set) throws SQLException
   {
      // Get the ResultSetMetaData for the column headings.
      ResultSetMetaData metadata = _result_set.getMetaData();

      // Display column headings
      for(int i = 1; i <= metadata.getColumnCount(); i++)
      {
         if(i > 1)
            System.out.print(",");
         System.out.print(metadata.getColumnLabel(i));
      }

      System.out.println("\n-------------------------------------");

      while(_result_set.next())
      {
         // Loop through each column, getting the
         // column data and displaying
         for(int i = 1; i <= metadata.getColumnCount(); i++)
         {
            if(i > 1)
               System.out.print(",");
            System.out.print(_result_set.getString(i));
         }
         System.out.println();
      }
   }
}

