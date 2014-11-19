package icbm.sentry.platform.gui.user;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import resonant.api.IScroll;
import resonant.lib.References;
import resonant.lib.access.AccessUser;
import resonant.lib.access.IProfileContainer;
import resonant.lib.gui.ContainerDummy;
import resonant.lib.gui.GuiContainerBase;
import cpw.mods.fml.common.FMLCommonHandler;

/** Gui that shows a list of users and allows the current user to go threw them. When clicking a user
 * a gui should be opened to allow the user to edit or view the selected user. Buttons should be
 * Grayed out for the users the current user has no access to edit.
 * 
 * @author DarkGuardsman */
public class GuiUserAccess extends GuiContainerBase implements IScroll
{
    private GuiButton[] user_list_buttons = new GuiButton[5];
    private GuiButton scroll_down_button;
    private GuiButton scroll_up_button;

    public static final int SCROLL_UP_BUTTON_ID = 0;
    public static final int SCROLL_DOWN_BUTTON_ID = 1;

    private AccessUser[] loadedUsers = new AccessUser[5];

    /** Current user page displayed */
    private int currentPage = 0;
    /** Limit page number that can be scrolled to */
    private int pageLimit = 0;
    /** Current tile being accessed */
    public TileEntity tileEntity;
    /** Player accessing the gui */
    public EntityPlayer player;

    public GuiUserAccess(EntityPlayer player, TileEntity tileEntity)
    {
        super(new ContainerDummy(player, tileEntity));
        this.tileEntity = tileEntity;
        this.player = player;
        this.baseTexture = References.GUI_EMPTY_FILE;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int buttonWidth = 150;
        int buttonHeight = 20;

        int shiftDown = 20;
        int shiftRight = 13;

        scroll_up_button = new GuiButton(SCROLL_UP_BUTTON_ID, this.guiLeft + shiftRight, this.guiTop + shiftDown, buttonWidth, buttonHeight, "PAGE UP");
        scroll_up_button.enabled = false;
        this.buttonList.add(scroll_up_button);
        scroll_down_button = new GuiButton(SCROLL_DOWN_BUTTON_ID, this.guiLeft + shiftRight, this.guiTop + 20 + shiftDown + user_list_buttons.length * 20, buttonWidth, buttonHeight, "PAGE DOWN");
        this.buttonList.add(scroll_down_button);

        for (int i = 0; i < user_list_buttons.length; i++)
        {
            user_list_buttons[i] = new GuiButton(i + SCROLL_DOWN_BUTTON_ID + 1, this.guiLeft + shiftRight, this.guiTop + 20 + shiftDown + i * 20, buttonWidth, buttonHeight, "+++++");
            user_list_buttons[i].enabled = false;
            this.buttonList.add(user_list_buttons[i]);
        }

        this.scroll(0);
        this.loadPage(this.currentPage);
    }

    /** Loads an array of users and buttons that will be displayed as a page */
    public void loadPage(int page)
    {
        if (this.tileEntity instanceof IProfileContainer)
        {
            List<AccessUser> users = ((IProfileContainer) tileEntity).getAccessProfile().getUsers();
            int nextID = this.currentPage * this.user_list_buttons.length;
            boolean disabled = false;
            for (int i = 0; i < loadedUsers.length; i++)
            {
                loadedUsers[i] = null;
                if (users != null && (nextID + i) < users.size() && users.get(nextID + i) != null)
                {
                    AccessUser user = users.get(nextID + i);
                    loadedUsers[i] = user;
                    this.user_list_buttons[i].displayString = (user.getGroup() != null ? "[" + user.getGroup().getName() + "]" : "") + user.getName();
                    this.user_list_buttons[i].enabled = true;
                }
                else
                {

                    if (disabled)
                    {
                        this.user_list_buttons[i].enabled = false;
                        this.user_list_buttons[i].displayString = "-----";
                    }
                    else
                    {
                        this.user_list_buttons[i].enabled = true;
                        this.user_list_buttons[i].displayString = "Add User";
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
        if (button.id == SCROLL_UP_BUTTON_ID)
        {
            this.scroll(-1);
        }
        else if (button.id == SCROLL_DOWN_BUTTON_ID)
        {
            this.scroll(1);
        }
        else if (button.id > SCROLL_DOWN_BUTTON_ID && button.id < (user_list_buttons.length + SCROLL_DOWN_BUTTON_ID + 1))
        {
            AccessUser user = this.loadedUsers[button.id - (SCROLL_DOWN_BUTTON_ID + 1)];
            if (user != null)
            {
                FMLCommonHandler.instance().showGuiScreen(new GuiEditUser(this, user));
            }
            else
            {
                FMLCommonHandler.instance().showGuiScreen(new GuiAddUser(this));
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        super.drawGuiContainerForegroundLayer(x, y);
        this.loadPage(this.currentPage);
        this.scroll(0);
    }

    @Override
    public void scroll(int amount)
    {
        setScroll(this.getScroll() + amount);
        if (this.currentPage == this.pageLimit)
        {
            this.scroll_down_button.enabled = false;
        }
        else
        {
            this.scroll_down_button.enabled = true;
        }
        if (this.currentPage == 0)
        {
            this.scroll_up_button.enabled = false;
        }
        else
        {
            this.scroll_up_button.enabled = true;
        }
    }

    @Override
    public void setScroll(int length)
    {
        this.currentPage = length;
        if (this.currentPage > this.pageLimit)
        {
            this.currentPage = pageLimit;
        }
        if (this.currentPage < 0)
        {
            this.currentPage = 0;
        }
    }

    @Override
    public int getScroll()
    {
        return this.currentPage;
    }
}
