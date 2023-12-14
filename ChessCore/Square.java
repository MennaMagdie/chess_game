package ChessCore;

public final class Square {
    private final BoardFile file;
    private final BoardRank rank;

    public Square(BoardFile file, BoardRank rank) {
        this.file = file;
        this.rank = rank;
    }

//    public Square(int from1, int from2) {
//    }

    public BoardFile getFile() {
        return file;
    }

    public BoardRank getRank() {
        return rank;
    }
}
