package uk.co.explose.schminder.android.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.ui.components.SchminderTopBar

data class RefillItem(
    val id: Int,
    val title: String,
    val description: String,
    val checked: Boolean
)

@Composable
fun PlanScreen(navController: NavHostController) {
    AppGlobal.logEvent("test_event", mapOf("origin" to "Schminder - Plan"))

    val userName = AppGlobal.doAPGDataRead().mFirebaseTokenInfo?.fbtUserName ?: "Guest"

    val refillList = remember {
        listOf(
            RefillItem(1, "Stay on top of your prescription refills", "Track your meds stock and set refill reminders...", true),
            RefillItem(2, "Upcoming refill reminder", "Reminder for your vitamin D supplement refill...", false)
        )
    }

    Scaffold(
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {

            items(refillList) { refill ->
                RefillCard(
                    title = refill.title,
                    description = refill.description,
                    checked = refill.checked,
                    onCheckedChange = { /* Handle change */ },
                    onManageAllClick = { /* Handle manage all click */ },
                    onReviewMedsClick = { /* Handle review meds click */ }
                )
            }
        }
    }
}






