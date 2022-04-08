package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.components.SectorKeywords;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.ErrorType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.*;

public class DocumentParser {
    private static final int POINTS = 20;
    private final SAXBuilder builder;
    private final CorrectDocumentParams params;
    private final SectorKeywords keywords;
    private final XWPFDocument document;
    private final int paragraphsCount;

    private final List<XWPFParagraph> frontPage = new ArrayList<>();
    private final List<XWPFParagraph> contents = new ArrayList<>();
    private final List<XWPFParagraph> introduction = new ArrayList<>();
    private final List<XWPFParagraph> essay = new ArrayList<>();
    private final List<XWPFParagraph> conclusion = new ArrayList<>();
    private final List<XWPFParagraph> references = new ArrayList<>();
    private final List<XWPFParagraph> appendix = new ArrayList<>();
    private final List<List<XWPFParagraph>> sectors = new ArrayList<>();

    public DocumentParser(XWPFDocument doc, CorrectDocumentParams params, SectorKeywords keywords) {
        this.builder = new SAXBuilder();
        this.keywords = keywords;
        document = doc;
        this.params = params;
        paragraphsCount = document.getParagraphs().size();
        sectors.add(frontPage);
        sectors.add(contents);
        sectors.add(introduction);
        sectors.add(essay);
        sectors.add(conclusion);
        sectors.add(references);
        sectors.add(appendix);
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

    private void checkPageSize(ArrayList<Error> errors) {
        var sector = document.getDocument().getBody().getSectPr();
        var pageSize = sector.getPgSz();

        var width = getLongPixels(pageSize.getW());
        var height = getLongPixels(pageSize.getH());
        if (width != params.pageWidth || height != params.pageHeight) {
            errors.add(new Error(-1, -1, ErrorType.INCORRECT_PAGE_SIZE));
        }
    }

    private void checkPageMargins(ArrayList<Error> errors) {
        var sector = document.getDocument().getBody().getSectPr();
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

    private void findSectors(ArrayList<Error> errors) {
        var s0 = new Pair<>(0, 0);
        var s1 = collectSector(s0, errors);
        var s2 = collectSector(s1, errors);
        var s3 = collectSector(s2, errors);
        var s4 = collectSector(s3, errors);
        var s5 = collectSector(s4, errors);
        var s6 = collectSector(s5, errors);
        collectSector(s6, errors);
    }

    private Pair<Integer, Integer> collectSector(Pair<Integer, Integer> coordinates, List<Error> errors) {
        var paragraph = coordinates.getKey();
        var sectorId = coordinates.getValue();
        var paragraphs = document.getParagraphs();
        for (; paragraph < paragraphs.size(); paragraph++) {
            var text = paragraphs.get(paragraph).getText();
            // are we collecting last sector? (he hasn't got next header)
            if (keywords.allKeywords.size() + 1 == sectorId) {
                sectors.get(sectorId).add(paragraphs.get(paragraph));
                continue;
            }
            var words = text.split("\s+");
            // did we find some header?
            if (words.length > 0 && words.length <= keywords.getMaxLength()
                    && keywords.allKeywordsFlat.contains(text.toUpperCase(Locale.ROOT))) {
                // yes, some header is here
                var error = new Error(paragraph, -1, ErrorType.INCORRECT_SECTORS);
                for (int i = sectorId + 1; i <= keywords.allKeywords.size(); i++) {
                    // what sector is this header?
                    if (keywords.allKeywords.get(i - 1).contains(text.toUpperCase(Locale.ROOT))) {
                        // we found next sector
                        //todo checkSectorHeader()
                        paragraph++;
                        return new Pair<>(paragraph, i);
                    } else if (!errors.contains(error)) {
                        // it is not next sector! error!
                        errors.add(error);
                    }
                }
            } else {
                //it's not a header
                sectors.get(sectorId).add(paragraphs.get(paragraph));
            }
        }
        return null;
    }

    private Map<String, Object> findDefaultStyles() {
        var globalStyles = new HashMap<String, Object>();
        try {
            var paragraph = getXmlFromField(document.getStyles().getDefaultParagraphStyle(), "ppr");
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
    */

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

    public List<Error> runStyleCheck() {
        var errors = new ArrayList<Error>();
        findSectors(errors);
        checkPageSize(errors);
        checkPageMargins(errors);

        return errors;
    }
}
