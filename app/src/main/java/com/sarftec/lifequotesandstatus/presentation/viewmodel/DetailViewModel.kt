package com.sarftec.lifequotesandstatus.presentation.viewmodel

import android.graphics.Color
import android.os.Parcelable
import androidx.lifecycle.*
import com.sarftec.lifequotesandstatus.data.repository.Repository
import com.sarftec.lifequotesandstatus.model.Quote
import com.sarftec.lifequotesandstatus.presentation.activity.BaseActivity
import com.sarftec.lifequotesandstatus.presentation.image.ImageHolder
import com.sarftec.lifequotesandstatus.presentation.image.ImageStore
import com.sarftec.lifequotesandstatus.presentation.panel.AlignmentManager
import com.sarftec.lifequotesandstatus.presentation.panel.BackgroundManager
import com.sarftec.lifequotesandstatus.presentation.panel.PanelListener
import com.sarftec.lifequotesandstatus.presentation.parcel.QuoteToDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import kotlin.random.Random

enum class QuoteAlignment { START, CENTER, END }

enum class BackgroundOption { IMAGES, GALLERY, COLOR, NEUTRAL }

class PanelState(
    var color: Int = Color.WHITE,
    var opacity: Int = -1,
    var size: Float = -1f,
    var fontLocation: String = "",
    var isAllCaps: Boolean = false,
    var isUnderlined: Boolean = false,
    var alignment: QuoteAlignment = QuoteAlignment.CENTER,
    var backgroundOption: BackgroundOption = BackgroundOption.NEUTRAL
) {
    private var changeSet: Boolean = true

    override fun equals(other: Any?): Boolean {
        if (other !is PanelState) return false
        return other.changeSet == this.changeSet
    }

    override fun hashCode(): Int {
        return if (changeSet) 1 else 0
    }

    fun switch(): PanelState {
        changeSet = !changeSet
        return this
    }
}

class IndexedQuotes(
    var index: Int,
    val quotes: List<Quote>
) {
    override fun equals(other: Any?): Boolean {
        if (other !is IndexedQuotes) return false
        return other.index == index
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + quotes.hashCode()
        return result
    }
}

enum class ToolbarState {
    EDIT, NORMAL
}

