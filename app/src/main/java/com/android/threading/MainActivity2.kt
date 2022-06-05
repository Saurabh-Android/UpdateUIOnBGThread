package com.android.threading

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class MainActivity2 : AppCompatActivity() {
    private var videoLayout: RelativeLayout? = null
    private var videoView: VideoView? = null
    private var mediacontrols: RelativeLayout? = null
    private var check = 0
    private var startTime: TextView? = null
    private var endTime: TextView? = null
    private var seekBar: SeekBar? = null
    private var videoHandler: Handler? = null
    private var videoRunnable: Runnable? = null
    private var cTimer: CountDownTimer? = null
    private var progressBar: ProgressBar? = null
    private var bufferbar: ProgressBar? = null
    private var youtubeLayout: Miniplayer? = null
    private var isMaximise = true
    private var url =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act2)
        videoLayout = findViewById(R.id.video_layout)
        frward_img = findViewById(R.id.frward_img)
        backward_img = findViewById(R.id.backward_img)
        val bbkframe = findViewById<FrameLayout>(R.id.bbkframe)
        val ffrdframe = findViewById<FrameLayout>(R.id.ffrdframe)
        mediacontrols = findViewById(R.id.mediacontrols)
        playbtn = findViewById(R.id.playbtn)
        pausebtn = findViewById(R.id.pausebtn)
        startTime = findViewById(R.id.starttime)
        endTime = findViewById(R.id.endtime)
        seekBar = findViewById(R.id.seekbar)
        rewindtxt = findViewById(R.id.rewindtxt)
        frwrdtxt = findViewById(R.id.frwrdtxt)
        fullscreen = findViewById(R.id.fullscreen)
        fullscreenexit = findViewById(R.id.fullscreenexit)
        progressBar = findViewById(R.id.progress)
        bufferbar = findViewById(R.id.bufferbar)
        videoView = findViewById(R.id.myvideo)
        youtubeLayout = findViewById(R.id.dragLayout)
        showimgDown = findViewById(R.id.showdown)
        showimgUp = findViewById(R.id.showup)
        setHandler()
        initialiseSeekBar()
        initiateVideo()
        val gd = GestureDetector(this@MainActivity2, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (check == 1) fastRewind() else if (check == 0) fastForward()
                return true
            }

            override fun onLongPress(e: MotionEvent) {}
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                startTimer()
                return super.onSingleTapConfirmed(e)
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onFling(
                event1: MotionEvent,
                event2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (isMaximise) {
                    minimiseView()
                } else {
                    maximiseView()
                }
                return true
            }
        })
        ffrdframe.setOnTouchListener { view, event ->
            check = 1
            gd.onTouchEvent(event)
        }
        bbkframe.setOnTouchListener { view, event ->
            check = 0
            gd.onTouchEvent(event)
        }
        playbtn?.setOnClickListener(
            View.OnClickListener {
                val fadeIn: Animation = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = DecelerateInterpolator()
                fadeIn.duration = 500
                val animation = AnimationSet(false)
                animation.addAnimation(fadeIn)
                pausebtn?.setAnimation(animation)
                videoView?.start()
                pausebtn?.setVisibility(View.VISIBLE)
                playbtn?.setVisibility(View.GONE)
            })
        pausebtn?.setOnClickListener(
            View.OnClickListener {
                val fadeIn: Animation = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = DecelerateInterpolator()
                fadeIn.duration = 500
                val animation = AnimationSet(false)
                animation.addAnimation(fadeIn)
                playbtn?.setAnimation(animation)
                pausebtn?.setVisibility(View.GONE)
                playbtn?.setVisibility(View.VISIBLE)
                videoView?.pause()
            })
        fullscreen?.setOnClickListener(
            View.OnClickListener {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val TIME_OUT = 2500
                Handler().postDelayed({
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                }, TIME_OUT.toLong())
            })
        fullscreenexit?.setOnClickListener(
            View.OnClickListener {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val TIME_OUT = 2500
                Handler().postDelayed({
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                }, TIME_OUT.toLong())
            })
    }

    fun showDown(view: View?) {
        minimiseView()
    }

    fun showUp(view: View?) {
        maximiseView()
    }

    fun minimiseView() {
        showimgUp!!.visibility =
            View.VISIBLE
        showimgDown!!.visibility = View.GONE
        youtubeLayout!!.minimize()
        fullscreen!!.visibility =
            View.GONE
        youtubeLayout!!.minimize()
        isMaximise = false
    }

    fun maximiseView() {
        showimgUp!!.visibility =
            View.GONE
        showimgDown!!.visibility =
            View.VISIBLE
        youtubeLayout!!.maximize()
        fullscreen!!.visibility =
            View.VISIBLE
        youtubeLayout!!.maximize()
        isMaximise = true
    }

    private fun initiateVideo() {
        videoView!!.setVideoURI(Uri.parse(url))
        videoView = findViewById(R.id.myvideo)
        url = "android.resource://" + packageName + "/" + R.raw.samplevideo
        videoView?.setVideoURI(Uri.parse(url))
        videoView?.start()
        if (videoView!!.isPlaying()) progressBar!!.visibility = View.VISIBLE
        videoView?.setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            videoView?.start()
            seekBar!!.max = videoView!!.getDuration()
            progressBar!!.visibility = View.GONE
            mp.setOnInfoListener { mp, what, extra ->
                when (what) {
                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> progressBar!!.visibility =
                        View.VISIBLE
                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> progressBar!!.visibility = View.GONE
                }
                false
            }
            mp.setOnBufferingUpdateListener { mp, percent -> bufferbar!!.progress = percent }
            mp.setOnCompletionListener { }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            230.0f,
            resources.displayMetrics
        ).toInt()
        val params = videoLayout!!.layoutParams
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUI()
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            fullscreen!!.visibility =
                View.GONE
            fullscreenexit!!.visibility =
                View.VISIBLE
            videoLayout!!.requestLayout()
            showimgDown!!.visibility =
                View.GONE
            showimgUp!!.visibility =
                View.GONE
            if (!isMaximise) {
                videoView!!.pause()
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = height
            fullscreen!!.visibility = View.VISIBLE
            fullscreenexit!!.visibility =
                View.GONE
            videoLayout!!.requestLayout()
            showimgDown!!.visibility = View.VISIBLE
            showimgUp!!.visibility = View.GONE
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val chechConfig = resources.configuration
        if (chechConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (hasFocus) {
                hideSystemUI()
            }
        }
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun fastForward() {
        videoView!!.seekTo(videoView!!.currentPosition + 10000)
        frward_img!!.visibility = View.VISIBLE
        frwrdtxt!!.visibility = View.VISIBLE
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 100
        val animation = AnimationSet(false)
        animation.addAnimation(fadeIn)
        frwrdtxt!!.animation =
            animation
        animForward(frward_img)
    }

    fun fastRewind() {
        videoView!!.seekTo(videoView!!.currentPosition - 10000)
        backward_img!!.visibility =
            View.VISIBLE
        rewindtxt!!.visibility =
            View.VISIBLE
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 100
        val animation = AnimationSet(false)
        animation.addAnimation(fadeIn)
        rewindtxt!!.animation =
            animation
        animBackward(backward_img)
    }

    fun startTimer() {
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 500
        val animation = AnimationSet(false)
        animation.addAnimation(fadeIn)
        mediacontrols!!.animation = animation
        cTimer = object : CountDownTimer(1500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mediacontrols!!.visibility = View.VISIBLE
                if (videoView!!.isPlaying) {
                    pausebtn!!.visibility = View.VISIBLE
                    playbtn!!.visibility = View.GONE
                } else {
                    playbtn!!.visibility = View.VISIBLE
                    pausebtn!!.visibility = View.GONE
                }
            }

            override fun onFinish() {
                val fadeOut: Animation = AlphaAnimation(1f, 0f)
                fadeOut.interpolator = AccelerateInterpolator()
                fadeOut.startOffset = 100
                fadeOut.duration = 100
                val animation = AnimationSet(false)
                animation.addAnimation(fadeOut)
                mediacontrols!!.animation = animation
                mediacontrols!!.visibility = View.GONE
                pausebtn!!.visibility = View.GONE
                playbtn!!.visibility = View.GONE
            }
        }
        cTimer?.start()
    }

    fun cancelTimer() {
        if (cTimer != null) cTimer!!.cancel()
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }

    fun setHandler() {
        videoHandler = Handler()
        videoRunnable = object : Runnable {
            override fun run() {
                if (videoView!!.duration > 0) {
                    val currentPosition = videoView!!.currentPosition
                    seekBar!!.progress = currentPosition
                    startTime!!.text = "" + convertIntToTime(currentPosition)
                    endTime!!.text = "" + convertIntToTime(videoView!!.duration - currentPosition)
                }
                videoHandler!!.postDelayed(this, 0)
            }
        }
        videoHandler!!.postDelayed(videoRunnable!!, 500)
    }

    private fun convertIntToTime(ms: Int): String {
        var time: String? = null
        var x: Int
        val seconds: Int
        val minutes: Int
        val hours: Int
        x = ms / 1000
        seconds = x % 60
        x /= 60
        minutes = x % 60
        x /= 60
        hours = x % 24
        time = if (hours != 0) String.format("%02d", hours) + ":" + String.format(
            "%02d",
            minutes
        ) + ":" + String.format("%02d", seconds) else String.format(
            "%02d",
            minutes
        ) + ":" + String.format("%02d", seconds)
        return time
    }

    fun initialiseSeekBar() {
        seekBar!!.progress = 0
        seekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (seekBar.id == R.id.seekbar) {
                    if (fromUser) {
                        videoView!!.seekTo(progress)
                        val currentPosition = videoView!!.currentPosition
                        startTime!!.text = "" + convertIntToTime(currentPosition)
                        endTime!!.text =
                            "" + convertIntToTime(videoView!!.duration - currentPosition)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    fun releaseVideoPlayer() {
        if (videoView != null) {
            videoHandler!!.removeCallbacks(videoRunnable!!)
            videoView = null
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        releaseVideoPlayer()
    }

    fun dissmissControls(view: View?) {
        cancelTimer()
        if (mediacontrols!!.visibility == View.VISIBLE) {
            val fadeOut: Animation = AlphaAnimation(1f, 0f)
            fadeOut.interpolator = AccelerateInterpolator()
            fadeOut.startOffset = 100
            fadeOut.duration = 100
            val animation = AnimationSet(false)
            animation.addAnimation(fadeOut)
            mediacontrols!!.animation = animation
            mediacontrols!!.visibility = View.GONE
            pausebtn!!.visibility =
                View.GONE
            playbtn!!.visibility =
                View.GONE
        } else {
            val fadeIn: Animation = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator()
            fadeIn.duration = 500
            val animation = AnimationSet(false)
            animation.addAnimation(fadeIn)
            mediacontrols!!.animation = animation
            mediacontrols!!.visibility = View.VISIBLE
            if (videoView!!.isPlaying) {
                pausebtn!!.visibility =
                    View.VISIBLE
                playbtn!!.visibility =
                    View.GONE
            } else {
                playbtn!!.visibility = View.VISIBLE
                pausebtn!!.visibility = View.GONE
            }
        }
    }

    fun samplevideo(view: View?) {
        url = "http://139.59.31.217:4864/api/videos/720p/food-showreel.mp4"
        initiateVideo()
    }

    fun samplevideo2(view: View?) {
        url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        initiateVideo()
    }

    companion object {
        private var frward_img: ImageView? = null
        private var backward_img: ImageView? = null
        private var playbtn: ImageView? = null
        private var pausebtn: ImageView? = null
        private var fullscreen: ImageView? = null
        private var fullscreenexit: ImageView? = null
        private var showimgUp: ImageView? = null
        private var showimgDown: ImageView? = null
        private var rewindtxt: TextView? = null
        private var frwrdtxt: TextView? = null
        fun animForward(view: ImageView?) {
            val scaleAnimation = ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
            prepareAnimationForward(scaleAnimation)
            val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
            prepareAnimationForward(alphaAnimation)
            val animation = AnimationSet(true)
            animation.addAnimation(alphaAnimation)
            animation.addAnimation(scaleAnimation)
            animation.duration = 500 //u can adjust yourself
            view!!.startAnimation(animation)
        }

        private fun prepareAnimationForward(animation: Animation): Animation {
            animation.repeatCount = 1
            animation.repeatMode = Animation.REVERSE
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    val fadeOut: Animation = AlphaAnimation(1f, 0f)
                    fadeOut.interpolator = AccelerateInterpolator()
                    fadeOut.startOffset = 100
                    fadeOut.duration = 100
                    val animation1 = AnimationSet(false)
                    animation1.addAnimation(fadeOut)
                    frwrdtxt!!.animation = animation1
                    frward_img!!.visibility = View.GONE
                    frwrdtxt!!.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            return animation
        }

        fun animBackward(view: ImageView?) {
            val scaleAnimation = ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
            prepareAnimationBackward(scaleAnimation)
            val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
            prepareAnimationBackward(alphaAnimation)
            val animation = AnimationSet(true)
            animation.addAnimation(alphaAnimation)
            animation.addAnimation(scaleAnimation)
            animation.duration = 500 //u can adjust yourself
            view!!.startAnimation(animation)
        }

        private fun prepareAnimationBackward(animation: Animation): Animation {
            animation.repeatCount = 1
            animation.repeatMode = Animation.REVERSE
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    val fadeOut: Animation = AlphaAnimation(1.0f, 0f)
                    fadeOut.interpolator = AccelerateInterpolator()
                    fadeOut.startOffset = 100
                    fadeOut.duration = 100
                    val animation1 = AnimationSet(false)
                    animation1.addAnimation(fadeOut)
                    rewindtxt!!.animation = animation1
                    backward_img!!.visibility = View.GONE
                    rewindtxt!!.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            return animation
        }
    }
}