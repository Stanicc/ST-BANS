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
import java.lang.NumberFormatException

class RevokeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revoke)
    }

    fun onClickInRevokeButton(view: View) {
        val id = findViewById<EditText>(R.id.revokeIDText)

        if (id.text.isEmpty()) Toast.makeText(
            this,
            "Coloque o id da punição",
            Toast.LENGTH_SHORT
        ).show()
        else {
            try {
                ServerController().sendRevoke(
                    Integer.parseInt(id.text.toString()),
                    object : ServerController.ServerResponse {
                        override fun onResponse(body: String) {
                            val intent = Intent(this@RevokeActivity, MainActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onResponseList(punishments: List<Punishment>) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onFailure(error: String) {
                            val intent = Intent(this@RevokeActivity, ErrorActivity::class.java)
                            intent.putExtra("error", error)
                            startActivity(intent)
                        }
                    })
                Toast.makeText(this, "Punição revogada!", Toast.LENGTH_LONG).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "O id precisa ser um número", Toast.LENGTH_SHORT).show()
            }
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
