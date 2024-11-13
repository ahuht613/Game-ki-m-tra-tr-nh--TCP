/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Controller.SequenceController;
import Controller.ResultController;
import Controller.UserController;
import java.util.List;
import Run.ServerRun;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Thu Ha
 */
public class GameSession {

    private Client player1;
    private Client player2;
    private int player1Score;
    private int player2Score;
    private int roundCount;
    private final int MAX_ROUNDS = 3; // Maximum rounds before a draw
    private boolean isFinished;

    private boolean finalRoundEvaluated = false;
    private boolean isRoundInProgress = false;
    private boolean isRoundCompleted = false;


    private String currentSequence;
    private long player1SubmitTime;
    private long player2SubmitTime;
    private boolean player1Correct;
    private boolean player2Correct;
    private boolean player1Answered = false; // Track if player1 has answered
    private boolean player2Answered = false; // Track if player2 has answered

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public GameSession(Client player1, Client player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.player1Score = 0;
        this.player2Score = 0;
        this.roundCount = 1;
        this.isFinished = false;

//        startRound();
    }

    public Client getPlayer1() {
        return player1;
    }

    public Client getPlayer2() {
        return player2;
    }

public void startRound() {
    if (isFinished || roundCount > MAX_ROUNDS || isRoundInProgress) {
        return;
    }

    // Đặt lại trạng thái
    isRoundInProgress = true;
    isRoundCompleted = false;
    finalRoundEvaluated = false;

    player1Correct = false;
    player2Correct = false;
    player1SubmitTime = 0;
    player2SubmitTime = 0;
    player1Answered = false;
    player2Answered = false;
    currentSequence = generateRandomSequence();

    String roundMessage = "ROUND_START;" + roundCount;
    player1.sendData(roundMessage);
    player2.sendData(roundMessage);

    String sequenceMessage = "SEQUENCE;" + currentSequence;
    player1.sendData(sequenceMessage);
    player2.sendData(sequenceMessage);

    scheduler.schedule(() -> {
        if (!isFinished) {
            hideSequence();
        }
    }, 5, TimeUnit.SECONDS);
}


    private void hideSequence() {
        if (isFinished) {
            return;
        }

        player1.sendData("HIDE_SEQUENCE");
        player2.sendData("HIDE_SEQUENCE");

        player1.sendData("ENTER_SEQUENCE");
        player2.sendData("ENTER_SEQUENCE");

        // Lên lịch để đánh giá vòng sau 30 giây
        scheduler.schedule(() -> {
            if (!isFinished) {
                evaluateRound();
            }
        }, 30, TimeUnit.SECONDS);
    }

private void endGameWithFinalEvaluation() {
    if (isFinished || finalRoundEvaluated) return;

    finalRoundEvaluated = true;

    if (!player1Answered && !player2Answered) {
        declareDraw();
    } else if (!player1Correct && !player2Correct) {
        declareDraw();
    } else {
        determineRoundWinner();
    }

    player1.sendData("UPDATE_SCORES;" + player1Score + ";" + player2Score);
    player2.sendData("UPDATE_SCORES;" + player2Score + ";" + player1Score);

    scheduler.schedule(this::endGameBasedOnScores, 2, TimeUnit.SECONDS);
}




    private String generateRandomSequence() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sequence = new StringBuilder(10);
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            sequence.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sequence.toString();
    }

    public void receiveAnswer(String playerId, String answer) {
        long submitTime = System.currentTimeMillis();
        boolean isCorrect = answer.equalsIgnoreCase(currentSequence);

        if (playerId.equals(player1.getLoginUserId())) {
            player1SubmitTime = submitTime;
            player1Correct = isCorrect;
            player1Answered = true;
            player1.sendData(isCorrect ? "CORRECT" : "WRONG");
        } else if (playerId.equals(player2.getLoginUserId())) {
            player2SubmitTime = submitTime;
            player2Correct = isCorrect;
            player2Answered = true;
            player2.sendData(isCorrect ? "CORRECT" : "WRONG");
        }
    }

