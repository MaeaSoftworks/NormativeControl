package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.components.SectorKeywords;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.ErrorType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DocumentParser {
    private static final int POINTS = 20;
    private final SAXBuilder builder;
    private final CorrectDocumentParams params;
    private final SectorKeywords keywords;
    private final MainDocumentPart mainDocumentPart;
    private final WordprocessingMLPackage document;

    private final List<Object> frontPage = new ArrayList<>();
    private final List<Object> annotation = new ArrayList<>();
    private final List<Object> contents = new ArrayList<>();
    private final List<Object> introduction = new ArrayList<>();
    private final List<Object> body = new ArrayList<>();
    private final List<Object> conclusion = new ArrayList<>();
    private final List<Object> references = new ArrayList<>();
    private final List<Object> appendix = new ArrayList<>();
    private final List<List<Object>> sectors = new ArrayList<>();

    public DocumentParser(WordprocessingMLPackage doc, CorrectDocumentParams params, SectorKeywords keywords) {
        this.builder = new SAXBuilder();
        this.keywords = keywords;
        mainDocumentPart = doc.getMainDocumentPart();
        document = doc;
        this.params = params;
        sectors.add(frontPage);
        sectors.add(annotation);
        sectors.add(contents);
        sectors.add(introduction);
        sectors.add(body);
        sectors.add(conclusion);
        sectors.add(references);
        sectors.add(appendix);
    }

    public List<List<Object>> getSectors() {
        return sectors;
    }

    private long getLongPixels(Object points) {
        return ((BigInteger) points).longValue() / POINTS;
    }

    private String getDoublePixels(String points) {
        return Double.toString(Double.parseDouble(points) / POINTS);
    }

    private Document getXmlFromField(Object object, String fieldName)
            throws IllegalAccessException, IOException, JDOMException {
        return builder.build(new StringReader(FieldUtils.readField(object, fieldName, true).toString()));
    }

    public void checkPageSize(ArrayList<Error> errors) {
        var sector = mainDocumentPart.getJaxbElement().getBody().getSectPr();
        var pageSize = sector.getPgSz();

        var width = getLongPixels(pageSize.getW());
        var height = getLongPixels(pageSize.getH());
        if (width != params.pageWidth || height != params.pageHeight) {
            errors.add(new Error(-1, -1, ErrorType.INCORRECT_PAGE_SIZE));
        }
    }

    public void checkPageMargins(ArrayList<Error> errors) {
        var sector = mainDocumentPart.getJaxbElement().getBody().getSectPr();
        var pageMargins = sector.getPgMar();
        var marginTop = getLongPixels(pageMargins.getTop());
        var marginLeft = getLongPixels(pageMargins.getLeft());
        var marginBottom = getLongPixels(pageMargins.getBottom());
        var marginRight = getLongPixels(pageMargins.getRight());

        if (marginTop != params.pageMarginTop || marginRight != params.pageMarginRight
                || marginBottom != params.pageMarginBottom || marginLeft != params.pageMarginLeft) {
            errors.add(new Error(-1, -1, ErrorType.INCORRECT_PAGE_MARGINS));
        }
    }

    public void findSectors(List<Error> errors) {
        findSectors(0, 0, errors);
    }

    private void findSectors(int paragraphId, int sectorId, List<Error> errors) {
        var paragraphs = mainDocumentPart.getContent();
        for (var paragraph = paragraphId; paragraph < paragraphs.size(); paragraph++) {
            // are we collecting last sector? (he hasn't got next header)
            if (keywords.allKeywords.size() + 1 == sectorId) {
                sectors.get(sectorId).add(paragraphs.get(paragraph));
                continue;
            }
            var p = paragraphs.get(paragraph);
            if (p instanceof P && ((P) p).getContent().size() <= 2) {
                var text = TextUtils.getText(paragraphs.get(paragraph));
                // did we find some header?
                if (isHeader(paragraph, text)) {
                    // yes, some header is here
                    var error = new Error(paragraph, -1, ErrorType.INCORRECT_SECTORS);
                    for (int i = sectorId + 1; i <= keywords.allKeywords.size(); i++) {
                        // what sector is this header?
                        if (keywords.allKeywords.get(i - 1).contains(text.toUpperCase(Locale.ROOT))) {
                            // we found next sector
                            checkHeaderStyle(paragraph, errors);
                            paragraph++;
                            findSectors(paragraph, i, errors);
                            return;
                        } else if (!errors.contains(error)) {
                            // it is not next sector! error!
                            errors.add(error);
                        }
                    }
                } else {
                    //it's not a header
                    sectors.get(sectorId).add(paragraphs.get(paragraph));
                }
            } else {
                sectors.get(sectorId).add(paragraphs.get(paragraph));
            }
        }
    }

    public boolean isHeader(int paragraph, String text) {
        var words = text.split("\s+");
        if (words.length == 0 || words.length > keywords.getMaxLength()) {
            return false;
        }
        var paragraphs = mainDocumentPart.getContent();
        if (paragraph > 0) {
            if (paragraphs.get(paragraph - 1) instanceof P previous) {
                var runs = previous.getContent();
                if (runs.size() > 0) {
                    var lastRun = runs.get(runs.size() - 1);
                    if (lastRun instanceof R r) {
                        if (r.getContent().get(r.getContent().size() - 1) instanceof Br) {
                            return true;
                        }
                    }
                    if (!(lastRun instanceof Br)) {
                        return false;
                    }
                }
            }
        }
        return keywords.allKeywordsFlat.contains(text.toUpperCase(Locale.ROOT));
    }

    public void checkHeaderStyle(int paragraph, List<Error> errors) {

    }

    /*
    private Map<String, Object> findDefaultStyles() {
        var globalStyles = new HashMap<String, Object>();
        try {
            var paragraph = getXmlFromField(mainDocumentPart.getStyles().getDefaultParagraphStyle(), "ppr");
            var root = paragraph.getRootElement();
            var namespace = root.getNamespace();

            var after = root.getAttributeValue("after", namespace);
            if (after != null) {
                globalStyles.put("p/after", getDoublePixels(after));
            }
            var line = root.getAttributeValue("line", namespace);
            if (line != null) {
                var px = Double.parseDouble(line) / POINTS;
                if (px >= 0) {
                    globalStyles.put("p/line", getDoublePixels(line));
                }
            }
            var rule = root.getAttributeValue("lineRule", namespace);
            if (rule != null) {
                globalStyles.put("p/lineRule", rule);
            }

            var run = getXmlFromField(mainDocumentPart.getStyles().getDefaultRunStyle(), "rpr");
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
            globalStyles.put("r/font-family", String.join(", ", fonts));
            return globalStyles;
        } catch (JDOMException | IOException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
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
*/

    public List<Error> runStyleCheck() {
        var errors = new ArrayList<Error>();
        findSectors(0, 0, errors);
        checkPageSize(errors);
        checkPageMargins(errors);

        return errors;
    }
}
