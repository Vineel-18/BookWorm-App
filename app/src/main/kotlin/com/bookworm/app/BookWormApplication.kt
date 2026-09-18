package com.bookworm.app

import android.app.Application

/**
 * BookWormApplication.kt
 *
 * Application class — entry point before any Activity starts.
 * Register global state, crash reporters, etc. here.
 */
class BookWormApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Global app init goes here
        // e.g. Timber.plant(Timber.DebugTree()) for logging
    }
}
