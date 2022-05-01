package com.android.threading

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    var count: MutableLiveData<Int> = MutableLiveData()

    init {
        updateUI()
    }

    fun updateUI(){
        val handler= Handler()
        val runnable = Runnable {
            for(i in 1..100){
                try {
                    Thread.sleep(500)
                }catch (e: InterruptedException){
                    e.printStackTrace()
                }
                handler.post{
                    count.postValue(i)
                }
            }
        }
        Thread(runnable).start()

    }
}