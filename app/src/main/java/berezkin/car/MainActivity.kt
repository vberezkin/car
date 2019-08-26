package berezkin.car

import android.graphics.PointF
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.PI
import kotlin.math.min

class MainActivity : AppCompatActivity() {
  val tag = MainActivity::class.java.simpleName

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setup()
  }

  private fun setup() {
    val model = ViewModelProviders.of(this).get(GameViewModel::class.java)
    if (model.state.value == null) {
      gameView.post {
        model.state.value =
          GameState(
            EmptyRoute(PointF(gameView.width / 2f, gameView.height / 2f), PI.toFloat()),
            currentTime(),
            0f
          )
      }
    }
    gameView?.next = {
      model.state.value?.let {
        if (it.timePart < 1f) {
          val dur = it.route.length() / resources.getDimension(R.dimen.speed) * 1000
          val nextPart = min(if (dur > 0) (currentTime() - it.startTime) / dur else 1f, 1f)
          model.state.value = GameState(
            it.route,
            it.startTime,
            nextPart
          )
        }
      }
    }
    model.state.observe(this, Observer {
      gameView.state = it
      gameView.invalidate()
    })
    val routeBuilder = RouteBuilder(resources.getDimension(R.dimen.route_radius))
    gameView.setOnTouchListener { v, event ->
      if (event.action == MotionEvent.ACTION_UP) {
        model.state.value?.let {
          val (curPos, curBearing) = it.location()
          Log.d(tag, "curPos = $curPos, curBearing = $curBearing")
          Log.d(tag, "touch: (${event.x}, ${event.y})")
          val route = routeBuilder.build(curPos, curBearing, PointF(event.x, event.y))
          Log.d(tag, "new route: $route")
          model.state.value = GameState(route, currentTime(), 0f)
        }
      }
      true
    }
  }

  private fun currentTime() = SystemClock.elapsedRealtime()
}
