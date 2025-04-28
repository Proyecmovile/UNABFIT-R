package com.yorguisanchez.unabfit_r

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            Surface(
                color = Color(0xFFBDBDBD),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "UNABFIT-R",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF424242),
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
                    IconButton(
                        onClick = { },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú"
                        )
                    }
                    IconButton(
                        onClick = {  },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFFBDBDBD),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Inicio"
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate("Reservation")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Calendario"
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favoritos"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ViewPage()
        }
    }
}

@Composable
fun ViewPage() {
    val url = "https://unabradio.com/"
    AndroidView(
        factory = {
            WebView(it).apply {
                webViewClient = WebViewClient()
                loadUrl(url)
                settings.javaScriptEnabled = true
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}

