/**
 * Copyright 2021-2023 Onsiea Studio All rights reserved.<br>
 * <br>
 *
 * This file is part of Onsiea Engine project. (https://github.com/OnsieaStudio/OnsieaEngine)<br>
 * <br>
 *
 * Onsiea Engine is [licensed] (https://github.com/OnsieaStudio/OnsieaEngine/blob/main/LICENSE) under the terms of the
 * "GNU General Public Lesser License v2.1" (LGPL-2.1).
 * https://github.com/OnsieaStudio/OnsieaEngine/wiki/License#license-and-copyright<br>
 * <br>
 *
 * Onsiea Engine is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License, or (at your option)
 * any later version.<br>
 * <br>
 *
 * Onsiea Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.<br>
 * <br>
 *
 * You should have received a copy of the GNU Lesser General Public License along with Onsiea Engine. If not, see
 * <https://www.gnu.org/licenses/>.<br>
 * <br>
 *
 * Neither the name "Onsiea Studio", "Onsiea Engine", or any derivative name or the names of its authors / contributors
 * may be used to endorse or promote products derived from this software and even less to name another project or other
 * work without clear and precise permissions written in advance.<br>
 * <br>
 *
 * @Author : Seynax (https://github.com/seynax)<br>
 * @Organization : Onsiea Studio (https://github.com/OnsieaStudio)
 */
package fr.onsiea.ludart.common.registry;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.Objects;

@Getter
@ToString
public class Registry
{
	private final String name;
	private final @Delegate FunctionsFlux functionsFlux;

	public Registry(final String nameIn)
	{
		this.name = nameIn;
		this.functionsFlux = new FunctionsFlux();
	}

	public final boolean is(final String nameIn)
	{
		return this.name.contentEquals(nameIn);
	}

	public final Registry execute()
	{
		for (final var function : this.functionsFlux.functions())
		{
			function.execute();
		}

		return this;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.name);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass())
		{
			return false;
		}

		final var other = (Registry) obj;
		return Objects.equals(this.name, other.name);
	}
}