package de.aelpecyem;

import de.aelpecyem.gui.ConverterGUI;
import de.aelpecyem.logic.ModelPartData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    public static final Map<String, ModelPartData> EXTERNAL_PARTS = new HashMap<>();
    public static final List<String> ISSUES = new ArrayList<>();
    public static void main(String[] args) {
        ConverterGUI.init();
    }
}
