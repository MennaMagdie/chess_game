package ChessGUI;

import ChessCore.Pieces.*;
import ChessCore.Player;

import javax.swing.*;

public class PieceFactory {
    private JPanel chessBoard;

    public PieceFactory(JPanel chessBoard) {
        this.chessBoard = chessBoard;
    }

    public static Piece initPiece(String pieceName, Player p) {

        return switch (pieceName) {

            case "Rook" -> new Rook(p);
            case "Knight" -> new Knight(p);
            case "King" -> new King(p);
            case "Queen" -> new Queen(p);
            case "Bishop" -> new Bishop(p);
            case "Pawn" -> new Pawn(p);
            default -> throw new IllegalArgumentException("Invalid Piece Type");
        };

    }

}
