package com.khaiqueng_finalterm.busticketbooking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.khaiqueng_finalterm.busticketbooking.navigation.AppNavigation
import com.khaiqueng_finalterm.busticketbooking.ui.theme.BusTicketBookingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusTicketBookingTheme {
                AppNavigation()
            }
        }
    }
}