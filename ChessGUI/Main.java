package ChessGUI;

import ChessCore.*;
import ChessCore.Observer;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame implements MouseListener, MouseMotionListener, Observer {
    JLayeredPane layeredPane;
    JPanel chessBoard;
    JLabel chessPiece;
    private ClassicChessGame game;

    //----------For normal movements----------//
    private Square from;
    private Square to;
    private int fromX;
    private int fromY;

    //---------Highlighting Movements---------//
    private List<Square> highlightedSquares = new ArrayList<>();

    //--------For Flipping--------//
    private boolean whiteTurn = true;

    //--------For Castling--------//
//    private boolean castlingPerformed = false;

    //-------Promotion-------//
    private PawnPromotion promotion = PawnPromotion.None;

    //------Undo + MEMENTO PATTERN------//
    private JButton undoButton;
    //    private Stack<ClassicChessGame> gameStateStack;
    private CareTaker gameStateStack;

    //-----OBSERVER PATTERN-----//
//    private ObservableGame observableGame;

    //-----New Colors----//
    public final Color BlACK = new Color(161, 98, 20);
    public final Color WHITE = new Color(236, 222, 191);
    public final Color GREEN = new Color(18, 227, 18, 255);
    public final Color RED = new Color(153, 0, 0);

    //--New Sizes--//

    public final int pieceWidth = 100;
    public final int pieceHeight = 100;



    public Main(){

        JOptionPane.showMessageDialog(null, "2-PLAYER CHESS GAME. LET'S START!","", JOptionPane.INFORMATION_MESSAGE);

        Dimension boardSize = new Dimension(700, 700);
        this.setTitle("CHESS GAME");
        game = new ClassicChessGame();

        game.addObserver(this);

//        gameStateStack = new Stack<>();
        gameStateStack = new CareTaker();

        layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);
        layeredPane.setPreferredSize(boardSize);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);

        //Add chessboard to the Layered Pane
        chessBoard = new JPanel();
        layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
        chessBoard.setLayout( new GridLayout(8, 8) );
        chessBoard.setPreferredSize( boardSize );
        chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);


        for (int i = 0; i < 64; i++) {
            JPanel square = new JPanel( new BorderLayout() );
            chessBoard.add( square );

            int row = (i / 8) % 2;
            if (row == 0)
                square.setBackground( i % 2 == 0 ? this.BlACK : this.WHITE );
            else
                square.setBackground( i % 2 == 0 ? this.WHITE : this.BlACK );
        }

        initBlackPieces();
        initWhitePieces();


        // Adding Undo Button
        undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undoMove();
            }
        });

        JPanel headerPanel = new JPanel();
        JLabel titleLabel = new JLabel("Chess");
        headerPanel.add(titleLabel);
        headerPanel.add(undoButton);

        getContentPane().add(headerPanel, BorderLayout.NORTH);
    }


    public void mousePressed(MouseEvent e){
        //betsheel elpiece men makanha
        //TO-DO: ne3raf e7na ekhtarna ay chessPiece then neshouf valid moves beta3etha w nelawenha belakhdar

        chessPiece = null;
        Component c =  chessBoard.findComponentAt(e.getX(), e.getY());

        if (c instanceof JPanel)
            return;

        chessPiece = (JLabel) c;

        fromX = e.getX();
        fromY = e.getY();
        int from1 = getXCoordinate(e.getX());
        int from2 = getYCoordinate(e.getY());

        System.out.println(chessPiece.getName());
        System.out.println("X: " + e.getX() + ", Y: " + e.getY());
        System.out.println("X: " + from1 + ", Y: " + from2);
        chessPiece.setLocation(e.getX(), e.getY());
        chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
        layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER);

        this.from = new Square(convertToFile(from1),convertToRank(from2));

        //Highlight possible moves
        highlightedSquares = game.getAllValidMovesFromSquare(this.from);
        highlightSquares(highlightedSquares);
    }

    //Move the chess piece around
    public void mouseDragged(MouseEvent me) { //bet7arrak elpiece ma3 7araket elmouse
        if (chessPiece == null) return;
        chessPiece.setLocation(me.getX(), me.getY());
    }

    //Drop the chess piece back onto the chess board + mafroud captures pieces
    public void mouseReleased(MouseEvent e) {
        //ne3raf ehna hanseeb fe ay piece delwa2ty + neshouf law elmakan elwa2feen 3ando lono akhdar, yeb2a tamam release, law mesh akhdar DON'T release.
        //neshouf esm elcaptured piece lol

        highlightedSquares = game.getAllValidMovesFromSquare(this.from);
        resetHighlighting(highlightedSquares);

        if(chessPiece == null) return;

        int to1 = getXCoordinate(e.getX());
        int to2 = getYCoordinate(e.getY());

        this.to = new Square(convertToFile(to1),convertToRank(to2));

        System.out.println("X: " + e.getX() + ", Y: " + e.getY());
        System.out.println("X: " + to1 + ", Y: " + to2);

        if(game.getPieceAtSquare(this.to) != null)
            System.out.println("Captured: " + game.getPieceAtSquare(this.to).toString());

//        if a white pawn is moving to 8th rank or a black pawn is moving to 1st rank
        if (game.getPieceAtSquare(this.from).toString().equals("Pawn")) {
            if (this.to.getRank().ordinal() == 7 && whiteTurn) {
                this.promotion =  Promotion.getPromotionType();
            } else if (this.to.getRank().ordinal() == 0 && !whiteTurn) {
                this.promotion = Promotion.getPromotionType();
            }
        }


        if(game.makeMove(new Move(this.from,this.to,promotion))) { //add argument lel pawn promotion here: DONE
            System.out.println("VALID MOVE!");

            chessPiece.setVisible(false);
            Component c = chessBoard.findComponentAt(e.getX(), e.getY());


            if(game.getWhoseTurn() == Player.BLACK) {
                if(game.isCanEnPassant()) {
                    enPassantMove(this.to);
                }
            }


            if (game.isCanWhiteCastleQueenSide() && to.getFile() == BoardFile.C) {
                castlingMove(new Move(this.from, this.to));
            } else if (game.isCanWhiteCastleKingSide() && to.getFile() == BoardFile.G) {
                castlingMove(new Move(this.from, this.to));
            } else if (game.isCanBlackCastleQueenSide() && to.getFile() == BoardFile.C) {
                castlingMove(new Move(this.from, this.to));
            } else if (game.isCanBlackCastleKingSide() && to.getFile() == BoardFile.G) {
                castlingMove(new Move(this.from, this.to));
            }

            JPanel parent;
            if (c instanceof JLabel) {
                parent = (JPanel) c.getParent();
                parent.remove(0);
            } else {
                parent = (JPanel) c;
            }
            parent.add(chessPiece);

            chessPiece.setVisible(true);

//            Square kingSquare =game.getWhiteKingSquare();

            this.highlightKingInCheck();
            this.flipBoard();

            if(game.getWhoseTurn() == Player.WHITE) {
                if(game.isCanEnPassant()) {
                    enPassantMove(this.to);
                }
            }

            whiteTurn = !whiteTurn;
//            this.checkEndGame();
            update(game.getGameStatus());
            this.resetKingHighlight();


        } else {
            System.out.println("INVALID MOVE!");

            chessPiece.setVisible(false);
            Component c = chessBoard.findComponentAt(fromX, fromY);

            JPanel parent;
            if (c instanceof JLabel) {
                parent = (JPanel) c.getParent();
                parent.remove(0);
            } else {
                parent = (JPanel) c;
            }
            parent.add(chessPiece);

            chessPiece.setVisible(true);
//            castlingPerformed = false;
        }

//        ClassicChessGame thisGame = new ClassicChessGame();
//        thisGame = this.game;

        Memento memento = new Memento(this.game);

//        gameStateStack.push(thisGame);
        gameStateStack.saveState(new Memento(this.game));

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e){

    }
    public void mouseExited(MouseEvent e) {

    }

    public static void main(String[] args) {
        JFrame frame = new Main();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE );
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }

    public void initBlackPieces() {

        //INITIALIZING PIECES ON BOARD

        //Adding Black Pawns
        for(int i=8 ; i<16 ; i++) {
            JLabel piece = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackPawn)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
            piece.setName("BlackPawn");
            JPanel panel = (JPanel)chessBoard.getComponent(i); //returns ith square
            panel.add(piece);
        }

        //Adding Black Rook
        JLabel piece = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackRook)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel1 = (JPanel)chessBoard.getComponent(0);
        piece.setName("BlackRook");
        panel1.add(piece);
        JLabel piece2 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackRook)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel2 = (JPanel)chessBoard.getComponent(7);
        panel2.add(piece2);
        piece2.setName("BlackRook");

        //Adding Black Knights
        JLabel piece3 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackKnight)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel3 = (JPanel)chessBoard.getComponent(1);
        piece3.setName("BlackKnight");
        panel3.add(piece3);
        JLabel piece4 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackKnight)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel4 = (JPanel)chessBoard.getComponent(6);
        panel4.add(piece4);
        piece4.setName("BlackKnight");

        //Adding Black Bishop
        JLabel piece5 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackBishop)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel5 = (JPanel)chessBoard.getComponent(2);
        panel5.add(piece5);
        piece5.setName("BlackBishop");
        JLabel piece6 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackBishop)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel6 = (JPanel)chessBoard.getComponent(5);
        panel6.add(piece6);
        piece6.setName("BlackBishop");

        //Adding Queen and King
        JLabel piece7 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackKing)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel7 = (JPanel)chessBoard.getComponent(4);
        panel7.add(piece7);
        piece5.setName("BlackKing");
        JLabel piece8 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.BlackQueen)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel8 = (JPanel)chessBoard.getComponent(3);
        panel8.add(piece8);
        piece5.setName("BlackQueen");

    }

    public void initWhitePieces() {
        //Adding White Pawns
        for(int i=48 ; i<56 ; i++) {
            JLabel piecex = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhitePawn)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
            JPanel panelx = (JPanel)chessBoard.getComponent(i);
            panelx.add(piecex);
            piecex.setName("WhitePawn");
        }

        //Adding White Rook
        JLabel piece = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhiteRook)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel1 = (JPanel)chessBoard.getComponent(56);
        piece.setName("WhiteRook");
        panel1.add(piece);
        JLabel piece2 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhiteRook)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel2 = (JPanel)chessBoard.getComponent(63);
        panel2.add(piece2);
        piece2.setName("WhiteRook");

        //Adding White Knights
        JLabel piece3 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhiteKnight)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel3 = (JPanel)chessBoard.getComponent(57);
        piece3.setName("WhiteKnight");
        panel3.add(piece3);
        JLabel piece4 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhiteKnight)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel4 = (JPanel)chessBoard.getComponent(62);
        panel4.add(piece4);
        piece4.setName("WhiteKnight");

        //Adding White Bishop
        JLabel piece5 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhiteBishop)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel5 = (JPanel)chessBoard.getComponent(58);
        panel5.add(piece5);
        piece5.setName("WhiteBishop");
        JLabel piece6 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhiteBishop)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel6 = (JPanel)chessBoard.getComponent(61);
        panel6.add(piece6);
        piece6.setName("WhiteBishop");

        //Adding Queen and King
        JLabel piece7 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhiteKing)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel7 = (JPanel)chessBoard.getComponent(60);
        panel7.add(piece7);
        piece5.setName("WhiteKing");
        JLabel piece8 = new JLabel(new ImageIcon((new ImageIcon(PieceImages.WhiteQueen)).getImage().getScaledInstance(pieceWidth, pieceHeight, Image.SCALE_SMOOTH)));
        JPanel panel8 = (JPanel)chessBoard.getComponent(59);
        panel8.add(piece8);
        piece5.setName("WhiteQueen");
    }

    //Mapping X coordinate from (0-750) to (0-7)
    public int getXCoordinate(int clickedCoordinate) {
        if(this.whiteTurn)
            return (7*clickedCoordinate)/620;
        else
            return Math.abs((7*clickedCoordinate/620) - 7);
    }

    //Mapping Y coordinate from (750-0) to (0-7)
    public int getYCoordinate(int clickedCoordinate) {
        if(this.whiteTurn)
            return Math.abs((7*clickedCoordinate/620) - 7);
        else
            return (7*clickedCoordinate)/620;
    }

    //Converts row to rank
    public BoardRank convertToRank(int x) {
        return switch (x) {
            case 0 -> BoardRank.FIRST;
            case 1 -> BoardRank.SECOND;
            case 2 -> BoardRank.THIRD;
            case 3 -> BoardRank.FORTH;
            case 4 -> BoardRank.FIFTH;
            case 5 -> BoardRank.SIXTH;
            case 6 -> BoardRank.SEVENTH;
            case 7 -> BoardRank.EIGHTH;
            default -> throw new RuntimeException("You're out of CHESS Scope");
        };
    }

    //Converts column to file
    public BoardFile convertToFile(int x) {
        return switch (x) {
            case 0 -> BoardFile.A;
            case 1 -> BoardFile.B;
            case 2 -> BoardFile.C;
            case 3 -> BoardFile.D;
            case 4 -> BoardFile.E;
            case 5 -> BoardFile.F;
            case 6 -> BoardFile.G;
            case 7 -> BoardFile.H;
            default -> throw new RuntimeException("You're out of CHESS Scope");
        };
    }

    private void flipBoard() {
        Component[] components = chessBoard.getComponents();
        chessBoard.removeAll();
        for (int i = components.length - 1; i >= 0; i--) {
            chessBoard.add(components[i]);
        }
        chessBoard.revalidate();
        chessBoard.repaint();
    }

