package online.uooc.vdeoplayer.video

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.SeekBar
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import online.uooc.vdeoplayer.R

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

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        //①
        val time = seekBar.progress * duration / 100
        val cur = currentPositionWhenPlaying
        if (time > cur) {
            //不允许快进
        } else {
            super.onStopTrackingTouch(seekBar)
        }

    }

    //①
    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
        mChangePosition = false
    }
    //功能点①：不允许快进
    //功能点②：
}