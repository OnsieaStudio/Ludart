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
package fr.onsiea.ludart.common.modules.stack;

import fr.onsiea.ludart.common.modules.EnumPosition;

import java.util.Collections;
import java.util.Stack;

/**
 * @param <T> stack child type
 */
public interface StackRelativizable<T>
{
	/**
	 * add childModuleIn module according to the placement defined by the enum placementIn
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	default StackRelativizable<T> add(final EnumPosition positionIn, final T childModuleIn)
	{
		switch (positionIn)
		{
			case BEGIN -> this.atBegin(childModuleIn);
			case MIDDLE -> this.inMiddle(childModuleIn);
			default -> this.atEnd(childModuleIn);
		}

		return this;
	}

	/**
	 * add childModuleIn module at the start of the flux
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	default StackRelativizable<T> atBegin(final T childModuleIn)
	{
		this.childs().add(0, childModuleIn);

		return this;
	}

	/**
	 * add childModuleIn module in the middle of the flux
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	default StackRelativizable<T> inMiddle(final T childModuleIn)
	{
		this.childs().add(this.childs().size() / 2, childModuleIn);

		return this;
	}

	/**
	 * add childModuleIn module at the end of the flux
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	default StackRelativizable<T> atEnd(final T childModuleIn)
	{
		this.childs().add(childModuleIn);

		return this;
	}

	/**
	 * @return all childs modules
	 * @author Seynax
	 */
	Stack<T> childs();

	/**
	 * add all modules from childsModulesIn according to the placement defined by the enum placementIn
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	@SuppressWarnings("unchecked")
	default StackRelativizable<T> addAll(final EnumPosition positionIn, final T... childsModulesIn)
	{
		switch (positionIn)
		{
			case BEGIN -> this.atBeginAll(childsModulesIn);
			case MIDDLE -> this.inMiddleAll(childsModulesIn);
			default -> this.atEndAll(childsModulesIn);
		}

		return this;
	}

	/**
	 * add all modules from childsModulesIn at the start of the flux
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	@SuppressWarnings("unchecked")
	default StackRelativizable<T> atBeginAll(final T... childsModulesIn)
	{
		for (var i = childsModulesIn.length - 1; i >= 0; i--)
		{
			final var childModule = childsModulesIn[i];

			this.childs().add(0, childModule);
		}

		return this;
	}

	/**
	 * add all modules from childsModulesIn in the middle of the flux
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	@SuppressWarnings("unchecked")
	default StackRelativizable<T> inMiddleAll(final T... childsModulesIn)
	{
		for (final var childModule : childsModulesIn)
		{
			this.childs().add(this.childs().size() / 2, childModule);
		}

		return this;
	}

	/**
	 * add all modules from childsModulesIn at the end of the flux
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	@SuppressWarnings("unchecked")
	default StackRelativizable<T> atEndAll(final T... childsModulesIn)
	{
		Collections.addAll(this.childs(), childsModulesIn);

		return this;
	}

	/**
	 * add childModuleIn module according to the placement defined by the enum placementIn
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	default StackRelativizable<T> add(final int indexIn, final T childModuleIn)
	{
		this.childs().add(indexIn, childModuleIn);

		return this;
	}

	/**
	 * add all modules from childsModulesIn according to the placement defined by the enum placementIn
	 *
	 * @return current module instance
	 * @author Seynax
	 */
	@SuppressWarnings("unchecked")
	default StackRelativizable<T> addAll(final int indexIn, final T... childsModulesIn)
	{
		for (final var childModule : childsModulesIn)
		{
			this.childs().add(indexIn, childModule);
		}

		return this;
	}
}
