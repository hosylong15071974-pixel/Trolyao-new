package com.trolyao.newapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Step(val type: String, val x: String = "500", val y: String = "800", val value: String = "100")

class MacroEditorActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { EditorScreen() } }
}

@Composable
private fun EditorScreen() {
 var name by remember { mutableStateOf("Macro 1") }
 var steps by remember { mutableStateOf(listOf(Step("Tap"))) }
 var saved by remember { mutableStateOf(false) }
 val cyan = Color(0xFF00E5FF); val pink = Color(0xFFFF36C7)
 MaterialTheme(colorScheme = darkColorScheme()) {
  Column(Modifier.fillMaxSize().background(Color(0xFF090B12)).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
   Text("MACRO EDITOR", color = cyan, fontSize = 24.sp)
   OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên macro") }, modifier = Modifier.fillMaxWidth())
   Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { listOf("Tap", "Long press", "Swipe", "Delay").forEach { type -> Button(onClick = { steps = steps + Step(type) }) { Text("+ $type") } } }
   steps.forEachIndexed { i, s ->
    Column(Modifier.fillMaxWidth().border(1.dp, Brush.horizontalGradient(listOf(cyan, pink)), RoundedCornerShape(16.dp)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
     Text("Bước ${i+1}: ${s.type}", color = cyan)
     if (s.type != "Delay") Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      OutlinedTextField(value = s.x, onValueChange = { v -> steps = steps.toMutableList().also { it[i] = s.copy(x=v) } }, label = { Text("X") }, modifier = Modifier.weight(1f))
      OutlinedTextField(value = s.y, onValueChange = { v -> steps = steps.toMutableList().also { it[i] = s.copy(y=v) } }, label = { Text("Y") }, modifier = Modifier.weight(1f))
     }
     OutlinedTextField(value = s.value, onValueChange = { v -> steps = steps.toMutableList().also { it[i] = s.copy(value=v) } }, label = { Text(if (s.type == "Delay") "Delay (ms)" else "Thời lượng / số lần") }, modifier = Modifier.fillMaxWidth())
     TextButton(onClick = { steps = steps.filterIndexed { index, _ -> index != i } }) { Text("Xóa bước") }
    }
   }
   Button(onClick = { saved = true }, modifier = Modifier.fillMaxWidth()) { Text("Lưu cấu hình (bản giao diện)") }
   if (saved) Text("Chưa có lưu bền vững; cấu hình này chỉ nằm trong màn hình hiện tại.", color = pink)
   Text("Các bước chưa được thực thi tự động.", color = Color.Gray, fontSize = 12.sp)
  }
 }
}
