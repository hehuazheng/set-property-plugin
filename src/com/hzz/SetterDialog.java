package com.hzz;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
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
    protected SetterDialog(@Nullable Project project, boolean canBeParent, GenerateData generateData) {
        super(project, canBeParent);
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

        jlist.setVisibleRowCount(10);
        jlist.setPreferredSize(new Dimension(200, 100));
        jlist.setListData(new String[]{"a","b","c","d"});
        jlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JBList source = (JBList) e.getSource();
                    if (fromTextFieldActivated) {
                        tf1.setText(source.getSelectedValue().toString());
                    } else {
                        tf2.setText(source.getSelectedValue().toString());
                    }
                }
            }
        });
    }

    private GenerateData generateData;
    private boolean fromTextFieldActivated = true;

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

        JLabel label3 = new JLabel("Classes");
        b1.add(label3);

        b1.add(jlist);

        p.add(b1);
        return p;
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
            if(fromTextFieldActivated) {
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
