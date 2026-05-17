package com.jalSanchay.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.viewmodel.AuthState
import com.jalSanchay.tracker.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var householdSize by remember { mutableStateOf("3-4 members") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmError by remember { mutableStateOf("") }
    var cityError by remember { mutableStateOf("") }

    val householdOptions = listOf("1-2 members", "3-4 members", "5+ members")

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Join the water-saving community",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Full Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = "" },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = nameError.isNotEmpty(),
                supportingText = if (nameError.isNotEmpty()) { { Text(nameError) } } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = "" },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = emailError.isNotEmpty(),
                supportingText = if (emailError.isNotEmpty()) { { Text(emailError) } } else null,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = "" },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Toggle")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError.isNotEmpty(),
                supportingText = if (passwordError.isNotEmpty()) { { Text(passwordError) } } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmError = "" },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Toggle")
                    }
                },
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = confirmError.isNotEmpty(),
                supportingText = if (confirmError.isNotEmpty()) { { Text(confirmError) } } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            // City
            OutlinedTextField(
                value = city,
                onValueChange = { city = it; cityError = "" },
                label = { Text("City / Location") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                isError = cityError.isNotEmpty(),
                supportingText = if (cityError.isNotEmpty()) { { Text(cityError) } } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Household Size Dropdown
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = householdSize,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Household Size") },
                    leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary)
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    householdOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                householdSize = option
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    var valid = true
                    if (name.isBlank()) { nameError = "Name is required"; valid = false }
                    if (email.isBlank()) { emailError = "Email is required"; valid = false }
                    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "Invalid email format"; valid = false }
                    if (password.length < 6) { passwordError = "Minimum 6 characters"; valid = false }
                    if (confirmPassword != password) { confirmError = "Passwords don't match"; valid = false }
                    if (city.isBlank()) { cityError = "City is required"; valid = false }
                    if (valid) {
                        viewModel.registerUser(name, email, password, city, householdSize)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Create Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Login",
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBack() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
