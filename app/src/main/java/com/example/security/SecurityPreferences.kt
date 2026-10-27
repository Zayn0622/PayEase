package com.example.security

import android.content.Context
import android.content.SharedPreferences

class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("payease_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DAILY_LIMIT_ENABLED = "daily_limit_enabled"
        private const val KEY_DAILY_TRANSACTION_LIMIT = "daily_transaction_limit"
        private const val KEY_BIOMETRIC_APP_LOCK = "biometric_app_lock"
        private const val KEY_BIOMETRIC_PAYMENT_AUTH = "biometric_payment_auth"
        private const val KEY_BIOMETRIC_BALANCE_AUTH = "biometric_balance_auth"

        const val DEFAULT_DAILY_LIMIT = 25000.0
        const val MIN_DAILY_LIMIT = 500.0
        const val MAX_DAILY_LIMIT = 100000.0
    }

    var isDailyLimitEnabled: Boolean
        get() = prefs.getBoolean(KEY_DAILY_LIMIT_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_DAILY_LIMIT_ENABLED, value).apply()

    var dailyTransactionLimit: Double
        get() = prefs.getFloat(KEY_DAILY_TRANSACTION_LIMIT, DEFAULT_DAILY_LIMIT.toFloat()).toDouble()
        set(value) = prefs.edit().putFloat(KEY_DAILY_TRANSACTION_LIMIT, value.toFloat()).apply()

    var isBiometricAppLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_APP_LOCK, true)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_APP_LOCK, value).apply()

    var isBiometricPaymentAuthEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_PAYMENT_AUTH, true)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_PAYMENT_AUTH, value).apply()

    var isBiometricBalanceAuthEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_BALANCE_AUTH, true)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_BALANCE_AUTH, value).apply()
}
