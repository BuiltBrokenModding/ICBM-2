package icbm;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
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
	public int getBlockTextureFromSideAndMetadata(int side, int metadata)
    {
        return this.blockIndexInTexture+metadata;
    }
	
	@Override
    protected int damageDropped(int metadata)
    {
		return metadata;
    }
}
