package com.hzz;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author: hezz
 */
public class SetterDialog extends DialogWrapper  {
    protected SetterDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        init();
        setTitle("test Dialog");
        tf1.getDocument().addDocumentListener(new MyDocumentListener(tf1, jlist));
        tf2.getDocument().addDocumentListener(new MyDocumentListener(tf2, jlist));
    }

    private final JTextField tf1 = new JTextField(20);
    private final JTextField tf2 = new JTextField(20);
    private final JBList jlist = new JBList();

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

        b1.add(jlist);

        p.add(b1);
        return p;
    }

    static class MyDocumentListener implements DocumentListener {
        private JTextField tf;
        private JBList jlist;

        public MyDocumentListener(JTextField tf, JBList jlist) {
            this.tf = tf;
            this.jlist = jlist;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            setData(tf.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setData(tf.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setData(tf.getText());
        }

        protected void setData(String s) {
            jlist.setListData(new String[]{s});
            System.out.println(s);
        }
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
