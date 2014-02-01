package icbm.explosion.fx;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

@SideOnly(Side.CLIENT)
public class FXEnderPortalPartical extends EntityPortalFX
{
    public FXEnderPortalPartical(World par1World, Vector3 position, float red, float green, float blue, float scale, double distance)
    {
        super(par1World, position.x, position.y, position.z, 0, 0, 0);
        this.particleScale = scale;
        try
        {
            ReflectionHelper.setPrivateValue(EntityPortalFX.class, this, this.particleScale, 0);
        }
        catch (Exception e)
        {
            FMLLog.warning("Failed to correctly spawn ICBM portal effects.");
        }
        this.renderDistanceWeight = distance;
    }

}
