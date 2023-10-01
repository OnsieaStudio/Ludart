package fr.onsiea.ludart.common.modules.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class Node<K, V>
{
	private final Nodes<K, V> root;
	private final @Getter K key;
	private final @Getter V value;
	private @Getter Node<K, V> previous;
	private @Getter Node<K, V> next;

	public Node(Nodes<K, V> rootIn, K keyIn, V targetIn)
	{
		this.root = rootIn;
		this.key = keyIn;
		this.value = targetIn;
	}

	public final Node<K, V> addBefore(Node<K, V> nodeIn)
	{
		if (this.previous != null)
		{
			this.previous.next = nodeIn;
			nodeIn.previous = this.previous;
		}

		this.previous = nodeIn;
		nodeIn.next = this;

		this.root.incSize();
		return this;
	}

	public final Node<K, V> addAfter(Node<K, V> nodeIn)
	{
		if (this.next != null)
		{
			this.next.previous = nodeIn;
			nodeIn.next = this.next;
		}

		this.next = nodeIn;
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