package com.prmncr.normativecontrol.components;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class SectorKeywords {
    @Getter
    private List<String> allKeywordsFlat;
    @Getter
    private List<List<String>> allKeywords;

    @Value("#{'${document.sectors.keywords.annotation}'.split(',')}")
    private List<String> annotation;
    @Value("#{'${document.sectors.keywords.contents}'.split(',')}")
    private List<String> contents;
    @Value("#{'${document.sectors.keywords.introduction}'.split(',')}")
    private List<String> introduction;
    @Value("#{'${document.sectors.keywords.body}'.split(',')}")
    private List<String> body;
    @Value("#{'${document.sectors.keywords.conclusion}'.split(',')}")
    private List<String> conclusion;
    @Value("#{'${document.sectors.keywords.references}'.split(',')}")
    private List<String> references;
    @Value("#{'${document.sectors.keywords.appendix}'.split(',')}")
    private List<String> appendix;

    private int maxLength = -1;

    @PostConstruct
    public void init() {
        allKeywordsFlat = new ArrayList<>();

        allKeywordsFlat.addAll(annotation);
        allKeywordsFlat.addAll(contents);
        allKeywordsFlat.addAll(introduction);
        allKeywordsFlat.addAll(body);
        allKeywordsFlat.addAll(conclusion);
        allKeywordsFlat.addAll(references);
        allKeywordsFlat.addAll(appendix);

        allKeywords = new ArrayList<>();
        allKeywords.add(annotation);
        allKeywords.add(contents);
        allKeywords.add(introduction);
        allKeywords.add(body);
        allKeywords.add(conclusion);
        allKeywords.add(references);
        allKeywords.add(appendix);
    }

    public int getMaxLength() {
        if (maxLength == -1) {
            maxLength = Math.max(
                    Math.max(Math.max(findLongest(contents), findLongest(introduction)),
                            Math.max(findLongest(annotation), findLongest(conclusion))),
                    Math.max(findLongest(references), findLongest(appendix))
            );
        }
        return maxLength;
    }

    private int findLongest(List<String> keywords) {
        var max = -1;
        for (var keyword : keywords) {
            var len = keyword.split("\s+").length;
            if (len > max) {
                max = len;
            }
        }
        return max;
    }
}
