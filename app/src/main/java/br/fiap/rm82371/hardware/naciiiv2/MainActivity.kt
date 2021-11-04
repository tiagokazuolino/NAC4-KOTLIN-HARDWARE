package br.fiap.rm82371.hardware.naciiiv2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.fiap.rm82371.hardware.naciiiv2.ui.main.presenter.view.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}