import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SetupDialog {

    private JDialog dialog;
    private JTextField timeField;
    private JTextField roundsField;
    private JButton confirmButton;
    private TimerWindow timerWindow;

    public SetupDialog(JFrame parentWindow, TimerWindow timerWindow) {
        this.timerWindow = timerWindow;
        dialog = new JDialog(parentWindow, "Set Time", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setLayout(new BorderLayout());

        JLabel previewLabel = new JLabel("Time 00:00");
        previewLabel.setFont(new Font("Arial", Font.BOLD, 30));

        timeField = new JTextField();
        timeField.setFont(new Font("Arial", Font.PLAIN, 30));
        timeField.setPreferredSize(new Dimension(100, 40));

        roundsField = new JTextField();
        roundsField.setFont(new Font("Arial", Font.PLAIN, 30));
        roundsField.setPreferredSize(new Dimension(100, 40));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(timeField);

        JLabel roundsLabel = new JLabel("Rounds");
        roundsLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel roundsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        roundsPanel.add(roundsField);


        timeField.getDocument().addDocumentListener(new DocumentListener() {

            private void updatePreview() {
                String userInput = timeField.getText();

                if (userInput.isEmpty()) {
                    previewLabel.setText("00:00");
                    return;
                }
                int timeInput = Integer.parseInt(userInput);
                int minutes = timeInput / 100;
                int seconds = timeInput % 100;

                previewLabel.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePreview();
            }

        });

        confirmButton = new JButton("OK");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        previewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roundsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roundsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(previewLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(inputPanel);
        centerPanel.add(roundsLabel);
        centerPanel.add(roundsPanel);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(confirmButton);
        centerPanel.add(Box.createVerticalGlue());

        dialog.add(centerPanel, BorderLayout.CENTER);

        timeField.addActionListener(event -> {
            roundsField.requestFocusInWindow();
        });

        roundsField.addActionListener(event -> {
            confirmButton.doClick();
        });

        confirmButton.addActionListener(event -> {

            try {
                String userInput = timeField.getText();
                String roundsInput = roundsField.getText();
                int timeInput = Integer.parseInt(userInput);
                int rounds = Integer.parseInt(roundsInput);
                int minutes = timeInput / 100;
                int seconds = timeInput % 100;
                int totalSeconds = minutes * 60 + seconds;

                if (totalSeconds <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Time must be bigger than 0!");
                    timeField.setText("");
                    timeField.requestFocus();
                    return;
                }

                if (seconds >= 60) {
                    JOptionPane.showMessageDialog(dialog, "Last two digits must must be < 60!");
                    timeField.setText("");
                    timeField.requestFocus();
                    return;

                }

                if (rounds <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Rounds must be bigger than 0");
                    roundsField.setText("");
                    roundsField.requestFocus();
                    return;
                }

                timerWindow.setTimerSettings(totalSeconds, rounds);
                dialog.dispose();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialog, "Numbers only, valid time input and rounds bigger than 0! Ò.ó");
                timeField.setText("");
                timeField.requestFocus();
            }
        });

        JRootPane rootPane = dialog.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "pressConfirm");
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "closeDialog");

        actionMap.put("pressConfirm", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmButton.doClick();
            }
        });
        actionMap.put("closeDialog", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }
}