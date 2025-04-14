package View;

import Model.Board;
import Model.Clock;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow {
    // Constants
    private static final String WINDOW_TITLE = "Chess";
    private static final String ICON_PATH = "wp.png";
    private static final int BORDER_GAP = 20;
    private static final int CLOCK_UPDATE_INTERVAL = 1000; // milliseconds
    private static final String UNTIMED_TEXT = "Untimed game";

    // UI Components
    private JFrame gameWindow;
    private JLabel blackTimeLabel;
    private JLabel whiteTimeLabel;

    // Game State
    private Clock blackClock;
    private Clock whiteClock;
    private Timer timer;
    private Board board;
    private String blackPlayerName;
    private String whitPlayerName;
    private int hours;
    private int minutes;
    private int seconds;

    public GameWindow(String blackName, String whiteName, int hours, int minutes, int seconds) {
        this.blackPlayerName = blackName;
        this.whitPlayerName = whiteName;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;

        initializeClocks(hours, minutes, seconds);
        initializeGameWindow();
        setupUI();

        gameWindow.setVisible(true);
    }

    private void initializeClocks(int hours, int minutes, int seconds) {
        blackClock = new Clock(hours, seconds, minutes);
        whiteClock = new Clock(hours, seconds, minutes);
    }

    private void initializeGameWindow() {
        gameWindow = new JFrame(WINDOW_TITLE);
        loadWindowIcon();
        gameWindow.setLocation(100, 100);
        gameWindow.setLayout(new BorderLayout(BORDER_GAP, BORDER_GAP));
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void loadWindowIcon() {
        try {
            Image whiteImg = ImageIO.read(getClass().getResource(ICON_PATH));
            gameWindow.setIconImage(whiteImg);
        } catch (Exception e) {
            System.out.println("Error: Chess piece icon file not found: " + e.getMessage());
        }
    }

    private void setupUI() {
        // Add game data panel (player names and clocks)
        JPanel gameDataPanel = createGameDataPanel();
        gameWindow.add(gameDataPanel, BorderLayout.NORTH);

        // Add chess board
        board = new Board(this);
        gameWindow.add(board, BorderLayout.CENTER);

        // Add control buttons
        JPanel buttonsPanel = createButtonsPanel();
        gameWindow.add(buttonsPanel, BorderLayout.SOUTH);

        // Finalize window setup
        gameWindow.setMinimumSize(gameWindow.getPreferredSize());
        gameWindow.setSize(gameWindow.getPreferredSize());
        gameWindow.setResizable(false);
        gameWindow.pack();
    }

    private JPanel createGameDataPanel() {
        JPanel gameData = new JPanel();
        gameData.setLayout(new GridLayout(3, 2, 0, 0));

        // Player names
        addPlayerLabels(gameData);

        // Clock displays
        addClockLabels(gameData);

        // Start timer if game is timed
        startTimerIfNeeded();

        gameData.setPreferredSize(gameData.getMinimumSize());
        return gameData;
    }

    private void addPlayerLabels(JPanel gameData) {
        JLabel whiteLabel = createCenteredLabel(whitPlayerName);
        JLabel blackLabel = createCenteredLabel(blackPlayerName);

        gameData.add(whiteLabel);
        gameData.add(blackLabel);
    }

    private JLabel createCenteredLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setSize(label.getMinimumSize());
        return label;
    }

    private void addClockLabels(JPanel gameData) {
        whiteTimeLabel = createCenteredLabel(whiteClock.getTime());
        blackTimeLabel = createCenteredLabel(blackClock.getTime());

        boolean isUntimedGame = (hours == 0 && minutes == 0 && seconds == 0);
        if (isUntimedGame) {
            whiteTimeLabel.setText(UNTIMED_TEXT);
            blackTimeLabel.setText(UNTIMED_TEXT);
        }

        gameData.add(whiteTimeLabel);
        gameData.add(blackTimeLabel);
    }

    private void startTimerIfNeeded() {
        if (hours == 0 && minutes == 0 && seconds == 0) {
            return; // Untimed game
        }

        timer = new Timer(CLOCK_UPDATE_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClocks();
            }
        });
        timer.start();
    }

    private void updateClocks() {
        boolean isWhiteTurn = board.getTurn();

        if (isWhiteTurn) {
            updateWhiteClock();
        } else {
            updateBlackClock();
        }
    }

    private void updateWhiteClock() {
        whiteClock.decr();
        whiteTimeLabel.setText(whiteClock.getTime());

        if (whiteClock.outOfTime()) {
            handleTimeOut(false); // Black wins
        }
    }

    private void updateBlackClock() {
        blackClock.decr();
        blackTimeLabel.setText(blackClock.getTime());

        if (blackClock.outOfTime()) {
            handleTimeOut(true); // White wins
        }
    }

    private void handleTimeOut(boolean isWhiteWinner) {
        timer.stop();
        String winner = isWhiteWinner ? whitPlayerName : blackPlayerName;

        int response = JOptionPane.showConfirmDialog(
                gameWindow,
                winner + " wins by time! Play a new game? \n" +
                        "Choosing \"No\" quits the game.",
                winner + " wins!",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            new GameWindow(blackPlayerName, whitPlayerName, hours, minutes, seconds);
            gameWindow.dispose();
        } else {
            gameWindow.dispose();
        }
    }

    private JPanel createButtonsPanel() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 3, 10, 0));

        JButton instructionsButton = createInstructionsButton();
        JButton newGameButton = createNewGameButton();
        JButton quitButton = createQuitButton();

        buttons.add(instructionsButton);
        buttons.add(newGameButton);
        buttons.add(quitButton);

        buttons.setPreferredSize(buttons.getMinimumSize());
        return buttons;
    }

    private JButton createInstructionsButton() {
        JButton button = new JButton("How to play");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInstructions();
            }
        });
        return button;
    }

    private void showInstructions() {
        JOptionPane.showMessageDialog(gameWindow,
                "Move the chess pieces on the board by clicking\n"
                        + "and dragging. The game will watch out for illegal\n"
                        + "moves. You can win either by your opponent running\n"
                        + "out of time or by checkmating your opponent.\n"
                        + "\nGood luck, hope you enjoy the game!",
                "How to play",
                JOptionPane.PLAIN_MESSAGE);
    }

    private JButton createNewGameButton() {
        JButton button = new JButton("New game");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmNewGame();
            }
        });
        return button;
    }

    private void confirmNewGame() {
        int response = JOptionPane.showConfirmDialog(
                gameWindow,
                "Are you sure you want to begin a new game?",
                "Confirm new game",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(new StartMenu());
            closeGame();
        }
    }

    private JButton createQuitButton() {
        JButton button = new JButton("Quit");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmQuit();
            }
        });
        return button;
    }

    private void confirmQuit() {
        int response = JOptionPane.showConfirmDialog(
                gameWindow,
                "Are you sure you want to quit?",
                "Confirm quit",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            closeGame();
        }
    }

    private void closeGame() {
        if (timer != null) {
            timer.stop();
        }
        gameWindow.dispose();
    }

    public void checkmateOccurred(int winnerColor) {
        if (timer != null) {
            timer.stop();
        }

        String winner = (winnerColor == 0) ? "White" : "Black";
        int response = JOptionPane.showConfirmDialog(
                gameWindow,
                winner + " wins by checkmate! Set up a new game? \n" +
                        "Choosing \"No\" lets you look at the final situation.",
                winner + " wins!",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(new StartMenu());
            gameWindow.dispose();
        }
    }
}