import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TimerWindow {
    private int startTime = 60;
    private int timeleft = startTime;
    private JFrame window;
    private JLabel timerLabel;
    private JButton startButton;
    private JButton setTimeButton;
    private JPanel buttonPanel;
    private Timer timer;
    private int currentRound = 1;
    private int totalRounds = 1;
    private JLabel roundLabel;
    private int breakTime = 0;
    private boolean isBreak = false;

    public TimerWindow() {

        window = new JFrame("SportTimer");

        timerLabel = new JLabel(formatTime(timeleft), SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 260));

        roundLabel = new JLabel("Round 1/1", SwingConstants.CENTER);
        roundLabel.setFont(new Font("Arial", Font.BOLD, 70));
        roundLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));

        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 50));

        setTimeButton = new JButton("Set Time");
        setTimeButton.setFont(new Font("Arial", Font.BOLD, 50));

        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
        buttonPanel.add(startButton);
        buttonPanel.add(setTimeButton);

        window.setLayout(new BorderLayout());
        window.add(timerLabel, BorderLayout.CENTER);
        window.add(roundLabel, BorderLayout.NORTH);
        window.add(buttonPanel, BorderLayout.SOUTH);

        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        timer = new Timer(1000, event -> {
            timeleft--;
            timerLabel.setText(formatTime(timeleft));


            if (timeleft <= 10 && timeleft > 0) {
                Toolkit.getDefaultToolkit().beep();
            }

            if (timeleft <= 0) {
                Timer runningTimer = (Timer) event.getSource();
                Toolkit.getDefaultToolkit().beep();

                if (isBreak) {
                    isBreak = false;
                    currentRound++;
                    timeleft = startTime;
                    updateRoundLabel();
                    timerLabel.setText(formatTime(timeleft));
                    return;
                }

                if (currentRound >= totalRounds) {
                    runningTimer.stop();

                    timerLabel.setFont(new Font("Arial", Font.BOLD, 100));
                    timerLabel.setText("Well Done!");
                    startButton.setText("Start");
                    setTimeButton.setText("Set Time");
                    return;
                }

                if (breakTime > 0) {
                    isBreak = true;
                    timeleft = breakTime;
                    roundLabel.setText("Break " + currentRound + " / " + totalRounds);
                    timerLabel.setText(formatTime(timeleft));
                    return;
                }

                currentRound++;
                timeleft = startTime;
                updateRoundLabel();
                timerLabel.setText(formatTime(timeleft));
            }
        });

        startButton.addActionListener(event -> {
            if (timer.isRunning()) {
                timer.stop();
                timerLabel.setFont(new Font("Arial", Font.BOLD, 260));
                startButton.setText("Resume");
                setTimeButton.setText("Restart");
            } else {
                if (timeleft <= 0) {
                    timeleft = startTime;
                    currentRound = 1;
                    isBreak = false;
                    updateRoundLabel();
                    timerLabel.setFont(new Font("Arial", Font.BOLD, 260));
                    timerLabel.setText(formatTime(timeleft));
                }

                timer.start();
                startButton.setText("Pause");
                setTimeButton.setText("Restart");
                //Toolkit.getDefaultToolkit().beep();
            }
        });

        setTimeButton.addActionListener(event -> {
            if (setTimeButton.getText().equals("Set Time")) {
                new SetupDialog(window, this);
            } else {
                timer.stop();
                timeleft = startTime;
                currentRound = 1;
                isBreak = false;
                updateRoundLabel();
                timerLabel.setFont(new Font("Arial", Font.BOLD, 280));
                timerLabel.setText(formatTime(timeleft));
                startButton.setText("Start");
                setTimeButton.setText("Set Time");
            }
        });


        JRootPane rootPane = window.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "selectStartButton");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "selectSetTimeButton");

        actionMap.put("selectStartButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.requestFocusInWindow();
            }
        });

        actionMap.put("selectSetTimeButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTimeButton.requestFocusInWindow();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "clickSelectedButton");
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "clickSelectedButton");

        actionMap.put("clickSelectedButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (setTimeButton.hasFocus()) {
                    setTimeButton.doClick();
                } else {
                    startButton.doClick();
                }

            }
        });
    }

    public void setTimerSettings(int timePerRound, int newRounds, int newTimeBreak) {
        startTime = timePerRound;
        timeleft = timePerRound;
        totalRounds = newRounds;
        breakTime = newTimeBreak;

        currentRound = 1;
        isBreak = false;

        timerLabel.setFont(new Font("Arial", Font.BOLD, 280));
        timerLabel.setText(formatTime(timeleft));
        updateRoundLabel();

        startButton.setText("Start");
        setTimeButton.setText("Set Time");
    }

    private void updateRoundLabel() {
        roundLabel.setText("Round " + currentRound + " / " + totalRounds);
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        }

        return String.valueOf(seconds);
    }
}
