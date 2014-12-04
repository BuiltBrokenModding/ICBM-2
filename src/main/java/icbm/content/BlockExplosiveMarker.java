package icbm.content;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 12/4/2014.
 */
public class BlockExplosiveMarker extends BlockAir
{
    public BlockExplosiveMarker()
    {
        setBlockBounds(0.45F, 0.45F, 0.45F, 0.55F, 0.55F, 0.55F);
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return Blocks.glass.getIcon(side, meta);
    }

    @Override
    public int getRenderType()
    {
        return 0;
    }
}
