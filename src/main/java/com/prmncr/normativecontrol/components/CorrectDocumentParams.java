package com.prmncr.normativecontrol.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class CorrectDocumentParams {
    @Value("${document.page.width}")
    public BigInteger pageWidth;
    @Value("${document.page.height}")
    public BigInteger pageHeight;

    @Value("${document.page.margin.top}")
    public BigInteger pageMarginTop;
    @Value("${document.page.margin.right}")
    public BigInteger pageMarginRight;
    @Value("${document.page.margin.bottom}")
    public BigInteger pageMarginBottom;
    @Value("${document.page.margin.left}")
    public BigInteger pageMarginLeft;
}
