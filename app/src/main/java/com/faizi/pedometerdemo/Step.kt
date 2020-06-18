package com.faizi.pedometerdemo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedo_table")
data class Step(@PrimaryKey(autoGenerate = true) val id: Int?,
                @ColumnInfo(name = "step") val step: Int)