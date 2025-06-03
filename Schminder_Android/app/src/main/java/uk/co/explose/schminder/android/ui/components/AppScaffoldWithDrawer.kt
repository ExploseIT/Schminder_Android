

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import uk.co.explose.schminder.android.core.PreferencesKeys

val Context.dataStore by preferencesDataStore(name = "user_prefs")

@OptIn(ExperimentalGetImage::class)
@Composable
fun AppScaffoldWithDrawer(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val showCreateProfileDialog = remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val authMode = remember { mutableStateOf("Guest") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Signed in as: ${authMode.value}", modifier = Modifier.padding(16.dp))
                Divider()
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
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                SchminderTopBar(
                    userName = authMode.value,
                    showBackButton = currentRoute !in listOf("home", "plan", "medications", "mockup"),
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
                    val result = UserRepo(context).createProfile(username)
                    result.onSuccess { message ->
                        authMode.value = username
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }.onFailure { error ->
                        Toast.makeText(context, error.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = {
                showCreateProfileDialog.value = false
            }
        )
    }

}


@Composable
fun CreateProfileDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("Guest") }

    LaunchedEffect(Unit) {
        // Load the last username when dialog opens
        username = getUsername(context)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


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
