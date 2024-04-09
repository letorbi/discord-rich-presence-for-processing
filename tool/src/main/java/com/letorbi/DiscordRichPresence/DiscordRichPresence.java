package com.letorbi.DiscordRichPresence;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import processing.app.Base;
import processing.app.Mode;
import processing.app.Sketch;
import processing.app.SketchCode;
import processing.app.tools.Tool;
import processing.app.ui.Editor;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.Activity;

public class DiscordRichPresence implements Tool {
    Core core;
    String mode;
    String name;
    String file;
    Instant start = Instant.now();
    int ticks = 0;

    public void init(Base base) {
        CreateParams params = new CreateParams();
        params.setClientID(1050726275544789022L);
        params.setFlags(CreateParams.getDefaultFlags());

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            try {
                ticks = ticks % 5;
                //System.out.println("Discord loop tick: " + ticks);
                if (core == null && ticks == 0) {
                    //System.out.println("Creating Discord core...");
                    core = new Core(params);
                    core.setLogHook(LogLevel.INFO, Core.DEFAULT_LOG_HOOK);
                    updateRichPresence();
                }
                else if (core != null) {
                    if (updateValues(base))
                        updateRichPresence();
                    core.runCallbacks();
                    core.isDiscordRunning();
                }
            }
            catch (Exception e) {
                //System.out.println("Discord exception: " + e);
                if (core != null) {
                    core.close();
                    core = null;
                }
            }
            finally {
                ticks++;
            }
        }, 0, 1, TimeUnit.SECONDS);
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
        /*System.out.println("Updating Discord rich presence...");
        System.out.println("Mode: " + mode);
        System.out.println("Sketch: " + name);
        System.out.println("File: " + file);*/

        Activity activity = new Activity();
        activity.setDetails("Creating " + mode + " code.");
        //activity.setState("Sketch: " + name);
        activity.timestamps().setStart(start);
        activity.assets().setLargeImage("logo");
        core.activityManager().updateActivity(activity);
    }
}
