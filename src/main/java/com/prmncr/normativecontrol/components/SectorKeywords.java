package com.prmncr.normativecontrol.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

@Component
public class SectorKeywords {
    @Value("#{'${document.sectors.keywords.contents}'.split(',')}")
    public List<String> contents;
    @Value("#{'${document.sectors.keywords.introduction}'.split(',')}")
    public List<String> introduction;
    @Value("#{'${document.sectors.keywords.essay}'.split(',')}")
    public List<String> essay;
    @Value("#{'${document.sectors.keywords.conclusion}'.split(',')}")
    public List<String> conclusion;
    @Value("#{'${document.sectors.keywords.references}'.split(',')}")
    public List<String> references;
    @Value("#{'${document.sectors.keywords.appendix}'.split(',')}")
    public List<String> appendix;

    public List<String> allKeywordsFlat;
    public List<List<String>> allKeywords;

    private int maxLength = -1;

    @PostConstruct
    public void init() {
        allKeywordsFlat = new ArrayList<>();
        allKeywordsFlat.addAll(contents);
        allKeywordsFlat.addAll(introduction);
        allKeywordsFlat.addAll(essay);
        allKeywordsFlat.addAll(conclusion);
        allKeywordsFlat.addAll(references);
        allKeywordsFlat.addAll(appendix);

        allKeywords = new ArrayList<>();
        allKeywords.add(contents);
        allKeywords.add(introduction);
        allKeywords.add(essay);
        allKeywords.add(conclusion);
        allKeywords.add(references);
        allKeywords.add(appendix);
    }

    public int getMaxLength() {
        if (maxLength == -1) {
            maxLength = Math.max(
                    Math.max(Math.max(findLongest(contents), findLongest(introduction)),
                            Math.max(findLongest(essay), findLongest(conclusion))),
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
