package com.lcj.remote.car

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.lcj.remote.car.databinding.ActivityBluetoothBinding
import com.lcj.remote.car.model.ControlResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothActivity : AppCompatActivity() {
    private val LOG_TAG = javaClass.simpleName

    private lateinit var binding: ActivityBluetoothBinding

    private val bluetoothAdapter: BluetoothAdapter? by lazy { (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter }
    private var bluetoothSocket: BluetoothSocket? = null

    // 3B: B8:27:EB:F2:79:AC
    // Zero 2W: B8:27:EB:D0:A5:BF
    private val serverDeviceAddress = "B8:27:EB:D0:A5:BF"
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val enableBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBluetoothBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        with(binding.forwardButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "forwardButton")
                sendData("forward")
            }
        }

        with(binding.backwardButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "backwardButton")
                sendData("backward")
            }
        }

        with(binding.acceleratorButton) {
            isEnabled = false
            setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        Log.i(LOG_TAG, "acceleratorButton ACTION_UP")
                        sendData("stop")
                        true
                    }

                    MotionEvent.ACTION_DOWN -> {
                        Log.i(LOG_TAG, "acceleratorButton ACTION_DOWN")
                        sendData("start")
                        true
                    }
                }
                return@setOnTouchListener false
            }
        }

        with(binding.frontLightOnButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "front_light_on")
                sendData("front_light_on")
            }
        }

        with(binding.backLightOnButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "back_light_on")
                sendData("back_light_on")
            }
        }

        with(binding.lightLeftBlinkButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "light_left_blink")
                sendData("light_left_blink")
            }
        }

        with(binding.lightRightBlinkButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "light_right_blink")
                sendData("light_right_blink")
            }
        }

        with(binding.lightBlinkButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "light_blink")
                sendData("light_blink")
            }
        }

        with(binding.lightOffButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "light_off")
                sendData("light_off")
            }
        }

        with(binding.musicButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "lightOffButton")
                sendData("speaker_music")
            }
        }

        with(binding.hornButton) {
            isEnabled = false
            setOnClickListener {
                Log.i(LOG_TAG, "lightOffButton")
                sendData("speaker_horn")
            }
        }

        with(binding.disconnectButton) {
            isEnabled = false
            setOnClickListener {
                closeConnection()
            }
        }

        with(binding.connectButton) {
            setOnClickListener {
                connectToServer()
            }
        }
    }

    private fun connectToServer() {
        Log.i(LOG_TAG, "connectToServer")
        bluetoothAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
                Toast.makeText(this, "請開啟藍牙", Toast.LENGTH_SHORT).show()
                return@let
            }

            val device: BluetoothDevice? = adapter.getRemoteDevice(serverDeviceAddress)

            device?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // 使用 UUID 連接伺服器
                        if (ActivityCompat.checkSelfPermission(this@BluetoothActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return@launch
                        }

                        runOnUiThread {
                            binding.connectButton.isEnabled = false
                        }

                        bluetoothSocket = it.createRfcommSocketToServiceRecord(uuid)
                        bluetoothSocket?.connect()

                        runOnUiThread {
                            binding.forwardButton.isEnabled = true
                            binding.backwardButton.isEnabled = true
                            binding.acceleratorButton.isEnabled = true
                            binding.frontLightOnButton.isEnabled = true
                            binding.backLightOnButton.isEnabled = true
                            binding.lightLeftBlinkButton.isEnabled = true
                            binding.lightRightBlinkButton.isEnabled = true
                            binding.lightBlinkButton.isEnabled = true
                            binding.lightOffButton.isEnabled = true
                            binding.musicButton.isEnabled = true
                            binding.hornButton.isEnabled = true
                            binding.disconnectButton.isEnabled = true
                        }

                        receiveData()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        closeConnection()
                        runOnUiThread { Toast.makeText(this@BluetoothActivity, "請檢查裝置是否開啟", Toast.LENGTH_SHORT).show() }
                    }
                }
            }
        }
    }

    private fun sendData(data: String) {
        Log.i(LOG_TAG, "sendData")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputStream: OutputStream? = bluetoothSocket?.outputStream
                outputStream?.write(data.toByteArray())
                receiveData()
            } catch (e: IOException) {
                e.printStackTrace()
                closeConnection()
            }
        }
    }

    private fun receiveData() {
        Log.i(LOG_TAG, "receiveData")
        try {
            val inputStream: InputStream? = bluetoothSocket?.inputStream
            val buffer = ByteArray(1024)
            val bytesRead: Int = inputStream?.read(buffer) ?: -1
            if (bytesRead != -1) {
                val receivedMessage = String(buffer, 0, bytesRead)
                val response = Gson().fromJson(receivedMessage, ControlResponse::class.java)
                Log.d(LOG_TAG, "Received: $receivedMessage")
                Log.d(LOG_TAG, "response.code: ${response.code}")
                Log.d(LOG_TAG, "response.direction: ${response.direction}")
                Log.d(LOG_TAG, "response.light: ${response.light}")
                Log.d(LOG_TAG, "response.message: ${response.message}")

                runOnUiThread {
                    when (response.direction) {
                        1 -> {
                            binding.forwardButton.isEnabled = false
                            binding.backwardButton.isEnabled = true
                        }

                        2 -> {
                            binding.forwardButton.isEnabled = true
                            binding.backwardButton.isEnabled = false
                        }
                    }

                    when (response.light) {
                        0 -> {
                            binding.frontLightOnButton.isEnabled = true
                            binding.backLightOnButton.isEnabled = true
                            binding.lightLeftBlinkButton.isEnabled = true
                            binding.lightRightBlinkButton.isEnabled = true
                            binding.lightBlinkButton.isEnabled = true
                            binding.lightOffButton.isEnabled = false
                        }

                        1 -> {
                            binding.frontLightOnButton.isEnabled = false
                            binding.backLightOnButton.isEnabled = true
                            binding.lightLeftBlinkButton.isEnabled = true
                            binding.lightRightBlinkButton.isEnabled = true
                            binding.lightBlinkButton.isEnabled = true
                            binding.lightOffButton.isEnabled = true
                        }

                        2 -> {
                            binding.frontLightOnButton.isEnabled = true
                            binding.backLightOnButton.isEnabled = false
                            binding.lightLeftBlinkButton.isEnabled = true
                            binding.lightRightBlinkButton.isEnabled = true
                            binding.lightBlinkButton.isEnabled = true
                            binding.lightOffButton.isEnabled = true
                        }

                        3 -> {
                            binding.frontLightOnButton.isEnabled = false
                            binding.backLightOnButton.isEnabled = false
                            binding.lightLeftBlinkButton.isEnabled = true
                            binding.lightRightBlinkButton.isEnabled = true
                            binding.lightBlinkButton.isEnabled = true
                            binding.lightOffButton.isEnabled = true
                        }

                        4 -> {
                            binding.frontLightOnButton.isEnabled = true
                            binding.backLightOnButton.isEnabled = true
                            binding.lightLeftBlinkButton.isEnabled = false
                            binding.lightRightBlinkButton.isEnabled = true
                            binding.lightBlinkButton.isEnabled = true
                            binding.lightOffButton.isEnabled = true
                        }

                        5 -> {
                            binding.frontLightOnButton.isEnabled = true
                            binding.backLightOnButton.isEnabled = true
                            binding.lightLeftBlinkButton.isEnabled = true
                            binding.lightRightBlinkButton.isEnabled = false
                            binding.lightBlinkButton.isEnabled = true
                            binding.lightOffButton.isEnabled = true
                        }

                        6 -> {
                            binding.frontLightOnButton.isEnabled = true
                            binding.backLightOnButton.isEnabled = true
                            binding.lightLeftBlinkButton.isEnabled = true
                            binding.lightRightBlinkButton.isEnabled = true
                            binding.lightBlinkButton.isEnabled = false
                            binding.lightOffButton.isEnabled = true
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            closeConnection()
        }
    }

    private fun closeConnection() {
        Log.i(LOG_TAG, "closeConnection")
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        runOnUiThread {
            binding.forwardButton.isEnabled = false
            binding.backwardButton.isEnabled = false
            binding.acceleratorButton.isEnabled = false
            binding.frontLightOnButton.isEnabled = false
            binding.backLightOnButton.isEnabled = false
            binding.lightLeftBlinkButton.isEnabled = false
            binding.lightRightBlinkButton.isEnabled = false
            binding.lightBlinkButton.isEnabled = false
            binding.lightOffButton.isEnabled = false
            binding.musicButton.isEnabled = false
            binding.hornButton.isEnabled = false
            binding.disconnectButton.isEnabled = false
            binding.connectButton.isEnabled = true
        }
    }
}