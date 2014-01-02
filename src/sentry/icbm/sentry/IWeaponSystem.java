package icbm.sentry;

import net.minecraft.entity.Entity;
import universalelectricity.api.vector.VectorWorld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Applied to objects that are weapons in nature
 * 
 * @author Darkguardsman */
public interface IWeaponSystem
{
    public IWeaponPlatform getWeaponHolder();

    public boolean canFire();

    public void fire(VectorWorld target);

    public void fire(Entity entity);
    
    @SideOnly(Side.CLIENT)
    public void renderShot(VectorWorld target);
}
