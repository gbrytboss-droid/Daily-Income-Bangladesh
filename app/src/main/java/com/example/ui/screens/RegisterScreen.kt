package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EmeraldReward
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RegisterScreen(
    onRegister: (name: String, age: Int, phone: String) -> Unit,
    onQuickDemo: () -> Unit = { onRegister("Sabbir Ahmed", 24, "01712345678") }
) {
    var name by remember { mutableStateOf("Sabbir Ahmed") }
    var ageText by remember { mutableStateOf("24") }
    var phone by remember { mutableStateOf("01712345678") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        var isValid = true

        if (name.trim().isEmpty()) {
            nameError = "Please enter your name"
            isValid = false
        } else {
            nameError = null
        }

        val parsedAge = ageText.trim().toIntOrNull()
        if (parsedAge == null || parsedAge < 13 || parsedAge > 120) {
            ageError = "Please enter a valid age (13-120)"
            isValid = false
        } else {
            ageError = null
        }

        val cleanPhone = phone.trim()
        if (cleanPhone.length < 6) {
            phoneError = "Please enter a valid phone number"
            isValid = false
        } else {
            phoneError = null
        }

        if (isValid && parsedAge != null) {
            onRegister(name.trim(), parsedAge, cleanPhone)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Icon / Coin Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(GoldLight, GoldDark)
                        )
                    )
                    .border(2.dp, GoldPrimary, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_coin_reward),
                    contentDescription = "Coin Reward",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Daily Income Bangladesh",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "AdMob বিজ্ঞাপন দেখে ৫০% নিশ্চিত কয়েন ও টাকা আয় করুন! সহজে bKash / Nagad এর মাধ্যমে রিডিম করুন।",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Welcome Bonus Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "+50 Free Coins (৳0.50 Taka) Welcome Gift",
                            color = GoldPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "অ্যাকাউন্ট খুললেই আপনার ওয়ালেটে ৫০ কয়েন জমা হবে",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Demo 1-Tap Entry Button (for fast preview & instant access)
            Button(
                onClick = { onQuickDemo() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("quick_demo_start_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = BackgroundDark
                )
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⚡ ১-ক্লিকে ডেমো প্রবেশ করুন (Quick Start Demo)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Registration Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Create Your Account",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (nameError != null) nameError = null
                        },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. Alex Morgan") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary)
                        },
                        isError = nameError != null,
                        supportingText = nameError?.let { { Text(it, color = Color(0xFFFF5252)) } },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SurfaceElevated
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Age Input
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 3) {
                                ageText = it
                                if (ageError != null) ageError = null
                            }
                        },
                        label = { Text("Age") },
                        placeholder = { Text("e.g. 24") },
                        leadingIcon = {
                            Icon(Icons.Default.Numbers, contentDescription = null, tint = GoldPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = ageError != null,
                        supportingText = ageError?.let { { Text(it, color = Color(0xFFFF5252)) } },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("age_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SurfaceElevated
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Number Input
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            if (phoneError != null) phoneError = null
                        },
                        label = { Text("Phone Number (মোবাইল নম্বর)") },
                        placeholder = { Text("e.g. 01712345678") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phoneError != null,
                        supportingText = phoneError?.let { { Text(it, color = Color(0xFFFF5252)) } },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SurfaceElevated
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { validateAndSubmit() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("register_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = BackgroundDark
                        )
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Register & Claim 50 Coins (৳০.৫০ টাকা)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            onRegister("Md. Shakil Ahmed", 24, "01712345678")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quick_demo_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                    ) {
                        Text(
                            text = "১ ক্লিকে শুরু করুন (Quick Demo Start)",
                            color = GoldPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
