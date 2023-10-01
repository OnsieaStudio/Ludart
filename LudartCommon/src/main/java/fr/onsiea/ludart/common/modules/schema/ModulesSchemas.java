package fr.onsiea.ludart.common.modules.schema;

import fr.onsiea.ludart.common.modules.IModule;
import fr.onsiea.ludart.common.modules.nodes.Components;
import fr.onsiea.ludart.common.modules.nodes.Node;
import fr.onsiea.ludart.common.modules.nodes.NodeComponents;
import fr.onsiea.ludart.common.modules.nodes.Nodes;
import fr.onsiea.tools.utils.function.IIFunction;
import fr.onsiea.tools.utils.function.IOFunction;
import fr.onsiea.tools.utils.function.IOIFunction;
import lombok.experimental.Delegate;

public class ModulesSchemas
{
	private final Nodes<String, ModuleSchema> root;

	private final @Delegate Components.Get<String, ModuleSchema, String>          get;
	private final @Delegate Components.Each<String, ModuleSchema, ModulesSchemas> each;

	private ModulesSchemas(Nodes<String, ModuleSchema> rootIn, Components.Get<String, ModuleSchema, String> getIn)
	{
		this.root = rootIn;
		this.get = getIn;

		this.each = new Components.Each<>(this, this.root);
	}

	public final ByNodes byNodes()
	{
		return new ByNodes(this.root);
	}

	public final static class ByNodes
	{
		private final @Delegate NodeComponents.Get<String, ModuleSchema, String> get;
		private final NodeComponents.ExtremumGet<String, ModuleSchema>           extremumGet;

		public ByNodes(Nodes<String, ModuleSchema> rootIn)
		{
			var root = Nodes.copy(rootIn);

			this.get = NodeComponents.Get.byKey(root);
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

		private final @Delegate Components.Get<String, ModuleSchema, String> get;
		private final Components.Batch<String, ModuleSchema, String> batch;

		private final IOIFunction<String, String> keyInitializer;

		public Builder()
		{
			this.root = new Nodes<>();

			this.get = Components.Get.byKey(this.root);
			this.batch = Components.Batch.byKey(this.root, new NodeComponents.Add<>(this.root));

			this.keyInitializer = (keyIn) -> keyIn;
		}

		@SafeVarargs
		public final Builder batchNeeded(final Class<?> sourceModuleClassIn, final IOFunction<IModule> defaultModuleInitializerIn, final Class<? extends IModule>... dependenciesIn)
		{
			this.batch.batch(sourceModuleClassIn.getSimpleName(), this.keyInitializer, (keyIn) -> new ModuleSchema(sourceModuleClassIn, defaultModuleInitializerIn, true, dependenciesIn));

			return this;
		}

		@SafeVarargs
		public final Builder batch(final Class<?> sourceModuleClassIn, final IOFunction<IModule> defaultModuleInitializerIn, final Class<? extends IModule>... dependenciesIn)
		{
			this.batch.batch(sourceModuleClassIn.getSimpleName(), this.keyInitializer, (keyIn) -> new ModuleSchema(sourceModuleClassIn, defaultModuleInitializerIn, false, dependenciesIn));

			return this;
		}

		public ModulesSchemas build()
		{
			return new ModulesSchemas(this.root, this.get);
		}
	}
}