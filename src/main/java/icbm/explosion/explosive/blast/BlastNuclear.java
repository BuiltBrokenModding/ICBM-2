package icbm.explosion.explosive.blast;

import icbm.Reference;
import icbm.Settings;
import icbm.core.ICBMCore;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.thread.ThreadLargeExplosion;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import resonant.lib.tranform.Vector3;

public class BlastNuclear extends Blast
{
    public static boolean POLLUTIVE_NUCLEAR = true;

    static
    {
        Settings.CONFIGURATION.load();
        POLLUTIVE_NUCLEAR = Settings.CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Pollutive Nuclear", POLLUTIVE_NUCLEAR).getBoolean(POLLUTIVE_NUCLEAR);
        Settings.CONFIGURATION.save();
    }

    private ThreadLargeExplosion thread;
    private float energy;
    private boolean spawnMoreParticles = false;
    private boolean isRadioactive = false;

    public BlastNuclear(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastNuclear(World world, Entity entity, double x, double y, double z, float size, float energy)
    {
        this(world, entity, x, y, z, size);
        this.energy = energy;
    }

    public BlastNuclear setNuclear()
    {
        this.spawnMoreParticles = true;
        this.isRadioactive = true;
        return this;
    }

    @Override
    public void doPreExplode()
    {
        if (!this.world().isRemote)
        {
            this.thread = new ThreadLargeExplosion(this.position, (int) this.getRadius(), this.energy, this.exploder);

            this.thread.start();

        }
        else if (this.spawnMoreParticles && ICBMExplosion.proxy.isGaoQing())
        {
            // Spawn nuclear cloud.
            for (int y = 0; y < 26; y++)
            {
                int r = 4;

                if (y < 8)
                {
                    r = Math.max(Math.min((8 - y) * 2, 10), 4);
                }
                else if (y > 15)
                {
                    r = Math.max(Math.min((y - 15) * 2, 15), 5);
                }

                for (int x = -r; x < r; x++)
                {
                    for (int z = -r; z < r; z++)
                    {
                        double distance = MathHelper.sqrt_double(x * x + z * z);

                        if (r > distance && r - 3 < distance)
                        {
                            Vector3 spawnPosition = Vector3.translate(position, new Vector3(x * 2, (y - 2) * 2, z * 2));
                            float xDiff = (float) (spawnPosition.x - position.x);
                            float zDiff = (float) (spawnPosition.z - position.z);
                            ICBMExplosion.proxy.spawnParticle("smoke", world(), spawnPosition, xDiff * 0.3 * world().rand.nextFloat(), -world().rand.nextFloat(), zDiff * 0.3 * world().rand.nextFloat(), (float) (distance / this.getRadius()) * world().rand.nextFloat(), 0, 0, 8F, 1.2F);
                        }
                    }
                }
            }
        }

        this.doDamageEntities(this.getRadius(), this.energy * 1000);

        this.world().playSoundEffect(this.position.x, this.position.y, this.position.z, Reference.PREFIX + "explosion", 7.0F, (1.0F + (this.world().rand.nextFloat() - this.world().rand.nextFloat()) * 0.2F) * 0.7F);
    }

    @Override
    public void doExplode()
    {
        int r = this.callCount;

        if (this.world().isRemote)
        {
            if (ICBMExplosion.proxy.isGaoQing())
            {
                for (int x = -r; x < r; x++)
                {
                    for (int z = -r; z < r; z++)
                    {
                        double distance = MathHelper.sqrt_double(x * x + z * z);

                        if (distance < r && distance > r - 1)
                        {
                            Vector3 targetPosition = Vector3.translate(this.position, new Vector3(x, 0, z));

                            if (this.world().rand.nextFloat() < Math.max(0.001 * r, 0.05))
                            {
                                ICBMExplosion.proxy.spawnParticle("smoke", this.world(), targetPosition, 5F, 1F);
                            }
                        }
                    }
                }
            }

        }
        else
        {
            if (this.thread != null)
            {
                if (this.thread.isComplete)
                {
                    this.controller.endExplosion();
                }
            }
            else
            {
                this.controller.endExplosion();
                ICBMCore.LOGGER.severe("Something went wrong with multi-threading while detonating the nuclear explosive.");
            }
        }
    }

    @Override
    public void doPostExplode()
    {
        try
        {
            if (!this.world().isRemote && this.thread.isComplete)
            {
                for (Vector3 p : this.thread.results)
                {
                    Block block = Block.blocksList[this.world().getBlockId(p.intX(), p.intY(), p.intZ())];
                    if (block != null)
                        block.onBlockExploded(this.world(), p.intX(), p.intY(), p.intZ(), this);

                }
            }
        }
        catch (Exception e)
        {
            ICBMCore.LOGGER.severe("Nuclear-type detonation Failed!");
            e.printStackTrace();
        }

        this.doDamageEntities(this.getRadius(), this.energy * 1000);

        if (this.isRadioactive)
        {
            new BlastRot(world(), this.exploder, position.x, position.y, position.z, this.getRadius(), this.energy).explode();
            new BlastMutation(world(), this.exploder, position.x, position.y, position.z, this.getRadius()).explode();

            if (this.world().rand.nextInt(3) == 0)
            {
                world().toggleRain();
            }
        }

        this.world().playSoundEffect(this.position.x, this.position.y, this.position.z, Reference.PREFIX + "explosion", 10.0F, (1.0F + (this.world().rand.nextFloat() - this.world().rand.nextFloat()) * 0.2F) * 0.7F);
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @param return - Return -1 if this explosive does not need procedural calls */
    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public long getEnergy()
    {
        return (long) (41840000 * this.energy);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.spawnMoreParticles = nbt.getBoolean("spawnMoreParticles");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("spawnMoreParticles", this.spawnMoreParticles);

    }
}
