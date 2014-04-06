package icbm.sentry.turret;

import icbm.core.ICBMCore;
import icbm.sentry.ICBMSentry;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.render.TurretRenderer;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.utility.ReflectionUtility;
import calclavia.lib.utility.nbt.SaveManager;

import com.google.common.collect.HashBiMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** This is the main registry for all sentry object. Sentries should be registered using string names
 * to prevent conflict. This class will later be moved to the API so that other mods can generate
 * sentries.
 * 
 * @author Darkguardsman */
public class TurretRegistry
{
    private static HashBiMap<String, Class<? extends Turret>> sentryMap = HashBiMap.create();

    @SideOnly(Side.CLIENT)
    private static HashMap<Class<? extends Turret>, TurretRenderer> sentryRenderMap;

    // TODO Fix SaveManager to handle the Tile argument
    /** Registers a sentry to be handled by ICBM
     * 
     * @param key - ID the sentry goes by, should be unique, will be registered with save manager
     * prefixed by 'Sentry-'
     * @param sentry - class file that extends Sentry.class and has a no param constructor */
    public static void registerSentry(String key, Class<? extends Turret> sentry)
    {
        synchronized (sentryMap)
        {
            if (!sentryMap.containsKey(key))
            {
                sentryMap.put(key, sentry);
                SaveManager.registerClass("Sentry-" + key, sentry);
            }
        }
    }

    /** Registers a renderer for a sentry in the same way a TileEntitySpecialRender is handled.
     * 
     * @param sentry - sentry class this renderer is for
     * @param render - renderer instance and is treated as static */
    @SideOnly(Side.CLIENT)
    public static void registerSentryRenderer(Class<? extends Turret> sentry, TurretRenderer render)
    {
        if (sentryRenderMap == null)
        {
            sentryRenderMap = new HashMap<Class<? extends Turret>, TurretRenderer>();
        }
        synchronized (sentryRenderMap)
        {
            if (!sentryRenderMap.containsKey(sentry))
            {
                System.out.println("[SentryRegistry]Registering Sentry Renderer. Sentry: " + sentry.getName() + "  Renderer: " + render.getClass().getName());
                sentryRenderMap.put(sentry, render);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static TurretRenderer getRenderFor(Turret sentry)
    {
        if (sentry != null)
        {
            Class<?> clazz = sentry.getClass();

            if (sentryRenderMap.containsKey(clazz))
            {
                return sentryRenderMap.get(sentry.getClass());
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public static TurretRenderer getRenderFor(Class<? extends Turret> sentry)
    {
        return sentryRenderMap.get(sentry);
    }

    /** Gets the sentry map */
    public static HashBiMap<String, Class<? extends Turret>> getSentryMap()
    {
        return sentryMap;
    }

    /** Grabs the class that links back the sentry id */
    public static Class<? extends Turret> getSentry(String id)
    {
        return sentryMap.get(id);
    }

    public static String getID(Turret sentry)
    {
        return sentryMap.inverse().get(sentry.getClass());
    }

    public static String getID(Class<? extends Turret> sentry)
    {
        return sentryMap.inverse().get(sentry);
    }

    /** Builds a sentry from a save using the SaveManager. This assumes that the sentries save ID was
     * saved to the NBTTagCompound. If it was not then use the other construct method followed by
     * calling the load method.
     * 
     * @param compoundTag - NBT save
     * @param args - args that will be passed into the sentry's class constructor
     * @return new Sentry instance or null if it failed */
    public static Turret constructSentry(NBTTagCompound compoundTag, Object... args)
    {
        Object object = SaveManager.createAndLoad(compoundTag, args);
        if (object instanceof Turret)
        {
            return (Turret) object;
        }
        return null;
    }

    /** Builds a new sentry instance from the given sentry ID and argument. Sentry ID must be
     * registered to a class, and the args must match a constructor in the registered class.
     * 
     * @param sentryID the key of the Sentry class used in SentryRegistry.registerSentry()
     * @param args arguments the Sentry's class constructor requires
     * @return the Sentry object for the given id and tile, or null it failed to create the sentry */
    public static Turret constructSentry(String sentryID, Object... args)
    {
        Object candidate = null;
        Turret sentryModule = null;

        try
        {
            Class clazz = SaveManager.getClass(sentryID);

            if (clazz != null)
            {
                Constructor<?> con = ReflectionUtility.getConstructorWithArgs(clazz, args);

                if (con != null)
                {
                    candidate = con.newInstance(args);
                }

                if (candidate instanceof Turret)
                {
                    sentryModule = (Turret) candidate;
                }
                else
                {
                    ICBMCore.LOGGER.severe("Construction of turret '" + sentryID + "' failed, an unexpected object '" + con + "' was created");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return sentryModule;

    }

    public static ItemStack getItemStack(Class<? extends Turret> sentry)
    {
        ItemStack stack = new ItemStack(ICBMSentry.blockTurret);
        NBTTagCompound itemNbt = new NBTTagCompound();
        NBTTagCompound sentry_nbt = new NBTTagCompound();
		System.out.println("itemNBT = unloc name :" + getID(sentry) + " \n\n\n\n\n\n\n\n\n\n\n\n\n");
		itemNbt.setString("unlocalizedName", getID(sentry));
        sentry_nbt.setString(ITurret.SENTRY_TYPE_SAVE_ID, SaveManager.getID(sentry));
        itemNbt.setCompoundTag(ITurret.SENTRY_OBJECT_SAVE, sentry_nbt);

        stack.setTagCompound(itemNbt);
        return stack;
    }
}
