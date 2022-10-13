package crash.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mredrock.lib.crash.R
import com.mredrock.lib.crash.core.CyxbsCrashMonitor

class DebugFirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 因为 debug 单模块调试不会主动启动，所以这里单独手动开启
        CyxbsCrashMonitor.install(application)
        setContentView(R.layout.crash_activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            1/0
        }
        findViewById<Button>(R.id.button2).setOnClickListener {
            Thread{
                throw RuntimeException("其他线程Crash")
            }.start()
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            startActivity(Intent(this, DebugSecondActivity::class.java))
        }
        
    }
}