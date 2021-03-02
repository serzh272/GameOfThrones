package ru.skillbranch.gameofthrones.data.remote.res

import com.squareup.moshi.Json
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.HouseType

data class HouseRes(
    @Json(name = "")
    val url: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "region")
    val region: String,
    @Json(name = "coatOfArms")
    val coatOfArms: String,
    @Json(name = "words")
    val words: String,
    @Json(name = "titles")
    val titles: List<String> = listOf(),
    @Json(name = "seats")
    val seats: List<String> = listOf(),
    @Json(name = "currentLord")
    val currentLord: String,
    @Json(name = "heir")
    val heir: String,
    @Json(name = "overlord")
    val overlord: String,
    @Json(name = "founded")
    val founded: String,
    @Json(name = "founder")
    val founder: String,
    @Json(name = "diedOut")
    val diedOut: String,
    @Json(name = "ancestralWeapons")
    val ancestralWeapons: List<String> = listOf(),
    @Json(name = "cadetBranches")
    val cadetBranches: List<Any> = listOf(),
    @Json(name = "swornMembers")
    val swornMembers: List<String> = listOf()
) : IRes {
    fun toHouse():House {
        return House(
            HouseType.fromString(name),
            name,
            region,
            coatOfArms,
            words,
            titles,
            seats,
            currentLord,
            heir,
            overlord,
            founded,
            founder,
            diedOut,
            ancestralWeapons)
    }

    override val id: String
        get() = url.lastSegment()
    val shortName:String
        get() = name.split(" ")
            .dropLastUntil{it == "of"}
            .last()
    val motherId:List<String>
        get() = swornMembers.map { it.lastSegment() }
}

private fun <E> List<E>.dropLastUntil(predicate: (E) -> Boolean): List<E> {
    if (!isEmpty()) {
        val iterator = listIterator(size)
        while (iterator.hasPrevious()) {
            if (!predicate(iterator.previous())) {
                return take(iterator.nextIndex())
            }
        }
    }
    return emptyList()
}
