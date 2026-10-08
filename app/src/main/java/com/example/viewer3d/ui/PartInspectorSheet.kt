package com.example.viewer3d.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ModelPart

@Composable
fun PartInspectorSheet(
    part: ModelPart?,
    allParts: List<ModelPart> = emptyList(),
    onSelectPart: ((String) -> Unit)? = null,
    onDeselect: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = part != null,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        if (part == null) return@AnimatedVisibility

        val partColor = Color(part.colorHex)
        val currentIndex = allParts.indexOfFirst { it.id == part.id }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("part_inspector_sheet"),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xF2111827),
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, partColor.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header with color badge, title, and stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(partColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = part.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            if (allParts.isNotEmpty() && currentIndex >= 0) {
                                Text(
                                    text = "Part ${currentIndex + 1} of ${allParts.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    // Stepper buttons & close
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (allParts.size > 1 && onSelectPart != null) {
                            IconButton(
                                onClick = {
                                    val prevIdx = if (currentIndex > 0) currentIndex - 1 else allParts.size - 1
                                    onSelectPart(allParts[prevIdx].id)
                                },
                                modifier = Modifier.size(32.dp).testTag("prev_part_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Part",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val nextIdx = (currentIndex + 1) % allParts.size
                                    onSelectPart(allParts[nextIdx].id)
                                },
                                modifier = Modifier.size(32.dp).testTag("next_part_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Part",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDeselect,
                            modifier = Modifier.size(32.dp).testTag("close_part_inspector")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Deselect",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Pedagogical Description
                Text(
                    text = part.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE2E8F0)
                )

                if (part.pedagogicalNotes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x330284C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = part.pedagogicalNotes,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFBAE6FD),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                if (part.formulaOrDetail.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Formula / Role: ${part.formulaOrDetail}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
