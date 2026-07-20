import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SetupDialog {

    private JDialog dialog;
    private JTextField timeField;
    private JTextField roundsField;
    private JTextField breakTimeField;
    private JButton confirmButton;
    private TimerWindow timerWindow;

    public SetupDialog(JFrame parentWindow, TimerWindow timerWindow) {
        this.timerWindow = timerWindow;
        dialog = new JDialog(parentWindow, "Set Time", true);
        dialog.setSize(400, 400);
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

        breakTimeField = new JTextField();
        breakTimeField.setFont(new Font("Arial", Font.PLAIN, 30));
        breakTimeField.setPreferredSize(new Dimension(100, 40));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(timeField);

        JLabel roundsLabel = new JLabel("Rounds");
        roundsLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel roundsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        roundsPanel.add(roundsField);

        JLabel breakTimeLabel = new JLabel("Break for 00:00");
        breakTimeLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel breakTimePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        breakTimePanel.add(breakTimeField);

        timeField.getDocument().addDocumentListener(new DocumentListener() {

            private void updatePreview() {
                String userTimeInput = timeField.getText();

                if (userTimeInput.isEmpty()) {
                    previewLabel.setText("00:00");
                    return;
                }
                int timeInput = Integer.parseInt(userTimeInput);
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

        breakTimeField.getDocument().addDocumentListener(new DocumentListener() {

            private void updatePreview() {
                String userBreakInput = breakTimeField.getText();

                if (userBreakInput.isEmpty()) {
                    breakTimeLabel.setText("00:00");
                    return;
                }

                int timeInput = Integer.parseInt(userBreakInput);
                int minutes = timeInput / 100;
                int seconds = timeInput % 100;

                breakTimeLabel.setText(String.format("%02d:%02d", minutes, seconds));
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
        breakTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        breakTimePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(previewLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(inputPanel);
        centerPanel.add(roundsLabel);
        centerPanel.add(roundsPanel);
        centerPanel.add(breakTimeLabel);
        centerPanel.add(breakTimePanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(confirmButton);
        centerPanel.add(Box.createVerticalGlue());

        dialog.add(centerPanel, BorderLayout.CENTER);

        timeField.addActionListener(e -> {
            roundsField.requestFocusInWindow();
        });

        roundsField.addActionListener(e -> {
            breakTimeField.requestFocusInWindow();
        });

        breakTimeField.addActionListener(e -> {
            confirmButton.doClick();
        });

        confirmButton.addActionListener(e -> {

            try {
                String userTimeInput = timeField.getText();
                String roundsInput = roundsField.getText();
                String userBreakInput = breakTimeField.getText();
                int timeInput = Integer.parseInt(userTimeInput);
                int rounds = Integer.parseInt(roundsInput);
                int breakInput = Integer.parseInt(userBreakInput);
                int totalSeconds = convertMmssToSeconds(timeInput);
                int totalBreakSeconds = convertMmssToSeconds(breakInput);

                if (hasInvalidSeconds(timeInput)) {
                    JOptionPane.showMessageDialog(dialog, "Time must be bigger than 0!");
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

                    if (hasInvalidSeconds(breakInput)) {
                        JOptionPane.showMessageDialog(dialog, "Timer must be bigger than 0");
                        breakTimeField.setText("");
                        breakTimeField.requestFocus();
                        return;
                    }

                    timerWindow.setTimerSettings(totalSeconds, rounds, totalBreakSeconds);
                    dialog.dispose();

                } catch(NumberFormatException exception){
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

        private int convertMmssToSeconds ( int input) {
            int minutes = input / 100;
            int seconds = input % 100;

            return minutes * 60 + seconds;
        }

        private boolean hasInvalidSeconds(int input) {
            int seconds = input % 100;
            return seconds >= 60;
        }
    }