package com.jesusdev.tarjetabipcompleta

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.jesusdev.tarjetabipcompleta.databinding.ActivityFullscreenBinding
import com.jesusdev.tarjetabipcompleta.viewmodel.ViewModelSaldo
import java.util.prefs.Preferences
import kotlin.time.seconds

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@Suppress("DEPRECATION")
class FullscreenActivity : AppCompatActivity() {
    private lateinit var mAdView: AdView
    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler()
    private val viewModelSaldo : ViewModelSaldo by viewModels()
    private val key = "MY_KEY"

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreenContent.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements

        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)


        //publicidad
        MobileAds.initialize(this) {}
        //Banner bajo lista de juegos
        mAdView = findViewById(R.id.adsView)
        val adRequest =AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        //animacion Lottie cargando bus
        binding.imageView.setAnimation(R.raw.bus)
        //obtener prefence manager esta deprecado
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        //put shared
        val editor =pref.edit()
        var id : String

        id = binding.numeroBip.editableText.toString()
        binding.btnEnviar.setOnClickListener {
            //aca se setea la id que se entrega al metodo getSaldoById (solicitud query a API)
            id = binding.numeroBip.editableText.toString()
            //guardando key de bip
            editor.putString(key,id)
            editor.apply()
            //hacer consulta en corutina by id
            viewModelSaldo.getSaldoById(id)

            //play a la animación Lottie, podria cambiar la animación por otra
            binding.imageView.setAnimation(R.raw.bus)
            binding.imageView.playAnimation()


        }

        binding.btnCargarBip.setOnClickListener {


            //intent to share the text
            val shareIntent = Intent()
            val url = "https://cargatubip.metro.cl/CargaTuBipV2/"
            shareIntent.action = Intent.ACTION_VIEW
            shareIntent.data = Uri.parse(url)
            startActivity(Intent.createChooser(shareIntent,"Carga Tú Bip!"))


        }
        viewModelSaldo.allTarjetabip.observe(this, {
            it?.let {


                binding.resultado.text =      ("Tu Saldo Disponible   : ${it.saldoTarjeta}")
                binding.saldoCard.text =      ("Tu Saldo Disponible   : ${it.saldoTarjeta}")
                binding.fechaConsulta.text=   ("Última Fecha de Carga : ${it.fechaSaldo}")
                binding.estadoContrato.text = ("Estado Tarjeta Bip!   : ${it.estadoContrato}")
                binding.nCodigo.text =        ("N° Tarjeta : ${it.id}")

                Log.d("saldo", it.saldoTarjeta)
            }
        })


        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.fullscreenContent



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.



    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()

        isFullscreen = true

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        fullscreenContent.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}