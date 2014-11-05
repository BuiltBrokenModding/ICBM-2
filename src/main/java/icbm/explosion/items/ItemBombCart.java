package icbm.explosion.items;

import icbm.core.prefab.item.ItemICBMBase;
import icbm.explosion.entities.EntityBombCart;
import icbm.explosion.explosive.Explosive;
import icbm.explosion.explosive.ExplosiveRegistry;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBombCart extends ItemICBMBase
{
    public ItemBombCart(int id)
    {
        super("minecart");
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /** Callback for item usage. If the item does something special on right clicking, he will have
     * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
     * BLOCKS */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        Block var11 = world.getBlock(x, y, z);

        if (BlockRailBase.isRailBlock(var11))
        {
            if (!world.isRemote)
            {
                world.spawnEntityInWorld(new EntityBombCart(world, x + 0.5F, y + 0.5F, z + 0.5F, itemStack.getItemDamage()));
            }

            --itemStack.stackSize;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("minecart_tnt");
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return "icbm.minecart." + ExplosiveRegistry.get(itemstack.getItemDamage()).getUnlocalizedName();
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (Explosive zhaPin : ExplosiveRegistry.getExplosives())
        {
            if (zhaPin.hasMinecartForm())
            {
                par3List.add(new ItemStack(par1, 1, zhaPin.getID()));

            }
        }
    }
}
