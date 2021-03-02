package ru.skillbranch.gameofthrones.ui.character

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.databinding.FragmentCharacterBinding
import ru.skillbranch.gameofthrones.databinding.FragmentHouseBinding
import ru.skillbranch.gameofthrones.databinding.FragmentHousesBinding
import ru.skillbranch.gameofthrones.databinding.ItemCharacterBinding
import ru.skillbranch.gameofthrones.ui.RootActivity

class CharacterFragment : Fragment() {
    private val args:CharacterFragmentArgs by navArgs()
    private lateinit var mViewModel:CharacterViewModel
    lateinit var binding:FragmentCharacterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this,CharacterViewModelFactory(args.characterId)).get(CharacterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val houseType = HouseType.fromString(args.house)
        val arms = houseType.coastOfArms
        val scrim = houseType.primaryColor
        val scrimDark = houseType.darkColor
        val rootActivity = requireActivity() as RootActivity
        rootActivity.setSupportActionBar(binding.toolbar)
        rootActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = args.title
        }
        binding.ivArms.setImageResource(arms)
        with(binding.collapsingLayout){
            setBackgroundResource(scrim)
            setContentScrimResource(scrim)
            setStatusBarScrimResource(scrimDark)
        }
        binding.collapsingLayout.post{binding.collapsingLayout.requestLayout()}
        mViewModel.getCharacter().observe(this, Observer<CharacterFull>{character ->
            if (character == null) return@Observer
            val iconColor = requireContext().getColor(houseType.accentColor)
            listOf(binding.tvWordsLabel, binding.tvBornLabel, binding.tvTitlesLabel, binding.tvAliasesLabel).forEach{
                it.compoundDrawables.first().setTint(iconColor)
            }
            binding.tvWords.text = character.words
            binding.tvBorn.text = character.born
            binding.tvTitles.text = character.titles
                .filter { it.isNotEmpty() }
                .joinToString ("\n")
            binding.tvAliases.text = character.aliases
                .filter { it.isNotEmpty() }
                .joinToString ("\n")
            character.father?.let {
                binding.groupFather.visibility = View.VISIBLE
                binding.btnFather.text = it.name
                val action = CharacterFragmentDirections.actionNavCharacterSelf(it.id, it.house, it.name)
                binding.btnFather.setOnClickListener{
                    findNavController().navigate(action)
                }
            }
            character.mother?.let {
                binding.groupMother.visibility = View.VISIBLE
                binding.btnMother.text = it.name
                val action = CharacterFragmentDirections.actionNavCharacterSelf(it.id, it.house, it.name)
                binding.btnMother.setOnClickListener{
                    findNavController().navigate(action)
                }
            }
            if (character.died.isNotBlank()){
                Snackbar.make(binding.coordinator, "Died in : ${character.died}", Snackbar.LENGTH_INDEFINITE).show()
            }
        })
    }

}