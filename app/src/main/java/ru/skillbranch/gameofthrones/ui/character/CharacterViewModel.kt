package ru.skillbranch.gameofthrones.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.ui.houses.house.HouseViewModel
import java.lang.IllegalArgumentException

class CharacterViewModel(private val characterId:String): ViewModel() {
    val repository = RootRepository

    fun getCharacter(): LiveData<CharacterFull> {
        return MutableLiveData(repository.findCharacterFullById(characterId))
    }

    //CharacterViewModelFactory
}

class CharacterViewModelFactory(private val characterId:String): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseViewModel::class.java)){
            return CharacterViewModel(characterId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

};