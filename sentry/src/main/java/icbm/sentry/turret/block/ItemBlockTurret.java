package icbm.sentry.turret.block;

import icbm.Reference;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import resonant.lib.prefab.item.ItemBlockSaved;

public class ItemBlockTurret extends ItemBlockSaved
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
        return "tile." + Reference.PREFIX + "turret." + itemstack.getTagCompound().getString("unlocalizedName");
    }

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4)
	{
		super.addInformation(itemStack, par2EntityPlayer, list, par4);
	}
}
