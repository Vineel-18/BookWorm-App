package com.bookworm.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.bookworm.app.AppViewModel
import com.bookworm.app.LoginResult
import com.bookworm.app.RegisterResult
import com.bookworm.app.Routes
import com.bookworm.app.ui.components.*
import com.bookworm.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// ─── Login / Register Screen ──────────────────────────────────────────────────
@Composable
fun LoginScreen(vm: AppViewModel, navController: NavController) {
    var showRegister by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        if (showRegister) {
            RegisterBox(
                vm = vm,
                navController = navController,
                onShowLogin = { showRegister = false }
            )
        } else {
            LoginBox(
                vm = vm,
                navController = navController,
                onShowRegister = { showRegister = true }
            )
        }
    }
}

// ─── Login Box ────────────────────────────────────────────────────────────────
@Composable
private fun LoginBox(
    vm: AppViewModel,
    navController: NavController,
    onShowRegister: () -> Unit
) {
    var selectedRole by remember { mutableStateOf("user") }   // "user" | "admin"
    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var toastMsg     by remember { mutableStateOf("") }
    val scope        = rememberCoroutineScope()

    fun showToast(msg: String) {
        scope.launch {
            toastMsg = msg
            delay(2600)
            toastMsg = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Text(
            text = if (selectedRole == "admin") "📚 Book Worm ADMIN" else "📚 Book Worm",
            color = if (selectedRole == "admin") RedAccent else OrangeAccent,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = if (selectedRole == "admin") "Admin Sign In" else "Welcome back",
            color = TextColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = if (selectedRole == "admin") "Access the Book Worm admin panel" else "Sign in to continue reading",
            color = MutedColor,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 22.dp)
        )

        // Role tab switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface2Color, RoundedCornerShape(10.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // User tab
            val userSelected = selectedRole == "user"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (userSelected) OrangeAccent else Color.Transparent,
                        RoundedCornerShape(7.dp)
                    )
                    .clickable { selectedRole = "user" }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "👤 User",
                    color = if (userSelected) Color.White else Color(0xFF666666),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // Admin tab
            val adminSelected = selectedRole == "admin"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (adminSelected) RedAccent else Color.Transparent,
                        RoundedCornerShape(7.dp)
                    )
                    .clickable { selectedRole = "admin" }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "🔐 Admin",
                    color = if (adminSelected) Color.White else Color(0xFF666666),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Admin hint box
        if (selectedRole == "admin") {
            Surface(
                color = RedAccent.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, RedAccent.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🔐 Admin Access Only", color = RedAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Use your admin credentials to access the dashboard.\n" +
                               "Default: admin@bookworm.com / admin123",
                        color = RedAccent,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Email field
        BwTextField(
            value = email,
            onValueChange = { email = it },
            label = "EMAIL ADDRESS",
            placeholder = "you@example.com",
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(14.dp))

        // Password field
        BwTextField(
            value = password,
            onValueChange = { password = it },
            label = "PASSWORD",
            placeholder = "••••••••",
            isPassword = true,
            imeAction = ImeAction.Done
        )
        Spacer(Modifier.height(16.dp))

        // Sign In button
        BwButton(
            text = if (selectedRole == "admin") "Sign In to Admin" else "Sign In",
            color = if (selectedRole == "admin") RedAccent else OrangeAccent,
            onClick = {
                when (val result = vm.login(email.trim(), password)) {
                    is LoginResult.Admin -> navController.navigate(Routes.ADMIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                    is LoginResult.User -> navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                    is LoginResult.InvalidCredentials ->
                        showToast("❌ Invalid credentials")
                }
            }
        )

        // User-only section
        if (selectedRole == "user") {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                Text(" or ", color = MutedColor, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
            }
            Spacer(Modifier.height(12.dp))

            BwOutlineButton(
                text = "Continue as Guest",
                onClick = { vm.continueAsGuest(); navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
            Spacer(Modifier.height(14.dp))

            Row {
                Text("Don't have an account? ", color = MutedColor, fontSize = 13.sp)
                Text(
                    "Create one",
                    color = OrangeAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onShowRegister() }
                )
            }
        } else {
            Spacer(Modifier.height(16.dp))
            Row {
                Text("Not an admin? ", color = MutedColor, fontSize = 13.sp)
                Text(
                    "Sign in as User",
                    color = OrangeAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { selectedRole = "user" }
                )
            }
        }
    }

    // Toast overlay
    if (toastMsg.isNotEmpty()) {
        BwToast(message = toastMsg)
    }
}

// ─── Register Box ─────────────────────────────────────────────────────────────
@Composable
private fun RegisterBox(
    vm: AppViewModel,
    navController: NavController,
    onShowLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirm   by remember { mutableStateOf("") }
    var toastMsg  by remember { mutableStateOf("") }
    val scope     = rememberCoroutineScope()

    fun showToast(msg: String) {
        scope.launch { toastMsg = msg; delay(2600); toastMsg = "" }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📚 Book Worm", color = OrangeAccent, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 6.dp))
        Text("Create account", color = TextColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 4.dp))
        Text("Join Book Worm and start reading today", color = MutedColor, fontSize = 13.sp, modifier = Modifier.padding(bottom = 22.dp))

        // User account tab (display only)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(OrangeAccent, RoundedCornerShape(7.dp))
                .padding(vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("👤 User Account", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Admin accounts cannot be self-registered.",
            color = Color(0xFF555555),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // First/Last name row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BwTextField(value = firstName, onValueChange = { firstName = it }, label = "FIRST NAME", placeholder = "Jane", modifier = Modifier.weight(1f))
            BwTextField(value = lastName,  onValueChange = { lastName  = it }, label = "LAST NAME",  placeholder = "Doe",  modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))

        BwTextField(value = email, onValueChange = { email = it }, label = "EMAIL ADDRESS", placeholder = "you@example.com", keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(14.dp))

        BwTextField(value = password, onValueChange = { password = it }, label = "PASSWORD", placeholder = "Min. 8 characters", isPassword = true)
        Spacer(Modifier.height(14.dp))

        BwTextField(value = confirm,  onValueChange = { confirm  = it }, label = "CONFIRM PASSWORD", placeholder = "Repeat password", isPassword = true, imeAction = ImeAction.Done)
        Spacer(Modifier.height(18.dp))

        BwButton(
            text = "Create Account",
            onClick = {
                if (password != confirm) { showToast("Passwords do not match"); return@BwButton }
                when (vm.register(firstName, lastName, email.trim(), password)) {
                    is RegisterResult.Success          -> {
                        showToast("Account created! Signing you in…")
                        scope.launch {
                            delay(900)
                            navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                        }
                    }
                    is RegisterResult.PasswordTooShort -> showToast("Password must be at least 8 characters")
                    else                               -> showToast("Something went wrong")
                }
            }
        )
        Spacer(Modifier.height(14.dp))

        Row {
            Text("Already have an account? ", color = MutedColor, fontSize = 13.sp)
            Text("Sign in", color = OrangeAccent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onShowLogin() })
        }
    }

    if (toastMsg.isNotEmpty()) BwToast(message = toastMsg)
}
