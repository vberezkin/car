package berezkin.car

import android.graphics.Matrix
import android.graphics.PointF
import androidx.core.graphics.minus
import androidx.core.graphics.unaryMinus
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt

class RouteBuilder(val radius: Float) {
  fun build(startPos: PointF, startBearing: Float, finishPos: PointF): Route {
    val m = toCar(startPos, startBearing)
    val im = Matrix()
    val ok = m.invert(im)
    assert(ok)
    val finishInCar = floatArrayOf(0f, 0f)
    m.mapPoints(finishInCar, floatArrayOf(finishPos.x, finishPos.y))
    val (x, y) = finishInCar
    val rightTurn = x > 0f
    val normX = if (rightTurn) x else -x
    val inner = (normX - radius) * (normX - radius) + y * y < radius * radius
    return if (inner) {
      val lowFinishY = -sqrt(radius * radius - (normX - radius) * (normX - radius))
      val turnShiftY = y - lowFinishY

      val turnCenterInCar = floatArrayOf(if (rightTurn) radius else -radius, turnShiftY)
      val turnCenter = floatArrayOf(0f, 0f)
      im.mapPoints(turnCenter, turnCenterInCar)

      val t = tangentLine(PointF(radius, turnCenterInCar[1]), PointF(normX, y))
      val turnAngle = if (rightTurn) t else -t

      val turnPointInCar = floatArrayOf(0f, turnShiftY)
      val turnPoint = floatArrayOf(0f, 0f)
      im.mapPoints(turnPoint, turnPointInCar)

      InnerRoute(
        startPos,
        startBearing,
        finishPos,
        PointF(turnCenter[0], turnCenter[1]),
        radius,
        turnAngle,
        PointF(turnPoint[0], turnPoint[1])
      )
    } else {
      val turnCenterInCar = PointF(radius, 0f)
      val t = tangentLine(turnCenterInCar, PointF(normX, y))
      val turnAngle = if (rightTurn) t else -t
      val tc = if (rightTurn) turnCenterInCar else -turnCenterInCar
      val turnCenter = floatArrayOf(0f, 0f)
      im.mapPoints(turnCenter, floatArrayOf(tc.x, tc.y))

      val turnPointInCar = floatArrayOf(-radius, 0f)
      val mr = Matrix().apply { setRotate((-t * 180 / PI).toFloat()) }
      mr.mapPoints(turnPointInCar)
      turnPointInCar[0] += radius
      turnPointInCar[0] = if (rightTurn) turnPointInCar[0] else -turnPointInCar[0]
      val turnPoint = floatArrayOf(0f, 0f)
      im.mapPoints(turnPoint, turnPointInCar)

      OuterRoute(
        startPos,
        startBearing,
        finishPos,
        PointF(turnCenter[0], turnCenter[1]),
        radius,
        turnAngle,
        PointF(turnPoint[0], turnPoint[1])
      )
    }
  }

  private fun toCar(carPos: PointF, carBearing: Float) =
    Matrix().apply {
      setTranslate(-carPos.x, -carPos.y)
      postRotate((carBearing * 180 / PI).toFloat())
    }

  fun tangentLine(center: PointF, point: PointF): Float {
    val d = point - center
    val dn = d.length()
    val sine = radius / dn
    val a = asin(sine)
    val b = atan2(d.x, d.y)
    val c = a + b
    return if (c < 0) (c + 2 * PI).toFloat() else c
  }
}
