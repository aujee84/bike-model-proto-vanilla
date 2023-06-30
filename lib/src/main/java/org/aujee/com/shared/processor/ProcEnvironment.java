package org.aujee.com.shared.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public enum ProcEnvironment {
    ON_INIT;

    private final Messager messager;
    private final Filer filer;
    private final Elements elementUtils;
    private final Types typeUtils;

    ProcEnvironment() {
        ProcessingEnvironment roundEnvironment = StartUpProcessor.getEnvironment();
        messager = roundEnvironment.getMessager();
        filer = roundEnvironment.getFiler();
        elementUtils = roundEnvironment.getElementUtils();
        typeUtils = roundEnvironment.getTypeUtils();
    }

    public Filer filer() {
        return filer;
    }

    public Elements elementUtils() {
        return elementUtils;
    }

    public Types typeUtils() {
        return typeUtils;
    }

    Messager messager() {
        return messager;
    }
}
