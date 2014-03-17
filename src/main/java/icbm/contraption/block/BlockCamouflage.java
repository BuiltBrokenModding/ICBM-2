package icbm.contraption.block;

import icbm.api.ICamouflageMaterial;
import icbm.core.TabICBM;
import icbm.core.prefab.BlockICBM;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.content.BlockInfo;

@BlockInfo(tileEntity = "icbm.contraption.block.TileCamouflage")
public class BlockCamouflage extends BlockICBM
{
    public BlockCamouflage(int id)
    {
        super(id, "camouflage", Material.cloth);
        this.setHardness(0.3F);
        this.setResistance(1F);
        this.setStepSound(Block.soundClothFootstep);
        this.setCreativeTab(TabICBM.INSTANCE);
    }

    /** Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z,
     * side */
    @Override
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
        TileEntity t = par1IBlockAccess.getBlockTileEntity(x, y, z);

        if (t != null)
        {
            if (t instanceof TileCamouflage)
            {
                TileCamouflage tileEntity = (TileCamouflage) t;

                if (tileEntity.canRenderSide(ForgeDirection.getOrientation(side)))
                {
                    return Block.glass.getBlockTextureFromSide(side);
                }

                Block block = Block.blocksList[tileEntity.getMimicBlockID()];

                if (block != null)
                {
                    try
                    {
                        Icon blockIcon = Block.blocksList[tileEntity.getMimicBlockID()].getIcon(side, tileEntity.getMimicBlockMeta());

                        if (blockIcon != null)
                        {
                            return blockIcon;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return this.blockIcon;
    }

    @Override
    public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        try
        {
            if (par5EntityPlayer.getCurrentEquippedItem() != null)
            {
                if (par5EntityPlayer.getCurrentEquippedItem().itemID < Block.blocksList.length)
                {
                    Block block = Block.blocksList[par5EntityPlayer.getCurrentEquippedItem().itemID];

                    if (block != null && block != this)
                    {
                        if (block instanceof ICamouflageMaterial || (isNormalCube(block.blockID) && (block.getRenderType() == 0 || block.getRenderType() == 31)))
                        {
                            ((TileCamouflage) par1World.getBlockTileEntity(x, y, z)).setMimicBlock(block.blockID, par5EntityPlayer.getCurrentEquippedItem().getItemDamage());
                            par1World.markBlockForRenderUpdate(x, y, z);
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity t = par1World.getBlockTileEntity(x, y, z);

        if (t != null)
        {
            if (t instanceof TileCamouflage)
            {
                ((TileCamouflage) par1World.getBlockTileEntity(x, y, z)).toggleRenderSide(ForgeDirection.getOrientation(side));
                par1World.markBlockForRenderUpdate(x, y, z);
            }
        }

        return true;
    }

    @Override
    public boolean onSneakUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity t = par1World.getBlockTileEntity(x, y, z);

        if (t != null)
        {
            if (t instanceof TileCamouflage)
            {
                ((TileCamouflage) par1World.getBlockTileEntity(x, y, z)).toggleCollision();
            }
        }

        return true;
    }

    /** Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color.
     * Note only called when first determining what to render. */
    @Override
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int x, int y, int z)
    {
        try
        {
            TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

            if (tileEntity instanceof TileCamouflage)
            {
                int haoMa = ((TileCamouflage) tileEntity).getMimicBlockID();

                if (haoMa < Block.blocksList.length)
                {
                    Block block = Block.blocksList[haoMa];

                    if (block != null)
                    {
                        return block.colorMultiplier(par1IBlockAccess, x, y, x);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 16777215;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
    {
        TileEntity t = par1World.getBlockTileEntity(x, y, z);

        if (t != null)
        {
            if (t instanceof TileCamouflage)
            {
                if (((TileCamouflage) t).getCanCollide())
                {
                    return super.getCollisionBoundingBoxFromPool(par1World, x, y, z);
                }
            }
        }

        return null;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
        return var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileCamouflage();
    }


}
