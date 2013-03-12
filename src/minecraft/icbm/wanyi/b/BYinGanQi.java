package icbm.wanyi.b;

import icbm.core.ZhuYao;
import icbm.core.di.BICBM;
import icbm.wanyi.ZhuYaoWanYi;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;

public class BYinGanQi extends BICBM
{
	public BYinGanQi(int id)
	{
		super(id, "proximityDetector", UniversalElectricity.machine);
		this.requireSidedTextures = true;
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public Icon getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
		return side == 0 ? this.iconBottom : (side == 1 ? this.iconTop : this.iconSide);
	}

	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		par5EntityPlayer.openGui(ZhuYaoWanYi.instance, ZhuYao.GUI_YIN_GAN_QI, par1World, x, y, z);
		return true;
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TYinGanQi)
		{
			((TYinGanQi) tileEntity).isInverted = !((TYinGanQi) tileEntity).isInverted;
			return true;
		}

		return false;
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TYinGanQi();
	}

	/**
	 * Override this if you do not want your machine to be added to the creative menu or if you have
	 * metadata machines and want to add more machines to the creative menu.
	 */
	@Override
	public void addCreativeItems(ArrayList itemList)
	{
		itemList.add(new ItemStack(this));
	}

	@Override
	public String getTextureFile()
	{
		return ZhuYao.BLOCK_PATH;
	}
}
