package icbm.explosion.missile.missile;

import icbm.core.ICBMCore;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MissilePlayerHandler implements IUpdatePlayerListBox
{
	private final SoundManager theSoundManager;

	private final EntityMissile entity;

	private final EntityPlayerSP thePlayer;
	private boolean playerSPRidingMinecart = false;
	private boolean minecartIsDead = false;
	private boolean minecartIsMoving = false;
	private boolean silent = false;
	private float minecartSoundPitch = 0.0F;
	private float minecartMoveSoundVolume = 0.0F;
	private float minecartRideSoundVolume = 0.0F;
	private double minecartSpeed = 0.0D;

	public MissilePlayerHandler(SoundManager par1SoundManager, EntityMissile entity, EntityPlayerSP par3EntityPlayerSP)
	{
		this.theSoundManager = par1SoundManager;
		this.entity = entity;
		this.thePlayer = par3EntityPlayerSP;
	}

	/** Updates the JList with a new model. */
	@Override
	public void update()
	{
		boolean var1 = false;
		final boolean var2 = this.playerSPRidingMinecart;
		final boolean var3 = this.minecartIsDead;
		final boolean var4 = this.minecartIsMoving;
		final float var5 = this.minecartMoveSoundVolume;
		final float var6 = this.minecartSoundPitch;
		final float var7 = this.minecartRideSoundVolume;
		this.playerSPRidingMinecart = this.thePlayer != null && this.entity.riddenByEntity == this.thePlayer;
		this.minecartIsDead = this.entity.isDead;
		this.minecartSpeed = 20;
		this.minecartIsMoving = this.minecartSpeed >= 0.01D;

		if (var2 && !this.playerSPRidingMinecart)
		{
			this.theSoundManager.stopEntitySound(this.thePlayer);
		}

		if (this.minecartIsDead || !this.silent && this.minecartMoveSoundVolume == 0.0F && this.minecartRideSoundVolume == 0.0F)
		{
			if (!var3)
			{
				this.theSoundManager.stopEntitySound(this.entity);

				if (var2 || this.playerSPRidingMinecart)
				{
					this.theSoundManager.stopEntitySound(this.thePlayer);
				}
			}

			this.silent = true;

			if (this.minecartIsDead)
			{
				return;
			}
		}

		if (this.theSoundManager != null && this.entity != null && this.minecartMoveSoundVolume > 0)
		{
			this.theSoundManager.playEntitySound(ICBMCore.PREFIX + "missileinair", this.entity, 7.0F, this.minecartSoundPitch, true);
			this.silent = false;
			var1 = true;
		}

		if (this.entity.getTicksInAir() > 0)
		{
			if (this.minecartSoundPitch < 1.0F)
			{
				this.minecartSoundPitch += 0.0025F;
			}

			if (this.minecartSoundPitch > 1.0F)
			{
				this.minecartSoundPitch = 1.0F;
			}

			float var10 = MathHelper.clamp_float((float) this.minecartSpeed, 0.0F, 4.0F) / 4.0F;
			this.minecartRideSoundVolume = 0.0F + var10 * 0.75F;
			var10 = MathHelper.clamp_float(var10 * 2.0F, 0.0F, 1.0F);
			this.minecartMoveSoundVolume = 0.0F + var10 * 6.7F;

			if (this.entity.posY > 1000)
			{
				this.minecartMoveSoundVolume = 0F;
				this.minecartRideSoundVolume = 0F;
			}
		}
		else if (var4)
		{
			this.minecartMoveSoundVolume = 0.0F;
			this.minecartSoundPitch = 0.0F;
			this.minecartRideSoundVolume = 0.0F;
		}

		if (!this.silent)
		{
			if (this.minecartSoundPitch != var6)
			{
				this.theSoundManager.setEntitySoundPitch(this.entity, this.minecartSoundPitch);
			}

			if (this.minecartMoveSoundVolume != var5)
			{
				this.theSoundManager.setEntitySoundVolume(this.entity, this.minecartMoveSoundVolume);
			}

			if (this.minecartRideSoundVolume != var7)
			{
				this.theSoundManager.setEntitySoundVolume(this.thePlayer, this.minecartRideSoundVolume);
			}
		}

		if (!var1)
		{
			this.theSoundManager.updateSoundLocation(this.entity);

			if (this.playerSPRidingMinecart)
			{
				this.theSoundManager.updateSoundLocation(this.thePlayer, this.entity);
			}
		}
	}
}