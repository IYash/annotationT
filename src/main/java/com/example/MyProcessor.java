package com.example;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @Author: shiguang
 * @Date: 2021/8/14
 * @Description: 通过定义一个annotation，在编译代码的时候，凡是用该annotation声明过的类，方法，我们都要在控制台输出他们的信息
 **/
@SupportedAnnotationTypes({"com.example.PrintMe"})
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        for(TypeElement te:annotations){
            for(Element e:roundEnv.getElementsAnnotatedWith(te)){
                messager.printMessage(Diagnostic.Kind.NOTE,"Printing: "+e.toString());
            }
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
