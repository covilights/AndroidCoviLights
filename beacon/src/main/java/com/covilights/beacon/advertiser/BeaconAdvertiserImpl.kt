package com.covilights.beacon.advertiser

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.AdvertisingSetCallback
import android.bluetooth.le.BluetoothLeAdvertiser
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.covilights.beacon.BeaconCallback
import com.covilights.beacon.BeaconState
import com.covilights.beacon.config.BeaconConfigProvider
import com.covilights.beacon.utils.getManufacturerData
import com.covilights.beacon.utils.toParcelUuid

internal class BeaconAdvertiserImpl(private val config: BeaconConfigProvider) : BeaconAdvertiser {

    private val bluetoothAdapter: BluetoothAdapter
        get() = BluetoothAdapter.getDefaultAdapter()

    private val bluetoothAdvertiser: BluetoothLeAdvertiser
        get() = bluetoothAdapter.bluetoothLeAdvertiser

    private val _state = MutableLiveData<BeaconState>()
    override val state: LiveData<BeaconState>
        get() = _state

    init {
        _state.value = BeaconState.STOPPED
    }

    override fun start(callback: BeaconCallback?) {

        if (_state.value == BeaconState.STARTED) {
            callback?.onSuccess()
            return
        }

        if (!bluetoothAdapter.isMultipleAdvertisementSupported) {
            callback?.onError(Throwable("Multi advertisement is not supported by this phone chip!"))
            return
        }
        this.callback = callback

        bluetoothAdvertiser.startAdvertising(
            getSettings(),
            getAdvertiseData(),
            getScanResponse(config.userUuid),
            advertiseCallback
        )
    }

    private fun getAdvertiseData(): AdvertiseData {
        return AdvertiseData.Builder()
            .addManufacturerData(config.manufacturerId, getManufacturerData(config.manufacturerUuid))
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

    private var callback: BeaconCallback? = null

    private val advertiseCallback = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            _state.value = BeaconState.STARTED
            callback?.onSuccess()

            super.onStartSuccess(settingsInEffect)
        }

        override fun onStartFailure(errorCode: Int) {
            _state.value = BeaconState.STOPPED
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

    override fun stop(callback: BeaconCallback?) {
        if (_state.value == BeaconState.STOPPED) {
            callback?.onSuccess()
            return
        }

        bluetoothAdvertiser.stopAdvertising(advertiseCallback)

        _state.value = BeaconState.STOPPED
        callback?.onSuccess()
    }

    private fun getErrorCodeMessage(errorCode: Int): String {
        return when (errorCode) {
            AdvertisingSetCallback.ADVERTISE_SUCCESS -> "The requested advertising was successful."
            AdvertisingSetCallback.ADVERTISE_FAILED_DATA_TOO_LARGE -> "Failed to start advertising as the advertise data to be broadcasted is too large."
            AdvertisingSetCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "Failed to start advertising because no advertising instance is available."
            AdvertisingSetCallback.ADVERTISE_FAILED_ALREADY_STARTED -> "Failed to start advertising as the advertising is already started."
            AdvertisingSetCallback.ADVERTISE_FAILED_INTERNAL_ERROR -> "Operation failed due to an internal error."
            AdvertisingSetCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "This feature is not supported on this platform."
            else -> "Unknown error!"
        }
    }
}
