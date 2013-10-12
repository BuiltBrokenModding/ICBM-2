package com.builtbroken.common;

public class Triple<A, B, C>
{
    private final A aaa;
    private final B bbb;
    private final C ccc;

    public Triple(A left, B right, C ccc)
    {
        this.aaa = left;
        this.bbb = right;
        this.ccc = ccc;
    }

    public A getA()
    {
        return aaa;
    }

    public B getB()
    {
        return bbb;
    }

    public C getC()
    {
        return ccc;
    }

    @Override
    public int hashCode()
    {
        return aaa.hashCode() ^ bbb.hashCode() ^ ccc.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (!(o instanceof Triple))
            return false;
        Triple pairo = (Triple) o;
        return this.aaa.equals(pairo.getA()) && this.bbb.equals(pairo.getB()) && this.ccc.equals(pairo.getC());
    }

}