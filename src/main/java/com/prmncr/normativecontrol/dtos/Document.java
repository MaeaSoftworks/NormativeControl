package com.prmncr.normativecontrol.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Document {
    @Getter
    private final String id;
    @Getter
    private final byte[] file;
    @Getter
    @Setter
    private State state = State.QUEUE;
    @Getter
    @Setter
    private Result result;
}
