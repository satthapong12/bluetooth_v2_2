package com.example.bluetooth_v2_2

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private var bluetoothServerSocket: BluetoothServerSocket? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        const val REQUEST_BLUETOOTH_CONNECT_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBluetoothServer()
    }

    private fun startBluetoothServer() {
        if (bluetoothAdapter == null) {
            showToast("Bluetooth is not supported on this device")
            return
        }

        if (!bluetoothAdapter!!.isEnabled) {
            showToast("Bluetooth is not enabled")
            return
        }

        try {
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                val uuid = UUID.randomUUID()
                bluetoothServerSocket = bluetoothAdapter!!.listenUsingRfcommWithServiceRecord("BluetoothExample", uuid)

                Thread {
                    try {
                        // ตรวจสอบก่อนใช้งาน
                        if (bluetoothServerSocket != null) {
                            bluetoothSocket = bluetoothServerSocket!!.accept()
                            inputStream = bluetoothSocket!!.inputStream

                            val buffer = ByteArray(1024)
                            var bytes: Int

                            while (true) {
                                bytes = inputStream!!.read(buffer)
                                if (bytes == -1) {
                                    break
                                }
                                val incomingMessage = String(buffer, 0, bytes)
                                handler.post {
                                    showToast("Received data: $incomingMessage")
                                }
                            }
                        } else {
                            showToast("Bluetooth server socket is null")
                        }
                    } catch (e: IOException) {
                        showToast("Error receiving data: ${e.message}")
                    } finally {
                        inputStream?.close()
                        bluetoothSocket?.close()
                        bluetoothServerSocket?.close()
                    }
                }.start()

            } else {
                requestPermissions(arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_CONNECT_PERMISSION)
            }
        } catch (e: IOException) {
            handler.post {
                showToast("Error starting Bluetooth server: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}