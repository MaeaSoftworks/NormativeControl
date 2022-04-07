package com.prmncr.normativecontrol.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CorrectDocumentParams {
    @Value("${document.page.width}")
    public long pageWidth;
    @Value("${document.page.height}")
    public long pageHeight;

    @Value("${document.page.margin.top}")
    public double pageMarginTop;
    @Value("${document.page.margin.right}")
    public double pageMarginRight;
    @Value("${document.page.margin.bottom}")
    public double pageMarginBottom;
    @Value("${document.page.margin.left}")
    public double pageMarginLeft;
}
