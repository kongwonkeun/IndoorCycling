package com.rdt.indoorcycling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.frag_ytv_player.*

class FragYTVPlayer : Fragment(), YouTubePlayer.OnInitializedListener {

    private lateinit var m_vid: String
    private lateinit var m_palyer: YouTubePlayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_ytv_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(v_player)

        m_vid = arguments!!.getString("video").toString()
        v_player.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                youTubePlayer.loadVideo(m_vid, 0f)
            }
        })
    }

    override fun onDestroy() {
        v_player.release()
        super.onDestroy()
    }

    //
    //
    //
    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
        m_palyer = p1!!
        m_palyer.loadVideo(m_vid)
    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
        Toast.makeText(activity, "youtube player init failed", Toast.LENGTH_LONG).show()
    }

}

/* EOF */