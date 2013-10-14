package icbm.explosion.missile.modular;

import calclavia.lib.multiblock.IMultiBlock;
import icbm.core.ICBMCore;
import icbm.core.base.BlockICBM;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.tile.IRotatable;
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
       return false;
    }

}
