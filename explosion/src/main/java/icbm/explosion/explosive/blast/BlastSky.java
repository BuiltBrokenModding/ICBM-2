package icbm.explosion.explosive.blast;

import icbm.Reference;
import icbm.explosion.potion.PDongShang;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.potion.CustomPotionEffect;

public class BlastSky extends BlastBeam
{
    public BlastSky(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        this.red = 0f;
        this.green = 0.3f;
        this.blue = 0.7f;
    }

    @Override
    public void doExplode()
    {
        super.doExplode();
        this.worldObj.playSoundEffect(position.x, position.y, position.z, Reference.PREFIX + "redmatter", 4.0F, 0.8F);
    }

    @Override
    public void doPostExplode()
    {
        super.doPostExplode();

        if (!this.worldObj.isRemote)
        {
            if (this.canFocusBeam(this.worldObj, position) && this.thread.isComplete)
            {
                /*
                 * Freeze all nearby entities.
                 */
                List<EntityLiving> livingEntities = worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(position.x - getRadius(), position.y - getRadius(), position.z - getRadius(), position.x + getRadius(), position.y + getRadius(), position.z + getRadius()));

                Iterator<EntityLiving> it = livingEntities.iterator();

                while (it.hasNext())
                {
                    EntityLiving entity = it.next();
                    entity.addPotionEffect(new CustomPotionEffect(PDongShang.INSTANCE.getId(), 60 * 20, 1, null));
                    entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 10 * 20, 2));
                    entity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 120 * 20, 2));
                    entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 120 * 20, 4));
                }

                for (Vector3 targetPosition : this.thread.results)
                {
                    double distance = Vector3.distance(targetPosition, position);

                    double distanceFromCenter = position.distance(targetPosition);

                    if (distanceFromCenter > this.getRadius())
                        continue;

                    /*
                     * Reduce the chance of setting blocks on fire based on distance from center.
                     */
                    double chance = this.getRadius() - (Math.random() * distanceFromCenter);

                    if (chance > distanceFromCenter * 0.55)
                    {
                        /*
                         * Place down ice blocks.
                         */
                        int blockID = this.worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

                        if (blockID == Block.fire.blockID || blockID == Block.lavaMoving.blockID || blockID == Block.lavaStill.blockID)
                        {
                            this.worldObj.setBlock(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), Block.snow.blockID, 0, 2);
                        }
                        else if (blockID == 0 && this.worldObj.getBlockId(targetPosition.intX(), targetPosition.intY() - 1, targetPosition.intZ()) != Block.ice.blockID && worldObj.getBlockId(targetPosition.intX(), targetPosition.intY() - 1, targetPosition.intZ()) != 0)
                        {
                            this.worldObj.setBlock(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), Block.ice.blockID, 0, 2);
                        }
                    }
                }

                this.worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, Reference.PREFIX + "redmatter", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);
            }

            this.worldObj.setWorldTime(1200);
        }
    }

    @Override
    public boolean canFocusBeam(World worldObj, Vector3 position)
    {
        long worldTime = worldObj.getWorldTime();

        while (worldTime > 23999)
        {
            worldTime -= 23999;
        }

        return worldTime > 12000 && super.canFocusBeam(worldObj, position);
    }

}
