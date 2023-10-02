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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class Node<K, V>
{
	private final         Nodes<K, V> root;
	private final @Getter K           key;
	private final @Getter V           value;
	private @Getter       Node<K, V>  previous;
	private @Getter       Node<K, V>  next;

	public Node(Nodes<K, V> rootIn, K keyIn, V targetIn)
	{
		this.root  = rootIn;
		this.key   = keyIn;
		this.value = targetIn;
	}

	public final Node<K, V> addBefore(Node<K, V> nodeIn)
	{
		if (this.previous != null)
		{
			this.previous.next = nodeIn;
			nodeIn.previous    = this.previous;
		}

		this.previous = nodeIn;
		nodeIn.next   = this;

		this.root.incSize();
		return this;
	}

	public final Node<K, V> addAfter(Node<K, V> nodeIn)
	{
		if (this.next != null)
		{
			this.next.previous = nodeIn;
			nodeIn.next        = this.next;
		}

		this.next       = nodeIn;
		nodeIn.previous = this;

		this.root.updateLast(this, nodeIn);
		this.root.incSize();

		return this;
	}

	public final boolean is(K keyIn)
	{
		return Nodes.same(this.key, keyIn);
	}

	public final void delete()
	{
		if (this.previous != null)
		{
			this.previous.next = this.next;
		}

		if (this.next != null)
		{
			this.next.previous = this.previous;
		}

		this.root.updateLast(this, this.previous);
		this.root.decSize();
	}

	@Override
	public String toString()
	{
		return this.value.toString();
	}
}