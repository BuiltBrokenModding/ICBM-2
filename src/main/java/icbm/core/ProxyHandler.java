package icbm.core;

import calclavia.lib.modproxy.IIntegrationProxy;
import calclavia.lib.modproxy.IMod;
import calclavia.lib.modproxy.LoadPhase;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * the Class that handles the submods of All ICBM
 *
 * to have the submodules work, You must register them in this class,
 * Adding support for a submodule includes only aquiring its class and throwing it in the
 * registerModules method, this is handled as such to allow turning these modules off by configuration,
 * and disable them if the parent mod is not loaded (Integration modules with other mods)
 *
 * Replace @Mod annotation with this system and it allows better handling in the end of it
 *
 * @since 23/02/14
 * @author tgame14
 */
public class ProxyHandler
{

    private static List<IMod> submodList = new LinkedList<IMod>();
    private static List<IIntegrationProxy> compatModulesList = new LinkedList<IIntegrationProxy>();
    private static LoadPhase phase = LoadPhase.PRELAUNCH;

    public static void applyModules (Class<?> clazz, boolean load)
    {
        if (clazz.isAssignableFrom(IMod.class))
        {
            try
            {
                if (clazz.isAssignableFrom(IIntegrationProxy.class))
                {
                    IIntegrationProxy proxy = (IIntegrationProxy) clazz.newInstance();
                    if (Loader.isModLoaded(proxy.modId()))
                        compatModulesList.add(proxy);
                }

                else
                {
                    submodList.add((IMod) clazz.newInstance());
                }
            }
            catch (Exception ex)
            {
                ICBMCore.LOGGER.severe("Exception thrown when registering sub modules");
                ex.printStackTrace();
            }
        }
    }

    /** Call for modules late or as already existing modules, DO NOT CALL FOR REGISTERED Proxies! */
    public static void applyModules (IIntegrationProxy module)
    {
        boolean registered = false;

        if (Loader.isModLoaded(module.modId()))
        {
            compatModulesList.add((IIntegrationProxy) module);
            registered = true;
        }

        if (registered)
        {
            switch (phase)
            {
            case DONE:
                break;
            case POSTINIT:
                module.preInit();
                module.init();
                module.preInit();
                break;
            case INIT:
                module.preInit();
                module.init();
                break;
            case PREINIT:
                module.preInit();
                break;
            default:
                break;
            }
        }
    }

    public static void preInit (FMLPreInitializationEvent event)
    {
        phase = LoadPhase.PREINIT;

        for (IMod submod : submodList)
        {
            submod.preInit(event);
        }

        for (IIntegrationProxy proxy : compatModulesList)
        {
            proxy.preInit();
        }

    }

    public static void init (FMLInitializationEvent event)
    {
        phase = LoadPhase.INIT;

        for (IMod submod : submodList)
        {
            submod.init(event);
        }

        for (IIntegrationProxy proxy : compatModulesList)
        {
            proxy.init();
        }
    }

    public static void postInit (FMLPostInitializationEvent event)
    {
        phase = LoadPhase.POSTINIT;

        for (IMod submod : submodList)
        {
            submod.postInit(event);
        }

        for (IIntegrationProxy proxy : compatModulesList)
        {
            proxy.postInit();
        }

        phase = LoadPhase.DONE;
    }

}
