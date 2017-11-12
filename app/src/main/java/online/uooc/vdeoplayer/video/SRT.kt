package online.uooc.vdeoplayer.video

class SRT {
    var beginTime: Int = 0
    var endTime: Int = 0
    var srtBody: String? = null

    override fun toString(): String {
        return "$beginTime:$endTime:$srtBody"
    }
}