package fr.onsiea.ludart.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes(value = "*")
@SupportedSourceVersion(SourceVersion.RELEASE_20)
@AutoService(Processor.class)
public class ModuleProcessor extends AbstractProcessor
{
	@Override
	// Work in progress
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		Messager messager = this.processingEnv.getMessager();

		messager.printMessage(Kind.NOTE, "Annotation compilation test ");

		for (TypeElement te : annotations)
		{
			messager.printMessage(Kind.NOTE, "Annotation test " + te.getQualifiedName());

			for (Element element : roundEnv.getElementsAnnotatedWith(te))
			{
				messager.printMessage(Kind.NOTE, "  Element test " + element.getSimpleName());
			}
		}

		return true;
	}
}