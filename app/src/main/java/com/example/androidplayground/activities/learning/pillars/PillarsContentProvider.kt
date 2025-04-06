package com.example.androidplayground.activities.learning.pillars

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Build
import android.util.Log
import java.util.UUID

/**
 * A simple ContentProvider implementation for the Four Pillars demo.
 * This provider simulates a contacts database to demonstrate ContentProvider functionality.
 * 
 * Privacy Note: This is a demo implementation that stores data only in memory.
 * No data is persisted to storage or shared with other applications.
 * All data is cleared when the application is closed.
 */
class PillarsContentProvider : ContentProvider() {
    
    companion object {
        private const val AUTHORITY = "com.example.androidplayground.pillars.provider"
        private const val CONTACTS_PATH = "contacts"
        private const val CONTACTS = 1
        private const val CONTACT_ID = 2
        
        // Content URI for the provider
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$CONTACTS_PATH")
        
        // Column names
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_FAVORITE = "favorite"
    }
    
    // URI matcher to handle different types of requests
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, CONTACTS_PATH, CONTACTS)
        addURI(AUTHORITY, "$CONTACTS_PATH/#", CONTACT_ID)
    }
    
    // In-memory storage for contacts
    private data class Contact(
        val id: String = UUID.randomUUID().toString(),
        var name: String,
        var phone: String,
        var email: String,
        var address: String,
        var favorite: Boolean = false
    )
    
    private val contacts = mutableListOf<Contact>()
    
    // Add some sample contacts
    init {
        contacts.add(Contact(
            name = "John Smith",
            phone = "(555) 123-4567",
            email = "john.smith@example.com",
            address = "123 Main St, Anytown, USA"
        ))
        contacts.add(Contact(
            name = "Jane Doe",
            phone = "(555) 987-6543",
            email = "jane.doe@example.com",
            address = "456 Oak Ave, Somewhere, USA",
            favorite = true
        ))
        contacts.add(Contact(
            name = "Bob Johnson",
            phone = "(555) 555-5555",
            email = "bob.johnson@example.com",
            address = "789 Pine Rd, Nowhere, USA"
        ))
    }
    
    override fun onCreate(): Boolean {
        Log.d("PillarsContentProvider", "ContentProvider created with ${contacts.size} sample contacts")
        return true
    }
    
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        Log.d("PillarsContentProvider", "Query called with URI: $uri")
        
        // Create a cursor with the required columns
        val cursor = MatrixCursor(arrayOf(
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_PHONE,
            COLUMN_EMAIL,
            COLUMN_ADDRESS,
            COLUMN_FAVORITE
        ))
        
        when (uriMatcher.match(uri)) {
            CONTACTS -> {
                // Return all contacts
                contacts.forEach { contact ->
                    cursor.addRow(arrayOf(
                        contact.id,
                        contact.name,
                        contact.phone,
                        contact.email,
                        contact.address,
                        if (contact.favorite) 1 else 0
                    ))
                }
            }
            CONTACT_ID -> {
                // Return a specific contact
                val id = uri.lastPathSegment
                val contact = contacts.find { it.id == id }
                if (contact != null) {
                    cursor.addRow(arrayOf(
                        contact.id,
                        contact.name,
                        contact.phone,
                        contact.email,
                        contact.address,
                        if (contact.favorite) 1 else 0
                    ))
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        
        return cursor
    }
    
    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            CONTACTS -> "vnd.android.cursor.dir/vnd.com.example.androidplayground.pillars.contact"
            CONTACT_ID -> "vnd.android.cursor.item/vnd.com.example.androidplayground.pillars.contact"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
    
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d("PillarsContentProvider", "Insert called with URI: $uri")
        
        if (uriMatcher.match(uri) != CONTACTS) {
            throw IllegalArgumentException("Invalid URI for insert: $uri")
        }
        
        val name = values?.getAsString(COLUMN_NAME) ?: "New Contact"
        val phone = values?.getAsString(COLUMN_PHONE) ?: "(555) 000-0000"
        val email = values?.getAsString(COLUMN_EMAIL) ?: "new.contact@example.com"
        val address = values?.getAsString(COLUMN_ADDRESS) ?: "No address provided"
        val favorite = values?.getAsBoolean(COLUMN_FAVORITE) ?: false
        
        // Check if a contact with the same name already exists
        val existingContact = contacts.find { it.name == name }
        if (existingContact != null) {
            // Update the existing contact instead of creating a new one
            existingContact.phone = phone
            existingContact.email = email
            existingContact.address = address
            existingContact.favorite = favorite
            
            Log.d("PillarsContentProvider", "Contact updated: ${existingContact.name}")
            return Uri.parse("$CONTENT_URI/${existingContact.id}")
        }
        
        val contact = Contact(
            name = name,
            phone = phone,
            email = email,
            address = address,
            favorite = favorite
        )
        
        contacts.add(contact)
        
        Log.d("PillarsContentProvider", "Contact added: ${contact.name}")
        return Uri.parse("$CONTENT_URI/${contact.id}")
    }
    
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d("PillarsContentProvider", "Delete called with URI: $uri")
        
        when (uriMatcher.match(uri)) {
            CONTACTS -> {
                // Delete all contacts
                val count = contacts.size
                contacts.clear()
                return count
            }
            CONTACT_ID -> {
                // Delete a specific contact
                val id = uri.lastPathSegment
                val initialSize = contacts.size
                contacts.removeAll { it.id == id }
                return initialSize - contacts.size
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
    
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d("PillarsContentProvider", "Update called with URI: $uri")
        
        when (uriMatcher.match(uri)) {
            CONTACTS -> {
                // Update all contacts (not implemented in this demo)
                return 0
            }
            CONTACT_ID -> {
                // Update a specific contact
                val id = uri.lastPathSegment
                val contact = contacts.find { it.id == id }
                
                if (contact != null && values != null) {
                    values.getAsString(COLUMN_NAME)?.let { contact.name = it }
                    values.getAsString(COLUMN_PHONE)?.let { contact.phone = it }
                    values.getAsString(COLUMN_EMAIL)?.let { contact.email = it }
                    values.getAsString(COLUMN_ADDRESS)?.let { contact.address = it }
                    values.getAsBoolean(COLUMN_FAVORITE)?.let { contact.favorite = it }
                    return 1
                }
                return 0
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
} 