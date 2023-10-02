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

public class Nodes<K, V>
{
	Node<K, V> first;
	Node<K, V> last;
	int        size;

	public static <K, V> Nodes<K, V> copy(Nodes<K, V> rootIn)
	{
		Nodes<K, V> copiedRoot = new Nodes<>();

		var current = rootIn.first;

		while (current != null)
		{
			copiedRoot.afterLast(new Node<>(copiedRoot, current.key(), current.value()));
			current = current.next();
		}

		return copiedRoot;
	}

	void afterLast(Node<K, V> nodeIn)
	{
		var current = this.last;

		if (current == null)
		{
			this.last = this.first;
		}

		if (this.last == null)
		{
			if (this.first != null)
			{
				throw new NullPointerException("[ERROR] Nodes : last is null, but first isn't null !");
			}

			this.last  = nodeIn;
			this.first = this.last;
			incSize();
		}
		else
		{
			if (this.first == null)
			{
				throw new NullPointerException("[ERROR] Nodes : last isn't null, but first is null !");
			}

			this.last.addAfter(nodeIn); // add node increase root size (root and node are linked)
			this.last = nodeIn;
		}
	}

	void incSize()
	{
		this.size++;
	}

	public static <T> boolean same(T fromIn, T toIn)
	{
		if (fromIn == null)
		{
			return false;
		}
		if (toIn == null)
		{
			return false;
		}
		if (fromIn.hashCode() != toIn.hashCode())
		{
			return false;
		}

		return fromIn.equals(toIn);
	}

	public boolean correct(int indexIn)
	{
		if (indexIn < 0)
		{
			throw new IndexOutOfBoundsException("[ERROR] Nodes : index is out of bounds ! " + indexIn + " < 0");
		}

		if (indexIn >= this.size())
		{
			throw new IndexOutOfBoundsException("[ERROR] Nodes : index is out of bounds ! " + indexIn + " >= " + this.size());
		}

		return true;
	}

	public int size()
	{
		return this.size;
	}

	void beforeFirst(Node<K, V> nodeIn)
	{
		if (this.first != null)
		{
			this.first.addBefore(nodeIn); // add node increase root size (root and node are linked)
		}
		else
		{
			incSize();
		}

		this.first = nodeIn;

		lastIfNull(nodeIn);
	}

	void lastIfNull(Node<K, V> nodeIn)
	{
		if (this.last == null)
		{
			this.last = nodeIn;
		}
	}

	void decSize()
	{
		this.size--;
	}

	Node<K, V> first()
	{
		return this.first;
	}

	Nodes<K, V> updateLast(Node<K, V> fromIn, Node<K, V> toIn)
	{
		if (fromIn.is(this.last.key()))
		{
			this.last = toIn;
		}

		return this;
	}

	Node<K, V> last()
	{
		return this.last;
	}
}