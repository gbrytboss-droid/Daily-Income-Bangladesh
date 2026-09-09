package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.RewardTransaction
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PaymentProvider(
    val id: String,
    val name: String,
    val bengaliName: String,
    val brandColor: Color,
    val shortCode: String
)

val PAYMENT_PROVIDERS = listOf(
    PaymentProvider("bKash", "bKash", "বিকাশ", Color(0xFFE2136E), "BK"),
    PaymentProvider("Nagad", "Nagad", "নগদ", Color(0xFFF7941D), "NG"),
    PaymentProvider("Rocket", "Rocket", "রকেট", Color(0xFF8C3494), "RK"),
    PaymentProvider("Recharge", "Recharge", "রিচার্জ", Color(0xFF0088CC), "RC")
)

@Composable
fun WithdrawScreen(
    profile: UserProfile?,
    transactions: List<RewardTransaction>,
    feedbackMessage: String?,
    onClearFeedback: () -> Unit,
    onSubmitWithdrawal: (amountTaka: Int, method: String, accountNumber: String) -> Unit,
    onNavigateToEarn: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val currentCoins = profile?.coins ?: 0L
    val currentTaka = currentCoins * 0.01

    var selectedProvider by remember { mutableStateOf(PAYMENT_PROVIDERS[0]) }
    var accountNumber by remember { mutableStateOf(profile?.phone ?: "") }
    var amountTakaText by remember { mutableStateOf("500") }
    var accountType by remember { mutableStateOf("Personal") }

    var localValidationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profile?.phone) {
        if (accountNumber.isEmpty() && !profile?.phone.isNullOrEmpty()) {
            accountNumber = profile.phone
        }
    }

    val amountInt = amountTakaText.toIntOrNull() ?: 0
    val coinsRequired = amountInt.toLong() * 100L
    val isAmountBelowMinimum = amountInt > 0 && amountInt < 500
    val hasInsufficientCoins = amountInt >= 500 && coinsRequired > currentCoins
    val isAmountValid = amountInt >= 500 && coinsRequired <= currentCoins

    val cleanNumber = accountNumber.trim()
    val isPhoneValid = cleanNumber.length == 11 &&
            cleanNumber.startsWith("01") &&
            cleanNumber[2] in listOf('3', '4', '5', '6', '7', '8', '9')

    val isFormReady = isAmountValid && isPhoneValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(bottom = 80.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "টাকা উত্তোলন ফরম (Withdrawal Form)",
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "মিনিমাম ৫০০ টাকা • বিকাশ ও নগদ ইনস্ট্যান্ট উইথড্র",
                    color = GoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Available Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "আপনার বর্তমান ব্যালেন্স (Available Balance)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentTaka >= 500) EmeraldReward.copy(alpha = 0.2f) else AmberAccent.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (currentTaka >= 500) "উত্তোলনযোগ্য (Eligible)" else "লক্ষ্য বাকি আছে",
                            color = if (currentTaka >= 500) EmeraldReward else AmberAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "৳${String.format(Locale.US, "%.2f", currentTaka)}",
                            color = GoldPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "$currentCoins কয়েন (১ টাকা = ১০০ কয়েন)",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "মিনিমাম উইথড্র",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "৳৫০০.০০",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "৫০,০০০ কয়েন",
                            color = GoldLight,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val progress = (currentTaka.toFloat() / 500f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (currentTaka >= 500) EmeraldReward else GoldPrimary,
                    trackColor = SurfaceElevated
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (currentTaka < 500) {
                    val remainingTaka = 500.0 - currentTaka
                    val remainingCoins = 50000L - currentCoins
                    Text(
                        text = "উইথড্র করতে আরও ৳${String.format(Locale.US, "%.2f", remainingTaka)} টাকা ($remainingCoins কয়েন) প্রয়োজন",
                        color = AmberAccent,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "অভিনন্দন! আপনি এখন টাকা উত্তোলন করতে পারবেন।",
                        color = EmeraldReward,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dedicated Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("dedicated_withdrawal_form_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, SurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // 1. Payment Method
                Text(
                    text = "১. পেমেন্ট মেথড নির্বাচন করুন (Select Method)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PAYMENT_PROVIDERS.forEach { provider ->
                        val isSelected = selectedProvider.id == provider.id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) provider.brandColor.copy(alpha = 0.22f) else SurfaceDark,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) provider.brandColor else SurfaceElevated
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedProvider = provider
                                    localValidationError = null
                                }
                                .testTag("method_option_${provider.id.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(provider.brandColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = provider.shortCode,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = provider.name,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = provider.bengaliName,
                                    color = if (isSelected) provider.brandColor else TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Account Type (Personal / Agent)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "একাউন্টের ধরণ (Account Type):",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Personal", "Agent").forEach { type ->
                            val isSelected = accountType == type
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GoldPrimary else SurfaceDark,
                                modifier = Modifier.clickable { accountType = type }
                            ) {
                                Text(
                                    text = if (type == "Personal") "ব্যক্তিগত" else "এজেন্ট",
                                    color = if (isSelected) BackgroundDark else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Account Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "২. ${selectedProvider.name} নম্বর লিখুন (Account Number)",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!profile?.phone.isNullOrEmpty() && accountNumber != profile?.phone) {
                        Text(
                            text = "রেজিস্টার্ড নম্বর ব্যবহার করুন",
                            color = GoldLight,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable {
                                accountNumber = profile?.phone ?: ""
                                localValidationError = null
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }.take(11)
                        accountNumber = digits
                        localValidationError = null
                    },
                    placeholder = { Text("01XXXXXXXXX") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = selectedProvider.brandColor
                        )
                    },
                    trailingIcon = {
                        if (isPhoneValid) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Valid Phone",
                                tint = EmeraldReward
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    isError = cleanNumber.isNotEmpty() && !isPhoneValid,
                    supportingText = {
                        if (cleanNumber.isNotEmpty() && !isPhoneValid) {
                            Text(
                                text = "সঠিক ১১ ডিজিটের বাংলাদেশি নম্বর লিখুন (যেমন 01712345678)",
                                color = Color(0xFFFF5252),
                                fontSize = 11.sp
                            )
                        } else if (isPhoneValid) {
                            val operator = when {
                                cleanNumber.startsWith("017") || cleanNumber.startsWith("013") -> "গ্রামীণফোন"
                                cleanNumber.startsWith("018") -> "রবি"
                                cleanNumber.startsWith("019") || cleanNumber.startsWith("014") -> "বাংলালিংক"
                                cleanNumber.startsWith("015") -> "টেলিটক"
                                cleanNumber.startsWith("016") -> "এয়ারটেল"
                                else -> "সঠিক নম্বর"
                            }
                            Text(
                                text = "✓ সঠিক $operator নম্বর (${selectedProvider.name} $accountType)",
                                color = EmeraldReward,
                                fontSize = 11.sp
                            )
                        } else {
                            Text(
                                text = "টাকা পাঠানো হবে এই ${selectedProvider.name} নম্বরে",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_account_number_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = selectedProvider.brandColor,
                        unfocusedBorderColor = SurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Amount
                Text(
                    text = "৩. উত্তোলনের পরিমাণ (টাকা)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountTakaText,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }.take(6)
                        amountTakaText = digits
                        localValidationError = null
                    },
                    placeholder = { Text("মিনিমাম ৫০০ টাকা") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldPrimary
                        )
                    },
                    trailingIcon = {
                        Text(
                            text = "BDT (৳)",
                            color = GoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    singleLine = true,
                    isError = isAmountBelowMinimum || hasInsufficientCoins,
                    supportingText = {
                        when {
                            isAmountBelowMinimum -> {
                                Text(
                                    text = "⚠️ সর্বনিম্ন উত্তোলনের পরিমাণ ৫০০ টাকা (Minimum ৳500 required)",
                                    color = Color(0xFFFF5252),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            hasInsufficientCoins -> {
                                val shortageCoins = coinsRequired - currentCoins
                                Text(
                                    text = "⚠️ অপর্যাপ্ত ব্যালেন্স! আপনার আরও $shortageCoins কয়েন লাগবে।",
                                    color = Color(0xFFFF5252),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            isAmountValid -> {
                                Text(
                                    text = "✓ কর্তন করা হবে: $coinsRequired কয়েন (অবশিষ্ট ব্যালেন্স: ${currentCoins - coinsRequired} 🪙)",
                                    color = EmeraldReward,
                                    fontSize = 11.sp
                                )
                            }
                            else -> {
                                Text(
                                    text = "মিনিমাম ৫০০ টাকা = ৫০,০০০ কয়েন",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_amount_taka_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(500, 1000, 2000, 5000).forEach { preset ->
                        val isCurrent = amountTakaText == preset.toString()
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrent) GoldPrimary else SurfaceDark,
                            border = BorderStroke(1.dp, if (isCurrent) GoldLight else SurfaceElevated),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    amountTakaText = preset.toString()
                                    localValidationError = null
                                }
                                .testTag("preset_amount_$preset")
                        ) {
                            Text(
                                text = "৳$preset",
                                color = if (isCurrent) BackgroundDark else GoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, SurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "উইথড্রয়াল সামারি (Payout Breakdown)",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("পেমেন্ট মেথড:", color = TextSecondary, fontSize = 12.sp)
                            Text("${selectedProvider.name} ($accountType)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("প্রাপক নম্বর:", color = TextSecondary, fontSize = 12.sp)
                            Text(if (cleanNumber.isNotEmpty()) cleanNumber else "নম্বর প্রদান করুন", color = if (isPhoneValid) EmeraldReward else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("মোট প্রদেয় অর্থ:", color = TextSecondary, fontSize = 12.sp)
                            Text("৳${if (amountInt > 0) amountInt else 0} টাকা", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("প্রক্রিয়াকরণ ফি:", color = TextSecondary, fontSize = 12.sp)
                            Text("৳০.০০ (ফ্রি)", color = EmeraldReward, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("আনুমানিক সময়:", color = TextSecondary, fontSize = 12.sp)
                            Text("১ - ২৪ ঘণ্টা", color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }

                // Error Feedback
                localValidationError?.let { errorMsg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3E1E1E), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMsg,
                            color = Color(0xFFFF8A80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Server Feedback
                feedbackMessage?.let { feedback ->
                    Spacer(modifier = Modifier.height(12.dp))
                    val isSuccess = feedback.contains("সফল")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSuccess) Color(0xFF1B3B2B) else Color(0xFF3E1E1E),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = if (isSuccess) EmeraldReward else Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feedback,
                            color = if (isSuccess) Color(0xFFA5D6A7) else Color(0xFFFF8A80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (!isPhoneValid) {
                            localValidationError = "অনুগ্রহ করে সঠিক ১১ ডিজিটের ${selectedProvider.name} নম্বর লিখুন।"
                        } else if (amountInt < 500) {
                            localValidationError = "সর্বনিম্ন উত্তোলনের পরিমাণ ৫০০ টাকা হতে হবে!"
                        } else if (coinsRequired > currentCoins) {
                            val shortageCoins = coinsRequired - currentCoins
                            localValidationError = "আপনার ব্যালেন্সে পর্যাপ্ত কয়েন নেই! আরও $shortageCoins কয়েন লাগবে।"
                        } else {
                            localValidationError = null
                            onClearFeedback()
                            onSubmitWithdrawal(amountInt, "${selectedProvider.name} ($accountType)", cleanNumber)
                        }
                    },
                    enabled = isFormReady,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_withdrawal_form_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = BackgroundDark,
                        disabledContainerColor = SurfaceElevated,
                        disabledContentColor = TextSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFormReady) "৳$amountInt টাকা উত্তোলনের রিকোয়েস্ট পাঠান" else "তথ্য সম্পূর্ণ ও ন্যূনতম ৫০০ টাকা দিন",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (currentTaka < 500) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onNavigateToEarn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceDark,
                            contentColor = GoldLight
                        ),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "বিজ্ঞাপন দেখে ব্যালেন্স বৃদ্ধি করুন (+০.৫০ টাকা প্রতি Ad)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History
        val withdrawalTransactions = transactions.filter { it.adType == "withdrawal" }
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "উত্তোলন ইতিহাস (Withdrawal History - ${withdrawalTransactions.size})",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (withdrawalTransactions.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, SurfaceElevated),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "এখনও কোনো উত্তোলন রিকোয়েস্ট করা হয়নি। ৫০০ টাকা সম্পন্ন করে রিকোয়েস্ট পাঠান।",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            } else {
                withdrawalTransactions.take(5).forEach { tx ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, SurfaceElevated),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = tx.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(tx.timestamp)),
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "-${kotlin.math.abs(tx.coinsEarned)} 🪙",
                                    color = Color(0xFFFF8A80),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldReward.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Processing",
                                        color = EmeraldReward,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
