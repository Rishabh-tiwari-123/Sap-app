package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiHelper
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    // Session User details (strictly matching user request to be owner)
    val sessionUserEmail = "rajat.tiwari.250196@gmail.com"
    val sessionUserName = "Rajat Tiwari"

    // Core Active Screens
    // Tabs: LEARN, EXAMS, COMMUNITY, MESSAGES, DOCUMENTS, NEWS
    private val _activeTab = MutableStateFlow(Tab.LEARN)
    val activeTab: StateFlow<Tab> = _activeTab.asStateFlow()

    enum class Tab(val title: String) {
        LEARN("Learn SAP"),
        EXAMS("Certification Prep"),
        COMMUNITY("Global Forums"),
        MESSAGES("Direct Peer Chat"),
        DOCUMENTS("Study Vault"),
        NEWS("AI News Corner")
    }

    // --- State: Interactive Modules Group ---
    val allModuleProgress: StateFlow<List<ModuleProgress>> = MutableStateFlow(emptyList())
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Active learning module (Interactive guide & interactive mini quiz)
    private val _activeLessonModule = MutableStateFlow<LessonModule?>(null)
    val activeLessonModule: StateFlow<LessonModule?> = _activeLessonModule.asStateFlow()

    private val _activeLessonQuizIndex = MutableStateFlow(0)
    val activeLessonQuizIndex: StateFlow<Int> = _activeLessonQuizIndex.asStateFlow()

    private val _selectedQuizAnswer = MutableStateFlow<Int?>(null)
    val selectedQuizAnswer: StateFlow<Int?> = _selectedQuizAnswer.asStateFlow()

    private val _quizChecked = MutableStateFlow(false)
    val quizChecked: StateFlow<Boolean> = _quizChecked.asStateFlow()

    // --- State: Practice Exam Module ---
    val allExamRecords: StateFlow<List<ExamRecord>> = MutableStateFlow(emptyList())
    private val _activeExamSession = MutableStateFlow<ExamSession?>(null)
    val activeExamSession: StateFlow<ExamSession?> = _activeExamSession.asStateFlow()

    // --- State: Community Forum Module ---
    val allPosts: StateFlow<List<ForumPost>> = MutableStateFlow(emptyList())
    private val _selectedPost = MutableStateFlow<ForumPost?>(null)
    val selectedPost: StateFlow<ForumPost?> = _selectedPost.asStateFlow()
    val activeComments: StateFlow<List<ForumComment>> = MutableStateFlow(emptyList())

    // --- State: Direct Messaging Module ---
    private val _selectedChatPeer = MutableStateFlow<ChatContact?>(null)
    val selectedChatPeer: StateFlow<ChatContact?> = _selectedChatPeer.asStateFlow()
    val activeChatHistory: StateFlow<List<DirectMessage>> = MutableStateFlow(emptyList())

    // Peer contacts list to select from
    val globalPeers = listOf(
        ChatContact("Priyanka Nair", "priyanka.n@example.com"),
        ChatContact("Arjun Patel", "arjun.p@example.com"),
        ChatContact("Christian Reinhardt (Walldorf)", "christian.r@example.com"),
        ChatContact("Sophia Chen (Singapore)", "sophia.c@example.com"),
        ChatContact("Amit Sharma (Bangalore)", "amit.s@example.com")
    )

    // --- State: Documents Upload Column ---
    val allDocuments: StateFlow<List<UploadedDocument>> = MutableStateFlow(emptyList())
    private val _docSummaryLoading = MutableStateFlow(false)
    val docSummaryLoading: StateFlow<Boolean> = _docSummaryLoading.asStateFlow()

    // --- State: News Corner & Forecasting Hub ---
    private val _newsFeed = MutableStateFlow<String>("")
    val newsFeed: StateFlow<String> = _newsFeed.asStateFlow()

    private val _newsQuery = MutableStateFlow("S/4HANA Cloud Clean Core integration & Joule 2026 update")
    val newsQuery: StateFlow<String> = _newsQuery.asStateFlow()

    private val _newsLoading = MutableStateFlow(false)
    val newsLoading: StateFlow<Boolean> = _newsLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.dao())

        // Fetch Room Flows
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty(sessionUserEmail, sessionUserName)

            // Direct data binding
            repository.allModuleProgress.collect { (allModuleProgress as MutableStateFlow).value = it }
        }
        viewModelScope.launch {
            repository.allPosts.collect { (allPosts as MutableStateFlow).value = it }
        }
        viewModelScope.launch {
            repository.allDocuments.collect { (allDocuments as MutableStateFlow).value = it }
        }
        viewModelScope.launch {
            repository.allExamRecords.collect { (allExamRecords as MutableStateFlow).value = it }
        }

        // Submitting default comment tracking/messages bindings dynamically when chosen
        viewModelScope.launch {
            _selectedPost.collect { post ->
                if (post != null) {
                    repository.getComments(post.id).collect {
                        (activeComments as MutableStateFlow).value = it
                    }
                } else {
                    (activeComments as MutableStateFlow).value = emptyList()
                }
            }
        }

        viewModelScope.launch {
            _selectedChatPeer.collect { peer ->
                if (peer != null) {
                    repository.getMessages(sessionUserEmail, peer.email).collect {
                        (activeChatHistory as MutableStateFlow).value = it
                    }
                } else {
                    (activeChatHistory as MutableStateFlow).value = emptyList()
                }
            }
        }

        // Initialize News with default trend forecast on startup
        refreshNews()
    }

    // --- Tab Navigate ---
    fun selectTab(tab: Tab) {
        _activeTab.value = tab
        // Close specific drawers on tab change
        _activeLessonModule.value = null
        _activeExamSession.value = null
        _selectedPost.value = null
    }

    // --- Interactive Modules Actions ---
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun startLesson(module: ModuleProgress) {
        val detail = sapLessonsMap[module.id] ?: return
        _activeLessonModule.value = detail
        _activeLessonQuizIndex.value = 0
        _selectedQuizAnswer.value = null
        _quizChecked.value = false
    }

    fun exitLesson() {
        _activeLessonModule.value = null
    }

    fun selectQuizAnswer(index: Int) {
        if (!_quizChecked.value) {
            _selectedQuizAnswer.value = index
        }
    }

    fun checkQuizAnswer() {
        _quizChecked.value = true
        val lesson = _activeLessonModule.value ?: return
        val isCorrect = _selectedQuizAnswer.value == lesson.quizCorrectIndex

        // If correct, update database progress
        if (isCorrect) {
            viewModelScope.launch {
                repository.updateModuleProgress(
                    ModuleProgress(
                        id = lesson.id,
                        moduleName = lesson.title,
                        category = lesson.category,
                        isCompleted = true,
                        score = 100
                    )
                )
            }
        }
    }

    // --- Practice Exam Actions ---
    fun startExam(exam: MockExamSpec) {
        _activeExamSession.value = ExamSession(
            spec = exam,
            currentQuestionIndex = 0,
            answers = mutableMapOf()
        )
    }

    fun selectExamAnswer(optionIndex: Int) {
        val session = _activeExamSession.value ?: return
        val updatedAnswers = session.answers.toMutableMap().apply {
            put(session.currentQuestionIndex, optionIndex)
        }
        _activeExamSession.value = session.copy(answers = updatedAnswers)
    }

    fun nextExamQuestion() {
        val session = _activeExamSession.value ?: return
        if (session.currentQuestionIndex < session.spec.questions.size - 1) {
            _activeExamSession.value = session.copy(currentQuestionIndex = session.currentQuestionIndex + 1)
        }
    }

    fun prevExamQuestion() {
        val session = _activeExamSession.value ?: return
        if (session.currentQuestionIndex > 0) {
            _activeExamSession.value = session.copy(currentQuestionIndex = session.currentQuestionIndex - 1)
        }
    }

    fun submitExam() {
        val session = _activeExamSession.value ?: return
        val questions = session.spec.questions
        var correctCount = 0
        questions.forEachIndexed { index, question ->
            if (session.answers[index] == question.correctIndex) {
                correctCount++
            }
        }

        val scorePercentage = (correctCount.toFloat() / questions.size * 100).toInt()
        val passed = scorePercentage >= 70

        viewModelScope.launch {
            repository.saveExamRecord(
                ExamRecord(
                    examCode = session.spec.code,
                    examName = session.spec.name,
                    score = scorePercentage,
                    totalQuestions = questions.size,
                    passed = passed
                )
            )
            _activeExamSession.value = null // Close exam
        }
    }

    fun cancelExam() {
        _activeExamSession.value = null
    }

    // --- Community Forum Actions ---
    fun selectPost(post: ForumPost) {
        _selectedPost.value = post
    }

    fun exitPostDetail() {
        _selectedPost.value = null
    }

    fun addPost(title: String, content: String, moduleTag: String) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            repository.insertPost(
                ForumPost(
                    title = title,
                    content = content,
                    authorName = sessionUserName,
                    authorEmail = sessionUserEmail,
                    isOwner = true, // Current developer-user is "Owner"
                    moduleTag = moduleTag
                )
            )
        }
    }

    fun addComment(content: String) {
        val post = _selectedPost.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.insertComment(
                ForumComment(
                    postId = post.id,
                    content = content,
                    authorName = sessionUserName,
                    authorEmail = sessionUserEmail,
                    isOwner = true // Current developer-user is "Owner"
                )
            )
            repository.incrementCommentsCount(post.id)
            
            // Refresh local object for instant UI update
            _selectedPost.value = post.copy(commentsCount = post.commentsCount + 1)
        }
    }

    fun likePost(postId: Int) {
        viewModelScope.launch {
            repository.incrementLikes(postId)
        }
    }

    // --- Direct Messaging Actions ---
    fun selectChat(peer: ChatContact) {
        _selectedChatPeer.value = peer
    }

    fun closeChat() {
        _selectedChatPeer.value = null
    }

    fun sendChatMessage(text: String) {
        val peer = _selectedChatPeer.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            val msg = DirectMessage(
                senderName = sessionUserName,
                senderEmail = sessionUserEmail,
                receiverName = peer.name,
                receiverEmail = peer.email,
                message = text
            )
            repository.insertMessage(msg)

            // Simulate automatic interactive response after a short delay!
            // This represents a real peer connected on the other side.
            simulatePeerResponse(peer, text)
        }
    }

    private fun simulatePeerResponse(peer: ChatContact, userText: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Delay to feel natural
            val responseText = when {
                userText.lowercase().contains("hi") || userText.lowercase().contains("hello") -> {
                    "Hello Rajat! Good to connect with a senior SAP architect from the global hub. How is the S/4HANA extensibility project going?"
                }
                userText.lowercase().contains("abap") || userText.lowercase().contains("rap") || userText.lowercase().contains("code") -> {
                    "Totally agree! I am studying the ABAP RAP behavior controls right now. Working with Entity Manipulation Language (EML) requires some getting used to compared to open SQL."
                }
                userText.lowercase().contains("btp") || userText.lowercase().contains("cap") -> {
                    "I find BTP CAP to be extremely comfortable with node.js. Is your team deploying standard Fiori launchpads on BTP Launchpad Service?"
                }
                userText.lowercase().contains("cert") || userText.lowercase().contains("exam") || userText.lowercase().contains("prep") -> {
                    "Yes, I am planning to schedule the S/4 Extensibility exam next week in Bangalore/Mumbai center. Your mock prep tests on this app are an awesome resource!"
                }
                else -> {
                    "Great point Rajat! Thanks for the feedback. Let's collaborate. Have you uploaded any new ABAP or BTP architecture specifications to the Study Vault?"
                }
            }

            repository.insertMessage(
                DirectMessage(
                    senderName = peer.name,
                    senderEmail = peer.email,
                    receiverName = sessionUserName,
                    receiverEmail = sessionUserEmail,
                    message = responseText
                )
            )
        }
    }

    // --- Documents Vault Actions ---
    fun uploadDocument(fileName: String, category: String) {
        if (fileName.isBlank()) return
        val ext = if (fileName.contains(".")) "" else ".pdf"
        val sizeString = "${(1..9).random()}.${(1..9).random()} MB"
        val doc = UploadedDocument(
            fileName = "$fileName$ext",
            fileSize = sizeString,
            category = category,
            summary = "Scanning document metadata. Click 'AI Summary' below to let Gemini analyze this SAP guide..."
        )
        viewModelScope.launch {
            repository.saveDocument(doc)
        }
    }

    fun removeDoc(docId: Int) {
        viewModelScope.launch {
            repository.deleteDocument(docId)
        }
    }

    fun requestDocsSummary(doc: UploadedDocument) {
        _docSummaryLoading.value = true
        viewModelScope.launch {
            val prompt = "Provide a comprehensive, high-quality, professional study guide summary (with key definitions, bullet points, and mock interview tips) for an SAP study document titled '${doc.fileName}' under category '${doc.category}'."
            val systemIns = "You are a senior SAP Platinum Mentor holding global certifications. Format response beautifully on dark markdown with code fragments where helpful."
            val aiResponse = GeminiHelper.generateContent(prompt, systemIns)
            
            val updatedDoc = doc.copy(summary = aiResponse)
            repository.saveDocument(updatedDoc)
            _docSummaryLoading.value = false
        }
    }

    // --- News Corner & Forecasting Actions ---
    fun updateNewsQuery(q: String) {
        _newsQuery.value = q
    }

    fun refreshNews() {
        _newsLoading.value = true
        viewModelScope.launch {
            val prompt = "Generate a highly informative, futuristic forecasting update regarding the latest trend details on '${_newsQuery.value}' including industry adoption across India and globally, technolgical advancements, cloud capabilities, and essential advice for youth to start learning."
            val systemIns = "You are an expert Chief SAP Technology Officer reporting latest internet updates. Write in a crisp, ultra-modern tech-journalist style with distinct sections, neon icons, and bullet lists in clear responsive markdown."
            val responseText = GeminiHelper.generateContent(prompt, systemIns)
            _newsFeed.value = responseText
            _newsLoading.value = false
        }
    }
}

