package icbm.sentry.turret;

import icbm.sentry.ICBMSentry;
import icbm.sentry.ItemSentryBase;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmmo extends ItemSentryBase
{
	public static final List<Icon> ICONS = new ArrayList<Icon>();
	public static final String[] TYPES = { "bulletShell", "bullet", "bulletRailgun", "bulletAntimatter" };

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
		return "item." + TYPES[itemStack.getItemDamage()];
	}

	@Override
	public Icon getIconFromDamage(int i)
	{
		return ICONS.get(i);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void func_94581_a(IconRegister iconRegister)
	{
		for (String type : TYPES)
		{
			ICONS.add(iconRegister.func_94245_a(ICBMSentry.PREFIX + type));
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