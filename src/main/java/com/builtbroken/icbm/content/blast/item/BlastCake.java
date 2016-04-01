package com.builtbroken.icbm.content.blast.item;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.Blast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

import java.util.List;

/**
 * Blast that spawns a cake
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/1/2016.
 */
public class BlastCake extends Blast<BlastCake>
{
    @Override
    public void getEffectedBlocks(final List<IWorldEdit> list)
    {
        Pos pos = new Pos(x, y, z);
        if (pos.isAirBlock(world))
        {
            list.add(new BlockEdit(world, x, y, z).set(ICBM.APRIL_FIRST ? ICBM.blockCake : Blocks.cake));
        }
        else if (pos.add(0, 1, 0).isAirBlock(world))
        {
            list.add(new BlockEdit(world, x, y + 1, z).set(ICBM.APRIL_FIRST ? ICBM.blockCake : Blocks.cake));
        }
    }

    @Override
    public int shouldThreadAction()
    {
        return -1;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (ICBM.APRIL_FIRST)
        {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(x - 100, y - 100, z - 100, z + 100, y + 100, z + 100);
            List<Entity> entities = world.getEntitiesWithinAABB(EntityPlayer.class, bb);
            for (Entity entity : entities)
            {
                if (entity instanceof EntityPlayer)
                {
                    ((EntityPlayerMP) entity).addChatComponentMessage(new ChatComponentText("The cake is a lie, or is it..."));
                }
            }
        }
    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {
        //used only to override default action
    }

    @Override
    public void doStartAudio()
    {
        //used only to override default action
    }

    @Override
    public void doEndAudio()
    {
        //TODO play birthday music
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {
        //used only to override default action
    }

    @Override
    public void doStartDisplay()
    {
        //used only to override default action
    }

    @Override
    public void doEndDisplay()
    {
        //used only to override default action
    }
}
