package com.thiagoperea.btgtest.presentation.currencylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thiagoperea.btgtest.R
import com.thiagoperea.btgtest.data.model.Currency
import com.thiagoperea.btgtest.databinding.ItemCurrencyListBinding

class CurrencyListAdapter(
    private val onCurrencyClick: (Currency) -> Unit,
    private val onFavoriteClick: (Currency) -> Unit
) : RecyclerView.Adapter<CurrencyListAdapter.ViewHolder>() {

    private var dataset = mutableListOf<Currency>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCurrencyListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding, onCurrencyClick, onFavoriteClick)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) = holder.bind(dataset[position])

    override fun getItemCount() = dataset.size

    fun setData(repositories: List<Currency>?) {
        dataset.clear()
        repositories?.let {
            dataset.addAll(it)
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        dataset.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemCurrencyListBinding,
        private val onCurrencyClick: (Currency) -> Unit,
        private val onFavoriteClick: (Currency) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: Currency) {
            binding.itemCurrencyFullName.text = currency.fullName
            binding.itemCurrencySymbol.text = currency.symbol
            val icon = if (currency.isFavorite) {
                R.drawable.ic_favorite_selected
            } else {
                R.drawable.ic_favorite_unselected
            }
            binding.itemCurrencyFavoriteButton.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, icon)
            )

            binding.root.setOnClickListener { onCurrencyClick(currency) }
            binding.itemCurrencyFavoriteButton.setOnClickListener { onFavoriteClick(currency) }
        }
    }
}

