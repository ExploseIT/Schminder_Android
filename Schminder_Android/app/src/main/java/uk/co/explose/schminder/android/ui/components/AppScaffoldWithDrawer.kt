

package uk.co.explose.schminder.android.ui.components

import android.content.Context
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
<<<<<<< HEAD
=======
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
>>>>>>> dfd0eec (v1.0.1.16)
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
<<<<<<< HEAD
=======
import androidx.compose.material3.TextButton
>>>>>>> dfd0eec (v1.0.1.16)
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
<<<<<<< HEAD
=======
import androidx.compose.runtime.collectAsState
>>>>>>> dfd0eec (v1.0.1.16)
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.model.user.UserRepo
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
<<<<<<< HEAD
import uk.co.explose.schminder.android.core.PreferencesKeys
=======
import uk.co.explose.schminder.android.core.ApiResponse
import uk.co.explose.schminder.android.core.AppRepo
import uk.co.explose.schminder.android.core.AppToast
import uk.co.explose.schminder.android.model.profile.UserProfile
import uk.co.explose.schminder.android.model.profile.UserProfileResponse
import uk.co.explose.schminder.android.model.profile.enUserStatus
import uk.co.explose.schminder.android.preferences.PrefRepo
import uk.co.explose.schminder.android.repo.Resource
>>>>>>> dfd0eec (v1.0.1.16)

val Context.dataStore by preferencesDataStore(name = "user_prefs")

