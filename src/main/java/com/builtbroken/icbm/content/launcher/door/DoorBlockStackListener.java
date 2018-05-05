package com.builtbroken.icbm.content.launcher.door;

import com.builtbroken.icbm.content.launcher.door.json.DoorData;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.framework.block.imp.*;
import com.builtbroken.mc.seven.framework.block.listeners.TileListener;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles anything related to ItemStack for the door
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/5/2018.
 */
public class DoorBlockStackListener extends TileListener implements IBlockStackListener, IPlacementListener, IBlockListener
{
    public static final String NBT_DOOR_ID = "door_id";

    public final Block block;

    public DoorBlockStackListener(Block block)
    {
        this.block = block;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
    {
        if (creativeTabs == block.getCreativeTabToDisplayOn())
        {
            //Clear invalid entries, by default 1 invalid entry is added
            Iterator it = list.iterator();
            while (it.hasNext())
            {
                Object object = it.next();
                if (!(object instanceof ItemStack) || ((ItemStack) object).stackTagCompound == null)
                {
                    it.remove();
                }
            }

            //Add doors
            for (DoorData doorData : DoorData.doorMap.values())
            {
                ItemStack stack = new ItemStack(item);
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString(NBT_DOOR_ID, doorData.getContentID());
                list.add(stack);
            }
        }
    }

    @Override
    public void onPlacedBy(EntityLivingBase entityLivingBase, ItemStack stack)
    {
        //Set door ID
        if (stack.getTagCompound().hasKey(NBT_DOOR_ID))
        {
            ITileNode node = getNode();
            if (node instanceof TileSiloDoor)
            {
                ((TileSiloDoor) node).doorID = stack.getTagCompound().getString(NBT_DOOR_ID);
            }
        }
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
            return new DoorBlockStackListener(block);
        }

        @Override
        public String getListenerKey()
        {
            return "icbm:SiloDoorBlockStackListener";
        }
    }
}
