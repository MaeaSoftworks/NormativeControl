package com.prmncr.normativecontrol.docx4nc.css;

public class CssDeclaration {
    private final String property;
    private final String value;
    private final String units;

    public CssDeclaration(String property, String value) {
        this.property = property;
        this.value = value;
        units = null;
    }

    public CssDeclaration(String property, String value, String units) {
        this.property = property;
        this.value = value;
        this.units = units;
    }

    @Override
    public String toString() {
        return property + ": " + value + (units != null ? units + ";" : ";");
    }
}
