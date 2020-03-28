package com.rdt.indoorcycling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*

class ActMain : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    private val m_handler = Handler()
    private val m_hide_delayed_runnable = Runnable { v_frag.systemUiVisibility = MyConfig.UI_HIDE_FLAG }
    private val m_hide_runnable = Runnable { hide() }

    //
    // LIFECYCLE
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.addOnBackStackChangedListener(this)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.v_frag, FragDevList(), "device").commit()
        } else {
            onBackStackChanged()
        }
    }

    /*
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        m_handler.removeCallbacks(m_hide_runnable)
        m_handler.postDelayed(m_hide_runnable, MyConfig.AUTO_HIDE_DELAY_MS.toLong())
    }
    */

    //
    // FRAGMENT MANAGER
    //
    override fun onBackStackChanged() {
        supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
    }

    //
    // APP BAR
    //
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //
    // PUBLIC FUN
    //
    fun hide() {
        supportActionBar?.hide()
        m_handler.removeCallbacks(m_hide_delayed_runnable)
        m_handler.postDelayed(m_hide_delayed_runnable, MyConfig.UI_ANIM_DELAY_MS.toLong())
    }

}

/* EOF */
