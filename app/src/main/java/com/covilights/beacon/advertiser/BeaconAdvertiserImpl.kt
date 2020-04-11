package com.covilights.beacon.advertiser

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.covilights.Constants
import com.covilights.beacon.advertiser.utils.getErrorCodeMessage
import com.covilights.toParcelUuid

class BeaconAdvertiserImpl : BeaconAdvertiser {

    private val bluetoothAdapter: BluetoothAdapter
        get() = BluetoothAdapter.getDefaultAdapter()

    private val bluetoothAdvertiser: BluetoothLeAdvertiser
        get() = bluetoothAdapter.bluetoothLeAdvertiser

    private val _state = MutableLiveData<BeaconAdvertiserState>()
    override val state: LiveData<BeaconAdvertiserState>
        get() = _state

    init {
        _state.value = BeaconAdvertiserState.IDLE
    }

    override fun start(userUuid: String, callback: BeaconAdvertiserCallback?) {

        if (_state.value == BeaconAdvertiserState.RUNNING) {
            callback?.onSuccess()
            return
        }

        if (!bluetoothAdapter.isMultipleAdvertisementSupported) {
            callback?.onError(Throwable("Multi advertisement is not supported by this phone chip!"))
            return
        }

        bluetoothAdvertiser.startAdvertising(
            getSettings(),
            getAdvertiseData(userUuid),
            getScanResponse(userUuid),
            getStartAdvertisingCallback(callback)
        )
    }

    private fun getAdvertiseData(userUuid: String): AdvertiseData {
        return AdvertiseData.Builder()
            .addManufacturerData(Constants.APPLE_MANUFACTURER_ID, Constants.getManufacturerData())
            .build()
    }

    private fun getScanResponse(userUuid: String): AdvertiseData {
        return AdvertiseData.Builder()
            .addServiceUuid(userUuid.toParcelUuid())
            .setIncludeTxPowerLevel(true)
            .build()
    }

    private fun getSettings(): AdvertiseSettings? {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
            .setConnectable(false)
            .setTimeout(0)
            .build()
    }

    private fun getStartAdvertisingCallback(callback: BeaconAdvertiserCallback?): AdvertiseCallback {
        return object : AdvertiseCallback() {

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                _state.value = BeaconAdvertiserState.RUNNING
                callback?.onSuccess()

                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                _state.value = BeaconAdvertiserState.IDLE
                callback?.onError(
                    Throwable(
                        "Advertising onStartFailure: $errorCode - ${getErrorCodeMessage(
                            errorCode
                        )}"
                    )
                )

                super.onStartFailure(errorCode)
            }
        }
    }

    override fun stop(callback: BeaconAdvertiserCallback?) {
        if (_state.value == BeaconAdvertiserState.IDLE) {
            callback?.onSuccess()
            return
        }

        bluetoothAdvertiser.stopAdvertising(getStopAdvertisingCallback(callback))
    }

    private fun getStopAdvertisingCallback(callback: BeaconAdvertiserCallback?): AdvertiseCallback {
        return object : AdvertiseCallback() {

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                _state.value = BeaconAdvertiserState.IDLE
                callback?.onSuccess()

                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                val error = getErrorCodeMessage(errorCode)

                callback?.onError(Throwable("Advertising onStopFailure: $errorCode - $error"))

                super.onStartFailure(errorCode)
            }
        }
    }
}
