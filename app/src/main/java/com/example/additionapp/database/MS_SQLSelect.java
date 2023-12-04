package com.example.additionapp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MS_SQLSelect extends SQLException {
    public static ResultSet IsCorrectLoginOP(Connection connection, String email,
                                             String login) throws SQLException {
        String query = "SELECT E.id, C.id as c_id, E.password, " +
                "E.login FROM MYAppData.Company C LEFT JOIN " +
                "MYAppData.Employee E ON C.id = E.company_id " +
                "AND E.login = ? WHERE C.email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, login); preparedStatement.setString(2, email);
        return preparedStatement.executeQuery();
    }

    public static ResultSet GetCompanyByID(Connection connection, int companyId)
            throws SQLException {
        String query = "SELECT name FROM MYAppData.Company WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, companyId);
        return preparedStatement.executeQuery();
    }

    public static ResultSet GetCompanyAll(Connection connection)
            throws SQLException {
        String query = "SELECT id, name FROM MYAppData.Company";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }

    public static ResultSet ReadProducts(Connection connection, int id) throws SQLException {
        String query = "SELECT P.*, P.id AS product_id FROM MYAppData.Company C JOIN " +
                "MYAppData.Product P ON C.id = P.company_id WHERE C.id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id); return preparedStatement.executeQuery();
    }
}
