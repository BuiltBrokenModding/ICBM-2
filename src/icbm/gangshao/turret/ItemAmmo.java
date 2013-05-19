package icbm.gangshao.turret;

import icbm.api.sentry.IAmmo;
import icbm.api.sentry.ProjectileTypes;
import icbm.core.ZhuYaoBase;
import icbm.core.di.ItICBM;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.actions.LookHelper;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmmo extends ItICBM implements IAmmo
{
	enum types
	{
		SHELL("bulletShell", ProjectileTypes.NEUTRIAL, true),
		BULLET("bullet", ProjectileTypes.CONVENTIONAL, true),
		BULLETRAIL("bulletRailgun", ProjectileTypes.RAILGUN, true),
		BULLETANTI("bulletAntimatter", ProjectileTypes.RAILGUN, true),
		BULLETINF("bulletInfinite", ProjectileTypes.CONVENTIONAL, false);

		public String iconName;
		public ProjectileTypes type;
		public boolean consume;

		private types(String iconName, ProjectileTypes type, boolean consume)
		{
			this.iconName = iconName;
			this.type = type;
			this.consume = consume;
		}
	}

	public static final Icon[] ICONS = new Icon[types.values().length];

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
		return "item." + ZhuYaoBase.PREFIX + types.values()[itemStack.getItemDamage()].iconName;
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
		for (int i = 0; i < types.values().length; i++)
		{
			ICONS[i] = iconRegister.registerIcon(ZhuYaoBase.PREFIX + types.values()[i].iconName);
		}
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < types.values().length; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public int getDamage(int meta, Entity entity)
	{
		return 0;
	}

	@Override
	public void attackTargetLiving(int meta, TileEntity tur, Entity target, boolean hit)
	{
		if (tur == null || tur instanceof TileEntityTurretBase && ((TileEntityTurretBase) tur).getPlatform() == null)
		{
			return;
		}
		TileEntityTurretBase turret = (TileEntityTurretBase) tur;
		if (meta == types.BULLET.ordinal() || meta == types.BULLETINF.ordinal())
		{
			// Apply Knock Back
			if (target instanceof EntityLiving)
			{
				if (turret.worldObj.rand.nextFloat() > 0.1)
				{
					((EntityLiving) target).attackEntityFrom(DamageSource.setExplosionSource(null), 4);
				}

				Vector3 look = LookHelper.getDeltaPositionFromRotation(turret.wantedRotationYaw, turret.wantedRotationPitch);
				look.multiply(0.3);
				((EntityLiving) target).knockBack(null, 0, look.intX(), look.intZ());

			}
			// Spawn Shell
			if (meta != types.BULLETINF.ordinal() && !turret.worldObj.isRemote && turret.worldObj.rand.nextFloat() > 0.8)
			{
				Vector3 spawnPos = turret.getMuzzle();
				EntityItem entityShell = new EntityItem(turret.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ZhuYaoGangShao.bulletShell.copy());
				entityShell.delayBeforeCanPickup = 20;
				turret.worldObj.spawnEntityInWorld(entityShell);
			}
		}

	}

	@Override
	public ProjectileTypes getType(int meta)
	{
		if (meta < types.values().length)
		{
			return types.values()[meta].type;
		}
		return null;
	}

	@Override
	public boolean consumeItem(int meta)
	{
		if (meta < types.values().length)
		{
			return types.values()[meta].consume;
		}
		return true;
	}
}