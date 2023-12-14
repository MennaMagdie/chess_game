package ChessCore;

import ChessGUI.Memento;

import java.util.Stack;

public class CareTaker {
    private Stack<Memento> gameStateStack;

    public CareTaker() {
        gameStateStack = new Stack<>();
    }

    public void saveState(Memento game) {
        gameStateStack.push(game);
    }

    public Memento undoState() {
        if (!gameStateStack.isEmpty()) {
            System.out.println("Undoing a move");
            return gameStateStack.pop();
        }
        return null;
    }

    public boolean emptyState(){
        return gameStateStack.empty();
    }

    public int sizeState(){
        return gameStateStack.size();
    }
}
