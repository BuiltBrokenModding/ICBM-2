package com.builtbroken.icbm.content;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Created by robert on 12/4/2014.
 */
public class BlockExplosiveMarker extends Block
{
    public BlockExplosiveMarker()
    {
        super(Material.circuits);
        setBlockBounds(0.45F, 0.45F, 0.45F, 0.55F, 0.55F, 0.55F);
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.hardened_clay.getIcon(side, meta);
    }

    @Override
    public void registerBlockIcons(IIconRegister r)
    {

    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_) {}
}
