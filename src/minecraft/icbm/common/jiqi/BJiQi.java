package icbm.common.jiqi;

import icbm.client.render.RHJiQi;
import icbm.common.ZhuYao;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.implement.IRedstoneProvider;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.implement.ITier;
import universalelectricity.prefab.multiblock.IBlockActivate;
import universalelectricity.prefab.multiblock.IMultiBlock;

public class BJiQi extends BlockMachine
{
	public enum JiQi
	{
		FaSheDi(TFaSheDi.class), FaSheShiMuo(TFaSheShiMuo.class), FaSheJia(TFaSheJia.class), LeiDaTai(TLeiDaTai.class), DianCiQi(TDianCiQi.class), CiGuiPao(TCiGuiPao.class), XiaoFaSheQi(TXiaoFaSheQi.class);

		public Class<? extends TileEntity> tileEntity;

		JiQi(Class<? extends TileEntity> tileEntity)
		{
			this.tileEntity = tileEntity;
		}

		public static JiQi get(int id)
		{
			if (id <= JiQi.values().length) { return JiQi.values()[id]; }

			return null;
		}
	}

	private static final int JI_QI_SHU = 13;

	public BJiQi(int id)
	{
		super("ICBM Machine", id, UniversalElectricity.machine, ZhuYao.TAB);
	}

