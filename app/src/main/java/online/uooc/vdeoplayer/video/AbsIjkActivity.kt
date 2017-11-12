package online.uooc.vdeoplayer.video

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.StandardVideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer

abstract class AbsIjkActivity : AppCompatActivity(), StandardVideoAllCallBack {

    protected var mPlayerIsPlay: Boolean = false

    protected var mPlayerIsPause: Boolean = false

    protected var orientationUtils: OrientationUtils? = null

    protected var mPlayerBuilder: GSYVideoOptionBuilder? = null

    protected var mVideoPlayer: GSYBaseVideoPlayer? = null

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
        mVideoPlayer!!.currentPlayer.onVideoPause()
        mPlayerIsPause = true
    }

    override fun onResume() {
        super.onResume()
        mVideoPlayer!!.currentPlayer.onVideoResume()
        mPlayerIsPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayerIsPlay) {
            mVideoPlayer!!.currentPlayer.release()
        }
        if (orientationUtils != null)
            orientationUtils!!.releaseListener()
    }

    override fun onPrepared(url: String, vararg objects: Any) {
        if (orientationUtils == null) {
            throw NullPointerException("initVideo() or initVideoBuilderMode() first")
        }
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

    /**
     * 播放控件
     */
    fun setVideoPlayer(player: GSYBaseVideoPlayer) {
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
                mVideoPlayer!!.startWindowFullscreen(this@AbsIjkActivity, true, true)
            }
        }
        //构造配置
        mPlayerBuilder = GSYVideoOptionBuilder()
                .setCacheWithPlay(true)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setSeekRatio(1f)
                .setVideoAllCallBack(this)
        mPlayerBuilder!!.build(mVideoPlayer)
    }


    /**
     * 配置播放器
     */
    fun setVideoParam(cover: ImageView, url: String, title: String) {
        mPlayerBuilder!!
                .setThumbImageView(cover)
                .setUrl(url)
                .setVideoTitle(title)
    }
}
