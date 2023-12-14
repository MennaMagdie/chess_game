package ChessCore;

//import ChessGUI.Memento;

public final class ClassicChessGame extends ChessGame {

    public ClassicChessGame() {
        super(ClassicBoardInitializer.getInstance());
    }

    public ClassicChessGame(ChessGame original) {
        super(original);
    }

    @Override
    protected boolean isValidMoveCore(Move move) {
        return !Utilities.willOwnKingBeAttacked(this.getWhoseTurn(), move, this.getBoard());
    }



}