private void evaluateRound() {
    if (isFinished || finalRoundEvaluated || !isRoundInProgress || isRoundCompleted) return;

    isRoundInProgress = false;
    isRoundCompleted = true;

    if (!player1Answered && !player2Answered) {
        declareDraw();
    } else if (!player1Correct && !player2Correct) {
        declareDraw();
    } else {
        determineRoundWinner();
    }

    if (roundCount >= MAX_ROUNDS) {
        endGameWithFinalEvaluation();
    } else {
        roundCount++;
        scheduler.schedule(this::startRound, 1, TimeUnit.SECONDS);
    }
}





 private void determineRoundWinner() {
    if (isFinished || finalRoundEvaluated) return;

    String result = "DRAW";
    boolean scoreUpdated = false;

    if (player1Correct && player2Correct) {
        result = (player1SubmitTime < player2SubmitTime) ? "USER1_WIN" : "USER2_WIN";
        if (result.equals("USER1_WIN")) {
            player1Score++;
            scoreUpdated = true;
        } else {
            player2Score++;
            scoreUpdated = true;
        }
    } else if (player1Correct) {
        result = "USER1_WIN";
        player1Score++;
        scoreUpdated = true;
    } else if (player2Correct) {
        result = "USER2_WIN";
        player2Score++;
        scoreUpdated = true;
    }

    // Chỉ cập nhật điểm nếu chưa phải vòng cuối
    if (scoreUpdated && roundCount < MAX_ROUNDS) {
        player1.sendData("UPDATE_SCORES;" + player1Score + ";" + player2Score);
        player2.sendData("UPDATE_SCORES;" + player2Score + ";" + player1Score);
    }

    // Gửi kết quả cho người chơi
    player1.sendData(result);
    player2.sendData(result);
}

