package com.example.happybirthday

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.text.style.TextAlign
import java.util.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth

// Data class for updates
data class JeeUpdate(
    val title: String,
    val date: String,
    val link: String,
    val content: String,
    val isArchived: Boolean = false,
    val isImportant: Boolean = false
)

// ViewModel to handle data operations
class JeeUpdatesViewModel(private val context: Context) : ViewModel() {
    private val _updates = MutableStateFlow<List<JeeUpdate>>(emptyList())
    val updates: StateFlow<List<JeeUpdate>> = _updates

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _hasNewUpdates = MutableStateFlow(false)
    val hasNewUpdates: StateFlow<Boolean> = _hasNewUpdates

    private val _lastCheckedTime = MutableStateFlow<Long>(0)
    val lastCheckedTime: StateFlow<Long> = _lastCheckedTime

    init {
        fetchUpdates()
    }

    fun fetchUpdates() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val updates = listOf(
                    // Current & Important Updates (2025) with direct PDF links
                    JeeUpdate(
                        title = "Change in Examination Venue - Ayodhya Center",
                        date = "January 2025",
                        link = "https://cdnbbsr.s3waas.gov.in/s3f8e59f4b2fe7c5705bf878bbd494ccdf/uploads/2025/01/2025012470.pdf",
                        content = """
                            • Change in venue for candidates scheduled at:
                              Institute for Advanced Computer Technology
                              21/03/44/03, behind Choti Devkali Mandir
                              Tulsi Nagar Ayodhya, Faizabad 224723
                            • Affects dates: 28, 29 and 30 January 2025
                        """.trimIndent(),
                        isArchived = false,
                        isImportant = true
                    ),
                    JeeUpdate(
                        title = "Admit Cards Release - Session 1 (January 2025)",
                        date = "January 2025",
                        link = "https://cdnbbsr.s3waas.gov.in/s3f8e59f4b2fe7c5705bf878bbd494ccdf/uploads/2025/01/2025012360.pdf",
                        content = """
                            • Admit Cards available for:
                              - 28, 29 and 30 January 2025 examinations
                            • Download from Candidate Activity section
                            • Check examination center details carefully
                        """.trimIndent(),
                        isArchived = false,
                        isImportant = true
                    ),
                    JeeUpdate(
                        title = "Bengaluru Center Rescheduling Notice",
                        date = "January 2025",
                        link = "https://cdnbbsr.s3waas.gov.in/s3f8e59f4b2fe7c5705bf878bbd494ccdf/uploads/2025/01/2025012220.pdf",
                        content = """
                            • Rescheduling for candidates who appeared at:
                              eTalent, (TC Code-40086)
                              No.3, Belmar Estate, Nagasandra Main Rd
                              Bengaluru, Karnataka
                            • Affects: 22 January 2025 (Shift-I)
                        """.trimIndent(),
                        isArchived = false,
                        isImportant = true
                    ),
                    JeeUpdate(
                        title = "Sharjah UAE Center Change Notice",
                        date = "January 2025",
                        link = "https://cdnbbsr.s3waas.gov.in/s3f8e59f4b2fe7c5705bf878bbd494ccdf/uploads/2025/01/2025012063.pdf",
                        content = """
                            • Important notice regarding examination centre change
                            • Location: Sharjah, UAE
                            • Session-I candidates to check new center details
                        """.trimIndent(),
                        isArchived = false,
                        isImportant = true
                    ),
                    JeeUpdate(
                        title = "Image Upload Discrepancy Notice",
                        date = "January 2025",
                        link = "https://cdnbbsr.s3waas.gov.in/s3f8e59f4b2fe7c5705bf878bbd494ccdf/uploads/2025/01/2025011839.pdf",
                        content = """
                            • Notice for candidates with image upload issues
                            • Session-I application discrepancies
                            • Follow instructions for correction
                        """.trimIndent(),
                        isArchived = false,
                        isImportant = false
                    ),

                    // Updated Previous Updates (Archive from jeemain.nta.ac.in)
                    JeeUpdate(
                        title = "JEE(Main) 2024 Session 2 Final Results",
                        date = "April 13, 2024",
                        link = "https://jeemain.nta.ac.in/",
                        content = """
                            • NTA Score for Paper 2B (B.Planning)
                            • NTA Score for Paper 2A (B.Arch)
                            • Results declared for Session 2
                            • Final Answer Keys Published
                        """.trimIndent(),
                        isArchived = true,
                        isImportant = false
                    ),
                    JeeUpdate(
                        title = "JEE(Main) 2024 Session 2 Results",
                        date = "April 8, 2024",
                        link = "https://jeemain.nta.ac.in/",
                        content = """
                            • NTA Score for Paper 1 (B.E./B.Tech.)
                            • Results declared for Session 2
                            • Download Score Card
                            • Check Final Merit List
                        """.trimIndent(),
                        isArchived = true,
                        isImportant = false
                    ),
                    JeeUpdate(
                        title = "JEE(Main) 2024 Session 1 Results",
                        date = "February 12, 2024",
                        link = "https://jeemain.nta.ac.in/",
                        content = """
                            • NTA Score for Paper 1 (B.E./B.Tech.)
                            • NTA Score for Paper 2A (B.Arch)
                            • NTA Score for Paper 2B (B.Planning)
                            • Final Answer Keys Available
                        """.trimIndent(),
                        isArchived = true,
                        isImportant = false
                    ),
                    JeeUpdate(
                        title = "JEE(Main) 2024 Session 1 Examination",
                        date = "January-February 2024",
                        link = "https://jeemain.nta.ac.in/",
                        content = """
                            • Examination conducted from Jan 27 to Feb 1, 2024
                            • Paper 1 (B.E./B.Tech)
                            • Paper 2A (B.Arch)
                            • Paper 2B (B.Planning)
                        """.trimIndent(),
                        isArchived = true,
                        isImportant = false
                    ),
                    JeeUpdate(
                        title = "JEE(Main) 2024 Registration Process",
                        date = "November-December 2023",
                        link = "https://jeemain.nta.ac.in/",
                        content = """
                            • Online Registration
                            • Application Form Submission
                            • Fee Payment
                            • Document Upload Guidelines
                        """.trimIndent(),
                        isArchived = true,
                        isImportant = false
                    ),
                    JeeUpdate(
                        title = "JEE(Main) 2024 Information Bulletin",
                        date = "November 1, 2023",
                        link = "https://jeemain.nta.ac.in/",
                        content = """
                            • Complete Information about JEE(Main) 2024
                            • Eligibility Criteria
                            • Examination Pattern
                            • Important Dates
                        """.trimIndent(),
                        isArchived = true,
                        isImportant = false
                    )
                )
                
