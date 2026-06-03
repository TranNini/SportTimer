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


    public TimerWindow () {

        window = new JFrame("SportTimer");

        timerLabel = new JLabel(String.valueOf(timeleft), SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 260));

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
        window.add(buttonPanel, BorderLayout.SOUTH);

        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        timer = new Timer(1000, event -> {
            timeleft--;

            timerLabel.setText(String.valueOf(timeleft));

            if (timeleft <= 0) {
                Timer runningTimer = (Timer) event.getSource();
                Toolkit.getDefaultToolkit().beep();
                runningTimer.stop();
                timerLabel.setFont(new Font("Arial", Font.BOLD, 80));
                timerLabel.setText("Well Done!");
            }

        });

        startButton.addActionListener(event -> {
            if (timer.isRunning()) {
                timer.stop();
                timeleft = startTime;
                timerLabel.setText(String.valueOf(timeleft));
                startButton.setText("Start");
            } else {
                if (timeleft <= 0) {
                    timeleft = startTime;

                    timerLabel.setFont(new Font("Arial", Font.BOLD, 260));
                    timerLabel.setText(String.valueOf(timeleft));

                    startButton.setText("Start");
                    return;
                }

                timer.start();
                startButton.setText("Restart");
                Toolkit.getDefaultToolkit().beep();
            }
        });

            setTimeButton.addActionListener(event -> {
                SetupDialog setupDialog = new SetupDialog(window,this);
            });

        window.getRootPane()
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("SPACE"), "pressStartButton");

        window.getRootPane()
                .getActionMap()
                .put("pressStartButton", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        startButton.doClick();
                    }
                });

        window.getRootPane()
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ENTER"), "pressStartButton");

        window.getRootPane()
                .getActionMap()
                .put("pressStartButton", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        startButton.doClick();
                    }
                });
    }

    public void setTime(int newTime) {
        startTime = newTime;
        timeleft = newTime;
        timerLabel.setText(String.valueOf(timeleft));
    }
}
