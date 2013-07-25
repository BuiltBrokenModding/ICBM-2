package icbm.gangshao.turret.mount;

import icbm.core.ZhuYaoICBM;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * A fake/invisible entity used for the player to mount on to create an illusion that the player is
 * mounting a TileEntity.
 * 
 * @author Calclavia
 */
public class EJia extends Entity implements IEntityAdditionalSpawnData
{
	private TileEntity controller;
	private boolean shouldSit = false;

	public EJia(World par1World)
	{
		super(par1World);
		this.setSize(1F, 1F);
		this.noClip = true;
	}

	public EJia(World par1World, Vector3 position, TileEntity controller, boolean sit)
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
		else
		{
			ZhuYaoICBM.LOGGER.severe("Failed to send ridable turret packet!");
		}

		data.writeBoolean(this.shouldSit);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		try
		{
			this.controller = this.worldObj.getBlockTileEntity(data.readInt(), data.readInt(), data.readInt());
			this.shouldSit = data.readBoolean();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

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

		if (this.controller instanceof TPaoTaiQi)
		{
			((TPaoTaiQi) this.controller).entityFake = this;
		}

		if (this.worldObj.isRemote && this.riddenByEntity != null)
		{
			this.riddenByEntity.updateRiderPosition();
		}

		this.posY = this.controller.yCoord + 1.2;
	}

	@Override
	public double getMountedYOffset()
	{
		return -0.5;
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for
	 * spiders and wolves to prevent them from trampling crops
	 */
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
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		this.shouldSit = nbt.getBoolean("shouldSit");
		Vector3 readVec = Vector3.readFromNBT(nbt.getCompoundTag("controller"));

		if (readVec.getTileEntity(this.worldObj) != null)
		{
			this.controller = readVec.getTileEntity(this.worldObj);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("shouldSit", this.shouldSit);
		nbt.setCompoundTag("controller", new Vector3(this.controller).writeToNBT(new NBTTagCompound()));
	}
}