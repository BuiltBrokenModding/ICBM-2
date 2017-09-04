package com.builtbroken.icbm.content.fragments;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.abstraction.world.IWorld;
import com.builtbroken.mc.api.data.EnumProjectileTypes;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.prefab.entity.EntityProjectile;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidBlock;

/**
 * Entity that represents a fragment inside of the game world
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public class EntityFragment extends EntityProjectile implements IEntityAdditionalSpawnData
{
    /** Velocity to break glass */
    public static final double GLASS_BREAK_VELOCITY = .1;

    /** Type of fragment, controls logic and rendering. */
    protected FragmentType fragmentType;
    /** Block material, only used for some fragment types. */
    public Item fragmentMaterial;
    /** Shape of the fragment, only used in rendering and only on some fragment types. */
    public Cube renderShape;

    public EntityFragment(World world)
    {
        super(world);
        setSize(.1f, .1f);
        renderShape = new Cube(0, 0, 0, 0.05 + MathUtility.rand.nextFloat() * 0.3, 0.05 + MathUtility.rand.nextFloat() * 0.3, 0.05 + MathUtility.rand.nextFloat() * 0.3);
        //TODO adjust bounds to render shape
    }

    public EntityFragment(World world, FragmentType fragmentType)
    {
        this(world);
        this.fragmentType = fragmentType;
    }

    public EntityFragment(World world, FragmentType fragmentType, Item block)
    {
        this(world);
        this.fragmentType = fragmentType;
        this.fragmentMaterial = block;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (worldObj.isRemote)
        {
            //Spawn fire effects
            if (isOnFire())
            {
                final Pos orginalVel = new Pos(motionX, motionY, motionZ).normalize().multiply(-.1);
                Pos vel;

                IWorld world = Engine.minecraft.getWorld(worldObj.provider.dimensionId);
                vel = orginalVel.addRandom(worldObj.rand, .1);
                world.spawnParticle("fire", posX, posY, posZ, vel.xf(), vel.yf(), vel.zf());

                vel = orginalVel.addRandom(worldObj.rand, .1);
                world.spawnParticle("fire", posX, posY, posZ, vel.xf(), vel.yf(), vel.zf());

                vel = orginalVel.addRandom(worldObj.rand, .1);
                world.spawnParticle("smoke", posX, posY, posZ, vel.xf(), vel.yf(), vel.zf());

                vel = orginalVel.addRandom(worldObj.rand, .1);
                world.spawnParticle("smoke", posX, posY, posZ, vel.xf(), vel.yf(), vel.zf());
            }
        }
    }

    public boolean isOnFire()
    {
        return fragmentType != null && fragmentType == FragmentType.BLAZE || isBurning();
    }

    @Override
    protected void handleBlockCollision(MovingObjectPosition movingobjectposition)
    {
        this.xTile = movingobjectposition.blockX;
        this.yTile = movingobjectposition.blockY;
        this.zTile = movingobjectposition.blockZ;
        this.sideTile = movingobjectposition.sideHit;

        this.inBlockID = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
        this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

        if (inBlockID.blockHardness > -1 && inBlockID.blockHardness < 100)
        {
            //TODO replace cloth with damaged cloth
            if (inBlockID.blockMaterial == Material.glass || inBlockID.blockMaterial == Material.cloth || inBlockID.blockMaterial == Material.leaves)
            {
                if (getSpeed() > GLASS_BREAK_VELOCITY)
                {
                    worldObj.setBlockToAir(xTile, yTile, zTile);
                    this.motionX -= motionX * (inBlockID.blockHardness / 100);
                    this.motionY -= motionY * (inBlockID.blockHardness / 100);
                    this.motionZ -= motionZ * (inBlockID.blockHardness / 100);
                }
                else
                {
                    setDead();
                }
                return;
            }
            else if (inBlockID == Blocks.tnt && fragmentType == FragmentType.BLAZE)
            {
                worldObj.setBlockToAir(xTile, yTile, zTile);
                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(worldObj, (double) ((float) xTile + 0.5F), (double) ((float) yTile + 0.5F), (double) ((float) zTile + 0.5F), shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) shootingEntity : null);
                entitytntprimed.fuse = worldObj.rand.nextInt(20) + 3;
                worldObj.spawnEntityInWorld(entitytntprimed);
                setDead();
                return;
            }
        }

        this.motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - this.posX));
        this.motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - this.posY));
        this.motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - this.posZ));

        float velocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.posX -= this.motionX / (double) velocity * 0.05000000074505806D;
        this.posY -= this.motionY / (double) velocity * 0.05000000074505806D;
        this.posZ -= this.motionZ / (double) velocity * 0.05000000074505806D;

        //Fire impact of ground, set ground on fire if fragment is on fire
        if (!inGround && isOnFire() && worldObj.rand.nextBoolean())
        {
            final Pos firePos = new Pos(movingobjectposition).add(ForgeDirection.getOrientation(movingobjectposition.sideHit));
            if (firePos.isReplaceable(worldObj) && !(inBlockID == Blocks.water || inBlockID == Blocks.flowing_water || inBlockID instanceof IFluidBlock || inBlockID instanceof BlockLiquid))
            {
                firePos.setBlock(worldObj, Blocks.fire);
                worldObj.markBlockForUpdate(firePos.xi(), firePos.yi(), firePos.zi());
            }
            if (fragmentType == FragmentType.BLAZE)
            {
                setDead();
                return;
            }
        }

        //TODO this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;

        if (this.inBlockID.getMaterial() != Material.air)
        {
            this.inBlockID.onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
        }

        //TODO implement splintering of fragment
        //TODO implement bouncing

        onImpactTile();
    }

    @Override
    protected void onImpactTile()
    {
        inGroundKillTime = 20 + worldObj.rand.nextInt(100);
        //TODO add config to control kill time
        //TODO add config to insta kill on impact
    }

    @Override
    protected void onImpactEntity(Entity entityHit, float velocity)
    {
        //TODO implement chance for fragment to bounce off of armor
        //TODO implement chance for fragment to stick into person
        //TODO implement chance for fragment to go threw entity
        //TODO implement body part fragmentation on impact
        //TODO implement blood on impact
        if (!(entityHit instanceof EntityFragment))
        {
            if (fragmentType == null)
            {
                return;
            }

            float damage = 1;
            if (fragmentType == FragmentType.BLOCK && fragmentMaterial != null)
            {
                //TODO get mass of block for better calculation
                //TODO add sharpness of material into calculation (Ex obsidian and metals)
                damage = getDamageForFragment() * velocity;
            }

            if (isBurning())
            {
                entityHit.setFire(5);
            }

            //If entity takes damage add velocity to entity
            if (entityHit.attackEntityFrom(new DamageFragment(fragmentType.name().toLowerCase(), entityHit, this), damage))
            {
                if (entityHit instanceof EntityLivingBase)
                {
                    float vel_horizontal = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    if (vel_horizontal > 0.0F)
                    {
                        entityHit.addVelocity(this.motionX * 0.6000000238418579D / (double) vel_horizontal, 0.1D, this.motionZ * 0.6000000238418579D / (double) vel_horizontal);
                    }
                }
            }
            setDead();
        }
    }

    public float getDamageForFragment()
    {
        if (fragmentMaterial != null)
        {
            if (fragmentType == FragmentType.BLOCK)
            {
                Block block = Block.getBlockFromItem(fragmentMaterial);
                return block.blockHardness;
            }
            else if (fragmentMaterial instanceof ItemSword)
            {
                return ((ItemSword) fragmentMaterial).func_150931_i();
            }
            //TODO add a registry or something to get damage per item
        }
        return 1;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("fragmentType"))
        {
            int i = nbt.getInteger("fragmentType");
            if (i > 0 && i < FragmentType.values().length)
            {
                fragmentType = FragmentType.values()[i];
            }
        }

        //Legacy code
        if (nbt.hasKey("blockToMimic"))
        {
            Block block = Block.getBlockById(nbt.getInteger("blockToMimic"));
            if (block != null)
            {
                fragmentMaterial = Item.getItemFromBlock(block);
            }
        }
        if (nbt.hasKey("itemToMimic"))
        {
            String itemRegistryName = nbt.getString("itemToMimic");
            fragmentMaterial = (Item) Item.itemRegistry.getObject(itemRegistryName);
        }
        if (nbt.hasKey("renderShape"))
        {
            renderShape = new Cube(nbt.getCompoundTag("renderShape"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        if (fragmentType != null)
        {
            nbt.setInteger("fragmentType", fragmentType.ordinal());
        }
        if (fragmentMaterial != null)
        {
            String regName = Item.itemRegistry.getNameForObject(fragmentMaterial);
            if (regName != null)
            {
                nbt.setString("itemToMimic", regName);
            }
        }
        if (renderShape != null)
        {
            nbt.setTag("renderShape", renderShape.toNBT());
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        if (fragmentType != null)
        {
            buffer.writeByte(fragmentType.ordinal());
        }
        else
        {
            buffer.writeByte(-1);
        }
        if (fragmentMaterial != null)
        {
            buffer.writeInt(Item.getIdFromItem(fragmentMaterial));
        }
        else
        {
            buffer.writeByte(-1);
        }
        if (renderShape != null)
        {
            buffer.writeBoolean(true);
            renderShape.writeBytes(buffer);
        }
        else
        {
            buffer.writeBoolean(false);
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        try
        {
            byte i = additionalData.readByte();
            if (i >= 0 && i < FragmentType.values().length)
            {
                fragmentType = FragmentType.values()[i];
            }
            int blockID = additionalData.readInt();
            if (blockID > -1)
            {
                fragmentMaterial = (Item) Item.itemRegistry.getObjectById(blockID);
            }
            if (additionalData.readBoolean())
            {
                renderShape = new Cube(additionalData);
            }
        }
        catch (Exception e)
        {
            ICBM.INSTANCE.logger().error("Failed to read spawn data for " + this);
        }
    }

    @Override
    public String toString()
    {
        return "EntityFragment[ dim@" + (worldObj != null && worldObj.provider != null ? worldObj.provider.dimensionId : "null") + " " + posX + "x " + posY + "y " + posZ + "z | " + fragmentType + "]@" + hashCode();
    }

    @Override
    public EnumProjectileTypes getProjectileType()
    {
        return EnumProjectileTypes.FRAGMENT;
    }
}
