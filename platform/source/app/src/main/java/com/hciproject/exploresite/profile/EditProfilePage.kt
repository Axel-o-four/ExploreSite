package com.hciproject.exploresite.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import com.hciproject.exploresite.R
import com.hciproject.exploresite.TranslationManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePage(
    onBack: () -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val user = CurrentUser

    // Local state initialized with current user data (decrypt password for editing)
    var fullName by remember { mutableStateOf(user?.fullName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf(user?.getDecryptedPassword() ?: "") }
    var dateOfBirth by remember { mutableStateOf(user?.dateOfBirth ?: "") }
    var nationality by remember { mutableStateOf(user?.nationality ?: "") }
    var profileImageUri by remember { mutableStateOf(user?.profileImageUri) }

    var passwordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showNationalityPicker by remember { mutableStateOf(false) }
    var showEmailPopup by remember { mutableStateOf(false) }
    var showPasswordPopup by remember { mutableStateOf(false) }

    val nationalities = listOf(
        "Italiana", "Francese", "Tedesca", "Spagnola", "Inglese", 
        "Americana", "Cinese", "Giapponese", "Russa", "Indiana"
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileImageUri = it.toString() }
    }

    val isEmailValid = email.contains("@") && email.substringAfter("@", "").contains(".")
    
    val isPasswordValid = password.length >= 8 &&
            password.any { it.isDigit() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isUpperCase() }

    val isChanged = user?.let {
        fullName != it.fullName ||
        email != it.email ||
        password != it.getDecryptedPassword() ||
        dateOfBirth != it.dateOfBirth ||
        nationality != it.nationality ||
        profileImageUri != it.profileImageUri
    } ?: true

    val isFormValid = fullName.isNotBlank() &&
            isEmailValid &&
            isPasswordValid &&
            dateOfBirth.isNotBlank() &&
            nationality.isNotBlank()

    var tModificaProfilo by remember { mutableStateOf("Modifica profilo") }
    var tNomeCompleto by remember { mutableStateOf("Nome completo") }
    var tEmail by remember { mutableStateOf("Email") }
    var tPassword by remember { mutableStateOf("Password") }
    var tDataDiNascita by remember { mutableStateOf("Data di nascita") }
    var tNazionalita by remember { mutableStateOf("Nazionalità") }
    var tApplicaModifiche by remember { mutableStateOf("Applica modifiche") }
    var tSuccesso by remember { mutableStateOf("Modifiche applicate con successo") }
    var tHoCapito by remember { mutableStateOf("Ho capito") }
    var tAnnulla by remember { mutableStateOf("Annulla") }
    var tEmailSuggestion by remember { mutableStateOf("L'email deve avere una forma simile a email@dominio.com") }
    var tPasswordSuggestion by remember { mutableStateOf("La password deve avere almeno 8 caratteri, una lettera maiuscola, una minuscola e un numero") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tModificaProfilo = TranslationManager.translate("Modifica profilo", currentLanguage)
            tNomeCompleto = TranslationManager.translate("Nome completo", currentLanguage)
            tEmail = TranslationManager.translate("Email", currentLanguage)
            tPassword = TranslationManager.translate("Password", currentLanguage)
            tDataDiNascita = TranslationManager.translate("Data di nascita", currentLanguage)
            tNazionalita = TranslationManager.translate("Nazionalità", currentLanguage)
            tApplicaModifiche = TranslationManager.translate("Applica modifiche", currentLanguage)
            tSuccesso = TranslationManager.translate("Modifiche applicate con successo", currentLanguage)
            tHoCapito = TranslationManager.translate("Ho capito", currentLanguage)
            tAnnulla = TranslationManager.translate("Annulla", currentLanguage)
            tEmailSuggestion = TranslationManager.translate("L'email deve avere una forma simile a email@dominio.com", currentLanguage)
            tPasswordSuggestion = TranslationManager.translate("La password deve avere almeno 8 caratteri, una lettera maiuscola, una minuscola e un numero", currentLanguage)
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false; onBack() }) {
                    Text(tHoCapito)
                }
            },
            text = { Text(tSuccesso) },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Date(it)
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dateOfBirth = format.format(date)
                    }
                    showDatePicker = false
                }) {
                    Text(tHoCapito)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(tAnnulla)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8FF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.left_arrow),
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = tModificaProfilo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(120.dp)
                                .clickable { launcher.launch("image/*") },
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            val currentUri = profileImageUri
                            if (currentUri != null) {
                                AsyncImage(
                                    model = currentUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(id = R.drawable.default_profile_picture)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.default_profile_picture),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                item {
                    EditInfoField(
                        label = tNomeCompleto,
                        value = fullName,
                        placeholder = user?.fullName ?: "",
                        onValueChange = { fullName = it }
                    )
                }
                item {
                    Box {
                        EditInfoField(
                            label = tEmail,
                            value = email,
                            placeholder = user?.email ?: "",
                            onValueChange = { email = it },
                            isError = email.isNotEmpty() && !isEmailValid,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            trailingIcon = {
                                if (email.isNotEmpty() && !isEmailValid) {
                                    IconButton(onClick = { showEmailPopup = !showEmailPopup }) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Info",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        )
                        
                        if (showEmailPopup && email.isNotEmpty() && !isEmailValid) {
                            Popup(
                                alignment = Alignment.TopCenter,
                                onDismissRequest = { showEmailPopup = false },
                                offset = IntOffset(0, -120)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Black.copy(alpha = 0.9f),
                                    shadowElevation = 8.dp,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                ) {
                                    Text(
                                        text = tEmailSuggestion,
                                        color = Color.White,
                                        modifier = Modifier.padding(12.dp),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Box {
                        EditInfoField(
                            label = tPassword,
                            value = password,
                            placeholder = "********",
                            onValueChange = { password = it },
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onPasswordToggle = { passwordVisible = !passwordVisible },
                            isError = password.isNotEmpty() && !isPasswordValid,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                if (password.isNotEmpty() && !isPasswordValid) {
                                    IconButton(onClick = { showPasswordPopup = !showPasswordPopup }) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Info",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        )

                        if (showPasswordPopup && password.isNotEmpty() && !isPasswordValid) {
                            Popup(
                                alignment = Alignment.TopCenter,
                                onDismissRequest = { showPasswordPopup = false },
                                offset = IntOffset(0, -150)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Black.copy(alpha = 0.9f),
                                    shadowElevation = 8.dp,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                ) {
                                    Text(
                                        text = tPasswordSuggestion,
                                        color = Color.White,
                                        modifier = Modifier.padding(12.dp),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    ClickableEditField(
                        label = tDataDiNascita,
                        value = dateOfBirth,
                        placeholder = user?.dateOfBirth ?: "",
                        onClick = { showDatePicker = true }
                    )
                }
                item {
                    Box {
                        ClickableEditField(
                            label = tNazionalita,
                            value = nationality,
                            placeholder = user?.nationality ?: "",
                            onClick = { showNationalityPicker = true },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        )
                        DropdownMenu(
                            expanded = showNationalityPicker,
                            onDismissRequest = { showNationalityPicker = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            nationalities.forEach { nation ->
                                DropdownMenuItem(
                                    text = { Text(nation) },
                                    onClick = {
                                        nationality = nation
                                        showNationalityPicker = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val newUser = User(
                            fullName = fullName,
                            email = email,
                            password = User.encryptPassword(password), // Encrypt on save
                            dateOfBirth = dateOfBirth,
                            nationality = nationality,
                            profileImageUri = profileImageUri,
                            visitedPois = user?.visitedPois ?: emptyList() // Preserve visits
                        )
                        UserManager.saveCurrentUser(context, newUser)
                        showSuccessDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = isChanged && isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = tApplicaModifiche,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EditInfoField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onPasswordToggle: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = if (isError) androidx.compose.foundation.BorderStroke(1.dp, Color.Red) else null
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isError) Color.Red else Color.Gray,
                fontSize = 12.sp
            )
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, color = Color.LightGray) },
                isError = isError,
                keyboardOptions = keyboardOptions,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true,
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (trailingIcon != null) {
                            trailingIcon()
                        }
                        if (isPassword) {
                            IconButton(onClick = { onPasswordToggle?.invoke() }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
fun ClickableEditField(
    label: String,
    value: String,
    placeholder: String,
    onClick: () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.ifEmpty { placeholder },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = if (value.isNotEmpty()) Color.Black else Color.LightGray
                )
                trailingIcon?.invoke()
            }
        }
    }
}
