package ChessCore;

import ChessCore.Pieces.*;

public abstract class PieceFactory {

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
