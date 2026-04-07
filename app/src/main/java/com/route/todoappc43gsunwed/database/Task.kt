package com.route.todoappc43gsunwed.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String? = null,
    val date: Date? = null,
    val isDone: Boolean? = false,
    val details: String? = null,
//    @ColumnInfo
) : Serializable

// DAO (Data Access Object )
