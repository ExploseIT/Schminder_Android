
package uk.co.explose.schminder.android.helper


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uk.co.explose.schminder.android.model.mpp.Med
import uk.co.explose.schminder.android.ui.viewmodels.settingsObj

import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun MedCard(mp: MedCardParms) {
    val status = getMedStatus(mp.med, mp.dtNow, mp.dayRel, mp.dtRel, mp.objSettings)
    val scale = remember { Animatable(1f) }
    val med = mp.med

    LaunchedEffect(Unit) {
        while (true) {
            scale.animateTo(1.2f, animationSpec = tween(300))
            scale.animateTo(1f, animationSpec = tween(300))
            delay(500)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column (
                modifier = Modifier
                .padding(start = 16.dp, top = 16.dp,
                    bottom = if (status.isNotBlank()) 8.dp else 16.dp)
                .weight(1f)
            ) {
                Text(text = med.medName, style = MaterialTheme.typography.titleMedium)
                Text(text = "Time: ${med.medTimeofday}", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Repeat: ${med.medRepeatType} every ${med.medRepeatCount} ${med.medRepeatInterval.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = status.medsText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = status.medsColour.colour
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    if (status.medsStatus == MedStatusName.MedSTakeNow) {
                        IconButton(
                            onClick = { /* mark as taken */ },
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFBBDEFB), shape = CircleShape) // light blue bg
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Mark as taken",
                                tint = Color.Blue,
                                modifier = Modifier.scale(scale.value)
                            )
                        }
                    }
                }
            }

            if (status.medsTaken) {
                IconButton(onClick = { /* mark as taken */ }) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle, // or Icons.Filled.Medication
                        contentDescription = "Mark as taken",
                        tint = Color.DarkGray
                    )
                }
            }
        }

    }
}

data class MedCardParms (
    var med: Med,
    var dtNow: LocalDateTime,
    var dayRel: LocalDate,
    var dtRel: LocalDateTime,
    var objSettings: settingsObj
)
