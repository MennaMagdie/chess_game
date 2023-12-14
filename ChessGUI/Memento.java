package ChessGUI;

import ChessCore.*;

public record Memento(ClassicChessGame state) {
}

//public class Memento {
//    private final ClassicChessGame state;
//
//    public Memento(ClassicChessGame state) {
//        this.state = state;
//    }
//
//    public ClassicChessGame getState() {
//        return state;
//    }
//}