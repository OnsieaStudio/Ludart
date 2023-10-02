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
package fr.onsiea.ludart.common.registries.manager;

import fr.onsiea.ludart.common.registries.Registries;
import fr.onsiea.ludart.common.registry.Registry;

public class RegistriesManager
{
	// Start registries
	private final Registries startRegistries;
	// All registries
	private final Registries iterationRegistries;
	// Stop registries
	private final Registries stopRegistries;

	public RegistriesManager()
	{
		this.startRegistries     = new Registries();
		this.iterationRegistries = new Registries();
		this.stopRegistries      = new Registries();
	}

	public final RegistriesManager startAll()
	{
		this.startRegistries.execute();

		return this;
	}

	public final RegistriesManager iterateAll()
	{
		this.iterationRegistries.execute();

		return this;
	}

	public final RegistriesManager stopAll()
	{
		this.stopRegistries.execute();

		return this;
	}

	public final Registry atStart(final String nameIn)
	{
		return this.startRegistries.batch(nameIn);
	}

	public final Registries atStart()
	{
		return this.startRegistries;
	}

	public final Registry atIteration(final String nameIn)
	{
		return this.iterationRegistries.batch(nameIn);
	}

	public final Registries atIteration()
	{
		return this.iterationRegistries;
	}

	public final Registry atStop(final String nameIn)
	{
		return this.stopRegistries.batch(nameIn);
	}

	public final Registries atStop()
	{
		return this.stopRegistries;
	}
}