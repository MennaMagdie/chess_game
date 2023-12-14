package ChessGUI;
import ChessCore.PawnPromotion;

import javax.swing.*;

public class Promotion {


    public static PawnPromotion getPromotionType() {
        String[] options = { "Knight", "Bishop", "Rook", "Queen" };
        String chosenOption = "";

        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose promotion:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice >= 0) {
            chosenOption = options[choice];
            System.out.println("You chose: " + chosenOption);
        } else {
            System.out.println("No promotion chosen");
        }

        switch (chosenOption) {
            case "Knight":
                return PawnPromotion.Knight;
            case "Bishop":
                return PawnPromotion.Bishop;
            case "Rook":
                return PawnPromotion.Rook;
            case "Queen":
                return PawnPromotion.Queen;
            default:
                return PawnPromotion.None;
        }
    }
}