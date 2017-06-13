package com.builtbroken.icbm.content.items.parts;

import com.builtbroken.icbm.ICBM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/** Enum of peaces used to craft the modules or casings for a missile
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/1/2015.
 */
public enum MissileCraftingParts
{
    SMALL_MISSILE_CASE("smallMissileCasing");

    @SideOnly(Side.CLIENT)
    public IIcon icon;

    public final String name;

    MissileCraftingParts(String name)
    {
        this.name = name;
    }

    public ItemStack stack()
    {
        return stack(1);
    }

    public ItemStack stack(int size)
    {
        return new ItemStack(ICBM.itemMissileParts, size, ordinal());
    }
}
