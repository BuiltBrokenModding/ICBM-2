package icbm.sentry.turret.turret;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockTurret extends ItemBlock
{
    public ItemBlockTurret(int par1)
    {
        super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public int getMetadata(int meta)
    {
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return super.getUnlocalizedName() + "." + itemstack.getTagCompound().getString("SentryID");
    }
    
    
    
}
