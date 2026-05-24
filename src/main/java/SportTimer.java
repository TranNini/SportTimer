import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SportTimer {

    static int startTime = 60;
    static int timeleft = startTime;

    public static void main(String[] args) {

        JFrame window = new JFrame("SportTimer");

        String input = JOptionPane.showInputDialog("Input time in seconds");

        startTime = Integer.parseInt(input);
        timeleft = startTime;

        JLabel timerLabel = new JLabel(String.valueOf(timeleft), SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 260));

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 50));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
        buttonPanel.add(startButton);

        window.setLayout(new BorderLayout());
        window.add(timerLabel, BorderLayout.CENTER);
        window.add(buttonPanel, BorderLayout.SOUTH);

        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        Timer timer = new Timer(1000, event -> {
            timeleft--;

            timerLabel.setText(String.valueOf(timeleft));

            if (timeleft == 0) {
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
                timer.start();
                startButton.setText("Restart");
                Toolkit.getDefaultToolkit().beep();
            }

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

/*  InputMap = which key?
    ActionMap = what action?
    AbstractAction = the action object
    actionPerformed = the code that runs
    doClick = fake a button click  */

    }
}
