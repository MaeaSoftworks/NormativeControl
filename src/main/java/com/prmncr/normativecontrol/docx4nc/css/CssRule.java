package com.prmncr.normativecontrol.docx4nc.css;

import java.util.ArrayList;
import java.util.List;

public class CssRule {
    protected final List<CssDeclaration> declarations = new ArrayList<>();
    protected String selector;

    public CssRule(String selector, CssDeclaration... declarations) {
        this.selector = selector;
        this.declarations.addAll(List.of(declarations));
    }

    @Override
    public String toString() {
        var result = new StringBuilder(selector + " { ");
        for (var declaration : declarations) {
            result.append(declaration.toString()).append(' ');
        }
        return result.append("} ").toString();
    }

    public void addDeclaration(CssDeclaration declaration) {
        declarations.add(declaration);
    }

    public void addToSelector(String newSelector) {
        selector += ", " + newSelector;
    }

    public String getSelector() {
        return selector;
    }
}
