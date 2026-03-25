package com.unknown.sancheck.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unknown.sancheck.BuildConfig
import com.unknown.sancheck.SanCheckApp
import com.unknown.sancheck.util.ExcelManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as SanCheckApp).container
    val prefsManager = container.prefsManager
    val repository = container.bookRepository
    val excelManager = container.excelManager
    val coroutineScope = rememberCoroutineScope()

    var ttbKey by remember { mutableStateOf(prefsManager.ttbKey.ifEmpty { BuildConfig.ALADIN_TTB_KEY }) }

    // Excel export launcher
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                try {
                    val books = withContext(Dispatchers.IO) { repository.getAllBooksSync() }
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(it)?.use { os ->
                            excelManager.exportToExcel(books, os)
                        }
                    }
                    Toast.makeText(context, "내보내기 완료 (${books.size}권)", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "내보내기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Excel import launcher
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                try {
                    val books = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(it)?.use { inputStream ->
                            excelManager.importFromExcel(inputStream)
                        } ?: emptyList()
                    }
                    withContext(Dispatchers.IO) {
                        books.forEach { book ->
                            repository.insertBook(book)
                        }
                    }
                    Toast.makeText(context, "가져오기 완료 (${books.size}권)", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "가져오기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TTBKey
            Text("알라딘 API 키", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = ttbKey,
                onValueChange = { ttbKey = it },
                label = { Text("TTBKey") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = {
                    prefsManager.ttbKey = ttbKey
                    Toast.makeText(context, "API 키가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("API 키 저장")
            }

            Divider()

            // Excel
            Text("데이터 관리", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = { exportLauncher.launch("sancheck_books.xlsx") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Excel로 내보내기 (.xlsx)")
            }

            OutlinedButton(
                onClick = {
                    importLauncher.launch(
                        arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Excel에서 가져오기 (.xlsx)")
            }

            Divider()

            // App info
            Text("앱 정보", style = MaterialTheme.typography.titleMedium)
            Text("SanCheck v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
            Text("홈 북 라이브러리 트래커", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
