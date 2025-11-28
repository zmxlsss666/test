package com.tomatoreader.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.tomatoreader.wear.presentation.screens.BookshelfScreen
import com.tomatoreader.wear.presentation.screens.ReaderScreen
import com.tomatoreader.wear.presentation.screens.BookmarkScreen
import com.tomatoreader.wear.presentation.screens.SearchScreen
import com.tomatoreader.wear.presentation.screens.OfflineScreen
import com.tomatoreader.wear.presentation.theme.TomatoReaderWearTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    TomatoReaderWearTheme {
        val navController = rememberSwipeDismissableNavController()
        
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "bookshelf"
        ) {
            composable("bookshelf") {
                BookshelfScreen(
                    onBookClick = { bookId ->
                        navController.navigate("reader/$bookId")
                    },
                    onBookmarkClick = {
                        navController.navigate("bookmarks")
                    },
                    onOfflineClick = {
                        navController.navigate("offline")
                    },
                    onSearchClick = {
                        navController.navigate("search")
                    }
                )
            }
            
            composable("reader/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                ReaderScreen(
                    bookId = bookId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onBookmarksClick = {
                        navController.navigate("bookmarks")
                    }
                )
            }
            
            composable("bookmarks") {
                BookmarkScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onBookmarkClick = { bookId, chapterId, position ->
                        navController.navigate("reader/$bookId?chapterId=$chapterId&position=$position")
                    }
                )
            }
            
            composable("search") {
                SearchScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onBookClick = { bookId ->
                        navController.navigate("reader/$bookId")
                    }
                )
            }
            
            composable("offline") {
                OfflineScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onBookClick = { bookId ->
                        navController.navigate("reader/$bookId")
                    }
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}