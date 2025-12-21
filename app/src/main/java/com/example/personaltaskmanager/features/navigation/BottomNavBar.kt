package com.example.personaltaskmanager.features.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.launch
import com.example.personaltaskmanager.R

enum class NavItem(val label: String, val icon: Int) {
    DASHBOARD("Dashboard", android.R.drawable.ic_menu_recent_history),
    TASKS("Tasks", R.drawable.feature_task_manager_ic_calendar),
    CALENDAR("Calendar", R.drawable.feature_calendar_ic_calendar),
    HABIT("Habit", R.drawable.feature_habit_ic_habit),
    SETTINGS("Settings", android.R.drawable.ic_menu_preferences)
}

@Composable
fun BottomNavBar(
    current: NavItem,
    onSelect: (NavItem) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    val density = LocalDensity.current
    var itemWidth by remember { mutableStateOf(0.dp) }

    val targetIndex = NavItem.values().indexOf(current)
    val offsetX by animateDpAsState(
        targetValue = itemWidth * targetIndex,
        label = "OffsetXAnimation"
    )

    val BlueGreen = Color(0xFF5AE4D9)
    val LightBlueGreenBackground = BlueGreen.copy(alpha = 0.25f)

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
        ) {

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = 8.dp)
                    .width(itemWidth)
                    .height(56.dp)
                    .padding(horizontal = 8.dp)
                    .background(
                        color = LightBlueGreenBackground,
                        shape = RoundedCornerShape(28.dp)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)
            ) {
                NavItem.values().forEachIndexed { idx, item ->

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .onGloballyPositioned {
                                if (idx == 0 && itemWidth == 0.dp) {
                                    itemWidth = with(density) { it.size.width.toDp() }
                                }
                            }
                            .clickable { coroutine.launch { onSelect(item) } },
                        contentAlignment = Alignment.Center
                    ) {

                        val selected = (item == current)

                        val scale by animateFloatAsState(
                            targetValue = if (selected) 1.1f else 1f,
                            label = "IconScale"
                        )

                        val tint by animateColorAsState(
                            targetValue = if (selected) BlueGreen else Color.Gray.copy(alpha = 0.7f),
                            label = "IconTint"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {

                            if (item.icon != 0) {
                                Icon(
                                    painter = painterResource(id = item.icon),
                                    contentDescription = item.label,
                                    tint = tint,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .scale(scale)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.label,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = tint
                            )
                        }
                    }
                }
            }
        }
    }
}
