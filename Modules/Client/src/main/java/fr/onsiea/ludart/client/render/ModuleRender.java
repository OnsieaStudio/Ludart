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

package fr.onsiea.ludart.client.render;

import fr.onsiea.ludart.client.window.IModuleWindow;
import fr.onsiea.ludart.client.window.ModuleWindow;
import fr.onsiea.ludart.common.modules.IModule;
import fr.onsiea.ludart.common.modules.ModuleBase;
import fr.onsiea.ludart.common.modules.manager.IModulesManager;
import fr.onsiea.ludart.common.modules.processor.LudartModule;

import java.util.function.Supplier;

@LudartModule(dependencies = {ModuleWindow.class})
public class ModuleRender extends ModuleBase implements IModuleRender
{
	private IRenderSystem         system;
	private IRenderImplementation implementation;
	private IModuleWindow         moduleWindow;

	public ModuleRender()
	{

	}

	public void define(Supplier<? extends IRenderSystem> systemIn, Supplier<? extends IRenderImplementation> implementationIn)
	{
		if (system != null || implementation != null)
		{
			throw new RuntimeException("[ERROR] ModuleRender : system or implementation already defined !");
		}

		if (systemIn == null || implementationIn == null)
		{
			throw new RuntimeException("[ERROR] ModuleRender : system and implementation must be defined, but one received is null !");
		}

		this.registries().atStart("initialization").atEnd(() ->
		{
			system = systemIn.get();
			system.initialization();
			implementation = implementationIn.get();
			implementation.initialization();
		});

		this.registries().atStop("initialization").atEnd(() ->
		{
			implementation.cleanup();
			system.cleanup();
		});
	}

	public final <T extends IModule> void dependencies(IModulesManager modulesManagerIn, T... dependenciesIn)
	{
		if (moduleWindow != null)
		{
			throw new RuntimeException("[ERROR] ModuleWindow already defined !");
		}
		if (dependenciesIn.length == 0)
		{
			throw new RuntimeException("[ERROR] ModuleRender must receive IModuleWindow dependency !");
		}

		var dependency = dependenciesIn[0];
		if (dependency == null)
		{
			throw new RuntimeException("[ERROR] ModuleRender must receive IModuleWindow dependency, but received is null !");
		}

		if (!(dependency instanceof IModuleWindow))
		{
			throw new RuntimeException("[ERROR] ModuleRender must receive IModuleWindow dependency, but received isn't IModuleWindow instance !");
		}

		moduleWindow = (IModuleWindow) dependency;

		registries().atIteration().add("update", () ->
		{
			moduleWindow.pollEvents();
			system.clear();

			implementation.draw();

			moduleWindow.refresh();
		});
	}
}