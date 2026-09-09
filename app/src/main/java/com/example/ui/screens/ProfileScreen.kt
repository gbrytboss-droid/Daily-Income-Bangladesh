package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.admob.AdMobConfig
import com.example.data.db.UserProfile
import com.example.ui.theme.AmberAccent
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    adMobConfig: AdMobConfig,
    showEditDialog: Boolean,
    fcmToken: String? = null,
    onOpenEditDialog: () -> Unit,
    onCloseEditDialog: () -> Unit,
    onUpdateProfile: (name: String, age: Int, phone: String) -> Unit,
    onOpenAdMobConfig: () -> Unit,
    onSendTestNotification: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val name = profile?.name ?: "Valued Earner"
    val age = profile?.age ?: 0
    val phone = profile?.phone ?: ""
    val registeredDateStr = profile?.registeredAt?.let {
        SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Today"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(bottom = 95.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // User Avatar & Name Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .border(2.dp, GoldLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        color = BackgroundDark,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Stars,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Daily Income Bangladesh • ৫০% শেয়ার",
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onOpenEditDialog,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceElevated,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Registration Info", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Registration Details Card
        Text(
            text = "Registered Account Details",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileDetailRow(
                    icon = Icons.Default.Person,
                    label = "Registered Name",
                    value = name
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileDetailRow(
                    icon = Icons.Default.Numbers,
                    label = "Age",
                    value = "$age years old"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileDetailRow(
                    icon = Icons.Default.Phone,
                    label = "Phone Number",
                    value = phone
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileDetailRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Joined On",
                    value = registeredDateStr
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AdMob Key & Integration Status Card
        Text(
            text = "AdMob API Key & Ad Units",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AdMob Integration",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (adMobConfig.isCustomKeyApplied) EmeraldReward.copy(alpha = 0.2f) else SurfaceCard
                    ) {
                        Text(
                            text = if (adMobConfig.isCustomKeyApplied) "Custom Key Active" else "Sample Test Key",
                            color = if (adMobConfig.isCustomKeyApplied) EmeraldReward else GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "AdMob App ID:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = adMobConfig.appId,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rewarded Ad Unit ID:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = adMobConfig.rewardedUnitId,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenAdMobConfig,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = BackgroundDark
                    )
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter / Update AdMob API Key",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Firebase Cloud Messaging (FCM) Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fcm_reminders_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daily Push Reminders (FCM)",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Firebase Cloud Messaging",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldReward.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Active",
                            color = EmeraldReward,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "প্রতিদিন Ad দেখে ৫০০ টাকার উইথড্র টার্গেট পূরণ করতে ব্যবহারকারীদের অটোমেটিক পুশ নোটিফিকেশন পাঠানো হয়।",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "FCM Topic: ",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "daily_earning_reminders",
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (!fcmToken.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "FCM Token: ${fcmToken.take(18)}...${fcmToken.takeLast(6)}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onSendTestNotification,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceCard,
                        contentColor = GoldPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Test Daily Reminder Now",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 50% Revenue Policy Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = EmeraldReward,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Guaranteed 50% Reward Share",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Every ad viewed on AdMob is divided equally: 50% is credited directly to your registered coin wallet, recorded on your ledger, and redeemable in Bangladeshi Taka (টাকা) via bKash / Nagad.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }

    if (showEditDialog && profile != null) {
        EditProfileDialog(
            currentName = profile.name,
            currentAge = profile.age,
            currentPhone = profile.phone,
            onSave = onUpdateProfile,
            onDismiss = onCloseEditDialog
        )
    }
}

@Composable
private fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentAge: Int,
    currentPhone: String,
    onSave: (name: String, age: Int, phone: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var ageText by remember { mutableStateOf(currentAge.toString()) }
    var phone by remember { mutableStateOf(currentPhone) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = SurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Edit Registration Info",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ageText,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() } && it.length <= 3) ageText = it
                    },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val parsedAge = ageText.toIntOrNull() ?: currentAge
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                onSave(name, parsedAge, phone)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = BackgroundDark
                        )
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
