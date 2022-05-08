package com.maeasoftworks.normativecontrol.dtos.enums

enum class ErrorType {
    // page size
    PAGE_WIDTH_IS_INCORRECT,
    PAGE_HEIGHT_IS_INCORRECT,

    // page margins
    PAGE_MARGIN_TOP_IS_INCORRECT,
    PAGE_MARGIN_LEFT_IS_INCORRECT,
    PAGE_MARGIN_BOTTOM_IS_INCORRECT,
    PAGE_MARGIN_RIGHT_IS_INCORRECT,

    // chapters
    CHAPTER_NO_ONE_CHAPTER_FOUND,
    CHAPTER_UNDEFINED_CHAPTER,
    CHAPTER_FRONT_PAGE_NOT_FOUND,
    CHAPTER_ANNOTATION_NOT_FOUND,
    CHAPTER_CONTENTS_NOT_FOUND,
    CHAPTER_INTRODUCTION_NOT_FOUND,
    CHAPTER_BODY_NOT_FOUND,
    CHAPTER_CONCLUSION_NOT_FOUND,
    CHAPTER_REFERENCES_NOT_FOUND,
    CHAPTER_APPENDIX_NOT_FOUND,
    CHAPTER_FRONT_PAGE_POSITION_MISMATCH,
    CHAPTER_ANNOTATION_POSITION_MISMATCH,
    CHAPTER_CONTENTS_POSITION_MISMATCH,
    CHAPTER_INTRODUCTION_POSITION_MISMATCH,
    CHAPTER_BODY_POSITION_MISMATCH,
    CHAPTER_CONCLUSION_POSITION_MISMATCH,
    CHAPTER_REFERENCES_POSITION_MISMATCH,
    CHAPTER_APPENDIX_POSITION_MISMATCH,

    //                      common                                  header                          regular text                            whitespace
    /*alignment*/                                                   TEXT_HEADER_ALIGNMENT,          TEXT_REGULAR_INCORRECT_ALIGNMENT,       TEXT_WHITESPACE_ALIGNMENT,
    /*uppercase*/                                                   TEXT_HEADER_NOT_UPPERCASE,      /*todo*/                                /*todo*/
    /*bold*/                                                        /*todo*/                        TEXT_REGULAR_WAS_BOLD,                  TEXT_WHITESPACE_BOLD,
    /*font*/                TEXT_COMMON_FONT,                                                                                               TEXT_WHITESPACE_FONT,
    /*color*/               TEXT_COMMON_INCORRECT_COLOR,                                                                                    TEXT_WHITESPACE_TEXT_COLOR,
    /*underline*/           TEXT_COMMON_UNDERLINED,                                                                                         TEXT_WHITESPACE_UNDERLINED,
    /*font size*/           TEXT_COMMON_INCORRECT_FONT_SIZE,                                                                                TEXT_WHITESPACE_INCORRECT_FONT_SIZE,
    /*italic*/              TEXT_COMMON_ITALIC_TEXT,                                                                                        TEXT_WHITESPACE_ITALIC,
    /*strikethrough*/       TEXT_COMMON_STRIKETHROUGH,                                                                                      TEXT_WHITESPACE_STRIKETHROUGH,
    /*highlight*/           TEXT_COMMON_HIGHLIGHT,                                                                                          TEXT_WHITESPACE_HIGHLIGHT,
    /*text direction*/      TEXT_COMMON_INCORRECT_DIRECTION,                                                                                TEXT_WHITESPACE_INCORRECT_DIRECTION,
    /*border*/              TEXT_COMMON_BORDER,                                                                                             TEXT_WHITESPACE_BORDER,
    /*background fill*/     TEXT_COMMON_BACKGROUND_FILL,                                                                                    TEXT_WHITESPACE_BACKGROUND_FILL,
    /**/
    /**/
    /**/
    /**/

    // annotation
    ANNOTATION_MUST_NOT_CONTAINS_MEDIA,
}