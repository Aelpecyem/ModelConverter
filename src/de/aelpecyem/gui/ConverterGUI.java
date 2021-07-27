package de.aelpecyem.gui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class ConverterGUI {
    public static final JFrame FRAME = new JFrame("Model Converter");
    public static final JButton IMPORT_BUTTON = new JButton("Import");
    public static final JButton EXPORT_BUTTON = new JButton("Export");;
    public static final JButton UPDATE_BUTTON = new JButton("Update Output");
    public static final JTextArea EXTERNAL_PARTS_TEXT = new JTextArea("//Externally added parts i.e. from inherited classes",10, 5);
    public static final JTextArea ISSUES_TEXT = new JTextArea(10, 10);

    public static final JTextArea INPUT_TEXT = new JTextArea("//Paste or import input here...");
    public static final JTextArea OUTPUT_TEXT = new JTextArea("//Update Output to make it appear here...");

    public static void init(){
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FRAME.setSize(700, 700);

        JMenuBar menuBar = new JMenuBar();
      //  menuBar.add(IMPORT_BUTTON); todo
      //  menuBar.add(EXPORT_BUTTON);
        menuBar.add(UPDATE_BUTTON);

        JToolBar infoBar = new JToolBar();
        infoBar.setOrientation(JToolBar.NORTH);
        JLabel externalPartsLabel = new JLabel("External");
        externalPartsLabel.setLocation(0, 0);
        EXTERNAL_PARTS_TEXT.setName("Externally Added Parts");
        EXTERNAL_PARTS_TEXT.setLocation(0, 1);
        infoBar.add(externalPartsLabel);
        infoBar.add(EXTERNAL_PARTS_TEXT);
        JLabel issuesLabel = new JLabel("Issues");
        ISSUES_TEXT.setName("Issues");
        infoBar.add(issuesLabel);
        infoBar.add(ISSUES_TEXT);

        JPanel ioPanel = new JPanel(new GridLayout());
        INPUT_TEXT.setMinimumSize(new Dimension(100, 100));
        INPUT_TEXT.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        OUTPUT_TEXT.setMinimumSize(new Dimension(100, 100));
        OUTPUT_TEXT.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        ioPanel.add(new JScrollPane(INPUT_TEXT));
        ioPanel.add(new JScrollPane(OUTPUT_TEXT));

        FRAME.setLayout(new BorderLayout());
        FRAME.getContentPane().add(BorderLayout.NORTH, menuBar);
        FRAME.getContentPane().add(BorderLayout.CENTER, ioPanel);
        FRAME.getContentPane().add(BorderLayout.WEST, infoBar);
        FRAME.setVisible(true);

        UPDATE_BUTTON.addActionListener(GuiLogic::updateContent);
    }
}
