package icbm.wanyi.b;

import icbm.core.ICBMTab;
import icbm.core.ZhuYao;

import java.util.Random;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BBuoLiPan extends BlockPressurePlate
{
	public BBuoLiPan(int id)
	{
		super(id, ZhuYao.PREFIX + "glassPressurePlate", Material.glass, EnumMobType.everything);
		this.setTickRandomly(true);
		this.setResistance(1F);
		this.setHardness(0.3F);
		this.setStepSound(soundGlassFootstep);
		this.setUnlocalizedName(ZhuYao.PREFIX + "glassPressurePlate");
		this.setCreativeTab(ICBMTab.INSTANCE);
	}

	@Override
	protected void setStateIfMobInteractsWithPlate(World par1World, int par2, int par3, int par4, int par5)
	{
        int i1 = this.getPlateState(par1World, par2, par3, par4);
		boolean flag = par5 > 0;
		boolean flag1 = i1 > 0;

		if (par5 != i1)
		{
            par1World.setBlockMetadataWithNotify(par2, par3, par4, this.getMetaFromWeight(i1), 2);
			this.func_94354_b_(par1World, par2, par3, par4);
			par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
		}

		if (flag1)
		{
			par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
		}
	}

	@Override
	public int tickRate(World world)
	{
		return 10;
	}

	/**
	 * The glass pressure plate will not yield itself once broken.
	 * 
	 * @param par1Random
	 * @return 0
	 */
	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
	 */
	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public int getMobilityFlag()
	{
		return 1;
	}
}
