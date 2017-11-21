package online.uooc.vdeoplayer.video

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.SeekBar
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import online.uooc.vdeoplayer.R
import online.uooc.vdeoplayer.video.srt.SubtitleView
import online.uooc.vdeoplayer.video.srt.SubtitlesCoding
import online.uooc.vdeoplayer.video.srt.SubtitlesModel
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Mission
import zlc.season.rxdownload3.core.Status
import zlc.season.rxdownload3.core.Succeed
import java.io.File
import java.util.*

class IjkVideoView : StandardGSYVideoPlayer {

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag!!) {
    }


    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    override fun init(context: Context?) {
        super.init(context)
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
        //屏蔽进度条滑动
        mChangePosition = false
    }

    //这个必须配置最上面的构造才能生效
    override fun getLayoutId(): Int {
        if (mIfCurrentIsFullscreen) {
            return R.layout.sample_video_land
        }
        return R.layout.sample_video_normal
    }

    override fun updateStartImage() {
        if (mIfCurrentIsFullscreen) {
            if (mStartButton is ImageView) {
                val imageView = mStartButton as ImageView
                if (mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING) {
                    imageView.setImageResource(R.drawable.video_click_pause_selector)
                } else if (mCurrentState == GSYVideoView.CURRENT_STATE_ERROR) {
                    imageView.setImageResource(R.drawable.video_click_play_selector)
                } else {
                    imageView.setImageResource(R.drawable.video_click_play_selector)
                }
            }
        } else {
            super.updateStartImage()
        }
    }

    private fun getSrtView(): SubtitleView? {
        val v = findViewById<SubtitleView>(R.id.mSubTitleView)
        if (mIfCurrentIsFullscreen) {
            v?.setFontSize(16f)
        } else {
            v?.setFontSize(13f)
        }
        return v
    }

    private fun seekSrt(position: Int) {
        if (getSrtView()?.data?.isEmpty() == true) {
            getSrtView()?.setData(srts)
        }
        getSrtView()?.seekTo(position)
    }

    //重置字幕
    fun clearSrt() {
        srts.clear()
    }

    var downSrtMission: Mission? = null
    //加载srt字幕
    fun loadSrt(srtPath: String) {
        if (!TextUtils.isEmpty(srtPath)) {
            val pathName = context.cacheDir.absolutePath
            val fileName = if (srtPath.lastIndexOf("/") != -1) srtPath.substring(srtPath.lastIndexOf("/") + 1) else "tmp.srt"
            downSrtMission = Mission(srtPath, fileName, pathName)
            RxDownload
                    .create(downSrtMission!!)
                    .subscribe {
                        when (it) {
                            is Succeed -> {
                                RxDownload.file(downSrtMission!!).subscribe {
                                    SubtitlesCoding.readFile(it.absolutePath)
                                    srts = SubtitlesCoding.getSubtitles()
                                }
                            }
                            else -> {
                                RxDownload.start(downSrtMission!!).subscribe {}
                            }
                        }
                    }

        } else {
            srts = arrayListOf()
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        RxDownload.stop(downSrtMission ?: return)
    }

    //切换播放和暂停
    fun togglePlayPause() {
        clickStartIcon()
    }

    //切换是否允许快进
    fun toggleForward(forward: Boolean) {
        VideoControl.isForwardEnable = forward
    }

    //播放状态监听
    fun watchState(s: (state: PlayState, position: Int) -> Unit) {
        VideoControl.playState = s
    }

    //清除状态监听
    fun clearWatchState() {
        VideoControl.clear()
        clearSrt()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

        val time = seekBar.progress * duration / 100
        val cur = currentPositionWhenPlaying
        if (!VideoControl.isForwardEnable && time > cur) {
        } else {
            super.onStopTrackingTouch(seekBar)
        }

    }

    override fun setStateAndUi(state: Int) {
        super.setStateAndUi(state)
        //notify
        val s = when (state) {
            CURRENT_STATE_PREPAREING -> VideoControl.PlayState.Start
            CURRENT_STATE_PLAYING -> VideoControl.PlayState.Playing
            CURRENT_STATE_PAUSE -> VideoControl.PlayState.Pause
            CURRENT_STATE_AUTO_COMPLETE -> VideoControl.PlayState.Complete
            CURRENT_STATE_ERROR -> VideoControl.PlayState.Error
            else -> VideoControl.PlayState.Wait
        }
        val p = when (state) {
            CURRENT_STATE_AUTO_COMPLETE -> duration
            else -> currentPositionWhenPlaying
        }
        VideoControl.notifyState(s, p, true)
        //timer toggle
        when (state) {
            CURRENT_STATE_PLAYING -> {
                playingTimerReset()
            }
            else -> {
                playingTimerClear()
            }
        }
    }

    override fun setTextAndProgress(secProgress: Int) {
        super.setTextAndProgress(secProgress)
        when (currentState) {
            CURRENT_STATE_PLAYING -> {
                VideoControl.currentPosition = currentPositionWhenPlaying
                seekSrt(currentPositionWhenPlaying)
            }
        }
    }

    companion object VideoControl {

        //字幕解析集
        var srts: ArrayList<SubtitlesModel> = arrayListOf()
        //设置是否可以拖动进度条
        var isForwardEnable = true
        //改变回调
        var playState: (state: PlayState, position: Int) -> Unit = { _, _ -> }
        //当前进度
        var currentPosition = 0
        private var currentState = PlayState.Wait
        private var playingTimer: Timer? = null
        private var playingTask: TimerTask? = null
        private var INTERVAL_TIME = 40 * 1000L


        fun playingTimerReset() {

            if (playingTimer != null || playingTask != null) {
                playingTimerClear()
            }
            playingTimer = Timer()
            playingTask = object : TimerTask() {
                override fun run() {
                    playState.invoke(currentState, currentPosition)
                }
            }
            playingTimer!!.schedule(playingTask, INTERVAL_TIME, INTERVAL_TIME)
        }

        fun playingTimerClear() {
            playingTimer?.cancel()
            playingTimer = null

            playingTask?.cancel()
            playingTask = null
        }

        fun notifyState(s: PlayState, p: Int, rightNow: Boolean = false) {
            currentState = s
            currentPosition = p
            if (rightNow)
                playState.invoke(currentState, currentPosition)
        }

        enum class PlayState { Wait, Start, Playing, Pause, Complete, Error }

        fun clear() {
            playingTimerClear()
            playState = { _, _ -> }
        }

    }

}