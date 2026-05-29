package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val dao: AppDao) {

    val allPosts: Flow<List<ForumPost>> = dao.getAllPosts()
    val allDocuments: Flow<List<UploadedDocument>> = dao.getAllDocuments()
    val allModuleProgress: Flow<List<ModuleProgress>> = dao.getAllModuleProgress()
    val allExamRecords: Flow<List<ExamRecord>> = dao.getAllExamRecords()

    suspend fun insertPost(post: ForumPost) = withContext(Dispatchers.IO) {
        dao.insertPost(post)
    }

    suspend fun incrementLikes(postId: Int) = withContext(Dispatchers.IO) {
        dao.incrementLikes(postId)
    }

    suspend fun incrementCommentsCount(postId: Int) = withContext(Dispatchers.IO) {
        dao.incrementCommentsCount(postId)
    }

    fun getComments(postId: Int): Flow<List<ForumComment>> {
        return dao.getCommentsForPost(postId)
    }

    suspend fun insertComment(comment: ForumComment) = withContext(Dispatchers.IO) {
        dao.insertComment(comment)
    }

    fun getMessages(userA: String, userB: String): Flow<List<DirectMessage>> {
        return dao.getMessageHistory(userA, userB)
    }

    suspend fun insertMessage(message: DirectMessage) = withContext(Dispatchers.IO) {
        dao.insertMessage(message)
    }

    suspend fun getRecentContacts(myEmail: String): List<ChatContact> = withContext(Dispatchers.IO) {
        dao.getRecentChatContacts(myEmail)
    }

    suspend fun saveDocument(doc: UploadedDocument) = withContext(Dispatchers.IO) {
        dao.insertDocument(doc)
    }

    suspend fun deleteDocument(docId: Int) = withContext(Dispatchers.IO) {
        dao.deleteDocumentById(docId)
    }

    suspend fun updateModuleProgress(progress: ModuleProgress) = withContext(Dispatchers.IO) {
        dao.insertModuleProgress(progress)
    }

    suspend fun saveExamRecord(record: ExamRecord) = withContext(Dispatchers.IO) {
        dao.insertExamRecord(record)
    }

    // Populate initial default data securely in background
    suspend fun populateInitialDataIfEmpty(myEmail: String, myName: String) = withContext(Dispatchers.IO) {
        val currentProgress = dao.getAllModuleProgress().firstOrNull() ?: emptyList()
        if (currentProgress.isEmpty()) {
            val defaultModules = listOf(
                ModuleProgress("abap_rap", "ABAP RESTful Application Model (RAP)", "ABAP on HANA", false, 0),
                ModuleProgress("s4_ext", "S/4HANA Cloud Extensibility Guide", "S/4HANA", false, 0),
                ModuleProgress("btp_cap", "SAP BTP Cloud Application Model (CAP)", "BTP Platform", false, 0),
                ModuleProgress("fiori_m3", "Modern Fiori Elements with Material 3", "Fiori", false, 0)
            )
            defaultModules.forEach { dao.insertModuleProgress(it) }

            // Pre-seed some interesting and welcoming forum posts
            val welcomePostId = dao.insertPost(
                ForumPost(
                    title = "✨ Welcome to the Future of SAP Learning!",
                    content = "Global SAP Learners, greeting! We established this platform as an interactive cybernetic hub. Collaborate on S/4HANA Developer extensibility, practice latest certification tests, and network peer-to-peer globally. Feel free to raise questions, share code snippets and post SAP trends here!",
                    authorName = "Rajat Tiwari",
                    authorEmail = "rajat.tiwari.250196@gmail.com",
                    isOwner = true,
                    likesCount = 28,
                    commentsCount = 2,
                    moduleTag = "SAP BTP & S/4"
                )
            ).toInt()

            dao.insertComment(
                ForumComment(
                    postId = welcomePostId,
                    content = "Incredible initiative! I can't wait to test my ABAP RAP knowledge on the mock practice exams.",
                    authorName = "Priyanka Nair",
                    authorEmail = "priyanka.n@example.com",
                    isOwner = false
                )
            )
            dao.insertComment(
                ForumComment(
                    postId = welcomePostId,
                    content = "Super helpful! Love the neon aesthetic, very fresh for SAP. Owner Rajat, thank you for putting this together!",
                    authorName = "Arjun Patel",
                    authorEmail = "arjun.p@example.com",
                    isOwner = false
                )
            )

            // Another post about S/4 HANA
            val s4PostId = dao.insertPost(
                ForumPost(
                    title = "🚨 ABAP Cloud: Custom CDS View Join Problem",
                    content = "I'm writing a custom analytic query combining I_SalesOrderCube with custom z-tables on SAP BTP Developer Extensibility. I keep getting syntax checks on client-independence constraint. Any expert suggestions from India or Europe teams?",
                    authorName = "Anish Kumar",
                    authorEmail = "anish.k@example.com",
                    isOwner = false,
                    likesCount = 8,
                    commentsCount = 1,
                    moduleTag = "ABAP on HANA"
                )
            ).toInt()

            dao.insertComment(
                ForumComment(
                    postId = s4PostId,
                    content = "Ensure your CDS projection is annotated with @ObjectModel.filter.enabled: true and check the mandate of system clients on BTP environment. ABAP Cloud is strict on client isolation.",
                    authorName = "Rajat Tiwari",
                    authorEmail = "rajat.tiwari.250196@gmail.com",
                    isOwner = true
                )
            )

            // Seed some preuploaded study sheets/documents
            val defaultDocs = listOf(
                UploadedDocument(
                    fileName = "SAP_ABAP_Cloud_Cheatsheet_v2.pdf",
                    fileSize = "2.4 MB",
                    category = "ABAP on HANA",
                    summary = "This cheat sheet covers the essential syntax of ABAP Cloud: RESTful Application Programming (RAP), strict type checks, forbidden traditional syntax (e.g., DIRECT SQL, obsolete statements), and CDS View entity constructs."
                ),
                UploadedDocument(
                    fileName = "S4HANA_Clean_Core_Strategy.pdf",
                    fileSize = "4.1 MB",
                    category = "S/4HANA",
                    summary = "Detailed official whitepaper explaining the Clean Core Strategy: Developer Extensibility, Key User Extensibility, Side-by-Side Extensibility on BTP, and standard pre-delivered APIs to reduce upgrade costs."
                )
            )
            defaultDocs.forEach { dao.insertDocument(it) }

            // Seed initial chats to make it alive
            dao.insertMessage(
                DirectMessage(
                    senderName = "Priyanka Nair",
                    senderEmail = "priyanka.n@example.com",
                    receiverName = "Rajat Tiwari",
                    receiverEmail = "rajat.tiwari.250196@gmail.com",
                    message = "Hi Rajat! I saw your post on the BTP CAP model tutorial. Is there any advanced lesson on Approuter configuration?"
                )
            )
            dao.insertMessage(
                DirectMessage(
                    senderName = "Rajat Tiwari",
                    senderEmail = "rajat.tiwari.250196@gmail.com",
                    receiverName = "Priyanka Nair",
                    receiverEmail = "priyanka.n@example.com",
                    message = "Hello Priyanka! Yes, the BTP section includes Approuter routing. Feel free to check the Doc vault; I uploaded the SAP BTP router config guide there! Let me know if you need any assistance."
                )
            )
        }
    }
}
