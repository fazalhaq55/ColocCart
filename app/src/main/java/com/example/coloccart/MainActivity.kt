package com.example.coloccart

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.outlined.Notifications
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // FIX: Explicitly required for the tab indicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar

// ==========================================
// DESIGN SYSTEM
// ==========================================
var isEcoModeEnabled by mutableStateOf(false)

object AppColors {
    // Full color palette
    val Navy = Color(0xFF0D1B2A)
    val Teal = Color(0xFF1B7F79)
    val TealLight = Color(0xFF27B2AB)
    val Sage = Color(0xFF52796F)
    val Mint = Color(0xFFB7E4C7)
    val Cream = Color(0xFFFAF8F4)
    val CardBg = Color(0xFFFFFFFF)
    val Surface = Color(0xFFF2F0EC)
    val Amber = Color(0xFFE8A838)
    val Rose = Color(0xFFD64045)
    val Slate = Color(0xFF64748B)
    val LightSlate = Color(0xFFCBD5E1)
    val TextPrimary = Color(0xFF0D1B2A)
    val TextSecondary = Color(0xFF64748B)

    // Eco overrides
    val EcoPrimary = Color(0xFF1B1B1B)
    val EcoSurface = Color(0xFFF5F5F5)
    val EcoAccent = Color(0xFF2D5016)
}

object EcoTheme {
    val primary: Color @Composable get() = if (isEcoModeEnabled) AppColors.EcoPrimary else AppColors.Teal
    val primaryVariant: Color @Composable get() = if (isEcoModeEnabled) Color(0xFF333333) else AppColors.TealLight
    val success: Color @Composable get() = if (isEcoModeEnabled) AppColors.EcoAccent else AppColors.Sage
    val warning: Color @Composable get() = if (isEcoModeEnabled) Color(0xFF555555) else AppColors.Amber
    val danger: Color @Composable get() = if (isEcoModeEnabled) Color.Black else AppColors.Rose
    val topBar: Color @Composable get() = if (isEcoModeEnabled) AppColors.EcoSurface else AppColors.Cream
    val info: Color @Composable get() = if (isEcoModeEnabled) Color.DarkGray else AppColors.TealLight
    val textHighlight: Color @Composable get() = if (isEcoModeEnabled) Color.Black else AppColors.Teal
    val background: Color @Composable get() = if (isEcoModeEnabled) AppColors.EcoSurface else AppColors.Cream
    val cardBg: Color @Composable get() = if (isEcoModeEnabled) Color.White else AppColors.CardBg
    val surface: Color @Composable get() = if (isEcoModeEnabled) Color(0xFFEEEEEE) else AppColors.Surface
}

// Consistent shape tokens
object AppShapes {
    val card = RoundedCornerShape(16.dp)
    val button = RoundedCornerShape(12.dp)
    val input = RoundedCornerShape(12.dp)
    val chip = RoundedCornerShape(8.dp)
    val dialog = RoundedCornerShape(20.dp)
    val pill = RoundedCornerShape(50)
}

@Composable
fun shapeOrRect(shape: androidx.compose.ui.graphics.Shape) =
    if (isEcoModeEnabled) RectangleShape else shape

// ==========================================
// DATA MODELS
// ==========================================
data class ChatMessage(val senderName: String, val text: String, val time: String)

data class CartPost(
    val id: Int,
    val shopperName: String,
    val shopperGender: String,
    val itemName: String,
    val storeName: String,
    val totalCost: Double,
    val totalSlots: Int,
    val meetingPoint: String,
    val meetingDateTime: String,
    val contactInfo: String,
    val createdAt: String,
    val joinedUsers: MutableList<String>,
    val messages: MutableList<ChatMessage>
) {
    val filledSlots: Int get() = joinedUsers.size
}

