package com.rdt.indoorcycling

import android.view.View

class MyConfig {

    companion object {
        val AUTO_HIDE = true
        val AUTO_HIDE_DELAY_MS = 300
        val UI_ANIM_DELAY_MS = 300
        val UI_HIDE_FLAG = (
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        val UI_SHOW_FLAG = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        val YTV_API_KEY = "AIzaSyCr3G4HDb2AJ0De3i9b1s8xizWaXcZwhn0"
        val YTV_MAX_RESULT = 50

        var G_BT_addr: String? = null
    }


}

/* EOF */