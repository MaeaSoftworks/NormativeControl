package com.prmncr.normativecontrol.docx4nc.html;

public class HtmlBody extends HtmlElement {
    public HtmlBody(String styles) {
        super("html");
        this.styles.add(styles);
    }

    @Override
    public String toString() {
        return String.format(
                "<!DOCTYPE html><html><head><style>*{margin: 0; padding:0;}</style><style>%s</style></head><body>%s</body></html>",
                String.join("", styles),
                String.join("", children.stream().map(HtmlElement::toString).toList()));
    }
}