// --- Local SAP Lesson Specifications Data Model ---
data class LessonModule(
    val id: String,
    val title: String,
    val category: String,
    val contentMarkdown: String,
    val quizQuestion: String,
    val quizOptions: List<String>,
    val quizCorrectIndex: Int,
    val quizExplanation: String
)

val sapLessonsMap = mapOf(
    "abap_rap" to LessonModule(
        "abap_rap",
        "ABAP RESTful Application Model (RAP)",
        "ABAP on HANA",
        """
        ### 💻 Master ABAP Cloud & RAP
        
        The **ABAP RESTful Application Model (RAP)** defines the architecture for efficient, cloud-ready development on SAP S/4HANA and SAP BTP.
        
        #### Core Concepts:
        1. **CDS View Entities**: Built-in native database entities providing declarative projections.
        2. **Behavior Definition (BD)**: Declared using `DEFINE BEHAVIOR FOR`, defining capabilities like `create`, `update`, `delete`, `action`.
        3. **Behavior Implementation (BP)**: Handled via specialized Local Handler Classes in ABAP OO.
        4. **Business Service**: Binding and publishing services to external REST/OData protocols.
        
        #### Why it Attracts Youth:
        Traditional ABAP was heavy on transactional Dynpro screens. RAP introduces modern RESTful design patterns, clean separation of concerns, and full compatibility with JavaScript/Fiori frontends, putting it on par with modern frameworks like Spring Boot or Express.js!
        """.trimIndent(),
        "Which statement is used to define transactional capabilities (actions, draft, locks) for a RAP Business Object?",
        listOf("DEFINE SERVICE EXPOSURE", "DEFINE VIEW ENTITY", "DEFINE BEHAVIOR FOR", "SELECT FROM DATABASE"),
        2,
        "Behavior Definition (`DEFINE BEHAVIOR FOR`) is the core specification that configures create, update, delete, actions, locks, and draft mode handling."
    ),
    "s4_ext" to LessonModule(
        "s4_ext",
        "S/4HANA Cloud Extensibility Guide",
        "S/4HANA",
        """
        ### 🎯 Clean Core & S/4HANA Extensibility
        
        S/4HANA Cloud requires customers to preserve a standard, upgradeable standard core. 
        
        #### The Three Extensibility Dimensions:
        *   **Key User Extensibility**: Code-free extensions, custom fields, custom business objects, done in-browser.
        *   **Developer Extensibility (On-Stack)**: Strict ABAP Cloud development directly on the SAP S/4HANA system. Replaces traditional BADIs and modifications with secure Cloud APIs.
        *   **Side-by-Side Extensibility**: Standalone responsive applications running on SAP BTP using modern developer pipelines.
        
        #### Clean Core Principal:
        "Keep the Core Clean" ensures SAP S/4HANA can be continuously upgraded autonomously by SAP without breaking customized functionalities. This reduces maintenance overheads to zero!
        """.trimIndent(),
        "What is the recommended practice for custom logic that requires highly complex database integrations not available on the ERP core?",
        listOf("Modify standard SAP source code directly", "Side-by-Side Extensibility on SAP BTP via secure API calls", "Disable SAP updates to stabilize old code", "Write direct database SQL inside standard tables"),
        1,
        "Side-by-Side Extensibility on SAP BTP using secure OData standard APIs guarantees that ERP upgrades never break custom applications, complying with the Clean Core standard."
    ),
    "btp_cap" to LessonModule(
        "btp_cap",
        "SAP BTP Cloud Application Model (CAP)",
        "BTP Platform",
        """
        ### ☁️ BTP Cloud Application Programming (CAP)
        
        The **SAP CAP** model is a framework of languages, libraries, and tools for building enterprise-grade services and applications on cloud infrastructures.
        
        #### Core Pillars:
        1. **Declarative Schemas**: Modeled in high-level Core Data Services (`.cds`) notation.
        2. **Dual-Language Business Logic**: Fully supports Node.js (JavaScript/TypeScript) and Java out-of-the-box.
        3. **Plug-and-Play Services**: Native integration with enterprise authentication (JWT/IAS), Event Mesh, Destinations, and SAP HANA.
        
        #### Youth Developer Appeal:
        CAP is designed with an open-source, flexible philosophy. You can write your custom logic using standard VS Code, standard npm packages, mocha testing, in standard Javascript! This bridges the entire gap between general software engineering and enterprise SAP development.
        """.trimIndent(),
        "Which programming languages are supported out-of-the-box by the CAP framework for writing back-end logic?",
        listOf("ABAP and COBOL", "Node.js (JS/TS) and Java", "Python and Rust", "Go and PHP"),
        1,
        "CAP officially supports Node.js (JavaScript/TypeScript) and Java for the implementation of custom logic, event handlers, and data transformations."
    ),
    "fiori_m3" to LessonModule(
        "fiori_m3",
        "Modern Fiori Elements with Material 3",
        "Fiori",
        """
        ### 🎨 Sleek SAP Fiori Elements
        
        Fiori Elements provides generic, responsive UI components that dynamically configure themselves based on CDS annotations on the backend.
        
        #### Key Canonical Layouts:
        *   **List Report**: Grid list with intense search criteria and visual stats filters.
        *   **Object Page**: High-fidelity detail page with sections, rich telemetry tables, and edit forms.
        *   **Analytical List Page (ALP)**: Dashboard featuring real-time charts synced automatically with table filters.
        
        With the incorporation of Material 3 and dynamic colorings, Fiori elements are evolving from enterprise forms into beautiful, dark-capable, mobile-friendly interactive experiences with high response rates.
        """.trimIndent(),
        "How is the UI of an SAP Fiori Elements application primarily configured?",
        listOf("Writing custom HTML/CSS stylesheet files", "Using backend annotations in CDS View Entities", "Drawing pixel-wise inside an XML Canvas", "Generating elements inside old Web-dynpro tables"),
        1,
        "Fiori Elements compiles its UI definitions automatically based on backend Metadata Annotations declared in the CDS view entities, drastically reducing frontend code."
    )
)

