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
		this.get = Components.Get.byKey(rootIn);
		this.each = new Components.Each<>(this, rootIn);
	}

	public final static class ByNodes
	{
		private final Nodes<String, ModulePositionedSchema> root;

		private final @Delegate NodeComponents.Get<String, ModulePositionedSchema, String> get;
		private final @Delegate NodeComponents.ExtremumGet<String, ModulePositionedSchema> extremumGet;
		private final @Delegate NodeComponents.Add<String, ModulePositionedSchema> add;

		public ByNodes()
		{
			this.root = new Nodes<>();

			this.get = NodeComponents.Get.byKey(this.root);
			this.extremumGet = new NodeComponents.ExtremumGet<>(this.root);
			this.add = new NodeComponents.Add<>(this.root);
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