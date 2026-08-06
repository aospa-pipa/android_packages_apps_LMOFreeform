package com.libremobileos.sidebar.ui.all_app

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.settingslib.spa.framework.compose.rememberDrawablePainter
import com.libremobileos.sidebar.bean.AppInfo

@Composable
fun AllAppGridView(
    viewModel: AllAppViewModel,
    onClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val appList by viewModel.appListFlow.collectAsState()
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            // The virtual display is scaled down with its freeform window.  A compact
            // launcher-style grid therefore makes both icons and labels too small to use.
            // Keep cells large enough that the scaled view remains legible, while still
            // adapting to resized windows.
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(appList) { appInfo ->
                AllAppGridItem(
                    appInfo = appInfo,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun AllAppGridItem(
    appInfo: AppInfo,
    onClick: (AppInfo) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 136.dp)
            .clickable { onClick(appInfo) }
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Image(
            painter = rememberDrawablePainter(appInfo.icon),
            contentDescription = appInfo.label,
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = appInfo.label,
            maxLines = 2,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
