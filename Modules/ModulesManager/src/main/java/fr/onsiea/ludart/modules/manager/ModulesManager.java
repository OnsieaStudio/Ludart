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
package fr.onsiea.ludart.modules.manager;

import fr.onsiea.ludart.common.modules.IModule;
import fr.onsiea.ludart.common.modules.ModuleBase;
import fr.onsiea.ludart.common.modules.manager.IModulesManager;
import fr.onsiea.ludart.common.modules.processor.LudartModulesManager;
import fr.onsiea.ludart.common.modules.schema.ModulesSchemas;
import fr.onsiea.ludart.common.modules.schema.instanced.ModulesPositionedSchemas;
import fr.onsiea.ludart.common.modules.schema.instanced.ModulesSchemasOrderSorter;
import fr.onsiea.ludart.common.modules.settings.ModuleSettings;
import fr.onsiea.tools.logger.Loggers;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@LudartModulesManager
public class ModulesManager extends ModuleBase implements IModulesManager
{
	private final static ModuleSettings SETTINGS = new ModuleSettings.Builder()
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
				this.logsBasePath = this.outputsPath + "\\logs\\modulesManager";
			}

			return super.solve();
		}

	}.build();

	public final static Loggers LOGGERS = ModulesManager.SETTINGS.loggers();

	private final   ModulesSchemas           schemas;
	private final   Map<String, IModule>     moduleMap;
	private @Getter ModulesPositionedSchemas schemasInstances;

	public ModulesManager(ModulesSchemas schemasIn)
	{
		schemas   = schemasIn;
		moduleMap = new LinkedHashMap<>();
	}

	@Override
	public IModule startAll()
	{
		if (this.schemasInstances == null)
		{
			initialize();
		}

		this.schemasInstances.each((schemaInstanceIn) ->
		{
			var module = moduleMap.get(schemaInstanceIn.schema().sourceModuleClass().getSimpleName());
			if (module == null)
			{
				return;
			}

			var dependencies = schemaInstanceIn.schema().dependencies();

			IModule[] dependenciesInstances = null;
			if (dependencies != null && dependencies.length > 0)
			{
				dependenciesInstances = new IModule[dependencies.length + 1];
				int i = 0;
				for (var dependency : dependencies)
				{
					var dependencyModule = moduleMap.get(dependency);

					if (!(dependencyModule instanceof IModule))
					{
						continue;
					}

					dependenciesInstances[i] = dependencyModule;

					i++;
				}
			}
			module.dependencies(this, dependenciesInstances);
		});
		this.moduleMap.forEach((sourceModuleNameIn, moduleIn) -> moduleIn.registries().startAll());

		this.registries().atIteration().add("update", () -> this.moduleMap.forEach((sourceModuleNameIn, moduleIn) -> moduleIn.registries().iterateAll()));

		this.registries().atStop().add("cleanup", () -> this.moduleMap.forEach((sourceModuleNameIn, moduleIn) -> moduleIn.registries().stopAll()));

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
		this.schemasInstances.each((schemaInstanceIn) ->
		{
			this.moduleMap.put(schemaInstanceIn.schema().sourceModuleClass().getSimpleName(),
					schemaInstanceIn.schema().moduleInitializer().execute());
		});
	}

	@Override
	public void stop()
	{
		isWorking = false;
	}

	public final <T extends IModule> T module(Class<T> sourceModuleClassIn)
	{
		if (sourceModuleClassIn == null)
		{
			return null;
		}

		var module = this.moduleMap.get(sourceModuleClassIn.getSimpleName());

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

	public final ModulesManager addAll(final IModule... modulesIn)
	{
		for (final var moduleClass : modulesIn)
		{
			this.add(moduleClass);
		}

		return this;
	}

	public final ModulesManager add(final IModule moduleIn)
	{
		if (moduleIn == null)
		{
			LOGGERS.logErrLn("Cannot add module from null instance !");

			return this;
		}

		final var moduleName = moduleIn.name();
		if (this.moduleMap.containsKey(moduleName))
		{
			LOGGERS.logErrLn("Module \"" + moduleName + "\" already exists !");

			return this;
		}
		this.moduleMap.put(moduleName, moduleIn);

		return this;
	}

	public final IModule get(final String moduleNameIn)
	{
		return this.moduleMap.get(moduleNameIn);
	}
}