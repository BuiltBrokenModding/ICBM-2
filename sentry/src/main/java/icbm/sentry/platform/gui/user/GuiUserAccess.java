package icbm.sentry.platform.gui.user;

import icbm.core.prefab.render.GuiICBMContainer;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import calclavia.lib.access.AccessUser;
import calclavia.lib.access.IProfileContainer;
import calclavia.lib.gui.ContainerDummy;
import calclavia.lib.prefab.terminal.IScroll;
import cpw.mods.fml.common.FMLCommonHandler;

/** Gui that shows a list of users and allows the current user to go threw them. On clicking a user a
 * gui should be opened to allow the user to edit or view the selected user. Buttons should be
 * Grayed out for the users the current user has no access to.
 * 
 * @author DarkGuardsman */
public class GuiUserAccess extends GuiICBMContainer implements IScroll
{
    private GuiButton[] userButtons = new GuiButton[5];
    private GuiButton down;
    private GuiButton up;

    private AccessUser[] loadedUsers = new AccessUser[5];

    private int page = 0;
    private int maxPage = 0;

    private TileEntity tileEntity;
    private EntityPlayer player;

    public GuiUserAccess(EntityPlayer player, TileEntity tileEntity)
    {
        super(new ContainerDummy(player, tileEntity));
        this.tileEntity = tileEntity;
        this.player = player;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int buttonWidth = 150;
        int buttonHeight = 20;

        int shiftDown = 20;
        int shiftRight = 13;

        up = new GuiButton(3, this.guiLeft + shiftRight, this.guiTop + shiftDown, buttonWidth, buttonHeight, "PAGE UP");
        up.enabled = false;
        this.buttonList.add(up);
        down = new GuiButton(4, this.guiLeft + shiftRight, this.guiTop + 20 + shiftDown + userButtons.length * 20, buttonWidth, buttonHeight, "PAGE DOWN");
        this.buttonList.add(down);

        for (int i = 0; i < userButtons.length; i++)
        {
            userButtons[i] = new GuiButton(i + 5, this.guiLeft + shiftRight, this.guiTop + 20 + shiftDown + i * 20, buttonWidth, buttonHeight, "+++++");
            userButtons[i].enabled = false;
            this.buttonList.add(userButtons[i]);
        }

        this.loadPage(this.page);
        this.scroll(0);
    }

    public void loadPage(int page)
    {
        if (this.tileEntity instanceof IProfileContainer)
        {
            List<AccessUser> users = ((IProfileContainer)tileEntity).getAccessProfile().getUsers();
            int nextID = this.page * this.userButtons.length;
            boolean disabled = false;
            for (int i = 0; i < loadedUsers.length; i++)
            {
                loadedUsers[i] = null;
                if (users != null && (nextID + i) < users.size() && users.get(nextID + i) != null)
                {
                    AccessUser user = users.get(nextID + i);
                    loadedUsers[i] = user;
                    this.userButtons[i].displayString = (user.getGroup() != null ? "[" + user.getGroup().getName() + "]" : "") + user.getName();
                    this.userButtons[i].enabled = true;
                }
                else
                {

                    if (disabled)
                    {
                        this.userButtons[i].enabled = false;
                        this.userButtons[i].displayString = "-----";
                    }
                    else
                    {
                        this.userButtons[i].enabled = true;
                        this.userButtons[i].displayString = "Add User";
                    }
                    disabled = true;
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        if (button.id == 3)
        {
            this.scroll(-1);
        }
        else if (button.id == 4)
        {
            this.scroll(1);
        }
        else if (button.id > 4 && button.id < userButtons.length + 5)
        {
            if (this.loadedUsers[button.id - 5] != null)
            {
                FMLCommonHandler.instance().showGuiScreen(new GuiEditUser(this, this.loadedUsers[button.id - 5]));
            }
            else
            {
                //TODO open add user dialog
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        this.loadPage(this.page);
        this.scroll(0);
    }

    @Override
    public void scroll(int amount)
    {
        setScroll(this.getScroll() + amount);
        if (this.page == this.maxPage)
        {
            this.down.enabled = false;
        }
        else
        {
            this.down.enabled = true;
        }
        if (this.page == 0)
        {
            this.up.enabled = false;
        }
        else
        {
            this.up.enabled = true;
        }
    }

    @Override
    public void setScroll(int length)
    {
        this.page = length;
        if (this.page > this.maxPage)
        {
            this.page = maxPage;
        }
        if (this.page < 0)
        {
            this.page = 0;
        }
    }

    @Override
    public int getScroll()
    {
        return this.page;
    }
}
