package com.thiagoperea.btgtest.presentation.currencylist

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thiagoperea.btgtest.data.model.Currency
import com.thiagoperea.btgtest.databinding.ActivityCurrencyListBinding
import com.thiagoperea.btgtest.databinding.DialogSelectFilterBinding
import com.thiagoperea.btgtest.internal.hide
import com.thiagoperea.btgtest.internal.show
import com.thiagoperea.btgtest.presentation.conversion.ConversionActivity
import org.koin.android.ext.android.inject

class CurrencyListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCurrencyListBinding
    private val viewModel: CurrencyListViewModel by inject()

    private var adapter: CurrencyListAdapter? = null

    private val resultCode: Int by lazy {
        if (intent.getBooleanExtra(EXTRA_IS_ORIGIN, false)) {
            ConversionActivity.RESULT_OK_ORIGIN
        } else {
            ConversionActivity.RESULT_OK_DESTINY
        }
    }

    companion object {
        const val EXTRA_IS_ORIGIN = "extra.is.origin"
        const val RESULT_DATA = "result.data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupList()
        setupObservers()
        setupListeners()
        viewModel.loadCurrencyList()
    }

    private fun setupList() {
        this.adapter = CurrencyListAdapter(::onCurrencyClick, ::onFavoriteClick)

        binding.currencyListRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@CurrencyListActivity)
            adapter = this@CurrencyListActivity.adapter
            addItemDecoration(DividerItemDecoration(this@CurrencyListActivity, VERTICAL))
        }

        binding.currencyListRecyclerLayout.setOnRefreshListener {
            viewModel.loadCurrencyList()
        }
    }

    private fun onCurrencyClick(currency: Currency) {
        val intent = Intent()
        intent.putExtra(RESULT_DATA, currency.symbol)
        setResult(resultCode, intent)
        finish()
    }

    private fun onFavoriteClick(currency: Currency) {
        viewModel.switchFavorite(currency)
    }

    private fun setupObservers() {
        viewModel.listState.observe(this) { state ->
            when (state) {
                is CurrencyListState.Loading -> showLoading()
                is CurrencyListState.Success -> doOnSuccess(state.currencyList)
                is CurrencyListState.SearchCompleted -> doOnSearchCompleted(state.filteredList)
                is CurrencyListState.Error -> doOnError()
            }
        }
    }


    private fun showLoading() {
        binding.currencyListErrorGroup.hide()
        adapter?.clearData()
        binding.currencyListRecyclerLayout.isRefreshing = true
    }

    private fun hideLoading() {
        binding.currencyListRecyclerLayout.isRefreshing = false
    }

    private fun doOnError() {
        hideLoading()
        binding.currencyListErrorGroup.show()
    }

    private fun doOnSuccess(currencyList: List<Currency>?) {
        hideLoading()
        adapter?.setData(currencyList)
    }

    private fun doOnSearchCompleted(filteredList: List<Currency>?) {
        adapter?.setData(filteredList)
        binding.currencyListRecycler.smoothScrollToPosition(0)
    }

    private fun setupListeners() {
        binding.currencyListSearchLayout.apply {
            setEndIconOnClickListener {
                viewModel.filterList(this.editText?.text.toString())
            }

            editText?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.filterList(this.editText?.text.toString())
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        binding.currencyListFilterButton.setOnClickListener { showFilterDialog() }

        binding.currencyListErrorButton.setOnClickListener { viewModel.loadCurrencyList() }
    }

    private fun showFilterDialog() {
        val dialogBinding = DialogSelectFilterBinding.inflate(layoutInflater, null, false)

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .show()

        dialogBinding.dialogFilterSymbolButton.setOnClickListener {
            dialog.dismiss()
            viewModel.filterListBySymbol()
        }

        dialogBinding.dialogFilterNameButton.setOnClickListener {
            dialog.dismiss()
            viewModel.filterListByName()
        }
    }
}
