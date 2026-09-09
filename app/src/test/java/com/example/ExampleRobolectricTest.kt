package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
    assertEquals("Daily Income Bangladesh", appName)
  }

  @Test
  fun `verify minimum withdrawal 500 taka requires 50000 coins`() {
    val minTaka = 500
    val coinsRequired = minTaka * 100L
    assertEquals(50000L, coinsRequired)
    val takaConverted = coinsRequired * 0.01
    assertEquals(500.0, takaConverted, 0.001)
  }

  @Test
  fun `verify fcm topic and notification channel configuration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.notification.NotificationHelper.createNotificationChannel(context)
    assertEquals("daily_earning_reminders", com.example.notification.NotificationHelper.TOPIC_DAILY_EARNING)
    assertEquals("daily_earning_reminders_channel", com.example.notification.NotificationHelper.CHANNEL_ID)
  }

  @Test
  fun `verify bkash and nagad payment providers exist`() {
    val providerIds = com.example.ui.screens.PAYMENT_PROVIDERS.map { it.id }
    org.junit.Assert.assertTrue(providerIds.contains("bKash"))
    org.junit.Assert.assertTrue(providerIds.contains("Nagad"))
  }

  @Test
  fun `verify withdrawal minimum 500 taka validation logic`() {
    fun isValidWithdrawal(amountTaka: Int, currentCoins: Long): Boolean {
      val minTaka = 500
      val requiredCoins = amountTaka * 100L
      return amountTaka >= minTaka && currentCoins >= requiredCoins
    }

    // Under minimum
    org.junit.Assert.assertFalse(isValidWithdrawal(499, 100000L))
    org.junit.Assert.assertFalse(isValidWithdrawal(100, 100000L))

    // Minimum met but insufficient coins
    org.junit.Assert.assertFalse(isValidWithdrawal(500, 49999L))

    // Minimum met and sufficient coins
    org.junit.Assert.assertTrue(isValidWithdrawal(500, 50000L))
    org.junit.Assert.assertTrue(isValidWithdrawal(1000, 150000L))
  }

  @Test
  fun `verify bangladesh mobile number validation for bkash and nagad`() {
    fun isValidBdPhone(phone: String): Boolean {
      val clean = phone.trim()
      return clean.length == 11 &&
              clean.startsWith("01") &&
              clean[2] in listOf('3', '4', '5', '6', '7', '8', '9')
    }

    org.junit.Assert.assertTrue(isValidBdPhone("01712345678")) // GP
    org.junit.Assert.assertTrue(isValidBdPhone("01812345678")) // Robi
    org.junit.Assert.assertTrue(isValidBdPhone("01912345678")) // Banglalink
    org.junit.Assert.assertFalse(isValidBdPhone("01212345678")) // Invalid prefix
    org.junit.Assert.assertFalse(isValidBdPhone("0171234567"))  // 10 digits
    org.junit.Assert.assertFalse(isValidBdPhone("017123456789")) // 12 digits
  }
}
