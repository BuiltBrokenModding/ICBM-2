package icbm.zhapin.gui;

import icbm.api.ICBM;
import icbm.core.GuiBase;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.daodan.EDaoDan;
import icbm.zhapin.jiqi.BJiQi;
import icbm.zhapin.jiqi.TLeiDaTai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import universalelectricity.core.electricity.ElectricInfo;
import universalelectricity.core.electricity.ElectricInfo.ElectricUnit;
import universalelectricity.core.vector.Vector2;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.vector.Region2;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GLeiDaTai extends GuiBase
{
	private TLeiDaTai tileEntity;

	private int containerPosX;
	private int containerPosY;

	private GuiTextField textFieldAlarmRange;
	private GuiTextField textFieldSafetyZone;

	private List<Vector2> missileCoords = new ArrayList<Vector2>();

	private Vector2 mouseOverCoords = new Vector2();

	private Vector2 mousePosition = new Vector2();

	// Radar Map
	private Vector2 radarCenter;
	private float radarMapRadius;

	private String info = "";

	private String info2;

	public GLeiDaTai(TLeiDaTai tileEntity)
	{
		this.tileEntity = tileEntity;
		mouseOverCoords = new Vector2(this.tileEntity.xCoord, this.tileEntity.zCoord);
		this.xSize = 256;
		radarCenter = new Vector2(this.containerPosX + this.xSize / 3 - 14, this.containerPosY + this.ySize / 2 + 4);
		radarMapRadius = this.tileEntity.MAX_BIAN_JING / 63.8F;
	}

	public void initGui()
	{
		super.initGui();

		this.textFieldSafetyZone = new GuiTextField(fontRenderer, 155, 83, 30, 12);
		this.textFieldSafetyZone.setMaxStringLength(3);
		this.textFieldSafetyZone.setText(this.tileEntity.safetyBanJing + "");

		this.textFieldAlarmRange = new GuiTextField(fontRenderer, 155, 110, 30, 12);
		this.textFieldAlarmRange.setMaxStringLength(3);
		this.textFieldAlarmRange.setText(this.tileEntity.alarmBanJing + "");

		PacketDispatcher.sendPacketToServer(PacketManager.getPacket("ICBM", this.tileEntity, (int) -1, true));
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		PacketDispatcher.sendPacketToServer(PacketManager.getPacket("ICBM", this.tileEntity, (int) -1, false));
	}

	/**
	 * Call this method from you GuiScreen to process the keys into textbox.
	 */
	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
		this.textFieldSafetyZone.textboxKeyTyped(par1, par2);
		this.textFieldAlarmRange.textboxKeyTyped(par1, par2);

		try
		{
			int newSafetyRadius = Math.min(this.tileEntity.MAX_BIAN_JING, Math.max(0, Integer.parseInt(this.textFieldSafetyZone.getText())));
			this.tileEntity.safetyBanJing = newSafetyRadius;
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this.tileEntity, (int) 2, this.tileEntity.safetyBanJing));
		}
		catch (NumberFormatException e)
		{
		}

		try
		{
			int newAlarmRadius = Math.min(this.tileEntity.MAX_BIAN_JING, Math.max(0, Integer.parseInt(this.textFieldAlarmRange.getText())));
			this.tileEntity.alarmBanJing = newAlarmRadius;
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this.tileEntity, (int) 3, this.tileEntity.alarmBanJing));
		}
		catch (NumberFormatException e)
		{
		}

	}

	/**
	 * Args: x, y, buttonClicked
	 */
	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		this.textFieldAlarmRange.mouseClicked(par1 - containerPosX, par2 - containerPosY, par3);
		this.textFieldSafetyZone.mouseClicked(par1 - containerPosX, par2 - containerPosY, par3);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawForegroundLayer()
	{
		this.fontRenderer.drawString(TranslationHelper.getLocal("icbm.machine.9.name"), this.xSize / 2 - 85, 6, 4210752);

		this.fontRenderer.drawString("Settings", this.xSize / 2 + 55, 6, 4210752);

		this.fontRenderer.drawString("Coordinates:", 155, 18, 4210752);
		this.fontRenderer.drawString("X: " + (int) Math.round(mouseOverCoords.x) + " Z: " + (int) Math.round(mouseOverCoords.y), 155, 30, 4210752);

		this.fontRenderer.drawString("\u00a76" + this.info, 155, 42, 4210752);
		this.fontRenderer.drawString("\u00a74" + this.info2, 155, 54, 4210752);

		this.fontRenderer.drawString("Safe Zone:", 155, 70, 4210752);
		this.textFieldSafetyZone.drawTextBox();
		this.fontRenderer.drawString("Blocks", 190, 85, 4210752);

		this.fontRenderer.drawString("Alarm Range:", 155, 98, 4210752);
		this.textFieldAlarmRange.drawTextBox();
		this.fontRenderer.drawString("Blocks", 190, 112, 4210752);

		this.fontRenderer.drawString(ElectricInfo.getDisplay(this.tileEntity.getRequest().getWatts() * 20, ElectricUnit.WATT) , 155, 128, 4210752);

		this.fontRenderer.drawString(ElectricInfo.getDisplay(this.tileEntity.getVoltage(), ElectricUnit.VOLTAGE), 155, 138, 4210752);

		// Shows the status of the radar
		String color = "\u00a74";
		String status = "Idle";

		if (this.tileEntity.isDisabled())
		{
			status = "Disabled!";
		}
		else if (this.tileEntity.prevWatts >= this.tileEntity.getRequest().getWatts())
		{
			color = "\u00a72";
			status = "Radar On!";
		}
		else
		{
			status = "No Electricity!";
		}

		this.fontRenderer.drawString(color + status, 155, 150, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH + "RadarGUI.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		this.containerPosX = (this.width - this.xSize) / 2;
		this.containerPosY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerPosX, containerPosY, 0, 0, this.xSize, this.ySize);

		this.radarCenter = new Vector2(this.containerPosX + this.xSize / 3 - 10, this.containerPosY + this.ySize / 2 + 4);
		this.radarMapRadius = this.tileEntity.MAX_BIAN_JING / 65F;

		this.info = "";
		this.info2 = "";

		if (this.tileEntity.prevWatts >= this.tileEntity.getRequest().getWatts())
		{
			int range = 4;

			for (Entity entity : this.tileEntity.xunZhaoEntity)
			{
				Vector2 position = new Vector2(radarCenter.x + (entity.posX - this.tileEntity.xCoord) / this.radarMapRadius, radarCenter.y - (entity.posZ - this.tileEntity.zCoord) / this.radarMapRadius);

				if (entity instanceof EDaoDan)
				{
					if (this.tileEntity.isWeiXianDaoDan((EDaoDan) entity))
					{
						var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH + "reddot.png");
					}
					else
					{
						var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH + "yellowdot.png");

					}
				}
				else
				{
					var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH + "yellowdot.png");
				}

				this.mc.renderEngine.bindTexture(var4);
				this.drawTexturedModalRect(position.intX(), position.intY(), 0, 0, 2, 2);

				// Hover Detection
				Vector2 minPosition = position.clone();
				minPosition.add(-range);
				Vector2 maxPosition = position.clone();
				maxPosition.add(range);

				if (new Region2(minPosition, maxPosition).isIn(this.mousePosition))
				{
					this.info = entity.getEntityName();

					if (entity instanceof EntityPlayer)
					{
						this.info = "\u00a71" + this.info;
					}

					if (entity instanceof EDaoDan)
					{
						if (((EDaoDan) entity).muBiao != null)
						{
							this.info2 = "(" + ((EDaoDan) entity).muBiao.intX() + ", " + ((EDaoDan) entity).muBiao.intZ() + ")";
						}
					}
				}
			}

			range = 2;

			for (TileEntity jiQi : this.tileEntity.xunZhaoJiQi)
			{
				Vector2 position = new Vector2(this.radarCenter.x + (int) (jiQi.xCoord - this.tileEntity.xCoord) / this.radarMapRadius, this.radarCenter.y - (int) (jiQi.zCoord - this.tileEntity.zCoord) / this.radarMapRadius);

				var4 = this.mc.renderEngine.getTexture("/icbm/whitedot.png");

				this.mc.renderEngine.bindTexture(var4);
				this.drawTexturedModalRect(position.intX(), position.intY(), 0, 0, 2, 2);

				Vector2 minPosition = position.clone();
				minPosition.add(-range);
				Vector2 maxPosition = position.clone();
				maxPosition.add(range);

				if (new Region2(minPosition, maxPosition).isIn(this.mousePosition))
				{
					if (jiQi.getBlockType() != null)
					{
						if (jiQi.getBlockType() instanceof BJiQi)
						{
							this.info = BJiQi.getJiQiMing(jiQi);
						}
						else
						{
							this.info = jiQi.getBlockType().translateBlockName();
						}
					}
				}
			}
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if (Mouse.isInsideWindow())
		{
			if (Mouse.getEventButton() == -1)
			{
				this.mousePosition = new Vector2(Mouse.getEventX() * this.width / this.mc.displayWidth, this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1);

				float difference = (int) this.tileEntity.MAX_BIAN_JING / this.radarMapRadius;

				if (this.mousePosition.x > this.radarCenter.x - difference && this.mousePosition.x < this.radarCenter.x + difference && this.mousePosition.y > this.radarCenter.y - difference && this.mousePosition.y < this.radarCenter.y + difference)
				{
					// Calculate from the mouse position the relative position on the grid
					int xDifference = (int) (this.mousePosition.x - this.radarCenter.x);
					int yDifference = (int) (this.mousePosition.y - this.radarCenter.y);
					int xBlockDistance = (int) (xDifference * this.radarMapRadius);
					int yBlockDistance = (int) (yDifference * this.radarMapRadius);

					this.mouseOverCoords = new Vector2(this.tileEntity.xCoord + xBlockDistance, this.tileEntity.zCoord - yBlockDistance);
				}
			}
		}

		if (!this.textFieldSafetyZone.isFocused())
			this.textFieldSafetyZone.setText(this.tileEntity.safetyBanJing + "");
		if (!this.textFieldAlarmRange.isFocused())
			this.textFieldAlarmRange.setText(this.tileEntity.alarmBanJing + "");
	}
}
