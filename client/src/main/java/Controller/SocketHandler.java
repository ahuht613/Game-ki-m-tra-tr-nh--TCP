/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import Run.ClientRun;
import static Run.ClientRun.matchView;
import View.ListView;
import java.awt.Color;

/**
 *
 * @author Thu Ha
 */
public class SocketHandler {

    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    String loginUserId = null; // lưu tài khoản đăng nhập hiện tại
    String loginUsername = null; // lưu tên tài khoản đăng nhập hiện tại
    float score = 0;

    Thread listener = null;

    public String connect(String addr, int port) {
        try {
            // getting ip 
            InetAddress ip = InetAddress.getByName(addr);

            // establish the connection with server port 
            s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 4000);
            System.out.println("Connected to " + ip + ":" + port + ", localport:" + s.getLocalPort());

            // obtaining input and output streams
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connect success
            return "success";

        } catch (IOException e) {
            // connect failed
            return "failed;" + e.getMessage();
        }
    }

    private void listen() {
        boolean running = true;

        while (running) {
            try {
                // receive the data from server
                String received = dis.readUTF();

                System.out.println("RECEIVED: " + received);

                String type = received.split(";")[0];

                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "USER_LIST":
                        onReceiveUserList(received); // Xử lý danh sách người dùng
                        break;
                    case "CORRECT":
                    case "WRONG":
                        onReceiveAnswerResult(received);
                        break;
                    case "WIN":
                    case "LOSE":
                    case "DRAW":
                    case "WIN1":
                        onReceiveGameResult(received);  // Xử lý kết quả thắng/thua
                        break;
                    case "GAME_RESULT_SAVED":
                        onGameResultSaved(received);
                        break;
                    case "INVITE":
                        onReceiveInvitation(received);
                        break;
                    case "INVITE_RESPONSE":
                        onReceiveInvitationResponse(received);
                        break;
                    case "GAME_STARTED":
                        onGameStarted(received);
                        break;
                    case "INVITE_DECLINED":
                        onInviteDeclined(received);
                        break;
                    case "RANKING":
                        onReceiveRanking(received);
                        break;
                    case "SEQUENCE":
                        onReceiveSequence(received);
                        break;
                    case "UPDATE_SCORES":
                        handleScoreUpdate(received);
                        break;
                    case "ROUND_START":
                        onMessageReceived(received);
                        break;

                }
            } catch (IOException ex) {
                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                running = false;
            }
        }

        try {
            // closing resources
            s.close();
            dis.close();
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * *
     * Handle from client
     */
    private void onReceiveSequence(String received) {
        // Tách chuỗi ký tự từ tin nhắn nhận được
        String[] parts = received.split(";");
        String sequence = parts[1];

        // Hiển thị chuỗi ký tự lên giao diện MatchView
        // Hiển thị chuỗi ký tự ngẫu nhiên trong MatchView
        ClientRun.matchView.displayRandomString(sequence); // sequence là chuỗi cần hiển thị

    }

// Client - SocketHandler
    public void onMessageReceived(String message) {
        if (message.startsWith("ROUND_START")) {
            try {
                int roundNumber = Integer.parseInt(message.split(";")[1]);
                matchView.updateRound(roundNumber); // Gọi updateRound để cập nhật số vòng trên giao diện
            } catch (NumberFormatException e) {
                System.out.println("Không thể phân tích số vòng: " + e.getMessage());
            }
        }
        // Xử lý các thông điệp khác (nếu có)
    }

    public void sendAnswer(String answer) {
        if (answer == null || answer.isEmpty()) {
            return;  // Only send an answer if it's not empty
        }

        String data = "ANSWER;" + answer;
        sendData(data);
        System.out.println("Answer sent...");
    }

    // Inside SocketHandler class
// When the game starts or during gameplay, you can call this method to update player names and scores
    private void onGameStarted(String received) {
        String[] parts = received.split(";");

        // Check if the received data has the correct number of parts
        if (parts.length >= 5) {
            // Extract player names and scores from the received data
            String player1Name = parts[1];
            String player2Name = parts[2];
            int player1Score = Integer.parseInt(parts[3]);
            int player2Score = Integer.parseInt(parts[4]);

            // Update the MatchView with player names and scores
            ClientRun.matchView.updatePlayerNames(player1Name, player2Name);
            ClientRun.matchView.updateScores(player1Score, player2Score);

            // Open the match scene
            ClientRun.openScene(ClientRun.SceneName.MATCH);
        } else {
            // Log or handle the error if the data format is invalid
            System.out.println("Error: Received data in an unexpected format: " + received);
        }
    }

    private void handleScoreUpdate(String received) {
        String[] parts = received.split(";");
        if (parts.length >= 3) {
            int myScore = Integer.parseInt(parts[1]);
            int opponentScore = Integer.parseInt(parts[2]);

            // Update the MatchView to reflect the new scores
            // Assuming MatchView is accessible or you have a reference to it
            matchView.updateScores(myScore, opponentScore); // Ensure matchView is correctly referenced
        }
    }

    private void onReceiveAnswerResult(String received) {
        if (received.equals("CORRECT")) {
            ClientRun.matchView.showResultMessage("Correct!", Color.GREEN);
        } else if (received.equals("WRONG")) {
            ClientRun.matchView.showResultMessage("Wrong!", Color.RED);
        }
    }

    private void onReceiveGameResult(String received) {
        String[] parts = received.split(";");
        if (parts.length > 1) {
            String type = parts[0];
            String message = parts[1];

            switch (type) {
                case "WIN":
                    JOptionPane.showMessageDialog(ClientRun.matchView, "Congratulations! You won!", "Result", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "LOSE":
                    JOptionPane.showMessageDialog(ClientRun.matchView, "Sorry! You lost!", "Result", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "DRAW":
                    JOptionPane.showMessageDialog(ClientRun.matchView, "The match ended in a draw!", "Result", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }

            ClientRun.openScene(ClientRun.SceneName.LIST);
            ClientRun.closeScene(ClientRun.SceneName.MATCH);
        }
//         else {
//            System.out.println("Error: Invalid game result received.");
//        }
    }

    public void sendExitRequest() {
        sendData("EXIT_GAME");
    }

    public void onReceiveExit(String received) {
        String[] parts = received.split(";");
        String resultMessage = parts[1];
        JOptionPane.showMessageDialog(ClientRun.matchView, resultMessage, "Result", JOptionPane.INFORMATION_MESSAGE);
        ClientRun.closeScene(ClientRun.SceneName.MATCH);
        ClientRun.openScene(ClientRun.SceneName.LIST);
    }

    private void onGameResultSaved(String received) {
        String[] parts = received.split(";");
        if (parts.length == 4) {
            String userId1 = parts[1];  // Local player's ID
            String userId2 = parts[2];  // Opponent's ID
            String result = parts[3];   // Result (WIN, LOSE, DRAW)

            // Optionally, show a message or perform any other actions upon receiving the confirmation
            JOptionPane.showMessageDialog(ClientRun.matchView, "The game result has been saved!", "Notification", JOptionPane.INFORMATION_MESSAGE);

            // You can also switch scenes or perform any other logic after saving the game result
            ClientRun.openScene(ClientRun.SceneName.LIST);  // For example, open the match history or list view
            ClientRun.closeScene(ClientRun.SceneName.MATCH);  // Close the current match view
        } else {
            System.out.println("Error: Invalid confirmation message for game result save.");
        }
    }

    public void invitePlayer(String player2Id) {
        // Gửi yêu cầu mời người chơi đến server
        String data = "CREATE_GAME;" + loginUserId + ";" + player2Id;
        sendData(data);
    }

    public void login(String email, String password) {
        // prepare data
        String data = "LOGIN" + ";" + email + ";" + password;
        // send data
        sendData(data);
    }

    public void register(String username, String password, String fullName) {
        // prepare data
        String data = "REGISTER" + ";" + username + ";" + password + ";" + fullName;
        // send data
        sendData(data);
    }

    // Thêm phương thức xử lý danh sách người dùng
    private void onReceiveUserList(String received) {
        String[] splitted = received.split(";");
        if (splitted.length > 1) {
            List<Object[]> userList = new ArrayList<>();

            for (int i = 1; i < splitted.length; i += 5) {
                Object[] user = new Object[5];
                user[0] = splitted[i];     // ID
                user[1] = splitted[i + 1]; // Username
                user[2] = splitted[i + 2]; // Full Name
                user[3] = splitted[i + 3]; // Score
                user[4] = splitted[i + 4]; // Status
                userList.add(user);
            }

            Object[][] userData = userList.toArray(Object[][]::new);
            ClientRun.listView.updateUserList(userData);
        }
    }

    // Thêm vào SocketHandler
    public void requestUserList() {
        // Gửi yêu cầu đến máy chủ
        String data = "GET_USERS"; // Một loại yêu cầu để nhận danh sách người dùng
        sendData(data);
    }

    // Lấy username cho trang List View
    public String getLoginUsername() {
        return this.loginUsername;
    }

    /**
     * *
     * Handle send data to server
     */
    public void sendData(String data) {
        try {
            dos.writeUTF(data);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

//    public String getLoginUsername() {
//        return loginUsername;
//    }
//    
    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    /**
     * *
     * Handle receive data from server
     */
    private void onReceiveLogin(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // Hiển thị lỗi
            JOptionPane.showMessageDialog(null, "Login failed: " + splitted[2], "Error", JOptionPane.ERROR_MESSAGE);
        } else if (status.equals("success")) {
            // Lưu user login
            this.loginUserId = splitted[2];
            this.loginUsername = splitted[3];
            this.score = Float.parseFloat(splitted[4]);
            System.out.println("Id user login: " + this.loginUserId);

            // Mở trang ListView và đóng trang đăng nhập
            ClientRun.openScene(ClientRun.SceneName.LIST);
            // Đóng cửa sổ hiện tại (có thể là trang đăng nhập)
            ClientRun.closeScene(ClientRun.SceneName.LOGIN);// Giả sử ClientRun.loginView là JFrame của trang đăng nhập
        }
    }

    private void onReceiveRegister(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.registerView, failedMsg, "Error", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            JOptionPane.showMessageDialog(ClientRun.registerView, "Register account successfully! Please login!");
            // chuyển scene
            ClientRun.closeScene(ClientRun.SceneName.REGISTER);
            ClientRun.openScene(ClientRun.SceneName.LOGIN);
        }
    }

    public void sendInvitation(String invitedUserId) {
        // Send invitation request to the server
        sendData("INVITE;" + invitedUserId);
    }

    private void onReceiveInvitation(String received) {
        String[] parts = received.split(";");
        String invitingUserId = parts[1];
        String invitingUsername = parts[2];

        int choice = JOptionPane.showConfirmDialog(
                null,
                invitingUsername + " invites you to play. Do you accept?",
                "Game Invitation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            sendData("ACCEPT_INVITE;" + invitingUserId);
        } else {
            sendData("DECLINE_INVITE;" + invitingUserId);
        }
    }

    public void onReceiveInvitationResponse(String received) {
        String[] parts = received.split(";");
        String status = parts[1];

        if ("FAILED".equals(status)) {
            String reason = parts[2];
            JOptionPane.showMessageDialog(
                    null,
                    "Invitation failed: " + reason,
                    "Invitation Error",
                    JOptionPane.WARNING_MESSAGE
            );
//        } else if ("SUCCESS".equals(status)) {
//            JOptionPane.showMessageDialog(
//                    null,
//                    "Invitation sent successfully.",
//                    "Invitation Sent",
//                    JOptionPane.INFORMATION_MESSAGE
//            );
        }
    }

    private void onInviteDeclined(String received) {
        String[] parts = received.split(";");
        String declinedUserId = parts[1];
        JOptionPane.showMessageDialog(null, "Player " + declinedUserId + " declined your invitation.");
    }

    public void requestRanking() {
        sendData("GET_RANKING");
    }

    private void onReceiveRanking(String received) {
        String[] parts = received.split(";");
        if (parts.length > 1) {
            List<Object[]> rankingList = new ArrayList<>();

            for (int i = 1; i < parts.length; i += 3) {
                Object[] user = new Object[3];
                user[0] = Integer.parseInt(parts[i]);     // Rank
                user[1] = parts[i + 1];                   // Username
                user[2] = Float.parseFloat(parts[i + 2]); // Score
                rankingList.add(user);
            }

            Object[][] rankingData = rankingList.toArray(new Object[0][]);
            showRankingDialog(rankingData);
        }
    }

    private void showRankingDialog(Object[][] rankingData) {
        JTable rankingTable = new JTable(rankingData, new String[]{"Rank", "Username", "Score"});
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(null, scrollPane, "Player Rankings", JOptionPane.INFORMATION_MESSAGE);
    }
}
