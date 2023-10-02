/*
 * Copyright 2021-2023 Onsiea Studio some rights reserved.
 *
 * This file is part of Ludart Game Framework project developed by Onsiea Studio.
 * (https://github.com/OnsieaStudio/Ludart)
 *
 * Ludart is [licensed]
 * (https://github.com/OnsieaStudio/Ludart/blob/main/LICENSE) under the terms of
 * the "GNU General Public License v3.0" (GPL-3.0).
 * https://github.com/OnsieaStudio/Ludart/wiki/License#license-and-copyright
 *
 * Ludart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * Ludart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ludart. If not, see <https://www.gnu.org/licenses/>.
 *
 * Any reproduction or alteration of this project may reference it and utilize its name and derivatives, provided it clearly states its modification status and includes a link to the original repository. Usage of all names belonging to authors, developers, and contributors remains subject to copyright.
 * in other cases prior written authorization is required for using names such as "Onsiea," "Ludart," or any names derived from authors, developers, or contributors for product endorsements or promotional purposes.
 *
 *
 * @Author : Seynax (https://github.com/seynax)
 * @Organization : Onsiea Studio (https://github.com/OnsieaStudio)
 */

package fr.onsiea.ludart.processor;

import com.google.auto.service.AutoService;
import fr.onsiea.ludart.common.modules.IModule;
import fr.onsiea.ludart.common.modules.processor.LudartModule;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes(value = "*")
@SupportedSourceVersion(SourceVersion.RELEASE_20)
@AutoService(Processor.class)
public class ModuleProcessor extends AbstractProcessor
{
	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		var filer        = processingEnv.getFiler();
		var messager     = processingEnv.getMessager();
		var elementUtils = processingEnv.getElementUtils();
		var typeUtils    = processingEnv.getTypeUtils();

		messager.printMessage(Kind.NOTE, "Module Processor is loaded. Modules detection started.");

		var annotatedElements = roundEnv.getElementsAnnotatedWith(LudartModule.class);
		var annotatedClasses  = annotatedElements.stream().collect(Collectors.partitioningBy(element -> (element.getKind().isClass())));
		var classes           = annotatedClasses.get(true);

		if (classes.size() == 0)
		{
			return true;
		}

		var groupedModulesClasses = new HashMap<String, List<Element>>();
		var packages              = new ArrayList<String>();

		for (var clazz : classes)
		{
			var comparable = typeUtils.erasure(elementUtils.getTypeElement(IModule.class.getName()).asType());

			if (!typeUtils.isAssignable(clazz.asType(), comparable))
			{
				continue;
			}

			var moduleElement = elementUtils.getModuleOf(clazz);

			String groupName = null;
			if (moduleElement == null)
			{
				groupName = moduleElement.getSimpleName().toString();
			}
			else
			{
				var packageName = processingEnv.getElementUtils().getPackageOf(clazz).toString();
				if (packageName == null)
				{
					groupName = clazz.getSimpleName().toString();
				}
				else
				{
					var packageWords = packageName.split("\\.");

					String moreEquivalent            = null;
					int    moreEquivalentWordsNumber = 0;
					for (var otherPackage : packages)
					{
						var otherPackageWords = otherPackage.split("\\.");

						var max = Math.min(packageWords.length, otherPackageWords.length);

						for (int i = 0; i < max; i++)
						{
							var fromWord = packageWords[i];
							var toWord   = otherPackageWords[i];

							if (fromWord == null || toWord == null)
							{
								continue;
							}

							if (!fromWord.contentEquals(toWord))
							{
								if (i > moreEquivalentWordsNumber)
								{
									moreEquivalentWordsNumber = i;
									moreEquivalent            = toWord;
								}
								break;
							}
						}
					}

					if (moreEquivalent == null)
					{
						packages.add(packageName);
						groupName = packageName;
					}
					else
					{
						groupName = moreEquivalent;
					}
				}
			}

			var elements = groupedModulesClasses.get(groupName);
			if (elements == null)
			{
				elements = new ArrayList<>();

				groupedModulesClasses.put(groupName, elements);
			}
			elements.add(clazz);
		}

		if (groupedModulesClasses.size() == 0)
		{
			return true;
		}

		for (var moduleEntry : groupedModulesClasses.entrySet())
		{
			var    groupName = moduleEntry.getKey();
			var    lastDot   = groupName.lastIndexOf('.');
			String className = null;
			if (lastDot > 0)
			{
				var lastElement = groupName.substring(lastDot + 1);
				className = lastElement.substring(0, 1).toUpperCase() + lastElement.substring(1) + "ModulesDescriptor";
			}

			String packageName   = "fr.onsiea.ludart.modules.processor";
			var    qualifiedName = packageName + "." + className;

			var modulesClasses = moduleEntry.getValue();

			if (modulesClasses == null || modulesClasses.size() == 0)
			{
				continue;
			}

			try (PrintWriter out = new PrintWriter((filer.createSourceFile(qualifiedName).openOutputStream())))
			{
				if (packageName != null)
				{
					out.print("package ");
					out.print(packageName);
					out.println(";");
					out.println();
				}
				out.println("import fr.onsiea.ludart.common.modules.schema.ModulesSchemas;");
				out.println();

				for (var moduleClass : modulesClasses)
				{
					out.println("import " + ((TypeElement) moduleClass).getQualifiedName() + ";");
				}
				out.println();

				out.print("public class ");
				out.println(className);
				out.println("{");
				out.println("\tpublic final static void resolveSchema(ModulesSchemas.Builder schemasBuilderIn)");
				out.println("\t{");

				for (var moduleClass : modulesClasses)
				{
					var annotation = moduleClass.getAnnotation(LudartModule.class);

					out.print("\t\tschemasBuilderIn.batch(");
					out.print(moduleClass.getSimpleName());
					out.print(".class, () -> new ");
					out.print(moduleClass.getSimpleName());
					out.print("()");

					List<? extends TypeMirror> dependencies = getTypeMirrorFromAnnotationValue(() -> annotation.dependencies());
					if (dependencies != null && dependencies.size() > 0)
					{
						out.print(", ");
						int i = 0;
						for (var dependency : dependencies)
						{
							String dependencyClassName = null;

							if (dependency.getKind().equals(TypeKind.DECLARED))
							{
								dependencyClassName = ((DeclaredType) dependency).asElement().getSimpleName().toString();
							}
							else
							{
								dependencyClassName = dependency.toString();
							}
							out.print(dependencyClassName);
							out.print(".class");

							i++;
							if (i < dependencies.size())
							{
								out.print(", ");
							}
						}
					}
					out.println(");");
				}

				out.println("\t}");
				out.print("}");
			}
			catch (IOException eIn)
			{
				throw new RuntimeException(eIn);
			}
		}

		return true;
	}

	public static List<? extends TypeMirror> getTypeMirrorFromAnnotationValue(GetClassValue classValueIn)
	{
		try
		{
			classValueIn.execute();
		}
		catch (MirroredTypesException ex)
		{
			return ex.getTypeMirrors();
		}

		return null;
	}

	@FunctionalInterface
	public interface GetClassValue
	{
		void execute() throws MirroredTypesException;
	}
}