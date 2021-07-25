package de.aelpecyem.gui;

import de.aelpecyem.Main;
import de.aelpecyem.logic.ModelPartData;
import de.aelpecyem.logic.ModelReader;

import java.awt.event.ActionEvent;
import java.util.List;

public class GuiLogic {
    public static void updateContent(ActionEvent e){
        Main.EXTERNAL_PARTS.clear();
        Main.ISSUES.clear();
        String[] external = ConverterGUI.EXTERNAL_PARTS_TEXT.getText().split("\\s");
        for (String s : external) {
            System.out.println(s);
            Main.EXTERNAL_PARTS.put(s, new ModelPartData(s));
        }
        List<ModelPartData> data = ModelReader.readModel(ConverterGUI.INPUT_TEXT.getText());
        StringBuilder builder = new StringBuilder();
        Main.EXTERNAL_PARTS.values().forEach(part -> part.writeAsExternal(builder, "superData"));
        data.forEach(part -> part.writeString(builder, "root"));
        ConverterGUI.OUTPUT_TEXT.setText(builder.toString());
        StringBuilder issueBuilder = new StringBuilder();
        Main.ISSUES.forEach(issue -> issueBuilder.append("%s\n".formatted(issue)));
        ConverterGUI.ISSUES_TEXT.setText(issueBuilder.toString());
    }
}
