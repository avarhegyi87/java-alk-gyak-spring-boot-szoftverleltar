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

    public List<Install> getInstallStats () {
        try {
            Connect();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT s.nev AS software_name, s.kategoria AS software_type, " +
                            "SUM(CASE WHEN g.tipus = 'notebook' THEN 1 ELSE 0 END) AS notebook_count, " +
                            "SUM(CASE WHEN g.tipus = 'asztali' THEN 1 ELSE 0 END) AS desktop_count " +
                            "FROM telepites AS t " +
                            "INNER JOIN szoftver AS s ON s.id = t.szoftverid " +
                            "INNER JOIN gep AS g ON g.id = t.gepid " +
                            "GROUP BY s.nev " +
                            "ORDER BY s.nev"
            );
            List<Install> installList = new ArrayList<>();
            while (resultSet.next()) {
                Software software = new Software();
                Comp computer = new Comp();
                Install install = new Install();

                software.setNev(resultSet.getString("software_name"));
                software.setKategoria(resultSet.getString("software_type"));

                install.setSoftware(software);
                install.setComputer(computer);
                install.setInstallCount_Notebook(resultSet.getInt("notebook_count"));
                install.setInstallCount_Desktop(resultSet.getInt("desktop_count"));

                installList.add(install);
            }
            connection.close();
            statement.close();
            resultSet.close();
            return installList;
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
