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

import fr.onsiea.ludart.common.modules.nodes.Node;
import fr.onsiea.ludart.common.modules.schema.AtomicCounter;
import fr.onsiea.ludart.common.modules.schema.ModuleSchema;
import fr.onsiea.ludart.common.modules.schema.ModulesSchemas;

public class ModulesSchemasOrderSorter
{
	private final ModulesSchemas.ByNodes           schemas;
	private final ModulesPositionedSchemas.ByNodes nodesInstances;

	public ModulesSchemasOrderSorter(ModulesSchemas schemasIn)
	{
		this.schemas        = schemasIn.byNodes();
		this.nodesInstances = new ModulesPositionedSchemas.ByNodes();
	}

	public final ModulesPositionedSchemas sort()
	{
		this.schemas.each(this::initialize);

		return this.nodesInstances.withoutNodes(this.nodesInstances);
	}

	private Node<String, ModulePositionedSchema> initialize(Node<String, ModuleSchema> nodeIn)
	{
		var schema = nodeIn.value();

		Node<String, ModulePositionedSchema> furthestInstance      = null;
		ModulePositionedSchema               furthestInstanceValue = null;

		if (schema.dependencies().length > 0)
		{
			for (var dependency : schema.dependencies())
			{
				var instance = instance(dependency);
				if (instance == null)
				{
					throw new NullPointerException("[ERROR] SchemasOrderSorter failed to find or generate \"" + dependency + "\" dependency of \"" + nodeIn.key() + "\" !");
				}

				if (furthestInstance == null || instance.value().isAfter(furthestInstanceValue.position()))
				{
					furthestInstance      = instance;
					furthestInstanceValue = furthestInstance.value();
				}
			}
		}
		else
		{
			furthestInstance = this.nodesInstances.last();

			if (furthestInstance == null)
			{
				var instancedSchema     = new ModulePositionedSchema(nodeIn.value(), new AtomicCounter(0));
				var instancedSchemaNode = this.nodesInstances.add(instancedSchema);

				nodeIn.delete();

				return instancedSchemaNode;
			}

			furthestInstanceValue = furthestInstance.value();
		}

		var previousPosition = furthestInstanceValue.position();
		var currentPosition  = new AtomicCounter(previousPosition.value() + 1, previousPosition);
		previousPosition.next(currentPosition);

		var instancedSchema     = new ModulePositionedSchema(nodeIn.value(), currentPosition);
		var instancedSchemaNode = this.nodesInstances.addAfter(furthestInstance, instancedSchema);

		nodeIn.delete();

		return instancedSchemaNode;
	}

	private Node<String, ModulePositionedSchema> instance(String nameIn)
	{
		var instance = this.nodesInstances.of(nameIn);

		if (instance != null)
		{
			return instance;
		}

		var schema = this.schemas.of(nameIn);

		if (schema == null)
		{
			throw new NullPointerException("[ERROR] SchemasOrderSorter failed to find \"" + nameIn + "\" schema !");
		}

		return initialize(schema);
	}
}