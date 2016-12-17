package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.icbm.content.fragments.EntityFragment;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.callback.PacketBlast;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.explosive.blast.BlastBasic;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/17/2016.
 */
public class BlastFragments extends BlastBasic<BlastFragments>
{
    public FragBlastType blastType;

    public BlastFragments(IExplosiveHandler handler, FragBlastType type)
    {
        super(handler);
        this.blastType = type;
        eUnitPerBlock = 2F;
    }

    @Override
    public void doEffectOther(boolean beforeBlocksPlaced)
    {
        super.doEffectOther(beforeBlocksPlaced);
        if (!beforeBlocksPlaced)
        {
            final Pos center = new Pos(x, y, z);
            int rotations = (int) Math.min(256, 3 + size); //Default of 1 = 5 rotations or 25 fragments
            double degrees = 360 / rotations;
            for (int yaw = 0; yaw < rotations; yaw++)
            {
                for (int pitch = 0; pitch < rotations; pitch++)
                {
                    //Reduces number of fragments and provides a small random in distributions
                    if (world.rand.nextBoolean())
                    {
                        EulerAngle rotation = new EulerAngle(yaw * degrees + (world.rand.nextFloat() * 2), pitch * degrees + (world.rand.nextFloat() * 2));
                        Pos velocity = rotation.toPos().multiply(2 + world.rand.nextFloat());
                        Pos pos = center.add(rotation.toPos()).addRandom(world.rand, 0.2);
                        if (pos.isAirBlock(world())) //TODO add proper collision check
                        {
                            EntityFragment fragment = new EntityFragment(world, blastType.fragmentType, blastType.blockMaterial);
                            fragment.setPosition(pos.x(), pos.y(), pos.z());

                            //Motion
                            fragment.motionX = velocity.x();
                            fragment.motionY = velocity.y();
                            fragment.motionZ = velocity.z();

                            world.spawnEntityInWorld(fragment);
                        }
                    }
                }
            }
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
        Engine.instance.packetHandler.sendToAllAround(new PacketBlast(this, PacketBlast.BlastPacketType.POST_BLAST_DISPLAY), this, 400);
    }
}

