package DiscordRichPresence;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Locale;

import processing.app.Base;
import processing.app.Mode;
import processing.app.Sketch;
import processing.app.SketchCode;
import processing.app.tools.Tool;
import processing.app.ui.Editor;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;

public class DiscordRichPresence implements Tool {
    Core core;
    String mode;
    String name;
    String file;
    Instant start = Instant.now();

    public void init(Base base) {
        try {
            //System.out.println("Initializing Discord Games SDK...");
            File discordLibrary = loadDiscordLibrary();
            if (discordLibrary == null) {
                throw new IOException("Error downloading Discord SDK.");
            }
            Core.init(discordLibrary);
            CreateParams params = new CreateParams();
            params.setClientID(1050726275544789022L);
            params.setFlags(CreateParams.getDefaultFlags());
            core = new Core(params);

            //System.out.println("Schedule rich presence updates...");
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(() -> {
                if (updateValues(base)) {
                    updateRichPresence();
                }
                core.runCallbacks();
            }, 0, 3, TimeUnit.SECONDS);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Hello from Discord Rich Presence :)");
        System.out.println("Sorry, no preferences, yet.");
    }

    public String getMenuTitle() {
        return "Discord Rich Presence";
    }

    private boolean updateValues(Base base) {
        boolean hasChanged = false;
        Editor editor = base.getActiveEditor();
        if (editor != null) {
            String m = editor.getMode().getTitle();
            if (!m.equals(mode)) {
                mode = m;
                hasChanged = true;
            }
            Sketch sketch = editor.getSketch();
            if (sketch != null) {
                String n = sketch.getName();
                if (!n.equals(name)) {
                    name = n;
                    hasChanged = true;
                }
                SketchCode code = sketch.getCurrentCode();
                if (code != null) {
                    String f = code.getFileName();
                    if (!f.equals(file)) {
                        file = f;
                        hasChanged = true;
                    }
                }
                else {
                    file = "";
                    hasChanged = true;
                }
            }
            else {
                name = "";
                file = "";
                hasChanged = true;
            }
        }
        else {
            mode = "";
            name = "";
            file = "";
            hasChanged = true;
        }
        return hasChanged;
    }

    private void updateRichPresence() {
        System.out.println("Updating Discord rich presence...");
        /*System.out.println("Mode: " + mode);
        System.out.println("Sketch: " + name);
        System.out.println("File: " + file);*/

        Activity activity = new Activity();
        activity.setDetails("Creating " + mode + " code.");
        activity.setState("Sketch: " + name);
        activity.timestamps().setStart(start);
        activity.assets().setLargeImage("logo");
        core.activityManager().updateActivity(activity);
    }

    // Based on: https://github.com/JnCrMx/discord-game-sdk4j/blob/master/examples/DownloadNativeLibrary.java
    private File loadDiscordLibrary() throws IOException {
		String name = "discord_game_sdk";
		String suffix;
		String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

		if(osName.contains("windows")) {
			suffix = ".dll";
		}
		else if(osName.contains("linux")) {
			suffix = ".so";
		}
		else if(osName.contains("mac os")) {
			suffix = ".dylib";
		}
		else {
			throw new RuntimeException("cannot determine OS type: "+osName);
		}
		// Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. Mac OS).
		if(arch.equals("amd64"))
			arch = "x86_64";

		String libPath = "lib/"+arch+"/"+name+suffix;
	    InputStream in = getClass().getResourceAsStream("/" + libPath);
        // We need to ceate a temp directory, because we may not change the filename on Windows
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-"+name+System.nanoTime());
        if(!tempDir.mkdir())
            throw new IOException("Cannot create temporary directory");
        tempDir.deleteOnExit();
        File temp = new File(tempDir, name+suffix);
        temp.deleteOnExit();
        Files.copy(in, temp.toPath());
        in.close();
        return temp;
	}
}
