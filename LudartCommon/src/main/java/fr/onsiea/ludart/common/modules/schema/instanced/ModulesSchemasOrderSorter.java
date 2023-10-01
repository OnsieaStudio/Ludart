package fr.onsiea.ludart.common.modules.schema.instanced;

import fr.onsiea.ludart.common.modules.nodes.Node;
import fr.onsiea.ludart.common.modules.schema.AtomicCounter;
import fr.onsiea.ludart.common.modules.schema.ModuleSchema;
import fr.onsiea.ludart.common.modules.schema.ModulesSchemas;

public class ModulesSchemasOrderSorter
{
	private final ModulesSchemas.ByNodes schemas;
	private final ModulesPositionedSchemas.ByNodes nodesInstances;

	public ModulesSchemasOrderSorter(ModulesSchemas schemasIn)
	{
		this.schemas = schemasIn.byNodes();
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

		Node<String, ModulePositionedSchema> furthestInstance = null;
		ModulePositionedSchema furthestInstanceValue = null;

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
					furthestInstance = instance;
					furthestInstanceValue = furthestInstance.value();
				}
			}
		}
		else
		{
			furthestInstance = this.nodesInstances.last();

			if (furthestInstance == null)
			{
				var instancedSchema = new ModulePositionedSchema(nodeIn.value(), new AtomicCounter(0));
				var instancedSchemaNode = this.nodesInstances.add(instancedSchema);

				nodeIn.delete();

				return instancedSchemaNode;
			}

			furthestInstanceValue = furthestInstance.value();
		}

		var previousPosition = furthestInstanceValue.position();
		var currentPosition = new AtomicCounter(previousPosition.value() + 1, previousPosition);
		previousPosition.next(currentPosition);

		var instancedSchema = new ModulePositionedSchema(nodeIn.value(), currentPosition);
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