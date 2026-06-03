import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SetupDialog {

    private JDialog dialog;
    private JTextField timeField;
    private JButton confirmButton;
    private TimerWindow timerWindow;

    public SetupDialog(JFrame parentWindow, TimerWindow timerWindow) {
        this.timerWindow = timerWindow;
        dialog = new JDialog(parentWindow, "Set Time", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setLayout(new BorderLayout());

        JLabel infoLabel = new JLabel("Add time in seconds.", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 26));

        timeField = new JTextField();
        timeField.setFont(new Font("Arial", Font.PLAIN, 30));
        timeField.setPreferredSize(new Dimension(100, 40));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(timeField);

        confirmButton = new JButton("OK");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(inputPanel);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(confirmButton);
        centerPanel.add(Box.createVerticalGlue());

        dialog.add(centerPanel, BorderLayout.CENTER);

        confirmButton.addActionListener(event -> {
            String userInput = timeField.getText();

            try {
                int newTime = Integer.parseInt(userInput);
                if (newTime <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Time must be bigger than 0!");
                    timeField.setText("");
                    timeField.requestFocus();
                    return;
                }
                timerWindow.setTime(newTime);
                dialog.dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(dialog, "invalid time Ò.ó");
                timeField.setText("");
                timeField.requestFocus();
            }
        });

        JRootPane rootPane = dialog.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "pressConfirm");
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