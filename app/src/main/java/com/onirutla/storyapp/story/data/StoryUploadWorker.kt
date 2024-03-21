package com.onirutla.storyapp.story.data

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import arrow.core.getOrElse
import com.onirutla.storyapp.R
import com.onirutla.storyapp.auth.data.UserSessionManager
import com.onirutla.storyapp.core.util.NotificationConstant
import com.onirutla.storyapp.core.util.orZero
import com.onirutla.storyapp.story.data.source.remote.model.request.StoryRequest
import com.onirutla.storyapp.story.domain.repository.StoryRepository
import com.onirutla.storyapp.story.ui.StoryActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

@HiltWorker
class StoryUploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val repository: StoryRepository,
    private val userSessionManager: UserSessionManager,
) : CoroutineWorker(appContext = context, params = workerParameters) {

    override suspend fun doWork(): Result {
        val requestBody = with(workerParameters.inputData) {
            val photoUri = getString("photo").orEmpty().toUri()
            val timestamp = LocalDateTime.now()
            val dateTimeString = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(timestamp)
            val photoFile = File.createTempFile(dateTimeString, ".jpg").apply {
                context.contentResolver.openInputStream(photoUri).use { inputStream ->
                    outputStream().use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                }
                val compressQuality = 80
                BitmapFactory.decodeFile(path).apply {
                    compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream())
                }
            }

            StoryRequest(
                description = getString("description").orEmpty(),
                lat = getDouble("lat", 0.0).orZero(),
                lon = getDouble("lon", 0.0).orZero(),
                photo = photoFile
            )
        }
        val token = userSessionManager.getUserSession()?.token.orEmpty()

        val contentIntent = Intent(context, StoryActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            NotificationConstant.UPLOAD_NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(
            context,
            NotificationConstant.UPLOAD_NOTIFICATION_CHANNEL_ID
        ).setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.posting_story))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentText("Story is uploading")
            .setOngoing(true)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannelCompat.Builder(
                    NotificationConstant.UPLOAD_NOTIFICATION_CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT
                ).setName(NotificationConstant.UPLOAD_NOTIFICATION_NAME)
                    .build()
                createNotificationChannel(notificationChannel)
            }
        }

        val result = repository.uploadStory(
            request = requestBody,
            token = token
        ) { sent, length ->
            val percent = (sent.toInt().toDouble() / length.toDouble())
            Timber.d("sent: $sent, length: $length, percent: $percent")
            notificationManager.notify(
                NotificationConstant.UPLOAD_NOTIFICATION_ID,
                notificationBuilder.setContentText("Progress $sent/$length")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setProgress(length.toInt(), sent.toInt(), false)
                    .setVibrate(longArrayOf(0))
                    .setOngoing(true)
                    .build()
            )
        }.mapLeft {
            Timber.e("result failure: $it")
            notificationBuilder.setContentTitle("Failed posting story")
            Result.failure(workDataOf("message" to it.message.orEmpty()))
        }.map {
            if (it.error == true) {
                notificationBuilder.setContentTitle("Failed posting story")
                Result.failure(workDataOf("message" to it.message.orEmpty()))
            } else {
                notificationBuilder.setContentTitle("Success posting story")
                Result.success(workDataOf("message" to it.message.orEmpty()))
            }
        }.getOrElse { it }

        val resultPermissionCheck = ActivityCompat
            .checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        if (resultPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            return result
        }
        delay(1.seconds)
        notificationManager.notify(
            NotificationConstant.UPLOAD_NOTIFICATION_ID,
            notificationBuilder.setProgress(0, 0, false)
                .setContentText(result.outputData.getString("message").orEmpty())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(false)
                .clearActions()
                .build()
        )
        return result
    }
}