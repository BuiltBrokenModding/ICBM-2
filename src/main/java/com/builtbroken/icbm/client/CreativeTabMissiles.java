package com.builtbroken.icbm.client;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.missile.data.missile.Missile;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.icbm.content.missile.item.ItemMissile;
import com.builtbroken.mc.framework.mod.ModCreativeTab;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/29/2016.
 */
public class CreativeTabMissiles extends ModCreativeTab
{
    ItemStack missile;
    public final MissileSize casing;

    public CreativeTabMissiles(MissileSize casing)
    {
        super("icbm.missiles." + casing.name().toLowerCase());
        this.casing = casing;
    }

    @Override
    public void displayAllReleventItems(List list)
    {
        ItemMissile.getSubItems(casing, list);
    }

    @Override
    public ItemStack getIconItemStack()
    {
        if (ICBM_API.itemMissile != null)
        {
            if (missile == null)
            {
                missile = new Missile(casing.getDefaultMissileCasing()).toStack();
            }
            return missile;
        }
        return super.getIconItemStack();
    }
}
