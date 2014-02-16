package icbm.sentry.turret.block;

import calclavia.lib.content.BlockInfo;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.prefab.block.BlockAdvanced;
import calclavia.lib.utility.nbt.SaveManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.Reference;
import icbm.core.CreativeTabICBM;
import icbm.core.prefab.BlockICBM;
import icbm.sentry.ICBMSentry;
import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.SentryRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map.Entry;

/**
 * Block turret is a class used by all turrets. Each type of turret will have a different tile
 * entity.
 * 
 * @author Calclavia, tgame14
 */
public class BlockTurret extends BlockICBM
{
	public BlockTurret(int id)
	{
		super(id, "turret");
		this.setCreativeTab(CreativeTabICBM.INSTANCE);
		this.setHardness(100f);
		this.setResistance(50f);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);

		if (ent instanceof TileTurret)
		{
			Entity fakeEntity = ((TileTurret) ent).getFakeEntity();

			if (fakeEntity != null)
			{
				this.setBlockBounds(.2f, 0, .2f, .8f, .4f, .8f);
			}
			else
			{
				this.setBlockBounds(.2f, 0, .2f, .8f, .8f, .8f);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(Reference.PREFIX + "machine");
	}

	@Override
	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IBlockActivate)
		{
			return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
		}

		return false;
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

		if (tileEntity instanceof TileTurret)
		{
			if (!this.canBlockStay(world, x, y, z))
			{
				this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 1);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileTurret();
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
		return 0;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return world.getBlockId(x, y - 1, z) == ICBMSentry.blockPlatform.blockID;
	}

	@Override
	public void getSubBlocks(int id, CreativeTabs creativeTab, List list)
	{
		for (Entry<String, Class<? extends Sentry>> entry : SentryRegistry.getSentryMap().entrySet())
		{
			if (entry.getValue() != null)
			{
				ItemStack stack = new ItemStack(this);
				NBTTagCompound itemNbt = new NBTTagCompound();
				NBTTagCompound sentry_nbt = new NBTTagCompound();

				itemNbt.setString("unlocalizedName", entry.getKey());
				sentry_nbt.setString("sentryID", SaveManager.getID(entry.getValue()));
				itemNbt.setCompoundTag("sentryTile", sentry_nbt);

				stack.setTagCompound(itemNbt);
				list.add(stack);
			}
		}

	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		ItemStack droppedStack = new ItemStack(this);
		NBTTagCompound tag = new NBTTagCompound();
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null)
			tile.writeToNBT(tag);

		droppedStack.setTagCompound(tag);

		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
		{
			float f = 0.7F;
			double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, droppedStack);
			entityitem.delayBeforeCanPickup = 10;
			world.spawnEntityInWorld(entityitem);

		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	/* to cancel the vanilla method of dropping the itemEntity */
	@Override
	protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack stack)
	{

	}

	@Override
	public int getRenderType()
	{
		return -1;
	}
}
