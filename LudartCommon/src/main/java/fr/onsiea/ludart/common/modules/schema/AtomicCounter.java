package fr.onsiea.ludart.common.modules.schema;

import lombok.Getter;

@Getter
public class AtomicCounter
{
	private int value;
	private AtomicCounter next;

	public AtomicCounter(int initialValueIn)
	{
		this.value = initialValueIn;
	}

	public AtomicCounter(int initialValueIn, AtomicCounter nextIn)
	{
		this.value = initialValueIn;
		this.next = nextIn;
	}

	public final AtomicCounter next(AtomicCounter atomicCounterIn)
	{
		if (this.next != null)
		{
			this.next.increase();
			atomicCounterIn.next = this.next;
		}
		this.next = atomicCounterIn;
		return this;
	}

	public final AtomicCounter increase()
	{
		this.value++;
		if (this.next != null)
		{
			this.next.increase();
		}

		return this;
	}
}