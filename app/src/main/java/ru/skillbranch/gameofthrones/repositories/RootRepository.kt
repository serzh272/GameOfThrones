package ru.skillbranch.gameofthrones.repositories

import android.util.Log
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.*
import okhttp3.internal.wait
import org.xml.sax.ErrorHandler
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.DbManager
import ru.skillbranch.gameofthrones.data.local.dao.CharactersDao
import ru.skillbranch.gameofthrones.data.local.dao.HouseDao
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.NetworkService
import ru.skillbranch.gameofthrones.data.remote.RestService
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.repositories.RootRepository.getNeedHouses

object RootRepository {
    private val api: RestService = NetworkService.api
    private val houseDao: HouseDao = DbManager.db.houseDao()
    private val characterDao: CharactersDao = DbManager.db.charactersDao()

    private val errHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
        exception.printStackTrace()
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + errHandler)

    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result: (houses: List<HouseRes>) -> Unit) {
        scope.launch {
            var i = 1
            val rez = mutableListOf<HouseRes>()
            while (api.houses(i).size != 0){
                rez.addAll(api.houses(i))
                i++
            }
            result(rez)
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result: (houses: List<HouseRes>) -> Unit) {
        scope.launch {
            result(getNeedHouses(*houseNames))
        }
    }


    suspend fun getNeedHouses(vararg houseNames: String): List<HouseRes> {
        return houseNames.fold(mutableListOf()) { acc, title ->
            acc.also { it.add(api.houseByName(title).first()) }
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(
        vararg houseNames: String,
        result: (houses: List<Pair<HouseRes, List<CharacterRes>>>) -> Unit
    ) {
        scope.launch {
            result(needHouseWithCharacters(*houseNames))
        }
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses: List<HouseRes>, complete: () -> Unit) {
        val list: List<House> = houses.map { it.toHouse() }
        scope.launch {
            houseDao.insert(list)
            complete()
        }
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(Characters: List<CharacterRes>, complete: () -> Unit) {
        val list: List<Character> = Characters.map { it.toCharacter() }
        scope.launch {
            characterDao.insert(list)
            complete()
        }
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        scope.launch {
            characterDao.deleteAll()
            houseDao.deleteAll()
            complete()
        }
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name: String, result: (characters: List<CharacterItem>) -> Unit) {
        scope.launch {
            findCharacters(name).value?.let { result(it) }
        }
    }

    fun findCharacters(houseName: String) = characterDao.findCharacters(houseName)

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id: String, result: (character: CharacterFull) -> Unit) {
        scope.launch { findCharacterFullById(id).value?.let { result(it) } }
    }

    fun findCharacterFullById(id: String) = characterDao.findCharacter(id)

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(result: (isNeed: Boolean) -> Unit) {
        scope.launch { result(isNeedUpdate()) }
    }

    suspend fun isNeedUpdate():Boolean{
        return houseDao.recordsCount() == 0
    }

    suspend fun needHouseWithCharacters(vararg houseNames: String): List<Pair<HouseRes, List<CharacterRes>>> {
        val result = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
        var houses = getNeedHouses(*houseNames)
        //Log.d("M_RootRepository", "needHouseWithCharacters houses count = ${houses.size}")
        scope.launch {
            houses?.forEach { house ->
                var i = 0
                //println("house byName ${house.url} scope this ctx ${this.coroutineContext}")
                val characters = mutableListOf<CharacterRes>()
                result.add(house to characters)
                house.members.forEach { character ->
                    launch(CoroutineName("character $character")) {
                        api.charter(character)
                            .apply { houseId = house.shortName }
                            .also { characters.add(it) }
                        i++
                        //println("complete coroutine $i/${house.swornMembers.size} ${house.name} ${this.coroutineContext[CoroutineName]}")
                    }
                }
            }
        }.join()

        return result
    }

    suspend fun sync() {
        val pairs: List<Pair<HouseRes, List<CharacterRes>>> =
            needHouseWithCharacters(*AppConfig.NEED_HOUSES)
        val initial = mutableListOf<House>() to mutableListOf<Character>()
        val lists = pairs.fold(initial) { acc, (houseRes, charactersList) ->
            //Log.d("M_RootRepository", "houses count = ${houseRes.name}")
            val house = houseRes.toHouse()
            val characters = charactersList.map { it.toCharacter() }
            acc.also { (hs, ch) ->
                hs.add(house)
                ch.addAll(characters)
            }
        }
        houseDao.upsert(lists.first)
        characterDao.upsert(lists.second)
    }
}