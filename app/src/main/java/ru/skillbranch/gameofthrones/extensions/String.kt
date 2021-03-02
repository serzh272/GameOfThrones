package ru.skillbranch.gameofthrones.extensions

import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.HouseType


val String.icon: Int
    get() {
        return HouseType.fromString(this).icon
    }

val String.title: String
    get() {
        return HouseType.fromString(this).title
    }
