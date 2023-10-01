package fr.onsiea.ludart.common.modules.nodes;

public class Nodes<K, V>
{
	Node<K, V> first;
	Node<K, V> last;
	int size;

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

			this.last = nodeIn;
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