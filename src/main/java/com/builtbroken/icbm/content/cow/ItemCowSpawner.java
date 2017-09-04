package com.builtbroken.icbm.content.cow;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.items.listeners.IItemActivationListener;
import com.builtbroken.mc.codegen.annotations.ItemWrapped;
import com.builtbroken.mc.framework.item.ItemNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/1/2017.
 */
@ItemWrapped(className = "ItemWrappedCowSpawner")
public class ItemCowSpawner extends ItemNode implements IItemActivationListener
{
    public ItemCowSpawner()
    {
        super(ICBM.DOMAIN, "cowSpawnItem");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hit_x, float hit_y, float hit_z)
    {
        if (!world.isRemote)
        {
            EntityGaintCow cow = new EntityGaintCow(world);
            cow.setLocationAndAngles(x + 0.5, y + 2, z + 0.5, player.rotationYaw - 180, 0);
            world.spawnEntityInWorld(cow);
        }
        return true;
    }
}
