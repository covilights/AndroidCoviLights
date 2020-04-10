package com.covilights.beacon

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BluetoothStateManager(context: Context) {

    private val bluetoothAdapter: BluetoothAdapter
        get() = BluetoothAdapter.getDefaultAdapter()

    private val bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                    }
                    BluetoothAdapter.STATE_ON -> {
                        listener?.onBluetoothTurnedOn()
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                    }
                }
            }
        }
    }

    init {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothReceiver, filter)
    }

    fun getBluetoothStatus(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    private var listener: OnBluetoothStatusListener? = null

    fun enableBluetooth(listener: OnBluetoothStatusListener) {

        val isEnabled = bluetoothAdapter.isEnabled

        if (!isEnabled) {
            this.listener = listener
            val successful = bluetoothAdapter.enable()

            if (!successful) {
                listener.onError(Throwable("Unable to enable Bluetooth!"))
                this.listener = null
            }
        } else {
            listener.onBluetoothTurnedOn()
        }
    }

    interface OnBluetoothStatusListener {

        fun onBluetoothTurnedOn()

        fun onError(throwable: Throwable)
    }
}