//    private void checkEndGame() {
//        if (game.getGameStatus() == GameStatus.INSUFFICIENT_MATERIAL) {
//            JOptionPane.showMessageDialog(null, "Insufficient Material!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
//            System.out.println("Insufficient Material");
//            System.exit(0);
//        } else if (game.getGameStatus() == GameStatus.STALEMATE) {
//            System.out.println("StaleMate");
//            JOptionPane.showMessageDialog(null, "Stale Mate!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
//            System.exit(0);
//        } else if (game.getGameStatus() == GameStatus.WHITE_WON) {
//            System.out.println("White Won!");
//            JOptionPane.showMessageDialog(null, "WHITE WON!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
//            System.exit(0);
//        } else if (game.getGameStatus() == GameStatus.BLACK_WON) {
//            System.out.println("Black Won!");
//            JOptionPane.showMessageDialog(null, "BLACK WON!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
//            System.exit(0);
//        }
//    }

    public void highlightSquares(List<Square> squares) {
        for (Square square : squares) {
            int fileIndex, rankIndex;
            if (whiteTurn) {
                fileIndex = square.getFile().ordinal();
                rankIndex = 7 - square.getRank().ordinal();
            } else {
                fileIndex = 7 - square.getFile().ordinal();
                rankIndex = square.getRank().ordinal();
            }
//            System.out.println("File Index: " + fileIndex + ", Rank Index: " + rankIndex);

            JPanel panel = (JPanel) chessBoard.getComponent(fileIndex + rankIndex * 8);
            panel.setBackground(this.GREEN);
        }
    }

    public void resetHighlighting(List<Square> squares) {
        for (Square square : squares) {
            int fileIndex, rankIndex;
            if (whiteTurn) {
                fileIndex = square.getFile().ordinal();
                rankIndex = 7 - square.getRank().ordinal();
            } else {
                fileIndex = 7 - square.getFile().ordinal();
                rankIndex = square.getRank().ordinal();
            }

            JPanel panel = (JPanel) chessBoard.getComponent(fileIndex + rankIndex * 8);
            int row = (rankIndex) % 2;
            panel.setBackground(row == 0 ? (fileIndex % 2 == 0 ? this.BlACK :this.WHITE) :
                    (fileIndex % 2 == 0 ? this.WHITE : this.BlACK));
        }
    }

    private void highlightKingInCheck() {
        if(game.getGameStatus() == GameStatus.WHITE_UNDER_CHECK) {
            System.out.println("White King Under Check");
            Square square = game.getWhiteKingSquare();
            int fileIndex = square.getFile().ordinal();
            int rankIndex = square.getRank().ordinal() ;
            JPanel panel = (JPanel) chessBoard.getComponent(rankIndex * 8 + fileIndex - 1);
            panel.setBackground(this.RED);
        } else if (game.getGameStatus() == GameStatus.BLACK_UNDER_CHECK) {
            System.out.println("Black King Under Check");
            Square square = game.getBlackKingSquare();
            int fileIndex = square.getFile().ordinal();
            int rankIndex = square.getRank().ordinal() ;
            JPanel panel = (JPanel) chessBoard.getComponent(rankIndex * 8 + fileIndex);
            panel.setBackground(this.RED);
        }
    }

    public void resetKingHighlight(){
        if (game.getGameStatus() != GameStatus.WHITE_UNDER_CHECK) {

            Square square = game.getWhiteKingSquare();
            int fileIndex = square.getFile().ordinal();
            int rankIndex = square.getRank().ordinal();
            JPanel panel = (JPanel) chessBoard.getComponent(rankIndex * 8 + fileIndex);
            int row = (rankIndex) % 2;
            panel.setBackground(row == 0 ? (fileIndex % 2 == 0 ? this.BlACK : this.WHITE) :
                    (fileIndex % 2 == 0 ? this.WHITE : this.BlACK));
        }
        if (game.getGameStatus() != GameStatus.BLACK_UNDER_CHECK) {
//            System.out.println("Black King Under Check");
            Square square = game.getBlackKingSquare();
            int fileIndex = square.getFile().ordinal();
            int rankIndex = square.getRank().ordinal();
            JPanel panel = (JPanel) chessBoard.getComponent(rankIndex * 8 + fileIndex );
            int row = (rankIndex) % 2;
            panel.setBackground(row == 0 ? (fileIndex % 2 == 0 ? this.BlACK : this.WHITE) :
                    (fileIndex % 2 == 0 ? this.WHITE :this.BlACK));
        }
    }


    public void enPassantMove(Square capturedPawnSquare) {
        System.out.println("enPassant in frontend");

        int fileIndex, rankIndex;

        fileIndex = capturedPawnSquare.getFile().ordinal();
        rankIndex = capturedPawnSquare.getRank().ordinal();

        JPanel capturedPawnPanel = (JPanel) chessBoard.getComponent(fileIndex + rankIndex * 8);
//        capturedPawnPanel.setBackground(Color.BLUE);
        if(whiteTurn)
        {
            JPanel pawn = (JPanel) chessBoard.getComponent((fileIndex + rankIndex* 8) - 16);
//            pawn.setBackground(Color.BLUE);
            pawn.removeAll();  //DONE
            pawn.repaint();
        }
        else {
            JPanel pawn = (JPanel) chessBoard.getComponent((fileIndex + rankIndex * 8) + 16);
//            pawn.setBackground(Color.PINK);
            pawn.removeAll();
            pawn.repaint();
        }
    }


    public void castlingMove(Move move){
//        System.out.println("TORPEDOO??????");
        Square fromSquare = move.getFromSquare();
        Square toSquare = move.getToSquare();

//            if (whiteTurn) {
        if (fromSquare.getFile() == BoardFile.E && toSquare.getFile() == BoardFile.G) {
            // King-side castling for both white and black
            Square rookFromSquare = new Square(BoardFile.H, fromSquare.getRank());
            Square rookToSquare = new Square(BoardFile.F, toSquare.getRank());
            moveRookForCastling(rookFromSquare, rookToSquare);
        } else if (fromSquare.getFile() == BoardFile.E && toSquare.getFile() == BoardFile.C) {
            // Queen-side castling for both white and black
            Square rookFromSquare = new Square(BoardFile.A, fromSquare.getRank());
            Square rookToSquare = new Square(BoardFile.D, toSquare.getRank());
            moveRookForCastling(rookFromSquare, rookToSquare);
        }

    }
    public void moveRookForCastling(Square fromSquare, Square toSquare) {
        int fromFileIndex, fromRankIndex, toFileIndex, toRankIndex;
        if (whiteTurn) {
            fromFileIndex = fromSquare.getFile().ordinal();
            fromRankIndex = 7 - fromSquare.getRank().ordinal();
            toFileIndex = toSquare.getFile().ordinal();
            toRankIndex = 7 - toSquare.getRank().ordinal();

        } else {
            fromFileIndex = 7 - fromSquare.getFile().ordinal();
            fromRankIndex = fromSquare.getRank().ordinal();
            toFileIndex = 7 - toSquare.getFile().ordinal();
            toRankIndex = toSquare.getRank().ordinal();
        }

        JPanel fromPanel = (JPanel) chessBoard.getComponent(fromFileIndex + fromRankIndex * 8);
        JPanel toPanel = (JPanel) chessBoard.getComponent(toFileIndex + toRankIndex * 8);

        // Move the rook from the "from" square to the "to" square
        Component rookComponent = fromPanel.getComponent(0);
        fromPanel.removeAll();
        toPanel.add(rookComponent);

        fromPanel.repaint();
        toPanel.repaint();
    }

    private void undoMove() {
//        if (!gameStateStack.isEmpty()) {
//            System.out.println("Undoing a move");
//            ClassicChessGame previousState = gameStateStack.pop();
//            gameStateStack.undoState();
        this.game = gameStateStack.undoState().state();
        if(this.game != null) {
            undoChessPiece(from,to);
            this.flipBoard();
            this.revalidate();
            this.repaint();
        }
    }

    private void undoChessPiece(Square from, Square to) {
        int fromFileIndex, fromRankIndex, toFileIndex, toRankIndex;

        if (whiteTurn) {
            System.out.println("hello");
            fromFileIndex = from.getFile().ordinal();
            fromRankIndex = 7 - from.getRank().ordinal();
            toFileIndex = to.getFile().ordinal();
            toRankIndex = 7 - to.getRank().ordinal();
        } else {
            System.out.println("hello 2");
            fromFileIndex = 7 - from.getFile().ordinal();
            fromRankIndex = from.getRank().ordinal();
            toFileIndex = 7 - to.getFile().ordinal();
            toRankIndex = to.getRank().ordinal() ;
        }
        System.out.println("fromFileIndex: " + fromFileIndex + ", fromRankIndex: " + fromRankIndex);
        System.out.println("toFileIndex: " + toFileIndex + ", toRankIndex: " + toRankIndex);


        JPanel fromPanel = (JPanel) chessBoard.getComponent(fromFileIndex + fromRankIndex * 8);
        JPanel toPanel = (JPanel) chessBoard.getComponent(toFileIndex + toRankIndex * 8);

        // Move the piece from the "to" square back to the "from" square
        if (toPanel.getComponentCount() > 0) {

            Component piece = toPanel.getComponent(0);
            toPanel.removeAll();
            fromPanel.add(piece);

            fromPanel.repaint();
            toPanel.repaint();
        } else {
            System.out.println("No piece");
        }
    }

    @Override
    public void update(GameStatus gameStatus) {
        if (gameStatus == GameStatus.INSUFFICIENT_MATERIAL) {
            JOptionPane.showMessageDialog(null, "Insufficient Material!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Insufficient Material");
            System.exit(0);
        } else if (gameStatus == GameStatus.STALEMATE) {
            System.out.println("StaleMate");
            JOptionPane.showMessageDialog(null, "Stale Mate!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (gameStatus == GameStatus.WHITE_WON) {
            System.out.println("White Won!");
            JOptionPane.showMessageDialog(null, "WHITE WON!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (gameStatus == GameStatus.BLACK_WON) {
            System.out.println("Black Won!");
            JOptionPane.showMessageDialog(null, "BLACK WON!", "GAME ENDED", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }


    }


}