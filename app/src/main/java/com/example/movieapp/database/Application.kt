// MyApplication.kt
package com.example.movieapp.database

import android.app.Application
import androidx.room.Room

class MyApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "my-database")
            .build()
    }
}
