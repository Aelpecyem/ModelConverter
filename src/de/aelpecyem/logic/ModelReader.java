package de.aelpecyem.logic;

import de.aelpecyem.Main;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModelReader {
    private static final Map<Predicate<String>, AssignmentConsumer> assignmentProcessors = new HashMap<>();
    private static final Map<String, BiConsumer<ModelPartData, List<String>>> methodProcessors = new HashMap<>();
    private static final Map<String, BiConsumer<ModelPartData, List<String>>> functionProcessors = new HashMap<>();
    private static Map<String, ModelPartData> fields = new HashMap<>();
    static {
        assignmentProcessors.put(string -> string.contains("mirror"), (statement, data, assignment) -> data.getBuilder().setMirrored(Boolean.parseBoolean(assignment)));

        methodProcessors.put("setTextureOffset", (data, params) -> {
            data.getBuilder().setUV(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)));
        });
        methodProcessors.put("setPivot", (data, params) -> {
            data.getTransform().setPivot(Float.parseFloat(params.get(0)), Float.parseFloat(params.get(1)), Float.parseFloat(params.get(2)));
        });
        methodProcessors.put("addCuboid", (data, params) -> {
            if (params.size() > 6){
                if (params.size() == 8){
                    data.getBuilder().addCuboid(Float.parseFloat(params.get(0)), Float.parseFloat(params.get(1)), Float.parseFloat(params.get(2)), Float.parseFloat(params.get(3)), Float.parseFloat(params.get(4)), Float.parseFloat(params.get(5)), Float.parseFloat(params.get(6)), Float.parseFloat(params.get(6)), Float.parseFloat(params.get(6)));
                    data.getBuilder().setMirrored(Boolean.parseBoolean(params.get(7)));
                }else if (params.size() == 7){
                    data.getBuilder().addCuboid(Float.parseFloat(params.get(0)), Float.parseFloat(params.get(1)), Float.parseFloat(params.get(2)), Float.parseFloat(params.get(3)), Float.parseFloat(params.get(4)), Float.parseFloat(params.get(5)), Float.parseFloat(params.get(6)), Float.parseFloat(params.get(6)), Float.parseFloat(params.get(6)));
                }
            }else {
                data.getBuilder().addCuboid(Float.parseFloat(params.get(0)), Float.parseFloat(params.get(1)), Float.parseFloat(params.get(2)), Float.parseFloat(params.get(3)), Float.parseFloat(params.get(4)), Float.parseFloat(params.get(5)), 0, 0, 0);
            }
        });
        methodProcessors.put("addChild", (data, params) -> {
            String param = params.get(0);
            if (param.startsWith("this.")){
                param = param.substring(5);
            }
            if (!param.equals(data.getName())) {
                data.addChild(fields.get(param));
            }
        });
        functionProcessors.put("setRotateAngle", (data, params) -> data.getTransform().setRotation(Float.parseFloat(params.get(1)), Float.parseFloat(params.get(2)), Float.parseFloat(params.get(3))));
        functionProcessors.put("setRotationAngle", (data, params) -> data.getTransform().setRotation(Float.parseFloat(params.get(1)), Float.parseFloat(params.get(2)), Float.parseFloat(params.get(3))));
    }
    public static List<ModelPartData> readModel(String input){
        List<String> statements = new ArrayList<>();
        Arrays.stream(input.split("\\n")).filter(s -> !s.startsWith("//")).map(s -> s.replaceAll("\\s", "").split(";")).forEach((strings) -> statements.addAll(Arrays.asList(strings)));
        fields = findFields(statements);
        String lastField = "nil";
        for (String statement : statements) {
            computeStatement(lastField, statement);
        }
        List<ModelPartData> result = new ArrayList<>(fields.values());
        fields.clear();
        return result;
    }

    private static void computeStatement(String lastField, String statement) {
        if (!statement.isBlank()) {
            lastField = checkStatementForField(lastField, statement);
            ModelPartData data = fields.get(lastField);
            String[] parts = statement.split("=");
            try {
                if (parts.length == 2) {
                    for (Predicate<String> stringPredicate : assignmentProcessors.keySet()) {
                        if (stringPredicate.test(parts[0])) {
                            assignmentProcessors.get(stringPredicate).accept(parts[0], data, parts[1]);
                            break;
                        }
                    }
                } else {
                    if (!computeOther(statement, data)) {
                        findMethodCalls(lastField, statement).forEach((method, params) -> methodProcessors.getOrDefault(method, (a, b) -> System.out.println("Detected unknown operation %s, skipping".formatted(method))).accept(data, params));
                    }
                }
            }catch (NullPointerException e){
                Main.ISSUES.add("Error parsing statement:\t %s%n".formatted(statement));
                boolean hasStatement = false;
                for(String field : fields.keySet()){
                    if (statement.startsWith(field) || statement.startsWith("this.%s".formatted(statement))){
                        hasStatement = true;
                    }
                }
                if (!hasStatement){
                    Main.ISSUES.add("Possible cause: field is not defined");
                }
            }
        }
    }

    private static String checkStatementForField(String lastField, String statement) {
        String[] checkParts = statement.split("[.,=()]");
        for (String checkPart : checkParts) {
            for (String field : fields.keySet()) {
                if (checkPart.equals(field)) {
                    return field;
                }
            }
        }
        return lastField;
    }

    private static Map<String, ModelPartData> findFields(List<String> statements) {
        Map<String, ModelPartData> fields = new HashMap<>();
        Pattern pattern = Pattern.compile("=newModelPart\\(");
        statements.removeIf(statement -> {
            Matcher matcher = pattern.matcher(statement);
            if (matcher.find()){
                String field = statement.substring(0, matcher.start()).replace("ModelPart", "").replace("this.", "");
                String params[] = statement.substring(matcher.end(), statement.length() - 1).split(",");
                ModelPartData data = new ModelPartData(field);
                if (params.length > 1) {
                    int[] uv = new int[2];
                    if (params[0].equals("this")) {
                        uv[0] = Integer.parseInt(params[1]);
                        uv[1] = Integer.parseInt(params[2]);
                    } else {
                        uv[0] = Integer.parseInt(params[0]);
                        uv[1] = Integer.parseInt(params[1]);
                    }
                    data.getBuilder().setUV(uv[0], uv[1]);
                }
                fields.put(field, data);
                return true;
            }
            return false;
        });
        fields.putAll(Main.EXTERNAL_PARTS);
        return fields;
    }


    public static Map<String, List<String>> findMethodCalls(String field, String statement){
        Pattern p = Pattern.compile("\\.(.*?)\\)");
        Matcher m = p.matcher(statement);
        Map<String, List<String>> parts = new HashMap<>();
        while (m.find()){
            String method = m.group(1);
            String[] components = method.split("\\(|,");
            List<String> parameters = new ArrayList<>(Arrays.asList(components).subList(1, components.length));
            parts.put(components[0].startsWith(field) ? components[0].substring(field.length() + 1) : components[0], parameters);
        }
        return parts;
    }

    public static boolean computeOther(String statement, ModelPartData data){
        if (statement.startsWith("this.")){
            statement = statement.substring(5);
        }
        for (String s : functionProcessors.keySet()) {
            if (statement.startsWith(s)){
                Pattern p = Pattern.compile("\\((.*?)\\)");
                Matcher m = p.matcher(statement);
                if (m.find()) {
                    String method = m.group(1);
                    String[] components = method.split(",");
                    List<String> parameters = new ArrayList<>(Arrays.asList(components));
                    functionProcessors.get(s).accept(data, parameters);
                    return true;
                }
            }
        }
        return false;
    }
    private interface AssignmentConsumer {
        void accept(String statement, ModelPartData data, String assignment);
    }
}
