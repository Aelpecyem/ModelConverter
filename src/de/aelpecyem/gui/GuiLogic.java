package de.aelpecyem.gui;

import de.aelpecyem.Main;
import de.aelpecyem.logic.ModelPartData;
import de.aelpecyem.logic.ModelReader;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

public class GuiLogic {
    public static void updateContent(ActionEvent e){
        Main.EXTERNAL_PARTS.clear();
        Main.ISSUES.clear();
        String[] external = ConverterGUI.EXTERNAL_PARTS_TEXT.getText().split("\\n");
        for (String s : external) {
            if (s.startsWith("//") || s.isBlank()){
                continue;
            }
            Arrays.stream(s.split("\\s")).forEach(s1 -> Main.EXTERNAL_PARTS.put(s1, new ModelPartData(s1)));
        }
        List<ModelPartData> data = ModelReader.readModel(ConverterGUI.INPUT_TEXT.getText());
        StringBuilder builder = new StringBuilder();
        Main.EXTERNAL_PARTS.values().forEach(part -> part.writeAsExternal(builder, "superData"));
        data.removeAll(Main.EXTERNAL_PARTS.values());
        data.stream().filter(part -> part.isTop).forEach(part -> part.writeString(builder, "root"));
        ConverterGUI.OUTPUT_TEXT.setText(builder.toString());
        StringBuilder issueBuilder = new StringBuilder();
        Main.ISSUES.forEach(issue -> issueBuilder.append("%s\n".formatted(issue)));
        ConverterGUI.ISSUES_TEXT.setText(issueBuilder.toString());
    }
}
