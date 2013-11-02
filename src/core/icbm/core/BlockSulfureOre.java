package icbm.core;

import icbm.core.base.BlockICBM;

import java.util.Random;

import net.minecraft.block.material.Material;

public class BlockSulfureOre extends BlockICBM
{
	public BlockSulfureOre(int id)
	{
		super(id, "oreSulfur", Material.rock);
		this.setHardness(3.0f);
		this.setCreativeTab(CreativeTabICBM.INSTANCE);
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return ICBMCore.itemSulfurDust.itemID;
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 3 + par1Random.nextInt(3);
	}

	@Override
	public int quantityDroppedWithBonus(int par1, Random par2Random)
	{
		return this.quantityDropped(par2Random) + par2Random.nextInt(par1 + 1);
	}

	@Override
	public boolean hasTileEntity(int metadata)
	{
		return false;
	}
}
