package com.example.bluetooth_v2_2


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.bluetooth_v2_2.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "com.example.bluetooth.EXTRA_DATA"
    }

    private val gattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_DATA_AVAILABLE -> {
                    val receivedData = intent.getStringExtra(EXTRA_DATA)
                    updateReceivedData(receivedData)
                }
            }
        }
    }

    private fun updateReceivedData(data: String?) {
        data?.let {
            val message = "Received Data: $it"
            binding.receivedDataTextView.text = message
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register BroadcastReceiver to receive data updates
        val filter = IntentFilter(ACTION_DATA_AVAILABLE)
        registerReceiver(gattUpdateReceiver, filter)

    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(gattUpdateReceiver)
    }
}