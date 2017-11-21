package online.uooc.vdeoplayer

import android.os.Bundle
import android.widget.ImageView
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import online.uooc.vdeoplayer.video.AbsIjkActivity


class VideoActivity : AbsIjkActivity() {
    val mRxPermissions: RxPermissions by lazy { RxPermissions(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //播放器
        val player = mPlayer
        //封面
        val cover = ImageView(this)
        cover.scaleType = ImageView.ScaleType.CENTER_CROP
        cover.setImageResource(R.mipmap.xxx1)
        //url
        val url = "http://baobab.wdjcdn.com/14564977406580.mp4"
        //title
        val title = "好莱坞混捡"
        setVideoParam(player, cover, url, title)

        //切换视频
        changeUrl.setOnClickListener {
            changePlayUrl("http://cdn.tiaobatiaoba.com/Upload/square/2017-11-02/1509585140_1279.mp4", "有只猪", 10000)
            //字幕解析
            val srtUrl = "http://ozr40md7m.bkt.clouddn.com/wczt.srt"
            loadSrt(srtUrl)
        }
        //切换状态
        toggle.setOnClickListener {
            togglePlayPause()
        }
        //是否允许快进
        forward.apply { tag = true }.setOnClickListener {
            it.tag = !(it.tag as Boolean)
            toggleForward(it.tag as Boolean)
        }
        //监听状态
        watchState()
    }

}