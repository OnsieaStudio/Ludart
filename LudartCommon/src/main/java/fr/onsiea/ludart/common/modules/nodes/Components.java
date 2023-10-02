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

package fr.onsiea.ludart.common.modules.nodes;

import fr.onsiea.tools.utils.function.IIFunction;
import fr.onsiea.tools.utils.function.IOIFunction;
import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Likely to change a lot. The goal is to improve modularity by creating a common abstraction while improving performance.
 */
public class Components
{
	public static <T> boolean is(T fromIn, T toIn)
	{
		return fromIn.hashCode() == toIn.hashCode() && fromIn.equals(toIn);
	}

	public interface IComponent<K, V>
	{

	}

	public interface IAdd<K, V, P>
	{
		P add(K keyIn, V valueIn);
	}

	public interface IAddRelative<K, V, G, P>
	{
		P before(G toGetIn, K keyIn, V valueIn);

		P after(G toGetIn, K keyIn, V valueIn);
	}

	public interface IAddPosition<K, V, P>
	{
		P begin(K keyIn, V valueIn);

		P middle(K keyIn, V valueIn);

		P end(K keyIn, V valueIn); // Equivalent to Add with IAdd<K, V>
	}

	public interface IGet<K, V, G>
	{
		V ofReverse(G toGetIn);

		V of(G toGetIn);
	}

	public interface IValueGet<K, V> // I create the ValueGet class separately from Get, because having a method to retrieve a node via K or via V with the same name would collide
	{
		V ofValueReverse(V valueIn);

		V ofValue(V valueIn);
	}

	public interface IBatch<K, V, G>
	{
		V batch(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn);
	}

	public interface IBatchCommonInitializer<K, V, G>
	{
		V batch(G toGetIn);
	}

	public interface IBatchRelative<K, V, G, R>
	{
		V batchBefore(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn, R beforeIn);

		V batchAfter(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn, R afterIn);
	}

	public interface IBatchRelativeCommonInitializer<K, V, G, R>
	{
		V batchBefore(G toGetIn, R beforeIn);

		V batchAfter(G toGetIn, R afterIn);
	}

	public interface IBatchPosition<K, V, G>
	{
		V batchBegin(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn);

		V batchMiddle(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn);

		V batchEnd(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn);
	}

	public interface IBatchPositionCommonInitializer<K, V, G>
	{
		V batchBegin(G toGetIn);

		V batchMiddle(G toGetIn);

		V batchEnd(G toGetIn);
	}

	public interface IRemove<P, G>
	{
		P remove(G toRemoveIn);
	}

	public static class Customizable<K, V>
	{
		private final Nodes<K, V>                   root;
		private final Map<String, IComponent<K, V>> components;

		public Customizable(Nodes<K, V> nodesIn)
		{
			this.root       = nodesIn;
			this.components = new LinkedHashMap<>();
		}

		public IComponent<K, V> get(String nameIn)
		{
			return this.components.get(nameIn);
		}

		public Customizable<K, V> add(@NonNull String nameIn, @NonNull IComponent<K, V> componentIn)
		{
			this.components.put(nameIn, componentIn);

			return this;
		}

		@SafeVarargs
		public final Customizable<K, V> addAll(@NonNull Class<IComponent<K, V>>... componentsClassesIn) throws Exception
		{
			for (var componentClass : componentsClassesIn)
			{
				add(componentClass);
			}

			return this;
		}

		public Customizable<K, V> add(@NonNull Class<IComponent<K, V>> componentClassIn) throws Exception
		{
			@NonNull var name        = componentClassIn.getName();
			@NonNull var constructor = componentClassIn.getConstructor(Nodes.class);

			constructor.newInstance(this.root);

			return this;
		}
	}

	public static class Add<K, V, P> implements IAdd<K, V, P>, IComponent<K, V>
	{
		private final P           parent;
		private final Nodes<K, V> root;

		public Add(P parentIn, final Nodes<K, V> rootIn)
		{
			this.parent = parentIn;
			this.root   = rootIn;
		}

		public P add(K keyIn, V valueIn)
		{
			var node = new Node<>(this.root, keyIn, valueIn);

			this.root.afterLast(node);

			return this.parent;
		}
	}

	public final static class Remove<K, V, G, P> implements IRemove<P, G>, IComponent<K, V>
	{
		private final IOIFunction<Node<K, V>, G> getFunction;
		private final P                          parent;

