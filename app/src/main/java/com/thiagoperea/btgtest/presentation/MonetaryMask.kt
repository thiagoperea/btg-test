package com.thiagoperea.btgtest.presentation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.thiagoperea.btgtest.internal.convertToDouble
import com.thiagoperea.btgtest.internal.formatDecimal
import java.math.BigDecimal
import java.math.RoundingMode

class MonetaryMask(
    private val editText: EditText,
    private val doAfterMask: (Double) -> Unit
) : TextWatcher {

    override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        // nothing to do
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        // nothing to do
    }

    override fun afterTextChanged(editable: Editable?) {
        editText.removeTextChangedListener(this)

        val onlyDigits = editable.convertToDouble()
        val asDecimal = BigDecimal(onlyDigits)
            .setScale(2, BigDecimal.ROUND_FLOOR)
            .divide(BigDecimal.valueOf(100), RoundingMode.FLOOR)

        editText.setText(asDecimal.formatDecimal())
        editText.setSelection(editText.length())
        doAfterMask(asDecimal.toDouble())

        editText.addTextChangedListener(this)
    }

}
