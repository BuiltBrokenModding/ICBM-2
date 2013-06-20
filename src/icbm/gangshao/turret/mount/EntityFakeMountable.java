package icbm.gangshao.turret.mount;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

/** A fake/invisible entity used for the player to mount on to create an illusion that the player is
 * mounting a TileEntity.
 * 
 * @author Calclavia */
public class EntityFakeMountable extends Entity implements IEntityAdditionalSpawnData
{
	private TileEntity controller;
	private boolean shouldSit = false;

	public EntityFakeMountable(World par1World)
	{
		super(par1World);
		this.setSize(1F, 1F);
	}

	public EntityFakeMountable(World par1World, Vector3 position, TileEntity controller, boolean sit)
	{
		this(par1World);
		this.isImmuneToFire = true;
		this.setPosition(position.x, position.y, position.z);
		this.controller = controller;
		this.shouldSit = sit;
	}

	@Override
	public String getEntityName()
	{
		return "Seat";
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		if (this.controller != null)
		{
			data.writeInt(this.controller.xCoord);
			data.writeInt(this.controller.yCoord);
			data.writeInt(this.controller.zCoord);
		}

		data.writeBoolean(this.shouldSit);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.controller = this.worldObj.getBlockTileEntity(data.readInt(), data.readInt(), data.readInt());
		this.shouldSit = data.readBoolean();
	}

	/** Called to update the entity's position/logic. */
	@Override
	public void onUpdate()
	{
		if (this.controller == null)
		{
			this.setDead();
			return;
		}
		else if (this.controller.isInvalid())
		{
			this.setDead();
			return;
		}

		this.posY = this.controller.yCoord + 1.2;
	}

	@Override
	public double getMountedYOffset()
	{
		return -0.5;
	}

	/** returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for
	 * spiders and wolves to prevent them from trampling crops */
	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public boolean shouldRiderSit()
	{
		return this.shouldSit;
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound var1)
	{

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1)
	{

	}
}