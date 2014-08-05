package icbm.explosion.gui;

import icbm.Reference;
import icbm.core.ICBMCore;
import icbm.core.gui.GuiICBM;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.machines.BlockICBMMachine;
import icbm.explosion.machines.TileRadarStation;
import icbm.explosion.machines.TileRadarStation.GUIEntityBase;
import icbm.explosion.machines.TileRadarStation.GUIEntityMissile;
import icbm.explosion.machines.TileRadarStation.GUITile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import resonant.lib.prefab.vector.Rectangle;
import resonant.lib.utility.LanguageUtility;
import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import universalelectricity.api.vector.Vector2;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiRadarStation extends GuiICBM
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_radar.png");
    public static final ResourceLocation TEXTURE_RED_DOT = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "reddot.png");
    public static final ResourceLocation TEXTURE_YELLOW_DOT = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "yellowdot.png");
    public static final ResourceLocation TEXTURE_WHITE_DOT = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "whitedot.png");
    private TileRadarStation tileEntity;

    private int containerPosX;
    private int containerPosY;

    private GuiTextField textFieldAlarmRange;
    private GuiTextField textFieldSafetyZone;
    private GuiTextField textFieldFrequency;

    private List<Vector2> missileCoords = new ArrayList<Vector2>();

    private Vector2 mouseOverCoords = new Vector2();
    private Vector2 mousePosition = new Vector2();

    // Radar Map
    private Vector2 radarCenter;
    private float radarMapRadius;

    private String info = "";

    private String info2;

    public GuiRadarStation(TileRadarStation tileEntity)
    {
        this.tileEntity = tileEntity;
        mouseOverCoords = new Vector2(this.tileEntity.xCoord, this.tileEntity.zCoord);
        ySize = 166;
        xSize = 256;
        radarCenter = new Vector2(this.containerPosX + this.xSize / 3 - 14, this.containerPosY + this.ySize / 2 + 4);
        radarMapRadius = TileRadarStation.MAX_DETECTION_RANGE / 63.8F;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.textFieldSafetyZone = new GuiTextField(fontRenderer, 210, 67, 30, 12);
        this.textFieldSafetyZone.setMaxStringLength(3);
        this.textFieldSafetyZone.setText(this.tileEntity.safetyRange + "");

        this.textFieldAlarmRange = new GuiTextField(fontRenderer, 210, 82, 30, 12);
        this.textFieldAlarmRange.setMaxStringLength(3);
        this.textFieldAlarmRange.setText(this.tileEntity.alarmRange + "");

        this.textFieldFrequency = new GuiTextField(fontRenderer, 155, 112, 50, 12);
        this.textFieldFrequency.setMaxStringLength(6);
        this.textFieldFrequency.setText(this.tileEntity.getFrequency() + "");

        PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, -1, true));
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, -1, false));
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString("\u00a77" + LanguageUtility.getLocal("icbm.machine.9.name"), this.xSize / 2 - 30, 6, 4210752);

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.radar.coords"), 155, 18, 4210752);
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.x") + " " + (int) Math.round(mouseOverCoords.x) + " " + LanguageUtility.getLocal("gui.misc.z") + " " + (int) Math.round(mouseOverCoords.y), 155, 30, 4210752);

        this.fontRenderer.drawString("\u00a76" + this.info, 155, 42, 4210752);
        this.fontRenderer.drawString("\u00a74" + this.info2, 155, 54, 4210752);

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.radar.zoneSafe"), 152, 70, 4210752);
        this.textFieldSafetyZone.drawTextBox();
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.radar.zoneAlarm"), 150, 85, 4210752);
        this.textFieldAlarmRange.drawTextBox();

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.freq"), 155, 100, 4210752);
        this.textFieldFrequency.drawTextBox();

        this.fontRenderer.drawString(UnitDisplay.getDisplay(TileRadarStation.WATTS, Unit.WATT), 155, 128, 4210752);

        this.fontRenderer.drawString(UnitDisplay.getDisplay(this.tileEntity.getVoltageInput(null), Unit.VOLTAGE), 155, 138, 4210752);

        // Shows the status of the radar
        String color = "\u00a74";
        String status = LanguageUtility.getLocal("gui.misc.idle");

        if (this.tileEntity.getEnergyHandler().checkExtract())
        {
            color = "\u00a72";
            status = LanguageUtility.getLocal("gui.radar.on");
        }
        else
        {
            status = LanguageUtility.getLocal("gui.radar.nopower");
        }

        this.fontRenderer.drawString(color + status, 155, 150, 4210752);
    }

    /** Call this method from you GuiScreen to process the keys into textbox. */
    @Override
    public void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.textFieldSafetyZone.textboxKeyTyped(par1, par2);
        this.textFieldAlarmRange.textboxKeyTyped(par1, par2);
        this.textFieldFrequency.textboxKeyTyped(par1, par2);

        try
        {
            int newSafetyRadius = Math.min(TileRadarStation.MAX_DETECTION_RANGE, Math.max(0, Integer.parseInt(this.textFieldSafetyZone.getText())));
            this.tileEntity.safetyRange = newSafetyRadius;
            PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, 2, this.tileEntity.safetyRange));
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            int newAlarmRadius = Math.min(TileRadarStation.MAX_DETECTION_RANGE, Math.max(0, Integer.parseInt(this.textFieldAlarmRange.getText())));
            this.tileEntity.alarmRange = newAlarmRadius;
            PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, 3, this.tileEntity.alarmRange));
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            this.tileEntity.setFrequency(Integer.parseInt(this.textFieldFrequency.getText()));
            PacketDispatcher.sendPacketToServer(ICBMCore.PACKET_TILE.getPacket(this.tileEntity, 4, this.tileEntity.getFrequency()));
        }
        catch (NumberFormatException e)
        {
        }

    }

    /** Args: x, y, buttonClicked */
    @Override
    public void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.textFieldAlarmRange.mouseClicked(par1 - containerPosX, par2 - containerPosY, par3);
        this.textFieldSafetyZone.mouseClicked(par1 - containerPosX, par2 - containerPosY, par3);
        this.textFieldFrequency.mouseClicked(par1 - containerPosX, par2 - containerPosY, par3);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.containerPosX = (this.width - this.xSize) / 2;
        this.containerPosY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerPosX, containerPosY, 0, 0, this.xSize, this.ySize);

        this.radarCenter = new Vector2(this.containerPosX + this.xSize / 3 - 10, this.containerPosY + this.ySize / 2 + 4);
        this.radarMapRadius = TileRadarStation.MAX_DETECTION_RANGE / 71f;

        this.info = "";
        this.info2 = "";

        if (this.tileEntity.getEnergyHandler().checkExtract())
        {
            int range = 4;

            for (GUIEntityBase entity : this.tileEntity.entityList)
            {
            	GUIEntityMissile missile = null;
                Vector2 position = new Vector2(radarCenter.x + (entity.pos.x - this.tileEntity.xCoord) / this.radarMapRadius, radarCenter.y - (entity.pos.y - this.tileEntity.zCoord) / this.radarMapRadius);

                if (entity.type == this.tileEntity.EntityTypeMissile)
                {
                	missile = (GUIEntityMissile)entity;
                    if (missile.willHit)
                    {
                        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_RED_DOT);
                    }
                    else
                    {
                        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_YELLOW_DOT);
                    }
                }
                else
                {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_YELLOW_DOT);
                }

                this.drawTexturedModalRect(position.intX(), position.intY(), 0, 0, 2, 2);

                // Hover Detection
                Vector2 minPosition = position.clone();
                minPosition.add(-range);
                Vector2 maxPosition = position.clone();
                maxPosition.add(range);

                if (new Rectangle(minPosition, maxPosition).isIn(this.mousePosition))
                {
                    this.info = entity.name;

                    if (entity.type == this.tileEntity.EntityTypePlayer)
                    {
                        this.info = "\u00a71" + this.info;
                    }

                    if (entity.type == this.tileEntity.EntityTypeMissile)
                    {
                        if (missile.gotTarget)
                        {
                            this.info2 = "(" + missile.hitX + ", " + missile.hitZ + ")";
                        }
                    }
                }
            }

            range = 2;

            for (GUITile tile : this.tileEntity.tileList)
            {
                Vector2 position = new Vector2(this.radarCenter.x + (tile.posX - this.tileEntity.xCoord) / this.radarMapRadius, this.radarCenter.y - (tile.posZ - this.tileEntity.zCoord) / this.radarMapRadius);
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_WHITE_DOT);

                this.drawTexturedModalRect(position.intX(), position.intY(), 0, 0, 2, 2);

                Vector2 minPosition = position.clone();
                minPosition.add(-range);
                Vector2 maxPosition = position.clone();
                maxPosition.add(range);

                if (new Rectangle(minPosition, maxPosition).isIn(this.mousePosition))
                {
                	this.info = tile.name;
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

                float difference = TileRadarStation.MAX_DETECTION_RANGE / this.radarMapRadius;

                if (this.mousePosition.x > this.radarCenter.x - difference && this.mousePosition.x < this.radarCenter.x + difference && this.mousePosition.y > this.radarCenter.y - difference && this.mousePosition.y < this.radarCenter.y + difference)
                {
                    // Calculate from the mouse position the relative position
                    // on the grid
                    int xDifference = (int) (this.mousePosition.x - this.radarCenter.x);
                    int yDifference = (int) (this.mousePosition.y - this.radarCenter.y);
                    int xBlockDistance = (int) (xDifference * this.radarMapRadius);
                    int yBlockDistance = (int) (yDifference * this.radarMapRadius);

                    this.mouseOverCoords = new Vector2(this.tileEntity.xCoord + xBlockDistance, this.tileEntity.zCoord - yBlockDistance);
                }
            }
        }

        if (!this.textFieldSafetyZone.isFocused())
            this.textFieldSafetyZone.setText(this.tileEntity.safetyRange + "");
        if (!this.textFieldAlarmRange.isFocused())
            this.textFieldAlarmRange.setText(this.tileEntity.alarmRange + "");
    }
}
