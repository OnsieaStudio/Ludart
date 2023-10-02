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

package fr.onsiea.ludart.common.modules.schema.instanced;

import fr.onsiea.ludart.common.modules.nodes.Components;
import fr.onsiea.ludart.common.modules.nodes.Node;
import fr.onsiea.ludart.common.modules.nodes.NodeComponents;
import fr.onsiea.ludart.common.modules.nodes.Nodes;
import lombok.experimental.Delegate;

public class ModulesPositionedSchemas
{
	private final @Delegate Components.Get<String, ModulePositionedSchema, String>                    get;
	private final @Delegate Components.Each<String, ModulePositionedSchema, ModulesPositionedSchemas> each;

	public ModulesPositionedSchemas(Nodes<String, ModulePositionedSchema> rootIn)
	{
		this.get  = Components.Get.byKey(rootIn);
		this.each = new Components.Each<>(this, rootIn);
	}

	public final static class ByNodes
	{
		private final Nodes<String, ModulePositionedSchema> root;

		private final @Delegate NodeComponents.Get<String, ModulePositionedSchema, String> get;
		private final @Delegate NodeComponents.ExtremumGet<String, ModulePositionedSchema> extremumGet;
		private final @Delegate NodeComponents.Add<String, ModulePositionedSchema>         add;

		public ByNodes()
		{
			this.root = new Nodes<>();

			this.get         = NodeComponents.Get.byKey(this.root);
			this.extremumGet = new NodeComponents.ExtremumGet<>(this.root);
			this.add         = new NodeComponents.Add<>(this.root);
		}

		public Node<String, ModulePositionedSchema> add(ModulePositionedSchema instancedSchemaIn)
		{
			var newNode = new Node<>(this.root, instancedSchemaIn.schema().sourceModuleName(), instancedSchemaIn);
			this.add.add(instancedSchemaIn.schema().sourceModuleName(), instancedSchemaIn);

			return newNode;
		}

		public Node<String, ModulePositionedSchema> addAfter(Node<String, ModulePositionedSchema> afterNodeIn, ModulePositionedSchema instancedSchemaIn)
		{
			if (afterNodeIn == null)
			{
				throw new NullPointerException("[ERROR] InstancedSchemas : cannot add instanced schema after null node !");
			}

			if (instancedSchemaIn == null)
			{
				throw new NullPointerException("[ERROR] InstancedSchemas : cannot add null instanced schema after \"" + afterNodeIn.key() + "\" node !");
			}

			var newNode = new Node<>(this.root, instancedSchemaIn.schema().sourceModuleName(), instancedSchemaIn);
			afterNodeIn.addAfter(newNode);

			return newNode;
		}

		public ModulesPositionedSchemas withoutNodes(final ModulesPositionedSchemas.ByNodes nodesInstancesIn)
		{
			return new ModulesPositionedSchemas(this.root);
		}
	}
}