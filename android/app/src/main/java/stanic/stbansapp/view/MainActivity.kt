package stanic.stbansapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import stanic.stbansapp.R
import stanic.stbansapp.app.MainApp
import stanic.stbansapp.app.controller.ServerController
import stanic.stbansapp.app.model.Punishment
import stanic.stbansapp.view.punish.InfoActivity
import stanic.stbansapp.view.punish.PunishActivity
import stanic.stbansapp.view.punish.PunishmentsListActivity
import stanic.stbansapp.view.punish.RevokeActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickInPunish(view: View) {
        if (MainApp.instance.user[0].permission in 2..3) {
            val intent = Intent(this, PunishActivity::class.java)
            startActivity(intent)
        } else Toast.makeText(this, "Você não tem permissão para isso", Toast.LENGTH_SHORT).show()
    }

    fun onClickInRevoke(view: View) {
        if (MainApp.instance.user[0].permission == 3) {
            val intent = Intent(this, RevokeActivity::class.java)
            startActivity(intent)
        } else Toast.makeText(this, "Você não tem permissão para isso", Toast.LENGTH_SHORT).show()
    }

    fun onClickInInfo(view: View) {
        val intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
    }

    fun onClickInList(view: View) {
        Toast.makeText(this, "Aguarde, estamos coletandos as informações...", Toast.LENGTH_SHORT)
            .show()

        ServerController().getPunishmentsList(
            object : ServerController.ServerResponse {
                override fun onResponse(body: String) {
                }

                override fun onResponseList(punishments: List<Punishment>) {
                    val list = ArrayList<String>()

                    punishments.forEach {
                        list.add("${it.nick}//${it.staff}//${it.type}//${it.reason}//${it.date}//${it.hour}//${it.time}//${it.id}//${if (it.isActive) "Ativa" else "Expirada"}")
                    }

                    runOnUiThread {
                        val intent = Intent(
                            this@MainActivity,
                            PunishmentsListActivity::class.java
                        )
                        intent.putExtra("info", list)
                        intent.putExtra("class", "1")
                        startActivity(intent)
                    }
                }

                override fun onFailure(error: String) {
                    val intent = Intent(this@MainActivity, ErrorActivity::class.java)
                    intent.putExtra("error", error)
                    startActivity(intent)
                }
            })
    }

    fun onClickInSocials(view: View) {
        when (view.id) {
            findViewById<ImageButton>(R.id.twitterButton).id -> ""
            findViewById<ImageButton>(R.id.githubButton).id -> ""
        }
    }

}