private void declareDraw() {
    if (isFinished || isRoundInProgress || isRoundCompleted) return;

    player1.sendData("DRAW");
    player2.sendData("DRAW");

    isRoundCompleted = true;

    if (roundCount >= MAX_ROUNDS) {
        endGameWithFinalEvaluation();
    } else {
        isRoundInProgress = false;
        roundCount++;
        scheduler.schedule(this::startRound, 1, TimeUnit.SECONDS);
    }
}



    private void endGameBasedOnScores() {
        if (isFinished) {
            return;
        }

        isFinished = true;
        stopGameSession();

        String result;
        ResultController resultController = new ResultController();
        String player1Name = player1.getLoginUsername();
    String player2Name = player2.getLoginUsername();

        if (player1Score > player2Score) {
        result = "USER1_WIN";
        player1.sendData("WIN;You won the game!");
        player2.sendData("LOSE;You lost the game.");
        resultController.saveResult(player1Name, player2Name, "USER1");
    } else if (player2Score > player1Score) {
        result = "USER2_WIN";
        player2.sendData("WIN;You won the game!");
        player1.sendData("LOSE;You lost the game.");
        resultController.saveResult(player1Name, player2Name, "USER2");
    } else {
        result = "DRAW";
        player1.sendData("DRAW;The game ended in a draw.");
        player2.sendData("DRAW;The game ended in a draw.");
        resultController.saveResult(player1Name, player2Name, "DRAW");
    }
        // Cập nhật điểm vào cơ sở dữ liệu
        ServerRun.clientManager.updateScore(player1.getLoginUserId(), player1Score);
        ServerRun.clientManager.updateScore(player2.getLoginUserId(), player2Score);
        ServerRun.clientManager.updateUserStatus(player1.getLoginUserId(), "Online");
        ServerRun.clientManager.updateUserStatus(player2.getLoginUserId(), "Online");
        ServerRun.clientManager.removeGameSession(this);
    }

    public void playerExit(Client exitingPlayer) {
        String resultMessage;
        ResultController resultController = new ResultController();

        String player1Name = player1.getLoginUsername();
        String player2Name = player2.getLoginUsername();
        // Kiểm tra xem người chơi thoát là Player 1 hay Player 2 và gửi thông báo kết quả cho người chơi còn lại
        if (exitingPlayer.equals(player1)) {
            if (player2Score > player1Score) {
                resultMessage = "WIN;Your opponent has exited the game. You won!";
                player2.sendData(resultMessage);
                resultController.saveResult(player1Name, player2Name, "USER2");
            } else if (player2Score < player1Score) {
                resultMessage = "LOSE;Your opponent has exited the game. You lost!";
                player2.sendData(resultMessage);
                resultController.saveResult(player1Name, player2Name, "USER1");
            } else {
                resultMessage = "DRAW;Your opponent has exited the game. The game ended in a draw!";
                player2.sendData(resultMessage);
                resultController.saveResult(player1Name, player2Name, "DRAW");
            }
            player1.sendData("EXIT;You have exited the game.");
        } else if (exitingPlayer.equals(player2)) {
            if (player1Score > player2Score) {
                resultMessage = "WIN;Your opponent has exited the game. You won!";
                player1.sendData(resultMessage);
                resultController.saveResult(player1Name, player2Name, "USER1");
            } else if (player1Score < player2Score) {
                resultMessage = "LOSE;Your opponent has exited the game. You lost!";
                player1.sendData(resultMessage);
                resultController.saveResult(player1Name, player2Name, "USER2");
            } else {
                resultMessage = "DRAW;Your opponent has exited the game. The game ended in a draw!";
                player1.sendData(resultMessage);
                resultController.saveResult(player1Name, player2Name, "DRAW");
            }
            player2.sendData("EXIT;You have exited the game.");
        }
        ServerRun.clientManager.broadcastUserListUpdate();
        // Dừng tất cả các luồng và dịch vụ liên quan
        stopGameSession();

        // Cập nhật trạng thái người chơi và xóa phiên trò chơi
        ServerRun.clientManager.updateScore(player1.getLoginUserId(), player1Score);
        ServerRun.clientManager.updateScore(player2.getLoginUserId(), player2Score);
        ServerRun.clientManager.updateUserStatus(player1.getLoginUserId(), "Online");
        ServerRun.clientManager.updateUserStatus(player2.getLoginUserId(), "Online");
        ServerRun.clientManager.removeGameSession(this);
        isFinished = true;
    }

    private void stopGameSession() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        System.out.println("Game session has been stopped.");
    }

    public void handleClientExit(String playerId) {
        if (player1.getLoginUserId().equals(playerId)) {
            playerExit(player1);
        } else if (player2.getLoginUserId().equals(playerId)) {
            playerExit(player2);
        }
    }

    public void endGameAsDraw() {
        player1.sendData("DRAW");
        player2.sendData("DRAW");
        player1.sendData("DRAW;We are draw");
        player2.sendData("DRAW;We are draw");
        ServerRun.clientManager.updateScore(player1.getLoginUserId(), player1Score);
        ServerRun.clientManager.updateScore(player2.getLoginUserId(), player2Score);
        ServerRun.clientManager.updateUserStatus(player1.getLoginUserId(), "Online");
        ServerRun.clientManager.updateUserStatus(player2.getLoginUserId(), "Online");
        ServerRun.clientManager.removeGameSession(this);

        isFinished = true;
    }

    public void endGame(String result) {
        if (isFinished) {
            return;
        }

        if (result.equals("USER1_WIN")) {
            player1.sendData("WIN;You won the game!");
            player2.sendData("LOSE;You lost the game.");
            ServerRun.clientManager.updateScore(player1.getLoginUserId(), player1Score);
            ServerRun.clientManager.updateScore(player2.getLoginUserId(), player2Score);

        } else if (result.equals("USER2_WIN")) {
            player2.sendData("WIN;You won the game!");
            player1.sendData("LOSE;You lost the game.");
            ServerRun.clientManager.updateScore(player1.getLoginUserId(), player1Score);
            ServerRun.clientManager.updateScore(player2.getLoginUserId(), player2Score);
        } else {
            endGameAsDraw();
        }

        ServerRun.clientManager.removeGameSession(this);

        stopGameSession();
        isFinished = true;
        ServerRun.clientManager.updateUserStatus(player1.getLoginUserId(), "Online");
        ServerRun.clientManager.updateUserStatus(player2.getLoginUserId(), "Online");
    }
}
