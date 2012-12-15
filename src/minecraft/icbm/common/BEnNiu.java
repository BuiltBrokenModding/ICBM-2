package icbm.common;

import java.util.Random;

import net.minecraft.block.BlockButton;

public class BEnNiu extends BlockButton
{
	protected BEnNiu(int id)
	{
		super(id, 0, true);
		this.setTickRandomly(true);
		this.setBlockName("glassButton");
		this.setStepSound(soundGlassFootstep);
		this.setCreativeTab(ZhuYao.TAB);
		this.setTextureFile(ZhuYao.BLOCK_TEXTURE_FILE);
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * How many world ticks before ticking
	 */
	@Override
	public int tickRate()
	{
		return 4;
	}
}