val dummyCarts = mutableStateListOf(
    CartPost(
        id = 1,
        shopperName = "Fazalhaq Zaland",
        shopperGender = "Male",
        itemName = "5kg Premium Basmati Rice",
        storeName = "Carrefour",
        totalCost = 14.50,
        totalSlots = 3,
        meetingPoint = "Building B Lobby",
        meetingDateTime = "2026-05-05 18:30",
        contactInfo = "WhatsApp",
        createdAt = "2 hours ago",
        joinedUsers = mutableListOf("Fazalhaq Zaland", "Omar"),
        messages = mutableListOf(
            ChatMessage("Fazalhaq Zaland", "Hey guys, heading to Carrefour around 5 PM tomorrow for the rice!", "2h ago"),
            ChatMessage("Omar", "Perfect, count me in. I'll bring exact change.", "1h ago")
        )
    ),
    CartPost(
        id = 2,
        shopperName = "Marie",
        shopperGender = "Female",
        itemName = "Fresh Veggie & Fruit Box",
        storeName = "Grand Frais",
        totalCost = 24.00,
        totalSlots = 4,
        meetingPoint = "CROUS Madrillet Cafeteria",
        meetingDateTime = "2026-05-06 17:00",
        contactInfo = "Snapchat",
        createdAt = "Yesterday",
        joinedUsers = mutableListOf("Marie", "Lucas", "Sophie"),
        messages = mutableListOf(
            ChatMessage("Marie", "Tomatoes, onions, bananas, and apples split 4 ways!", "Yesterday"),
            ChatMessage("Sophie", "Sounds great. Could we also grab some garlic?", "Yesterday")
        )
    ),
    CartPost(
        id = 3,
        shopperName = "Lucas",
        shopperGender = "Male",
        itemName = "Pack of 6 Oat Milks",
        storeName = "Lidl",
        totalCost = 8.50,
        totalSlots = 2,
        meetingPoint = "ESIGELEC Library Entrance",
        meetingDateTime = "2026-05-05 12:15",
        contactInfo = "Room 304",
        createdAt = "Just now",
        joinedUsers = mutableListOf("Lucas"),
        messages = mutableListOf()
    ),
    CartPost(
        id = 4,
        shopperName = "Sophie",
        shopperGender = "Female",
        itemName = "Shared Cleaning Supplies",
        storeName = "Aldi",
        totalCost = 15.00,
        totalSlots = 3,
        meetingPoint = "Residence Kitchen, Floor 2",
        meetingDateTime = "2026-05-07 19:00",
        contactInfo = "Instagram: @sophie_c",
        createdAt = "3 hours ago",
        joinedUsers = mutableListOf("Sophie"),
        messages = mutableListOf()
    )
)

enum class AppScreen { WELCOME, FEED, CREATE, DETAIL, EDIT }

// ==========================================
// MAIN ACTIVITY
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = AppColors.Teal,
                    background = AppColors.Cream,
                    surface = AppColors.CardBg,
                    onPrimary = Color.White,
                    onBackground = AppColors.TextPrimary,
                    onSurface = AppColors.TextPrimary
                )
            ) {
                ColocCartRouter()
            }
        }
    }
}

@Composable
fun ColocCartRouter() {
    var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }
    var currentUser by remember { mutableStateOf("") }
    var currentGender by remember { mutableStateOf("") }
    var currentHub by remember { mutableStateOf("") }
    var selectedCart by remember { mutableStateOf<CartPost?>(null) }

    Box(modifier = Modifier.background(EcoTheme.background)) {
        when (currentScreen) {
            AppScreen.WELCOME -> WelcomeScreen(
                onEnter = { name, gender, hub ->
                    currentUser = name; currentGender = gender; currentHub = hub
                    currentScreen = AppScreen.FEED
                }
            )
            AppScreen.FEED -> FeedScreen(
                userName = currentUser,
                hubName = currentHub,
                onNavigateToCreate = { currentScreen = AppScreen.CREATE },
                onLogOut = { currentScreen = AppScreen.WELCOME },
                onCartClick = { cart -> selectedCart = cart; currentScreen = AppScreen.DETAIL }
            )
            AppScreen.CREATE -> CreatePoolScreen(
                userName = currentUser,
                userGender = currentGender,
                onNavigateBack = { currentScreen = AppScreen.FEED }
            )
            AppScreen.DETAIL -> selectedCart?.let { cart ->
                CartDetailScreen(
                    cart = cart,
                    currentUser = currentUser,
                    onNavigateBack = { currentScreen = AppScreen.FEED },
                    onNavigateToEdit = { currentScreen = AppScreen.EDIT }
                )
            }
            AppScreen.EDIT -> selectedCart?.let { cart ->
                EditPoolScreen(cart = cart, onNavigateBack = { currentScreen = AppScreen.FEED })
            }
        }
    }
}

