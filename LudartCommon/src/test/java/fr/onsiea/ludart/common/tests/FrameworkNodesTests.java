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
package fr.onsiea.ludart.common.tests;

import fr.onsiea.ludart.common.MapBatch;
import fr.onsiea.ludart.common.modules.settings.ModuleSettings;
import fr.onsiea.ludart.common.registry.FunctionsFlux;
import fr.onsiea.tools.logger.Loggers;

/**
 * @author Seynax
 */
public class FrameworkNodesTests
{
	// Settings
	private final static ModuleSettings SETTINGS = ModuleSettings.defaults(); // Without modification, configured on
	// DEFAULTS SETTINGS from ModuleSettingsClient
	public final static  Loggers        LOGGERS  = FrameworkNodesTests.SETTINGS.loggers();

	/**
	 * @author Seynax
	 */
	public static void main(final String[] args)
	{
		FrameworkNodesTests.LOGGERS.logLn("ENGINE-FRAMEWORK_TESTS : HELLO WORLD !");

		MapBatch<String, FunctionsFlux> root = new MapBatch<>((keyIn) -> new FunctionsFlux());
		show(root);
		root.batch("a");
		root.batch("b");
		root.batch("c");
		show(root);
		LOGGERS.logLn(root.remove("b"));
		show(root);
		LOGGERS.logLn(root.remove("f"));
		show(root);
	}

	public static void show(MapBatch<String, FunctionsFlux> rootIn)
	{
		StringBuilder toShow = new StringBuilder((rootIn.first() != null ? "first : " + rootIn.first() + ", " : "") + (rootIn.last() != null ? "last : " + rootIn.last() + ", " : "") + (rootIn.size() > 0 ? "[" + rootIn.size() + "]\r\n{\r\n" : "- [0]"));
		if (rootIn.size() > 0)
		{
			rootIn.each((nodeIn) -> toShow.append("\t").append("\r\n"));
			toShow.append("}");
		}
		LOGGERS.logLn(toShow);
	}
}