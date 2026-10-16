package com.trolyao.newapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rikka.shizuku.Shizuku

private val BG = Color(0xFF090B12)
private val PANEL = Color(0xFF151827)
private val CYAN = Color(0xFF00E5FF)
private val PINK = Color(0xFFFF36C7)
private val PURPLE = Color(0xFF9A72FF)

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent { MainScreen(
   status = { shizukuStatus() },
   requestShizuku = { try { if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) Shizuku.requestPermission(401) } catch (_: Exception) {} },
   overlayPermission = { if (!Settings.canDrawOverlays(this)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) },
   startOverlay = { if (Settings.canDrawOverlays(this)) startForegroundService(Intent(this, MacroOverlayService::class.java)) },
   editor = { startActivity(Intent(this, MacroEditorActivity::class.java)) }
  ) }
 }
 private fun shizukuStatus(): String = try {
  when { !Shizuku.pingBinder() -> "Chưa kết nối Shizuku"; Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED -> "Đã cấp quyền Shizuku"; else -> "Chưa cấp quyền Shizuku" }
 } catch (_: Exception) { "Chưa thể kiểm tra Shizuku" }
}

@Composable
private fun MainScreen(status: () -> String, requestShizuku: () -> Unit, overlayPermission: () -> Unit, startOverlay: () -> Unit, editor: () -> Unit) {
 var selected by remember { mutableStateOf("Chưa chọn ứng dụng") }
 var enabled by remember { mutableStateOf(false) }
 MaterialTheme(colorScheme = darkColorScheme(background = BG, surface = PANEL, primary = CYAN, secondary = PINK)) {
  Column(Modifier.fillMaxSize().background(BG).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
   Text("TROLYAO", fontSize = 27.sp, color = Color.White)
   Text("TRỢ LÝ MACRO", color = CYAN)
   RgbCard { Text("Trạng thái Shizuku", color = CYAN); Text(status(), color = Color.White); Button(onClick = requestShizuku, modifier = Modifier.fillMaxWidth()) { Text("Kiểm tra / cấp quyền Shizuku") } }
   RgbCard { Text("Quyền cửa sổ nổi", color = PINK); Text("Cần bật để hiện menu trên ứng dụng khác.", color = Color.LightGray); OutlinedButton(onClick = overlayPermission, modifier = Modifier.fillMaxWidth()) { Text("Mở cài đặt cửa sổ nổi") } }
   RgbCard { Text("Ứng dụng / game", color = CYAN); Text(selected, color = Color.LightGray); OutlinedButton(onClick = { selected = "Bộ chọn ứng dụng sẽ được bổ sung" }, modifier = Modifier.fillMaxWidth()) { Text("Thêm ứng dụng") } }
   RgbCard { Text("Chạy nền", color = PINK); Row(verticalAlignment = Alignment.CenterVertically) { Text(if (enabled) "Đang bật" else "Đang tắt", modifier = Modifier.weight(1f), color = Color.White); Switch(checked = enabled, onCheckedChange = { enabled = it }) }; Button(onClick = startOverlay, modifier = Modifier.fillMaxWidth()) { Text("Hiện menu nổi") } }
   RgbCard { Text("Custom Macro", color = PURPLE); Text("Soạn các bước macro.", color = Color.LightGray); Button(onClick = editor, modifier = Modifier.fillMaxWidth()) { Text("Mở Macro Editor") } }
   Text("Lưu ý: thao tác chạm tự động chưa được triển khai trong bản nền.", color = Color.Gray, fontSize = 12.sp)
  }
 }
}

@Composable
private fun RgbCard(content: @Composable ColumnScope.() -> Unit) {
 Column(Modifier.fillMaxWidth().border(1.dp, Brush.horizontalGradient(listOf(CYAN, PURPLE, PINK)), RoundedCornerShape(20.dp)).background(PANEL, RoundedCornerShape(20.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
}
