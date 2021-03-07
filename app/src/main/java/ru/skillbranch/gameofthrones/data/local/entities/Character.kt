package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*

@Entity(tableName = "characters",
    foreignKeys = [ForeignKey(
        entity = House::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("house_id"),
        onDelete = ForeignKey.CASCADE
    )])
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

@DatabaseView("""
    SELECT id, house_id AS house,  name, titles, aliases
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
    SELECT characters.id, characters.name,characters.born,characters.died,characters.titles,characters.aliases,characters.house_id,characters.mother,characters.father,houses.words,mother.id as m_id,  mother.name as m_name, mother.house_id as m_house, father.id as f_id, father.name as f_name, father.house_id as f_house FROM characters
    LEFT JOIN characters AS mother ON characters.mother=mother.id
    LEFT JOIN characters AS father ON characters.father=father.id
    INNER JOIN houses ON characters.house_id=houses.id
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
data class RelativeCharacter(
    val id: String,
    val name: String,
    val house:String //rel
)
