
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarftec.lifequotesandstatus.R
import com.sarftec.lifequotesandstatus.databinding.LayoutFontCardBinding
import com.sarftec.lifequotesandstatus.databinding.LayoutTextPanelBinding
import com.sarftec.lifequotesandstatus.presentation.panel.ItemAdapter
import com.sarftec.lifequotesandstatus.presentation.panel.ItemBox
import com.sarftec.lifequotesandstatus.presentation.panel.ItemViewHolder
import com.sarftec.lifequotesandstatus.presentation.panel.PanelListener
import kotlinx.coroutines.CoroutineScope

class FontManager(
    private val coroutineScope: CoroutineScope,
    private val panelListener: PanelListener,
    layout: LayoutTextPanelBinding
) {

    init {
        layout.readersettingsFontRvId.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            context.assets.list("fonts")?.toList()?.let { fonts ->
                adapter = FontItemAdapter(coroutineScope, fonts) { fontLocation ->
                    fontLocation?.let {
                        panelListener.setFont(it)
                    }
                }
            }
        }
    }


    class FontItemAdapter(
        coroutineScope: CoroutineScope,
        fonts: List<String>,
        configure: (String?) -> Unit
    ) : ItemAdapter<String, String?>(coroutineScope, fonts, configure) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ItemViewHolder<String, String?> {
            val binding = LayoutFontCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return FontViewHolder(binding, itemBox).also { it.init() }
        }

        inner class FontViewHolder(
            private val binding: LayoutFontCardBinding,
            fontBox: ItemBox<String?>
        ) : ItemViewHolder<String, String?>(fontBox, binding.root) {

            private var fontLocation: String? = null
            override fun init() {
                binding.fontLayout.setOnClickListener {
                    itemBox.listener(fontLocation)
                    clicked(adapterPosition) //Renamed from bindingAdapterPosition
                }
                super.init()
            }

            override fun bind(font: String) {
                fontLocation = "fonts/$font"
                val typeface = Typeface.createFromAsset(
                    binding.root.context.assets,
                    fontLocation!!
                )
                binding.fontText.typeface = typeface
            }

            override fun switch(isOn: Boolean) {
                if (isOn) {
                    binding.fontText.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.white)
                    )
                    binding.fontLayout.background = ContextCompat.getDrawable(
                        binding.root.context, R.drawable.font_card_shape
                    )
                } else {
                    binding.fontText.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.reader_text_color)
                    )
                    binding.fontLayout.background = null
                }
            }
        }
    }
}