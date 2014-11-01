package icbm.core.blocks;

import icbm.core.ICBMCore;
import icbm.core.prefab.BlockICBM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import universalelectricity.api.UniversalElectricity;

public class BlockProximityDetector extends BlockICBM
{
    public BlockProximityDetector(int id)
    {
        super(id, "proximityDetector", UniversalElectricity.machine);
        this.requireSidedTextures = true;
    }

    /** From the specified side and block metadata retrieves the blocks texture. Args: side, metadata */
    @Override
    public Icon getIcon(int side, int metadata)
    {
        return side == 0 ? this.iconBottom : (side == 1 ? this.iconTop : this.iconSide);
    }
}
