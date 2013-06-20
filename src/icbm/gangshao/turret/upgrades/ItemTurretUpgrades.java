package icbm.gangshao.turret.upgrades;

import icbm.api.sentry.ISentryUpgrade;
import icbm.core.ZhuYaoBase;
import icbm.core.di.ItICBM;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTurretUpgrades extends ItICBM implements ISentryUpgrade
{
	enum upgrades
	{
		RANGE("TargetCard", "TargetRange"),
		COLLECTOR("ShellCollector", "ShellBag");

		String iconName;
		String[] upgrades;

		private upgrades(String name, String... upgrades)
		{
			this.iconName = name;
			this.upgrades = upgrades;
		}
	}

	public static final Icon[] ICONS = new Icon[upgrades.values().length];

	public ItemTurretUpgrades(int par1)
	{
		super(par1, "turretUpgrades");
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
		return "item." + ZhuYaoBase.PREFIX + upgrades.values()[itemStack.getItemDamage()].iconName;
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
		for (int i = 0; i < upgrades.values().length; i++)
		{
			ICONS[i] = iconRegister.registerIcon(ZhuYaoBase.PREFIX + upgrades.values()[i].iconName);
		}
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < upgrades.values().length; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public List<String> getTypes(ItemStack itemstack)
	{
		List<String> list = new ArrayList<String>();
		if (itemstack != null)
		{
			int meta = itemstack.getItemDamage();
			if (meta < upgrades.values().length)
			{
				String[] ups = upgrades.values()[meta].upgrades;
				for (int i = 0; ups != null && i < ups.length; i++)
				{
					list.add(ups[i]);
				}
			}
		}
		return list;
	}

	@Override
	public float getEffectiveness(ItemStack itemstack)
	{
		return .1f;
	}

}