	/**
	 * Is this block powering the block on the specified side
	 */
	@Override
	public boolean isProvidingStrongPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof IRedstoneProvider) { return ((IRedstoneProvider) tileEntity).isPoweringTo(ForgeDirection.getOrientation(side)); }

		return false;
	}

	/**
	 * Is this block indirectly powering the block on the specified side
	 */
	@Override
	public boolean isProvidingWeakPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof IRedstoneProvider) { return ((IRedstoneProvider) tileEntity).isIndirectlyPoweringTo(ForgeDirection.getOrientation(side)); }

		return false;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its
	 * state.
	 */
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		this.isBeingPowered(par1World, x, y, z);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving)
	{
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRotatable)
		{
			IRotatable rotatableEntity = ((IRotatable) tileEntity);

			switch (angle)
			{
				case 0:
					rotatableEntity.setDirection(ForgeDirection.getOrientation(3));
					break;
				case 1:
					rotatableEntity.setDirection(ForgeDirection.getOrientation(4));
					break;
				case 2:
					rotatableEntity.setDirection(ForgeDirection.getOrientation(2));
					break;
				case 3:
					rotatableEntity.setDirection(ForgeDirection.getOrientation(5));
					break;
			}
		}

		if (tileEntity instanceof IMultiBlock)
		{
			((IMultiBlock) tileEntity).onCreate(new Vector3(x, y, z));
		}
	}

	public static boolean canBePlacedAt(World par1World, int x, int y, int z, int metadata, EntityLiving entityLiving)
	{
		int angle = MathHelper.floor_double((entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		// Launcher Base
		if (metadata == 0)
		{
			if (angle == 0 || angle == 2)
			{
				return par1World.getBlockId(x, y, z) == 0 &&
				// Left
						par1World.getBlockId(x + 1, y, z) == 0 && par1World.getBlockId(x + 1, y + 1, z) == 0 && par1World.getBlockId(x + 1, y + 2, z) == 0 &&
						// Right
						par1World.getBlockId(x - 1, y, z) == 0 && par1World.getBlockId(x - 1, y + 1, z) == 0 && par1World.getBlockId(x - 1, y + 2, z) == 0;
			}
			else if (angle == 1 || angle == 3) { return par1World.getBlockId(x, y, z) == 0 &&
			// Front
					par1World.getBlockId(x, y, z + 1) == 0 && par1World.getBlockId(x, y + 1, z + 1) == 0 && par1World.getBlockId(x, y + 2, z + 1) == 0 &&
					// Back
					par1World.getBlockId(x, y, z - 1) == 0 && par1World.getBlockId(x, y + 1, z - 1) == 0 && par1World.getBlockId(x, y + 2, z - 1) == 0; }
		}
		// Launcher Screen
		else if (metadata == 1 || metadata == 5 || metadata == 6)
		{
			return par1World.getBlockMaterial(x, y - 1, z).isSolid();
		}
		// Launcher Frame
		else if (metadata == 2)
		{
			return par1World.getBlockMaterial(x, y - 1, z).isSolid() && par1World.getBlockId(x, y, z) == 0 && par1World.getBlockId(x, y + 1, z) == 0 && par1World.getBlockId(x, y + 2, z) == 0;
		}
		// Radar
		else if (metadata == 3)
		{
			return par1World.getBlockMaterial(x, y - 1, z).isSolid() && par1World.getBlockId(x, y, z) == 0 &&

			par1World.getBlockId(x, y + 1, z) == 0 && par1World.getBlockId(x + 1, y + 1, z) == 0 && par1World.getBlockId(x - 1, y + 1, z) == 0 && par1World.getBlockId(x, y + 1, z + 1) == 0 && par1World.getBlockId(x, y + 1, z - 1) == 0 && par1World.getBlockId(x + 1, y + 1, z + 1) == 0 && par1World.getBlockId(x - 1, y + 1, z - 1) == 0 && par1World.getBlockId(x + 1, y + 1, z - 1) == 0 && par1World.getBlockId(x - 1, y + 1, z + 1) == 0;
		}
		else if (metadata == 4) { return par1World.getBlockId(x, y, z) == 0 && par1World.getBlockId(x, y + 1, z) == 0; }

		return false;
	}

	/**
	 * Can this block stay at this position. Similar to canPlaceBlockAt except gets checked often
	 * with plants.
	 */
	@Override
	public boolean canBlockStay(World par1World, int x, int y, int z)
	{
		return true;
	}

	@Override
	public void onNeighborBlockChange(World par1World, int x, int y, int z, int par5)
	{
		this.isBeingPowered(par1World, x, y, z);
	}

	/**
	 * Called when the block is right clicked by the player
	 */
	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (par5EntityPlayer.inventory.getCurrentItem() != null)
		{
			if (par5EntityPlayer.inventory.getCurrentItem().itemID == ZhuYao.itLeiSheZhiBiao.shiftedIndex)
			{
				return false;
			}
			else if (par5EntityPlayer.inventory.getCurrentItem().itemID == ZhuYao.itLeiDaQiang.shiftedIndex) { return false; }
		}

		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof IBlockActivate) { return ((IBlockActivate) tileEntity).onActivated(par5EntityPlayer); }
		}

		return false;
	}

	/**
	 * Checks of this block is being powered by redstone
	 */
	public void isBeingPowered(World par1World, int x, int y, int z)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);

		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IRedstoneReceptor)
		{
			if (par1World.isBlockIndirectlyGettingPowered(x, y, z))
			{
				// Send signal to tile entity
				((IRedstoneReceptor) tileEntity).onPowerOn();
			}
			else
			{
				((IRedstoneReceptor) tileEntity).onPowerOff();
			}
		}
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs,
	 * buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public void breakBlock(World par1World, int x, int y, int z, int par5, int par6)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);
		Random random = new Random();

		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IInventory)
		{
			IInventory inventory = (IInventory) tileEntity;

			if (inventory != null)
			{
				for (int var6 = 0; var6 < inventory.getSizeInventory(); ++var6)
				{
					ItemStack var7 = inventory.getStackInSlot(var6);

					if (var7 != null)
					{
						float var8 = random.nextFloat() * 0.8F + 0.1F;
						float var9 = random.nextFloat() * 0.8F + 0.1F;
						float var10 = random.nextFloat() * 0.8F + 0.1F;

						while (var7.stackSize > 0)
						{
							int var11 = random.nextInt(21) + 10;

							if (var11 > var7.stackSize)
							{
								var11 = var7.stackSize;
							}

							var7.stackSize -= var11;
							EntityItem var12 = new EntityItem(par1World, (x + var8), (y + var9), (z + var10), new ItemStack(var7.itemID, var11, var7.getItemDamage()));

							if (var7.hasTagCompound())
							{
								var12.item.setTagCompound((NBTTagCompound) var7.getTagCompound().copy());
							}

							float var13 = 0.05F;
							var12.motionX = ((float) random.nextGaussian() * var13);
							var12.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
							var12.motionZ = ((float) random.nextGaussian() * var13);
							par1World.spawnEntityInWorld(var12);
						}
					}
				}

			}
		}

		// Drops the machine
		int itemMetadata = 0;

		if (tileEntity instanceof ITier)
		{
			itemMetadata = ((ITier) tileEntity).getTier() + metadata * 3;
		}
		else
		{
			itemMetadata = 9 + metadata - 3;
		}

		EntityItem entityItem = new EntityItem(par1World, x, y, z, new ItemStack(ZhuYao.bJiQi, 1, itemMetadata));

		float var13 = 0.05F;
		entityItem.motionX = ((float) random.nextGaussian() * var13);
		entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
		entityItem.motionZ = ((float) random.nextGaussian() * var13);
		par1World.spawnEntityInWorld(entityItem);

		if (tileEntity instanceof IMultiBlock)
		{
			((IMultiBlock) tileEntity).onDestroy(tileEntity);
		}

		super.breakBlock(par1World, x, y, z, par5, par6);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		if (JiQi.get(metadata) != null)
		{
			try
			{
				return JiQi.get(metadata).tileEntity.newInstance();
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	/**
	 * The type of render function that is called for this block
	 */
	@Override
	public int getRenderType()
	{
		return RHJiQi.ID;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < JI_QI_SHU; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World par1World, int x, int y, int z)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);

		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		int itemMetadata = 0;

		if (tileEntity instanceof ITier)
		{
			itemMetadata = ((ITier) tileEntity).getTier() + metadata * 3;
		}
		else
		{
			itemMetadata = 9 + metadata - 3;
		}

		return new ItemStack(ZhuYao.bJiQi, 1, itemMetadata);
	}
}
