package com.sarftec.lifequotesandstatus.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sarftec.lifequotesandstatus.model.Quote

abstract class BaseListViewModel : ViewModel() {

    protected val _quotes = MutableLiveData<MutableList<Quote>>()
    val quote: LiveData<MutableList<Quote>>
        get() = _quotes

    abstract fun fetch()

    abstract fun getToolbarTitle() : String?

    abstract fun saveQuote(quote: Quote)

    open fun saveAndRemove(quote: Quote) {
        //Meant to be overridden by favorite viewModel
    }
}