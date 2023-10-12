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

package fr.onsiea.ludart.client.window;

import fr.onsiea.ludart.client.window.settings.IWindowSettings;
import fr.onsiea.ludart.common.modules.IModule;
import fr.onsiea.ludart.common.modules.ModuleBase;
import fr.onsiea.ludart.common.modules.manager.IModulesManager;
import fr.onsiea.ludart.common.modules.processor.LudartModule;

import java.util.function.Supplier;

@LudartModule
public class ModuleWindow extends ModuleBase implements IModuleWindow
{
	private IWindowSettings     settings;
	private IWindowSystem       system;
	private IWindowRenderSystem windowRenderSystem;
	private IModulesManager     modulesManager;

	public ModuleWindow()
	{

	}

	public final void define(Supplier<? extends IWindowSettings> settingsIn, Supplier<? extends IWindowSystem> systemIn, Supplier<? extends IWindowRenderSystem> windowRenderSystemIn)
	{
		if (settings != null || system != null || windowRenderSystem != null)
		{
			throw new RuntimeException("[ERROR] ModuleWindow : system, settings and windowRenderSystem already defined !");
		}

		if (settingsIn == null || systemIn == null || windowRenderSystemIn == null)
		{
			throw new RuntimeException("[ERROR] ModuleRender : received system, settings and/or windowRenderSystem is null !");
		}

		this.registries().atStart().add("initialization", () ->
		{
			settings = settingsIn.get();
			system   = systemIn.get();
			system.initialization(settings);
			windowRenderSystem = windowRenderSystemIn.get();
			windowRenderSystem.initialization(system.handle(), settings);
		});

		this.registries().atStop("cleanup").atEnd(() ->
		{
			system.cleanup();
		});
	}

	@Override
	public void pollEvents()
	{
		system.pollEvents();
	}

	@Override
	public void refresh()
	{
		system.refresh();
	}

	public final <T extends IModule> void dependencies(final IModulesManager modulesManagerIn, final T... dependenciesIn)
	{
		if (modulesManager != null)
		{
			throw new RuntimeException("[ERROR] ModulesManager already defined !");
		}

		modulesManager = modulesManagerIn;

		registries().atIteration().add("update", () ->
		{
			if (system.shouldClose())
			{
				modulesManager.stop();
			}
		});
	}
}
