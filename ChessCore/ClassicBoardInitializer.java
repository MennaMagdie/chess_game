package ChessCore;

import ChessCore.Pieces.*;

public final class ClassicBoardInitializer implements BoardInitializer {
    private static final BoardInitializer instance = new ClassicBoardInitializer();

    private ClassicBoardInitializer() {
    }

    public static BoardInitializer getInstance() {
        return instance;
    }

    @Override
    public Piece[][] initialize() {
        return new Piece[][]{
                {PieceFactory.initPiece("Rook", Player.WHITE),PieceFactory.initPiece("Knight",Player.WHITE),
                        PieceFactory.initPiece("Bishop",Player.WHITE), PieceFactory.initPiece("Queen",Player.WHITE),
                        PieceFactory.initPiece("King",Player.WHITE), PieceFactory.initPiece("Bishop",Player.WHITE),
                        PieceFactory.initPiece("Knight",Player.WHITE), PieceFactory.initPiece("Rook",Player.WHITE)},

                {PieceFactory.initPiece("Pawn",Player.WHITE), PieceFactory.initPiece("Pawn",Player.WHITE),
                        PieceFactory.initPiece("Pawn",Player.WHITE), PieceFactory.initPiece("Pawn",Player.WHITE),
                        PieceFactory.initPiece("Pawn",Player.WHITE), PieceFactory.initPiece("Pawn",Player.WHITE),
                        PieceFactory.initPiece("Pawn",Player.WHITE), PieceFactory.initPiece("Pawn",Player.WHITE)},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {PieceFactory.initPiece("Pawn",Player.BLACK), PieceFactory.initPiece("Pawn",Player.BLACK),
                        PieceFactory.initPiece("Pawn",Player.BLACK), PieceFactory.initPiece("Pawn",Player.BLACK),
                        PieceFactory.initPiece("Pawn",Player.BLACK), PieceFactory.initPiece("Pawn",Player.BLACK),
                        PieceFactory.initPiece("Pawn",Player.BLACK), PieceFactory.initPiece("Pawn",Player.BLACK)},
                {PieceFactory.initPiece("Rook",Player.BLACK), PieceFactory.initPiece("Knight",Player.BLACK),
                        PieceFactory.initPiece("Bishop",Player.BLACK), PieceFactory.initPiece("Queen",Player.BLACK),
                        PieceFactory.initPiece("King",Player.BLACK), PieceFactory.initPiece("Bishop",Player.BLACK),
                        PieceFactory.initPiece("Knight",Player.BLACK), PieceFactory.initPiece("Rook",Player.BLACK)}
        };
    }
}
