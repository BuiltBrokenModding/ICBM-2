package com.builtbroken.common;

/**
 * Container for two objects
 * 
 * @author Robert Seifert
 */
public class Pair<L, R>
{
	private final L left;
	private final R right;

	public Pair(L left, R right)
	{
		this.left = left;
		this.right = right;
	}

	public L left()
	{
		return left;
	}

	public R right()
	{
		return right;
	}

	@Override
	public int hashCode()
	{
		if (left == null || right == null)
		{
			super.hashCode();
		}
		return left.hashCode() ^ right.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (!(o instanceof Pair))
			return false;
		Pair pairo = (Pair) o;
		return this.left.equals(pairo.left()) && this.right.equals(pairo.right());
	}

}