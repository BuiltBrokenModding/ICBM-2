package icbm.sentry.turret.cart;

import icbm.core.prefab.item.ItemICBMBase;
import icbm.explosion.cart.EntityBombCart;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.ExplosiveRegistry;

import java.util.List;

import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGunCart extends ItemICBMBase
{
    public ItemGunCart(int id)
    {
        super(id, "guncart");
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /** Callback for item usage. If the item does something special on right clicking, he will have
     * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
     * BLOCKS */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        int var11 = world.getBlockId(x, y, z);

        if (BlockRailBase.isRailBlock(var11))
        {
            if (!world.isRemote)
            {
                world.spawnEntityInWorld(new EntityTurretCart(world, x + 0.5F, y + 0.5F, z + 0.5F));
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
    public void registerIcons(IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("minecart_tnt");
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return "icbm.minecart.sentry";
    }
}