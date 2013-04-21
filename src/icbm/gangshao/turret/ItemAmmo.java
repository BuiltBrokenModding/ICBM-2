package icbm.gangshao.turret;

import icbm.core.ZhuYao;
import icbm.core.di.ItICBM;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmmo extends ItICBM
{
	public static final String[] TYPES = { "bulletShell", "bullet", "bulletRailgun", "bulletAntimatter" };
	public static final Icon[] ICONS = new Icon[TYPES.length];

	public ItemAmmo(int id)
	{
		super(id, "ammunition");
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		return "item." + ZhuYao.PREFIX + TYPES[itemStack.getItemDamage()];
	}

	@Override
	public Icon getIconFromDamage(int i)
	{
		return ICONS[i];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		for (int i = 0; i < TYPES.length; i++)
		{
			ICONS[i] = iconRegister.registerIcon(ZhuYao.PREFIX + TYPES[i]);
		}
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < TYPES.length; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}