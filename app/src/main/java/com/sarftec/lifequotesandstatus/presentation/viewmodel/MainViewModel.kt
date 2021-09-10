package com.sarftec.lifequotesandstatus.presentation.viewmodel

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarftec.lifequotesandstatus.data.repository.Repository
import com.sarftec.lifequotesandstatus.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed class FloatingButtonVisibility {
    object Visible : FloatingButtonVisibility() {
        var isVisible = false
        var id: Int = -1

        fun switch() {
            id = if (id == -1)  0 else -1
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Visible) return false
            return other.id  == id
        }
    }

    object Neutral : FloatingButtonVisibility()
}

sealed class SearchCloseVisibility {
    object Visible : SearchCloseVisibility() {
        var isVisible = false
        var id = -1

        fun switch() {
            id = if (id == -1)  0 else -1
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Visible) return false
            return other.id  == Visible.id
        }
    }

    object Neutral : SearchCloseVisibility()
}

sealed class ClearSearchField {
    object Clear : ClearSearchField()
    object Neutral : ClearSearchField()
}

sealed class OnBackPressed {
    object Clear : OnBackPressed()
    object Proceed : OnBackPressed()
    object Neutral : OnBackPressed()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private var storedCategories = listOf<Category>()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>>
        get() = _categories

    private val _floatingVisibility = MutableLiveData<FloatingButtonVisibility>()
    val floatingVisibility
        get() = _floatingVisibility

    private val _searchCloseVisibility = MutableLiveData<SearchCloseVisibility>()
    val searchCloseVisibility
        get() = _searchCloseVisibility

    private val _clearSearchField = MutableLiveData<ClearSearchField>()
    val clearSearchField: LiveData<ClearSearchField>
        get() = _clearSearchField

    private val _onBackPressed = MutableLiveData<OnBackPressed>()
    val onBackPressed: LiveData<OnBackPressed>
        get() = _onBackPressed

    private var scrollPosition: Int = 0

    private var searchFieldHasFocus = false

    fun fetch() {
        if (storedCategories.isNotEmpty()) return
        viewModelScope.launch {
            storedCategories = repository.getCategories()
            _categories.value = storedCategories
        }
    }

    fun neutralizeFloatingButton() {
        _floatingVisibility.value = FloatingButtonVisibility.Neutral
    }

    fun neutralizeSearchClose() {
        _searchCloseVisibility.value = SearchCloseVisibility.Neutral
    }

    fun neutralizeSearchField() {
        _clearSearchField.value = ClearSearchField.Neutral
    }

    fun neutralizeBackPressed() {
        _onBackPressed.value = OnBackPressed.Neutral
    }

    fun onBackPressed() {
        _onBackPressed.value = if (searchFieldHasFocus) {
            _categories.value?.let {
                if (it.size == storedCategories.size)
                    OnBackPressed.Proceed else OnBackPressed.Clear
            } ?: OnBackPressed.Proceed
        } else OnBackPressed.Proceed
    }

    fun onSearchFieldHasFocus(hasFocus: Boolean) {
        searchFieldHasFocus = hasFocus
    }

    fun onScrollUp() {
        _floatingVisibility.value = FloatingButtonVisibility.Visible.also {
            it.isVisible = false
            it.switch()
        }
    }

    fun onScrollPositionChanged(dy: Int) {
        scrollPosition -= dy
        _floatingVisibility.value =
            if (scrollPosition > 1000) FloatingButtonVisibility.Visible.also {
                it.isVisible = true
                it.switch()
            } else FloatingButtonVisibility.Visible.also {
                it.isVisible = false
                it.switch()
            }
    }

    fun onCloseSearchQuery() {
        _categories.value = storedCategories
        _searchCloseVisibility.value = SearchCloseVisibility.Visible.also {
            it.isVisible = false
            it.switch()
        }
        _clearSearchField.value = ClearSearchField.Clear
    }

    fun onSearchQueryChanged(query: Editable) {
        if (query.isEmpty()) {
            _categories.value = storedCategories
            _searchCloseVisibility.value = SearchCloseVisibility.Visible.also {
                it.isVisible = false
                it.switch()
            }
        } else {
            _categories.value = storedCategories.filter {
                it.name.lowercase(Locale.ENGLISH)
                    .contains(query.toString().lowercase(Locale.ENGLISH))
            }
            _searchCloseVisibility.value = SearchCloseVisibility.Visible.also {
                it.isVisible = true
                it.switch()
            }
        }
    }
}