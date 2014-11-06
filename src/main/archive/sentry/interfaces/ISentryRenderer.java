package icbm.sentry.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

/** Render class that handles how the sentry renders when hosted by another object
 * 
 * @author Darkguardsman */
public interface ISentryRenderer
{
    public void render(ForgeDirection side, ITurretProvider container, float yaw, float pitch);

    public ResourceLocation getTexture(EntityPlayer player, ITurretProvider container);
}
