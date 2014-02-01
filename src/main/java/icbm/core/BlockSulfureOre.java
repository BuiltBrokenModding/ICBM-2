package icbm.core;

import icbm.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Random;

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
