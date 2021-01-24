package com.thiagoperea.btgtest.internal

import android.view.View
import java.text.DecimalFormat

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Number.formatDecimal(): String = DecimalFormat("#,##0.00").format(this)

fun CharSequence?.convertToDouble(): Double {
    return this?.replace(Regex("[^0-9]"), "")?.toDoubleOrNull() ?: 0.0
}