package icbm.core;

import java.net.URL;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class ShengYin
{
	public static final ShengYin INSTANCE = new ShengYin();

	public static final String[] SOUND_FILES = { "lasershot.ogg", "aagun.ogg", "collapse1.ogg", "collapse2.ogg", "collapse3.ogg", "collapse4.ogg", "collapse5.ogg", "collapse6.ogg", "collapse7.ogg", "collapse8.ogg", "railgun.ogg", "machinegun1.ogg", "machinegun2.ogg", "debilitation.ogg", "airstrike.ogg", "hypersonic.ogg", "missilelaunch.ogg", "emp.ogg", "sonicwave.ogg", "machinehum.ogg", "alarm.ogg", "explosion1.ogg", "explosionfire.ogg", "gasleak.ogg", "beamcharging.ogg", "powerdown.ogg", "targetlocked.ogg", "redmatter.ogg", "railgun.ogg", "antigravity.ogg", "antimatter.ogg", "laser.ogg" };

	@ForgeSubscribe
	public void loadSoundEvents(SoundLoadEvent event)
	{
		for (int i = 0; i < SOUND_FILES.length; i++)
		{
			URL url = this.getClass().getResource("/icbm/" + SOUND_FILES[i]);

			event.manager.soundPoolSounds.addSound("icbm/" + SOUND_FILES[i], url);

			if (url == null)
			{
				System.out.println("Invalid sound file: " + SOUND_FILES[i]);
			}
		}
	}
}
