package com.rdt.indoorcycling

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

class BTSock : Runnable {

    private val BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var m_ctx: Context? = null
    private var m_listener: BTCallback? = null
    private var m_dev: BluetoothDevice? = null
    private var m_sock: BluetoothSocket? = null
    private var m_connected: Boolean = false

    override fun run() {
        try {
            m_sock = m_dev!!.createRfcommSocketToServiceRecord(BLUETOOTH_SPP)
            m_sock!!.connect()
            if (m_listener != null) {
                m_listener!!.on_connect()
            }
        } catch (e: Exception) {
            if (m_listener != null) {
                m_listener!!.on_connect_err(e)
            }
            if (m_sock != null) {
                m_sock!!.close()
                m_sock  = null
            }
            return
        }

        m_connected = true
        try {
            val buf = ByteArray(124)
            while (true) {
                val cnt = m_sock!!.inputStream.read(buf)
                val data = buf.copyOf(cnt)
                if (m_listener != null) {
                    m_listener!!.on_recv(data)
                }
            }
        } catch (e: Exception) {
            m_connected = false
            if (m_listener != null) {
                m_listener!!.on_io_err(e)
            }
            if (m_sock != null) {
                m_sock!!.close()
                m_sock  = null
            }
        }
    }

    //
    // PUBLIC FUN
    //
    @Throws(IOException::class)
    fun connect(ctx: Context, listener: BTCallback, dev: BluetoothDevice) {
        if (m_connected || m_sock != null) {
            throw IOException("already connected")
        }
        m_ctx = ctx
        m_listener = listener
        m_dev = dev
        Executors.newSingleThreadExecutor().submit(this) // go to run() loop
    }

    fun disconnect() {
        m_listener = null
        if (m_sock != null) {
            m_sock!!.close()
            m_sock = null
        }
    }

    @Throws(IOException::class)
    fun send(data: ByteArray) {
        if (!m_connected) {
            throw IOException("not connected")
        }
        m_sock!!.outputStream.write(data)
    }

}

/* EOF */