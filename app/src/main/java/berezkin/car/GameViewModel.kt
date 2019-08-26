package berezkin.car

import android.graphics.PointF
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameState(val route: Route, val startTime: Long, val timePart: Float) {
  private val interpolator = AccelerateDecelerateInterpolator()
  fun location(): Pair<PointF, Float> =
    route.location(route.length() * interpolator.getInterpolation(timePart))
}

class GameViewModel : ViewModel() {
  val state = MutableLiveData<GameState>()
}
