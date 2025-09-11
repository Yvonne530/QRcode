package com.example.qrcodeapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

    companion object {
        private const val TAG = "QRCodeApp"
    }

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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 用户ID，可以从登录或本地存储获取
    val userId = "user123" 

    LaunchedEffect(userId) {
        try {
            Log.d("QRCodeApp", "Start fetching friendId for userId=$userId")
            val friendId = fetchFriendId(userId)
            Log.d("QRCodeApp", "Received friendId: $friendId")
            qrBitmap = generateQRCode(friendId)
        } catch (e: Exception) {
            Log.e("QRCodeApp", "Failed to fetch friendId", e)
            errorMessage = e.message
        }
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
            if (errorMessage != null) {
                Text("Error: $errorMessage")
            } else {
                Text("Loading QR Code...")
            }
        }
    }
}

// 网络请求函数，传入用户ID
suspend fun fetchFriendId(userId: String): String {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("http://10.0.2.2:8080/api/friend/getLink?userId=$userId")
        .build()

    return withContext(Dispatchers.IO) {
        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: throw Exception("Empty response body")
            Log.d("QRCodeApp", "Raw response: $body")
            val json = JSONObject(body)
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
