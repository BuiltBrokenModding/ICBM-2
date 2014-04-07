package icbm.sentry.turret.block;

import icbm.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import calclavia.lib.prefab.item.ItemBlockSaved;

import java.util.List;

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
		if (par2EntityPlayer.username.equals("Etho"))
		{
			list.add("Best served with Canadian Maple Syrup");
		}
		super.addInformation(itemStack, par2EntityPlayer, list, par4);
	}
}
