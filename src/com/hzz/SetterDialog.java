package com.hzz;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author: hezz
 */
public class SetterDialog extends DialogWrapper {
    private GenerateData generateData;
    private boolean fromTextFieldActivated = true;

    private final JBTextField tf1;
    private final JBTextField tf2;
    private final JBList jlist = new JBList();

    private String text1;
    private String text2;

    protected SetterDialog(@Nullable Project project, boolean canBeParent, int maxLength, GenerateData generateData) {
        super(project, canBeParent);
        tf1 = new JBTextField(maxLength);
        tf2 = new JBTextField(maxLength);

        init();
        this.generateData = generateData;
        setTitle("convert Dialog");
        tf1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fromTextFieldActivated = true;
            }
        });
        tf2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fromTextFieldActivated = false;
            }
        });
        tf1.getDocument().addDocumentListener(new MyDocumentListener(tf1));
        tf2.getDocument().addDocumentListener(new MyDocumentListener(tf2));

        jlist.setExpandableItemsEnabled(false);
        jlist.setListData(new String[0]);
        jlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JBList source = (JBList) e.getSource();
                    if (fromTextFieldActivated) {
                        tf1.setText(source.getSelectedValue().toString().trim());
                    } else {
                        tf2.setText(source.getSelectedValue().toString().trim());
                    }
                    source.setListData(new String[0]);
                }
            }
        });
        this.setResizable(false);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel p = new JPanel();
        Box b1 = Box.createVerticalBox();
        b1.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        Box r3  = Box.createHorizontalBox();
        r3.add(jlist);
        b1.add(r3);

        p.add(b1);
        p.setMaximumSize(new Dimension(300, 200));
        return p;
    }

    // get focus
    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return tf1;
    }

    class MyDocumentListener implements DocumentListener {
        private JTextField tf;

        public MyDocumentListener(JTextField tf) {
            this.tf = tf;
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
            if (fromTextFieldActivated) {
                jlist.setListData(generateData.getData(s));
            } else {
                jlist.setListData(generateData.getData(s));
            }
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
