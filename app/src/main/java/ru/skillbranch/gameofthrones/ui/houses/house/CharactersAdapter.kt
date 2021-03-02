package ru.skillbranch.gameofthrones.ui.houses.house


import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.databinding.ItemCharacterBinding
import ru.skillbranch.gameofthrones.extensions.icon

class CharactersAdapter(private val listener: (CharacterItem) -> Unit):RecyclerView.Adapter<CharactersAdapter.CharacterViewHolder>() {
    var items: List<CharacterItem> = listOf()
    lateinit var binding:ItemCharacterBinding
    inner class CharacterViewHolder(override val containerView:View):RecyclerView.ViewHolder(containerView),
        LayoutContainer {
            fun bind(
                item: CharacterItem,
                listener: (CharacterItem) -> Unit
            ){
                item.name.also {
                    binding.tvName.text = if (it.isBlank()) "Information is unknown" else it
                }
                item.titles
                    .plus(item.aliases)
                    .filter { it.isNotBlank() }
                    .also {
                        binding.tvAliases.text = if (it.isEmpty()) "Information is unknown"
                        else it.joinToString { " • " }
                    }
                binding.ivAvatar.setImageResource(item.house.icon)
                itemView.setOnClickListener{listener(item)}
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        binding = ItemCharacterBinding.inflate(from(parent.context))
        return CharacterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int =items.size

    fun updateItems(data:List<CharacterItem>){
        val diffCallback = object :DiffUtil.Callback(){
            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int =data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = items[oldItemPosition].id == items[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = items[oldItemPosition] ==data[newItemPosition]

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }
}
