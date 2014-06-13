package icbm.sentry.turret;

import icbm.sentry.interfaces.IMountedTurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.mounted.TurretMounted;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import resonant.lib.utility.LanguageUtility;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Entity that fakes being the sentry gun so that the player can ride the sentry. This entity also
 * handles most damage interaction with other entities.
 * 
 * @Author DarkGuardsman */
public class EntityMountableDummy extends EntityLiving
{
    private TurretMounted turret;

    public EntityMountableDummy(World world)
    {
        super(world);
    }

    public EntityMountableDummy(TurretMounted controller)
    {
        this(controller.world());
        this.isImmuneToFire = true;
        this.setPosition(controller.x() + 0.5, controller.y() + 0.5, controller.z() + 0.5);
        this.turret = controller;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return true;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect)
    {
        return false;
    }

    @Override
    public String getEntityName()
    {
        return LanguageUtility.getLocal("entity.turretseat.name");
    }

    /** Called to update the entity's position/logic. */
    @Override
    public void onUpdate()
    {
        if (this.ridingEntity != null && this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }

        if (this.turret == null)
        {
            TileEntity tile = worldObj.getBlockTileEntity((int) posX, (int) posY, (int) posZ);
            if (tile instanceof TileTurret && ((TileTurret) tile).getTurret() instanceof IMountedTurret)
            {
                this.turret = (TurretMounted) ((TileTurret) tile).getTurret();
            }
            else
            {
                setDead();
                return;
            }
        }

        if (this.turret.getFakeEntity() != null)
        {
            if (this.turret.getFakeEntity() != this)
            {
                setDead();
                return;
            }
        }
        else
        {
            this.turret.setFakeEntity(this);
        }
        // TODO adjust for center of sentry
        setPosition(this.turret.x() + 0.5, this.turret.y() + 0.5, this.turret.z() + 0.5);
    }

    @Override
    public double getMountedYOffset()
    {
        if (this.turret instanceof IMountedTurret)
            return ((TurretMounted) this.turret).getRiderOffset().y;
        else
            return -0.5;
    }

    @Override
    public void updateRiderPosition()
    {
        if (riddenByEntity != null && turret != null)
        {
            Vector3 setPosition = turret.fromCenter().clone();
            Vector3 offset = new Vector3(0, getMountedYOffset() + riddenByEntity.getYOffset(), -0.5);
            offset.rotate(-turret.getServo().yaw + 180);
            setPosition.add(offset);
            riddenByEntity.setPosition(setPosition.x, setPosition.y, setPosition.z);
        }

    }

    @Override
    public boolean shouldRiderSit()
    {
        return true;
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return AxisAlignedBB.getBoundingBox(this.posX - .6, this.posY - .6, this.posZ - .6, this.posX + .6, this.posY + .6, this.posZ + .6);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderVec3D(Vec3 par1Vec3)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double par1)
    {
        return false;
    }

    @Override
    public void setVelocity(double par1, double par3, double par5)
    {

    }

    @Override
    public boolean isInsideOfMaterial(Material par1Material)
    {
        return false;
    }

    @Override
    public boolean interact(EntityPlayer player)
    {
        if (this.turret != null && player != null)
        {
            Block block = Block.blocksList[this.worldObj.getBlockId((int) this.turret.x(), (int) this.turret.y(), (int) this.turret.z())];

            if (block != null)
            {
                return block.onBlockActivated(this.turret.world(), (int) this.turret.x(), (int) this.turret.y(), (int) this.turret.z(), player, 0, 0, 0, 0);
            }
        }
        return false;
    }

}