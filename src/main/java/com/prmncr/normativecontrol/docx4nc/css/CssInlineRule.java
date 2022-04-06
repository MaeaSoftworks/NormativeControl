package com.prmncr.normativecontrol.docx4nc.css;

public class CssInlineRule extends CssRule {
    public CssInlineRule(CssDeclaration... declarations) {
        super(null, declarations);
    }

    @Override
    public String toString() {
        var result = new StringBuilder();
        for (var declaration : declarations) {
            result.append(declaration.toString()).append(' ');
        }
        return result.substring(0, result.length() - 2);
    }
}
