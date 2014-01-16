package icbm.contraption.block;

import icbm.contraption.ICBMContraption;
import icbm.core.prefab.BlockICBM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import universalelectricity.api.UniversalElectricity;

public class BlockProximityDetector extends BlockICBM
{
	public BlockProximityDetector(int id)
	{
		super(id, "proximityDetector", UniversalElectricity.machine);
		this.requireSidedTextures = true;
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public Icon getIcon(int side, int metadata)
	{
		return side == 0 ? this.iconBottom : (side == 1 ? this.iconTop : this.iconSide);
	}

	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		par5EntityPlayer.openGui(ICBMContraption.instance, 0, par1World, x, y, z);
		return true;
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileDetector)
		{
			((TileDetector) tileEntity).isInverted = !((TileDetector) tileEntity).isInverted;
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
		return new TileDetector();
	}
}
