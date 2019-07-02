package com.hzz;

import javax.swing.*;
import java.util.Objects;

/**
 * @author: hezz
 */
public class ConverterGui {
    private JPanel panel;
    private ConverterConfig config;
    private JTextField textField;

    public ConverterGui(ConverterConfig config) {
        this.config = config;
        this.panel = new JPanel();
        textField = new JTextField(20);
        textField.setText(config.value);
        this.panel.add(textField);
        this.panel.setVisible(true);
    }

    public JComponent getPanel() {
        return this.panel;
    }

    // when OK is clicked
    public void apply() {
        String text = textField.getText();
        config.setValue(text);
    }

    public boolean isModified() {
        return !Objects.equals(config.value, textField.getText());
    }

}
