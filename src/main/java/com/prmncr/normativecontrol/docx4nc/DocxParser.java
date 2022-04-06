package com.prmncr.normativecontrol.docx4nc;

import com.prmncr.normativecontrol.docx4nc.html.HtmlBody;
import com.prmncr.normativecontrol.docx4nc.html.HtmlElement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class DocxParser {
    private static final int POINTS = 20;
    private final SAXBuilder builder;
    private XWPFDocument document;
    private final Logger logger = LoggerFactory.getLogger(DocxParser.class);

    public DocxParser() {
        this.builder = new SAXBuilder();
    }

    public static void main(String[] args) throws IOException {
        var parser = new DocxParser().init(new FileInputStream("src/main/resources/e0.docx"));
        parser.checkPageConfig();
        parser.findDefaultStyles();
        //try (var file = new FileOutputStream("src/main/resources/result.html")) {
        //    var html = parser.generateHtml();
        //    file.write(html.getBytes(StandardCharsets.UTF_16));
        //}
    }

    public DocxParser init(InputStream doc) throws IOException {
        document = new XWPFDocument(doc);
        return this;
    }

    private long getLongPixels(Object points) {
        return ((BigInteger) points).longValue() / POINTS;
    }

    private String getDoublePixels(String points) {
        return Double.toString(Double.parseDouble(points) / POINTS);
    }

    private void checkPageConfig() {
        var sector = document.getDocument().getBody().getSectPr();
        var pageSize = sector.getPgSz();
        var pageMargins = sector.getPgMar();

        var width = getLongPixels(pageSize.getW());
        var height = getLongPixels(pageSize.getH());
        if (width - 595 < 2 && height - 842 < 2) {
            logger.info("Page size: correct! ({} {})", width, height);
        } else {
            logger.warn("Page size: incorrect! ({} {})", width, height);
        }

        var marginTop = getLongPixels(pageMargins.getTop());
        var marginLeft = getLongPixels(pageMargins.getLeft());
        var marginBottom = getLongPixels(pageMargins.getBottom());
        var marginRight = getLongPixels(pageMargins.getRight());

        if (marginTop == 56 && marginRight == 42 && marginBottom == 56 && marginLeft == 85) {
            logger.info("Margins: correct! ({} {} {} {})", marginTop, marginRight, marginBottom, marginLeft);
        } else {
            logger.warn("Margins: incorrect! ({} {} {} {})", marginTop, marginRight, marginBottom, marginLeft);
        }
    }

    private StyleLayer findDefaultStyles() {
        var globalStyles = new StyleLayer();
        try {
            var paragraph = getXmlFromField(document.getStyles().getDefaultParagraphStyle(), "ppr");
            var root = paragraph.getRootElement();
            var namespace = root.getNamespace();

            var after = root.getAttributeValue("after", namespace);
            if (after != null) {
                globalStyles.addDeclaration("p/after", getDoublePixels(after));
            }
            var line = root.getAttributeValue("line", namespace);
            if (line != null) {
                var px = Double.parseDouble(line) / POINTS;
                if (px >= 0) {
                    globalStyles.addDeclaration("p/line", getDoublePixels(line));
                }
            }
            var rule = root.getAttributeValue("lineRule", namespace);
            if (rule != null) {
                globalStyles.addDeclaration("p/lineRule", rule);
            }

            var run = getXmlFromField(document.getStyles().getDefaultRunStyle(), "rpr");
            root = run.getRootElement();
            var attributes =
                    FieldUtils.readField(
                            CollectionUtils.get(
                                    FieldUtils.readField(root, "content", true),
                                    1),
                            "attributes", true);

            var fonts = new ArrayList<String>();
            for (int i = 0; i < CollectionUtils.size(attributes); i++) {
                var font = ((Attribute) CollectionUtils.get(attributes, i)).getValue();
                if (Objects.equals(font, "minorHAnsi")) {
                    fonts.add("Calibri");
                } else {
                    fonts.add(font);
                }
            }
            globalStyles.addDeclaration("r/font-family", String.join(", ", fonts));
            globalStyles.printStyles();
            return globalStyles;
        } catch (JDOMException | IOException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateHtml() {
        var html = new HtmlBody("generatePageConfig()");
        var page = new HtmlElement("div", "page");
        html.addChild(page);
        for (var paragraph : document.getParagraphs()) {
            if (paragraph.isPageBreak()) {
                page = new HtmlElement("div", "page");
                html.addChild(page);
            }
            var htmlParagraph = new HtmlElement("p");
            var styles = generateParagraphStyle(paragraph);
            htmlParagraph.addStyle(styles.get(0));
            htmlParagraph.addData(styles.get(1));
            page.addChild(htmlParagraph);
            long runNum = 0;
            for (var run : paragraph.getRuns()) {
                var span = new HtmlElement("span");
                htmlParagraph.addChild(span);
                span.setContent(run.text());
                var runStyle = checkRunStyle(run, runNum);
                span.addStyle(runStyle.get(0));
                span.addData(runStyle.get(1));
                runNum++;
            }
        }
        return html.toString();
    }

    private List<String> checkRunStyle(XWPFRun run, long id) {
        var style = new StringBuilder();
        var data = new StringBuilder();
        var font = run.getFontFamily();
        if (font != null) {
            style.append("font-family:").append(font).append(";");
        } else {
            try {
                var globalFont = getXmlFromField(run, "run");
                var element = FieldUtils.readField(CollectionUtils.get(FieldUtils.readField(globalFont.getRootElement(), "content", true), 1), "content", true);
                System.out.println();
                if (CollectionUtils.size(element) == 9) {
                    var fontElement = (Element) CollectionUtils.get(element, 1);
                    var fontStyle = fontElement.getAttributeValue("asciiTheme", fontElement.getNamespace());
                    style.append("font-family:").append("var(--").append(fontStyle).append(");");
                }
            } catch (IllegalAccessException | IOException | JDOMException e) {
                e.printStackTrace();
            }
        }
        if (run.isBold()) {
            style.append("font-weight: bold;");
        }

        if (run.isCapitalized()) {
            style.append("text-transform: capitalize;");
        }

        if (run.isSmallCaps()) {
            style.append("font-variant: small-caps;");
        }
        data.append("data-underline='").append(run.getUnderline()).append("';");
        style.append("background-color:").append(run.getTextHightlightColor()).append(";");
        style.append("color:#").append(run.getColor()).append(";");
        style.append("font-size:").append(run.getFontSizeAsDouble()).append("px;");
        return Arrays.asList(style.toString(), data.toString());
    }

    private List<String> generateParagraphStyle(XWPFParagraph p) {
        var style = new StringBuilder();
        var data = new StringBuilder();
        style.append("text-align:");
        switch (p.getAlignment()) {
            case CENTER -> style.append("center;");
            case BOTH -> style.append("justify;");
            case LEFT -> style.append("left;");
            case RIGHT -> style.append("right;");
        }
        style.append("text-indent:").append(p.getFirstLineIndent() / POINTS).append("px;");
        if (p.getSpacingBetween() >= 0) {
            style.append("line-height:").append(p.getSpacingBetween()).append(";");
        }
        style.append("vertical-align:");
        switch (p.getVerticalAlignment()) {
            case TOP -> style.append("top;");
            case CENTER -> style.append("middle;");
            case BASELINE -> style.append("baseline;");
            case BOTTOM -> style.append("bottom;");
            case AUTO -> style.append("super;");
        }
        style.append("padding-left:").append(p.getIndentFromLeft() / POINTS).append("px;");
        style.append("padding-right:").append(p.getIndentFromRight() / POINTS).append("px;");
        if (!Objects.equals(p.getBorderTop().toString(), "NONE")) {
            style.append("border-top:var(--PARAGRAPH-BORDER-").append(p.getBorderTop().toString()).append(");");
        }
        if (!Objects.equals(p.getBorderBottom().toString(), "NONE")) {
            style.append("border-bottom:var(--PARAGRAPH-BORDER-").append(p.getBorderBottom().toString()).append(");");
        }
        if (!Objects.equals(p.getBorderLeft().toString(), "NONE")) {
            style.append("border-left:var(--PARAGRAPH-BORDER-").append(p.getBorderLeft().toString()).append(");");
        }
        if (!Objects.equals(p.getBorderRight().toString(), "NONE")) {
            style.append("border-right:var(--PARAGRAPH-BORDER-").append(p.getBorderRight().toString()).append(");");
        }
        data.append("data-border-between='").append(p.getBorderRight().toString()).append("'");
        return Arrays.asList(style.toString(), data.toString());
    }

    private Document getXmlFromField(Object object, String fieldName)
            throws IllegalAccessException, IOException, JDOMException {
        return builder.build(new StringReader(FieldUtils.readField(object, fieldName, true).toString()));
    }
}
