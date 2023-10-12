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

package fr.onsiea.ludart.common.modules.processor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LudartModule
{
	Class<?> parent() default NULL.class; // if null, the parent is the manager module, if not null, will be stored in the parent and executed by it, specifying the current module as parent raises an error

	Class<?> inherited() default NULL.class; // if not null inherits dependencies and addons settings from the specified module, specifying the current module as inherited raises an error

	Class<?>[] dependencies() default {};   // mandatory dependencies, specifying the current module as dependency raises an error

	Class<?>[] addons() default {}; // additional dependencies, specifying the current module as addon raises an error

	String insteadOf() default ""; // the qualified name of the class which must be replaced by the module, does not take a "Class<?>" parameter to avoid having to have the class of the module in question, specifying the current module as "instead of" raises an error

	byte insteadOfPriority() default 0; // if several modules replace the same module, the one with the replacement priority value takes precedence over the others

	/**
	 * MANDATORY : cannot be removed, if replaceable is true, can be replaced to be removed
	 * DEVELOPER_EXPLICIT_CHOICE : developer must explicitly request that the module must be loaded, is still loaded if another loaded module is dependent on it
	 * LOADED_BY_DEFAULT : loaded by default, the developer must explicitly ask for it not to be loaded, if the developer explicitly says that it should not be loaded, a warning or even an error will be raised (depending on the developer's configuration)
	 *
	 * @return
	 */
	LoadType loadType() default LoadType.DEVELOPER_EXPLICIT_CHOICE;

	boolean replaceable() default true; // if true, can be replaced by another module

	enum LoadType
	{
		MANDATORY,
		DEVELOPER_EXPLICIT_CHOICE,
		LOADED_BY_DEFAULT,
	}

	final class NULL
	{

	}
}