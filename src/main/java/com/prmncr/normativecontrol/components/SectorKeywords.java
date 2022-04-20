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
    private final List<String> body = null;
    private List<String> allKeywords;

    private List<List<String>> keywordsBySector;
    @Value("#{'${document.sectors.keywords.annotation}'.split(',')}")
    @Getter
    private List<String> annotation;
    @Value("#{'${document.sectors.keywords.contents}'.split(',')}")
    @Getter
    private List<String> contents;
    @Value("#{'${document.sectors.keywords.introduction}'.split(',')}")
    @Getter
    private List<String> introduction;
    @Value("#{'${document.sectors.keywords.conclusion}'.split(',')}")
    @Getter
    private List<String> conclusion;
    @Value("#{'${document.sectors.keywords.references}'.split(',')}")
    @Getter
    private List<String> references;
    @Value("#{'${document.sectors.keywords.appendix}'.split(',')}")
    @Getter
    private List<String> appendix;
    private int maxLength = -1;

    public List<String> getAllKeywords() {
        return allKeywords;
    }

    public List<List<String>> getKeywordsBySector() {
        return keywordsBySector;
    }

    @PostConstruct
    public void init() {
        allKeywords = new ArrayList<>();

        // front page skip
        allKeywords.addAll(annotation);
        allKeywords.addAll(contents);
        allKeywords.addAll(introduction);
        // body skip
        allKeywords.addAll(conclusion);
        allKeywords.addAll(references);
        allKeywords.addAll(appendix);

        keywordsBySector = new ArrayList<>();
        keywordsBySector.add(new ArrayList<>()); // front page
        keywordsBySector.add(annotation);
        keywordsBySector.add(contents);
        keywordsBySector.add(introduction);
        keywordsBySector.add(new ArrayList<>()); // body
        keywordsBySector.add(conclusion);
        keywordsBySector.add(references);
        keywordsBySector.add(appendix);
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

    public enum SectorOrder {
        FIRST_PAGE,
        ANNOTATION,
        CONTENTS,
        INTRODUCTION,
        BODY,
        CONCLUSION,
        REFERENCES,
        APPENDIX
    }
}
