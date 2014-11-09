package icbm.explosion.machines;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import resonant.api.ITier;

public class ItemBlockMachine extends Item
{
    public ItemBlockMachine()
    {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return this.getUnlocalizedName() + "." + itemstack.getItemDamage();
    }

    @Override
    public String getUnlocalizedName()
    {
        return "icbm.machine";
    }

    @Override
    public boolean placeBlockAt(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        int meta;

        if (itemStack.getItemDamage() < 3)
        {
            meta = 0;
        }
        else if (itemStack.getItemDamage() < 6)
        {
            meta = 1;
        }
        else if (itemStack.getItemDamage() < 9)
        {
            meta = 2;
        }
        else
        {
            meta = itemStack.getItemDamage() - 6;
        }

        int direction = MathHelper.floor_double((entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (BlockICBMMachine.canBePlacedAt(world, x, y, z, meta, direction))
        {
            Block var9 = Block.blocksList[this.getBlockID()];

            if (world.setBlock(x, y, z, this.getBlockID(), meta, 3))
            {
                if (world.getBlockId(x, y, z) == this.getBlockID())
                {
                    if (itemStack.getItemDamage() < 9)
                    {
                        ITier tileEntity = (ITier) world.getBlockTileEntity(x, y, z);

                        if (tileEntity != null)
                        {
                            if (itemStack.getItemDamage() < 3)
                            {
                                tileEntity.setTier(itemStack.getItemDamage());
                            }
                            else if (itemStack.getItemDamage() < 6)
                            {
                                tileEntity.setTier(itemStack.getItemDamage() - 3);
                            }
                            else if (itemStack.getItemDamage() < 9)
                            {
                                tileEntity.setTier(itemStack.getItemDamage() - 6);
                            }
                        }
                    }

                    Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, entityPlayer, itemStack);
                }

                return true;
            }
        }

        return false;
    }
}
