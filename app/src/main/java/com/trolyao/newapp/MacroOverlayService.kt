package com.trolyao.newapp

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.atomic.AtomicBoolean

class MacroOverlayService : Service() {
 private lateinit var wm: WindowManager
 private var root: LinearLayout? = null
 private var open = false
 private val held = AtomicBoolean(false)
 private var repeatThread: Thread? = null
 private val channel = "trolyao_new_overlay"
 override fun onBind(intent: Intent?): IBinder? = null
 override fun onCreate() {
  super.onCreate()
  if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
  val nm = getSystemService(NotificationManager::class.java)
  if (Build.VERSION.SDK_INT >= 26) nm.createNotificationChannel(NotificationChannel(channel, "Trolyao floating controls", NotificationManager.IMPORTANCE_LOW))
  val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
  val nb = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, channel) else Notification.Builder(this)
  startForeground(22, nb.setContentTitle("Trolyao New").setContentText("Menu nổi đang bật").setSmallIcon(android.R.drawable.ic_media_play).setContentIntent(pi).build())
  wm = getSystemService(WINDOW_SERVICE) as WindowManager
  showHandle()
 }
 override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
 private fun params() = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.END; x=12; y=240 }
 private fun btn(label: String, color: Int, action: () -> Unit) = TextView(this).apply {
  text=label; textSize=14f; typeface=Typeface.DEFAULT_BOLD; gravity=Gravity.CENTER; setTextColor(-1); setPadding(15,12,15,12); alpha=.92f
  background=GradientDrawable().apply { cornerRadius=26f; setColor(0xFF171A2A.toInt()); setStroke(3,color) }
  layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(4,4,4,4) }
  setOnClickListener { action() }
 }
 private fun showHandle() {
  val v=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; addView(btn("T",0xFFFF36C7.toInt()) { toggle() }) }
  root=v
  try { wm.addView(v,params()) } catch (_: Exception) { stopSelf() }
 }
 private fun toggle() {
  if (open) { closeMenu(); return }
  root?.let { try { wm.removeView(it) } catch (_: Exception) {} }
  val panel=LinearLayout(this).apply {
   orientation=LinearLayout.VERTICAL; setPadding(5,5,5,5)
   background=GradientDrawable().apply { cornerRadius=28f; setColor(0xFF10121D.toInt()); setStroke(3,0xFFFF36C7.toInt()) }
   addView(btn("Macro Runner",0xFF00E5FF.toInt()) { Toast.makeText(this@MacroOverlayService,"Engine thao tác chạm chưa được triển khai",Toast.LENGTH_SHORT).show() })
   val mult=btn("Multiply Button Runner",0xFFFF36C7.toInt()) { }
   mult.setOnTouchListener { _, e -> when(e.actionMasked) { MotionEvent.ACTION_DOWN -> { startPlaceholder(); true }; MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> { stopPlaceholder(); true }; else -> true } }
   addView(mult)
   addView(btn("Tab",0xFF9A72FF.toInt()) { startActivity(Intent(this@MacroOverlayService,MacroEditorActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); closeMenu() })
  }
  root=panel; try { wm.addView(panel,params()) } catch (_: Exception) {}; open=true
 }
 private fun startPlaceholder() { if (!held.compareAndSet(false,true)) return; repeatThread=Thread { while(held.get() && !Thread.currentThread().isInterrupted) try { Thread.sleep(2L) } catch (_:InterruptedException) { break } }.apply { isDaemon=true; start() } }
 private fun stopPlaceholder() { held.set(false); repeatThread?.interrupt(); repeatThread=null }
 private fun closeMenu() { root?.let { try { wm.removeView(it) } catch (_:Exception) {} }; root=null; open=false; showHandle() }
 override fun onDestroy() { stopPlaceholder(); root?.let { try { wm.removeView(it) } catch (_:Exception) {} }; super.onDestroy() }
}
