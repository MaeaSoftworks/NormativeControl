package ru.maeasoftworks.normativecontrol.core.implementations.ufru

interface FullVerifier<T>:
    FrontPageVerifier<T>,
    AnnotationVerifier<T>,
    ContentsVerifier<T>,
    IntroductionVerifier<T>,
    BodyVerifier<T>,
    ConclusionVerifier<T>,
    ReferencesVerifier<T>,
    AppendixVerifier<T>