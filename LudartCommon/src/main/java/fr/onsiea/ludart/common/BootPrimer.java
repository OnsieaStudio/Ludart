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

package fr.onsiea.ludart.common;

import fr.onsiea.ludart.common.modules.processor.LudartModulesDescriptor;
import fr.onsiea.ludart.common.modules.schema.ModulesSchemas;
import fr.onsiea.ludart.common.modules.schema.ModulesSchemas.Builder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;

public class BootPrimer
{
	public final ModulesSchemas boot(Builder schemasBuilderIn) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		for (var modulesDescriptor : findAllClassesUsingReflectionsLibrary("fr.onsiea.ludart.modules.processor.descriptors"))
		{
			if (modulesDescriptor == null || !modulesDescriptor.accessFlags().contains(AccessFlag.PUBLIC))
			{
				continue;
			}

			var annotation = modulesDescriptor.getAnnotation(LudartModulesDescriptor.class);
			if (annotation == null)
			{
				continue;
			}

			var declaredMethods = modulesDescriptor.getDeclaredMethods();
			int realMethodCount = 0;
			for (var declared : declaredMethods)
			{
				if (!declared.isSynthetic())
				{
					realMethodCount++;
				}
			}
			if (declaredMethods == null || declaredMethods.length == 0 || realMethodCount > 1)
			{
				throw new IllegalAccessException("[ERROR] Boot primer : class, present in the package \"fr.onsiea.ludart.modules.processor.descriptors\" defined as being a \"modules descriptor\" via the annotation @LudartModulesDescriptor must contain only public and static" +
				                                 " " +
				                                 "method, named \"resolve\" receiving the parameter \"" + schemasBuilderIn.getClass().getName() + "\" is accepted  ! \"" + modulesDescriptor.getSimpleName() + "\"");
			}

			var declaredMethod = declaredMethods[0];
			if (declaredMethod == null)
			{
				throw new IllegalAccessException("[ERROR] Boot primer : \"" + modulesDescriptor.getSimpleName() + "\", declared methods is null !");
			}

			if (!declaredMethod.getName().contentEquals("resolve"))
			{
				throw new IllegalAccessException("[ERROR] Boot primer : ludart modules descriptor class \"" + modulesDescriptor.getSimpleName() + "\", declared method must be named \"resolve\"  !");
			}

			if (declaredMethod == null || !declaredMethod.accessFlags().contains(AccessFlag.STATIC) || !declaredMethod.accessFlags().contains(AccessFlag.PUBLIC))
			{
				throw new IllegalAccessException("[ERROR] Boot primer : ludart modules descriptor class \"" + modulesDescriptor.getSimpleName() + "\", resolve method isn't static or accessible !");
			}

			if (declaredMethod.getParameterCount() != 1)
			{
				throw new IllegalAccessException("[ERROR] Boot primer : ludart modules descriptor class \"" + modulesDescriptor.getSimpleName() + "\", resolve method must receive one \"" + schemasBuilderIn.getClass().getName() + "\"  parameter !");
			}

			var parametersTypes = declaredMethod.getParameterTypes();
			if (!parametersTypes[0].isInstance(schemasBuilderIn))
			{
				String parameters = "(";
				int    i          = 0;
				for (var parameter : parametersTypes)
				{
					parameters += parameter.getTypeName();

					i++;
					if (i < parametersTypes.length)
					{
						parameters += ", ";
					}
				}
				parameters += ")[" + parametersTypes.length + "]";

				throw new IllegalAccessException("[ERROR] Boot primer : Modules descriptor, resolver methods found but parameters isn't correct ! \"" + modulesDescriptor.getSimpleName() + "\" -> " + declaredMethod.getName() + parameters);
			}

			declaredMethod.invoke(null, schemasBuilderIn);
		}

		return schemasBuilderIn.build();
	}

	public Set<Class> findAllClassesUsingReflectionsLibrary(String packageName)
	{
		Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
		return reflections.getSubTypesOf(Object.class)
				.stream()
				.collect(Collectors.toSet());
	}

	public Set<Class> findAllClassesUsingClassLoader(String packageName)
	{
		InputStream stream = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines()
				.filter(line -> line.endsWith(".class"))
				.map(line -> getClass(line, packageName))
				.collect(Collectors.toSet());
	}

	private Class getClass(String className, String packageName)
	{
		try
		{
			return Class.forName(packageName + "."
			                     + className.substring(0, className.lastIndexOf('.')));
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}