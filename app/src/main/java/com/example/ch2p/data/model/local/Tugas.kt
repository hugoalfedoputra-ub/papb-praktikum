package com.example.ch2p.data.model.local;

import android.net.Uri
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import kotlinx.parcelize.Parcelize;
import androidx.room.TypeConverter

class UriConverter {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}

@Entity
@Parcelize
class Tugas(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "matkul") var matkul: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "is_done") var isDone: Boolean,
    @ColumnInfo(name = "date_added") var dateAdded: Long? = 0L,
    @ColumnInfo(name = "date_due") var dateDue: Long? = 0L,
    @ColumnInfo(name = "image_uri") var imageUri: Uri? = null
) : Parcelable
