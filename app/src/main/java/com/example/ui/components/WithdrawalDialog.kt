package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EmeraldReward
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WithdrawalDialog(
    profile: UserProfile?,
    feedbackMessage: String?,
    onSubmit: (amountTaka: Int, method: String, accountNumber: String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentCoins = profile?.coins ?: 0L
    val currentTaka = currentCoins * 0.01

    var selectedMethod by remember { mutableStateOf("bKash") }
    var accountNumber by remember { mutableStateOf(profile?.phone ?: "") }
    var amountTakaText by remember { mutableStateOf("500") }
    var localError by remember { mutableStateOf<String?>(null) }

    val methods = listOf("bKash", "Nagad", "Rocket", "Recharge")
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "উত্তোলন (Withdraw)",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "মিনিমাম উত্তোলন: ৫০০ টাকা",
                                color = GoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Balance & Minimum notice
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "আপনার বর্তমান ব্যালেন্স:",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "≈ ৳${String.format("%.2f", currentTaka)} ($currentCoins 🪙)",
                                color = if (currentTaka >= 500) EmeraldReward else AmberAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Minimum Withdraw: ৳500 টাকা (৫০,০০০ কয়েন)",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method Selector
                Text(
                    text = "পেমেন্ট মেথড নির্বাচন করুন:",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    methods.forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) GoldPrimary else SurfaceElevated,
                            border = if (isSelected) BorderStroke(1.dp, GoldLight) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = method }
                        ) {
                            Text(
                                text = method,
                                color = if (isSelected) BackgroundDark else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Account Number Input
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = {
                        accountNumber = it
                        localError = null
                    },
                    label = { Text("$selectedMethod নম্বর (Account Number)") },
                    placeholder = { Text("01XXXXXXXXX") },
                    leadingIcon = {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = GoldPrimary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_account_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Amount (Taka) Input
                OutlinedTextField(
                    value = amountTakaText,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) {
                            amountTakaText = it
                            localError = null
                        }
                    },
                    label = { Text("উত্তোলন পরিমাণ (টাকা)") },
                    supportingText = {
                        Text("মিনিমাম ৫০০ টাকা = ৫০,০০০ কয়েন", color = TextSecondary, fontSize = 11.sp)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(500, 1000, 2000, 5000).forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceCard,
                            border = BorderStroke(1.dp, SurfaceElevated),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { amountTakaText = preset.toString() }
                        ) {
                            Text(
                                text = "৳$preset",
                                color = GoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                // Error / Feedback
                localError?.let { err ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = err,
                        color = Color(0xFFFF5252),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                feedbackMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = msg,
                        color = if (msg.contains("সফল")) EmeraldReward else Color(0xFFFF5252),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        val amt = amountTakaText.toIntOrNull()
                        if (accountNumber.trim().length < 11) {
                            localError = "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন!"
                        } else if (amt == null || amt < 500) {
                            localError = "মিনিমাম উত্তোলন ৫০০ টাকা হতে হবে!"
                        } else if (currentTaka < amt) {
                            val shortageCoins = (amt * 100) - currentCoins
                            localError = "অপর্যাপ্ত ব্যালেন্স! আপনার আরও $shortageCoins কয়েন লাগবে।"
                        } else {
                            localError = null
                            onSubmit(amt, selectedMethod, accountNumber.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_withdraw_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = BackgroundDark
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "টাকা উইথড্র করুন (৳${amountTakaText.ifEmpty { "500" }})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
