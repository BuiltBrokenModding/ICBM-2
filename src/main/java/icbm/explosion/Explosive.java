package icbm.explosion;

import icbm.ICBM;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import resonant.api.explosion.IExplosive;
import resonant.api.explosion.Trigger;

/** The explosive registry class. Used to register explosions. */
public abstract class Explosive implements IExplosive
{
    private String unlocalizedExplosiveName;
    private int tier;
    protected boolean isDisabled;

    protected Explosive(String name, int tier)
    {
        this.unlocalizedExplosiveName = name;
        this.tier = tier;

        //TODO this.flagName = FlagRegistry.registerFlag("ban_" + this.unlocalizedExplosiveName);
        //this.isDisabled = Settings.CONFIGURATION.get("Disable_Explosives", "Disable " + this.unlocalizedExplosiveName, false).getBoolean(false);

    }

    @Override
    public String getUnlocalizedName()
    {
        return this.unlocalizedExplosiveName;
    }

    /** Called to add the recipe for this explosive */
    public void init()
    {

    }

    @Override
    public final void createExplosion(World world, double x, double y, double z, Trigger trigger)
    {
        if (!this.isDisabled)
        {
            this.doCreateExplosion(world, x, y, z, trigger);
        }
    }

    public abstract void doCreateExplosion(World world, double x, double y, double z, Trigger trigger);

}
