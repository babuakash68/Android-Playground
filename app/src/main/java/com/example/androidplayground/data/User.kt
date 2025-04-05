package com.example.androidplayground.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val name: String,
    val photoUrl: String? = null,
    var isLoggedIn: Boolean = true,
    val lastLoginTime: Long = System.currentTimeMillis()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        email = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        photoUrl = parcel.readString(),
        isLoggedIn = parcel.readByte() != 0.toByte(),
        lastLoginTime = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(photoUrl)
        parcel.writeByte(if (isLoggedIn) 1 else 0)
        parcel.writeLong(lastLoginTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
} 