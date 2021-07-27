package de.aelpecyem.logic;

import de.aelpecyem.Main;
import de.aelpecyem.gui.ConverterGUI;

import java.util.Arrays;
import java.util.List;

public class ParsingLogic {
    public static List<ModelPartData> readModelPartData() {
        String[] external = ConverterGUI.EXTERNAL_PARTS_TEXT.getText().split("\\n");
        for (String s : external) {
            if (s.startsWith("//") || s.isBlank()){
                continue;
            }
            Arrays.stream(s.split("\\s")).forEach(s1 -> Main.EXTERNAL_PARTS.put(s1, new ModelPartData(s1)));
        }
        return ModelReader.readModel(ConverterGUI.INPUT_TEXT.getText());
    }

    public static String getTextureDataString(List<ModelPartData> data) {
        StringBuilder builder = new StringBuilder();
        Main.EXTERNAL_PARTS.values().forEach(part -> part.writeAsExternal(builder, "superData"));
        data.removeAll(Main.EXTERNAL_PARTS.values());
        data.stream().filter(part -> part.isTop).forEach(part -> part.writeString(builder, "root"));
        return builder.toString();
    }

    public static String getIssueString() {
        StringBuilder issueBuilder = new StringBuilder();
        Main.ISSUES.forEach(issue -> issueBuilder.append("%s\n".formatted(issue)));
        return issueBuilder.toString();
    }
}
