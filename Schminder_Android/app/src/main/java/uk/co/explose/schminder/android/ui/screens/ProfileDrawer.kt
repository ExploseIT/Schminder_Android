

package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileDrawer(
    isGuest: Boolean,
    onSignUp: () -> Unit,
    onLogin: () -> Unit,
    onEditProfile: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (isGuest) {
            Text("Guest", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onSignUp) { Text("Create Profile") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onLogin) { Text("Log In") }
        } else {
            Text("Welcome", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onEditProfile) { Text("Edit Profile") }
        }
    }
}


