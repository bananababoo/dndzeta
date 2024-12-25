package org.banana_inc.resourcepack

import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.Permission
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import me.bananababoo.dndzeta.BuildConfig
import java.io.ByteArrayInputStream

object GoogleDrive {
    fun addOrReplaceFile(
        driveService: Drive,
        byteArrayInputStream: ByteArrayInputStream,
    ) {
        val fileName = "pack.zip" // The desired file name
        val contentType = "application/zip" // MIME type for a zip file

        // Step 1: Search for the existing file
        val query = StringBuilder("name = '$fileName' and trashed = false")

        val existingFiles = driveService.files().list()
            .setQ(query.toString())
            .setFields("files(id, name)")
            .execute()
            .files

        // Step 2: Create InputStreamContent from the ByteArrayInputStream
        val contentStream = InputStreamContent(contentType, byteArrayInputStream)

        if (existingFiles.isNotEmpty()) {
            // Replace the existing file
            val existingFileId = existingFiles[0].id
            driveService.files().update(existingFileId, null, contentStream).execute()
            ResourcePackProvider.fileId = existingFileId
            println("Replaced existing file: $fileName (ID: $existingFileId)")
        } else {
            // Add a new file
            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = fileName
            }
            val newFile = driveService.files().create(fileMetadata, contentStream).execute()
            ResourcePackProvider.fileId = newFile.id
            println("Created new file: $fileName (ID: ${newFile.id})")
        }

        val permission = Permission().apply {
            type = "anyone"  // This means the file will be accessible to anyone on the internet
            role = "reader"  // Read-only access, allowing anyone to view it
        }
        driveService.permissions().create(ResourcePackProvider.fileId, permission).execute()

        // Generate and print the file URL
        val fileUrl = "https://drive.google.com/file/d/${ResourcePackProvider.fileId}/view?usp=sharing"
        println("Resource Pack URL: $fileUrl")
    }

    fun getDriveService(): Drive {
        val credentials = GoogleCredentials
            .fromStream(ByteArrayInputStream(BuildConfig.gdriveAuth.toByteArray()))
            .createScoped(listOf(DriveScopes.DRIVE))

        val httpTransport = NetHttpTransport()

        val jsonFactory = GsonFactory.getDefaultInstance()
        return Drive.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("Drive API Kotlin")
            .build()
    }

}