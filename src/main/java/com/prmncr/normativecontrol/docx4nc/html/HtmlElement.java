package com.prmncr.normativecontrol.docx4nc.html;

import java.util.ArrayList;
import java.util.List;

public class HtmlElement {
    protected List<HtmlElement> children = new ArrayList<>();
    protected List<String> styles = new ArrayList<>();
    protected List<String> data = new ArrayList<>();
    private final String type;
    private String content;
    private String _class;

    public HtmlElement(String type, String... styles) {
        this.type = type;
        this.styles = List.of(styles);
    }

    public HtmlElement(String type) {
        this.type = type;
    }

    public HtmlElement(String type, String _class) {
        this.type = type;
        this._class = _class;
    }

    public void addChild(HtmlElement child) {
        children.add(child);
    }

    public void addStyle(String style) {
        styles.add(style);
    }

    public void addData(String data) {
        this.data.add(data);
    }

    public String getType() {
        return type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("<%s %s %s %s>%s</%s>",
                type,
                _class == null ? "" : "class='" + _class + "'",
                data.size() > 0 ? String.join("", data) : "",
                styles.size() > 0 ? "style='" + String.join("", styles) + "'" : "",
                children.size() != 0
                        ? String.join("", children.stream().map(HtmlElement::toString).toList())
                        : (content != null ? content : ""),
                type);
    }
}
