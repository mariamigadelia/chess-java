package View;

import Controller.GameController;
import Model.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application window for the Chess Game.
 * Manages the game interface and coordinates between the UI components and controller.
 */
public class GameWindow extends JFrame {
    // UI Components
    private ChessBoardUI boardPanel;
    private JPanel controlPanel;
    private JLabel gameStatusLabel;
    private JButton newGameBtn;
    private JButton resignBtn;
    private JButton pauseTimerBtn;
    private StartMenu menuScreen;

    // Game controller reference
    private final GameController controller;

    // Window constants
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 650;
    private static final String APP_TITLE = "Chess Master";

    /**
     * Creates a new game window with the specified controller.
     *
     * @param controller The game controller that manages game logic
     */
    public GameWindow(GameController controller) {
        this.controller = controller;

        // Configure window properties
        setupWindowProperties();

        // Initialize all UI components
        initializeComponents();

        // Show the start menu initially
        displayStartMenu();

        // Make window visible
        setVisible(true);
    }

    /**
     * Configures basic window properties
     */
    private void setupWindowProperties() {
        setTitle(APP_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Initializes all UI components used in the application
     */
    private void initializeComponents() {
        // Create chess board UI component
        boardPanel = new ChessBoardUI(controller);

        // Create game control panel
        createControlPanel();

        // Create start menu
        menuScreen = new StartMenu(this, controller);
    }

    /**
     * Creates the game control panel with buttons and status display
     */
    private void createControlPanel() {
        // Create main panel with vertical layout
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Create status label
        gameStatusLabel = new JLabel("Game Ready");
        gameStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gameStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create control buttons
        newGameBtn = createButton("New Game", e -> displayStartMenu());
        resignBtn = createButton("Resign", e -> handleResignation());
        pauseTimerBtn = createButton("Pause Timer", e -> toggleTimer());

        // Add components to control panel with spacing
        controlPanel.add(createHeaderPanel());
        controlPanel.add(Box.createVerticalStrut(30));
        controlPanel.add(gameStatusLabel);
        controlPanel.add(Box.createVerticalStrut(40));
        controlPanel.add(newGameBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(resignBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(pauseTimerBtn);
        controlPanel.add(Box.createVerticalGlue());
    }

    /**
     * Creates a styled button with the given label and action
     */
    private JButton createButton(String label, ActionListener action) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(150, 30));
        button.addActionListener(action);
        return button;
    }

    /**
     * Creates a decorative header panel for the control section
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(APP_TITLE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Game Controls");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    /**
     * Shows the start menu screen
     */
    public void displayStartMenu() {
        getContentPane().removeAll();
        getContentPane().add(menuScreen, BorderLayout.CENTER);
        validate();
        repaint();
    }

    /**
     * Initializes and displays the game board and controls
     *
     * @param gameMode The selected game mode
     */
    public void startGame(String gameMode) {
        // Clear the current content
        getContentPane().removeAll();

        // Add board and control panel to the window
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.EAST);

        // Set up the game in the controller
        controller.startNewGame(gameMode);

        // Update status label
        updateStatus("White's turn");

        // Refresh the window
        validate();
        repaint();
    }

    /**
     * Handles player resignation
     */
    private void handleResignation() {
        int currentPlayer = controller.getCurrentTurn();
        String winner = PieceColor.colorName(PieceColor.opponent(currentPlayer));
        showGameOver(winner + " wins by resignation");
        controller.surrender();
    }

    /**
     * Toggles the chess timer on/off
     */
    private void toggleTimer() {
        boolean isRunning = controller.toggleClockPause();
        pauseTimerBtn.setText(isRunning ? "Pause Timer" : "Resume Timer");
    }

    /**
     * Updates the status display with current game information
     *
     * @param statusMessage The status message to display
     */
    public void updateStatus(String statusMessage) {
        gameStatusLabel.setText(statusMessage);
    }

    /**
     * Shows a game over dialog with the specified result message
     *
     * @param resultMessage The game result message
     */
    public void showGameOver(String resultMessage) {
        // Display game over dialog
        JOptionPane.showMessageDialog(
                this,
                resultMessage,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Refreshes the chess board display
     */
    public void refreshBoard() {
        if (boardPanel != null) {
            boardPanel.updateBoard();
        }
    }

    /**
     * Gets the chess board UI component
     *
     * @return The chess board UI
     */
    public ChessBoardUI getChessBoardUI() {
        return boardPanel;
    }
}