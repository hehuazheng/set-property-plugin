package com.hzz;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author: hezz
 */
public class SetterDialog extends DialogWrapper  {
    protected SetterDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        init();
        setTitle("GenerateConvertCode");
    }

    private JTextField tf1 = new JTextField(20);
    private JTextField tf2 = new JTextField(20);

    private String text1;
    private String text2;

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel p = new JPanel();
        Box b1 = Box.createVerticalBox();

        Box r1 = Box.createHorizontalBox();
        JLabel label = new JLabel("From");
        r1.add(label);
        r1.add(tf1);
        b1.add(r1);

        Box r2 = Box.createHorizontalBox();
        JLabel label2 = new JLabel("To");
        r2.add(label2);
        r2.add(tf2);

        b1.add(r2);

        p.add(b1);
        return p;
    }


    @Override
    protected void doOKAction() {
        text1 = tf1.getText();
        text2 = tf2.getText();
        super.doOKAction();
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }

}
