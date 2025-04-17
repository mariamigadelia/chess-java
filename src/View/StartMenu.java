package View;

import Controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Entry point UI for the chess application that manages game setup and configuration.
 * Provides options for different game modes and time controls.
 */
public class StartMenu extends JPanel implements Runnable {
    private JFrame mainFrame;
    private GameController gameController;
    private GameWindow gameWindow;
    private JComboBox<String> modeDropdown;
    private JCheckBox timedGameCheckbox;
    private JSpinner hoursSpinner, minutesSpinner, secondsSpinner;
    private JButton playButton;
    private JButton exitButton;

    /**
     * Constructor that accepts both GameWindow and GameController
     */
    public StartMenu(GameWindow gameWindow, GameController controller) {
        this.gameWindow = gameWindow;
        this.gameController = controller;
        if (controller != null) {
            controller.setView(gameWindow);
        }
    }

    /**
     * Initializes and configures all UI components
     */
    private void initializeUI() {
        // Set up panel properties
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setBackground(new Color(240, 240, 240));

        // Add main components to the layout
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createConfigPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * Creates the header panel with game title and description
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        // Game title
        JLabel titleLabel = new JLabel("Chess Master");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Game subtitle/description
        JLabel subtitleLabel = new JLabel("Start a new game with your preferred settings");
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with spacing
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        return headerPanel;
    }

    /**
     * Creates the game configuration panel with all game options
     */
    private JPanel createConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setOpaque(false);

        // Game mode selection
        JPanel gameModePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gameModePanel.setOpaque(false);

        JLabel modeLabel = new JLabel("Game Mode:");
        modeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        String[] modes = {"Player vs Player", "Player vs Computer", "Computer vs Computer"};
        modeDropdown = new JComboBox<>(modes);
        modeDropdown.setPreferredSize(new Dimension(200, 25));

        gameModePanel.add(modeLabel);
        gameModePanel.add(modeDropdown);

        // Time control options
        JPanel timeControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeControlPanel.setOpaque(false);

        timedGameCheckbox = new JCheckBox("Enable Time Control");
        timedGameCheckbox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timedGameCheckbox.setOpaque(false);

        timeControlPanel.add(timedGameCheckbox);

        // Time settings panel
        JPanel timeSettingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeSettingsPanel.setOpaque(false);

        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        minutesSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 59, 1));
        secondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 5));

        timeSettingsPanel.add(new JLabel("Hours:"));
        timeSettingsPanel.add(hoursSpinner);
        timeSettingsPanel.add(new JLabel("Minutes:"));
        timeSettingsPanel.add(minutesSpinner);
        timeSettingsPanel.add(new JLabel("Seconds:"));
        timeSettingsPanel.add(secondsSpinner);

        // Initially disable time settings
        enableTimeSettings(false);

        // Add listener to checkbox
        timedGameCheckbox.addActionListener(e ->
                enableTimeSettings(timedGameCheckbox.isSelected())
        );

        // Add all panels to config
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(gameModePanel);
        configPanel.add(Box.createVerticalStrut(15));
        configPanel.add(timeControlPanel);
        configPanel.add(Box.createVerticalStrut(5));
        configPanel.add(timeSettingsPanel);
        configPanel.add(Box.createVerticalGlue());

        return configPanel;
    }

    /**
     * Creates the button panel with start and exit buttons
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        playButton = new JButton("Start Game");
        playButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        playButton.setPreferredSize(new Dimension(150, 40));

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        exitButton.setPreferredSize(new Dimension(100, 40));

        // Add action listeners
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(playButton);
        buttonPanel.add(exitButton);

        return buttonPanel;
    }

    /**
     * Enables or disables the time control settings
     *
     * @param enable True to enable, false to disable
     */
    private void enableTimeSettings(boolean enable) {
        hoursSpinner.setEnabled(enable);
        minutesSpinner.setEnabled(enable);
        secondsSpinner.setEnabled(enable);
    }

    /**
     * Starts the game with the currently selected options
     */
    private void startGame() {
        String gameMode = (String) modeDropdown.getSelectedItem();

        if (gameController != null) {
            if (timedGameCheckbox.isSelected()) {
                int hours = (Integer) hoursSpinner.getValue();
                int minutes = (Integer) minutesSpinner.getValue();
                int seconds = (Integer) secondsSpinner.getValue();

                // Start timed game
                gameController.startTimedGame(gameMode, hours, minutes, seconds);
            } else {
                // Start regular game
                gameController.startNewGame(gameMode);
            }

            // Make the game window visible and close the start menu
            if (gameWindow != null) {
                gameWindow.setVisible(true);
            }

            if (mainFrame != null) {
                mainFrame.dispose();
            }
        }
    }

    /**
     * Creates and shows the start menu window
     */
    @Override
    public void run() {
        // Create main frame
        mainFrame = new JFrame("Chess Game Setup");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize UI
        initializeUI();

        // Add this panel to the frame
        mainFrame.add(this);
        mainFrame.setSize(500, 400);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
    }
}