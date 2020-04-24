package stanic.stbansapp.view.punish

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import stanic.stbansapp.R
import stanic.stbansapp.view.MainActivity

class PunishmentInfoActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_punishmentinfo)

        val info = intent.getStringExtra("info")

        findViewById<TextView>(R.id.nickInfo).text = info!!.split("//")[0]
        findViewById<TextView>(R.id.stafferInfo).text = info.split("//")[1]
        findViewById<TextView>(R.id.typeInfo).text = info.split("//")[2]
        findViewById<TextView>(R.id.reasonInfo).text = info.split("//")[3]
        findViewById<TextView>(R.id.dateInfo).text = info.split("//")[4]
        findViewById<TextView>(R.id.timeInfo).text = info.split("//")[5]
        findViewById<TextView>(R.id.idInfo).text = info.split("//")[6]

        findViewById<TextView>(R.id.isActiveInfo).text = "Punição ${info.split("//")[7]}"
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        return super.onKeyDown(keyCode, event)
    }

    fun onClickInExit(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}