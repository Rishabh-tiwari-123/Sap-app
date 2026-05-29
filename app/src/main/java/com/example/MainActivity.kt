package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.MainViewModel
import com.example.ui.MainViewModel.Tab
import com.example.ui.mockExamsList
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.* // Import all UI entities
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val viewModel: MainViewModel = viewModel()
    val activeTab by viewModel.activeTab.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                activeTab = activeTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        containerColor = Color(0xFF07050F),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF090616),
                            Color(0xFF07050F)
                        )
                    )
                )
        ) {
            // Futuristic cosmic stardust drawing on background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = java.util.Random(42L)
                for (i in 0..30) {
                    val x = r.nextFloat() * size.width
                    val y = r.nextFloat() * size.height
                    val radius = r.nextFloat() * 3f + 1f
                    val alpha = r.nextFloat() * 0.5f + 0.1f
                    drawCircle(
                        color = Color(0xFF00E5FF),
                        radius = radius,
                        center = Offset(x, y),
                        alpha = alpha
                    )
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Enterprise Space-Hub Header
                AppHeader(
                    userName = viewModel.sessionUserName,
                    userEmail = viewModel.sessionUserEmail
                )

                HorizontalDivider(
                    color = Color(0xFF2C2455),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Tab Content Switcher with animations
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (activeTab) {
                        Tab.LEARN -> LearnTabScreen(viewModel)
                        Tab.EXAMS -> ExamsTabScreen(viewModel)
                        Tab.COMMUNITY -> CommunityTabScreen(viewModel)
                        Tab.MESSAGES -> MessagesTabScreen(viewModel)
                        Tab.DOCUMENTS -> DocumentsTabScreen(viewModel)
                        Tab.NEWS -> NewsTabScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeader(userName: String, userEmail: String) {
    val initials = userName.split(" ")
        .filter { it.isNotBlank() }
        .map { it.first().uppercase() }
        .joinToString("")
        .take(2)
        .ifEmpty { "JD" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "GLOBAL NODE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF22D3EE),
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SAP ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Pulse",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22D3EE),
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Owner Badge for Rajat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "OWNER",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF59E0B),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Text(
                text = "Connecting All-India & Global SAP Consultants",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Profile Badge with gradient ring and status indicator
        Box(
            modifier = Modifier.size(44.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(Color(0xFF22D3EE), Color(0xFF6366F1), Color(0xFF22D3EE))
                        )
                    )
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF0B0F1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            // Online status dot (bottom-right)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                    .border(2.dp, Color(0xFF0B0F1A), CircleShape)
            )
        }
    }
}

// Custom Markdown Text Layout Printer supporting headers, bullets and code snippets
@Composable
fun CyberMarkdownText(text: String, modifier: Modifier = Modifier) {
    val lines = text.split("\n")
    Column(modifier = modifier) {
        lines.forEach { line ->
            when {
                line.startsWith("###") -> {
                    Text(
                        text = line.replace("###", "").trim(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }
                line.startsWith("####") -> {
                    Text(
                        text = line.replace("####", "").trim(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB026FF),
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                line.startsWith("##") -> {
                    Text(
                        text = line.replace("##", "").trim(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF39FF14),
                        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                    )
                }
                line.startsWith("*") || line.startsWith("-") -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 12.sp,
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.padding(end = 6.dp, top = 2.dp)
                        )
                        Text(
                            text = line.substring(1).trim(),
                            fontSize = 13.sp,
                            color = Color(0xFFF1EDFF),
                            lineHeight = 18.sp
                        )
                    }
                }
                line.startsWith("1.") || line.startsWith("2.") || line.startsWith("3.") || line.startsWith("4.") -> {
                    Text(
                        text = line,
                        fontSize = 13.sp,
                        color = Color(0xFFF1EDFF),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 3.dp),
                        lineHeight = 18.sp
                    )
                }
                line.trim().startsWith("`") && line.trim().endsWith("`") -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF07050F))
                            .border(1.dp, Color(0xFF2C2455), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = line.replace("`", "").trim(),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF39FF14)
                        )
                    }
                }
                line.isNotBlank() -> {
                    Text(
                        text = line,
                        fontSize = 13.sp,
                        color = Color(0xFFA39BCB),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// --- LEARN SCREEN (MODULES & INTERACTIVE QUIZ) ---
@Composable
fun CertificationProgressHeroCard(viewModel: MainViewModel) {
    val progressList by viewModel.allModuleProgress.collectAsState()
    val total = progressList.size
    val completed = progressList.count { it.isCompleted }
    val percentage = if (total > 0) (completed * 100 / total) else 84

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .drawBehind {
                // Faded translucent styling circle/ring in the top-right
                drawCircle(
                    color = Color(0xFF22D3EE).copy(alpha = 0.04f),
                    radius = 120.dp.toPx(),
                    center = Offset(size.width - 20.dp.toPx(), 20.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFF6366F1).copy(alpha = 0.03f),
                    radius = 60.dp.toPx(),
                    center = Offset(size.width - 20.dp.toPx(), 20.dp.toPx())
                )
            }
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE CERTIFICATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF22D3EE).copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "84% ACCURACY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22D3EE),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "S/4HANA Cloud Public Edition 2408",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Extensibility and custom clean core ABAP integrations practice metrics.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Course mastery",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "$percentage%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF22D3EE),
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Beautiful Gradient Progress bar (cyan to indigo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage.toFloat() / 100f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF22D3EE), Color(0xFF6366F1))
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun QuickNavGrid(viewModel: MainViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Button 1: Interactive Modules
        Box(
            modifier = Modifier
                .weight(1f)
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .clickable { /* Already here */ }
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF22D3EE).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Modules",
                        tint = Color(0xFF22D3EE),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Column {
                    Text(
                        text = "Interactive",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "Modules",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Button 2: Certification Mock Exams
        Box(
            modifier = Modifier
                .weight(1f)
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .clickable { viewModel.selectTab(Tab.EXAMS) }
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Mock Exams",
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Column {
                    Text(
                        text = "Certification",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "Mock Exams",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LearnTabScreen(viewModel: MainViewModel) {
    val progressList by viewModel.allModuleProgress.collectAsState()
    val activeLesson by viewModel.activeLessonModule.collectAsState()
    val currentCategory by viewModel.selectedCategory.collectAsState()

    if (activeLesson != null) {
        LessonDetailScreen(viewModel, activeLesson!!)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Certification Progress Hero Card
            CertificationProgressHeroCard(viewModel)

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Nav Grid Buttons (Interactive Modules vs Mock Exams switcher layout)
            QuickNavGrid(viewModel)

            Spacer(modifier = Modifier.height(4.dp))

            // Filtering Chips (India/Global modules)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("All", "ABAP on HANA", "S/4HANA", "BTP Platform", "Fiori")
                categories.forEach { cat ->
                    val isSelected = cat == currentCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF22D3EE) else Color(0xFF0F172A))
                            .border(1.dp, if (isSelected) Color(0xFF22D3EE) else Color(0xFF1E293B), RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF0B0F1A) else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Modules list view
            val filteredList = progressList.filter {
                currentCategory == "All" || it.category == currentCategory
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { mod ->
                    ModuleItemRow(mod, onStart = { viewModel.startLesson(mod) })
                }
            }
        }
    }
}

@Composable
fun ModuleItemRow(mod: ModuleProgress, onStart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .clickable { onStart() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF22D3EE).copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = mod.category.uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22D3EE),
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mod.moduleName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (mod.isCompleted) "Status: COMPLETED" else "Status: PENDING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (mod.isCompleted) Color(0xFF10B981) else Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Progress Check representation
        if (mod.isCompleted) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22D3EE).copy(alpha = 0.15f))
                    .clickable { onStart() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Lesson",
                    tint = Color(0xFF22D3EE),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun LessonDetailScreen(viewModel: MainViewModel, lesson: LessonModule) {
    val selectedQuizAnswer by viewModel.selectedQuizAnswer.collectAsState()
    val quizChecked by viewModel.quizChecked.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Back Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { viewModel.exitLesson() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF22D3EE),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back to Modules",
                fontSize = 14.sp,
                color = Color(0xFF22D3EE),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Detailed Instruction
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            CyberMarkdownText(text = lesson.contentMarkdown)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive diagnostic review card
        Text(
            text = "⚡ CONCEPT ALIGNMENT VERIFICATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10B981),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = lesson.quizQuestion,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Standard Composable compliant for-loop to prevent context errors
                for (idx in lesson.quizOptions.indices) {
                    val opt = lesson.quizOptions[idx]
                    val isSelected = selectedQuizAnswer == idx
                    val colorBorder = when {
                        quizChecked && idx == lesson.quizCorrectIndex -> Color(0xFF10B981)
                        quizChecked && isSelected && idx != lesson.quizCorrectIndex -> Color.Red
                        isSelected -> Color(0xFF22D3EE)
                        else -> Color(0xFF1E293B)
                    }
                    val bgCol = when {
                        isSelected -> Color(0xFF22D3EE).copy(alpha = 0.1f)
                        else -> Color.Transparent
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgCol)
                            .border(1.dp, colorBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.selectQuizAnswer(idx) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                               .size(18.dp)
                                .clip(CircleShape)
                                .border(1.dp, colorBorder, CircleShape)
                                .background(if (isSelected) colorBorder else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0B0F1A))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = opt, fontSize = 12.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (quizChecked) {
                    val correct = selectedQuizAnswer == lesson.quizCorrectIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (correct) Color(0xFF10B981).copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f))
                            .border(1.dp, if (correct) Color(0xFF10B981) else Color.Red, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = if (correct) "✓ MODULE VERIFIED & SYNCHRONIZED" else "✗ RE-CHECK LOGIC SCHEMA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (correct) Color(0xFF10B981) else Color.Red,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = lesson.quizExplanation, fontSize = 12.sp, color = Color.White)
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.checkQuizAnswer() },
                        enabled = selectedQuizAnswer != null,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22D3EE)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "VERIFY RESPONSE", color = Color(0xFF0B0F1A), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- TAB 2: CERTIFICATION EXAMS SCREEN ---
@Composable
fun ExamsTabScreen(viewModel: MainViewModel) {
    val activeSession by viewModel.activeExamSession.collectAsState()
    val examRecords by viewModel.allExamRecords.collectAsState()

    if (activeSession != null) {
        ExamSimulatorScreen(viewModel, activeSession!!)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Intro Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF22D3EE).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "GLOBAL SAP CERTIFICATION MOCK EXAMS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22D3EE),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Simulate actual C_ACT (Extensibility) & C_TAW12 questions with immediate score tracking. Pass score target is 70%.",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "⚡ SELECT PRACTICE SPECIALIZATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            mockExamsList.forEach { exam ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF6366F1).copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = exam.code,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6366F1),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${exam.totalTimeMin} MINS LIMIT",
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = exam.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.startExam(exam) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "LAUNCH EXAM INJECTOR", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // History Records from SQLite DB
            Text(
                text = "⚡ PREVIOUS TRANSMISSION LOGS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981),
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (examRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No previous certification simulations recorded.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            } else {
                examRecords.forEach { rec ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = rec.examName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "${rec.examCode} • ${SimpleDateFormat("dd-MMM HH:mm", Locale.getDefault()).format(rec.timestamp)}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (rec.passed) Color(0xFF10B981).copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f))
                                .border(1.dp, if (rec.passed) Color(0xFF10B981) else Color.Red, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SCORE: ${rec.score}%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (rec.passed) Color(0xFF10B981) else Color.Red,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamSimulatorScreen(viewModel: MainViewModel, session: ExamSession) {
    val qIdx = session.currentQuestionIndex
    val currentQuestion = session.spec.questions[qIdx]
    val selectedOption = session.answers[qIdx]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // TOP HUD Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${session.spec.code} SIMULATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF22D3EE),
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "QUESTION ${qIdx + 1} OF ${session.spec.questions.size}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Simulated Progress timeline
        val progressPercent = (qIdx + 1).toFloat() / session.spec.questions.size
        LinearProgressIndicator(
            progress = progressPercent,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = Color(0xFF22D3EE),
            trackColor = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Question box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Text(
                    text = currentQuestion.query,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Structured Composable-friendly container to avoid context losses
                for (optIdx in currentQuestion.options.indices) {
                    val op = currentQuestion.options[optIdx]
                    val isChecked = selectedOption == optIdx
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isChecked) Color(0xFF22D3EE).copy(alpha = 0.1f) else Color(0xFF0F172A))
                            .border(1.dp, if (isChecked) Color(0xFF22D3EE) else Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectExamAnswer(optIdx) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(1.dp, if (isChecked) Color(0xFF22D3EE) else Color(0xFF94A3B8), CircleShape)
                                .background(if (isChecked) Color(0xFF22D3EE) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0B0F1A))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = op, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.cancelExam() },
                border = BorderStroke(1.dp, Color.Red),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "ABORT", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            if (qIdx > 0) {
                Button(
                    onClick = { viewModel.prevExamQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(text = "BACK", color = Color.White)
                }
            }

            if (qIdx < session.spec.questions.size - 1) {
                Button(
                    onClick = { viewModel.nextExamQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22D3EE)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(text = "NEXT", color = Color(0xFF0B0F1A), fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { viewModel.submitExam() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Text(text = "SUBMIT", color = Color(0xFF0B0F1A), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- TAB 3: COMMUNITY DISCUSSION SCREEN ---
@Composable
fun CommunityTabScreen(viewModel: MainViewModel) {
    val postsList by viewModel.allPosts.collectAsState()
    val activePost by viewModel.selectedPost.collectAsState()

    var showDraftForm by remember { mutableStateOf(false) }

    if (activePost != null) {
        ForumDetailsDrawer(viewModel, activePost!!)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Post trigger banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ CONCURRENT DISCUSSIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF22D3EE))
                        .clickable { showDraftForm = !showDraftForm }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (showDraftForm) "CLOSE" else "NEW POST +",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B0F1A),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post creation form
            AnimatedVisibility(visible = showDraftForm) {
                PostCreationCard(onSubmit = { t, c, tag ->
                    viewModel.addPost(t, c, tag)
                    showDraftForm = false
                })
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Posts List Feed
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(postsList) { post ->
                    ForumPostItemCard(post, onSelect = { viewModel.selectPost(post) }, onLike = { viewModel.likePost(post.id) })
                }
            }
        }
    }
}

@Composable
fun ForumPostItemCard(post: ForumPost, onSelect: () -> Unit, onLike: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val authorBadgeText = if (post.isOwner) "FOUNDER & OWNER 👑" else "MEMBER"
                val authorBadgeColor = if (post.isOwner) Color(0xFFF59E0B) else Color(0xFF22D3EE)
                
                Text(
                    text = "${post.authorName} ($authorBadgeText)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = authorBadgeColor,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = post.moduleTag,
                        fontSize = 8.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.content,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                maxLines = 3,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Like Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { onLike() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "👍 ${post.likesCount}",
                        fontSize = 10.sp,
                        color = Color(0xFF10B981),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Comments Label
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "💬 ${post.commentsCount}",
                        fontSize = 10.sp,
                        color = Color(0xFF22D3EE),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun PostCreationCard(onSubmit: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("General") }

    val tags = listOf("General", "ABAP on HANA", "S/4HANA", "SAP BTP & S/4", "Fiori UX")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "DRAFT NEW THREAD",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6366F1),
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Thread title", color = Color(0xFF94A3B8)) },
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF1E293B)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Describe the logic gap or update details...", color = Color(0xFF94A3B8)) },
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF1E293B)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tag picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.forEach { tag ->
                    val chosen = tag == selectedTag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (chosen) Color(0xFF6366F1) else Color(0xFF0F172A))
                            .border(1.dp, if (chosen) Color(0xFF6366F1) else Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .clickable { selectedTag = tag }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = if (chosen) Color.White else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onSubmit(title, content, selectedTag) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "BROADCAST TO FORUMS", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ForumDetailsDrawer(viewModel: MainViewModel, post: ForumPost) {
    val commentsList by viewModel.activeComments.collectAsState()
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { viewModel.exitPostDetail() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF22D3EE),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back to list",
                fontSize = 14.sp,
                color = Color(0xFF22D3EE),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main thread details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, Color(0xFF22D3EE).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val authorBadgeText = if (post.isOwner) "FOUNDER & OWNER 👑" else "MEMBER"
                    val authorBadgeColor = if (post.isOwner) Color(0xFFF59E0B) else Color(0xFF22D3EE)
                    
                    Text(
                        text = "${post.authorName} ($authorBadgeText)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = authorBadgeColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.content,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "⚡ DISCUSSION ENTRIES (${commentsList.size})",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10B981),
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Comments feed
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(commentsList) { comment ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        val commentBadgeColor = if (comment.isOwner) Color(0xFFF59E0B) else Color(0xFF22D3EE)
                        val commentBadgeText = if (comment.isOwner) "OWNER 👑" else "MEMBER"
                        
                        Text(
                            text = "${comment.authorName} ($commentBadgeText)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = commentBadgeColor,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = comment.content, fontSize = 12.sp, color = Color.White, lineHeight = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Comment Input Form
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Type custom response...", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF22D3EE),
                    unfocusedBorderColor = Color(0xFF1E293B)
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22D3EE))
                    .clickable {
                        viewModel.addComment(commentText)
                        commentText = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color(0xFF0B0F1A),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// --- TAB 4: DIRECT PEER CHAT / MESSAGING SCREEN ---
@Composable
fun MessagesTabScreen(viewModel: MainViewModel) {
    val selectedPeer by viewModel.selectedChatPeer.collectAsState()
    val chatHistory by viewModel.activeChatHistory.collectAsState()

    if (selectedPeer != null) {
        DirectChatWindow(viewModel, selectedPeer!!, chatHistory)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "⚡ AD-HOC PEER-TO-PEER INTERACTIVE CHANNELS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Secure collaboration. Select any active SAP peer globally to initiate direct networking.",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.globalPeers) { peer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectChat(peer) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = peer.name.take(1).uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22D3EE)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = peer.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = peer.email,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DirectChatWindow(viewModel: MainViewModel, peer: ChatContact, history: List<DirectMessage>) {
    var textMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.closeChat() }) {
                Icon(
                     imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Close",
                    tint = Color(0xFF22D3EE)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = peer.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = peer.email,
                    fontSize = 9.sp,
                    color = Color(0xFF10B981),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Bubble list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { msg ->
                val isMe = msg.senderEmail == viewModel.sessionUserEmail
                val bubbleBg = if (isMe) Color(0xFF6366F1).copy(alpha = 0.2f) else Color(0xFF0F172A)
                val borderCol = if (isMe) Color(0xFF6366F1) else Color(0xFF1E293B)
                val alignment = if (isMe) Alignment.End else Alignment.Start

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = alignment
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bubbleBg)
                            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg.message,
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 16.sp,
                            modifier = Modifier.widthIn(max = 240.dp)
                        )
                    }
                    Text(
                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(msg.timestamp),
                        fontSize = 8.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textMessage,
                onValueChange = { textMessage = it },
                placeholder = { Text("Secure message interface...", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF22D3EE),
                    unfocusedBorderColor = Color(0xFF1E293B)
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22D3EE))
                    .clickable {
                        viewModel.sendChatMessage(textMessage)
                        textMessage = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = Color(0xFF0B0F1A),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// --- TAB 5: STUDY VAULT (DOCUMENTS COLUMN) ---
@Composable
fun DocumentsTabScreen(viewModel: MainViewModel) {
    val documents by viewModel.allDocuments.collectAsState()
    val isSummaryLoading by viewModel.docSummaryLoading.collectAsState()

    var customFileName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("S/4HANA") }
    val categories = listOf("S/4HANA", "ABAP on HANA", "BTP Platform", "General SAP")

    var expandedSummaryDocId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "⚡ COLLABORATIVE DOCUMENT INTEGRATOR",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Document upload Simulator Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                OutlinedTextField(
                    value = customFileName,
                    onValueChange = { customFileName = it },
                    placeholder = { Text("E.g. SAP_BTP_Event_Mesh_Guide.pdf", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF22D3EE),
                        unfocusedBorderColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val chosen = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (chosen) Color(0xFF22D3EE) else Color(0xFF0F172A))
                                .border(1.dp, if (chosen) Color(0xFF22D3EE) else Color(0xFF1E293B), RoundedCornerShape(8.dp))
                               .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 10.sp,
                                color = if (chosen) Color(0xFF0B0F1A) else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.uploadDocument(customFileName, selectedCategory)
                        customFileName = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "UPLOAD RESOURCE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "⚡ STUDY VAULT REPOSITORY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10B981),
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(documents) { doc ->
                val isExpanded = expandedSummaryDocId == doc.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "PDF Icon",
                                tint = Color(0xFF22D3EE),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doc.fileName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${doc.category} • ${doc.fileSize}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            // Delete Doc
                            IconButton(onClick = { viewModel.removeDoc(doc.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = Color.Red.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // AI summary button
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                    .clickable {
                                        expandedSummaryDocId = if (isExpanded) null else doc.id
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isExpanded) "CLOSE PREVIEW" else "READ PREVIEW & METADATA",
                                    fontSize = 10.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF6366F1).copy(alpha = 0.15f))
                                    .clickable {
                                        viewModel.requestDocsSummary(doc)
                                        expandedSummaryDocId = doc.id
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isSummaryLoading && isExpanded) "AI GENERATING..." else "GEMINI AI SUMMARY",
                                    fontSize = 10.sp,
                                    color = Color(0xFF6366F1),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Summary display drawer
                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                HorizontalDivider(color = Color(0xFF1E293B))
                                Spacer(modifier = Modifier.height(8.dp))
                                if (isSummaryLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color(0xFF6366F1))
                                    }
                                } else {
                                    CyberMarkdownText(text = doc.summary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 6: AI NEWS CORNER & RECENT UPDATES FORECAST ---
@Composable
fun NewsTabScreen(viewModel: MainViewModel) {
    val newsText by viewModel.newsFeed.collectAsState()
    val newsQuery by viewModel.newsQuery.collectAsState()
    val isNewsLoading by viewModel.newsLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "⚡ FORECAST NEURAL CORNER (GEMINI POWERED)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Instant technological advancement forecasts & global industry updates regarding SAP.",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search options integration
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newsQuery,
                onValueChange = { viewModel.updateNewsQuery(it) },
                placeholder = { Text("E.g. Clean Core upgrades, ABAP RAP vs CAP", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF22D3EE),
                    unfocusedBorderColor = Color(0xFF1E293B)
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22D3EE))
                    .clickable { viewModel.refreshNews() },
                contentAlignment = Alignment.Center
            ) {
                if (isNewsLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF0B0F1A),
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Search / Refresh Forecast",
                        tint = Color(0xFF0B0F1A),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content forecast display section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (isNewsLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF22D3EE))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "RETRIEVING GLOBAL SAP NEWS & INDUSTRY FORECASTS...",
                            fontSize = 11.sp,
                            color = Color(0xFF22D3EE),
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                CyberMarkdownText(text = newsText)
            }
        }
    }
}

// --- Dynamic Navigation Bar ---
@Composable
fun BottomNavBar(activeTab: Tab, onTabSelected: (Tab) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF0F172A),
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color(0xFF1E293B)))
    ) {
        val items = listOf(
            TabItem(Tab.LEARN, Icons.Default.Menu, "Learn"),
            TabItem(Tab.EXAMS, Icons.Default.Star, "Exams"),
            TabItem(Tab.COMMUNITY, Icons.Default.Share, "Forum"),
            TabItem(Tab.MESSAGES, Icons.Default.Email, "Chats"),
            TabItem(Tab.DOCUMENTS, Icons.Default.Info, "Docs"),
            TabItem(Tab.NEWS, Icons.Default.Search, "AI News")
        )

        items.forEach { item ->
            val isSelected = item.tab == activeTab
            val activeColor = Color(0xFF22D3EE)
            val inactiveColor = Color(0xFF94A3B8)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) activeColor else inactiveColor,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) activeColor else inactiveColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF1E293B)
                )
            )
        }
    }
}

data class TabItem(val tab: Tab, val icon: ImageVector, val label: String)
