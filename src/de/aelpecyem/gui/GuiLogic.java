package de.aelpecyem.gui;

import de.aelpecyem.Main;
import de.aelpecyem.logic.ModelPartData;
import de.aelpecyem.logic.ParsingLogic;

import java.awt.event.ActionEvent;
import java.util.List;

public class GuiLogic {
    public static void updateContent(ActionEvent e){
        Main.EXTERNAL_PARTS.clear();
        Main.ISSUES.clear();
        List<ModelPartData> data = ParsingLogic.readModelPartData();
        ConverterGUI.OUTPUT_TEXT.setText(ParsingLogic.getTextureDataString(data));
        ConverterGUI.ISSUES_TEXT.setText(ParsingLogic.getIssueString());
    }
}
