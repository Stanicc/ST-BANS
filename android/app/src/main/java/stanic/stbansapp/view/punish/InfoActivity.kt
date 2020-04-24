package stanic.stbansapp.view.punish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import stanic.stbansapp.R
import stanic.stbansapp.app.controller.ServerController
import stanic.stbansapp.app.model.Punishment
import stanic.stbansapp.view.ErrorActivity
import stanic.stbansapp.view.MainActivity
import java.util.*

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        return super.onKeyDown(keyCode, event)
    }

    fun onClickInShowInfo(view: View) {
        val punishment = findViewById<EditText>(R.id.putIDText)

        if (punishment.text.isEmpty()) Toast.makeText(
            this,
            "Coloque o id ou nick da punição",
            Toast.LENGTH_SHORT
        ).show()
        else {
            try {
                val id = Integer.parseInt(punishment.text.toString())
                ServerController().getPunish(
                    id,
                    "normal",
                    object : ServerController.ServerResponse {
                        override fun onResponse(body: String) {
                            val intent =
                                Intent(
                                    this@InfoActivity,
                                    PunishmentInfoActivity::class.java
                                )
                            intent.putExtra("info", body)
                            intent.putExtra("class", "1")
                            startActivity(intent)
                        }

                        override fun onResponseList(punishments: List<Punishment>) {
                        }

                        override fun onFailure(error: String) {
                            if (error == "404") {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@InfoActivity,
                                        "Punição não encontrada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val intent = Intent(this@InfoActivity, ErrorActivity::class.java)
                                intent.putExtra("error", error)
                                startActivity(intent)
                            }
                        }
                    })
            } catch (e: NumberFormatException) {
                ServerController().getPunish(
                    punishment,
                    "list",
                    object : ServerController.ServerResponse {
                        override fun onResponse(body: String) {
                        }

                        override fun onResponseList(punishments: List<Punishment>) {
                            val list = ArrayList<String>()

                            punishments.forEach {
                                list.add("${it.nick}//${it.staff}//${it.type}//${it.reason}//${it.date}//${it.hour}//${it.time}//${it.id}//${if (it.isActive) "Ativa" else "Expirada"}")
                            }

                            val intent = Intent(
                                this@InfoActivity,
                                PunishmentsListActivity::class.java
                            )
                            intent.putExtra("info", list)
                            startActivity(intent)
                        }

                        override fun onFailure(error: String) {
                            if (error == "404") {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@InfoActivity,
                                        "Punição não encontrada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val intent = Intent(this@InfoActivity, ErrorActivity::class.java)
                                intent.putExtra("error", error)
                                startActivity(intent)
                            }
                        }
                    })
            }
        }
    }

}