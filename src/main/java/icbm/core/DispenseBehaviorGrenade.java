package icbm.core;

import icbm.explosion.entities.EntityGrenade;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import resonant.lib.transform.vector.Vector3;

/**
 * Created by robert on 11/18/2014.
 */
public class DispenseBehaviorGrenade implements IBehaviorDispenseItem
{
    @Override
    public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
    {
        World world = blockSource.getWorld();

        if (!world.isRemote)
        {
            int x = blockSource.getXInt();
            int y = blockSource.getYInt();
            int z = blockSource.getZInt();
            EnumFacing enumFacing = EnumFacing.getFront(blockSource.getBlockMetadata());

            EntityGrenade entity = new EntityGrenade(world, new Vector3(x, y, z), itemStack.getItemDamage());
            entity.setThrowableHeading(enumFacing.getFrontOffsetX(), 0.10000000149011612D, enumFacing.getFrontOffsetZ(), 0.5F, 1.0F);
            world.spawnEntityInWorld(entity);
        }

        itemStack.stackSize--;
        return itemStack;
    }
}
