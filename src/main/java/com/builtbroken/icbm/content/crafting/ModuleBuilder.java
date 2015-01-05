package com.builtbroken.icbm.content.crafting;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.Engine;
import com.google.common.collect.HashBiMap;
import net.minecraft.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by robert on 12/28/2014.
 */
public class ModuleBuilder
{
    public static final String SAVE_ID = "moduleID";
    HashBiMap<String, Class<? extends AbstractModule>> idToCLassMap = HashBiMap.create();
    HashMap<String, List<String>> modToModules = new HashMap();

    protected boolean register(String mod_id, String name, Class<? extends AbstractModule> clazz)
    {
        //Value checks to prevent other modders from making mistakes
        if (clazz == null)
        {
            throw new IllegalArgumentException("ModuleBuidler.register(" + mod_id + ", " + name + ", clazz) clazz can not be null");
        }

        if (name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("ModuleBuidler.register(" + mod_id + ", name, " + clazz + ") name can not be empty");
        }

        if (mod_id == null || mod_id.isEmpty())
        {
            throw new IllegalArgumentException("ModuleBuidler.register(mod_id, " + name + ", " + clazz + ") mod_id is invalid");
        }

        //Check to make sure we don't override something already registered
        String id = mod_id + "." + name;
        if (!idToCLassMap.containsKey(id))
        {
            //Add module to id list
            idToCLassMap.put(id, clazz);

            //Adds the module to a per mod list for look up later
            List<String> list = null;
            if (modToModules.containsKey(mod_id))
            {
                list = modToModules.get(mod_id);
            }
            if (list == null)
            {
                list = new ArrayList();
            }
            list.add(id);
            modToModules.put(mod_id, list);

            return true;

        }
        return false;
    }

    public boolean isRegistered(String id)
    {
        return this.idToCLassMap.containsKey(id);
    }

    public Set<String> getIDs()
    {
        return idToCLassMap.keySet();
    }

    public String getID(AbstractModule module)
    {
        Class<? extends AbstractModule> clazz = module.getClass();
        if(idToCLassMap.inverse().containsKey(clazz))
        {
            return idToCLassMap.inverse().get(clazz);
        }
        return module.getClass().getSimpleName();
    }

    /**
     * Builds the module from the item stack
     *
     * @param stack - item stack that is a module containing the NBT string id
     *              to use to construct the module
     * @return the module or null if something went wrong
     */
    public AbstractModule build(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey(SAVE_ID))
        {
            String id = stack.getTagCompound().getString(SAVE_ID);
            if (idToCLassMap.containsKey(id))
            {
                if (idToCLassMap.get(id) != null)
                {
                    try
                    {
                        return idToCLassMap.get(id).getConstructor(ItemStack.class).newInstance(stack).load();
                    }
                    catch (InstantiationException e)
                    {
                        ICBM.LOGGER.error("ModuleBuilder failed to create module from class " + idToCLassMap.get(id));
                        if (Engine.runningAsDev)
                            e.printStackTrace();
                    } catch (IllegalAccessException e)
                    {
                        ICBM.LOGGER.error("ModuleBuilder was prevented access to class " + idToCLassMap.get(id));
                        if (Engine.runningAsDev)
                            e.printStackTrace();
                    } catch (NoSuchMethodException e)
                    {
                        ICBM.LOGGER.error("ModuleBuilder failed to find  constructor(ItemStack.class) for class " + idToCLassMap.get(id));
                        if (Engine.runningAsDev)
                            e.printStackTrace();
                    } catch (InvocationTargetException e)
                    {
                        ICBM.LOGGER.error("ModuleBuilder failed to find to invoke constructor(ItemStack.class) for class " + idToCLassMap.get(id));
                        if (Engine.runningAsDev)
                            e.printStackTrace();
                    }
                }
                else
                {
                    ICBM.LOGGER.error("ModuleBuilder, module " + id + " has no class registered.");
                }
            }
        }
        else
        {
            ICBM.LOGGER.error("ModuleBuilder failed to create module due to NBT data being " + ( stack.getTagCompound() == null ? "null" : "invalid ") + " for item stack " + stack);
        }
        return null;
    }
}
