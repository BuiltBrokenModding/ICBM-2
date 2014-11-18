package icbm.core;

import icbm.explosion.entities.EntityBombCart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Created by robert on 11/18/2014.
 */
public class DispenseBehaviorBombCart implements IBehaviorDispenseItem
{
    private final BehaviorDefaultDispenseItem defaultItemDispenseBehavior = new BehaviorDefaultDispenseItem();

    @Override
    public ItemStack dispense(IBlockSource blockSource, ItemStack itemStack)
    {
        World world = blockSource.getWorld();

        if (!world.isRemote)
        {
            int x = blockSource.getXInt();
            int y = blockSource.getYInt();
            int z = blockSource.getZInt();

            EnumFacing var3 = EnumFacing.getFront(blockSource.getBlockMetadata());
            double var5 = blockSource.getX() + var3.getFrontOffsetX() * 1.125F;
            double var7 = blockSource.getY();
            double var9 = blockSource.getZ() + var3.getFrontOffsetZ() * 1.125F;
            int var11 = blockSource.getXInt() + var3.getFrontOffsetX();
            int var12 = blockSource.getYInt();
            int var13 = blockSource.getZInt() + var3.getFrontOffsetZ();
            Block block = world.getBlock(var11, var12, var13);
            double var15;

            if (block instanceof BlockRailBase)
            {
                var15 = 0.0D;
            }
            else
            {
                block = world.getBlock(var11, var12 - 1, var13);
                if (block !=null && block.isAir(world, var11, var12, var13) || block instanceof BlockRailBase)
                {
                    return this.defaultItemDispenseBehavior.dispense(blockSource, itemStack);
                }

                var15 = -1.0D;
            }

            EntityBombCart var22 = new EntityBombCart(world, var5, var7 + var15, var9, itemStack.getItemDamage());
            world.spawnEntityInWorld(var22);
            world.playAuxSFX(1000, x, y, z, 0);
        }

        itemStack.stackSize--;
        return itemStack;
    }
}
