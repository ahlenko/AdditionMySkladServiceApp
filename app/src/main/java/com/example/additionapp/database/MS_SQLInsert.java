package com.example.additionapp.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class MS_SQLInsert extends Exception {
    private static int executeInsertWithTransaction(Connection connection, String query,
                                                    Object ... parameters) throws SQLException {
        connection. setAutoCommit(false); try {
            PreparedStatement stmt = connection.prepareStatement(query, Statement. RETURN_GENERATED_KEYS);
            for (int i = 0; i < parameters. length; i++) {
                if (parameters[i] instanceof String)
                    stmt.setString( i + 1, (String) parameters[i]);
                else if (parameters[i] instanceof Integer)
                    stmt.setInt( i + 1, (Integer) parameters[i]);
                else if (parameters[i] instanceof Date)
                    stmt.setDate( i + 1, (Date) parameters[i]);
            } stmt. executeUpdate ();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) { connection. commit();
                return resultSet.getInt(1); }
        } catch (SQLException e) { connection.rollback(); throw e;
        } finally { connection.setAutoCommit(true);} return -1;
    }

    public static int AddOrder(Connection connection, int company, String adresser, String email,
                               String ph_number, int sum_count, int code) throws SQLException {
        String query = "INSERT INTO MYAppData.[Orders] ([company_id], [adresser], " +
                "[email], [phnumber], [sum_count], [code], [date]) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Date date = null; if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate curDate = LocalDate.now(); date = java.sql.Date.valueOf(String.valueOf(curDate));}
        return executeInsertWithTransaction(connection, query, company, adresser, email, ph_number, sum_count, code, date);
    }

    public static void AddOrderDetails(Connection connection, int company, int product, int order,
                                       int count) throws SQLException {
        String query = "INSERT INTO MYAppData.[OrdersDetails] ([company_id], " +
                "[product_id], [orders_id], [count]) VALUES (?, ?, ?, ?)";
        executeInsertWithTransaction(connection, query, company, product, order, count);
    }

    public static int AddAddition(Connection connection, int company, int employee, int count) throws SQLException {
        String query = "INSERT INTO MYAppData.[Addition] ([company_id], [performer_id], [date], [sum_count]) VALUES (?, ?, ?, ?)";
        Date date = null; if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate curDate = LocalDate.now(); date = java.sql.Date.valueOf(String.valueOf(curDate));}
        return executeInsertWithTransaction(connection, query, company, employee, date, count);
    }

    public static void AddAdditionDetails(Connection connection, int company, int product, int addition,
                                          int count) throws SQLException {
        String query = "INSERT INTO MYAppData.[AdditionValues] ([company_id], " +
                "[product_id], [addition_id], [count]) VALUES (?, ?, ?, ?)";
        executeInsertWithTransaction(connection, query, company, product, addition, count);
    }
}
