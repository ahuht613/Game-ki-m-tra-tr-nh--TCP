/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Controller.UserController;
import Service.Client;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Thu Ha
 */
public class ClientManager {
    ArrayList<Client> clients;
    private Map<String, GameSession> gameSessions = new HashMap<>();

    public ClientManager() {
        clients = new ArrayList<>();
    }
    
    public Client getClientById(String userId) {
        for (Client c : clients) {
            if (c.getLoginUserId().equals(userId)) {
                return c;
            }
        }
        return null;
    }

    public boolean add(Client c) {
        if (!clients.contains(c)) {
            clients.add(c);
            return true;
        }
        return false;
    }

    public boolean remove(Client c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }
    
    public void createGameSession(Client player1, Client player2) {
        GameSession session = new GameSession(player1, player2);
        gameSessions.put(player1.getLoginUserId() + "-" + player2.getLoginUserId(), session);
        updateUserStatus(player1.getLoginUserId(), "Ingame");
        updateUserStatus(player2.getLoginUserId(), "Ingame");
        session.startRound();
    }
    
    public GameSession getGameSessionForPlayer(String loginUserId) {
        for (GameSession session : gameSessions.values()) {
            if (session.getPlayer1().getLoginUserId().equals(loginUserId) ||
                session.getPlayer2().getLoginUserId().equals(loginUserId)) {
                return session;
            }
        }
        return null;
    }

    public GameSession getGameSession(Client player1, Client player2) {
        return gameSessions.get(player1.getLoginUserId() + "-" + player2.getLoginUserId());
    }
    
    public void removeGameSession(GameSession session) {
        String sessionKey = null;
        for (Map.Entry<String, GameSession> entry : gameSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                sessionKey = entry.getKey();
                break;
            }
        }

        if (sessionKey != null) {
            gameSessions.remove(sessionKey);
            System.out.println("Removed game session: " + sessionKey);
        }
    }
    
     public int getSize() {
        return clients.size();
    }


    public void updateUserStatus(String userId, String status) {
        UserController userController = new UserController();
        userController.updateUserStatus(userId, status);
    }

    public void setUserOnline(String userId) {
        updateUserStatus(userId, "Online");
    }

    public void setUserOffline(String userId) {
        updateUserStatus(userId, "Offline");
    }
    
    public void updateScore(String userId, float score) {
        UserController userController = new UserController();
        userController.updateScore(userId, score);

        // Tìm client dựa trên userId và gửi cập nhật điểm số
        
    }
    
    public void broadcastUserListUpdate() {
    // Lấy danh sách người dùng mới nhất từ UserController
    UserController userController = new UserController();
    Object[][] users = userController.getAllUsersForBroadcast();

    // Tạo chuỗi dữ liệu để gửi cho các client
    StringBuilder userListBuilder = new StringBuilder("USER_LIST_UPDATE;");
    for (Object[] user : users) {
        userListBuilder.append(user[0]).append(";") // ID
                       .append(user[1]).append(";") // Username
                       .append(user[2]).append(";") // Full Name
                       .append(user[3]).append(";") // Score
                       .append(user[4]).append(";"); // Status
    }

    String userListData = userListBuilder.toString();

    // Gửi danh sách cập nhật tới tất cả các client đang kết nối
    for (Client client : clients) {
        if (client.isConnected()) {
            client.sendData(userListData);
        }
    }
    System.out.println("Broadcasted updated user list to all clients.");
}

}

