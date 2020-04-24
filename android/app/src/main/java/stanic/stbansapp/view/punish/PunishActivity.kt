package stanic.stbansapp.view.punish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import stanic.stbansapp.app.controller.*
import android.widget.Toast
import stanic.stbansapp.R
import stanic.stbansapp.app.model.Punishment
import stanic.stbansapp.view.ErrorActivity
import stanic.stbansapp.view.MainActivity
import java.util.*

class PunishActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_punish)

        findViewById<EditText>(R.id.timeText).visibility = View.GONE
        findViewById<EditText>(R.id.timeUnitText).visibility = View.GONE

        onClickInTime()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun onClickInTime() {
        findViewById<Switch>(R.id.time).setOnCheckedChangeListener { _, checked ->
            val nick =
                findViewById<EditText>(R.id.nickText).layoutParams as ConstraintLayout.LayoutParams
            if (!checked) {
                findViewById<EditText>(R.id.timeText).visibility = View.GONE
                findViewById<EditText>(R.id.timeUnitText).visibility = View.GONE

                nick.setMargins(nick.leftMargin, nick.topMargin, nick.rightMargin, 250)
            } else {
                findViewById<EditText>(R.id.timeText).visibility = View.VISIBLE
                findViewById<EditText>(R.id.timeUnitText).visibility = View.VISIBLE

                nick.setMargins(nick.leftMargin, nick.topMargin, nick.rightMargin, 370)
            }
        }
    }

    fun onClickInApply(view: View) {
        val time = findViewById<Switch>(R.id.time)

        val nick = findViewById<EditText>(R.id.nickText)
        val staffer = findViewById<EditText>(R.id.stafferText)
        val reason = findViewById<EditText>(R.id.reasonText)
        val timeToPunish = findViewById<EditText>(R.id.timeText)
        val timeUnit = findViewById<EditText>(R.id.timeUnitText)

        val banType = findViewById<RadioButton>(R.id.banButton)
        val muteType = findViewById<RadioButton>(R.id.muteButton)

        var id = 0
        val controller = ServerController()

        if (time.isChecked) {
            if (nick.text.isEmpty() || staffer.text.isEmpty() || reason.text.isEmpty() || timeToPunish.text.isEmpty() || timeUnit.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Todos os campos devem ser preenchidos",
                    Toast.LENGTH_SHORT
                ).show()
                return
            } else {
                when {
                    banType.isChecked -> runOnUiThread {
                        controller.sendTempPunish(
                            nick.text.toString(),
                            staffer.text.toString(),
                            "${timeToPunish.text}".toInt(),
                            timeUnit.text.toString(),
                            reason.text.toString(),
                            "TempBan",
                            object : ServerController.ServerResponse {
                                override fun onFailure(error: String) {
                                    val intent =
                                        Intent(this@PunishActivity, ErrorActivity::class.java)
                                    intent.putExtra("error", error)
                                    startActivity(intent)
                                }

                                override fun onResponseList(punishments: List<Punishment>) {
                                }

                                override fun onResponse(body: String) {
                                    id = body.toInt()
                                }
                            }
                        )
                    }
                    muteType.isChecked -> runOnUiThread {
                        controller.sendTempPunish(
                            nick.text.toString(),
                            staffer.text.toString(),
                            "${timeToPunish.text}".toInt(),
                            timeUnit.text.toString(),
                            reason.text.toString(),
                            "TempMute",
                            object : ServerController.ServerResponse {
                                override fun onFailure(error: String) {
                                    val intent =
                                        Intent(this@PunishActivity, ErrorActivity::class.java)
                                    intent.putExtra("error", error)
                                    startActivity(intent)
                                }

                                override fun onResponseList(punishments: List<Punishment>) {
                                }

                                override fun onResponse(body: String) {
                                    id = body.toInt()
                                }
                            }
                        )
                    }
                    else -> {
                        Toast.makeText(this, "Escolha o tipo da punição!", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                }
            }

        } else {
            if (nick.text.isEmpty() || staffer.text.isEmpty() || reason.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Todos os campos devem ser preenchidos",
                    Toast.LENGTH_SHORT
                ).show()
                return
            } else {
                when {
                    banType.isChecked -> runOnUiThread {
                        controller.sendPunish(
                            nick.text.toString(),
                            staffer.text.toString(),
                            reason.text.toString(),
                            "Ban",
                            object : ServerController.ServerResponse {
                                override fun onFailure(error: String) {
                                    val intent =
                                        Intent(this@PunishActivity, ErrorActivity::class.java)
                                    intent.putExtra("error", error)
                                    startActivity(intent)
                                }

                                override fun onResponseList(punishments: List<Punishment>) {
                                }

                                override fun onResponse(body: String) {
                                    id = body.toInt()
                                }
                            }
                        )
                    }
                    muteType.isChecked -> runOnUiThread {
                        controller.sendPunish(
                            nick.text.toString(),
                            staffer.text.toString(),
                            reason.text.toString(),
                            "Mute",
                            object : ServerController.ServerResponse {
                                override fun onFailure(error: String) {
                                    val intent =
                                        Intent(this@PunishActivity, ErrorActivity::class.java)
                                    intent.putExtra("error", error)
                                    startActivity(intent)
                                }

                                override fun onResponseList(punishments: List<Punishment>) {
                                }

                                override fun onResponse(body: String) {
                                    id = body.toInt()
                                }
                            }
                        )
                    }
                    else -> {
                        Toast.makeText(this, "Escolha o tipo da punição!", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                }
            }

        }
        controller.getPunish(id, "normal", object : ServerController.ServerResponse {
            override fun onResponse(body: String) {
                runOnUiThread {
                    val intent =
                        Intent(this@PunishActivity, PunishmentInfoActivity::class.java)
                    intent.putExtra("info", body)
                    startActivity(intent)
                }
            }

            override fun onResponseList(punishments: List<Punishment>) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFailure(error: String) {
                val intent = Intent(this@PunishActivity, ErrorActivity::class.java)
                intent.putExtra("error", error)
                startActivity(intent)
            }
        })
        Toast.makeText(this, "Punição aplicada!", Toast.LENGTH_SHORT).show()
    }

}