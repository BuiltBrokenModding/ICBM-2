package icbm.sentry.workbench.ammo;

import icbm.core.prefab.BlockICBM;
import icbm.explosion.ICBMExplosion;
import icbm.sentry.ICBMSentry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.prefab.tile.IRedstoneReceptor;
import calclavia.lib.prefab.tile.IRotatable;

public class BlockMunitionPrinter extends BlockICBM
{
    public BlockMunitionPrinter(int id)
    {
        super(id, "munitionPrinter");
    }

    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        player.openGui(ICBMSentry.INSTANCE, 2, world, x, y, z);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileMunitionPrinter();
    }
}
