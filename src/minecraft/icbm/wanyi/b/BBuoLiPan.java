package icbm.wanyi.b;

import icbm.api.ICBMTab;
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
		this.setTextureFile(ZhuYao.BLOCK_PATH);
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
