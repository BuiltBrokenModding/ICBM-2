package icbm.gangshao;

import java.net.URL;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class SoundHandler
{
	public static final SoundHandler INSTANCE = new SoundHandler();

	public static final String[] SOUND_FILES = { "railgun.ogg", "machinegun1.ogg", "machinegun2.ogg" };

	@ForgeSubscribe
	public void loadSoundEvents(SoundLoadEvent event)
	{
		for (int i = 0; i < SOUND_FILES.length; i++)
		{
			URL url = this.getClass().getResource("/icbm/" + SOUND_FILES[i]);

			event.manager.soundPoolSounds.addSound("icbm/" + SOUND_FILES[i], url);

			if (url == null)
			{
				System.out.println("Invalid sound file: " + SOUND_FILES[i] + ".ogg");
			}
		}
	}
}