package icbm.content.tile;

import icbm.TabICBM;
import icbm.content.prefab.tile.BlockICBM;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import resonant.api.blocks.IAntiPoisonBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReinforcedGlass extends BlockICBM implements IAntiPoisonBlock
{
    public BlockReinforcedGlass()
    {
        super("glassReinforced", Material.glass);
        this.setResistance(48);
        this.setCreativeTab(TabICBM.INSTANCE);
    }

    /** Returns which pass should this block be rendered on. 0 for solids and 1 for alpha */
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass()
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        Block i1 = par1IBlockAccess.getBlock(par2, par3, par4);
        return i1 == this ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }

    /** Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the
     * shared face of two adjacent blocks and also whether the player can attach torches, redstone
     * wire, etc to this block. */
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    /** If this block doesn't render as an ordinary block it will return False (examples: signs,
     * buttons, stairs, etc) */
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /** Return true if a player with Silk Touch can harvest this block directly, and not its normal
     * drops. */
    @Override
    protected boolean canSilkHarvest()
    {
        return true;
    }

    @Override
    public boolean isPoisonPrevention(World par1World, int x, int y, int z, String type)
    {
        return true;
    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return false;
    }
}
