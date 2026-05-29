import javax.swing.*;
import java.awt.*;

public class SportTimer {

    public static void main(String[] args) {
        new TimerWindow();


/*
        JDialog setupDialog = new JDialog(window, "SportTimer Setup", true);

        JLabel instructionLabel = new JLabel("Enter time in seconds", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextField inputText = new JTextField(5);
        inputText.setFont(new Font("Arial", Font.BOLD, 24));

        JButton confirmButton = new JButton("Start");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputText);

        JPanel confirmButtonPanel = new JPanel();
        confirmButtonPanel.add(confirmButton);

        JPanel setupPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        setupPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        setupPanel.add(instructionLabel);
        setupPanel.add(inputPanel);
        setupPanel.add(confirmButtonPanel);

        setupDialog.add(setupPanel);
        setupDialog.setSize(350, 300);
        setupDialog.setLocationRelativeTo(null);

        confirmButton.addActionListener(event -> {
            String userInput = inputText.getText();

            try {
                startTime = Integer.parseInt(userInput);
                timeleft = startTime;

                timerLabel.setText(String.valueOf(timeleft));

                setupDialog.dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(setupDialog, "invalid Time input ò_ó");
            }
        });

        setupDialog.getRootPane()
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ENTER"), "pressConfirm");

        setupDialog.getRootPane()
                .getActionMap()
                .put("pressConfirm", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        confirmButton.doClick();
                    }
                });

        setupDialog.getRootPane()
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("SPACE"), "pressConfirm");

        setupDialog.getRootPane()
                .getActionMap()
                .put("pressConfirm", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        confirmButton.doClick();
                    }
                });

        setupDialog.setVisible(true);




*/
    }
}
