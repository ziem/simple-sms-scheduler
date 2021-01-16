package com.github.ziem.simplesmsscheduler.contact

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.github.ziem.simplesmsscheduler.model.Contact
import io.reactivex.Single
import javax.inject.Inject

class ContactsDao @Inject constructor() {
    fun getContact(context: Context, uri: Uri): Single<Contact> {
        return Single.fromCallable { extractContact(context, uri) }
    }

    private fun extractContact(context: Context, uri: Uri): Contact? {
        var contact: Contact? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()

            val phoneNumberIndex: Int =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameIndex: Int =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val thumbnailIndex: Int =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
            val phoneNumber = cursor.getString(phoneNumberIndex)
            val name = cursor.getString(nameIndex)
            val thumbnail = cursor.getString(thumbnailIndex)

            contact = Contact(phoneNumber, name, thumbnail)
        }

        cursor?.close()
        return contact
    }
}