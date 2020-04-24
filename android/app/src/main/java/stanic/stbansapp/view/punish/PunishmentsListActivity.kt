package stanic.stbansapp.view.punish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_punishmentslist.*
import stanic.stbansapp.R
import stanic.stbansapp.adapter.RecyclerAdapter
import stanic.stbansapp.app.controller.ServerController
import stanic.stbansapp.app.model.Punishment
import stanic.stbansapp.view.ErrorActivity
import stanic.stbansapp.view.MainActivity

class PunishmentsListActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var list: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_punishmentslist)

        findViewById<TextView>(R.id.notHasPunishments).visibility = View.GONE

        list = intent.getStringArrayListExtra("info")!!

        val punishments = ArrayList<Punishment>()

        list.forEach {
            punishments.add(
                Punishment(
                    it.split("//")[0],
                    it.split("//")[1],
                    it.split("//")[2],
                    it.split("//")[3],
                    it.split("//")[4],
                    it.split("//")[5],
                    it.split("//")[6],
                    it.split("//")[7].toInt(),
                    it.split("//")[8].toBoolean()
                )
            )
        }

        recycler = findViewById(R.id.recyclerView)

        val adapter = RecyclerAdapter(this, punishments)
        val layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        adapter.setClickListener(object : RecyclerAdapter.ButtonClickListener {
            override fun onClick(
                view: View,
                punishment: Punishment,
                position: Int,
                holder: RecyclerAdapter.ViewHolder
            ) {
                ServerController().getPunish(
                    punishment.id,
                    "normal",
                    object : ServerController.ServerResponse {
                        override fun onResponse(body: String) {
                            runOnUiThread {
                                val intent = Intent(
                                    this@PunishmentsListActivity,
                                    PunishmentInfoActivity::class.java
                                )
                                intent.putExtra("info", body)
                                startActivity(intent)
                            }
                        }

                        override fun onResponseList(punishments: List<Punishment>) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onFailure(error: String) {
                            val intent =
                                Intent(this@PunishmentsListActivity, ErrorActivity::class.java)
                            intent.putExtra("error", error)
                            startActivity(intent)
                        }
                    })
            }
        })

        if (punishments.isEmpty()) {
            findViewById<TextView>(R.id.notHasPunishments).visibility = View.VISIBLE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        return super.onKeyDown(keyCode, event)
    }

}