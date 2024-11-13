/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
import java.util.Random;
/**
 *
 * @author Thu Ha
 */
public class SequenceController {
//    private static final int SEQUENCE_LENGTH = 10;
//    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//
//    public String generateRandomSequence() {
//        Random random = new Random();
//        StringBuilder sequence = new StringBuilder(SEQUENCE_LENGTH);
//        
//        for (int i = 0; i < SEQUENCE_LENGTH; i++) {
//            sequence.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
//        }
//        
//        return sequence.toString();
//    }

    public boolean checkAnswer(String correctSequence, String playerAnswer) {
        return correctSequence.equalsIgnoreCase(playerAnswer.trim());
    }
}
