package ru.skillbranch.gameofthrones.ui.houses.house

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.databinding.FragmentHouseBinding
import ru.skillbranch.gameofthrones.extensions.title
import ru.skillbranch.gameofthrones.ui.custom.ItemDivider
import ru.skillbranch.gameofthrones.ui.houses.HousesFragmentDirections

class HouseFragment: Fragment() {
    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var viewModel: HouseViewModel
    private lateinit var binding:FragmentHouseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val houseName:String = arguments?.getString(HOUSE_NAME) ?: HouseType.STARK.title
        val vmFactory = HouseViewModelFactory(houseName)
        charactersAdapter = CharactersAdapter{
            val action = HousesFragmentDirections.actionNavHousesToNavCharacter(it.id, it.house.title, it.name)
            findNavController().navigate(action)
        }
        viewModel = ViewModelProviders.of(this, vmFactory).get(HouseViewModel::class.java)
        viewModel.getCharacters().observe(this, Observer<List<CharacterItem>> {
            charactersAdapter.updateItems(it)
        })
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        with(menu.findItem(R.id.action_search).actionView as SearchView){
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.handleSearchQuery(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.handleSearchQuery(newText)
                    return true
                }

            })
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHouseBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.rvCharacterList){
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(ItemDivider())
            adapter = charactersAdapter
        }
    }

    companion object{
        private const val HOUSE_NAME = "house_name"

        @JvmStatic
        fun newInstance(houseName: String): HouseFragment{
            return HouseFragment().apply {
                arguments = bundleOf(HOUSE_NAME to houseName)
            }
        }
    }
}