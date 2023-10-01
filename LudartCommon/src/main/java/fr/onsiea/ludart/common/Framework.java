/*
 *  Copyright 2021-2023 Onsiea All rights reserved.
 *
 *  This file is part of Ludart Game Framework project developed by Onsiea Studio.
 *  (https://github.com/OnsieaStudio/Ludart)
 *
 * Ludart is [licensed]
 *  (https://github.com/OnsieaStudio/Ludart/blob/main/LICENSE) under the terms of
 *  the "GNU General Public License v3.0" (GPL-3.0).
 *  https://github.com/OnsieaStudio/Ludart/wiki/License#license-and-copyright
 *
 *  Ludart is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3.0 of the License, or
 *  (at your option) any later version.
 *
 *  Ludart is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ludart. If not, see <https://www.gnu.org/licenses/>.
 *
 *  Neither the name "Onsiea", "Ludart", or any derivative name or the
 *  names of its authors / contributors may be used to endorse or promote
 *  products derived from this software and even less to name another project or
 *  other work without clear and precise permissions written in advance.
 *
 *  @Author : Seynax (https://github.com/seynax)
 *  @Organization : Onsiea Studio (https://github.com/OnsieaStudio)
 */
package fr.onsiea.ludart.common;

import fr.onsiea.ludart.common.modules.IModule;
import fr.onsiea.ludart.common.modules.LudartModule;
import fr.onsiea.ludart.common.modules.ModuleBase;
import fr.onsiea.ludart.common.modules.schema.ModulesSchemas;
import fr.onsiea.ludart.common.modules.schema.instanced.ModulesPositionedSchemas;
import fr.onsiea.ludart.common.modules.schema.instanced.ModulesSchemasOrderSorter;
import fr.onsiea.ludart.common.modules.settings.ModuleSettings;
import fr.onsiea.tools.logger.Loggers;
import lombok.Getter;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Framework extends ModuleBase
{
	private final static String         PACKAGE_PREFIX = "fr.onsiea.engine";
	private final static ModuleSettings SETTINGS       = new ModuleSettings.Builder()
	{
		@Override
		public boolean solve()
		{
			if (this.outputsPath == null)
			{
				this.outputsPath = "outputs";
			}

			if (this.logsBasePath == null)
			{
				this.logsBasePath = this.outputsPath + "\\logs\\framework";
			}

			return super.solve();
		}

	}.build();

	public final static Loggers   LOGGERS = Framework.SETTINGS.loggers();
	private static      Framework INSTANCE;

	private final ModulesSchemas schemas;
	private final   Map<String, IModule>     modules;
	private @Getter ModulesPositionedSchemas schemasInstances;

	private Framework(ModulesSchemas schemasIn)
	{
		if (Framework.INSTANCE != null)
		{
			throw new ExceptionInInitializerError("[ERROR] Framework is already initialized");
		}

		this.schemas = schemasIn;
		this.modules = new LinkedHashMap<>();

		Framework.INSTANCE = this;
	}

	@Override
	public String name()
	{
		return "FRAMEWORK";
	}

	@Override
	public IModule startAll()
	{
		if (this.schemasInstances == null)
		{
			initialize();
		}

		this.modules.forEach((sourceModuleNameIn, moduleIn) -> moduleIn.registries().startAll());

		this.registries().atIteration().add("update", () -> this.modules.forEach((sourceModuleNameIn, moduleIn) -> moduleIn.registries().iterateAll()));

		this.registries().atStop().add("cleanup", () -> this.modules.forEach((sourceModuleNameIn, moduleIn) -> moduleIn.registries().stopAll()));

		return super.startAll();
	}

	public void initialize()
	{
		if (this.schemasInstances != null)
		{
			throw new RuntimeException("[ERROR] Framework is already initialized !");
		}

		ModulesSchemasOrderSorter schemasOrderSorter = new ModulesSchemasOrderSorter(this.schemas);
		this.schemasInstances = schemasOrderSorter.sort();
		this.schemasInstances.each((schemaInstanceIn) -> this.modules.put(schemaInstanceIn.schema().sourceModuleName(), schemaInstanceIn.schema().moduleInitializer().execute()));
	}

	public final <T extends IModule> T module(Class<T> sourceModuleClassIn)
	{
		if (sourceModuleClassIn == null)
		{
			return null;
		}

		var module = this.modules.get(sourceModuleClassIn.getSimpleName());

		if (module == null)
		{
			return null;
		}

		if (sourceModuleClassIn.isInstance(module))
		{
			return (T) module;
		}

		return null;
	}

	@Getter
	public final static class Builder
	{
		private final ModulesSchemas.Builder schemasBuilder;

		public Builder() throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException
		{
			this.schemasBuilder = new ModulesSchemas.Builder();
			initializeDefaultSchema();
		}

		private void initializeDefaultSchema() throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException
		{
			initializeDefault("ModuleClient", "client");

			initializeDefault("ModuleCore", "core");
		}

		private void initializeDefault(String moduleNameIn, String... relativePackagesNamesIn) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException
		{
			StringBuilder classPath = new StringBuilder(PACKAGE_PREFIX + ".");
			int i = 0;
			for (var relativePackageName : relativePackagesNamesIn)
			{
				classPath.append(relativePackageName);
				if (i < relativePackagesNamesIn.length - 1)
				{
					classPath.append(".");
				}
				i++;
			}
			classPath.append(".").append(moduleNameIn);

			Class<?> moduleClass = Class.forName(classPath.toString());
			if (moduleClass == null || !moduleClass.accessFlags().contains(AccessFlag.PUBLIC))
			{
				return;
			}

			var declaredMethods = moduleClass.getDeclaredMethods();
			if (declaredMethods == null)
			{
				return;
			}

			boolean hasStaticMethod = false;
			for (var declaredMethod : declaredMethods)
			{
				if (declaredMethod == null)
				{
					continue;
				}

				if (!declaredMethod.accessFlags().contains(AccessFlag.STATIC) || !declaredMethod.accessFlags().contains(AccessFlag.PUBLIC))
				{
					continue;
				}

				LOGGERS.logLn(declaredMethod.getName());
				if (declaredMethod.isAnnotationPresent(LudartModule.class))
				{
					LOGGERS.logLn("PRESENT !");
					var parametersTypes = declaredMethod.getParameterTypes();
					if (parametersTypes.length != 1 || !parametersTypes[0].isInstance(this.schemasBuilder))
					{
						throw new IllegalAccessException("[ERROR]Static schema resolver method found but parameters isn't correct ! \"" + moduleNameIn + "\"");
					}
					hasStaticMethod = true;
					LOGGERS.logLn(("FOUNDED SCHEMA RESOLVER METHOD : " + declaredMethod.getName()));

					declaredMethod.invoke(null, this.schemasBuilder);
				}
			}

			if (hasStaticMethod)
			{
				return;
			}

			if (!IModule.class.isAssignableFrom(moduleClass))
			{
				LOGGERS.logLn("[WARNING] \"" + moduleNameIn + "\" isn't module !");

				return;
			}

			var constructor = moduleClass.getConstructor();

			if (constructor == null)
			{
				throw new IllegalAccessException("[ERROR] Cannot find static schema resolver method AND cannot get constructor without parameters to create module initializer ! \"" + moduleNameIn + "\"");
			}

			this.schemasBuilder.batch(moduleClass, () ->
			{
				try
				{
					return (IModule) constructor.newInstance();
				}
				catch (InstantiationException | IllegalAccessException | InvocationTargetException eIn)
				{
					throw new RuntimeException(eIn);
				}
			});
		}

		public Framework build()
		{
			return new Framework(this.schemasBuilder.build());
		}
	}
}