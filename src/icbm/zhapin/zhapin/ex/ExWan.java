package icbm.zhapin.zhapin.ex;

import icbm.api.explosion.IExplosiveContainer;
import icbm.core.SheDing;
import icbm.core.base.MICBM;
import icbm.zhapin.baozha.bz.BzWan;
import icbm.zhapin.muoxing.daodan.MMWan;
import icbm.zhapin.zhapin.TZhaDan;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.daodan.DaoDan;
import icbm.zhapin.zhapin.daodan.EDaoDan;
import mffs.api.card.ICoordLink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.RecipeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExWan extends DaoDan
{
	public ExWan(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
	{

		if (entityPlayer.inventory.getCurrentItem() != null)
		{
			if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ICoordLink)
			{
				Vector3 link = ((ICoordLink) entityPlayer.inventory.getCurrentItem().getItem()).getLink(entityPlayer.inventory.getCurrentItem());

				if (link != null)
				{
					TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

					if (tileEntity instanceof TZhaDan)
					{
						link.writeToNBT(((TZhaDan) tileEntity).nbtData);

						if (!world.isRemote)
						{
							entityPlayer.addChatMessage("Synced coordinate with " + this.getExplosiveName());
						}

						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean onInteract(EDaoDan missileObj, EntityPlayer entityPlayer)
	{
		if (entityPlayer.inventory.getCurrentItem() != null)
		{
			if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ICoordLink)
			{
				Vector3 link = ((ICoordLink) entityPlayer.inventory.getCurrentItem().getItem()).getLink(entityPlayer.inventory.getCurrentItem());

				if (link != null)
				{
					link.writeToNBT(missileObj.nbtData);
					if (!missileObj.worldObj.isRemote)
					{
						entityPlayer.addChatMessage("Synced coordinate with " + this.getMissileName());
					}
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "PPP", "PTP", "PPP", 'P', Item.enderPearl, 'T', ZhaPin.la.getItemStack() }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		Vector3 teleportTarget = null;

		if (entity instanceof IExplosiveContainer)
		{
			if (((IExplosiveContainer) entity).getTagCompound().hasKey("x") && ((IExplosiveContainer) entity).getTagCompound().hasKey("y") && ((IExplosiveContainer) entity).getTagCompound().hasKey("z"))
			{
				teleportTarget = Vector3.readFromNBT(((IExplosiveContainer) entity).getTagCompound());
			}
		}

		new BzWan(world, entity, x, y, z, 30, teleportTarget).explode();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MICBM getMissileModel()
	{
		return new MMWan();
	}

}
