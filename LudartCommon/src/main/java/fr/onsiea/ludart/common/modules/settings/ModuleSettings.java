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
package fr.onsiea.ludart.common.modules.settings;

import fr.onsiea.tools.logger.Loggers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class ModuleSettings
{
	private final String loggerPattern;
	private final SimpleDateFormat dateFormat;
	private final String outputsPath;
	private final String logsBasePath;
	private final String logsErrorsPath;
	private final String logsErrorsFilesPrefix;
	private final String logsErrorsFilesExtension;
	private final String logsInfosPath;
	private final String logsInfosFilesPrefix;
	private final String logsInfosFilesExtension;
	private final Date startedDate;
	private final String  formattedStartedDate;
	private final Loggers loggers;
	private final String  logsErrorCurrentFilePath;
	private final String logsInfoCurrentFilePath;
	public ModuleSettings(final Builder builderIn, final Date startedDateIn, final String formattedStartedDateIn,
			final String logsInfosCurrentFilePathIn, final String logsErrorsCurrentFilePathIn, final Loggers loggersIn)
	{
		this.loggerPattern = builderIn.loggerPattern;
		this.dateFormat = builderIn.dateFormat;
		this.outputsPath = builderIn.outputsPath;
		this.logsBasePath = builderIn.logsBasePath;

		this.logsErrorsPath = builderIn.logsErrorsPath;
		this.logsErrorsFilesPrefix = builderIn.logsErrorsFilesNamePrefix;
		this.logsErrorsFilesExtension = builderIn.logsErrorsFilesExtension;

		this.logsInfosPath = builderIn.logsInfosPath;
		this.logsInfosFilesPrefix = builderIn.logsInfosFilesNamePrefix;
		this.logsInfosFilesExtension = builderIn.logsInfosFilesExtension;

		this.startedDate = startedDateIn;
		this.formattedStartedDate = formattedStartedDateIn;
		this.loggers = loggersIn;
		this.logsErrorCurrentFilePath = logsErrorsCurrentFilePathIn;
		this.logsInfoCurrentFilePath = logsInfosCurrentFilePathIn;
	}

	/**
	 * @author Seynax
	 * @return default modules settings
	 */
	public static ModuleSettings defaults()
	{
		return new Builder().build();
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Builder
	{
		protected String loggerPattern;
		protected SimpleDateFormat dateFormat;
		protected String outputsPath;

		protected String logsBasePath;

		protected String logsErrorsPath;
		protected String logsErrorsFilesNamePrefix;
		protected String logsErrorsFilesExtension;

		protected String logsInfosPath;
		protected String logsInfosFilesNamePrefix;
		protected String logsInfosFilesExtension;

		protected Date startedDate;
		protected String formattedStartedDate;
		protected String logsInfosCurrentFilePath;
		protected String logsErrorsCurrentFilePath;
		protected Loggers loggers;

		public boolean solve()
		{
			if (this.startedDate == null)
			{
				this.startedDate = new Date();
			}

			if (this.dateFormat == null)
			{
				this.dateFormat = new SimpleDateFormat("MM-dd-yyyy_kk-mm_ss-SSS");
			}

			if (this.formattedStartedDate == null)
			{
				this.formattedStartedDate = this.dateFormat.format(this.startedDate);
			}

			if (this.loggerPattern == null)
			{
				this.loggerPattern = "[<severity_alias>|<thread>|<time:kk'h'mm_ss's'SSS'ms'>|<classFullName>.<methodName>|<lineNumber>] <content>";
			}

			if (this.outputsPath == null || this.outputsPath.matches("\\s+") || this.outputsPath.isEmpty())
			{
				this.outputsPath = "outputs";
			}

			if (this.logsBasePath == null || this.logsBasePath.matches("\\s+") || this.logsBasePath.isEmpty())
			{
				this.logsBasePath = this.outputsPath + "\\logs";
			}

			if (this.logsInfosPath == null || this.logsInfosPath.matches("\\s+") || this.logsInfosPath.isEmpty())
			{
				this.logsInfosPath = this.logsBasePath + "\\info";
			}

			if (this.logsInfosFilesNamePrefix == null)
			{
				this.logsInfosFilesNamePrefix = "infos_";
			}

			if (this.logsInfosFilesExtension == null)
			{
				this.logsInfosFilesExtension = "logs";
			}
			if (this.logsInfosCurrentFilePath == null)
			{
				this.logsInfosCurrentFilePath =
						this.logsInfosPath + "\\" + this.logsInfosFilesNamePrefix + "_" + this.formattedStartedDate
								+ "." + this.logsInfosFilesExtension;
			}

			if (this.logsErrorsPath == null || this.logsErrorsPath.matches("\\s+") || this.logsBasePath.isEmpty())
			{
				this.logsErrorsPath = this.logsBasePath + "\\errors";
			}

			if (this.logsErrorsFilesNamePrefix == null)
			{
				this.logsErrorsFilesNamePrefix = "errors_";
			}

			if (this.logsErrorsFilesExtension == null)
			{
				this.logsErrorsFilesExtension = this.logsInfosFilesExtension;
			}

			if (this.logsErrorsCurrentFilePath == null)
			{
				this.logsErrorsCurrentFilePath =
						this.logsErrorsPath + "\\" + this.logsErrorsFilesNamePrefix + this.formattedStartedDate + "."
								+ this.logsErrorsFilesExtension;
			}

			try
			{
				if (this.loggers == null)
				{
					this.loggers = Loggers.consoleAndFile(this.loggerPattern, this.logsInfosCurrentFilePath,
							this.logsErrorsCurrentFilePath, false);
				}
			}
			catch (final Exception e)
			{
				e.printStackTrace();

				return true;
			}

			return false;
		}

		public ModuleSettings build()
		{
			if (this.solve())
			{
				System.err.println("[ERROR] Failed to create module settings !");

				System.exit(-1);
			}

			return new ModuleSettings(this, this.startedDate, this.formattedStartedDate, this.logsInfosCurrentFilePath,
					this.logsErrorsCurrentFilePath, this.loggers);
		}
	}
}