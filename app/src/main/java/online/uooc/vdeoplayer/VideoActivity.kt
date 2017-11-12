package online.uooc.vdeoplayer

import android.os.Bundle
import android.widget.ImageView
import online.uooc.vdeoplayer.video.AbsIjkActivity
import online.uooc.vdeoplayer.video.IjkVideoView

class VideoActivity : AbsIjkActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setVideoPlayer(findViewById(R.id.mPlayer) as IjkVideoView)
        //封面
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageResource(R.mipmap.xxx1)
        //url
        val url = "http://baobab.wdjcdn.com/14564977406580.mp4"
        //title
        val title = "好莱坞混捡"
        setVideoParam(imageView, url, title)
    }

}