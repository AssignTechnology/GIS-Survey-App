package gis.data.survey.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    useMediumTopAppBar: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    ),
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector = Icons.Filled.ArrowBackIosNew,
    onNavigationIconClick: () -> Unit = {},
//    actionIcon: ImageVector = Icons.Filled.Logout,
//    onActionIconClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit = { _ -> },
    rememberNavController: NavHostController
) {
    var context = LocalContext.current
    Scaffold(
        modifier = modifier.let {
            if (scrollBehavior != null) {
                it.nestedScroll(scrollBehavior.nestedScrollConnection)
            } else {
                it
            }
        },
        topBar = {
            if (useMediumTopAppBar) {
                MediumTopAppBar(
                    title = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    modifier = Modifier,
                    navigationIcon = {
                        IconButton(onClick = onNavigationIconClick) {
                            Icon(
                                imageVector = navigationIcon,
                                contentDescription = "navigationIcon"
                            )
                        }
                    },
//                    actions = {
//                        IconButton(onClick = {
//                            showDialog = true
//                        }) {
//                            Icon(
//                                imageVector =  Icons.Filled.MoreVert,
//                                contentDescription = "actionIcon"
//                            )
//                        }
//                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    scrollBehavior = scrollBehavior
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    modifier = Modifier,
                    navigationIcon = {
                        IconButton(onClick = onNavigationIconClick) {
                            Icon(
                                imageVector = navigationIcon,
                                contentDescription = "navigationIcon"
                            )
                        }
                    },
//                    actions = {
//                        IconButton(onClick = {
//                            showDialog = true
//                        }) {
//                            Icon(
//                                imageVector = actionIcon,
//                                contentDescription = "actionIcon"
//                            )
//                        }
//                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    scrollBehavior = scrollBehavior
                )
            }
        },
        content = content
    )

}

