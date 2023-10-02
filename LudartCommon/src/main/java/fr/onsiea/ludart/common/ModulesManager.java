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

import fr.onsiea.ludart.common.modules.IModule;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModulesManager
{
	private final Map<String, IModule> allModules;

	@SafeVarargs
	public ModulesManager(final IModule... modulesClassesIn)
	{
		this.allModules = new LinkedHashMap<>();

		this.addAll(modulesClassesIn);
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
			Framework.LOGGERS.logErrLn("Cannot add module from null instance !");

			return this;
		}

		final var moduleName = moduleIn.name();
		if (this.allModules.containsKey(moduleName))
		{
			Framework.LOGGERS.logErrLn("Module \"" + moduleName + "\" already exists !");

			return this;
		}
		this.allModules.put(moduleName, moduleIn);

		return this;
	}

	public final IModule get(final String moduleNameIn)
	{
		return this.allModules.get(moduleNameIn);
	}

	public final ModulesManager startAll()
	{
		for (final var module : this.allModules.values())
		{
			module.startAll();
		}

		return this;
	}

	public final ModulesManager iterateAll()
	{
		for (final var module : this.allModules.values())
		{
			module.iterateAll();
		}

		return this;
	}

	public final ModulesManager stopAll()
	{
		for (final var module : this.allModules.values())
		{
			module.stopAll();
		}

		return this;
	}
}