package com.example;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Author: shiguang
 * @Date: 2021/8/14
 * @Description:
 **/
@SupportedAnnotationTypes({"com.example.BoundInfo"})
public class MyClassProcessor extends AbstractProcessor {

    private Messager messager;
    private JavacElements elementUtils;
    private Filer filer;

    private TreeMaker treeMaker;
    private JavacProcessingEnvironment jcEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = (JavacElements) processingEnvironment.getElementUtils();
        if(processingEnvironment instanceof JavacProcessingEnvironment){
            jcEnv = (JavacProcessingEnvironment) processingEnvironment;
            //出现过的问题 java: java.lang.ClassCastException: com.sun.proxy.$Proxy25 cannot be cast to com.sun.tools.javac.processing.JavacProcessingEnvironment
        }else{
            try{
                Field f =processingEnvironment.getClass().getDeclaredField("delegate");
                f.setAccessible(true);
                jcEnv = (JavacProcessingEnvironment) f.get(processingEnvironment);
            }catch(Exception e){

            }
        }
        filer = processingEnvironment.getFiler();
        treeMaker = TreeMaker.instance(jcEnv.getContext());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        generateFile(annotations,roundEnvironment);
        coverFile(annotations,roundEnvironment);
        return true;

    }

    private void coverFile(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE,"cover 日志开始---------------");
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(BoundInfo.class);
        for (Element element : elementsAnnotatedWith) {
            if(element.getKind() == ElementKind.METHOD) {
                JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) elementUtils.getTree(element);

                treeMaker.pos = jcMethodDecl.pos;
                jcMethodDecl.body = treeMaker.Block(0, List.of(
                        treeMaker.Exec(
                                treeMaker.Apply(
                                        List.<JCTree.JCExpression>nil(),
                                        treeMaker.Select(
                                                treeMaker.Select(
                                                        treeMaker.Ident(
                                                                elementUtils.getName("System")
                                                        ),
                                                        elementUtils.getName("out")
                                                ),
                                                elementUtils.getName("println")
                                        ),
                                        List.<JCTree.JCExpression>of(
                                                treeMaker.Literal("Hello, world!!!")
                                        )
                                )
                        ),
                        jcMethodDecl.body
                ));
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE,"cover 日志结束---------------");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
    //修改文件，生成新的文件，修改源文件
    private void generateFile(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment){
        messager.printMessage(Diagnostic.Kind.NOTE,"generate 日志开始---------------");

        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(BoundInfo.class);
        for (Element element:elementsAnnotatedWith) {
            if(element.getKind() == ElementKind.CLASS){
                TypeElement typeElement = (TypeElement) element;
                PackageElement packageElement = elementUtils.getPackageOf(element);
                String packagePath = packageElement.getQualifiedName().toString();
                String className = typeElement.getSimpleName().toString();
                resolveGenerate(typeElement, packagePath, className);
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE,"generate 日志结束---------------");
    }


    private void resolveGenerate(TypeElement typeElement, String packagePath, String className) {
        try {
            JavaFileObject sourceFile = filer.createSourceFile(packagePath + "." + className + "_ViewBinding", typeElement);
            Writer writer = sourceFile.openWriter();
            writer.write("package  "+ packagePath +";\n");
            writer.write("import  "+ packagePath +"."+ className +";\n");
            writer.write("public class "+ className +"_ViewBinding"+"  { \n");
            writer.write("\n");
            writer.append("       public "+ className +"  target;\n");
            writer.write("\n");
            writer.append("}");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
