package icbm.explosion.missile.modular;

import icbm.core.ICBMCore;
import icbm.core.base.BlockICBM;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
import calclavia.lib.multiblock.IMultiBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Multi-block table use to hold a missile prototype while the player is working on the design.
 * 3x1x1 in size
 *
 * @author DarkGuardsman */
public class BlockMissileTable extends BlockICBM
{
    public BlockMissileTable(int id)
    {
        super(id, "MissileTable", UniversalElectricity.machine);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityMissileTable();
    }

    /** Called when the block is placed in the world. */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onBlockPlacedBy(world, z, y, z, entityLiving, itemStack);
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof IMultiBlock)
        {
            ICBMCore.blockMulti.createMultiBlockStructure((IMultiBlock) tileEntity);
        }
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        ForgeDirection side = ForgeDirection.UP;
        byte rot = 0;
        if (world.getBlockTileEntity(x, y, z) instanceof TileEntityMissileTable)
        {
            side = ((TileEntityMissileTable) world.getBlockTileEntity(x, y, z)).placedSide;
            rot = ((TileEntityMissileTable) world.getBlockTileEntity(x, y, z)).rotationSide;
        }
        return canPlaceBlockAt(world, x, y, z, side, rot);
    }

    /** Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y,
     * z
     *
     * @param rot
     * @param placeSide */
    public static boolean canPlaceBlockAt(World world, int x, int y, int z, ForgeDirection placeSide, int rot)
    {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block == null || block.isBlockReplaceable(world, x, y, z))
        {
            Vector3[] vecs = TileEntityMissileTable.getMultiBlockVectors(placeSide, (byte) rot);
            for (int i = 0; i > vecs.length; i++)
            {
                block = Block.blocksList[world.getBlockId(x + vecs[i].intX(), y + vecs[i].intY(), z + vecs[i].intZ())];
                if (block != null && !block.isBlockReplaceable(world, x, y, z))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
