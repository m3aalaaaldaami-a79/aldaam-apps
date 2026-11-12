package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PreferencesManager
import com.example.model.PraiseRecord
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextOnDarkPrimary
import com.example.ui.theme.TextOnDarkSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    prefsManager: PreferencesManager,
    modifier: Modifier = Modifier
) {
    var historyList by remember { mutableStateOf(prefsManager.getHistory()) }
    var allTimeTotal by remember { mutableStateOf(prefsManager.getAllTimeTotal()) }
    var showClearDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "سجل الأذكار والإحصائيات",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = GoldLight
                )
                Text(
                    text = "توثيق التسبيحات والأوراد المنجزة",
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp,
                    color = TextOnDarkSecondary
                )
            }

            if (historyList.isNotEmpty()) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "مسح السجل",
                        tint = TextOnDarkSecondary
                    )
                }
            }
        }

        // Summary Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, EmeraldBorder, RoundedCornerShape(20.dp)),
            color = EmeraldSurface,
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "إجمالي التسبيحات التاريخي",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        color = TextOnDarkSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$allTimeTotal تسبيحة",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = GoldPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmeraldSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // History List
        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TextOnDarkSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد أوراد مسجلة حتى الآن",
                        fontFamily = CairoFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnDarkPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "عند إتمام أي تسبيح في أحد الأبواب، سيظهر توثيقه هنا تلقائياً",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = TextOnDarkSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyList, key = { it.id }) { record ->
                    HistoryItemCard(record = record, dateFormat = dateFormat)
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "مسح سجل الأذكار؟",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في حذف جميع الأوراد المسجلة؟ لن يتم تصفير الإجمالي الكلي.",
                    fontFamily = CairoFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        prefsManager.clearHistory()
                        historyList = emptyList()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("مسح", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }
}

@Composable
fun HistoryItemCard(
    record: PraiseRecord,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(0.5.dp, EmeraldBorder, RoundedCornerShape(16.dp)),
        color = EmeraldSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.dhikrName,
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextOnDarkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = record.doorType,
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = GoldLight
                    )
                    Text(
                        text = "• ${dateFormat.format(Date(record.timestamp))}",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = TextOnDarkSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldSurface)
                    .border(1.dp, EmeraldBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${record.count}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GoldPrimary
                )
            }
        }
    }
}
