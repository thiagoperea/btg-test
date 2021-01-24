package com.thiagoperea.btgtest.presentation.conversion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thiagoperea.btgtest.R
import com.thiagoperea.btgtest.databinding.ActivityConversionBinding
import com.thiagoperea.btgtest.internal.formatDecimal
import com.thiagoperea.btgtest.internal.hide
import com.thiagoperea.btgtest.internal.show
import com.thiagoperea.btgtest.presentation.MonetaryMask
import com.thiagoperea.btgtest.presentation.currencylist.CurrencyListActivity
import org.koin.android.viewmodel.ext.android.viewModel

class ConversionActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_CURRENCY = 1

        const val RESULT_OK_ORIGIN = 2
        const val RESULT_OK_DESTINY = 3
    }

    private lateinit var binding: ActivityConversionBinding
    private val viewModel by viewModel<ConversionViewModel>()

    private var origin: String? = null
    private var destiny: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        setupListeners()
        setupValueField()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CURRENCY) {
            val result = data?.getStringExtra(CurrencyListActivity.RESULT_DATA)

            when (resultCode) {
                RESULT_OK_ORIGIN -> {
                    origin = result
                    setupOriginData(result)
                }
                RESULT_OK_DESTINY -> {
                    destiny = result
                    setupDestinyData(result)
                }
                else -> return
            }

            if (origin != null && destiny != null) {
                viewModel.searchRate(
                    origin,
                    destiny,
                    binding.conversionValueField.text.toString()
                )
            }
        }
    }

    private fun setupDestinyData(result: String?) {
        binding.conversionDestinyButton.text = result
        binding.conversionResultSymbol.text = result
    }

    private fun setupOriginData(result: String?) {
        binding.conversionValueLayout.hint = getString(R.string.value_to_convert, result)
        binding.conversionOriginButton.text = result
    }

    private fun setupListeners() {
        binding.conversionOriginButton.setOnClickListener {
            startCurrencyScreen(true)
        }

        binding.conversionDestinyButton.setOnClickListener {
            startCurrencyScreen(false)
        }

        binding.conversionErrorButton.setOnClickListener {
            viewModel.searchRate(origin, destiny)
        }
    }

    private fun setupValueField() {
        binding.conversionValueField.addTextChangedListener(
            MonetaryMask(binding.conversionValueField) { value ->
                viewModel.convertValue(value)
            }
        )
    }

    private fun startCurrencyScreen(isOrigin: Boolean) {
        val intent = Intent(this, CurrencyListActivity::class.java)
        intent.putExtra(CurrencyListActivity.EXTRA_IS_ORIGIN, isOrigin)
        startActivityForResult(intent, REQUEST_CODE_CURRENCY)
    }

    private fun setupObservers() {
        viewModel.searchRateState.observe(this) {
            when (it) {
                is ConversionState.Loading -> showLoading()
                is ConversionState.Success -> {
                    hideLoading()
                    binding.conversionValueResultGroup.show()
                }
                is ConversionState.Error -> {
                    hideLoading()
                    binding.conversionErrorGroup.show()
                }
            }
        }

        viewModel.conversionValue.observe(this) {
            binding.conversionResultValue.text = it.formatDecimal()
        }
    }

    private fun showLoading() {
        binding.conversionErrorGroup.hide()
        binding.conversionValueResultGroup.hide()
        binding.conversionLoading.show()
    }

    private fun hideLoading() {
        binding.conversionLoading.hide()
    }
}
