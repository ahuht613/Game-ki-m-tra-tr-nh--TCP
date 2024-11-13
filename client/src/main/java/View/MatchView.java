/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;
import Run.ClientRun;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.Timer;
/**
 *
 * @author Thu Ha
 */
public class MatchView extends javax.swing.JFrame {
   private String correctAnswer;
    private Timer displayTimer;
    private Controller.SocketHandler socketHandler; // Declare socketHandler
    private int player1Score = 0;  // Variable to store Player 1's score
    private int player2Score = 0;  // Variable to store Player 2's score
private Timer countdownTimer;
private int countdownTime = 30; // 30 seconds countdown


   
    public MatchView(Controller.SocketHandler socketHandler) { // Accept socketHandler as a parameter
        initComponents();
        setLocationRelativeTo(null);
        this.socketHandler = socketHandler; // Initialize SocketHandler
        
        setTitle("Memory Matching Game");

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                sendAnswer(); // Link button action
            }
        });
//        exitButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                exit(); // Link button action
//            }
//        });
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
}
public void displayRandomString(String randomString) {
    jLabel1.setText(randomString);
    jLabel1.setVisible(true);

    // Ẩn countdownProgressBar và resultLabel khi dãy ký tự xuất hiện
    if (countdownProgressBar != null) {
        countdownProgressBar.setVisible(false);
    }
    resultLabel.setVisible(false);

    // Dừng bộ đếm thời gian nếu đang chạy
    if (displayTimer != null && displayTimer.isRunning()) {
        displayTimer.stop();
    }

    // Hiển thị dãy ký tự trong 5 giây, sau đó bắt đầu đếm ngược
    displayTimer = new Timer(5000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            jLabel1.setVisible(false); // Ẩn dãy ký tự
            displayTimer.stop();

            // Đặt lại thanh đếm ngược và bắt đầu đếm ngược sau khi dãy ký tự bị ẩn
            resetCountdown();
            initializeCountdownProgressBar();
        }
    });
    displayTimer.start();
}


    
public void resetCountdown() {
    countdownTime = 30; // Đặt lại thời gian đếm ngược
    if (countdownProgressBar != null) {
        countdownProgressBar.setValue(countdownTime);
        countdownProgressBar.setForeground(Color.GREEN);
        countdownProgressBar.setVisible(false); // Ẩn thanh đếm ngược
    }
}


 private void initializeCountdownProgressBar() {
    if (countdownProgressBar == null) {
        countdownProgressBar = new JProgressBar(0, countdownTime);
        countdownProgressBar.setStringPainted(true);
        countdownProgressBar.setForeground(Color.GREEN);

        getContentPane().add(countdownProgressBar);
        countdownProgressBar.setBounds(100, 300, 300, 20); // Đặt vị trí và kích thước
    }
    
    countdownProgressBar.setMaximum(countdownTime);
    countdownProgressBar.setValue(countdownTime);

    countdownProgressBar.setVisible(true); // Hiển thị thanh đếm ngược
    countdownProgressBar.setValue(countdownTime);

    if (countdownTimer != null && countdownTimer.isRunning()) {
        countdownTimer.stop();
    }

    countdownTimer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            countdownTime--;
            countdownProgressBar.setValue(countdownTime);

            if (countdownTime <= 10) {
                countdownProgressBar.setForeground(Color.RED);
            }

            if (countdownTime <= 0) {
                countdownTimer.stop();
                countdownProgressBar.setVisible(false);
            }
        }
    });
    countdownTimer.start();
}


 private void sendAnswer() {
    String answer = jTextField1.getText();
    if (answer.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter your answer.");
        return;
    }

    if (socketHandler != null) {
        socketHandler.sendAnswer(answer);

        // Giả sử socketHandler sẽ trả về kết quả "CORRECT" hoặc "WRONG"
        if (answer.equals(correctAnswer)) {
            showResultMessage("Correct!", Color.GREEN);
        } else {
            showResultMessage("Wrong!", Color.RED);
        }
    } else {
        JOptionPane.showMessageDialog(this, "SocketHandler is not initialized.");
    }

    jTextField1.setText(""); // Xóa nội dung trường nhập
}

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    // Method to update the scores
    public void updateScores(int myScore, int opponentScore) {
    jLabel3.setText(String.valueOf(myScore));        // Update your score display
    jLabel5.setText(String.valueOf(opponentScore));  // Update opponent's score display
}

    // Method to update player names
    public void updatePlayerNames(String player1Name, String player2Name) {
        jLabel2.setText(player1Name);
        jLabel4.setText(player2Name);
    }
    
    // Trong lớp MatchView, tạo một phương thức để cập nhật kết quả
public void showResultMessage(String message, Color color) {
    resultLabel.setText(message);
    resultLabel.setForeground(color);
    resultLabel.setVisible(true);

    Timer timer = new Timer(2000, e -> resultLabel.setVisible(false));
    timer.setRepeats(false);
    timer.start();
}


public void updateRound(int roundNumber) {
    roundLabel.setText("Round: " + roundNumber);
}

//public void exit(){
//    socketHandler.onReceiveGameResult(correctAnswer);
//}
//public void handleGameEnd(String result) {
//    if (result.equals("WIN") || result.equals("LOSE") || result.equals("DRAW")) {
//        JOptionPane.showMessageDialog(this, "Game Over: " + result);
//        this.dispose(); // Close the MatchView window
//    }
//}





   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        resultLabel = new javax.swing.JLabel();
        exitButton = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        countdownProgressBar = new javax.swing.JProgressBar();
        roundLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("jLabel1");

        jButton1.setText("Submit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("jLabel2");

        jLabel3.setText("jLabel3");

        jLabel4.setText("jLabel4");

        jLabel5.setText("jLabel5");

        resultLabel.setText("resultLabel");
        resultLabel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                resultLabelComponentHidden(evt);
            }
        });

        exitButton.setText("Thoát");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 92, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 88, Short.MAX_VALUE)
        );

        roundLabel.setText("Round: 1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1)
                            .addComponent(countdownProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addComponent(roundLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(129, 129, 129)))
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(448, 448, 448)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(16, 16, 16))
            .addGroup(layout.createSequentialGroup()
                .addGap(209, 209, 209)
                .addComponent(resultLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(268, 268, 268))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(roundLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                        .addGap(29, 29, 29)
                        .addComponent(countdownProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1)
                        .addGap(39, 39, 39)))
                .addComponent(jTextField1)
                .addGap(18, 18, 18)
                .addComponent(resultLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(44, 44, 44))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit the game?", 
                                                "Confirm Exit", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        if (socketHandler != null) {
            socketHandler.sendData("EXIT_GAME");
        }
        this.dispose(); // Đóng cửa sổ hiện tại
        ClientRun.openScene(ClientRun.SceneName.LIST);
    }
    }//GEN-LAST:event_exitButtonActionPerformed

    private void resultLabelComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_resultLabelComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_resultLabelComponentHidden

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MatchView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MatchView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MatchView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MatchView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        Controller.SocketHandler socketHandler = new Controller.SocketHandler(); // Initialize your SocketHandler

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MatchView(socketHandler).setVisible(true); // Pass the socketHandler to the view
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar countdownProgressBar;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JLabel roundLabel;
    // End of variables declaration//GEN-END:variables

    
}