		public Remove(IOIFunction<Node<K, V>, G> getFunctionIn, P parentIn)
		{
			this.getFunction = getFunctionIn;
			this.parent      = parentIn;
		}

		public static <K, V, G, P> Remove<K, V, G, P> make(IOIFunction<Node<K, V>, G> getterIn, P parentIn)
		{
			return new Remove<>(getterIn, parentIn);
		}

		public static <K, V, G> Remove<K, V, G, Nodes<K, V>> make(Nodes<K, V> parentIn, IOIFunction<Node<K, V>, G> getterIn)
		{
			return new Remove<>(getterIn, parentIn);
		}

		public static <K, V, P> Remove<K, V, IOIFunction<Boolean, Node<K, V>>, P> byFunction(Nodes<K, V> rootIn, P parentIn)
		{
			return new Remove<>(NodeComponents.Get.byFunction(rootIn)::of, parentIn);
		}

		public static <K, V> Remove<K, V, IOIFunction<Boolean, Node<K, V>>, Nodes<K, V>> byFunction(Nodes<K, V> rootIn)
		{
			return new Remove<>(NodeComponents.Get.byFunction(rootIn)::of, rootIn);
		}

		public static <K, V, P> Remove<K, V, Integer, P> byIndex(Nodes<K, V> rootIn, P parentIn)
		{
			return new Remove<>(NodeComponents.Get.byIndex(rootIn)::of, parentIn);
		}

		public static <K, V> Remove<K, V, Integer, Nodes<K, V>> byIndex(Nodes<K, V> rootIn)
		{
			return new Remove<>(NodeComponents.Get.byIndex(rootIn)::of, rootIn);
		}

		public static <K, V, P> Remove<K, V, V, P> byValue(Nodes<K, V> rootIn, P parentIn)
		{
			return new Remove<>(new NodeComponents.ValueGet<>(rootIn)::ofValue, parentIn);
		}

		public static <K, V> Remove<K, V, V, Nodes<K, V>> byValue(Nodes<K, V> rootIn)
		{
			return new Remove<>(new NodeComponents.ValueGet<>(rootIn)::ofValue, rootIn);
		}

		public static <K, V, P> Remove<K, V, K, P> byKey(Nodes<K, V> rootIn, P parentIn)
		{
			return new Remove<>(NodeComponents.Get.byKey(rootIn)::of, parentIn);
		}

		public static <K, V> Remove<K, V, K, Nodes<K, V>> byKey(Nodes<K, V> rootIn)
		{
			return new Remove<>(NodeComponents.Get.byKey(rootIn)::of, rootIn);
		}

		public P remove(@NonNull G toRemoveIn)
		{
			var node = this.getFunction.execute(toRemoveIn);

			if (node != null)
			{
				node.delete();
			}

			return this.parent;
		}
	}

	public final static class Batch<K, V, G> implements IBatch<K, V, G>
	{
		private final IOIFunction<Node<K, V>, G> getFunction;
		private final NodeComponents.IAdd<K, V>  addFunction;

		public Batch(IOIFunction<Node<K, V>, G> getFunctionIn, NodeComponents.IAdd<K, V> addFunctionIn)
		{
			this.getFunction = getFunctionIn;
			this.addFunction = addFunctionIn;
		}

		public static <K, V, G> Batch<K, V, G> make(IOIFunction<Node<K, V>, G> getterIn, NodeComponents.IAdd<K, V> addFunctionIn)
		{
			return new Batch<>(getterIn, addFunctionIn);
		}

		public static <K, V> Batch<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn, NodeComponents.IAdd<K, V> addFunctionIn)
		{
			return new Batch<>(NodeComponents.Get.byFunction(rootIn)::of, addFunctionIn);
		}

		public static <K, V> Batch<K, V, Integer> byIndex(Nodes<K, V> rootIn, NodeComponents.IAdd<K, V> addFunctionIn)
		{
			return new Batch<>(NodeComponents.Get.byIndex(rootIn)::of, addFunctionIn);
		}

		public static <K, V> Batch<K, V, V> byValue(Nodes<K, V> rootIn, NodeComponents.IAdd<K, V> addFunctionIn)
		{
			return new Batch<>(new NodeComponents.ValueGet<>(rootIn)::ofValue, addFunctionIn);
		}

