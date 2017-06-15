package com.builtbroken.icbm.content.missile.data.missile;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.missile.data.casing.MissileCasingData;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.engine.RocketEngine;
import com.builtbroken.icbm.content.missile.parts.guidance.Guidance;
import com.builtbroken.icbm.content.missile.parts.warhead.Warhead;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * Instance of a missile
 *
 * @author Darkguardsman
 */
public class Missile implements IMissile
{
    private static final String NBT_MISSILE_CASING = "missileCasingID";

    /** Data describing how the missile looks and functions */
    public MissileCasingData data;

    /** Skin currently in use for the missile */
    public String textureSkinID = "icbm.default";

    private IWarhead warhead;
    private IGuidance guidance;
    private IRocketEngine engine;

    public Missile(MissileCasingData data)
    {
        this.data = data;
    }

    public Missile(ItemStack stack) throws IllegalArgumentException
    {
        if (stack.getItem() instanceof IMissileItem)
        {
            //Find data
            data = getMissileCasingData(stack);

            //Load save
            if (stack.getTagCompound() != null)
            {
                load(stack.getTagCompound());
            }
        }
        else
        {
            throw new IllegalArgumentException("Invalid stack input for missile creation, stack = " + stack);
        }
    }

    @Override
    public final String getSaveID()
    {
        return MissileModuleBuilder.INSTANCE.getID(this);
    }

    @Override
    public void save(ItemStack stack)
    {
        NBTTagCompound nbt = stack.getTagCompound() != null ? stack.getTagCompound() : new NBTTagCompound();
        save(nbt);
        if (nbt.hasNoTags())
        {
            stack.setTagCompound(null);
        }
        else
        {
            stack.setTagCompound(nbt);
        }
    }

    @Override
    public List<IModule> getSubModules()
    {
        List<IModule> module = new ArrayList();
        if (warhead != null)
        {
            module.add(warhead);
        }
        if (guidance != null)
        {
            module.add(guidance);
        }
        if (engine != null)
        {
            module.add(engine);
        }
        return module;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            if (nbt.hasKey("warhead"))
            {
                IModule module = getModule(nbt, "warhead");
                setWarhead(module instanceof IWarhead ? (IWarhead) module : null);
            }
            if (nbt.hasKey("engine"))
            {
                IModule module = getModule(nbt, "engine");
                setEngine(module instanceof IRocketEngine ? (IRocketEngine) module : null);
            }
            if (nbt.hasKey("guidance"))
            {
                IModule module = getModule(nbt, "guidance");
                setGuidance(module instanceof IGuidance ? (IGuidance) module : null);
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (nbt != null)
        {
            if (getWarhead() != null)
            {
                nbt.setTag("warhead", getWarhead().toStack().writeToNBT(new NBTTagCompound()));
            }
            if (getEngine() != null)
            {
                nbt.setTag("engine", getEngine().toStack().writeToNBT(new NBTTagCompound()));
            }
            if (getGuidance() != null)
            {
                nbt.setTag("guidance", getGuidance().toStack().writeToNBT(new NBTTagCompound()));
            }
        }
        return nbt;
    }

    @Override
    public boolean canInstallModule(IModule module)
    {
        return module instanceof RocketEngine || module instanceof Warhead || module instanceof Guidance;
    }

    @Override
    public boolean installModule(IModule module)
    {
        if (module instanceof RocketEngine && engine == null)
        {
            setEngine((RocketEngine) module);
            return getEngine() == module;
        }
        else if (module instanceof Warhead && warhead == null)
        {
            setWarhead((Warhead) module);
            return getWarhead() == module;
        }
        else if (module instanceof Guidance && guidance == null)
        {
            setGuidance((Guidance) module);
            return getGuidance() == module;
        }
        return false;
    }

    @Override
    public boolean canLaunch()
    {
        return getEngine() != null && getEngine().getMaxDistance(this) > 0 && getEngine().getSpeed(this) > 0;
    }

    public void setWarhead(IWarhead warhead)
    {
        this.warhead = warhead;
    }

    public void setGuidance(IGuidance guidance)
    {
        this.guidance = guidance;
    }

    public void setEngine(IRocketEngine engine)
    {
        this.engine = engine;
    }

    public IWarhead getWarhead()
    {
        return warhead;
    }

    public IGuidance getGuidance()
    {
        return guidance;
    }

    public IRocketEngine getEngine()
    {
        return engine;
    }

    @Override
    public int getMissileSize()
    {
        return data.getMissileBodySize();
    }

    @Override
    public String toString()
    {
        //TODO maybe cache being in missile enum to save a little cpu time?
        return data.getContentID() + " Missile[" + getWarhead() + ", " + getGuidance() + ", " + getEngine() + "]";
    }

    @Override
    public ItemStack toStack()
    {
        ItemStack stack = new ItemStack(ICBM_API.itemMissile, 1, data != null ? data.getMissileBodySize() : 0);
        save(stack);
        return stack;
    }

    @Override
    public String getUnlocalizedName()
    {
        return data.getContentID();
    }

    /**
     * Max number of hit points the missile has
     *
     * @return
     */
    public float getMaxHitPoints()
    {
        return data != null ? data.getHealth() : 10; //TODO implement
    }

    /**
     * How tall is the missile, mainly used
     * for collsion box code and rendering
     *
     * @return height in meters
     */
    public double getHeight()
    {
        return data != null ? data.getHeight() : 3; //TODO implement
    }


    /**
     * How wide is the missile, mainly used
     * for collsion box code and rendering
     *
     * @return height in meters
     */
    public double getWidth()
    {
        return data != null ? data.getWidth() : 0.5; //TODO implement
    }


    /**
     * Called to create a new missile instance based on the size and explosive.
     * Only use this if you do not care on the exact details of the missile.
     *
     * @param size    - size to use
     * @param handler - explosive to use
     * @return default missile casing with a random explosive item of type
     */
    public static Missile createMissile(MissileSize size, IExplosiveHandler handler)
    {
        Missile missile = new Missile(size.defaultMissileCasing);
        missile.setWarhead(MissileModuleBuilder.INSTANCE.buildWarhead(size.warhead_casing));
        List<ItemStackWrapper> items = ExplosiveRegistry.getItems(handler);
        if (items.size() > 0)
        {
            missile.getWarhead().setExplosiveStack(items.get(0).itemStack);
        }
        return missile;
    }

    /**
     * Bypasses the need to load the entire missile to access just the warhead data.
     *
     * @param itemStack - missile stack
     * @return warhead data
     */
    public static IWarhead loadWarheadFromMissileStack(ItemStack itemStack)
    {
        if (itemStack != null && itemStack.getTagCompound() != null)
        {
            NBTTagCompound nbt = itemStack.getTagCompound();
            if (nbt.hasKey("warhead"))
            {
                return (IWarhead) getModule(nbt, "warhead");
            }
        }
        return null;
    }

    public static MissileCasingData getMissileCasingData(ItemStack stack)
    {
        MissileSize size = MissileSize.fromMeta(stack.getItemDamage());
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(NBT_MISSILE_CASING))
        {
            return size.getMissileData(stack.getTagCompound().getString(NBT_MISSILE_CASING));
        }
        else
        {
            return size.defaultMissileCasing;
        }
    }

    //Use to load module with less duplicate code
    private static IModule getModule(NBTTagCompound nbt, String id)
    {
        ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(id));
        if (stack != null && stack.getItem() instanceof IModuleItem)
        {
            return ((IModuleItem) stack.getItem()).getModule(stack);
        }
        return null;
    }
}
