/**
 * Copyright 2021-2023 Onsiea Studio All rights reserved.<br>
 * <br>
 *
 * This file is part of Aeison project. (https://github.com/OnsieaStudio/Aeison)<br>
 * <br>
 *
 * Aeison is [licensed] (https://github.com/OnsieaStudio/Aeison/blob/main/LICENSE) under the terms of the "GNU General
 * Public Lesser License v2.1" (LGPL-2.1).
 * https://github.com/OnsieaStudio/Aeison/wiki/License#license-and-copyright<br>
 * <br>
 *
 * Aeison is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either version 2.1 of the License, or (at your option) any
 * later version.<br>
 * <br>
 *
 * Aeison is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.<br>
 * <br>
 *
 * You should have received a copy of the GNU Lesser General Public License along with Aeison. If not, see
 * <https://www.gnu.org/licenses/>.<br>
 * <br>
 *
 * Neither the name "Onsiea Studio", "Aeison", or any derivative name or the names of its authors / contributors may be
 * used to endorse or promote products derived from this software and even less to name another project or other work
 * without clear and precise permissions written in advance.<br>
 * <br>
 *
 * @Author : Seynax (https://github.com/seynax)<br>
 * @Organization : Onsiea Studio (https://github.com/OnsieaStudio)
 */
package fr.onsiea.ludart.common.tests;

import fr.onsiea.ludart.common.modules.settings.ModuleSettings;
import fr.onsiea.tools.logger.Loggers;

/**
 * @author Seynax
 */
public class LudartCommonTests
{
	// Settings
	private final static ModuleSettings SETTINGS = ModuleSettings.defaults(); // Without modification, configured on DEFAULTS SETTINGS from ModuleSettingsClient
	public final static  Loggers        LOGGERS  = LudartCommonTests.SETTINGS.loggers();

	/**
	 * @author Seynax
	 */
	public static void main(final String[] args)
	{
		LudartCommonTests.LOGGERS.log("ENGINE-UTILS_TESTS : HELLO WORLD !");
	}
}