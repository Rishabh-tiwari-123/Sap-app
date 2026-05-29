package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forum_posts")
data class ForumPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOwner: Boolean = false,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val moduleTag: String = "General"
)

@Entity(tableName = "forum_comments")
data class ForumComment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val content: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOwner: Boolean = false
)

@Entity(tableName = "direct_messages")
data class DirectMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val senderEmail: String,
    val receiverName: String,
    val receiverEmail: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "uploaded_documents")
data class UploadedDocument(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileSize: String,
    val category: String,
    val uploadTime: Long = System.currentTimeMillis(),
    val summary: String = ""
)

@Entity(tableName = "module_progress")
data class ModuleProgress(
    @PrimaryKey val id: String, // e.g. "abap_1", "btp_2"
    val moduleName: String,
    val category: String,
    val isCompleted: Boolean = false,
    val score: Int = 0
)

@Entity(tableName = "exam_records")
data class ExamRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examCode: String, // C_ACT_2403, etc.
    val examName: String,
    val score: Int,
    val totalQuestions: Int,
    val passed: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
