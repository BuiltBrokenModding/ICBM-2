package icbm.common;

import java.util.Random;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;

public class BBuoLiPan extends BlockPressurePlate
{
	protected BBuoLiPan(int id)
	{
		super(id, 0, EnumMobType.everything, Material.glass);
		this.setTickRandomly(true);
		this.setResistance(1F);
		this.setHardness(0.3F);
		this.setStepSound(soundGlassFootstep);
		this.setBlockName("glassPressurePlate");
		this.setCreativeTab(ZhuYao.TAB);
		this.setTextureFile(ZhuYao.BLOCK_TEXTURE_FILE);
	}

	@Override
	public int tickRate()
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