// --- Exam Specifications & Questions Data Model ---
data class MockExamSpec(
    val code: String,
    val name: String,
    val totalTimeMin: Int,
    val questions: List<ExamQuestion>
)

data class ExamQuestion(
    val query: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class ExamSession(
    val spec: MockExamSpec,
    val currentQuestionIndex: Int,
    val answers: Map<Int, Int> // questionIndex -> selectedOptionIndex
)

val mockExamsList = listOf(
    MockExamSpec(
        code = "C_ACT_2403",
        name = "SAP Certified Associate - S/4HANA Cloud Extensibility",
        totalTimeMin = 15,
        questions = listOf(
            ExamQuestion(
                query = "Which extensible methodology should you select to implement custom custom business calculations directly during transactional database entries in S/4HANA Cloud?",
                options = listOf(
                    "Directly modify SAP source classes",
                    "Developer Extensibility (On-Stack ABAP Cloud) using business BAdIs",
                    "Side-by-Side Application on nodeJS",
                    "Create a custom Web Dynpro component"
                ),
                correctIndex = 1,
                explanation = "On-Stack Developer Extensibility (using ABAP Cloud) allows low-latency transaction hooks such as Business Add-Ins (BAdIs) inside the ERP system safely, keeping the core clean."
            ),
            ExamQuestion(
                query = "What is the critical constraint enforced when utilizing standard CDS views as 'Cloud APIs' in Developer Extensibility?",
                options = listOf(
                    "They must belong to C1 release contract classification",
                    "They can only query local temporary DB schemas",
                    "They must use raw non-managed native SQL connections",
                    "They cannot be projected in Fiori bindings"
                ),
                correctIndex = 0,
                explanation = "Only CDS views and classes marked with Release Contract (C1) can be securely referenced in developer extensibility to guard against future upgrades breaking standard entities."
            ),
            ExamQuestion(
                query = "What SAP tool allows developers to deploy side-by-side applications with seamless OAuth configuration and destination tunnels?",
                options = listOf(
                    "SAP GUI Desktop Explorer",
                    "SAP Business Technology Platform (BTP)",
                    "SAP Cloud Connector local agent only",
                    "SAP NetWeaver Gateway Service"
                ),
                correctIndex = 1,
                explanation = "SAP Business Technology Platform is the designated suite for side-by-side SaaS extensions, security governance, and multi-tenant capabilities."
            )
        )
    ),
    MockExamSpec(
        code = "C_TAW12_750",
        name = "SAP Certified Development Associate - ABAP with SAP NetWeaver",
        totalTimeMin = 15,
        questions = listOf(
            ExamQuestion(
                query = "When defining a CDS View Entity in modern ABAP Cloud, which annotation is mandatory to specify client handling?",
                options = listOf(
                    "@ClientHandling.type: #INHERITED",
                    "No annotation is required; View Entities are client-compliant and client-independent by default",
                    "@ClientDependent: true",
                    "@ObjectModel.filter.enabled: true"
                ),
                correctIndex = 1,
                explanation = "Unlike obsolete CDS DDIC-based views, modern CDS View Entities handle clients implicitly, making explicit client annotation redundant and forbidden."
            ),
            ExamQuestion(
                query = "In the ABAP RAP framework, what construct processes the transactional modify and save operations?",
                options = listOf(
                    "ABAP Class Pool with Local Handler and Local Saver classes",
                    "Direct Open SQL statements in the Fiori Controller",
                    "Database Triggers declared in AMDP",
                    "SAP Gateway UI service files"
                ),
                correctIndex = 0,
                explanation = "The RAP Behavior Implementation utilizes Local Handler Classes (`lhc_`) for modify commands and Local Saver Classes (`lsc_`) for final save transaction orchestrations."
            ),
            ExamQuestion(
                query = "What statement is part of EML (Entity Manipulation Language) used in ABAP RAP?",
                options = listOf(
                    "UPDATE ztable FROM @wa",
                    "MODIFY ENTITIES OF root_entity_name ...",
                    "SELECT SINGLE FOR UPDATE",
                    "COMMIT WORK SYSTEM"
                ),
                correctIndex = 1,
                explanation = "Entity Manipulation Language (EML) uses statements like `MODIFY ENTITIES OF` or `READ ENTITIES OF` to interact with business object states securely."
            )
        )
    )
)
