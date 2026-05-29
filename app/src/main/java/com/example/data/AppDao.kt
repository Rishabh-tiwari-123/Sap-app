package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Forum Posts ---
    @Query("SELECT * FROM forum_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<ForumPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: ForumPost): Long

    @Query("UPDATE forum_posts SET likesCount = likesCount + 1 WHERE id = :postId")
    suspend fun incrementLikes(postId: Int)

    @Query("UPDATE forum_posts SET commentsCount = commentsCount + 1 WHERE id = :postId")
    suspend fun incrementCommentsCount(postId: Int)

    // --- Forum Comments ---
    @Query("SELECT * FROM forum_comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<ForumComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ForumComment)

    // --- Direct Messages ---
    @Query("SELECT * FROM direct_messages WHERE (senderEmail = :userA AND receiverEmail = :userB) OR (senderEmail = :userB AND receiverEmail = :userA) ORDER BY timestamp ASC")
    fun getMessageHistory(userA: String, userB: String): Flow<List<DirectMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DirectMessage)

    @Query("SELECT DISTINCT senderName as name, senderEmail as email FROM direct_messages WHERE receiverEmail = :myEmail UNION SELECT DISTINCT receiverName as name, receiverEmail as email FROM direct_messages WHERE senderEmail = :myEmail")
    suspend fun getRecentChatContacts(myEmail: String): List<ChatContact>

    // --- Uploaded Documents ---
    @Query("SELECT * FROM uploaded_documents ORDER BY uploadTime DESC")
    fun getAllDocuments(): Flow<List<UploadedDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: UploadedDocument)

    @Query("DELETE FROM uploaded_documents WHERE id = :docId")
    suspend fun deleteDocumentById(docId: Int)

    // --- Module Progress ---
    @Query("SELECT * FROM module_progress")
    fun getAllModuleProgress(): Flow<List<ModuleProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModuleProgress(progress: ModuleProgress)

    // --- Exam Records ---
    @Query("SELECT * FROM exam_records ORDER BY timestamp DESC")
    fun getAllExamRecords(): Flow<List<ExamRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamRecord(record: ExamRecord)
}

data class ChatContact(
    val name: String,
    val email: String
)
