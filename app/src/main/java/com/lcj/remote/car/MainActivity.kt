package com.lcj.remote.car

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.lcj.remote.car.databinding.ActivityMainBinding
import com.lcj.remote.car.fragment.HttpControlFragment

class MainActivity : AppCompatActivity() {
    private val LOG_TAG = javaClass.simpleName

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showHttpControl()
    }

    private fun showHttpControl() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<HttpControlFragment>(R.id.main, args = bundleOf())
        }
    }
}