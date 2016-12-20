package com.builtbroken.icbm.content.blast.power;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.callback.PacketBlast;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

/**
 * Generates a short term microwave effect that drains water blocks.
 * <p>
 * damages entities as a side effect
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/20/2016.
 */
public class BlastMicrowave extends BlastSimplePath<BlastMicrowave>
{
    public BlastMicrowave(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public IWorldEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        //TODO set fire to wood near water
        //TODO cause steam damage
        //TODO send shocks out from metal
        //TODO make energy based (Blocks destroyed consumes energy, Metal consumes a lot of energy, Travel distance consumes energy)
        //TODO destroy plants
        if (block == Blocks.water || block == Blocks.flowing_water)
        {
            return new BlockEdit(location, Blocks.air, 0);
        }
        return null;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        if (!beforeBlocksPlaced)
        {
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x - size, y - size, z - size, z + size, y + size, z + size);
            List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds);
            for (EntityLivingBase entity : list)
            {
                //TODO if wearing armor apply heat damage
                //TODO apply heat damage directly
                //TODO scale damage by distance
                //Set fire to entry
                entity.setFire(10); //TODO only set on fire if close
            }
        }
    }

    @Override
    public int shouldThreadAction()
    {
        return -2;
    }

    @Override
    public void doStartAudio()
    {
        if (!world.isRemote)
        {
            //TODO get custom audio
            //world.playSoundEffect(x, y, z, "random.explode", 4.0F, (float) ((1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * size));
        }
    }

    @Override
    public void doStartDisplay()
    {
        Engine.instance.packetHandler.sendToAllAround(new PacketBlast(this, PacketBlast.BlastPacketType.PRE_BLAST_DISPLAY), this, 400);
    }

    @Override
    public void doEndDisplay()
    {
        //No need so cancel out default
    }

    @Override
    public void displayEffectForEdit(IWorldEdit edit)
    {
        if (edit instanceof BlockEdit)
        {
            //Send packet to spawn effects client side
            Engine.instance.packetHandler.sendToAllAround(new PacketBlast(this, (BlockEdit) edit), edit, 20);
            //Activate audio, sent to client by MC
            world.playSoundEffect(edit.z() + 0.5, edit.y() + 0.5, edit.z() + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
        }
    }

}