// ==========================================
// REUSABLE COMPONENTS
// ==========================================

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        isError = isError,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = shapeOrRect(AppShapes.input),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EcoTheme.primary,
            focusedLabelColor = EcoTheme.primary,
            unfocusedBorderColor = AppColors.LightSlate,
            unfocusedLabelColor = AppColors.Slate,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = EcoTheme.primary,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = shapeOrRect(AppShapes.button),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = AppColors.LightSlate,
            disabledContentColor = AppColors.Slate
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isEcoModeEnabled) 0.dp else 2.dp
        )
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        // ECO MODE: Hide the icon completely to save rendering power
        if (!isEcoModeEnabled) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EcoTheme.primary,
                modifier = Modifier.size(18.dp).padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        Column {
            Text(label, fontSize = 11.sp, color = AppColors.Slate, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
            Text(value, fontSize = 14.sp, color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AvatarCircle(name: String, size: Int = 36, fontSize: Int = 14) {
    val initial = name.firstOrNull()?.uppercaseChar() ?: '?'

    // FIX: "and 0x7FFFFFFF" forces the hash code to always be a positive integer,
    // preventing Color.hsl from crashing with a negative hue value!
    val positiveHash = name.hashCode() and 0x7FFFFFFF
    val hue = (positiveHash % 12) * 30f

    val bgColor = Color.hsl(hue, 0.45f, if (isEcoModeEnabled) 0.75f else 0.65f)

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initial.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp
        )
    }
}

@Composable
fun SlotProgressBar(filled: Int, total: Int) {
    val fraction = if (total > 0) filled.toFloat() / total.toFloat() else 0f
    val isFull = filled >= total
    val barColor = when {
        isEcoModeEnabled -> Color(0xFF444444)
        isFull -> AppColors.Rose
        fraction > 0.5f -> AppColors.Amber
        else -> AppColors.Teal
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$filled/$total slots filled",
                fontSize = 12.sp,
                color = if (isFull) AppColors.Rose else AppColors.Slate,
                fontWeight = if (isFull) FontWeight.SemiBold else FontWeight.Normal
            )
            if (isFull) {
                Text("FULL", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = AppColors.Rose, letterSpacing = 1.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = barColor,
            trackColor = AppColors.LightSlate
        )
    }
}

// ==========================================
// WELCOME / ONBOARDING SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(onEnter: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var expandedGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

    var hub by remember { mutableStateOf("") }
    var expandedHub by remember { mutableStateOf(false) }
    var isSearchingLocation by remember { mutableStateOf(false) }
    var hubOptions by remember { mutableStateOf(listOf("Search for nearby hubs first...")) }
    var hubsFound by remember { mutableStateOf(false) }

    LaunchedEffect(isSearchingLocation) {
        if (isSearchingLocation) {
            delay(1500)
            hubOptions = listOf(
                "CROUS Madrillet (0.2 km)",
                "ESIGELEC Campus (0.5 km)",
                "Rouen City Center (4.0 km)"
            )
            hub = hubOptions[0]
            hubsFound = true
            isSearchingLocation = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (!isEcoModeEnabled)
                    Brush.verticalGradient(listOf(Color(0xFFE8F4F2), AppColors.Cream))
                else Brush.verticalGradient(listOf(AppColors.EcoSurface, AppColors.EcoSurface))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo / Brand area
            if (!isEcoModeEnabled) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(AppColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Text(
                "ColocCart",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EcoTheme.primary,
                letterSpacing = (-0.5).sp
            )
            if (!isEcoModeEnabled) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Smart micro-pooling for students",
                    color = AppColors.Slate,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Form card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = shapeOrRect(AppShapes.card),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isEcoModeEnabled) 0.dp else 4.dp
                ),
                border = if (isEcoModeEnabled) BorderStroke(1.dp, AppColors.LightSlate) else null
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Create your profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tell us a bit about yourself", fontSize = 13.sp, color = AppColors.Slate)
                    Spacer(modifier = Modifier.height(20.dp))

                    AppTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Your name"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedGender,
                        onExpandedChange = { expandedGender = !expandedGender }
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender", fontSize = 14.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = shapeOrRect(AppShapes.input),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EcoTheme.primary,
                                focusedLabelColor = EcoTheme.primary,
                                unfocusedBorderColor = AppColors.LightSlate,
                                unfocusedLabelColor = AppColors.Slate,
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedGender,
                            onDismissRequest = { expandedGender = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 15.sp) },
                                    onClick = { gender = option; expandedGender = false }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hub detection card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = shapeOrRect(AppShapes.card),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(if (isEcoModeEnabled) 0.dp else 4.dp),
                border = if (isEcoModeEnabled) BorderStroke(1.dp, AppColors.LightSlate) else null
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Find your hub", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pooling is location-based", fontSize = 13.sp, color = AppColors.Slate)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { isSearchingLocation = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = shapeOrRect(AppShapes.button),
                        border = BorderStroke(1.5.dp, EcoTheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EcoTheme.primary)
                    ) {
                        if (isSearchingLocation) {
                            CircularProgressIndicator(
                                color = EcoTheme.primary,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Searching nearby hubs...", fontWeight = FontWeight.Medium)
                        } else if (hubsFound) {
                            Icon(Icons.Outlined.CheckCircle, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hubs detected — tap to refresh", fontWeight = FontWeight.Medium)
                        } else {
                            if (!isEcoModeEnabled) {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Detect Nearby Hubs", fontWeight = FontWeight.Medium)
                        }
                    }

                    if (hubsFound) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedHub,
                            onExpandedChange = { expandedHub = !expandedHub }
                        ) {
                            OutlinedTextField(
                                value = hub,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Selected Hub", fontSize = 14.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHub) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = shapeOrRect(AppShapes.input),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EcoTheme.primary,
                                    focusedLabelColor = EcoTheme.primary,
                                    unfocusedBorderColor = AppColors.Teal,
                                    unfocusedLabelColor = AppColors.Slate,
                                    unfocusedContainerColor = Color(0xFFF0FAF8),
                                    focusedContainerColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedHub,
                                onDismissRequest = { expandedHub = false }
                            ) {
                                hubOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, fontSize = 15.sp) },
                                        onClick = { hub = option; expandedHub = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            val canEnter = name.isNotBlank() && gender.isNotBlank() && hub.isNotBlank()
                    && hub != "Search for nearby hubs first..."

            PrimaryButton(
                text = "Enter Hub →",
                onClick = {
                    if (canEnter) {
                        val cleanHub = hub.substringBefore(" (")
                        onEnter(name, gender, cleanHub)
                    }
                },
                enabled = canEnter,
                containerColor = if (canEnter) EcoTheme.primary else AppColors.LightSlate
            )
        }
    }
}

