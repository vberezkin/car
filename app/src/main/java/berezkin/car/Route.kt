package berezkin.car

import android.graphics.Matrix
import android.graphics.PointF
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import kotlin.math.PI
import kotlin.math.abs

sealed class Route {
  abstract fun length(): Float
  abstract fun location(mileage: Float): Pair<PointF, Float>
}

//class TestRoute(val start: PointF, val bearing: Float, val finish: PointF) : Route() {
//  override fun length(): Float = (finish - start).length()
//
//  override fun location(mileage: Float): Pair<PointF, Float> {
//    val dir = finish - start
//    val dx = dir.x * mileage / length()
//    val dy = dir.y * mileage / length()
//    return Pair(PointF(start.x + dx, start.y + dy), bearing)
//  }
//}

class EmptyRoute(val start: PointF, val startBearing: Float) : Route() {
  override fun length(): Float = 0f

  override fun location(mileage: Float): Pair<PointF, Float> = Pair(start, startBearing)
}

data class InnerRoute(
  val start: PointF,
  val startBearing: Float,
  val finish: PointF,
  val turnCenter: PointF,
  val turnRadius: Float,
  val turnAngle: Float,
  val turnPoint: PointF
) : Route() {

  private val straightLen = (turnPoint - start).length()
  private val arcLen = turnRadius * abs(turnAngle)

  override fun length(): Float = straightLen + arcLen

  override fun location(mileage: Float): Pair<PointF, Float> = if (mileage < straightLen) {
    val part = mileage / straightLen
    val s = turnPoint - start
    Pair(start + PointF(s.x * part, s.y * part), startBearing)
  } else {
    val turn = turnAngle * (mileage - straightLen) / arcLen
    val m = Matrix().apply { setRotate((-turn * 180 / PI).toFloat(), turnCenter.x, turnCenter.y) }
    val p = floatArrayOf(0f, 0f)
    m.mapPoints(p, floatArrayOf(turnPoint.x, turnPoint.y))
    Pair(PointF(p[0], p[1]), startBearing + turn)
  }
}

data class OuterRoute(
  val start: PointF,
  val startBearing: Float,
  val finish: PointF,
  val turnCenter: PointF,
  val turnRadius: Float,
  val turnAngle: Float,
  val turnPoint: PointF
) : Route() {

  private val arcLen = turnRadius * abs(turnAngle)
  private val straightLen = (finish - turnPoint).length()

  override fun length(): Float = arcLen + straightLen

  override fun location(mileage: Float): Pair<PointF, Float> = if (mileage < arcLen) {
    val turn = turnAngle * mileage / arcLen
    val m = Matrix().apply { setRotate((-turn * 180 / PI).toFloat(), turnCenter.x, turnCenter.y) }
    val p = floatArrayOf(0f, 0f)
    m.mapPoints(p, floatArrayOf(start.x, start.y))
    Pair(PointF(p[0], p[1]), startBearing + turn)
  } else {
    val sm = mileage - arcLen
    val part = sm / straightLen
    val s = finish - turnPoint
    Pair(turnPoint + PointF(s.x * part, s.y * part), startBearing + turnAngle)
  }
}
