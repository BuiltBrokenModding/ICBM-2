package icbm;

import java.util.List;
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import universalelectricity.basiccomponents.BasicComponents;

public class BlockSulfurOre extends ICBMBlock
{
	public BlockSulfurOre(int id)
	{
		super(id, 10, Material.rock);
        this.setBlockName("Sulfur Ore");
        this.setHardness(3.0f);
        this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
	public int getBlockTextureFromSide(int side)
    {
        return this.blockIndexInTexture;
    }
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3)
    {
        return ICBM.itemSulfur.shiftedIndex;
    }
	
	@Override
    public int quantityDropped(Random par1Random)
    {
        return 2 + par1Random.nextInt(2);
    }
	
	@Override
	public int quantityDroppedWithBonus(int par1, Random par2Random)
    {
        return this.quantityDropped(par2Random) + par2Random.nextInt(par1 + 1);
    }
}
