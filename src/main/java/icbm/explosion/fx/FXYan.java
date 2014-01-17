package icbm.explosion.fx;

import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXYan extends EntitySmokeFX
{
    public FXYan(World par1World, Vector3 position, float red, float green, float blue, float scale, double distance)
    {
        super(par1World, position.x, position.y, position.z, 0, 0, 0, scale);
        this.renderDistanceWeight = distance;
        this.particleRed = red;
        this.particleBlue = blue;
        this.particleGreen = green;

        float colorVarient = (float) (Math.random() * 0.90000001192092896D);
        this.particleRed *= colorVarient;
        this.particleBlue *= colorVarient;
        this.particleGreen *= colorVarient;
    }

    public FXYan setAge(int age)
    {
        this.particleMaxAge = age;
        return this;
    }
}
