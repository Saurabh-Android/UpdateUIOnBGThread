package com.android.threading

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.threading.R

class MainActivity : AppCompatActivity() {
    var tv: TextView? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*Initializing viewe model by using ViewModel provider handle the configuration change of the device and it will prevent the
        * viewmodel to be reinitialize.
        * Recommended way to initialize the view model like this*/
        val viewModel : MainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        /*This will not handle the configuration change; if you rotate your device then the view model instance will also
        * re created and re call the object and functions*/
        val viewModel1  = MainViewModel()



        tv = findViewById(R.id.tv)
        progressBar = findViewById(R.id.progressBar)
        //updateUI()

        viewModel.count.observe(this, Observer {
            tv!!.text = it.toString()
            progressBar!!.progress = it
        })


    }

    fun updateUI() {
        val handler = Handler()
        val runnable = Runnable {
            for (i in 1..100) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                handler.post {
                    progressBar!!.progress = i
                    tv!!.text = "$i %"
                }
            }
        }
        Thread(runnable).start()
    }
}