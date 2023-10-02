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
public class NodeComponents
{
	public static <T> boolean is(T fromIn, T toIn)
	{
		return fromIn.hashCode() == toIn.hashCode() && fromIn.equals(toIn);
	}

	public interface IComponent<K, V>
	{

	}

	public interface IAdd<K, V>
	{
		Node<K, V> add(K keyIn, V valueIn);
	}

	public interface IAddRelative<K, V, G>
	{
		Node<K, V> before(G toGetIn, K keyIn, V valueIn);

		Node<K, V> after(G toGetIn, K keyIn, V valueIn);
	}

	public interface IAddPosition<K, V>
	{
		Node<K, V> begin(K keyIn, V valueIn);

		Node<K, V> middle(K keyIn, V valueIn);

		Node<K, V> end(K keyIn, V valueIn); // Equivalent to Add with IAdd<K, V>
	}

	public interface IGet<K, V, G>
	{
		Node<K, V> ofReverse(G toGetIn);

		Node<K, V> of(G toGetIn);
	}

	public interface IValueGet<K, V> // I create the ValueGet class separately from Get, because having a method to retrieve a node via K or via V with the same name would collide
	{
		Node<K, V> ofValueReverse(V valueIn);

		Node<K, V> ofValue(V valueIn);
	}

	public interface IBatch<K, V, G>
	{
		Node<K, V> batch(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn);
	}

	public interface IBatchCommonInitializer<K, V, G>
	{
		Node<K, V> batch(G toGetIn);
	}

	public interface IBatchRelative<K, V, G, R>
	{
		Node<K, V> batchBefore(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn, R beforeIn);

		Node<K, V> batchAfter(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn, R afterIn);
	}

	public interface IBatchRelativeCommonInitializer<K, V, G, R>
	{
		Node<K, V> batchBefore(G toGetIn, R beforeIn);

		Node<K, V> batchAfter(G toGetIn, R afterIn);
	}

	public interface IBatchPosition<K, V, G>
	{
		Node<K, V> batchBegin(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn);

		Node<K, V> batchMiddle(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn);

		Node<K, V> batchEnd(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn);
	}

	public interface IBatchPositionCommonInitializer<K, V, G>
	{
		Node<K, V> batchBegin(G toGetIn);

		Node<K, V> batchMiddle(G toGetIn);

		Node<K, V> batchEnd(G toGetIn);
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

	public static class Add<K, V> implements IAdd<K, V>, IComponent<K, V>
	{
		private final Nodes<K, V> root;

		public Add(final Nodes<K, V> rootIn)
		{
			this.root = rootIn;
		}

		public Node<K, V> add(K keyIn, V valueIn)
		{
			var node = new Node<>(this.root, keyIn, valueIn);

			this.root.afterLast(node);

			return node;
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
			return new Remove<>(Get.byFunction(rootIn)::of, parentIn);
		}

		public static <K, V> Remove<K, V, IOIFunction<Boolean, Node<K, V>>, Nodes<K, V>> byFunction(Nodes<K, V> rootIn)
		{
			return new Remove<>(Get.byFunction(rootIn)::of, rootIn);
		}

		public static <K, V, P> Remove<K, V, Integer, P> byIndex(Nodes<K, V> rootIn, P parentIn)
		{
			return new Remove<>(Get.byIndex(rootIn)::of, parentIn);
		}

		public static <K, V> Remove<K, V, Integer, Nodes<K, V>> byIndex(Nodes<K, V> rootIn)
		{
			return new Remove<>(Get.byIndex(rootIn)::of, rootIn);
		}

		public static <K, V, P> Remove<K, V, V, P> byValue(Nodes<K, V> rootIn, P parentIn)
		{
			return new Remove<>(new ValueGet<>(rootIn)::ofValue, parentIn);
		}

		public static <K, V> Remove<K, V, V, Nodes<K, V>> byValue(Nodes<K, V> rootIn)
		{
			return new Remove<>(new ValueGet<>(rootIn)::ofValue, rootIn);
		}

