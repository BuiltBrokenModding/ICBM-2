package com.builtbroken.icbm.content.blast.power;

import com.builtbroken.mc.api.edit.IWorldEdit;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.callback.PacketBlast;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.mc.prefab.entity.damage.DamageElectrical;
import com.builtbroken.mc.prefab.entity.damage.DamageMicrowave;
import com.builtbroken.mc.prefab.explosive.blast.BlastSimplePath;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
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
    protected List<Pos> metalBlocks = new ArrayList();

    public BlastMicrowave(IExplosiveHandler handler)
    {
        super(handler);
    }

    @Override
    public IWorldEdit changeBlock(Location location)
    {
        Block block = location.getBlock();
        if (block.blockHardness > -1)
        {
            //TODO cause steam damage
            //TODO send shocks out from metal
            //TODO make energy based (Blocks destroyed consumes energy, Metal consumes a lot of energy, Travel distance consumes energy)
            //TODO induce power and interference into wires
            //TODO damage low grade wires
            if (block == Blocks.water || block == Blocks.flowing_water)
            {
                return new BlockEdit(location, Blocks.air, 0);
            }
            else if (block == Blocks.cactus)
            {
                return new BlockEdit(location, Blocks.fire, 0);
            }
            else if (block.blockMaterial == Material.wood)
            {
                if (world.rand.nextFloat() < 0.05)
                {
                    return new BlockEdit(location, Blocks.fire, 0);
                }
            }
            else if (block.blockMaterial == Material.plants || block.blockMaterial == Material.leaves)
            {
                if (world.rand.nextFloat() < 0.25 && block == Blocks.sapling)
                {
                    return new BlockEdit(location, Blocks.deadbush, 0);
                }
                else if (world.rand.nextFloat() < 0.10)
                {
                    return new BlockEdit(location, Blocks.fire, 0);
                }
            }
            else if (block.blockMaterial == Material.anvil || block.blockMaterial == Material.iron)
            {
                metalBlocks.add(location.toPos());
            }
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
                double distanceToCenter = new Pos(entity).distance(x, y, z);
                if (distanceToCenter < (size / 2))
                {
                    float damage = (float) (size / 10); //Size 25 should do 2.5 damage at point blank range
                    damage = (float) (damage - damage * (distanceToCenter / (size / 2)));

                    int armorCount = InventoryUtility.getWornMetalCount(entity);
                    if (armorCount > 0)
                    {
                        //+ 25% per armor worn that is metal
                        damage += damage * armorCount * 0.25;
                    }
                    world.playSoundEffect(x, y, z, "icbm:icbm.fry", 0.2F + world.rand.nextFloat() * 0.2F, 0.9F + world.rand.nextFloat() * 0.15F);
                    entity.attackEntityFrom(new DamageMicrowave(cause instanceof TriggerCause.TriggerCauseEntity ? ((TriggerCause.TriggerCauseEntity) cause).source : this, new Location(entity)), damage);
                }
                //Set fire to entry
                entity.setFire((int) (10 - 5 * (distanceToCenter / size)));
            }

            for (Pos pos : metalBlocks)
            {
                double distanceToCenter = pos.distance(x, y, z);
                float damage = (float) (6 - 5 * (distanceToCenter / size));
                for (EntityLivingBase entity : list)
                {
                    if (pos.distance(entity) < 4 && world.rand.nextBoolean())
                    {
                        world.playSoundEffect(pos.x(), pos.y(), pos.z(), "icbm:icbm.emp", 0.2F + world.rand.nextFloat() * 0.2F, 0.9F + world.rand.nextFloat() * 0.15F);
                        entity.attackEntityFrom(new DamageElectrical(cause instanceof TriggerCause.TriggerCauseEntity ? ((TriggerCause.TriggerCauseEntity) cause).source : this, new Location(world, pos)), damage);
                    }
                }
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
            world.playSoundEffect(x, y, z, "icbm:icbm.buzz", 0.2F + world.rand.nextFloat() * 0.2F, 0.9F + world.rand.nextFloat() * 0.15F);
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
