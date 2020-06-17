package com.faizi.pedometerdemo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_table")
data class Step(@PrimaryKey @ColumnInfo(name = "step") val step: String)