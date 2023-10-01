/**
 * Copyright 2021-2023 Onsiea Studio All rights reserved.<br>
 * <br>
 *
 * This file is part of Onsiea Engine project. (https://github.com/OnsieaStudio/OnsieaEngine)<br>
 * <br>
 *
 * Onsiea Engine is [licensed] (https://github.com/OnsieaStudio/OnsieaEngine/blob/main/LICENSE) under the terms of the "GNU General Public Lesser License v2.1" (LGPL-2.1). https://github.com/OnsieaStudio/OnsieaEngine/wiki/License#license-and-copyright<br>
 * <br>
 *
 * Onsiea Engine is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or (at your option) any later version.<br>
 * <br>
 *
 * Onsiea Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.<br>
 * <br>
 *
 * You should have received a copy of the GNU Lesser General Public License along with Onsiea Engine. If not, see <https://www.gnu.org/licenses/>.<br>
 * <br>
 *
 * Neither the name "Onsiea Studio", "Onsiea Engine", or any derivative name or the names of its authors / contributors may be used to endorse or promote products derived from this software and even less to name another project or other work without clear and precise
 * permissions written in advance.<br>
 * <br>
 *
 * @Author : Seynax (https://github.com/seynax)<br>
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
		this.startRegistries = new Registries();
		this.iterationRegistries = new Registries();
		this.stopRegistries = new Registries();
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