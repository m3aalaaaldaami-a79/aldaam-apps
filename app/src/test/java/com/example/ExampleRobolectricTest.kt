package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PreferencesManager
import com.example.model.PraiseRecord
import com.example.model.ZahraaStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("سبحة العداد الأسطواني", appName)
  }

  @Test
  fun `verify zahraa step progression`() {
    val step1 = ZahraaStep.ALLAHU_AKBAR
    assertEquals(34, step1.targetCount)
    val step2 = step1.next()
    assertNotNull(step2)
    assertEquals(ZahraaStep.ALHAMDULILLAH, step2)
    assertEquals(33, step2!!.targetCount)
    val step3 = step2.next()
    assertNotNull(step3)
    assertEquals(ZahraaStep.SUBHANALLAH, step3)
    assertEquals(33, step3!!.targetCount)
    assertNull(step3.next())
  }

  @Test
  fun `verify preferences manager history storage`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = PreferencesManager(context)
    prefs.clearHistory()
    assertEquals(0, prefs.getHistory().size)

    val record = PraiseRecord(
      dhikrName = "سبحان الله وبحمده",
      count = 100,
      doorType = "التسبيح المفتوح (تصاعدي)"
    )
    prefs.addHistoryRecord(record)
    val history = prefs.getHistory()
    assertEquals(1, history.size)
    assertEquals("سبحان الله وبحمده", history[0].dhikrName)
    assertEquals(100, history[0].count)
  }
}
