package com.builtbroken.icbm.content.blast.nuke;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.entity.selector.EntityDistanceSelector;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/16/2017.
 */
public class BlastNuke extends BlastSimplePath<BlastNuke>
{
    public static float defaultSize = 10f;
    public static float percentAntimatterRange = .2f;

    public BlastNuke(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public BlockEdit changeBlock(BlockPos location)
    {
        if (location.isAirBlock(world))
        {
            return null;
        }
        return new BlockEdit(world, location).set(Blocks.air, 0, false, true).setNotificationLevel(2);
    }

    @Override
    public boolean shouldPath(BlockPos location)
    {
        if (super.shouldPath(location))
        {
            if (location.getHardness(world) < 0)
            {
                return false;
            }
            double distance = blockCenter.distance(location);
            double check = percentAntimatterRange * size;
            if (distance > check)
            {
                double scale = size / defaultSize;
                double energy = 100 * defaultSize * scale;
                return energy >= location.getResistance(explosionBlameEntity, x(), y(), z());
            }
            return true;
        }
        return false;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        super.doEffectOther(beforeBlocksPlaced);
        if (!beforeBlocksPlaced)
        {
            //TODO wright own version of getEntitiesWithinAABB that takes a filter and cuboid(or Vector3 to Vector3)
            //TODO ensure that the entity is in line of sight
            //TODO ensure that the entity can be pathed by the explosive
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - size - 1, y - size - 1, z - size - 1, x + size + 1, y + size + 1, z + size + 1);
            List list = world.selectEntitiesWithinAABB(Entity.class, bounds, new EntityDistanceSelector(new Pos(x, y, z), size + 1, true));
            if (list != null && !list.isEmpty())
            {
                damageEntities(list, new DamageSource("antimatter").setExplosion().setDamageBypassesArmor(), 10);
            }
        }
    }

    @Override
    public void displayEffectForEdit(IWorldEdit blocks)
    {

    }

    @Override
    public void playAudioForEdit(IWorldEdit blocks)
    {

    }

    @Override
    public void doStartDisplay()
    {
        //Engine.instance.packetHandler.sendToAllAround(new PacketBlast(this, PacketBlast.BlastPacketType.PRE_BLAST_DISPLAY), this, 400);
    }

    @Override
    public void doEndDisplay()
    {
        //Engine.instance.packetHandler.sendToAllAround(new PacketBlast(this, PacketBlast.BlastPacketType.POST_BLAST_DISPLAY), this, 400);
    }
}