		public static <K, V> Batch<K, V, K> byKey(Nodes<K, V> rootIn, NodeComponents.IAdd<K, V> addFunctionIn)
		{
			return new Batch<>(NodeComponents.Get.byKey(rootIn)::of, addFunctionIn);
		}

		@Override
		public @NonNull V batch(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node.value();
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			this.addFunction.add(key, value);

			return value;
		}

		public static class CommonInitializer<K, V, G> implements IBatchCommonInitializer<K, V, G>
		{
			private final IOIFunction<Node<K, V>, G> getFunction;
			private final NodeComponents.IAdd<K, V>  addFunction;
			private final IOIFunction<K, G>          keyInitializer;
			private final IOIFunction<V, K>          valueInitializer;

			public CommonInitializer(IOIFunction<Node<K, V>, G> getFunctionIn, NodeComponents.IAdd<K, V> addFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				this.getFunction      = getFunctionIn;
				this.addFunction      = addFunctionIn;
				this.keyInitializer   = keyInitializerIn;
				this.valueInitializer = valueInitializerIn;
			}

			public static <K, V, G> CommonInitializer<K, V, G> make(IOIFunction<Node<K, V>, G> getterIn, NodeComponents.IAdd<K, V> adderIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(getterIn, adderIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn, NodeComponents.IAdd<K, V> adderIn, IOIFunction<K, IOIFunction<Boolean, Node<K, V>>> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byFunction(rootIn)::of, adderIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, Integer> byIndex(Nodes<K, V> rootIn, NodeComponents.IAdd<K, V> adderIn, IOIFunction<K, Integer> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byIndex(rootIn)::of, adderIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, V> byValue(Nodes<K, V> rootIn, NodeComponents.IAdd<K, V> adderIn, IOIFunction<K, V> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(new NodeComponents.ValueGet<>(rootIn)::ofValue, adderIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, K> byKey(Nodes<K, V> rootIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byKey(rootIn)::of, new NodeComponents.Add<>(rootIn), (keyIn) -> keyIn, valueInitializerIn);
			}

			public @NonNull V batch(G toGetIn)
			{
				var found = this.getFunction.execute(toGetIn);
				if (found != null)
				{
					return found.value();
				}

				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				this.addFunction.add(key, value);

				return value;
			}
		}
	}

	public final static class BatchRelative<K, V, G, R> implements IBatchRelative<K, V, G, R>
	{
		private final IOIFunction<Node<K, V>, G>           getFunction;
		private final NodeComponents.IAddRelative<K, V, R> addFunction;

		public BatchRelative(IOIFunction<Node<K, V>, G> getFunctionIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn)
		{
			this.getFunction = getFunctionIn;
			this.addFunction = addFunctionIn;
		}

		public static <K, V, G, R> BatchRelative<K, V, G, R> make(IOIFunction<Node<K, V>, G> getterIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn)
		{
			return new BatchRelative<>(getterIn, addFunctionIn);
		}

		public static <K, V, R> BatchRelative<K, V, IOIFunction<Boolean, Node<K, V>>, R> byFunction(Nodes<K, V> rootIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn)
		{
			return new BatchRelative<>(NodeComponents.Get.byFunction(rootIn)::of, addFunctionIn);
		}

		public static <K, V, R> BatchRelative<K, V, Integer, R> byIndex(Nodes<K, V> rootIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn)
		{
			return new BatchRelative<>(NodeComponents.Get.byIndex(rootIn)::of, addFunctionIn);
		}

		public static <K, V, R> BatchRelative<K, V, V, R> byValue(Nodes<K, V> rootIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn)
		{
			return new BatchRelative<>(new NodeComponents.ValueGet<>(rootIn)::ofValue, addFunctionIn);
		}

		public static <K, V, R> BatchRelative<K, V, K, R> byKey(Nodes<K, V> rootIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn)
		{
			return new BatchRelative<>(NodeComponents.Get.byKey(rootIn)::of, addFunctionIn);
		}

		public @NonNull V batchBefore(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn, R beforeIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node.value();
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			this.addFunction.after(beforeIn, key, value);

			return value;
		}

		public @NonNull V batchAfter(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn, R beforeIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node.value();
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			this.addFunction.before(beforeIn, key, value);

			return value;
		}

		public static class CommonInitializer<K, V, G, R> implements IBatchRelativeCommonInitializer<K, V, G, R>
		{
			private final IOIFunction<Node<K, V>, G>           getFunction;
			private final NodeComponents.IAddRelative<K, V, R> addFunction;
			private final IOIFunction<K, G>                    keyInitializer;
			private final IOIFunction<V, K>                    valueInitializer;

			public CommonInitializer(IOIFunction<Node<K, V>, G> getFunctionIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				this.getFunction      = getFunctionIn;
				this.addFunction      = addFunctionIn;
				this.keyInitializer   = keyInitializerIn;
				this.valueInitializer = valueInitializerIn;
			}

			public static <K, V, G, R> CommonInitializer<K, V, G, R> make(IOIFunction<Node<K, V>, G> getterIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(getterIn, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V, R> CommonInitializer<K, V, IOIFunction<Boolean, Node<K, V>>, R> byFunction(Nodes<K, V> rootIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn, IOIFunction<K, IOIFunction<Boolean, Node<K, V>>> keyInitializerIn,
			                                                                                                IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byFunction(rootIn)::of, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V, R> CommonInitializer<K, V, Integer, R> byIndex(Nodes<K, V> rootIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn, IOIFunction<K, Integer> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byIndex(rootIn)::of, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V, R> CommonInitializer<K, V, V, R> byValue(Nodes<K, V> rootIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn, IOIFunction<K, V> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(new NodeComponents.ValueGet<>(rootIn)::ofValue, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V, R> CommonInitializer<K, V, K, R> byKey(Nodes<K, V> rootIn, NodeComponents.IAddRelative<K, V, R> addFunctionIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byKey(rootIn)::of, addFunctionIn, (keyIn) -> keyIn, valueInitializerIn);
			}

			public @NonNull V batchBefore(G toGetIn, R beforeIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node.value();
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				this.addFunction.after(beforeIn, key, value);

				return value;
			}

			public @NonNull V batchAfter(G toGetIn, R beforeIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node.value();
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				this.addFunction.before(beforeIn, key, value);

				return value;
			}
		}
	}

	public final static class BatchPosition<K, V, G> implements IBatchPosition<K, V, G>
	{
		private final IOIFunction<Node<K, V>, G>        getFunction;
		private final NodeComponents.IAddPosition<K, V> addFunction;

		public BatchPosition(IOIFunction<Node<K, V>, G> getFunctionIn, NodeComponents.IAddPosition<K, V> addFunctionIn)
		{
			this.getFunction = getFunctionIn;
			this.addFunction = addFunctionIn;
		}

		public static <K, V, G, R> BatchPosition<K, V, G> make(IOIFunction<Node<K, V>, G> getterIn, NodeComponents.IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(getterIn, addFunctionIn);
		}

		public static <K, V, R> BatchPosition<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn, NodeComponents.IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(NodeComponents.Get.byFunction(rootIn)::of, addFunctionIn);
		}

		public static <K, V, R> BatchPosition<K, V, Integer> byIndex(Nodes<K, V> rootIn, NodeComponents.IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(NodeComponents.Get.byIndex(rootIn)::of, addFunctionIn);
		}

		public static <K, V, R> BatchPosition<K, V, V> byValue(Nodes<K, V> rootIn, NodeComponents.IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(new NodeComponents.ValueGet<>(rootIn)::ofValue, addFunctionIn);
		}

		public static <K, V, R> BatchPosition<K, V, K> byKey(Nodes<K, V> rootIn, NodeComponents.IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(NodeComponents.Get.byKey(rootIn)::of, addFunctionIn);
		}

		public @NonNull V batchBegin(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node.value();
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			this.addFunction.begin(key, value);

			return value;
		}

		public @NonNull V batchMiddle(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node.value();
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			this.addFunction.middle(key, value);

			return value;
		}

		public @NonNull V batchEnd(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node.value();
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			this.addFunction.end(key, value);

			return value;
		}

		public static class CommonInitializer<K, V, G> implements IBatchPositionCommonInitializer<K, V, G>
		{
			private final IOIFunction<Node<K, V>, G>        getFunction;
			private final NodeComponents.IAddPosition<K, V> addFunction;
			private final IOIFunction<K, G>                 keyInitializer;
			private final IOIFunction<V, K>                 valueInitializer;

			public CommonInitializer(IOIFunction<Node<K, V>, G> getFunctionIn, NodeComponents.IAddPosition<K, V> addFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				this.getFunction      = getFunctionIn;
				this.addFunction      = addFunctionIn;
				this.keyInitializer   = keyInitializerIn;
				this.valueInitializer = valueInitializerIn;
			}

			public static <K, V, G> CommonInitializer<K, V, G> make(IOIFunction<Node<K, V>, G> getterIn, NodeComponents.IAddPosition<K, V> addFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(getterIn, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn, NodeComponents.IAddPosition<K, V> addFunctionIn, IOIFunction<K, IOIFunction<Boolean, Node<K, V>>> keyInitializerIn,
			                                                                                          IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byFunction(rootIn)::of, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, Integer> byIndex(Nodes<K, V> rootIn, NodeComponents.IAddPosition<K, V> addFunctionIn, IOIFunction<K, Integer> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byIndex(rootIn)::of, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, V> byValue(Nodes<K, V> rootIn, NodeComponents.IAddPosition<K, V> addFunctionIn, IOIFunction<K, V> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(new NodeComponents.ValueGet<>(rootIn)::ofValue, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, K> byKey(Nodes<K, V> rootIn, NodeComponents.IAddPosition<K, V> addFunctionIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(NodeComponents.Get.byKey(rootIn)::of, addFunctionIn, (keyIn) -> keyIn, valueInitializerIn);
			}

			public @NonNull V batchBegin(G toGetIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node.value();
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				this.addFunction.begin(key, value);

				return value;
			}

			public @NonNull V batchMiddle(G toGetIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node.value();
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				this.addFunction.middle(key, value);

				return value;
			}

			public @NonNull V batchEnd(G toGetIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node.value();
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				this.addFunction.end(key, value);

				return value;
			}
		}
	}

	public static class AddRelative<K, V, G, P> implements IAddRelative<K, V, G, P>
	{
		private final P                            parent;
		private final Nodes<K, V>                  root;
		private final NodeComponents.IGet<K, V, G> getFunction;

		public AddRelative(P parentIn, Nodes<K, V> rootIn, NodeComponents.IGet<K, V, G> getFunctionIn)
		{
			this.parent      = parentIn;
			this.root        = rootIn;
			this.getFunction = getFunctionIn;
		}

		/**
		 * Add new node from keyIn and valueIn before node got by functionIn. Create new node only if functionIn allow to get node.
		 */
		public P before(G beforeIn, final K keyIn, final V valueIn)
		{
			var node = this.getFunction.of(beforeIn);

			if (node != null)
			{
				node.addBefore(new Node<>(this.root, keyIn, valueIn));

				return this.parent;
			}

			return this.parent;
		}

		/**
		 * Add new node from keyIn and valueIn after node got by functionIn. Create new node only if functionIn allow to get node.
		 */
		public P after(G afterIn, final K keyIn, final V valueIn)
		{
			var node = this.getFunction.of(afterIn);

			if (node != null)
			{
				node.addAfter(new Node<>(this.root, keyIn, valueIn));

				return this.parent;
			}

			return this.parent;
		}
	}

	// Read-only components

	public abstract static class Get<K, V, G> implements IGet<K, V, G>
	{
		protected final Nodes<K, V> root;

		public Get(final Nodes<K, V> rootIn)
		{
			this.root = rootIn;
		}

		public static <K, V> Get<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn)
		{
			return new Get<>(rootIn)
			{
				@Override
				public V ofReverse(final IOIFunction<Boolean, Node<K, V>> toGetIn)
				{
					var current = this.root.last();
					while (current != null)
					{
						if (toGetIn.execute(current))
						{
							return current.value();
						}

						current = current.previous();
					}

					return null;
				}

				@Override
				public V of(final IOIFunction<Boolean, Node<K, V>> toGetIn)
				{
					var current = this.root.first();

					while (current != null)
					{
						if (toGetIn.execute(current))
						{
							return current.value();
						}

						current = current.next();
					}

					return null;
				}
			};
		}

		public static <K, V> Get<K, V, Integer> byIndex(Nodes<K, V> rootIn)
		{
			return new Get<>(rootIn)
			{
				@Override
				public V ofReverse(Integer toGetIn)
				{
					return ofReverse(toGetIn, this.root.size() - 1);
				}

				private V ofReverse(int indexIn, int lastIndexIn)
				{
					var current = this.root.last();
					int index   = lastIndexIn;
					while (current != null)
					{
						if (index == indexIn)
						{
							return current.value();
						}

						current = current.previous();
						index--;
					}

					return null;
				}

				@Override
				public V of(Integer toGetIn)
				{
					var lastIndex = this.root.size() - 1;
					if (toGetIn > lastIndex / 2)
					{
						return ofReverse(toGetIn, lastIndex);
					}

					var current = this.root.first();
					int index   = 0;
					while (current != null)
					{
						if (index == toGetIn)
						{
							return current.value();
						}

						current = current.next();
						index++;
					}

					return null;
				}
			};
		}

		public static <K, V> Get<K, V, K> byKey(Nodes<K, V> rootIn)
		{
			return new Get<>(rootIn)
			{
				@Override
				public V ofReverse(K keyIn)
				{
					var current = this.root.last();
					while (current != null)
					{
						if (is(current.key(), keyIn))
						{
							return current.value();
						}

						current = current.previous();
					}

					return null;
				}

				@Override
				public V of(K keyIn)
				{
					var current = this.root.first();

					while (current != null)
					{
						if (is(current.key(), keyIn))
						{
							return current.value();
						}

						current = current.next();
					}

					return null;
				}
			};
		}

		public abstract V ofReverse(G toGetIn);

		public abstract V of(G toGetIn);
	}

	public final static class PositionGet<K, V> implements IComponent<K, V>
	{
		private final Nodes<K, V>         root;
		private final IGet<K, V, Integer> indexGet;

		public PositionGet(final Nodes<K, V> rootIn, IGet<K, V, Integer> indexGetIn)
		{
			this.root     = rootIn;
			this.indexGet = indexGetIn;
		}

		public static <K, V> PositionGet<K, V> make(final Nodes<K, V> rootIn)
		{
			return new PositionGet<>(rootIn, Get.byIndex(rootIn));
		}

		public V first()
		{
			if (this.root.first() != null)
			{
				return this.root.first().value();
			}

			return null;
		}

		public V middle()
		{
			return this.indexGet.of(this.root.size() / 2);
		}

		public V last()
		{
			if (this.root.last() != null)
			{
				return this.root.last().value();
			}

			return null;
		}
	}

	public final static class ValueGet<K, V> implements IValueGet<K, V>, IComponent<K, V> // I create the ValueGet class separately from Get, because having a method to retrieve a node via K or via V with the same name would collide
	{
		private final Nodes<K, V> root;

		public ValueGet(final Nodes<K, V> rootIn)
		{
			this.root = rootIn;
		}

		public V ofValueReverse(V valueIn)
		{
			var current = this.root.first();

			while (current != null)
			{
				if (is(current.value(), valueIn))
				{
					return current.value();
				}

				current = current.previous();
			}

			return null;
		}

		public V ofValue(V valueIn)
		{
			var current = this.root.first();

			while (current != null)
			{
				if (is(current.value(), valueIn))
				{
					return current.value();
				}

				current = current.next();
			}

			return null;
		}
	}

	public static class Each<K, V, P> implements IComponent<K, V>
	{
		private final P           parent;
		private final Nodes<K, V> root;

		public Each(final P parentIn, final Nodes<K, V> rootIn)
		{
			this.parent = parentIn;
			this.root   = rootIn;
		}

		public P takeWhileReverse(IOIFunction<Boolean, V> functionIn)
		{
			var current = this.root.last();

			while (current != null && functionIn.execute(current.value()))
			{
				current = current.previous();
			}

			return this.parent;
		}

		public P takeWhile(IOIFunction<Boolean, V> functionIn)
		{
			var current = this.root.first();

			while (current != null && functionIn.execute(current.value()))
			{
				current = current.next();
			}

			return this.parent;
		}

		public P eachReverse(IIFunction<V> functionIn)
		{
			var current = this.root.last();

			while (current != null)
			{
				functionIn.execute(current.value());

				current = current.previous();
			}

			return this.parent;
		}

		public P each(IIFunction<V> functionIn)
		{
			var current = this.root.first();

			while (current != null)
			{
				functionIn.execute(current.value());

				current = current.next();
			}

			return this.parent;
		}
	}
}