                _updates.value = updates
                
            } catch (e: Exception) {
                _error.value = "Failed to load updates: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkForNewUpdates(context: Context) {
        viewModelScope.launch {
            try {
                val doc = withContext(Dispatchers.IO) {
                    Jsoup.connect("https://jeemain.nta.nic.in/")
                        .timeout(10000)
                        .get()
                }
                
                val latestNotices = doc.select("div.public-notices li a, .latest-news li a").map { it.text() }
                _hasNewUpdates.value = latestNotices.isNotEmpty()
                _lastCheckedTime.value = System.currentTimeMillis()
                
                if (_hasNewUpdates.value) {
                    val notificationService = NotificationService()
                    notificationService.showUpdateNotification(context)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "No new updates available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateCheck", "Error checking updates", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error checking updates: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun openWebsite(intent: Intent) {
        viewModelScope.launch {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("Website", "Error opening website", e)
            }
        }
    }
}

// Add a ViewModel factory
class JeeUpdatesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JeeUpdatesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JeeUpdatesViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JeeMainUpdatesApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JeeMainUpdatesApp() {
    val customColorScheme = darkColorScheme(
        primary = Color(0xFFFF5722),          
        onPrimary = Color.White,              
        primaryContainer = Color(0xFFD84315),  
        onPrimaryContainer = Color.White,      
        secondary = Color(0xFFBF360C),         
        onSecondary = Color.White,            
        background = Color.Black,             
        surface = Color(0xFF932B0A),          
        onSurface = Color.White,              
        surfaceVariant = Color(0xFFE64A19),   
        onSurfaceVariant = Color.White        
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("Home") }
    val context = LocalContext.current
    val viewModel: JeeUpdatesViewModel = viewModel(
        factory = JeeUpdatesViewModelFactory(context)
    )

    MaterialTheme(
        colorScheme = customColorScheme
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = currentScreen == "Home",
                        onClick = {
                            currentScreen = "Home"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("Important Updates") },
                        selected = currentScreen == "Important Updates",
                        onClick = {
                            currentScreen = "Important Updates"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("Previous Updates") },
                        selected = currentScreen == "Previous Updates",
                        onClick = {
                            currentScreen = "Previous Updates"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("About") },
                        selected = currentScreen == "About",
                        onClick = {
                            currentScreen = "About"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(currentScreen) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { 
                                viewModel.checkForNewUpdates(context)
                            }) {
                                Text(
                                    text = "⟳",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (viewModel.hasNewUpdates.collectAsState().value) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = customColorScheme.primaryContainer,
                            titleContentColor = customColorScheme.onSurface
                        )
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    when (currentScreen) {
                        "Home" -> HomeScreen(viewModel)
                        "Important Updates" -> ImportantUpdatesScreen(viewModel)
                        "Previous Updates" -> PreviousUpdatesScreen(viewModel)
                        "About" -> AboutScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun PreviousUpdatesScreen(viewModel: JeeUpdatesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Previous Updates",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Latest Results",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "JEE(Main) 2024 Session-2 Score Card is now available",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jeemain.nta.ac.in/"))
                            viewModel.openWebsite(intent)
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "About JEE Main Updates",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app provides updates about the JEE Main exam. Stay informed about changes, admit cards, and more.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UpdatesList(
    updates: List<JeeUpdate>,
    isLoading: Boolean,
    error: String?,
    colors: ColorScheme
) {
    when {
        isLoading -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(color = colors.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading updates...", color = colors.onBackground)
            }
        }
        error != null -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = error,
                    color = colors.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
        updates.isEmpty() -> {
            Text(
                text = "No updates available",
                modifier = Modifier.padding(16.dp),
                color = colors.onBackground
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(updates) { update ->
                    UpdateCard(update, colors)
                }
            }
        }
    }
}

@Composable
fun UpdateCard(update: JeeUpdate, colors: ColorScheme) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (update.isArchived) 
                colors.surfaceVariant 
            else 
                colors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(update.link))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("UpdateCard", "Error opening link: ${e.message}")
                    }
                }
        ) {
            Text(
                text = update.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    textDecoration = TextDecoration.Underline
                ),
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = update.date,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = update.content,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface
            )
        }
    }
}

