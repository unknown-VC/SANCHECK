package com.unknown.sancheck.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddBookDialog(
    onDismiss: () -> Unit,
    onScanBarcode: () -> Unit,
    onInternetSearch: () -> Unit,
    onManualInput: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("책 추가") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DialogOption(
                    icon = Icons.Default.CameraAlt,
                    text = "바코드 스캔",
                    onClick = {
                        onDismiss()
                        onScanBarcode()
                    }
                )
                DialogOption(
                    icon = Icons.Default.Search,
                    text = "인터넷 검색",
                    onClick = {
                        onDismiss()
                        onInternetSearch()
                    }
                )
                DialogOption(
                    icon = Icons.Default.Edit,
                    text = "직접 입력",
                    onClick = {
                        onDismiss()
                        onManualInput()
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun DialogOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text)
        }
    }
}
