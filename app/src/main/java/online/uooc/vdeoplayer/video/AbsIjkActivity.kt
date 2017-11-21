package online.uooc.vdeoplayer.video

import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.StandardVideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

//初始化：setVideoParam
abstract class AbsIjkActivity : AppCompatActivity(), StandardVideoAllCallBack {

    protected var mPlayerIsPlay: Boolean = false

    protected var mPlayerIsPause: Boolean = false

    protected var orientationUtils: OrientationUtils? = null

    protected var mPlayerBuilder: GSYVideoOptionBuilder? = null

    protected var mVideoPlayer: StandardGSYVideoPlayer? = null


    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }


    override fun onPause() {
        super.onPause()
        mVideoPlayer?.currentPlayer?.onVideoPause()
        mPlayerIsPause = true
    }

    override fun onResume() {
        super.onResume()
        mVideoPlayer?.currentPlayer?.onVideoResume()
        mPlayerIsPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayerIsPlay) {
            mVideoPlayer?.currentPlayer?.release()
        }
        if (orientationUtils != null)
            orientationUtils!!.releaseListener()
        clearWatchState()
    }

    override fun onPrepared(url: String, vararg objects: Any) {
        if (orientationUtils == null) {
            throw NullPointerException("initVideo() or initVideoBuilderMode() first")
        }
        orientationUtils!!.isEnable = false
        mPlayerIsPlay = true
    }

    override fun onClickStartIcon(url: String, vararg objects: Any) {

    }

    override fun onClickStartError(url: String, vararg objects: Any) {
    }

    override fun onClickStop(url: String, vararg objects: Any) {
    }

    override fun onClickStopFullscreen(url: String, vararg objects: Any) {
    }

    override fun onClickResume(url: String, vararg objects: Any) {
    }

    override fun onClickResumeFullscreen(url: String, vararg objects: Any) {
    }

    override fun onClickSeekbar(url: String, vararg objects: Any) {
    }

    override fun onClickSeekbarFullscreen(url: String, vararg objects: Any) {
    }

    override fun onAutoComplete(url: String, vararg objects: Any) {

    }

    override fun onEnterFullscreen(url: String, vararg objects: Any) {
    }

    override fun onQuitFullscreen(url: String, vararg objects: Any) {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
    }

    override fun onQuitSmallWidget(url: String, vararg objects: Any) {
    }

    override fun onEnterSmallWidget(url: String, vararg objects: Any) {
    }

    override fun onTouchScreenSeekVolume(url: String, vararg objects: Any) {
    }

    override fun onTouchScreenSeekPosition(url: String, vararg objects: Any) {
    }

    override fun onTouchScreenSeekLight(url: String, vararg objects: Any) {
    }

    override fun onPlayError(url: String, vararg objects: Any) {
    }

    override fun onClickStartThumb(url: String, vararg objects: Any) {
    }

    override fun onClickBlank(url: String, vararg objects: Any) {
    }

    override fun onClickBlankFullscreen(url: String, vararg objects: Any) {
    }

    fun setVideoParam(player: StandardGSYVideoPlayer, cover: ImageView, url: String, title: String, showPlayButton: Boolean = true) {
        mVideoPlayer = player
        //小窗下隐藏标题栏
        mVideoPlayer!!.titleTextView.visibility = View.GONE
        mVideoPlayer!!.backButton.visibility = View.GONE
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, mVideoPlayer)
        //初始化不打开外部的旋转
        orientationUtils!!.isEnable = false

        if (mVideoPlayer!!.fullscreenButton != null) {
            mVideoPlayer!!.fullscreenButton.setOnClickListener {
                //直接横屏
                orientationUtils!!.resolveByClick()
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                mVideoPlayer!!.startWindowFullscreen(this, true, true)
            }
        }
        //构造配置
        mPlayerBuilder = GSYVideoOptionBuilder()
                .setThumbImageView(cover)
                .setUrl(url)
                .setVideoTitle(title)
                .setCacheWithPlay(true)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setSeekRatio(1f)
                .setStandardVideoAllCallBack(this)
        mPlayerBuilder!!.build(mVideoPlayer)
        //是否显示播放按钮
        mVideoPlayer!!.startButton.visibility = if (showPlayButton) View.VISIBLE else View.GONE
    }

    //切换url
    fun changePlayUrl(url: String, title: String, second: Long) {
        mVideoPlayer!!.release()
        mPlayerBuilder!!.setUrl(url)
                .setVideoTitle(title)
                .setSeekOnStart(second)
                .build(mVideoPlayer!!)
        //手动播放
        mVideoPlayer!!.startPlayLogic()
        //重置srt字幕
        (mVideoPlayer as? IjkVideoView)?.clearSrt()
    }

    //切换播放 暂停
    fun togglePlayPause() {
        (mVideoPlayer as? IjkVideoView)?.togglePlayPause()
    }

    //切换是否允许快进
    fun toggleForward(forward: Boolean) {
        (mVideoPlayer as? IjkVideoView)?.toggleForward(forward)
    }

    //设置播放状态监听
    fun watchState() {
        (mVideoPlayer as? IjkVideoView)?.watchState { state, position ->
            println("+++++++${state.name} ${position}")
        }
    }

    //清除状态监听
    fun clearWatchState() {
        (mVideoPlayer as? IjkVideoView)?.clearWatchState()
    }

    //加载srt
    fun loadSrt(srtPath: String) {
        (mVideoPlayer as? IjkVideoView)?.loadSrt(srtPath)
    }
}
