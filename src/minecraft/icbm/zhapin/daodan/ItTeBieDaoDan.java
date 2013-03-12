package icbm.zhapin.daodan;

import icbm.core.ZhuYao;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItTeBieDaoDan extends ItDaoDan
{
	public ItTeBieDaoDan(int id)
	{
		super(id, "specialMissile");
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		if (itemstack.getItemDamage() == 0)
		{
			return this.getUnlocalizedName() + ".missileModule";
		}

		return this.getUnlocalizedName() + "." + DaoDan.list[itemstack.getItemDamage() + 100].getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	public void func_94581_a(IconRegister par1IconRegister)
	{
		this.iconIndex = par1IconRegister.func_94245_a(ZhuYao.PREFIX + "missileModule");
	}

	@Override
	public String getUnlocalizedName()
	{
		return "icbm.missile";
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < DaoDan.MAX_DAO_DAN + 1; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}
