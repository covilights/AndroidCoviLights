package com.covilights.beacon.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.covilights.beacon.BeaconCallback

internal class BluetoothManagerImpl(context: Context) : BluetoothManager {

    private val _isEnabled = MutableLiveData<Boolean>()
    override val isEnabled: LiveData<Boolean>
        get() = _isEnabled

    private val bluetoothAdapter: BluetoothAdapter?
        get() = BluetoothAdapter.getDefaultAdapter()

    init {
        _isEnabled.value = isBluetoothEnabled()
    }

    private val bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_OFF -> {
                        _isEnabled.value = false
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                    }
                    BluetoothAdapter.STATE_ON -> {
                        callback?.onSuccess()
                        _isEnabled.value = true
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

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled ?: false
    }

    private var callback: BeaconCallback? = null

    override fun enableBluetooth(callback: BeaconCallback?) {

        if (isBluetoothEnabled()) {
            callback?.onSuccess()
            return
        }

        this.callback = callback

        val enabled = bluetoothAdapter?.enable() ?: false

        if (!enabled) {
            callback?.onError(Throwable("Unable to enable Bluetooth!"))
            this.callback = null
        }
    }
}
