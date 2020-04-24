package stanic.stbansapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import stanic.stbansapp.R
import stanic.stbansapp.app.MainApp
import java.util.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onClickInLogin(view: View) {
        val address = findViewById<EditText>(R.id.addressText)
        val token = findViewById<EditText>(R.id.tokenText)

        if (address.text.isEmpty() || token.text.isEmpty()) Toast.makeText(
            this,
            "Todos os campos devem ser preenchidos",
            Toast.LENGTH_SHORT
        ).show()
        else {
            MainApp().login(address.text.toString(), token.text.toString())

            Toast.makeText(this, "Aguarde alguns instantes...", Toast.LENGTH_SHORT).show()

            GlobalScope.launch {
                delay(2000)
                if (MainApp.instance.user[0].login) {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Autenticação completa!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Token ou ip incorreto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

}