		public static <K, V, P> Remove<K, V, K, P> byKey(Nodes<K, V> rootIn, P parentIn)
		{
			return new Remove<>(Get.byKey(rootIn)::of, parentIn);
		}

		public static <K, V> Remove<K, V, K, Nodes<K, V>> byKey(Nodes<K, V> rootIn)
		{
			return new Remove<>(Get.byKey(rootIn)::of, rootIn);
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
		private final IAdd<K, V>                 adder;

		public Batch(IOIFunction<Node<K, V>, G> getFunctionIn, IAdd<K, V> addFunctionIn)
		{
			this.getFunction = getFunctionIn;
			this.adder       = addFunctionIn;
		}

		public static <K, V, G> Batch<K, V, G> make(IOIFunction<Node<K, V>, G> getterIn, IAdd<K, V> adderIn)
		{
			return new Batch<>(getterIn, adderIn);
		}

		public static <K, V> Batch<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn, IAdd<K, V> adderIn)
		{
			return new Batch<>(Get.byFunction(rootIn)::of, adderIn);
		}

		public static <K, V> Batch<K, V, Integer> byIndex(Nodes<K, V> rootIn, IAdd<K, V> adderIn)
		{
			return new Batch<>(Get.byIndex(rootIn)::of, adderIn);
		}

		public static <K, V> Batch<K, V, V> byValue(Nodes<K, V> rootIn, IAdd<K, V> adderIn)
		{
			return new Batch<>(new ValueGet<>(rootIn)::ofValue, adderIn);
		}

		public static <K, V> Batch<K, V, K> byKey(Nodes<K, V> rootIn, IAdd<K, V> adderIn)
		{
			return new Batch<>(Get.byKey(rootIn)::of, adderIn);
		}

		@Override
		public @NonNull Node<K, V> batch(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node;
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			return this.adder.add(key, value);
		}

		public static class CommonInitializer<K, V, G> implements IBatchCommonInitializer<K, V, G>
		{
			private final IOIFunction<Node<K, V>, G> getFunction;
			private final IAdd<K, V>                 addFunction;
			private final IOIFunction<K, G>          keyInitializer;
			private final IOIFunction<V, K>          valueInitializer;

			public CommonInitializer(IOIFunction<Node<K, V>, G> getFunctionIn, IAdd<K, V> addFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				this.getFunction      = getFunctionIn;
				this.addFunction      = addFunctionIn;
				this.keyInitializer   = keyInitializerIn;
				this.valueInitializer = valueInitializerIn;
			}

