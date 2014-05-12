package icbm.sentry.weapon.hand.items.conventional;

import icbm.core.prefab.item.ItemICBMBase;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemConventionalAddons extends ItemICBMBase {

	public ItemConventionalAddons(int id) {
		super(id, "conventionalAddon");
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}

	@Override
	public Icon getIconFromDamage(int metadata) {
		if (metadata < Addons.values().length) { return Addons.values()[metadata].icon; }

		return null;
	}

	@Override
	public String getUnlocalizedName() {
		super.getUnlocalizedName();
		for (Addons data : Addons.values()) {
			return getUnlocalizedName() + "_" + data.name;
		}
		return super.getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister) {
		super.registerIcons(iconRegister);
		for (Addons data : Addons.values()) {
			data.icon = iconRegister.registerIcon(this.getUnlocalizedName().replace("item.", ""));
		}
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (Addons con : Addons.values()) {
			par3List.add(new ItemStack(par1, 1, con.ordinal()));
		}
	}

	public static enum Addons {
		SILENCER("silencer");

		public int explores;
		public Icon icon;
		public String name;

		Addons(String name) {
			this.name = name;
		}
	}
}
