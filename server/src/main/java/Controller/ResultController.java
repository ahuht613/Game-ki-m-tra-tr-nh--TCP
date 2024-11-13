/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import Connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
/**
 *
 * @author Thu Ha
 */
public class ResultController {
    private final Connection con;

    public ResultController() {
        this.con = DatabaseConnection.getInstance().getConnection();
    }

  public void saveResult(String userName1, String userName2, String result) {
        String sql = "INSERT INTO results (timestamp, userName1, userName2, UserWin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            // Lấy thời gian hiện tại
            LocalDateTime now = LocalDateTime.now();
            stmt.setTimestamp(1, Timestamp.valueOf(now)); // Lưu thời gian vào CSDL
            stmt.setString(2, userName1);
            stmt.setString(3, userName2);
            stmt.setString(4, result);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Game result saved successfully.");
            } else {
                System.out.println("Failed to save game result.");
            }
        } catch (SQLException e) {
            System.err.println("Error saving game result: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