			public static <K, V, G> CommonInitializer<K, V, G> make(IOIFunction<Node<K, V>, G> getterIn, IAdd<K, V> adderIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(getterIn, adderIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn, IAdd<K, V> adderIn, IOIFunction<K, IOIFunction<Boolean, Node<K, V>>> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(Get.byFunction(rootIn)::of, adderIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, Integer> byIndex(Nodes<K, V> rootIn, IAdd<K, V> adderIn, IOIFunction<K, Integer> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(Get.byIndex(rootIn)::of, adderIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, V> byValue(Nodes<K, V> rootIn, IAdd<K, V> adderIn, IOIFunction<K, V> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(new ValueGet<>(rootIn)::ofValue, adderIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> CommonInitializer<K, V, K> byKey(Nodes<K, V> rootIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new CommonInitializer<>(Get.byKey(rootIn)::of, new Add<>(rootIn), (keyIn) -> keyIn, valueInitializerIn);
			}

			public @NonNull Node<K, V> batch(G toGetIn)
			{
				var found = this.getFunction.execute(toGetIn);
				if (found != null)
				{
					return found;
				}

				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				return this.addFunction.add(key, value);
			}
		}
	}

	public final static class BatchRelative<K, V, G, R> implements IBatchRelative<K, V, G, R>
	{
		private final IOIFunction<Node<K, V>, G> getFunction;
		private final IAddRelative<K, V, R>      placeFunction;

		public BatchRelative(IOIFunction<Node<K, V>, G> getFunctionIn, IAddRelative<K, V, R> placeFunction)
		{
			this.getFunction   = getFunctionIn;
			this.placeFunction = placeFunction;
		}

		public static <K, V, G, R> BatchRelative<K, V, G, R> make(IOIFunction<Node<K, V>, G> getterIn, IAddRelative<K, V, R> placeIn)
		{
			return new BatchRelative<>(getterIn, placeIn);
		}

		public static <K, V, R> BatchRelative<K, V, IOIFunction<Boolean, Node<K, V>>, R> byFunction(Nodes<K, V> rootIn, IAddRelative<K, V, R> placeIn)
		{
			return new BatchRelative<>(Get.byFunction(rootIn)::of, placeIn);
		}

		public static <K, V, R> BatchRelative<K, V, Integer, R> byIndex(Nodes<K, V> rootIn, IAddRelative<K, V, R> placeIn)
		{
			return new BatchRelative<>(Get.byIndex(rootIn)::of, placeIn);
		}

		public static <K, V, R> BatchRelative<K, V, V, R> byValue(Nodes<K, V> rootIn, IAddRelative<K, V, R> placeIn)
		{
			return new BatchRelative<>(new ValueGet<>(rootIn)::ofValue, placeIn);
		}

		public static <K, V, R> BatchRelative<K, V, K, R> byKey(Nodes<K, V> rootIn, IAddRelative<K, V, R> placeIn)
		{
			return new BatchRelative<>(Get.byKey(rootIn)::of, placeIn);
		}

		public @NonNull Node<K, V> batchBefore(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn, R beforeIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node;
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			return this.placeFunction.after(beforeIn, key, value);
		}

		public @NonNull Node<K, V> batchAfter(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn, R beforeIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node;
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			return this.placeFunction.before(beforeIn, key, value);
		}

		public static class CommonInitializer<K, V, G, R> implements IBatchRelativeCommonInitializer<K, V, G, R>
		{
			private final IOIFunction<Node<K, V>, G> getFunction;
			private final IAddRelative<K, V, R>      placeFunction;
			private final IOIFunction<K, G>          keyInitializer;
			private final IOIFunction<V, K>          valueInitializer;

			public CommonInitializer(IOIFunction<Node<K, V>, G> getFunctionIn, IAddRelative<K, V, R> placeFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				this.getFunction      = getFunctionIn;
				this.placeFunction    = placeFunctionIn;
				this.keyInitializer   = keyInitializerIn;
				this.valueInitializer = valueInitializerIn;
			}

			public static <K, V, G, R> BatchRelative.CommonInitializer<K, V, G, R> make(IOIFunction<Node<K, V>, G> getterIn, IAddRelative<K, V, R> placeIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchRelative.CommonInitializer<>(getterIn, placeIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V, R> BatchRelative.CommonInitializer<K, V, IOIFunction<Boolean, Node<K, V>>, R> byFunction(Nodes<K, V> rootIn, IAddRelative<K, V, R> placeIn, IOIFunction<K, IOIFunction<Boolean, Node<K, V>>> keyInitializerIn,
			                                                                                                              IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchRelative.CommonInitializer<>(Get.byFunction(rootIn)::of, placeIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V, R> BatchRelative.CommonInitializer<K, V, Integer, R> byIndex(Nodes<K, V> rootIn, IAddRelative<K, V, R> placeIn, IOIFunction<K, Integer> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchRelative.CommonInitializer<>(Get.byIndex(rootIn)::of, placeIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V, R> BatchRelative.CommonInitializer<K, V, V, R> byValue(Nodes<K, V> rootIn, IAddRelative<K, V, R> placeIn, IOIFunction<K, V> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchRelative.CommonInitializer<>(new ValueGet<>(rootIn)::ofValue, placeIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V, R> BatchRelative.CommonInitializer<K, V, K, R> byKey(Nodes<K, V> rootIn, IAddRelative<K, V, R> placeIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchRelative.CommonInitializer<>(Get.byKey(rootIn)::of, placeIn, (keyIn) -> keyIn, valueInitializerIn);
			}

			public @NonNull Node<K, V> batchBefore(G toGetIn, R beforeIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node;
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				return this.placeFunction.after(beforeIn, key, value);
			}

			public @NonNull Node<K, V> batchAfter(G toGetIn, R beforeIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node;
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				return this.placeFunction.before(beforeIn, key, value);
			}
		}
	}

	public final static class BatchPosition<K, V, G> implements IBatchPosition<K, V, G>
	{
		private final IOIFunction<Node<K, V>, G> getFunction;
		private final IAddPosition<K, V>         addFunction;

		public BatchPosition(IOIFunction<Node<K, V>, G> getFunctionIn, IAddPosition<K, V> addFunctionIn)
		{
			this.getFunction = getFunctionIn;
			this.addFunction = addFunctionIn;
		}

		public static <K, V, G, R> BatchPosition<K, V, G> make(IOIFunction<Node<K, V>, G> getterIn, IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(getterIn, addFunctionIn);
		}

		public static <K, V, R> BatchPosition<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn, IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(Get.byFunction(rootIn)::of, addFunctionIn);
		}

		public static <K, V, R> BatchPosition<K, V, Integer> byIndex(Nodes<K, V> rootIn, IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(Get.byIndex(rootIn)::of, addFunctionIn);
		}

		public static <K, V, R> BatchPosition<K, V, V> byValue(Nodes<K, V> rootIn, IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(new ValueGet<>(rootIn)::ofValue, addFunctionIn);
		}

		public static <K, V, R> BatchPosition<K, V, K> byKey(Nodes<K, V> rootIn, IAddPosition<K, V> addFunctionIn)
		{
			return new BatchPosition<>(Get.byKey(rootIn)::of, addFunctionIn);
		}

		public @NonNull Node<K, V> batchBegin(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node;
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			return this.addFunction.begin(key, value);
		}

		public @NonNull Node<K, V> batchMiddle(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node;
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			return this.addFunction.middle(key, value);
		}

		public @NonNull Node<K, V> batchEnd(G toGetIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
		{
			var node = this.getFunction.execute(toGetIn);

			if (node != null)
			{
				return node;
			}
			var key   = keyInitializerIn.execute(toGetIn);
			var value = valueInitializerIn.execute(key);

			return this.addFunction.end(key, value);
		}

		public static class CommonInitializer<K, V, G> implements IBatchPositionCommonInitializer<K, V, G>
		{
			private final IOIFunction<Node<K, V>, G> getFunction;
			private final IAddPosition<K, V>         addFunction;
			private final IOIFunction<K, G>          keyInitializer;
			private final IOIFunction<V, K>          valueInitializer;

			public CommonInitializer(IOIFunction<Node<K, V>, G> getFunctionIn, IAddPosition<K, V> addFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				this.getFunction      = getFunctionIn;
				this.addFunction      = addFunctionIn;
				this.keyInitializer   = keyInitializerIn;
				this.valueInitializer = valueInitializerIn;
			}

			public static <K, V, G> BatchPosition.CommonInitializer<K, V, G> make(IOIFunction<Node<K, V>, G> getterIn, IAddPosition<K, V> addFunctionIn, IOIFunction<K, G> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchPosition.CommonInitializer<>(getterIn, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> BatchPosition.CommonInitializer<K, V, IOIFunction<Boolean, Node<K, V>>> byFunction(Nodes<K, V> rootIn, IAddPosition<K, V> addFunctionIn, IOIFunction<K, IOIFunction<Boolean, Node<K, V>>> keyInitializerIn,
			                                                                                                        IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchPosition.CommonInitializer<>(Get.byFunction(rootIn)::of, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> BatchPosition.CommonInitializer<K, V, Integer> byIndex(Nodes<K, V> rootIn, IAddPosition<K, V> addFunctionIn, IOIFunction<K, Integer> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchPosition.CommonInitializer<>(Get.byIndex(rootIn)::of, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> BatchPosition.CommonInitializer<K, V, V> byValue(Nodes<K, V> rootIn, IAddPosition<K, V> addFunctionIn, IOIFunction<K, V> keyInitializerIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchPosition.CommonInitializer<>(new ValueGet<>(rootIn)::ofValue, addFunctionIn, keyInitializerIn, valueInitializerIn);
			}

			public static <K, V> BatchPosition.CommonInitializer<K, V, K> byKey(Nodes<K, V> rootIn, IAddPosition<K, V> addFunctionIn, IOIFunction<V, K> valueInitializerIn)
			{
				return new BatchPosition.CommonInitializer<>(Get.byKey(rootIn)::of, addFunctionIn, (keyIn) -> keyIn, valueInitializerIn);
			}

			public @NonNull Node<K, V> batchBegin(G toGetIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node;
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				return this.addFunction.begin(key, value);
			}

			public @NonNull Node<K, V> batchMiddle(G toGetIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node;
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				return this.addFunction.middle(key, value);
			}

			public @NonNull Node<K, V> batchEnd(G toGetIn)
			{
				var node = this.getFunction.execute(toGetIn);

				if (node != null)
				{
					return node;
				}
				var key   = this.keyInitializer.execute(toGetIn);
				var value = this.valueInitializer.execute(key);

				return this.addFunction.end(key, value);
			}
		}
	}

	public static class Place<K, V, G> implements IAddRelative<K, V, G>
	{
		private final Nodes<K, V>   root;
		private final IGet<K, V, G> getFunction;

		public Place(Nodes<K, V> rootIn, IGet<K, V, G> getFunctionIn)
		{
			this.root        = rootIn;
			this.getFunction = getFunctionIn;
		}

		/**
		 * Add new node from keyIn and valueIn before node got by functionIn. Create new node only if functionIn allow to get node.
		 */
		public @NonNull Node<K, V> before(G beforeIn, final K keyIn, final V valueIn)
		{
			var node = this.getFunction.of(beforeIn);

			if (node != null)
			{
				var newNode = new Node<>(this.root, keyIn, valueIn);
				node.addBefore(newNode);

				return newNode;
			}

			return null;
		}

		/**
		 * Add new node from keyIn and valueIn after node got by functionIn. Create new node only if functionIn allow to get node.
		 */
		public @NonNull Node<K, V> after(G afterIn, final K keyIn, final V valueIn)
		{
			var node = this.getFunction.of(afterIn);

			if (node != null)
			{
				Node<K, V> newNode = new Node<>(this.root, keyIn, valueIn);
				node.addAfter(newNode);

				return newNode;
			}

			return null;
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
				public @NonNull Node<K, V> ofReverse(final IOIFunction<Boolean, Node<K, V>> toGetIn)
				{
					var current = this.root.last();
					while (current != null)
					{
						if (toGetIn.execute(current))
						{
							return current;
						}

						current = current.previous();
					}

					return null;
				}

				@Override
				public @NonNull Node<K, V> of(final IOIFunction<Boolean, Node<K, V>> toGetIn)
				{
					var current = this.root.first();

					while (current != null)
					{
						if (toGetIn.execute(current))
						{
							return current;
						}

						current = current.next();
					}

					return null;
				}
			};
		}

		public static <K, V> Get<K, V, Integer> byIndex(Nodes<K, V> rootIn)
		{
			return new Get<K, V, Integer>(rootIn)
			{
				@Override
				public Node<K, V> ofReverse(Integer toGetIn)
				{
					return ofReverse(toGetIn, this.root.size() - 1);
				}

				public Node<K, V> ofReverse(int indexIn, int lastIndexIn)
				{
					var current = this.root.last();
					int index   = lastIndexIn;
					while (current != null)
					{
						if (index == indexIn)
						{
							return current;
						}

						current = current.previous();
						index--;
					}

					return null;
				}

				@Override
				public Node<K, V> of(Integer toGetIn)
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
							return current;
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
				public Node<K, V> ofReverse(K keyIn)
				{
					var current = this.root.last();
					while (current != null)
					{
						if (is(current.key(), keyIn))
						{
							return current;
						}

						current = current.previous();
					}

					return null;
				}

				@Override
				public Node<K, V> of(K keyIn)
				{
					var current = this.root.first();

					while (current != null)
					{
						if (is(current.key(), keyIn))
						{
							return current;
						}

						current = current.next();
					}

					return null;
				}
			};
		}

		public abstract Node<K, V> ofReverse(G toGetIn);

		public abstract Node<K, V> of(G toGetIn);
	}

	public final static class ExtremumGet<K, V> implements IComponent<K, V>
	{
		private final Nodes<K, V> root;

		public ExtremumGet(final Nodes<K, V> rootIn)
		{
			this.root = rootIn;
		}

		public static <K, V> ExtremumGet<K, V> make(final Nodes<K, V> rootIn)
		{
			return new ExtremumGet<>(rootIn);
		}

		public Node<K, V> first()
		{
			return this.root.first();
		}

		public Node<K, V> last()
		{
			return this.root.last();
		}
	}

	public final static class MiddleGet<K, V> implements IComponent<K, V>
	{
		private final Nodes<K, V>         root;
		private final IGet<K, V, Integer> indexGet;

		public MiddleGet(final Nodes<K, V> rootIn, IGet<K, V, Integer> indexGetIn)
		{
			this.root     = rootIn;
			this.indexGet = indexGetIn;
		}

		public static <K, V> MiddleGet<K, V> make(final Nodes<K, V> rootIn)
		{
			return new MiddleGet<>(rootIn, Get.byIndex(rootIn));
		}

		public Node<K, V> middle()
		{
			return this.indexGet.of(this.root.size() / 2);
		}
	}

	public final static class ValueGet<K, V> implements IValueGet<K, V>, IComponent<K, V> // I create the ValueGet class separately from Get, because having a method to retrieve a node via K or via V with the same name would collide
	{
		private final Nodes<K, V> root;

		public ValueGet(final Nodes<K, V> rootIn)
		{
			this.root = rootIn;
		}

		public Node<K, V> ofValueReverse(V valueIn)
		{
			var current = this.root.first();

			while (current != null)
			{
				if (is(current.value(), valueIn))
				{
					return current;
				}

				current = current.previous();
			}

			return null;
		}

		public Node<K, V> ofValue(V valueIn)
		{
			var current = this.root.first();

			while (current != null)
			{
				if (is(current.value(), valueIn))
				{
					return current;
				}

				current = current.next();
			}

			return null;
		}
	}

	public static class Each<K, V> implements IComponent<K, V>
	{
		private final Nodes<K, V> root;

		public Each(final Nodes<K, V> rootIn)
		{
			this.root = rootIn;
		}

		public Nodes<K, V> takeWhileReverse(IOIFunction<Boolean, Node<K, V>> functionIn)
		{
			var current = this.root.last();

			while (current != null && functionIn.execute(current))
			{
				current = current.previous();
			}

			return this.root;
		}

		public Nodes<K, V> takeWhile(IOIFunction<Boolean, Node<K, V>> functionIn)
		{
			var current = this.root.first();

			while (current != null && functionIn.execute(current))
			{
				current = current.next();
			}

			return this.root;
		}

		public Nodes<K, V> eachReverse(IIFunction<Node<K, V>> functionIn)
		{
			var current = this.root.last();

			while (current != null)
			{
				functionIn.execute(current);

				current = current.previous();
			}

			return this.root;
		}

		public Nodes<K, V> each(IIFunction<Node<K, V>> functionIn)
		{
			var current = this.root.first();

			while (current != null)
			{
				functionIn.execute(current);

				current = current.next();
			}

			return this.root;
		}
	}
}