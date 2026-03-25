package com.unknown.sancheck.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DuplicateDialog(
    bookTitle: String,
    currentQuantity: Int,
    onDismiss: () -> Unit,
    onIncrementQuantity: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("중복 도서 발견") },
        text = {
            Text("\"$bookTitle\"은(는) 이미 등록된 도서입니다.\n현재 수량: ${currentQuantity}권\n\n수량을 1권 추가하시겠습니까?")
        },
        confirmButton = {
            TextButton(onClick = {
                onIncrementQuantity()
                onDismiss()
            }) {
                Text("수량 추가")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel()
                onDismiss()
            }) {
                Text("취소")
            }
        }
    )
}
