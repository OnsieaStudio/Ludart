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

package fr.onsiea.ludart.common;

import fr.onsiea.ludart.common.modules.nodes.Components;
import fr.onsiea.ludart.common.modules.nodes.Node;
import fr.onsiea.ludart.common.modules.nodes.NodeComponents;
import fr.onsiea.ludart.common.modules.nodes.Nodes;
import fr.onsiea.tools.utils.function.IOIFunction;
import lombok.experimental.Delegate;

public class MapBatch<K, V>
{
	private final @Delegate Nodes<K, V> root;

	private final @Delegate Components.Each<K, V, MapBatch<K, V>> each;

	private final @Delegate Components.IGet<K, V, IOIFunction<Boolean, Node<K, V>>> functionGet;
	private final @Delegate Components.IGet<K, V, Integer>                          indexGet;
	private final @Delegate Components.ValueGet<K, V>                               valueGet;
	private final @Delegate Components.IGet<K, V, K>                                keyGet;
	private final @Delegate Components.PositionGet<K, V>                            positionGet;

	private final @Delegate Components.IBatchCommonInitializer<K, V, K> batch;

	private final @Delegate Components.IRemove<MapBatch<K, V>, K> remove;

	public MapBatch(IOIFunction<V, K> commonInitializerIn)
	{
		this.root = new Nodes<>();

		this.each = new Components.Each<>(this, this.root);

		this.functionGet = Components.Get.byFunction(this.root);
		this.indexGet    = Components.Get.byIndex(this.root);
		this.valueGet    = new Components.ValueGet<>(this.root);
		this.keyGet      = Components.Get.byKey(this.root);
		this.positionGet = new Components.PositionGet<>(this.root, this.indexGet);

		var nodeKeyGet = NodeComponents.Get.byKey(this.root);
		this.batch = Components.Batch.CommonInitializer.make(nodeKeyGet::of, new NodeComponents.Add<>(this.root), (keyIn) -> keyIn, commonInitializerIn);

		this.remove = Components.Remove.make(nodeKeyGet::of, this);
	}
}