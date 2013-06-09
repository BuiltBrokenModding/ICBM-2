package icbm.gangshao.turret;

import icbm.core.ICBMTab;
import icbm.core.ZhuYaoBase;
import icbm.core.di.BICBM;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.render.BlockRenderingHandler;
import icbm.gangshao.turret.mount.TileEntityRailTurret;
import icbm.gangshao.turret.sentries.TileEntityAATurret;
import icbm.gangshao.turret.sentries.TileEntityGunTurret;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.block.BlockAdvanced;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.multiblock.IBlockActivate;
import universalelectricity.prefab.multiblock.IMultiBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Block turret is a class used by all turrets. Each type of turret will have a different tile
 * entity.
 * 
 * @author Calclavia
 * 
 */
public class BlockTurret extends BICBM
{
	public enum TurretType
	{
		GUN(TileEntityGunTurret.class), RAILGUN(TileEntityRailTurret.class), AA(TileEntityAATurret.class);

		public Class<? extends TileEntity> tileEntity;

		private TurretType(Class<? extends TileEntity> tile)
		{
			this.tileEntity = tile;
		}
	}

	public BlockTurret(int par1)
	{
		super(par1, "turret", UniversalElectricity.machine);
		this.setCreativeTab(ICBMTab.INSTANCE);
		this.setHardness(100f);
		this.setResistance(50f);
		this.setBlockBounds(.2f, 0, .2f, .8f, .8f, .8f);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(ZhuYaoBase.PREFIX + "machine");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity ent)
	{
		if (ent instanceof IProjectile)
		{
			// TODO pass event to tileEntity to apply damage
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack itemStack)
	{
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRotatable)
		{
			IRotatable rotatableEntity = ((IRotatable) tileEntity);

			switch (angle)
			{
				case 0:
					rotatableEntity.setDirection(world, x, y, z, ForgeDirection.getOrientation(3));
					break;
				case 1:
					rotatableEntity.setDirection(world, x, y, z, ForgeDirection.getOrientation(4));
					break;
				case 2:
					rotatableEntity.setDirection(world, x, y, z, ForgeDirection.getOrientation(2));
					break;
				case 3:
					rotatableEntity.setDirection(world, x, y, z, ForgeDirection.getOrientation(5));
					break;
			}
		}

		if (tileEntity instanceof IMultiBlock)
		{
			((IMultiBlock) tileEntity).onCreate(new Vector3(x, y, z));
		}
	}

	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		/**
		 * Checks the TileEntity if it can activate. If not, then try to activate the turret
		 * platform below it.
		 */
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IBlockActivate)
		{
			return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
		}

		int id = world.getBlockId(x, y - 1, z);
		Block block = Block.blocksList[id];

		if (block instanceof BlockAdvanced)
		{
			return ((BlockAdvanced) block).onMachineActivated(world, x, y - 1, z, entityPlayer, side, hitX, hitY, hitZ);
		}

		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int side)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityTurretBase)
		{
			if (this.canBlockStay(world, x, y, z))
			{
				if (tileEntity instanceof IRedstoneReceptor)
				{
					if (world.isBlockIndirectlyGettingPowered(x, y, z))
					{
						((IRedstoneReceptor) tileEntity).onPowerOn();
					}
					else
					{
						((IRedstoneReceptor) tileEntity).onPowerOff();
					}
				}
			}
			else
			{
				/*
				 * if (tileEntity instanceof IMultiBlock) { ((IMultiBlock)
				 * tileEntity).onDestroy(tileEntity); }
				 */

				if (tileEntity != null)
				{
					((TileEntityTurretBase) tileEntity).destroy(false);
				}
			}
		}
	}

	@Override
	public void breakBlock(World par1World, int x, int y, int z, int par5, int par6)
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IMultiBlock)
		{
			((IMultiBlock) tileEntity).onDestroy(tileEntity);
		}

		super.breakBlock(par1World, x, y, z, par5, par6);
	}

	@Override
	public TileEntity createTileEntity(World world, int meta)
	{
		if (meta < TurretType.values().length)
		{
			try
			{
				return TurretType.values()[meta].tileEntity.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return world.getBlockId(x, y - 1, z) == ZhuYaoGangShao.blockPlatform.blockID;
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List list)
	{
		for (int i = 0; i < TurretType.values().length; i++)
		{
			list.add(new ItemStack(par1, 1, i));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return BlockRenderingHandler.ID;
	}
}
