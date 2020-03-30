package com.rdt.indoorcycling

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment

class FragDevList : ListFragment() {

    private var m_menu: Menu? = null
    private val m_adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val m_dev_list: ArrayList<BluetoothDevice> = ArrayList()
    private var m_dev_list_adapter: ArrayAdapter<BluetoothDevice>? = null

    //
    // LIFECYCLE
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        m_dev_list_adapter = object : ArrayAdapter<BluetoothDevice>(activity!!, 0, m_dev_list) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val dev: BluetoothDevice = m_dev_list[position]
                var view = convertView
                if (view == null) {
                    view = activity!!.layoutInflater.inflate(R.layout.frag_dev_list, parent, false)
                }
                val name = view!!.findViewById<TextView>(R.id.v_name)
                val address = view.findViewById<TextView>(R.id.v_address)
                name.text = dev.name
                address.text = dev.address
                return view
            }
            override fun getCount(): Int {
                return m_dev_list.size
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listAdapter = null
        val header: View = activity!!.layoutInflater.inflate(R.layout.frag_dev_list_header, null, false)
        listView.addHeaderView(header, null, false)
        setEmptyText("initializing...")
        listAdapter = m_dev_list_adapter
    }

    override fun onResume() {
        super.onResume()
        if (!m_adapter.isEnabled) {
            setEmptyText("bluetooth is disabled")
        }
        refresh()
    }

    //
    // MENU
    //
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dev_list, menu)
        m_menu = menu
        menu.findItem(R.id.v_setting).isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.v_setting -> {
                val intent: Intent = Intent()
                intent.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
                startActivity(intent)
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    //
    // LIST
    //
    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        //---- kong ----
        //(activity as ActMain).hide()
        //----
        val dev: BluetoothDevice = m_dev_list[position - 1]
        MyConfig.G_BT_addr = dev.address // save the address of remote device

        val arg: Bundle = Bundle()
        arg.putString("device", dev.address)
        val f: Fragment = FragYTVList()
        f.arguments = arg
        fragmentManager!!.beginTransaction().replace(R.id.v_frag, f, "youtube").commit()
        //fragmentManager!!.beginTransaction().replace(R.id.v_frag, f, "youtube").addToBackStack(null).commit()
    }

    //
    // PRIVATE FUN
    //
    private fun refresh() {
        m_dev_list.clear()
        for (dev: BluetoothDevice in m_adapter.bondedDevices) {
            if (dev.type != BluetoothDevice.DEVICE_TYPE_LE) {
                m_dev_list.add(dev)
            }
        }
        m_dev_list.sortWith(kotlin.Comparator { x: BluetoothDevice, y: BluetoothDevice ->
            MyUtil.compare(x, y)
        })
        m_dev_list_adapter!!.notifyDataSetChanged()
    }

}

/* EOF */