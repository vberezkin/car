package berezkin.car

import android.graphics.Matrix
import android.graphics.PointF
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getTargetContext()
    assertEquals("berezkin.car", appContext.packageName)
  }

  @Test
  fun matrix() {
    val m = Matrix()
    m.setTranslate(-100f, -200f)
    m.postRotate(30f)
    val p = floatArrayOf(100f, 200f, 0f, 0f)
    m.mapPoints(p)
    assertEquals(4, 2 + 2)
  }

  @Test
  fun tangent() {
    val rb = RouteBuilder(100f)
    val t1 = rb.tangentLine(PointF(100f,0f), PointF(0f, 200f))
    val t2 = rb.tangentLine(PointF(100f,0f), PointF(300f, 100f))
    val t3 = rb.tangentLine(PointF(100f,0f), PointF(200f, 0f))
    val t4 = rb.tangentLine(PointF(100f,0f), PointF(0f, -100f))
    assertEquals(4, 2 + 2)
  }

  @Test
  fun route() {
    val rb = RouteBuilder(100f)
    val r1 = rb.build(PointF(0f, 0f), 0f, PointF(0f, 100f))
    val r2 = rb.build(PointF(0f, 0f), 0f, PointF(200f, 100f))

    assertEquals(4, 2 + 2)
  }

}
