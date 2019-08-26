package berezkin.car

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI

class GameView : View {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  )

  var state: GameState? = null
  var next: (() -> Unit)? = null

  private val carStrokePaint =
    Paint().apply {
      color = resources.getColor(R.color.colorPrimaryDark, null)
      strokeWidth = resources.getDimension(R.dimen.car_stroke_width)
      style = Paint.Style.STROKE
    }

  private val carFillPaint =
    Paint().apply {
      color = resources.getColor(R.color.colorPrimary, null)
      style = Paint.Style.FILL
    }

  private val headLightOnPaint =
    Paint().apply {
      color = Color.WHITE
      style = Paint.Style.FILL
    }

  private val headLightOffPaint =
    Paint().apply {
      color = Color.LTGRAY
      style = Paint.Style.FILL
    }

  private val brakeLightOnPaint =
    Paint().apply {
      color = Color.RED
      style = Paint.Style.FILL
    }

  private val brakeLightOffPaint =
    Paint().apply {
      color = Color.GRAY
      style = Paint.Style.FILL
    }

  private val routePaint =
    Paint().apply {
      color = Color.LTGRAY
      style = Paint.Style.FILL
    }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    canvas?.let {
      drawRoute(it)
      drawCar(it)
    }
    next?.let { it() }
  }

  private fun drawRoute(canvas: Canvas) {
    state?.route?.let {
      val len = it.length()
      val w = resources.getDimension(R.dimen.route_width)
      for (i in 0 until len.toInt() step (w * 3).toInt()) {
        val pos = it.location(i.toFloat()).first
        canvas.drawCircle(pos.x, pos.y, w, routePaint)
      }
    }
  }

  private fun drawCar(canvas: Canvas) {
    state?.let {
      val w = resources.getDimension(R.dimen.car_width)
      val l = resources.getDimension(R.dimen.car_length)

      val (pos, bearing) = it.location()
      canvas.save()
      canvas.rotate((-bearing * 180 / PI).toFloat(), pos.x, pos.y)
      val rect = RectF(pos.x - w / 2, pos.y - l / 2, pos.x + w / 2, pos.y + l / 2)
      val headLightPaint = if (it.timePart < 1f) headLightOnPaint else headLightOffPaint
      canvas.drawCircle(pos.x - w / 2, pos.y + l / 2, w / 4, headLightPaint)
      canvas.drawCircle(pos.x + w / 2, pos.y + l / 2, w / 4, headLightPaint)
      val brakeLightPaint =
        if (it.timePart > 0.5f && it.timePart < 1f) brakeLightOnPaint else brakeLightOffPaint
      canvas.drawCircle(pos.x - w / 2, pos.y - l / 2, w / 4, brakeLightPaint)
      canvas.drawCircle(pos.x + w / 2, pos.y - l / 2, w / 4, brakeLightPaint)
      canvas.drawRect(rect, carFillPaint)
      canvas.drawRect(rect, carStrokePaint)
      canvas.restore()
    }
  }
}
