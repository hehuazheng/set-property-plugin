package com.hzz;

import com.intellij.ui.components.JBList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author: hezz
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().createAndShowGUI();
            }
        });
    }

    public Main() {
        jlist.setListData(new String[]{
                "a","b","ccc","ddd","eeeee"
        });
        jlist.setVisibleRowCount(10);
        jlist.setPreferredSize(new Dimension(200, 100));
        jlist.setFixedCellHeight(10);
        jlist.setFixedCellWidth(200);
        // 响应鼠标双击
        jlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    JBList source = (JBList)e.getSource();
                    System.out.println(source.getSelectedValue());
                }
            }
        });
//        jlist.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//
//                JBList source = (JBList)e.getSource();
//                System.out.println(source.getSelectedValue());
//            }
//        });
        tf1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
            }
        });
        tf1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("tf1 clicked");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        tf1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setData(tf1.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setData(tf1.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setData(tf1.getText());
            }

            protected void setData(String s) {
                jlist.setListData(new String[]{s});
                System.out.println(s);
            }
        });
    }

    private final JTextField tf1 = new JTextField(20);
    private final JTextField tf2 = new JTextField(20);
    private JBList jlist = new JBList();

    public void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(createPanel());
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel createPanel() {
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
}
