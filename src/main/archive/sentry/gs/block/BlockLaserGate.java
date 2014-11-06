package icbm.sentry.gs.block;

import icbm.core.prefab.BlockICBM;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import resonant.api.IRotatable;
import universalelectricity.api.UniversalElectricity;

public class BlockLaserGate extends BlockICBM {

	public BlockLaserGate(int id) {
		super(id, "laserGate", UniversalElectricity.machine);
	}
	
    /** Called when the block is placed in the world. */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase par5EntityLiving, ItemStack itemStack)
    {
        int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IRotatable)
        {
            IRotatable rotatableEntity = ((IRotatable) tileEntity);

            switch (angle)
            {
                case 0:
                    rotatableEntity.setDirection(ForgeDirection.getOrientation(3));
                    break;
                case 1:
                    rotatableEntity.setDirection(ForgeDirection.getOrientation(4));
                    break;
                case 2:
                    rotatableEntity.setDirection(ForgeDirection.getOrientation(2));
                    break;
                case 3:
                    rotatableEntity.setDirection(ForgeDirection.getOrientation(5));
                    break;
            }
        }
    }
    
    public static boolean canBePlacedAt(World world, int x, int y, int z, int metadata, int direction)
    {
        switch (metadata)
        {
            default:
            {
                return world.getBlockMaterial(x, y - 1, z).isSolid();
            }
            case 0:
            {
                // Launcher Base
                if (direction == 0 || direction == 2)
                {
                    return world.getBlockId(x, y, z) == 0 &&
                    // Left
                            world.getBlockId(x + 1, y, z) == 0 && world.getBlockId(x + 1, y + 1, z) == 0 && world.getBlockId(x + 1, y + 2, z) == 0 &&
                            // Right
                            world.getBlockId(x - 1, y, z) == 0 && world.getBlockId(x - 1, y + 1, z) == 0 && world.getBlockId(x - 1, y + 2, z) == 0;
                }
                else if (direction == 1 || direction == 3)
                {
                    return world.getBlockId(x, y, z) == 0 &&
                    // Front
                            world.getBlockId(x, y, z + 1) == 0 && world.getBlockId(x, y + 1, z + 1) == 0 && world.getBlockId(x, y + 2, z + 1) == 0 &&
                            // Back
                            world.getBlockId(x, y, z - 1) == 0 && world.getBlockId(x, y + 1, z - 1) == 0 && world.getBlockId(x, y + 2, z - 1) == 0;
                }
            }
            case 2:
            {
                // Launcher Frame
                return world.getBlockMaterial(x, y - 1, z).isSolid() && world.getBlockId(x, y, z) == 0 && world.getBlockId(x, y + 1, z) == 0 && world.getBlockId(x, y + 2, z) == 0;
            }
            case 4:
            {
                return world.getBlockId(x, y, z) == 0 && world.getBlockId(x, y + 1, z) == 0;
            }
        }
    }
    
    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        int direction = 0;
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IRotatable)
        {
            direction = ((IRotatable) tileEntity).getDirection().ordinal();
        }

        return canBePlacedAt(world, x, y, z, world.getBlockMetadata(x, y, z), direction);
    }
    
    @Override
    public TileEntity createNewTileEntity(World var1) {
    	return new TileLaserGate();
    }
}
