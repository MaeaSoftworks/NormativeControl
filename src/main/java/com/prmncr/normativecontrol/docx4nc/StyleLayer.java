package com.prmncr.normativecontrol.docx4nc;

import java.util.*;

public class StyleLayer {
    private final Map<String, Object> declarations = new HashMap<>();
    private final List<StyleLayer> children = new ArrayList<>();
    private StyleLayer parent;

    public StyleLayer(StyleLayer parent) {
        this.parent = parent;
    }

    public StyleLayer() {
        parent = null;
    }

    public void addDeclaration(String property, Object value) {
        declarations.put(property, value);
    }

    public boolean hasRule(String property) {
        return declarations.containsKey(property) || parent.hasRule(property);
    }

    public void printStyles() {
        for (var key : declarations.keySet()) {
            System.out.println(key + " " + declarations.get(key));
        }
    }

    public void appendIfNecessary(String property, Object value) {
        if (hasRule(property)) {
            declarations.put(property, value);
        }
    }
}
