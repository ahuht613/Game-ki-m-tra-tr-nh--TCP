/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Controller.ResultController;
import Controller.UserController;
import Run.ServerRun;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.List;
import Model.UserModel;

/**
 *
 * @author Thu Ha
 */
public class Client implements Runnable {

    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    String loginUser;
    String loginUserId;
    Client cCompetitor;

    public String getLoginUserId() {
        return loginUserId;
    }

    public Client(Socket s) throws IOException {
        this.s = s;
        if (s != null && s.isConnected()) {
            this.dis = new DataInputStream(s.getInputStream());
            this.dos = new DataOutputStream(s.getOutputStream());
        } else {
            throw new IOException("Socket is null or not connected.");
        }
    }

    @Override
    public void run() {
        String received;
        boolean running = true;

        while (!ServerRun.isShutDown) {
            try {
                received = dis.readUTF();
                System.out.println(received);
                String type = received.split(";")[0];

                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "ANSWER":
                        handleGameRequest(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "GET_USERS":
                        onRequestUserList();
                        break;

                    case "EXIT":
                        running = false;
                        break;
                    case "INVITE":
                        handleInvitation(received);
                        break;
                    case "INVITE_RESPONSE":
                        handleInvitation(received);
                        break;
                    case "ACCEPT_INVITE":
                        handleAcceptInvitation(received);
                        break;
                    case "DECLINE_INVITE":
                        handleDeclineInvitation(received);
                        break;
                    case "GET_RANKING":
                        handleRankingRequest();
                        break;
                    case "GAME_RESULT_SAVED":
                        onReceiveGameResultFromClient(received);
                        break;
                    case "EXIT_GAME":
                        handleClientExit(); // Xử lý yêu cầu thoát game
//                    cleanup();
                        running = false;
                        break;

                }
            } catch (IOException ex) {
                break;
            }
        }
        try {
//             closeResources() ;
            this.s.close();
            this.dis.close();
            this.dos.close();
            System.out.println("- Client disconnected: " + s);

            // Check if the client was in a game session
            GameSession session = ServerRun.clientManager.getGameSessionForPlayer(loginUserId);
//            if (session != null) {
//                session.endGame();  // End the game session if the client was involved
//            }

            // remove from clientManager
            ServerRun.clientManager.remove(this);

            ServerRun.clientManager.setUserOffline(this.loginUserId);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isConnected() {
        return s != null && !s.isClosed() && s.isConnected();
    }

//private void cleanup() {
//    try {
//        if (s != null) s.close();
//        if (dis != null) dis.close();
//        if (dos != null) dos.close();
//
//        // Kiểm tra nếu người chơi đang trong một game session
//        GameSession session = ServerRun.clientManager.getGameSessionForPlayer(loginUserId);
//        if (session != null) {
//            session.playerExit(this);
//        }
//
//        // Xóa người chơi khỏi clientManager
//        ServerRun.clientManager.remove(this);
//        ServerRun.clientManager.setUserOffline(this.loginUserId);
//
//    } catch (IOException ex) {
//        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//    }
//}
    public String getLoginUsername() {
        return this.loginUser;
    }

    private void handleClientExit() {
        GameSession session = ServerRun.clientManager.getGameSessionForPlayer(loginUserId);
        if (session != null) {
            session.playerExit(this); // Dừng phiên game và cập nhật điểm
            ServerRun.clientManager.broadcastUserListUpdate(); // Cập nhật danh sách người dùng
        }
    }

    private void onRequestCreateGame(String received) {
        String[] parts = received.split(";");
        if (parts.length < 3) {
            return;
        }

        String player1Id = parts[1];
        String player2Id = parts[2];

        Client player1 = ServerRun.clientManager.getClientById(player1Id);
        Client player2 = ServerRun.clientManager.getClientById(player2Id);

        if (player1 != null && player2 != null) {
            ServerRun.clientManager.createGameSession(player1, player2);
        } else {
            sendData("ERROR;One or both players not found.");
        }
    }

    private void onRequestUserList() {
        UserController userController = new UserController();
        List<UserModel> users = userController.getAllUsers();

        // Tạo chuỗi kết quả để gửi lại cho client
        StringBuilder userList = new StringBuilder("USER_LIST;");
        for (UserModel user : users) {
            if (!String.valueOf(user.getId()).equals(this.loginUserId)) { // Exclude the logged-in user
                userList.append(user.getId()).append(";")
                        .append(user.getUsername()).append(";")
                        .append(user.getFullName()).append(";")
                        .append(user.getTotalScore()).append(";")
                        .append(user.getStatus()).append(";");
            }
        }

        // Send the user list back to the client
        sendData(userList.toString());
    }

    public void handleGameRequest(String request) {
        String[] parts = request.split(";");
        if (parts.length < 2) {
            sendData("ERROR;Invalid request format.");
            return; // Nếu không đủ phần, thoát khỏi phương thức
        }

        String type = parts[0];
        String answer = parts[1];

        if ("ANSWER".equals(type)) {
            // Gửi câu trả lời đến GameSession để xử lý
            GameSession session = ServerRun.clientManager.getGameSessionForPlayer(loginUserId);
            if (session != null) {
                // Gửi câu trả lời và để GameSession xử lý
                session.receiveAnswer(loginUserId, answer);
                System.out.println("Received answer from user: " + loginUserId + " - Answer: " + answer);

                // Gửi phản hồi đến người chơi về việc câu trả lời đã được nhận
                sendData("ANSWER_RECEIVED;Your answer has been received.");
            } else {
                sendData("ERROR;No active game session found.");
            }
        } else {
            sendData("ERROR;Unknown request type.");
        }
    }

    private void onReceiveGameResultFromClient(String received) {
        String[] parts = received.split(";");
        if (parts.length < 4) {
            System.out.println("Error: Invalid game result format received.");
            return; // Prevent further processing if the format is incorrect
        }

        String userId1 = parts[1];  // Local player's ID
        String userId2 = parts[2];  // Opponent's ID
        String result = parts[3];   // Result (WIN, LOSE, DRAW)

        // Create an instance of ResultController and save the result
        ResultController resultController = new ResultController();
        resultController.saveResult(userId1, userId2, result);  // Save the game result in the database

        // Send a confirmation back to the client indicating the result was saved
        sendData("GAME_RESULT_SAVED;" + userId1 + ";" + userId2 + ";" + result);
    }

    public String sendData(String data) {
        try {
            this.dos.writeUTF(data);
            return "success";
        } catch (IOException e) {
            System.err.println("Send data failed!");
            return "failed;" + e.getMessage();
        }
    }

    private void onReceiveLogin(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        String result = new UserController().login(username, password);
        String[] parts = result.split(";");

        if (result.split(";")[0].equals("success")) {
            this.loginUser = username;
            this.loginUserId = parts[1];
            ServerRun.clientManager.setUserOnline(this.loginUserId);
            System.out.println("id da login: " + loginUserId);
        }

        sendData("LOGIN;" + result);

        if (result.split(";")[0].equals("success")) {
            onRequestUserList();
        }
    }

//    private void handleInvitationn(String received) {
//        String[] parts = received.split(";");
//        String invitedUserId = parts[1];
//        Client invitedClient = ServerRun.clientManager.getClientById(invitedUserId);
//        if (invitedClient != null) {
//            invitedClient.sendData("INVITE;" + this.loginUserId + ";" + this.loginUser);
//        }
//    }
    private void handleInvitation(String received) {
        String[] parts = received.split(";");
        String invitedUserId = parts[1];
        Client invitedClient = ServerRun.clientManager.getClientById(invitedUserId);

        if (invitedClient == null) {
            // Send failure response if invited user is offline
            sendData("INVITE_RESPONSE;FAILED;User is offline or unavailable.");
        } else {
            // Check if the invited user is already in a game
            GameSession invitedSession = ServerRun.clientManager.getGameSessionForPlayer(invitedUserId);

            if (invitedSession != null) {
                // Send failure response if invited user is already in a game
                sendData("INVITE_RESPONSE;FAILED;User is already in a game.");
            } else {
                // Send the invitation to the invited player
                invitedClient.sendData("INVITE;" + this.loginUserId + ";" + this.loginUser);

                // Inform inviter of successful invitation
                sendData("INVITE_RESPONSE;SUCCESS");
            }
        }
    }

    private void handleAcceptInvitation(String received) {
        String[] parts = received.split(";");
        String invitingUserId = parts[1];
        Client invitingClient = ServerRun.clientManager.getClientById(invitingUserId);

        if (invitingClient != null) {
            // Start the game session between invitingClient and the current client
            ServerRun.clientManager.createGameSession(invitingClient, this);

            // Retrieve player names and initial scores for the GAME_STARTED message
            String player1Name = invitingClient.loginUser;
            String player2Name = this.loginUser;
            int player1Score = 0; // Initial score
            int player2Score = 0; // Initial score

            // Send GAME_STARTED message with the specified format to both players
            String gameStartMessage1 = "GAME_STARTED;" + player1Name + ";" + player2Name + ";" + player1Score + ";" + player2Score;
            String gameStartMessage2 = "GAME_STARTED;" + player2Name + ";" + player1Name + ";" + player2Score + ";" + player1Score;

            invitingClient.sendData(gameStartMessage1);
            this.sendData(gameStartMessage2);
        }
    }

    private void handleDeclineInvitation(String received) {
        String[] parts = received.split(";");
        String invitingUserId = parts[1];
        Client invitingClient = ServerRun.clientManager.getClientById(invitingUserId);
        if (invitingClient != null) {
            invitingClient.sendData("INVITE_DECLINED;" + this.loginUserId);
        }
    }

    private void handleRankingRequest() {
        UserController userController = new UserController();
        List<UserModel> rankedUsers = userController.getRankedUsers();

        StringBuilder rankingData = new StringBuilder("RANKING;");
        int rank = 1;
        for (UserModel user : rankedUsers) {
            rankingData.append(rank).append(";")
                    .append(user.getUsername()).append(";")
                    .append(user.getTotalScore()).append(";");
            rank++;
        }

        sendData(rankingData.toString());
    }

    private void onReceiveRegister(String received) {
        // get email / password from data
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];
        String fullName = splitted[3];

        // reigster
        String result = new UserController().register(username, password, fullName);

        // send result
        sendData("REGISTER" + ";" + result);
    }
}
