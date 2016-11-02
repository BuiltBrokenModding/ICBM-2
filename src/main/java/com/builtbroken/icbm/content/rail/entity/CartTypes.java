package com.builtbroken.icbm.content.rail.entity;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/2/2016.
 */
public enum CartTypes
{
    MICRO(1, 1),
    SMALL(1, 3),
    MEDIUM(3, 3),
    LARGE(5, 5);

    public final float width;
    public final float length;

    CartTypes(float width, float length)
    {
        this.width = width;
        this.length = length;
    }
}
