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

package fr.onsiea.ludart.common.modules.schema;

import fr.onsiea.ludart.common.modules.IModule;
import fr.onsiea.ludart.common.modules.manager.IModulesManager;
import fr.onsiea.ludart.common.modules.nodes.Components;
import fr.onsiea.ludart.common.modules.nodes.Node;
import fr.onsiea.ludart.common.modules.nodes.NodeComponents;
import fr.onsiea.ludart.common.modules.nodes.Nodes;
import fr.onsiea.ludart.common.modules.processor.LudartModulesManager;
import fr.onsiea.tools.utils.function.IIFunction;
import fr.onsiea.tools.utils.function.IOFunction;
import fr.onsiea.tools.utils.function.IOIFunction;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModulesSchemas
{
	private final Nodes<String, ModuleSchema> root;

	private final @Delegate Components.Get<String, ModuleSchema, String>          get;
	private final @Delegate Components.Each<String, ModuleSchema, ModulesSchemas> each;

	private final @Getter IModulesManager.IFactory modulesManagerSchemaFactory;

	private ModulesSchemas(Nodes<String, ModuleSchema> rootIn, Components.Get<String, ModuleSchema, String> getIn, IModulesManager.IFactory modulesManagerSchemaIn)
	{
		this.root                   = rootIn;
		this.get                    = getIn;
		modulesManagerSchemaFactory = modulesManagerSchemaIn;

		this.each = new Components.Each<>(this, this.root);
	}

	public final ByNodes byNodes()
	{
		return new ByNodes(this.root);
	}

	public final void clear()
	{
		root.clear();
	}

	public final static class ByNodes
	{
		private final @Delegate NodeComponents.Get<String, ModuleSchema, String> get;
		private final           NodeComponents.ExtremumGet<String, ModuleSchema> extremumGet;

		public ByNodes(Nodes<String, ModuleSchema> rootIn)
		{
			var root = Nodes.copy(rootIn);

			this.get         = NodeComponents.Get.byKey(root);
			this.extremumGet = new NodeComponents.ExtremumGet<>(root);
		}

		/**
		 * Concurrent each
		 */
		public ByNodes each(IIFunction<Node<String, ModuleSchema>> functionIn)
		{
			var current = this.extremumGet.first();
			while (current != null)
			{
				var toExecute = current;
				current = current.next();
				functionIn.execute(toExecute);
			}

			return this;
		}
	}

	public final static class Builder
	{
		private final Nodes<String, ModuleSchema> root;

		private final @Delegate Components.Get<String, ModuleSchema, String>   get;
		private final           Components.Batch<String, ModuleSchema, String> batch;

		private final IOIFunction<String, String> keyInitializer;

		private final Map<String, ModulesSchemasPackage> packageMap;

		private IModulesManager.IFactory modulesManager;

		public Builder()
		{
			this.root = new Nodes<>();

			this.get   = Components.Get.byKey(this.root);
			this.batch = Components.Batch.byKey(this.root, new NodeComponents.Add<>(this.root));

			this.keyInitializer = (keyIn) -> keyIn;

			packageMap = new LinkedHashMap<>();
		}

		public ModulesSchemasPackage makePackage(String packageNameIn, int moduleCountIn)
		{
			if (moduleCountIn <= 0)
			{
				throw new RuntimeException("[ERROR] ModulesSchemasPackageList.Builder : cannot make package without modules ! \"" + packageNameIn + "\", \"" + moduleCountIn + "\"");
			}

			if (packageMap.containsKey(packageNameIn))
			{
				throw new RuntimeException("[ERROR] ModulesSchemasPackageList.Builder : package already exists ! \"" + packageNameIn + "\"");
			}

			var modulesSchemasPackage = new ModulesSchemasPackage(this, packageNameIn, moduleCountIn);

			packageMap.put(modulesSchemasPackage.name, modulesSchemasPackage);

			return modulesSchemasPackage;
		}

		private void build(ModulesSchemasPackage modulesSchemasPackageIn) throws IllegalAccessException, NoSuchMethodException
		{
			if (!packageMap.containsKey(modulesSchemasPackageIn.name()))
			{
				throw new IllegalAccessException("[ERROR] ModulesSchemas.Builder : cannot build unregistered modules schemas package ! \"" + modulesSchemasPackageIn.name() + "\" is unknown !");
			}

			if (modulesSchemasPackageIn.currentIndex() > 0)
			{
				for (var moduleSchema : modulesSchemasPackageIn.schemaArray)
				{
					if (moduleSchema.moduleClass().isAnnotationPresent(LudartModulesManager.class))
					{
						if (!IModulesManager.class.isAssignableFrom(moduleSchema.moduleClass()))
						{
							throw new IllegalStateException("[ERROR] ModulesSchemas.Builder : cannot load module with @LudartModuleManager annotation, which does not implement IModulesManager !");
						}

						final var modulesManagerClass = (Class<? extends IModulesManager>) moduleSchema.moduleClass();
						var       constructor         = modulesManagerClass.getConstructor(ModulesSchemas.class);
						if (constructor == null)
						{
							throw new IllegalStateException("[ERROR] ModulesSchemas.Builder : modules manager must had constructor with one ModulesSchemas parameter  !");
						}

						if (modulesManager != null)
						{
							System.out.println("[WARNING] ModulesSchemas.Builder : last modules manager \"" + modulesManager.name() + "\" replaced by new \"" + moduleSchema.name() + "\" !");
						}

						modulesManager = new IModulesManager.IFactory()
						{
							@Override
							public Class<? extends IModulesManager> moduleClass()
							{
								return modulesManagerClass;
							}

							@Override
							public IModulesManager create(ModulesSchemas schemasIn) throws InvocationTargetException, InstantiationException, IllegalAccessException
							{
								return constructor.newInstance(schemasIn);
							}
						};

						continue;
					}
					this.batch.batch(moduleSchema.name(), this.keyInitializer, moduleSchema.supplier());
				}
			}

			packageMap.remove(modulesSchemasPackageIn);
		}

		public ModulesSchemas build() throws IllegalAccessException, NoSuchMethodException
		{
			for (var modulesSchemasPackage : packageMap.values())
			{
				modulesSchemasPackage.build();
			}
			packageMap.clear();

			return new ModulesSchemas(this.root, this.get, modulesManager);
		}
	}

	public final static class ModulesSchemasPackage
	{
		private final         ModulesSchemas.Builder  modulesSchemas;
		private final @Getter String                  name;
		private final         Map<String, Integer>    indexMap;
		private final         ModuleSchema.IFactory[] schemaArray;
		private @Getter       int                     currentIndex;

		private boolean builded;

		ModulesSchemasPackage(final ModulesSchemas.Builder modulesSchemasIn, final String nameIn, final int sizeIn)
		{
			modulesSchemas = modulesSchemasIn;
			name           = nameIn;
			indexMap       = new LinkedHashMap<>();
			schemaArray    = new ModuleSchema.IFactory[sizeIn];
		}

		@SafeVarargs
		public final ModulesSchemasPackage add(final Class<? extends IModule> sourceModuleClassIn, final IOFunction<IModule> defaultModuleInitializerIn, final Class<? extends IModule>... dependenciesIn)
		{
			return add(sourceModuleClassIn, defaultModuleInitializerIn, false, dependenciesIn);
		}

		public ModulesSchemasPackage add(final Class<? extends IModule> sourceModuleClassIn, final IOFunction<IModule> defaultModuleInitializerIn, boolean neededIn, final Class<? extends IModule>... dependenciesIn)
		{
			if (!IModule.class.isAssignableFrom(sourceModuleClassIn))
			{
				throw new RuntimeException("[ERROR] ModulesSchemasPackage : cannot add module schema from class which does not implement IModule !");
			}

			for (var dependency : dependenciesIn)
			{
				if (!IModule.class.isAssignableFrom(dependency))
				{
					throw new RuntimeException("[ERROR] ModulesSchemasPackage : cannot add dependency into module schema from class which does not implement IModule !");
				}
			}

			Integer index = indexMap.get(sourceModuleClassIn.getSimpleName());

			if (index != null)
			{
				return this;
			}

			if (currentIndex >= schemaArray.length)
			{
				throw new ArrayIndexOutOfBoundsException("[ERROR] ModulesSchemasPackage : array index out of bounds exception for \"" + currentIndex + "\" from array with \"" + schemaArray.length + "\" length !");
			}

			schemaArray[currentIndex] = new ModuleSchema.IFactory()
			{
				@Override
				public IOIFunction<ModuleSchema, String> supplier()
				{
					return (keyIn) -> new ModuleSchema(sourceModuleClassIn, defaultModuleInitializerIn, neededIn, dependenciesIn);
				}

				@Override
				public Class<? extends IModule> moduleClass()
				{
					return sourceModuleClassIn;
				}
			};

			currentIndex++;

			return this;
		}

		@SafeVarargs
		public final ModulesSchemasPackage addNeeded(final Class<? extends IModule> sourceModuleClassIn, final IOFunction<IModule> defaultModuleInitializerIn, final Class<? extends IModule>... dependenciesIn)
		{
			return add(sourceModuleClassIn, defaultModuleInitializerIn, true, dependenciesIn);
		}

		public ModulesSchemas.Builder build() throws IllegalAccessException, NoSuchMethodException
		{
			if (builded)
			{
				return modulesSchemas;
			}

			if (currentIndex != schemaArray.length)
			{
				System.out.println("[INFO] ModulesSchemas.Package : The number of loaded module plans is not equal to the number of desired modules, this may be due to modules having the same identifier, which results in replacement by the last loaded module.");
			}

			modulesSchemas.build(this);
			for (int i = 0; i < schemaArray.length; i++)
			{
				schemaArray[i] = null;
			}
			indexMap.clear();
			currentIndex = 0;
			builded      = true;

			return modulesSchemas;
		}
	}
}