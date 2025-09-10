package com.example.qrcodeapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qrcodeapp.ui.theme.QRCodeAppTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRCodeAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QRCodeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun QRCodeScreen(modifier: Modifier = Modifier) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 启动协程请求后端接口
    LaunchedEffect(Unit) {
        val friendId = fetchFriendId()
        qrBitmap = generateQRCode(friendId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        qrBitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code")
        }
        if (qrBitmap == null) {
            Text("Loading QR Code...")
        }
    }
}

// 网络请求函数
suspend fun fetchFriendId(): String {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.0.2.2:8080/api/qr/friend") // 模拟器访问本机 Spring Boot
        .build()

    return withContext(Dispatchers.IO) {
        client.newCall(request).execute().use { response ->
            val json = JSONObject(response.body!!.string())
            json.getString("friendId")
        }
    }
}

// 生成二维码函数
fun generateQRCode(text: String): Bitmap {
    val size = 512
    val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bmp
}

@Preview(showBackground = true)
@Composable
fun QRCodePreview() {
    QRCodeAppTheme {
        QRCodeScreen()
    }
}
