package com.builtbroken.icbm.content.launcher.door.listeners;

import com.builtbroken.icbm.content.launcher.door.TileSiloDoor;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.framework.block.imp.IBlockListener;
import com.builtbroken.mc.framework.block.imp.IRenderBoundsListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListenerBuilder;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.seven.framework.block.listeners.TileListener;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/5/2018.
 */
public class DoorBoundListener extends TileListener implements IBlockListener, IRenderBoundsListener
{
    Map<Integer, Map<Integer, Cube>> sizeToCube = new HashMap();

    @Override
    public Cube getRenderBounds()
    {
        ITileNode node = getNode();
        if (node instanceof TileSiloDoor)
        {
            return get(((TileSiloDoor) node).getDoorSize(), 2);
        }
        return null;
    }

    public Cube get(int size, int height)
    {
        if (!sizeToCube.containsKey(size))
        {
            sizeToCube.put(size, new HashMap());
        }

        Map<Integer, Cube> map = sizeToCube.get(size);
        if (!map.containsKey(height))
        {
            float h = (height + 1) / 2f;
            float s = size + 1;
            map.put(size, new Cube(-s, -h, -s, s, h, s));
        }
        return map.get(height);
    }

    @Override
    public List<String> getListenerKeys()
    {
        List<String> list = new ArrayList();
        list.add("blockStack");
        return list;
    }

    public static class Builder implements ITileEventListenerBuilder
    {
        @Override
        public ITileEventListener createListener(Block block)
        {
            return new DoorBoundListener();
        }

        @Override
        public String getListenerKey()
        {
            return "icbm:SiloDoorBoundListener";
        }
    }
}