@Composable
fun HomeScreen(viewModel: JeeUpdatesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to JEE Main Updates",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Stay updated with the latest JEE Main notifications",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Quick Links",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jeemain.nta.nic.in/"))
                        viewModel.openWebsite(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("Official JEE Main Website")
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jeemain.nta.ac.in/"))
                        viewModel.openWebsite(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("JEE Main Archive Portal")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Click the refresh button ⟳ in the top bar to check for new updates",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ImportantUpdatesScreen(viewModel: JeeUpdatesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Important JEE Main 2025 Updates",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Latest Announcements",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn {
                    items(importantUpdates) { update ->
                        UpdateItemWithLink(update, viewModel)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Quick Links",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jeemain.nta.nic.in/"))
                        viewModel.openWebsite(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("Official JEE Main Website")
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, 
                            Uri.parse("https://jeemain.nta.nic.in/download-admit-card/"))
                        viewModel.openWebsite(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("Download Admit Card")
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, 
                            Uri.parse("https://jeemain.nta.nic.in/download-advance-city-intimation/"))
                        viewModel.openWebsite(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("Check Exam City")
                }
            }
        }
    }
}

@Composable
private fun UpdateItemWithLink(update: Update, viewModel: JeeUpdatesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                update.link?.let { link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    viewModel.openWebsite(intent)
                }
            }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = update.title,
            style = MaterialTheme.typography.titleMedium.copy(color = Color.Black),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = update.date,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        if (update.description.isNotEmpty()) {
            Text(
                text = update.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

data class Update(
    val title: String,
    val date: String,
    val description: String = "",
    val link: String? = null
)

val importantUpdates = listOf(
    Update(
        "Venue Change - Ayodhya Center (28-30 January)",
        "January 25, 2025",
        "Change in venue of examination for candidates scheduled at Institute for Advanced Computer Technology, Tulsi Nagar Ayodhya, Faizabad 224723 for JEE (Main) 2025 Session-1",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Admit Cards Release (28-30 January)",
        "January 25, 2025",
        "Release of Admit Cards for candidates scheduled to appear on 28, 29 and 30 January 2025",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Rescheduling Notice - Bengaluru Center",
        "January 22, 2025",
        "Rescheduling of exams for candidates who appeared at eTalent (TC Code-40086), Nagasandra Main Rd, Bengaluru, held on 22 January 2025 (Shift-I)",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Examination Centre Change - Sharjah",
        "January 2025",
        "Intimation regarding change of Examination Centre for JEE Main 2025 Session-I at Sharjah, UAE",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Admit Cards Release (22-24 January)",
        "January 2025",
        "Release of Admit Cards for candidates scheduled to appear on 22, 23 and 24 January 2025",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Image Upload Discrepancy Notice",
        "January 2025",
        "Notice regarding discrepancy in Images uploaded by candidates for JEE Main 2025 Session-I",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Advance City Intimation",
        "December 2024",
        "Advance Intimation for Allotment of Examination City to the Applicants of JEE Main 2025 Session 1",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Examination Schedule - Session 1",
        "December 2024",
        "Official schedule released for JEE Main 2025 Session-1 examinations",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Application Correction Notice",
        "November 2024",
        "Intimation of correction in particulars of the Online Application Form of JEE Main 2025",
        "https://jeemain.nta.nic.in/"
    ),
    Update(
        "Aadhaar Advisory",
        "November 2024",
        "Advisory and Instructions on Aadhaar Card Name Mismatch while Filling of Online Applications Form",
        "https://jeemain.nta.nic.in/"
    )
)

@Preview(showBackground = true)
@Composable
fun BirthdayCardPreview() {
    JeeMainUpdatesApp()
}