@OptIn(ExperimentalGetImage::class)
@Composable
fun AppScaffoldWithDrawer(
    navController: NavHostController,
    userProfileResponse: Resource<UserProfileResponse>,
    content: @Composable (PaddingValues) -> Unit,
) {
    val context = LocalContext.current
    val showCreateProfileDialog = remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val up = when (userProfileResponse) {
        is Resource.Success -> {
            userProfileResponse.data.apiData
        }
        is Resource.Error -> {
            val errMsg = userProfileResponse.message
            AppToast(context).showToast("Error: $errMsg")
            UserProfile()
        }
        is Resource.Empty -> {
            //var errMsg = "Error in retrieving data \nMay require reloading data in Settings -> App Info"
            //AppToast(context).showToast("$errMsg")
            UserProfile()
        }
        is Resource.Loading -> {
            UserProfile()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                if (up.userIsLoggedIn()) {
                    Text(
                        buildAnnotatedString {
                            append("Signed in as: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(up.getUserName())
                            }
                        },
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else {
                    Text(
                        buildAnnotatedString {
                            append("Not signed in: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Guest mode")
                            }
                        },
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Divider()
<<<<<<< HEAD
                NavigationDrawerItem(
                    label = { Text("Create Profile") },
                    selected = false,
                    onClick = {
                        showCreateProfileDialog.value = true
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Edit Profile") },
                    selected = false,
                    onClick = { /* TODO */ }
=======
                if (up.user_enabled == false) {
                    AnimatedDrawerItem(
                        text = "Username profile disabled",
                        onClick = {
                            //showCreateProfileDialog.value = true
                        }
                    )
                }
                else if (!up.userIsLoggedIn()) {
                    AnimatedDrawerItem(
                        text = "Create a Schminder user name",
                        onClick = {
                            showCreateProfileDialog.value = true
                        }
                    )
                }
                else {
                    AnimatedDrawerItem(
                        text = "Edit User Name Profile",
                        onClick = { AppToast(context).showToast("Coming in a following update") }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    AnimatedDrawerItem(
                        text = "Send message or question?",
                        onClick = { AppToast(context).showToast("Coming in a following update") }
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                AnimatedDrawerItem(
                    text = "Send message or question?",
                    onClick = { AppToast(context).showToast("Coming in a following update") }
>>>>>>> dfd0eec (v1.0.1.16)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                SchminderTopBar(
                    up = up,
                    showBackButton = currentRoute !in listOf("home", "medications", "mockup"),
                    onBackClick = { navController.popBackStack() },
                    onNotificationClick = { /* TODO */ },
                    onProfileClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                AppBottomBar(currentRoute = currentRoute, navController = navController)
            }
        ) { innerPadding ->
            content(innerPadding) // Pass padding to actual screen content
        }
    }

    if (showCreateProfileDialog.value) {
        CreateProfileDialog(
            onConfirm = { username ->
                showCreateProfileDialog.value = false
                scope.launch {
<<<<<<< HEAD
                    val result = UserRepo(context).createProfile(username)
                    result.onSuccess { message ->
                        authMode.value = username
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }.onFailure { error ->
                        Toast.makeText(context, error.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show()
=======
                    val resp = UserRepo.updateUsername(username)
                    val upp = when (resp) {
                        is Resource.Success -> {
                            resp.data.apiData
                        }
                        else -> {
                            UserProfile.fromUsername(username)
                        } // This needs updating
                    }
                    val response = UserRepo.userProfileCreate(upp)
                    val up = when (response) {
                        is Resource.Success -> {
                            response.data
                        }
                        is Resource.Error -> {
                            ApiResponse(
                                apiSuccess = false,
                                apiMessage = response.message ?: "Unknown error",
                                apiData = UserProfile()
                            )
                        }

                        else -> {
                            ApiResponse(
                                apiSuccess = false,
                                apiMessage = "Unexpected error",
                                apiData = UserProfile()
                            )
                        }
                    }
                    if (up.apiSuccess == false) {
                        Toast.makeText(context, up.apiMessage, Toast.LENGTH_SHORT).show()
                    }
                    else if (up.apiData.user_alreadyexists == true) {
                        Toast.makeText(context, "Username already exists", Toast.LENGTH_SHORT).show()
>>>>>>> dfd0eec (v1.0.1.16)
                    }
                }
            },
            onDismiss = {
                showCreateProfileDialog.value = false
            }
        )
    }
<<<<<<< HEAD

}


=======
}

>>>>>>> dfd0eec (v1.0.1.16)
@Composable
fun CreateProfileDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("Guest") }
<<<<<<< HEAD

    LaunchedEffect(Unit) {
        // Load the last username when dialog opens
        username = getUsername(context)
=======
    val regex = Regex("^[A-Z][a-zA-Z0-9]{5,19}$") // 6 to 20 characters

    LaunchedEffect(Unit) {
        // Load the last username when dialog opens
        username = PrefRepo.getUsername(context)
>>>>>>> dfd0eec (v1.0.1.16)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
<<<<<<< HEAD
        title = { Text("Create Profile") },
        text = {
            Column {
                Text("Choose a username")
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Username") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(username)
                // Save the username when confirmed
                CoroutineScope(Dispatchers.IO).launch {
                    saveUsername(context, username)
                }
            }) {
=======
        title = {
            Text(
                text = "Create Username Profile",
                color = Color.Blue, // Or any other Color
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif, // Change to your desired font
                fontWeight = FontWeight.Medium // Optional
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { newValue ->
                        // Filter to alphanumeric only and max 20
                        val filtered = newValue.filter { it.isLetterOrDigit() }.take(20)

                        // Capitalize first letter if available
                        username = filtered
                            .takeIf { it.isNotEmpty() }
                            ?.let { it[0].uppercaseChar() + it.drop(1) }
                            ?: ""
                    },
                    label = { Text("Username") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (regex.matches(username)) {
                                onConfirm(username)
                            }
                        }
                    ),
                    isError = username.isNotEmpty() && !regex.matches(username)
                )
                if (username.isNotEmpty() && !regex.matches(username)) {
                    Text(
                        "Username must start with an uppercase letter and be 6–20 alphanumeric characters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = regex.matches(username),
                onClick = {
                    onConfirm(username)
                    // Save the username when confirmed
                    CoroutineScope(Dispatchers.IO).launch {
                        PrefRepo.saveUsername(context, username)
                    }
                }
            ) {
>>>>>>> dfd0eec (v1.0.1.16)
                Text("Create")
            }
        },
        dismissButton = {
<<<<<<< HEAD
            Button(onClick = onDismiss) {
=======
            TextButton(onClick = onDismiss) {
>>>>>>> dfd0eec (v1.0.1.16)
                Text("Cancel")
            }
        }
    )
}


<<<<<<< HEAD
suspend fun saveUsername(context: Context, username: String) {
    context.dataStore.edit { prefs ->
        prefs[PreferencesKeys.USERNAME] = username
    }
}

suspend fun getUsername(context: Context): String {
    return context.dataStore.data
        .map { prefs -> prefs[PreferencesKeys.USERNAME] ?: "SchminderUser" }
        .first()
}
=======

>>>>>>> dfd0eec (v1.0.1.16)
