package com.builtbroken.icbm.content.ams;

import com.builtbroken.mc.api.IUpdate;

import javax.swing.*;
import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/6/2016.
 */
public class WindowAMSDebug extends JFrame implements IUpdate
{
    TileAMS tile;

    public Label yaw_label;
    public Label pitch_label;

    public WindowAMSDebug(TileAMS ams)
    {
        this.tile = ams;

        setLayout(new FlowLayout());

        yaw_label = new Label("Yaw: ");
        pitch_label = new Label("Pitch: ");

        add(yaw_label);
        add(pitch_label);
        if (tile.world().isRemote)
        {
            setTitle("CLIENT: AMS Turret Debug Window");
        }
        else
        {
            setTitle("SERVER: AMS Turret Debug Window");
        }
        setSize(250, 100);
    }

    public void open()
    {
        setVisible(true);
    }

    public void close()
    {
        setVisible(false);
    }

    @Override
    public boolean update()
    {
        yaw_label.setText(String.format("Yaw: %.2f  -> %.2f", tile.currentAim.yaw(), tile.aim.yaw()));
        pitch_label.setText(String.format("Pitch: %.2f  -> %.2f", tile.currentAim.pitch(), tile.aim.pitch()));
        return true;
    }
}
