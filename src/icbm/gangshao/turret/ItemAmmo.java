package icbm.gangshao.turret;

import icbm.core.ZhuYaoBase;
import icbm.core.di.ItICBM;
import icbm.gangshao.IAmmunition;
import icbm.gangshao.ProjectileTypes;
import icbm.gangshao.damage.GangShaoDamageSource;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmmo extends ItICBM implements IAmmunition
{
	public static enum AmmoType
	{
		SHELL("bulletShell", ProjectileTypes.UNKNOWN, true),
		BULLET("bullet", ProjectileTypes.CONVENTIONAL, true),
		BULLETRAIL("bulletRailgun", ProjectileTypes.RAILGUN, true),
		BULLETANTI("bulletAntimatter", ProjectileTypes.RAILGUN, true),
		BULLETINF("bulletInfinite", ProjectileTypes.CONVENTIONAL, false);

		public String iconName;
		public ProjectileTypes type;
		public boolean consume;

		private AmmoType(String iconName, ProjectileTypes type, boolean consume)
		{
			this.iconName = iconName;
			this.type = type;
			this.consume = consume;
		}
	}

	public static final Icon[] ICONS = new Icon[AmmoType.values().length];

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
		return "item." + ZhuYaoBase.PREFIX + AmmoType.values()[itemStack.getItemDamage()].iconName;
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
		for (int i = 0; i < AmmoType.values().length; i++)
		{
			ICONS[i] = iconRegister.registerIcon(ZhuYaoBase.PREFIX + AmmoType.values()[i].iconName);
		}
	}

	@Override
	public ProjectileTypes getType(int meta)
	{
		if (meta < AmmoType.values().length)
		{
			return AmmoType.values()[meta].type;
		}

		return null;
	}

	@Override
	public void onFire(Entity target, ItemStack itemStack)
	{
		if (itemStack.getItemDamage() == AmmoType.BULLET.ordinal() || itemStack.getItemDamage() == AmmoType.BULLETINF.ordinal())
		{
			target.attackEntityFrom(GangShaoDamageSource.doBulletDamage(target), 8);
		}
	}

	@Override
	public boolean canDrop(int meta)
	{
		if (meta == AmmoType.BULLETINF.ordinal())
		{
			return false;
		}
		return true;
	}

	@Override
	public ItemStack onDroppedIntoWorld(ItemStack stack)
	{
		return stack;
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world)
	{
		if (itemStack != null && itemStack.getItemDamage() == AmmoType.BULLETINF.ordinal())
		{
			return 40;
		}
		return super.getEntityLifespan(itemStack, world);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < AmmoType.values().length; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}
}