// ==========================================
// FEED SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    userName: String,
    hubName: String,
    onNavigateToCreate: () -> Unit,
    onLogOut: () -> Unit,
    onCartClick: (CartPost) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val myCarts = dummyCarts.filter { it.shopperName == userName }
    val totalGuests = myCarts.sumOf { if (it.joinedUsers.size > 0) it.joinedUsers.size - 1 else 0 }
    var viewedGuests by remember { mutableStateOf(0) }
    val newActivityCount = totalGuests - viewedGuests

    val filteredCarts = dummyCarts.filter { cart ->
        val matchesSearch = cart.itemName.contains(searchQuery, ignoreCase = true) ||
                cart.storeName.contains(searchQuery, ignoreCase = true)
        when (selectedTabIndex) {
            0 -> matchesSearch
            1 -> matchesSearch && cart.shopperName == userName
            2 -> matchesSearch && cart.joinedUsers.contains(userName) && cart.shopperName != userName
            else -> false
        }
    }

    Scaffold(
        containerColor = EcoTheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            hubName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            "Hello, ${userName.substringBefore(" ")} 👋",
                            fontSize = 12.sp,
                            color = AppColors.Slate
                        )
                    }
                },
                actions = {
                    // Eco toggle chip
                    Surface(
                        shape = AppShapes.chip,
                        color = if (isEcoModeEnabled) Color(0xFFDCEDC8) else Color(0xFFE8F5E9),
                        modifier = Modifier
                            .clickable { isEcoModeEnabled = !isEcoModeEnabled }
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (isEcoModeEnabled) "ECO ON" else "🌿 ECO",
                            color = if (isEcoModeEnabled) AppColors.EcoAccent else Color(0xFF388E3C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    TextButton(onClick = { onLogOut() }) {
                        Text("Exit", color = AppColors.Rose, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EcoTheme.topBar)
            )
        },
        floatingActionButton = {
            if (!isEcoModeEnabled) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToCreate() },
                    containerColor = EcoTheme.primary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("New Pool", fontWeight = FontWeight.SemiBold) }
                )
            } else {
                FloatingActionButton(
                    onClick = { onNavigateToCreate() },
                    containerColor = EcoTheme.primary,
                    contentColor = Color.White,
                    shape = RectangleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EcoTheme.topBar)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search items or stores...", color = AppColors.Slate, fontSize = 14.sp) },
                    leadingIcon = if (!isEcoModeEnabled) {
                        { Icon(Icons.Default.Search, null, tint = AppColors.Slate, modifier = Modifier.size(20.dp)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = shapeOrRect(AppShapes.pill),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = AppColors.LightSlate,
                        focusedBorderColor = EcoTheme.primary,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
            }

            // Tab row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = EcoTheme.topBar,
                contentColor = EcoTheme.primary,
                indicator = { tabPositions ->
                    if (!isEcoModeEnabled) {
                        // FIX: Replaced SecondaryIndicator with universal Indicator to prevent crash
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            height = 3.dp,
                            color = EcoTheme.primary
                        )
                    }
                },
                // FIX: Replaced deprecated Divider with HorizontalDivider
                divider = { HorizontalDivider(color = AppColors.LightSlate, thickness = 1.dp) }
            ) {
                val tabLabels = listOf("All Pools", "Hosting", "Joined")
                tabLabels.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            if (index == 1) viewedGuests = totalGuests
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (index == 1 && newActivityCount > 0) {
                                    Badge(containerColor = AppColors.Rose) {
                                        Text(newActivityCount.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Text(
                                label,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredCarts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (!isEcoModeEnabled) {
                                    Icon(
                                        Icons.Outlined.ShoppingCart,
                                        null,
                                        tint = AppColors.LightSlate,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                Text(
                                    when {
                                        searchQuery.isNotEmpty() -> "No results for \"$searchQuery\""
                                        selectedTabIndex == 1 -> "You aren't hosting any pools yet"
                                        selectedTabIndex == 2 -> "You haven't joined any pools yet"
                                        else -> "No carts available"
                                    },
                                    color = AppColors.Slate,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredCarts) { cart ->
                        CartItemCard(cart = cart, currentUser = userName, onClick = { onCartClick(cart) })
                    }
                }
                item { Spacer(modifier = Modifier.height(72.dp)) } // FAB clearance
            }
        }
    }
}

// ==========================================
// CART ITEM CARD
// ==========================================
@Composable
fun CartItemCard(cart: CartPost, currentUser: String, onClick: () -> Unit) {
    val hasJoined = cart.joinedUsers.contains(currentUser)
    val isFull = cart.filledSlots >= cart.totalSlots
    val isMyCart = cart.shopperName == currentUser
    val costPerPerson = cart.totalCost / cart.totalSlots

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = shapeOrRect(AppShapes.card),
        colors = CardDefaults.cardColors(containerColor = EcoTheme.cardBg),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEcoModeEnabled) 0.dp else 3.dp
        ),
        border = if (isEcoModeEnabled) BorderStroke(1.dp, AppColors.LightSlate)
        else if (isMyCart) BorderStroke(1.5.dp, EcoTheme.primary.copy(alpha = 0.3f))
        else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(name = cart.shopperName)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        cart.shopperName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )
                    Text(cart.createdAt, fontSize = 11.sp, color = AppColors.Slate)
                }
                // Store chip
                Surface(
                    shape = shapeOrRect(RoundedCornerShape(6.dp)),
                    color = EcoTheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isEcoModeEnabled) {
                            Icon(Icons.Outlined.Store, null, tint = AppColors.Slate, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(cart.storeName, fontSize = 12.sp, color = AppColors.Slate, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = AppColors.LightSlate.copy(alpha = 0.6f), thickness = 0.5.dp) // FIX: HorizontalDivider
            Spacer(modifier = Modifier.height(14.dp))

            // Item name
            Text(
                cart.itemName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isEcoModeEnabled) {
                    Icon(Icons.Default.LocationOn, null, tint = AppColors.Slate, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(cart.meetingPoint, fontSize = 13.sp, color = AppColors.Slate, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Per person", fontSize = 11.sp, color = AppColors.Slate, letterSpacing = 0.3.sp)
                    Text(
                        "€${String.format("%.2f", costPerPerson)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EcoTheme.textHighlight
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total", fontSize = 11.sp, color = AppColors.Slate, letterSpacing = 0.3.sp)
                    Text(
                        "€${String.format("%.2f", cart.totalCost)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Slot progress
            SlotProgressBar(filled = cart.filledSlots, total = cart.totalSlots)

            Spacer(modifier = Modifier.height(14.dp))

            // Action button
            Button(
                onClick = {
                    if (!isMyCart) {
                        val index = dummyCarts.indexOfFirst { it.id == cart.id }
                        if (index != -1) {
                            val updatedUsers = cart.joinedUsers.toMutableList()
                            if (hasJoined) updatedUsers.remove(currentUser)
                            else if (!isFull) updatedUsers.add(currentUser)
                            dummyCarts[index] = cart.copy(joinedUsers = updatedUsers)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                enabled = !isMyCart && (!isFull || hasJoined),
                shape = shapeOrRect(AppShapes.button),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isMyCart -> EcoTheme.surface
                        hasJoined -> AppColors.Rose
                        isFull -> AppColors.LightSlate
                        else -> EcoTheme.primary
                    },
                    contentColor = when {
                        isMyCart -> AppColors.TextSecondary
                        hasJoined -> Color.White
                        isFull -> AppColors.Slate
                        else -> Color.White
                    },
                    disabledContainerColor = AppColors.Surface,
                    disabledContentColor = AppColors.Slate
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = when {
                        isMyCart -> "Your Cart • Host"
                        hasJoined -> "Leave Cart"
                        isFull -> "Cart Full"
                        else -> "Join Cart"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ==========================================
// CREATE POOL SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePoolScreen(userName: String, userGender: String, onNavigateBack: () -> Unit) {
    var itemName by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf("") }
    var totalCost by remember { mutableStateOf("") }
    var totalSlots by remember { mutableStateOf("") }
    var meetingPoint by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var meetingDateTime by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(context, { _, h, m ->
        meetingDateTime = "$meetingDateTime ${String.format("%02d:%02d", h, m)}"
    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

    val datePickerDialog = DatePickerDialog(context, { _, y, mo, d ->
        meetingDateTime = "$y-${String.format("%02d", mo + 1)}-${String.format("%02d", d)}"
        timePickerDialog.show()
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    val parsedSlots = totalSlots.toIntOrNull() ?: 0
    val isSlotsValid = parsedSlots >= 2
    val canPost = itemName.isNotBlank() && totalCost.isNotBlank() && isSlotsValid && meetingDateTime.isNotBlank()

    Scaffold(
        containerColor = EcoTheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Create Pool", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = AppColors.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EcoTheme.topBar)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormSection(title = "What are you buying?") {
                AppTextField(value = itemName, onValueChange = { itemName = it }, label = "Item name")
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(value = storeName, onValueChange = { storeName = it }, label = "Store name")
            }

            FormSection(title = "Cost & Slots") {
                AppTextField(
                    value = totalCost,
                    onValueChange = { totalCost = it },
                    label = "Total cost (€)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = totalSlots,
                    onValueChange = { totalSlots = it },
                    label = { Text("Number of people (min 2)", fontSize = 14.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = totalSlots.isNotEmpty() && !isSlotsValid,
                    supportingText = {
                        if (totalSlots.isNotEmpty() && !isSlotsValid)
                            Text("Minimum 2 people required", color = AppColors.Rose, fontSize = 12.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = shapeOrRect(AppShapes.input),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoTheme.primary,
                        focusedLabelColor = EcoTheme.primary,
                        errorBorderColor = AppColors.Rose,
                        unfocusedBorderColor = AppColors.LightSlate,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
            }

            FormSection(title = "Meeting Details") {
                AppTextField(value = meetingPoint, onValueChange = { meetingPoint = it }, label = "Safe meeting point")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = meetingDateTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date & Time", fontSize = 14.sp) },
                    trailingIcon = {
                        if (!isEcoModeEnabled) {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.DateRange, null, tint = EcoTheme.primary)
                            }
                        } else {
                            TextButton(onClick = { datePickerDialog.show() }) {
                                Text("SET", fontWeight = FontWeight.Bold, color = EcoTheme.primary, fontSize = 12.sp)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = shapeOrRect(AppShapes.input),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoTheme.primary,
                        unfocusedBorderColor = AppColors.LightSlate,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(value = contactInfo, onValueChange = { contactInfo = it }, label = "Contact info (e.g. WhatsApp)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Post Open Cart",
                onClick = {
                    if (canPost) {
                        dummyCarts.add(
                            CartPost(
                                id = dummyCarts.size + 1,
                                shopperName = userName,
                                shopperGender = userGender,
                                itemName = itemName,
                                storeName = storeName,
                                totalCost = totalCost.toDoubleOrNull() ?: 0.0,
                                totalSlots = parsedSlots,
                                meetingPoint = meetingPoint,
                                meetingDateTime = meetingDateTime,
                                contactInfo = contactInfo,
                                createdAt = "Just now",
                                joinedUsers = mutableListOf(userName),
                                messages = mutableListOf()
                            )
                        )
                        onNavigateBack()
                    }
                },
                enabled = canPost,
                containerColor = EcoTheme.success
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Slate,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 10.dp, start = 2.dp)
        )
        Card(
            shape = shapeOrRect(AppShapes.card),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(if (isEcoModeEnabled) 0.dp else 2.dp),
            border = if (isEcoModeEnabled) BorderStroke(1.dp, AppColors.LightSlate) else null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartDetailScreen(
    cart: CartPost,
    currentUser: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    var chatInput by remember { mutableStateOf("") }
    val liveCart = dummyCarts.find { it.id == cart.id } ?: return
    val isMember = liveCart.joinedUsers.contains(currentUser)
    var showChatDialog by remember { mutableStateOf(false) }
    val costPerPerson = liveCart.totalCost / liveCart.totalSlots

    Scaffold(
        containerColor = EcoTheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Pool Details", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = AppColors.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EcoTheme.topBar)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero card
            Card(
                shape = shapeOrRect(AppShapes.card),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEcoModeEnabled) Color.White else AppColors.Navy
                ),
                elevation = CardDefaults.cardElevation(if (isEcoModeEnabled) 0.dp else 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        liveCart.storeName.uppercase(),
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isEcoModeEnabled) AppColors.Slate else AppColors.TealLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        liveCart.itemName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isEcoModeEnabled) AppColors.TextPrimary else Color.White,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Per person",
                                fontSize = 11.sp,
                                color = if (isEcoModeEnabled) AppColors.Slate else Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                "€${String.format("%.2f", costPerPerson)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isEcoModeEnabled) EcoTheme.textHighlight else Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Total",
                                fontSize = 11.sp,
                                color = if (isEcoModeEnabled) AppColors.Slate else Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                "€${String.format("%.2f", liveCart.totalCost)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEcoModeEnabled) AppColors.TextPrimary else Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        liveCart.createdAt,
                        fontSize = 12.sp,
                        color = if (isEcoModeEnabled) AppColors.Slate else Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            // Details card
            Card(
                shape = shapeOrRect(AppShapes.card),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(if (isEcoModeEnabled) 0.dp else 2.dp),
                border = if (isEcoModeEnabled) BorderStroke(1.dp, AppColors.LightSlate) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pool Info", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 12.dp))
                    val dateTimeParts = liveCart.meetingDateTime.split(" ")
                    val date = dateTimeParts.getOrNull(0) ?: liveCart.meetingDateTime
                    val time = dateTimeParts.getOrNull(1) ?: "N/A"
                    InfoRow(
                        Icons.Filled.Group,
                        "HOSTED BY",
                        "${liveCart.shopperName} · ${liveCart.shopperGender}"
                    )
                    InfoRow(Icons.Default.LocationOn, "MEET AT", liveCart.meetingPoint)
                    InfoRow(Icons.Default.DateRange, "DATE", dateTimeParts.getOrNull(0) ?: liveCart.meetingDateTime)
                    if (dateTimeParts.size > 1) {
                        // FIX: Replaced CheckCircle with a clock/time icon
                        InfoRow(Icons.Outlined.Notifications, "TIME", dateTimeParts[1])
                    }
                    InfoRow(Icons.Outlined.ShoppingCart, "CONTACT", liveCart.contactInfo)
                }
            }

            // Members card
            Card(
                shape = shapeOrRect(AppShapes.card),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(if (isEcoModeEnabled) 0.dp else 2.dp),
                border = if (isEcoModeEnabled) BorderStroke(1.dp, AppColors.LightSlate) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Members", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "${liveCart.filledSlots}/${liveCart.totalSlots}",
                            fontWeight = FontWeight.Bold,
                            color = EcoTheme.primary,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    SlotProgressBar(filled = liveCart.filledSlots, total = liveCart.totalSlots)
                    Spacer(modifier = Modifier.height(16.dp))
                    liveCart.joinedUsers.forEach { member ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarCircle(name = member, size = 32, fontSize = 13)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(member, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            if (member == liveCart.shopperName) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EcoTheme.primary.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        "Host",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoTheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Action buttons
            if (isMember) {
                PrimaryButton(
                    text = if (isEcoModeEnabled) "Open Group Chat" else "💬 Open Group Chat",
                    onClick = { showChatDialog = true },
                    containerColor = EcoTheme.success
                )
            } else {
                Card(
                    shape = shapeOrRect(AppShapes.card),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEcoModeEnabled) AppColors.EcoSurface else Color(0xFFFFF3E0)
                    )
                ) {
                    Text(
                        "Join this cart from the feed to access the group chat",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = if (isEcoModeEnabled) AppColors.TextSecondary else AppColors.Amber,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (liveCart.shopperName == currentUser) {
                OutlinedButton(
                    onClick = { onNavigateToEdit() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = shapeOrRect(AppShapes.button),
                    border = BorderStroke(1.5.dp, EcoTheme.warning),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EcoTheme.warning)
                ) {
                    Text("Edit My Pool", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            OutlinedButton(
                onClick = { onNavigateBack() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = shapeOrRect(AppShapes.button),
                border = BorderStroke(1.dp, AppColors.LightSlate),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Slate)
            ) {
                Text("Back to Feed", fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Chat dialog
        if (showChatDialog) {
            AlertDialog(
                onDismissRequest = { showChatDialog = false },
                shape = shapeOrRect(AppShapes.dialog),
                containerColor = Color.White,
                title = {
                    Column {
                        Text(
                            "Group Chat",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            liveCart.itemName,
                            fontSize = 13.sp,
                            color = AppColors.Slate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                text = {
                    Column {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(240.dp),
                            shape = shapeOrRect(RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                if (liveCart.messages.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(
                                            "No messages yet. Say hi! 👋",
                                            color = AppColors.Slate,
                                            fontSize = 14.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                } else {
                                    liveCart.messages.forEach { msg ->
                                        val isMe = msg.senderName == currentUser
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(bottom = 3.dp)
                                            ) {
                                                if (!isMe) {
                                                    AvatarCircle(name = msg.senderName, size = 20, fontSize = 10)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                }
                                                Text(
                                                    if (isMe) "You" else msg.senderName,
                                                    fontSize = 11.sp,
                                                    color = AppColors.Slate,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(
                                                    topStart = 12.dp,
                                                    topEnd = 12.dp,
                                                    bottomStart = if (isMe) 12.dp else 4.dp,
                                                    bottomEnd = if (isMe) 4.dp else 12.dp
                                                ),
                                                color = if (isMe) EcoTheme.primary else Color.White
                                            ) {
                                                Text(
                                                    msg.text,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    fontSize = 14.sp,
                                                    color = if (isMe) Color.White else AppColors.TextPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Message...", fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                shape = shapeOrRect(RoundedCornerShape(24.dp)),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = AppColors.LightSlate,
                                    focusedBorderColor = EcoTheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        val index = dummyCarts.indexOfFirst { it.id == cart.id }

                                        if (index != -1) {
                                            val updatedCart = dummyCarts[index]
                                            val msgs = updatedCart.messages.toMutableList()

                                            msgs.add(
                                                ChatMessage(
                                                    senderName = currentUser,
                                                    text = chatInput,
                                                    time = "Just now"
                                                )
                                            )

                                            dummyCarts[index] = updatedCart.copy(messages = msgs)
                                            chatInput = ""
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EcoTheme.primary),
                                shape = shapeOrRect(CircleShape),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Text("→", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showChatDialog = false }) {
                        Text("Close", fontWeight = FontWeight.SemiBold, color = EcoTheme.primary)
                    }
                }
            )
        }
    }
}

// ==========================================
// EDIT POOL SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPoolScreen(cart: CartPost, onNavigateBack: () -> Unit) {
    var itemName by remember { mutableStateOf(cart.itemName) }
    var storeName by remember { mutableStateOf(cart.storeName) }
    var totalCost by remember { mutableStateOf(cart.totalCost.toString()) }
    var totalSlots by remember { mutableStateOf(cart.totalSlots.toString()) }
    var meetingPoint by remember { mutableStateOf(cart.meetingPoint) }
    var contactInfo by remember { mutableStateOf(cart.contactInfo) }
    var meetingDateTime by remember { mutableStateOf(cart.meetingDateTime) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(context, { _, h, m ->
        meetingDateTime = "$meetingDateTime ${String.format("%02d:%02d", h, m)}"
    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

    val datePickerDialog = DatePickerDialog(context, { _, y, mo, d ->
        meetingDateTime = "$y-${String.format("%02d", mo + 1)}-${String.format("%02d", d)}"
        timePickerDialog.show()
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    val parsedSlots = totalSlots.toIntOrNull() ?: 0
    val isSlotsValid = parsedSlots >= 2
    val canSave = itemName.isNotBlank() && totalCost.isNotBlank() && isSlotsValid

    Scaffold(
        containerColor = EcoTheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Edit Pool", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = AppColors.TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EcoTheme.topBar)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormSection(title = "What are you buying?") {
                AppTextField(value = itemName, onValueChange = { itemName = it }, label = "Item name")
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(value = storeName, onValueChange = { storeName = it }, label = "Store name")
            }

            FormSection(title = "Cost & Slots") {
                AppTextField(
                    value = totalCost,
                    onValueChange = { totalCost = it },
                    label = "Total cost (€)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = totalSlots,
                    onValueChange = { totalSlots = it },
                    label = { Text("Number of people (min 2)", fontSize = 14.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = totalSlots.isNotEmpty() && !isSlotsValid,
                    supportingText = {
                        if (totalSlots.isNotEmpty() && !isSlotsValid)
                            Text("Minimum 2 people required", color = AppColors.Rose, fontSize = 12.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = shapeOrRect(AppShapes.input),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoTheme.primary,
                        focusedLabelColor = EcoTheme.primary,
                        errorBorderColor = AppColors.Rose,
                        unfocusedBorderColor = AppColors.LightSlate,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
            }

            FormSection(title = "Meeting Details") {
                AppTextField(value = meetingPoint, onValueChange = { meetingPoint = it }, label = "Safe meeting point")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = meetingDateTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date & Time", fontSize = 14.sp) },
                    trailingIcon = {
                        if (!isEcoModeEnabled) {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.DateRange, null, tint = EcoTheme.primary)
                            }
                        } else {
                            TextButton(onClick = { datePickerDialog.show() }) {
                                Text("SET", fontWeight = FontWeight.Bold, color = EcoTheme.primary, fontSize = 12.sp)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = shapeOrRect(AppShapes.input),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoTheme.primary,
                        unfocusedBorderColor = AppColors.LightSlate,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(value = contactInfo, onValueChange = { contactInfo = it }, label = "Contact info")
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text = "Save Changes",
                onClick = {
                    if (canSave) {
                        val index = dummyCarts.indexOfFirst { it.id == cart.id }
                        if (index != -1) {
                            dummyCarts[index] = cart.copy(
                                itemName = itemName,
                                storeName = storeName,
                                totalCost = totalCost.toDoubleOrNull() ?: cart.totalCost,
                                totalSlots = parsedSlots,
                                meetingPoint = meetingPoint,
                                meetingDateTime = meetingDateTime,
                                contactInfo = contactInfo,
                                messages = cart.messages
                            )
                        }
                        onNavigateBack()
                    }
                },
                enabled = canSave,
                containerColor = EcoTheme.warning
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}