@Parcelize
class BackgroundState(
    var color: Int? = null,
    var image: String? = null,
    var isImageAsset: Boolean = true
) : Parcelable

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
    private val imageStore: ImageStore,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), PanelListener {

    private val _indexedQuotes = MutableLiveData<IndexedQuotes>()
    val indexedQuotes: LiveData<IndexedQuotes>
        get() = _indexedQuotes

    private val _currentQuote = MutableLiveData<Quote>()
    val currentQuote: LiveData<Quote>
        get() = _currentQuote

    private val _panelState = MutableLiveData(PanelState())
    val panelState: LiveData<PanelState>
        get() = _panelState

    private val _background = MutableLiveData<BackgroundState>()
    val background: LiveData<BackgroundState>
        get() = _background

    private val _toolbarState = MutableLiveData(ToolbarState.NORMAL)
    val toolbarState: LiveData<ToolbarState>
        get() = _toolbarState

    fun fetch() {
        _background.value = savedStateHandle.get<BackgroundState>(SAVED_IMAGE) ?: kotlin.run {
            BackgroundState()
        }

        if (_indexedQuotes.value != null) return
        val parcel = savedStateHandle.get<Parcelable>(PARCEL) as QuoteToDetail? ?: return
        viewModelScope.launch {
            val quoteList = when (parcel.origin) {
                QuoteToDetail.ORIGIN_QUOTE -> {
                    repository.getCategoryQuotes(parcel.categoryId).shuffled(Random(parcel.seed))
                }
                QuoteToDetail.ORIGIN_MAIN -> {
                    listOf(repository.getTodayQuote())
                }
                else -> repository.getFavoriteQuotes()
            }

            var index = savedStateHandle.get<Int>(CURRENT_INDEX) ?: -1
            if (index == -1) {
                index = if(parcel.origin == QuoteToDetail.ORIGIN_MAIN) 0
                else quoteList.indexOfFirst { quote ->
                    quote.id == parcel.quoteId
                }
            }
            _indexedQuotes.value = IndexedQuotes(index, quoteList)
            if(parcel.origin == QuoteToDetail.ORIGIN_MAIN) randomBackground()
            else setBackgroundImage(imageStore.getQuoteImage(parcel.quoteImageName))
            setCurrentQuoteIndex(index)
        }
    }

    fun toolbarIconClicked() {
        _toolbarState.value = if(toolbarState.value == ToolbarState.NORMAL) ToolbarState.EDIT
        else ToolbarState.NORMAL
    }

    fun neutralizeBackgroundOption() {
        _panelState.value?.let {
            it.backgroundOption = BackgroundOption.NEUTRAL
        }
    }

    fun randomBackground() {
        setBackgroundImage(imageStore.getRandomQuoteImage())
    }

    fun setBackgroundImage(imageHolder: ImageHolder) {
        val changes = _background.value?.also {
            it.image = imageHolder.uri.toString()
            it.color = null
            it.isImageAsset = imageHolder is ImageHolder.AssetImage
        }
        savedStateHandle.set<BackgroundState>(SAVED_IMAGE, changes)
        _background.value = changes
    }

    fun setBackgroundColor(color: Int) {
        val changes = _background.value?.also {
            it.image = null
            it.color = color
        }
        savedStateHandle.set<BackgroundState>(SAVED_IMAGE, changes)
        _background.value = changes
    }

    fun getToolbarTitle(): String? {
        return savedStateHandle.get<QuoteToDetail>(PARCEL)?.categoryTitle
    }

    fun getCurrentQuote(): Quote? {
        val index = savedStateHandle.get<Int>(CURRENT_INDEX) ?: -1
        if (index == -1) return null
        return _indexedQuotes.value?.quotes?.get(index)
    }

    fun setCurrentQuoteIndex(index: Int) {
        savedStateHandle.set(CURRENT_INDEX, index)
        _indexedQuotes.value?.let {
            _currentQuote.value = it.quotes[index]
        }
    }

    fun changeCurrentQuoteFavorite(): Quote? {
        val current = _currentQuote.value ?: return null
        current.isFavorite = !current.isFavorite
        savedStateHandle.get<Int>(CURRENT_INDEX)?.let { index ->
            BaseActivity.modifiedQuoteList
                .entries
                .firstOrNull { it.key == index }
                ?.setValue(current.isFavorite) ?: kotlin.run {
                BaseActivity.modifiedQuoteList.put(index, current.isFavorite)
            }
        }
        viewModelScope.launch {
            repository.saveFavoriteQuote(current.id, current.isFavorite)
        }
        _currentQuote.value = current
        return current
    }

    fun setParcel(parcel: QuoteToDetail) {
        if (savedStateHandle.get<Parcelable>(QuoteViewModel.PARCEL) != null) return
        savedStateHandle.set(QuoteViewModel.PARCEL, parcel)
    }

    fun isPanelShown() : Boolean {
        return _toolbarState.value?.let {
            it == ToolbarState.EDIT
        } ?: false
    }

    private fun updatePanelState() {
        _panelState.value = _panelState.value?.switch()
    }

    /**
     ** This block of code implement panel listener
     **/
    override fun setFont(fontLocation: String) {
        _panelState.value?.fontLocation = fontLocation
        updatePanelState()
    }

    override fun setColor(color: Int) {
        _panelState.value?.color = color
        updatePanelState()
    }

    override fun setOpacity(color: Int) {
        _panelState.value?.opacity = color
        updatePanelState()
    }

    override fun setSize(size: Float) {
        _panelState.value?.size = size
        updatePanelState()
    }

    override fun setAlignment(position: AlignmentManager.Position) {
        _panelState.value?.alignment = when (position) {
            AlignmentManager.Position.CENTER -> QuoteAlignment.CENTER
            AlignmentManager.Position.LEFT -> QuoteAlignment.START
            AlignmentManager.Position.RIGHT -> QuoteAlignment.END
        }
        updatePanelState()
    }

    override fun setAllCaps(option: AlignmentManager.Option) {
        _panelState.value?.isAllCaps = option == AlignmentManager.Option.YES
        updatePanelState()
    }

    override fun setUnderlined(option: AlignmentManager.Option) {
        _panelState.value?.isUnderlined = option == AlignmentManager.Option.YES
        updatePanelState()
    }

    override fun chooseBackground(chooser: BackgroundManager.Chooser) {
        _panelState.value?.backgroundOption = when(chooser) {
            BackgroundManager.Chooser.IMAGE -> BackgroundOption.IMAGES
            BackgroundManager.Chooser.GALLERY -> BackgroundOption.GALLERY
            BackgroundManager.Chooser.COLOR -> BackgroundOption.COLOR
        }
        updatePanelState()
    }

    override fun getAlignment(): AlignmentManager.Position {
        if (_panelState.value == null) AlignmentManager.Position.CENTER
        return when (_panelState.value!!.alignment) {
            QuoteAlignment.CENTER -> AlignmentManager.Position.CENTER
            QuoteAlignment.START -> AlignmentManager.Position.LEFT
            QuoteAlignment.END -> AlignmentManager.Position.RIGHT
        }
    }

    override fun isAllCaps(): AlignmentManager.Option {
        return _panelState.value?.isAllCaps?.let {
            if (it) AlignmentManager.Option.YES else AlignmentManager.Option.NO
        } ?: AlignmentManager.Option.NO
    }

    override fun isUnderlined(): AlignmentManager.Option {
        return _panelState.value?.isUnderlined?.let {
            if (it) AlignmentManager.Option.YES else AlignmentManager.Option.NO
        } ?: AlignmentManager.Option.NO
    }

    companion object {
        const val PARCEL = "parcel"
        const val SAVED_IMAGE = "saved_image"
        const val CURRENT_INDEX = "detail_current_index"
    }
}