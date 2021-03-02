package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String, //rel
    val mother: String, //rel
    val spouse: String,
    @ColumnInfo(name = "house_id")
    val houseId: HouseType//rel
)

data class RelativeCharacter(
    val id: String,
    val name: String,
    val house:String //rel
)
@DatabaseView("""
    SELECT house_id AS house, id, name, aliases, titles
    FROM characters
    ORDER BY name ASC
""")
data class CharacterItem(
    val id: String,
    val house: String, //rel
    val name: String,
    val titles: List<String>,
    val aliases: List<String>
)
@DatabaseView("""
    SELECT character.id, character.name,character.born,character.died,character.titles,character.aliases,character.house_id,character.mother,character.father,houses.words, mother.name as m_name, mother.id as m_id, mother.house_id as m_house, father.name as f_name, father.id as f_id, father.house_id as f_house FROM characters as character
    LEFT JOIN characters AS mother ON character.mother=mother.id
    LEFT JOIN characters AS father ON character.father=father.id
    INNER JOIN houses ON character.house_id=houses.id
""")
data class CharacterFull(
    val id: String,
    val name: String,
    val words: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    @ColumnInfo(name = "house_id")
    val house:String, //rel
    @Embedded(prefix = "f_")
    val father: RelativeCharacter?,
    @Embedded(prefix = "m_")
    val mother: RelativeCharacter?
)
