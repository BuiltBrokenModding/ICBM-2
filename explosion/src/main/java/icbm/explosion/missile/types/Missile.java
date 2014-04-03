package icbm.explosion.missile.types;

import icbm.Reference;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.explosive.Explosive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Missile extends Explosive
{
    @SideOnly(Side.CLIENT)
    private ResourceLocation resourceLocation;

    @SideOnly(Side.CLIENT)
    private IModelCustom model;

    protected String modelName;

    public Missile(String mingZi, int tier)
    {
        super(mingZi, tier);
    }

    /** Called when launched. */
    public void launch(EntityMissile missileObj)
    {
    }

    /** Called every tick while flying. */
    public void update(EntityMissile missileObj)
    {
    }

    public boolean onInteract(EntityMissile missileObj, EntityPlayer entityPlayer)
    {
        return false;
    }

    /** Is this missile compatible with the cruise launcher?
     * 
     * @return */
    public boolean isCruise()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getMissileResource()
    {
        if (this.resourceLocation == null)
        {
            this.resourceLocation = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "missile_" + this.getUnlocalizedName() + ".png");
        }

        return this.resourceLocation;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IModelCustom getMissileModel()
    {
        try
        {
            if (this.model == null)
                model = AdvancedModelLoader.loadModel(Reference.MODEL_DIRECTORY + this.modelName);
        }
        catch (ModelFormatException e)
        {
            System.out.println("Crash  ModelName: " + this.modelName);
            e.printStackTrace();
        }

        return model;
    }
}
