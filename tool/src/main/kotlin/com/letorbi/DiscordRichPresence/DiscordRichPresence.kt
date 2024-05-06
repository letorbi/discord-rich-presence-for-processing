package com.letorbi.DiscordRichPresence

import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import processing.app.Base
import processing.app.Sketch
import processing.app.SketchCode
import processing.app.tools.Tool
import processing.app.ui.Editor

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.LogLevel
import de.jcm.discordgamesdk.activity.Activity

class DiscordRichPresence : Tool {
    private var core: Core? = null
    private var mode: String? = null
    private var name: String? = null
    private var file: String? = null
    private val start = Instant.now()
    private var ticks = 0

    override fun init(base: Base) {
        val params = CreateParams()
        params.clientID = 1050726275544789022L
        params.flags = CreateParams.getDefaultFlags()

        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        executor.scheduleAtFixedRate({
            try {
                ticks %= 5
                if (core == null && ticks == 0) {
                    core = Core(params)
                    core!!.setLogHook(LogLevel.INFO, Core.DEFAULT_LOG_HOOK)
                    updateRichPresence()
                } else if (core != null) {
                    if (updateValues(base)) updateRichPresence()
                    core!!.runCallbacks()
                    if (!core!!.isDiscordRunning())
                        throw Exception("Discord is not running")
                }
            } catch (e: Exception) {
                if (core != null) {
                    core!!.close()
                    core = null
                }
            } finally {
                ticks++
            }
        }, 0, 1, TimeUnit.SECONDS)
    }

    override fun run() {
        println("Discord Rich Presence 1.1.0")
        println("Sorry, no preferences, yet.")
    }

    override fun getMenuTitle(): String {
        return "Discord Rich Presence"
    }

    private fun updateValues(base: Base): Boolean {
        var hasChanged = false
        val editor: Editor? = base.activeEditor
        if (editor != null) {
            val m = editor.mode.title
            if (m != mode) {
                mode = m
                hasChanged = true
            }
            val sketch: Sketch? = editor.sketch
            if (sketch != null) {
                val n = sketch.name
                if (n != name) {
                    name = n
                    hasChanged = true
                }
                val code: SketchCode? = sketch.currentCode
                if (code != null) {
                    val f = code.fileName
                    if (f != file) {
                        file = f
                        hasChanged = true
                    }
                } else {
                    file = ""
                    hasChanged = true
                }
            } else {
                name = ""
                file = ""
                hasChanged = true
            }
        } else {
            mode = ""
            name = ""
            file = ""
            hasChanged = true
        }
        return hasChanged
    }

    private fun updateRichPresence() {
        val activity = Activity()
        activity.details = "Creating $mode code."
        activity.timestamps().start = start
        activity.assets().largeImage = "logo"
        core!!.activityManager().updateActivity(activity)
    }
}
