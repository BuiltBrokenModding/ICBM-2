package icbm.explosion.blast;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import resonant.lib.transform.region.Cuboid;
import resonant.lib.transform.vector.Vector3;

public class BlastRepulsive extends Blast
{
    private int checkBanJing = 16;
    protected float nengLiang = 10F;

    private List<Vector3> blownBlocks = new ArrayList<Vector3>();

    /** 0- No push, 1 - Attract, 2 - Repel */
    private int pushType = 0;
    private boolean destroyItem = false;

    public BlastRepulsive(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastRepulsive setPushType(int type)
    {
        this.pushType = type;
        return this;
    }

    public BlastRepulsive setDestroyItems()
    {
        this.destroyItem = true;
        return this;
    }

    @Override
    public void doExplode()
    {
        if (!this.world().isRemote)
        {
            for (int x = 0; x < this.checkBanJing; ++x)
            {
                for (int y = 0; y < this.checkBanJing; ++y)
                {
                    for (int z = 0; z < this.checkBanJing; ++z)
                    {
                        if (x == 0 || x == this.checkBanJing - 1 || y == 0 || y == this.checkBanJing - 1 || z == 0 || z == this.checkBanJing - 1)
                        {
                            double xStep = x / (this.checkBanJing - 1.0F) * 2.0F - 1.0F;
                            double yStep = y / (this.checkBanJing - 1.0F) * 2.0F - 1.0F;
                            double zStep = z / (this.checkBanJing - 1.0F) * 2.0F - 1.0F;
                            double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
                            xStep /= diagonalDistance;
                            yStep /= diagonalDistance;
                            zStep /= diagonalDistance;
                            float var14 = this.getRadius() * (0.7F + this.world().rand.nextFloat() * 0.6F);
                            double var15 = this.position.x();
                            double var17 = this.position.y();
                            double var19 = this.position.z();

                            for (float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F)
                            {
                                int var22 = MathHelper.floor_double(var15);
                                int var23 = MathHelper.floor_double(var17);
                                int var24 = MathHelper.floor_double(var19);
                                Block var25 = this.world().getBlock(var22, var23, var24);

                                if (var25 != null)
                                {
                                    var14 -= (var25.getExplosionResistance(this.exploder, this.world(), var22, var23, var24, this.position.xi(), this.position.yi(), this.position.zi()) + 0.3F) * var21;
                                }

                                if (var14 > 0.0F)
                                {
                                    blownBlocks.add(new Vector3(var22, var23, var24));
                                }

                                var15 += xStep * var21;
                                var17 += yStep * var21;
                                var19 += zStep * var21;
                            }
                        }
                    }
                }
            }

        }

        this.world().playSoundEffect(this.position.x(), this.position.y(), this.position.z(), "random.explode", 4.0F, (1.0F + (this.world().rand.nextFloat() - this.world().rand.nextFloat()) * 0.2F) * 0.7F);

        switch (this.pushType)
        {
            case 0:
                this.doDamageEntities(this.getRadius(), nengLiang, this.destroyItem);
                break;
            default:
                this.pushEntities(12, this.getRadius() * 4, this.pushType);
                break;
        }

        if (!this.world().isRemote)
        {
            int var3;
            Vector3 blownPosition;
            int var5;
            int var6;
            int var7;
            Block blockID;
            int metadata;

            for (var3 = blownBlocks.size() - 1; var3 >= 0; --var3)
            {
                blownPosition = blownBlocks.get(var3);
                var5 = blownPosition.xi();
                var6 = blownPosition.yi();
                var7 = blownPosition.zi();
                blockID = this.world().getBlock(var5, var6, var7);
                metadata = this.world().getBlockMetadata(var5, var6, var7);

                double var9 = (var5 + this.world().rand.nextFloat());
                double var11 = (var6 + this.world().rand.nextFloat());
                double var13 = (var7 + this.world().rand.nextFloat());
                double var151 = var9 - this.position.y();
                double var171 = var11 - this.position.y();
                double var191 = var13 - this.position.z();
                double var211 = MathHelper.sqrt_double(var151 * var151 + var171 * var171 + var191 * var191);
                var151 /= var211;
                var171 /= var211;
                var191 /= var211;
                double var23 = 0.5D / (var211 / this.getRadius() + 0.1D);
                var23 *= (this.world().rand.nextFloat() * this.world().rand.nextFloat() + 0.3F);
                var151 *= var23;
                var171 *= var23;
                var191 *= var23;
                this.world().spawnParticle("explode", (var9 + this.position.x() * 1.0D) / 2.0D, (var11 + this.position.y() * 1.0D) / 2.0D, (var13 + this.position.z() * 1.0D) / 2.0D, var151, var171, var191);
                this.world().spawnParticle("smoke", var9, var11, var13, var151, var171, var191);

                if (blockID != null)
                {
                    try
                    {
                        Block block = blockID;

                        if (block.canDropFromExplosion(null))
                        {
                            block.dropBlockAsItemWithChance(this.world(), var5, var6, var7, this.world().getBlockMetadata(var5, var6, var7), 1F, 0);
                        }

                        block.onBlockExploded(this.world(), var5, var6, var7, this);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void pushEntities(float radius, float force, int type)
    {
        // Step 2: Damage all entities
        Vector3 minCoord = position.clone();
        minCoord.add(-radius - 1);
        Vector3 maxCoord = position.clone();
        maxCoord.add(radius + 1);

        Cuboid region = new Cuboid(minCoord, maxCoord);
        List<Entity> entities = region.getEntities(this.world(), Entity.class);

        for (Entity entity : entities)
        {
            double var13 = entity.getDistance(position.x(), position.y(), position.z()) / radius;

            if (var13 <= 1.0D)
            {
                double xDifference = entity.posX - position.x();
                double yDifference = entity.posY - position.y();
                double zDifference = entity.posZ - position.z();
                double distance = MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                xDifference /= distance;
                yDifference /= distance;
                zDifference /= distance;

                if (type == 1)
                {
                    double modifier = var13 * force * (entity instanceof EntityPlayer ? 0.5 : 1);
                    entity.addVelocity(-xDifference * modifier, -yDifference * modifier, -zDifference * modifier);
                }
                else if (type == 2)
                {
                    double modifier = (1.0D - var13) * force * (entity instanceof EntityPlayer ? 0.5 : 1);
                    entity.addVelocity(xDifference * modifier, yDifference * modifier, zDifference * modifier);
                }
            }
        }
    }

    @Override
    public long getEnergy()
    {
        return 418000;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.pushType = nbt.getInteger("pushType");
        this.destroyItem = nbt.getBoolean("destroyItem");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("pushType", this.pushType);
        nbt.setBoolean("destroyItem", this.destroyItem);
    }
}
