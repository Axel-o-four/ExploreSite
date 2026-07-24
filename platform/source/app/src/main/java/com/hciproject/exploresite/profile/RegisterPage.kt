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
import androidx.compose.ui.graphics.painter.ColorPainter
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
fun RegisterPage(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    currentLanguage: String = "it",
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showNationalityPicker by remember { mutableStateOf(false) }
    var showEmailPopup by remember { mutableStateOf(false) }
    var showPasswordPopup by remember { mutableStateOf(false) }
    var registerError by remember { mutableStateOf<String?>(null) }

    val nationalities = listOf(
        "Italiana", "Francese", "Tedesca", "Spagnola", "Inglese", 
        "Americana", "Cinese", "Giapponese", "Russa", "Indiana"
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { profileImageUri = it.toString() } }

    val isEmailValid = email.contains("@") && email.contains(".")
    val isPasswordValid = password.length >= 8 &&
            password.any { it.isDigit() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isUpperCase() }

    val isFormValid = fullName.isNotBlank() &&
            isEmailValid &&
            isPasswordValid &&
            dateOfBirth.isNotBlank() &&
            nationality.isNotBlank()

    var tRegistrati by remember { mutableStateOf("Registrati") }
    var tNomeCompleto by remember { mutableStateOf("Nome completo") }
    var tEmail by remember { mutableStateOf("Email") }
    var tPassword by remember { mutableStateOf("Password") }
    var tDataDiNascita by remember { mutableStateOf("Data di nascita") }
    var tNazionalita by remember { mutableStateOf("Nazionalità") }
    var tEmailSuggestion by remember { mutableStateOf("L'email deve avere una forma simile a email@dominio.com") }
    var tPasswordSuggestion by remember { mutableStateOf("La password deve avere almeno 8 caratteri, una lettera maiuscola, una minuscola e un numero") }
    var tUserExists by remember { mutableStateOf("L'utente esiste già") }
    var tHoCapito by remember { mutableStateOf("Ho capito") }

    LaunchedEffect(currentLanguage) {
        if (currentLanguage != "it") {
            tRegistrati = TranslationManager.translate("Registrati", currentLanguage)
            tNomeCompleto = TranslationManager.translate("Nome completo", currentLanguage)
            tEmail = TranslationManager.translate("Email", currentLanguage)
            tPassword = TranslationManager.translate("Password", currentLanguage)
            tDataDiNascita = TranslationManager.translate("Data di nascita", currentLanguage)
            tNazionalita = TranslationManager.translate("Nazionalità", currentLanguage)
            tEmailSuggestion = TranslationManager.translate("L'email deve avere una forma simile a email@dominio.com", currentLanguage)
            tPasswordSuggestion = TranslationManager.translate("La password deve avere almeno 8 caratteri, una lettera maiuscola, una minuscola e un numero", currentLanguage)
            tUserExists = TranslationManager.translate("L'utente esiste già", currentLanguage)
            tHoCapito = TranslationManager.translate("Ho capito", currentLanguage)
        }
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
                }) { Text(tHoCapito) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Annulla") } }
        ) { DatePicker(state = datePickerState) }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8F8FF)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), bottom = paddingValues.calculateBottomPadding())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Color.White, CircleShape).size(40.dp)
                ) { Icon(painter = painterResource(id = R.drawable.left_arrow), contentDescription = "Back", tint = Color.Black, modifier = Modifier.size(24.dp)) }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = tRegistrati, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(120.dp).clickable { launcher.launch("image/*") },
                            shape = CircleShape, color = Color.White, shadowElevation = 4.dp
                        ) {
                            if (profileImageUri != null) {
                                AsyncImage(model = profileImageUri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                            } else {
                                Image(painter = painterResource(id = R.drawable.default_profile_picture), contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                            }
                        }
                    }
                }

                item { EditInfoField(label = tNomeCompleto, value = fullName, placeholder = "", onValueChange = { fullName = it }) }
                
                item {
                    Box {
                        EditInfoField(
                            label = tEmail, value = email, placeholder = "", onValueChange = { email = it; registerError = null },
                            isError = (email.isNotEmpty() && !isEmailValid) || registerError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            trailingIcon = {
                                if (email.isNotEmpty() && !isEmailValid) {
                                    IconButton(onClick = { showEmailPopup = !showEmailPopup }) {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.Red)
                                    }
                                }
                            }
                        )
                        if (showEmailPopup && email.isNotEmpty() && !isEmailValid) {
                            Popup(alignment = Alignment.TopCenter, onDismissRequest = { showEmailPopup = false }, offset = IntOffset(0, -120)) {
                                Surface(shape = RoundedCornerShape(12.dp), color = Color.Black.copy(alpha = 0.9f), shadowElevation = 8.dp, modifier = Modifier.padding(horizontal = 32.dp)) {
                                    Text(text = tEmailSuggestion, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    if (registerError != null) {
                        Text(text = tUserExists, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                    }
                }

                item {
                    Box {
                        EditInfoField(
                            label = tPassword, value = password, placeholder = "", onValueChange = { password = it },
                            isPassword = true, passwordVisible = passwordVisible, onPasswordToggle = { passwordVisible = !passwordVisible },
                            isError = password.isNotEmpty() && !isPasswordValid,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                if (password.isNotEmpty() && !isPasswordValid) {
                                    IconButton(onClick = { showPasswordPopup = !showPasswordPopup }) {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.Red)
                                    }
                                }
                            }
                        )
                        if (showPasswordPopup && password.isNotEmpty() && !isPasswordValid) {
                            Popup(alignment = Alignment.TopCenter, onDismissRequest = { showPasswordPopup = false }, offset = IntOffset(0, -150)) {
                                Surface(shape = RoundedCornerShape(12.dp), color = Color.Black.copy(alpha = 0.9f), shadowElevation = 8.dp, modifier = Modifier.padding(horizontal = 32.dp)) {
                                    Text(text = tPasswordSuggestion, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item { ClickableEditField(label = tDataDiNascita, value = dateOfBirth, placeholder = "", onClick = { showDatePicker = true }) }

                item {
                    Box {
                        ClickableEditField(
                            label = tNazionalita, value = nationality, placeholder = "", onClick = { showNationalityPicker = true },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                        )
                        DropdownMenu(expanded = showNationalityPicker, onDismissRequest = { showNationalityPicker = false }, modifier = Modifier.fillMaxWidth(0.9f)) {
                            nationalities.forEach { nation ->
                                DropdownMenuItem(text = { Text(nation) }, onClick = { nationality = nation; showNationalityPicker = false })
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        val user = User(fullName, email, password, dateOfBirth, nationality, profileImageUri)
                        if (UserManager.registerUser(context, user)) {
                            UserManager.saveCurrentUser(context, user)
                            onRegisterSuccess()
                        } else {
                            registerError = "User exists"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text(text = tRegistrati, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
