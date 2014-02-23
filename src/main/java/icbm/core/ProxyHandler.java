package icbm.core;

import calclavia.lib.modproxy.IMod;
import calclavia.lib.modproxy.IProxy;
import calclavia.lib.modproxy.LoadPhase;

import java.util.LinkedList;
import java.util.List;

/**
 * the Class that handles the submods of All ICBM
 *
 * @since 23/02/14
 * @author tgame14
 */
public class ProxyHandler
{

    private static List<IMod> submodList = new LinkedList<IMod>();
    private static List<IProxy> compatModulesList = new LinkedList<IProxy>();
    private static LoadPhase phase = LoadPhase.PRELAUNCH;

    private void applyModules(Class<?> clazz, String modId) throws IllegalAccessException, InstantiationException
    {
        if (clazz.isAssignableFrom(IMod.class))
        {
            if (clazz.isAssignableFrom(IProxy.class))
            {
                compatModulesList.add((IProxy) clazz.newInstance());
            }

            else
            {
                submodList.add((IMod) clazz.newInstance());
            }
        }
    }



    public static void registerModules()
    {

    }
}
