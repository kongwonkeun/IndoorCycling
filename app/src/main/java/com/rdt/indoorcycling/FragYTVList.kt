package com.rdt.indoorcycling

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_ytv_list.*
import org.apache.hc.client5.http.ClientProtocolException
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class FragYTVList : Fragment() {

    private lateinit var m_task: YTVTask
    private val m_ytv_list: ArrayList<YTVData> = ArrayList()
    private var m_ytv_list_adapter: ArrayAdapter<YTVData>? = null

    //
    // LIFECYCLE
    //
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_ytv_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        v_search.setOnClickListener {
            m_task = YTVTask()
            m_task.execute()
        }
    }

    //
    //
    //
    @Throws(JSONException::class)
    fun parse_json_data(json: JSONObject) {
        val contacts = json.getJSONArray("items")

        for (i in 0 until contacts.length()) {
            val c = contacts.getJSONObject(i)
            val kind = c.getJSONObject("id").getString("kind")
            val vid = if (kind == "youtube#video")
                c.getJSONObject("id").getString("videoId")
            else
                c.getJSONObject("id").getString("playlistId")
            val title = c.getJSONObject("snippet").getString("title")
            var title_ = ""
            try { title_ = String(title.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8) }
            catch (e: UnsupportedEncodingException) {}
            val date = c.getJSONObject("snippet").getString("publishedAt").substring(0, 10)
            var date_ = ""
            try { date_ = String(date.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8) }
            catch (e: UnsupportedEncodingException) {}
            val thumbnail = c.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url")
            m_ytv_list.add(YTVData(vid, title_, thumbnail, date_))
        }
    }

    fun search_ytv_by_keyword(): JSONObject {
        val word = v_word.text.toString()
        val word_ = URLEncoder.encode(word, "UTF-8")
        val search_word = word_.replace(" ", "%20")
        val url_base = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
        val url_key = "&key="
        val url_x = "&maxResults=50"
        val http_get = HttpGet(url_base + search_word + url_key + MyConfig.YTV_API_KEY + url_x)
        val http_client = HttpClientBuilder.create().build()
        val str_builder = StringBuilder()

        try {
            val http_res = http_client.execute(http_get)
            val entity = http_res.entity
            val stream = entity.content
            while (true) {
                val b = stream.read()
                if (b == -1)
                    break
                str_builder.append(b.toChar())
            }
        }
        catch (e: ClientProtocolException) {}
        catch (e: IOException) {}

        var json = JSONObject()
        try {
            json = JSONObject(str_builder.toString())
        }
        catch (e: JSONException) {}

        return json
    }

    //
    //
    //
    inner class YTVData(val vid: String, val title: String, val thumbnail: String, val date: String) {}

    inner class YTVListAdapter(ctx: Context, rid: Int, var items: ArrayList<YTVData>) : ArrayAdapter<YTVData>(ctx, rid, items) {

        private var data: YTVData? = null
        init {}

        override fun getCount(): Int {
            return m_ytv_list.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            data = items[position]

            if (view == null) {
                view = activity!!.layoutInflater.inflate(R.layout.frag_ytv_list_item, parent, false)
            }

            val url = data!!.thumbnail
            val url_s = url.substring(0, url.lastIndexOf("/") + 1)
            var url_e = url.substring(url.lastIndexOf("/") + 1, url.length)

            try {
                url_e = URLEncoder.encode(url_e, "UTF-8").replace("+", "%20")
            }
            catch (e: UnsupportedEncodingException) {}

            val url_ = url_s + url_e
            Picasso.get().load(url_).into(view!!.findViewById<ImageView>(R.id.v_img))
            (view.findViewById<TextView>(R.id.v_date)).text = data!!.date
            (view.findViewById<TextView>(R.id.v_title)).text = data!!.title

            view.tag = position
            view.setOnClickListener {
                //---- kong ----
                (activity as ActMain).hide()
                //----
                val pos = it.tag
                val arg: Bundle = Bundle()
                arg.putString("video", items[pos as Int].vid)
                val f: Fragment = FragYTVPlayer()
                f.arguments = arg
                fragmentManager!!.beginTransaction().replace(R.id.v_frag, f, "player").addToBackStack(null).commit()
            }
            return view
        }

    }

    inner class YTVTask() : AsyncTask<Void, Void, String?>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): String? {
            try {
                parse_json_data(search_ytv_by_keyword())
            }
            catch (e: JSONException) {}
            return null
        }

        override fun onPostExecute(result: String?) {
            m_ytv_list_adapter = YTVListAdapter(context!!, R.layout.frag_ytv_list_item, m_ytv_list)
            v_list.adapter = m_ytv_list_adapter
            val s = m_ytv_list.size
            Log.d("YTV", "---- SIZE=$s ----")
            m_ytv_list_adapter!!.notifyDataSetChanged()
        }

    }

}

/* EOF */