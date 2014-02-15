package icbm.core;

import icbm.Reference;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/** World generated block
 * 
 * @author Calcalvia */
public class BlockSulfureOre extends Block
{
    public BlockSulfureOre(int id)
    {
        super(Settings.CONFIGURATION.getBlock("oreSulfur", id).getInt(id), Material.rock);
        this.setUnlocalizedName(Reference.PREFIX + "oreSulfur");
        this.setTextureName(Reference.PREFIX + "oreSulfur");
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(3.0f);
    }

    @Override
    public int idDropped(int id, Random rand, int meta)
    {
        return ICBMCore.itemSulfurDust.itemID;
    }

    @Override
    public int quantityDropped(Random rand)
    {
        return 3 + rand.nextInt(3);
    }

    @Override
    public int quantityDroppedWithBonus(int drop, Random rand)
    {
        return this.quantityDropped(rand) + rand.nextInt(drop + 1);
    }
}
