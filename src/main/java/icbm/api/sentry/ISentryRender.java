package icbm.api.sentry;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISentryRender
{
    /** Called to render the body of the sentry, or rather the stand by which the gun rests on */
    void renderBody(TileEntity entity, ForgeDirection side);

    /** Called to render the turret of the sentry. Yaw and pitch rotation will be applied. */
    void renderTurret(TileEntity entity, ForgeDirection side);

    /** Called to render any other part of the sentry */
    void renderOther(TileEntity entity, ForgeDirection side);

    void renderItem(ItemStack stack);
}
