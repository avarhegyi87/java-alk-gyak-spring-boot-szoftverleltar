package com.example.softwareinventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SzoftverleltarDbManager {
    Statement statement;
    Connection connection;

    void Connect() throws SQLException {
        final String URL = "jdbc:mysql://localhost/szoftverleltar?user=root&characterEncoding=utf8";
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        connection = DriverManager.getConnection(URL);
        statement = connection.createStatement();
    }

    public Integer getUserIdFromUserName(String userName) {
        try {
            Connect();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id FROM felhasznalok WHERE email = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY
            );
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = 0;
            try {
                resultSet.last();
                count = resultSet.getRow();
                resultSet.beforeFirst();
            } catch (Exception exception) {
                return null;
            }
            if (count == 1) {
                resultSet.next();
                return resultSet.getInt("id");
            } else {
                System.out.println("Túl sok felhasználó ugyanazzal a névvel: " + userName + " - " + count);
                return null;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Message> getAllMessages() {
        try {
            Connect();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT ido, felhasznalo_nev, uzenet_tipus, uzenet " +
                    "FROM uzenetek ORDER BY ido DESC");
            List<Message> msgResult = new ArrayList<>();
            while (resultSet.next()) {
                Message msgTemp = new Message();
                msgTemp.setTime(resultSet.getTime("ido"));
                msgTemp.setUserName(resultSet.getString("felhasznalo_nev"));
                msgTemp.setMessageType(resultSet.getString("uzenet_tipus"));
                msgTemp.setMessageText(resultSet.getString("uzenet"));
                msgResult.add(msgTemp);
            }
            connection.close();
            statement.close();
            resultSet.close();
            return msgResult;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Message> getInstallStats () {
        try {
            Connect();
            //TODO: correct the SQL query to a valid one, using all 3 tables
            ResultSet resultSet = statement.executeQuery(
                    "SELECT DISTINCT g.hely FROM telepites AS t" +
                            "INNER JOIN gep AS g WHERE t.gepid = g.id" +
                            "INNER JOIN szoftver AS s WHERE t.szoftverid = s.id"
            );
            List<Message> msgResult = new ArrayList<>();
            //TODO: add the query results to the result set
            while (resultSet.next()) {}
            connection.close();
            statement.close();
            resultSet.close();
            return msgResult;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean insertMessage(Message message) {
        try {
            Connect();

            int rows;
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO uzenetek(felhasznalo_id, felhasznalo_nev, uzenet_tipus, uzenet)" +
                            "VALUES (?,?,?,?)");
            
            if (message.getUserId() != null) {

                preparedStatement.setInt(1, message.getUserId());
                preparedStatement.setString(2, message.getUserName());
                preparedStatement.setString(3, message.getMessageType());
                preparedStatement.setString(4, message.getMessageText());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
                preparedStatement.setString(2, "Vendég");
                preparedStatement.setString(3, message.getMessageType());
                preparedStatement.setString(4, message.getMessageText());
            }
            rows = preparedStatement.executeUpdate();
            connection.close();
            statement.close();
            return rows == 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


}
