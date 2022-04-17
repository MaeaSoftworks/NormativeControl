package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.components.CorrectDocumentParams;
import com.prmncr.normativecontrol.components.SectorKeywords;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.ErrorType;
import com.prmncr.normativecontrol.dtos.Prs;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.docx4j.TextUtils;
import org.docx4j.model.styles.StyleTree;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import java.math.BigInteger;
import java.util.*;

public class DocumentParser {
    private static final int POINTS = 20;
    private final CorrectDocumentParams params;
    private final SectorKeywords keywords;
    private final MainDocumentPart mainDocumentPart;
    private final WordprocessingMLPackage document;
    @Getter
    private final List<List<Object>> sectors = new ArrayList<>();
    @Getter
    private final List<Error> errors = new ArrayList<>();
    private final StyleTree styleTree;
    private final Map<String, Prs> styles = new HashMap<>();

    public DocumentParser(WordprocessingMLPackage document, CorrectDocumentParams params, SectorKeywords keywords)
            throws IllegalAccessException {
        this.document = document;
        mainDocumentPart = document.getMainDocumentPart();
        this.keywords = keywords;
        this.params = params;
        this.styleTree = document.getMainDocumentPart().getStyleTree();
        for (int i = 0; i <8; i++) {
            sectors.add(new ArrayList<>());
        }
        createStyleMap();
    }

    public List<Error> runStyleCheck() {
        findSectors(0, 0);
        checkPageSize();
        checkPageMargins();
        return errors;
    }

    private long getLongPixels(Object points) {
        return ((BigInteger) points).longValue() / POINTS;
    }

    public void checkPageSize() {
        val sector = mainDocumentPart.getJaxbElement().getBody().getSectPr();
        val pageSize = sector.getPgSz();

        val width = getLongPixels(pageSize.getW());
        val height = getLongPixels(pageSize.getH());
        if (width != params.pageWidth || height != params.pageHeight) {
            errors.add(new Error(-1, -1, ErrorType.INCORRECT_PAGE_SIZE));
        }
    }

    public void checkPageMargins() {
        val sector = mainDocumentPart.getJaxbElement().getBody().getSectPr();
        val pageMargins = sector.getPgMar();
        val marginTop = getLongPixels(pageMargins.getTop());
        val marginLeft = getLongPixels(pageMargins.getLeft());
        val marginBottom = getLongPixels(pageMargins.getBottom());
        val marginRight = getLongPixels(pageMargins.getRight());

        if (marginTop != params.pageMarginTop || marginRight != params.pageMarginRight
                || marginBottom != params.pageMarginBottom || marginLeft != params.pageMarginLeft) {
            errors.add(new Error(-1, -1, ErrorType.INCORRECT_PAGE_MARGINS));
        }
    }

    public void findSectors() {
        findSectors(0, 0);
    }

    private void findSectors(int paragraphId, int sectorId) {
        val paragraphs = mainDocumentPart.getContent();
        for (var paragraph = paragraphId; paragraph < paragraphs.size(); paragraph++) {
            // are we collecting last sector? (he hasn't got next header)
            if (keywords.getAllKeywords().size() + 1 == sectorId) {
                sectors.get(sectorId).add(paragraphs.get(paragraph));
                continue;
            }
            if (paragraphs.get(paragraph) instanceof P p && p.getContent().size() <= 2) {
                val text = TextUtils.getText(paragraphs.get(paragraph));
                // did we find some header?
                if (isHeader(paragraph, text)) {
                    // yes, some header is here
                    val error = new Error(paragraph, -1, ErrorType.INCORRECT_SECTORS);
                    for (int i = sectorId + 1; i <= keywords.getAllKeywords().size(); i++) {
                        // what sector is this header?
                        if (keywords.getAllKeywords().get(i - 1).contains(text.toUpperCase(Locale.ROOT))) {
                            // we found next sector
                            checkHeaderStyle(paragraph);
                            paragraph++;
                            findSectors(paragraph, i);
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
        val words = text.split("\s+");
        if (words.length == 0 || words.length > keywords.getMaxLength()) {
            return false;
        }
        val paragraphs = mainDocumentPart.getContent();
        if (paragraph > 0) {
            if (paragraphs.get(paragraph - 1) instanceof P previous) {
                var runs = previous.getContent();
                if (!runs.isEmpty()) {
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
        return keywords.getAllKeywordsFlat().contains(text.toUpperCase(Locale.ROOT));
    }

    public void checkHeaderStyle(int paragraph) {
        var p = (P) mainDocumentPart.getContent().get(paragraph);
        if (!p.getPPr().getPStyle().getVal().equals("Heading1")) {
            errors.add(new Error(paragraph, 0, ErrorType.BUILT_IN_HEADER_STYLE_IS_NOT_USED));
        }
        var run = (R) p.getContent().get(0);
        var text = TextUtils.getText(run);
        if (!text.toUpperCase().equals(text)) {
            errors.add(new Error(paragraph, 0, ErrorType.HEADER_IS_NOT_UPPER_CASE));
        }
        checkInvalidProperties(run, paragraph);
    }

    private void checkInvalidProperties(R run, int paragraph) {
        if (run.getRPr() == null) {
            return;
        }
        if (!run.getRPr().getRFonts().getAscii().equals("Times New Roman")) {
            errors.add(new Error(paragraph, 0, ErrorType.INCORRECT_HEADER_FONT));
        }
        if (!Objects.equals(run.getRPr().getColor().getVal(), "FFFFFF")
                && !Objects.equals(run.getRPr().getColor().getVal(), "auto")) {
            errors.add(new Error(paragraph, 0, ErrorType.INCORRECT_TEXT_COLOR));
        }
        if (run.getRPr().getSz().getVal().intValue() / 2 != 14) {
            errors.add(new Error(paragraph, 0, ErrorType.INCORRECT_TEXT_COLOR));
        }
    }

    private void createStyleMap() throws IllegalAccessException {
        val nodesP = styleTree.getParagraphStylesTree().toList();
        // todo add character style props overriting rules
        // val nodesR = styleTree.getCharacterStylesTree().toList();
        RPr defaultRPr = null;
        PPr defaultPPr = null;
        for (val style : nodesP) {
            if (FieldUtils.readField(style, "name", true).equals("DocDefaults")) {
                defaultRPr = style.data.getStyle().getRPr();
                defaultPPr = style.data.getStyle().getPPr();
            } else {
                this.styles.put((String) FieldUtils.readField(style, "name", true),
                        new Prs(style.data.getStyle().getRPr() == null ? defaultRPr : style.data.getStyle().getRPr(),
                                style.data.getStyle().getPPr() == null ? defaultPPr : style.data.getStyle().getPPr()));
            }
        }
    }

    public RPr detectRStyle(int paragraph, int run) {
        val rpr = new RPr();
        val r = (R) ((P) mainDocumentPart.getContent().get(paragraph)).getContent().get(run);

        // font size
        if (r.getRPr() == null) {
            if (((P) r.getParent()).getPPr().getRPr() == null) {
                // get style from style tree
            } else {
                // get from p
            }
        } else {
            if (r.getRPr().getSz() != null) {
                rpr.setSz(r.getRPr().getSz());
            } else {
                if (((P) r.getParent()).getPPr().getRPr().getSz() != null) {
                    rpr.setSz(((P) r.getParent()).getPPr().getRPr().getSz());
                } else {
                    if (styleTree.getCharacterStylesTree().get(((P) r.getParent()).getPPr().getPStyle().getVal()) != null) {

                    }
                }
            }
        }
        return rpr;
    }
}
