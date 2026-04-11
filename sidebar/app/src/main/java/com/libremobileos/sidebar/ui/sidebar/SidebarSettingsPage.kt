package com.libremobileos.sidebar.ui.sidebar

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.android.settingslib.spa.framework.compose.localNavController
import com.android.settingslib.spa.framework.compose.rememberDrawablePainter
import com.android.settingslib.spa.framework.theme.SettingsDimension
import com.android.settingslib.spa.widget.preference.MainSwitchPreference
import com.android.settingslib.spa.widget.preference.SwitchPreference
import com.android.settingslib.spa.widget.preference.SwitchPreferenceModel
import com.android.settingslib.spa.widget.scaffold.SettingsScaffold
import com.android.settingslib.spa.widget.ui.Category
import com.libremobileos.sidebar.R
import com.libremobileos.sidebar.bean.SidebarAppInfo

@Composable
fun SidebarSettingsPage(viewModel: SidebarSettingsViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val mainChecked = remember { mutableStateOf(viewModel.getSidebarEnabled()) }

    androidx.compose.runtime.CompositionLocalProvider(navController.localNavController()) {
        SettingsScaffold(
            title = stringResource(R.string.sidebar_label),
            actions = {
                IconButton(onClick = {
                    context.startActivity(Intent(context, SidebarCustomizationActivity::class.java))
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Sidebar customization",
                    )
                }
            },
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                MainSwitchPreference(object : SwitchPreferenceModel {
                    override val title = stringResource(R.string.enable_sideline)
                    override val checked = { mainChecked.value }
                    override val changeable = { viewModel.isEnabled }
                    override val onCheckedChange: (Boolean) -> Unit = {
                        mainChecked.value = it
                        viewModel.setSidebarEnabled(it)
                    }
                })
                if (mainChecked.value) {
                    SidebarSettingSwitch(
                        title = stringResource(R.string.sidebar_predicted_apps),
                        summary = stringResource(R.string.sidebar_predicted_apps_summary),
                        isChecked = viewModel.getPredictedAppsEnabled(),
                        onCheckedChange = viewModel::setPredictedAppsEnabled,
                    )
                    SidebarAppList(viewModel)
                }
            }
        }
    }
}

@Composable
fun SidebarAppList(viewModel: SidebarSettingsViewModel) {
    val sidebarApps by viewModel.appListFlow.collectAsState()
    Category(title = stringResource(R.string.sidebar_app_setting_label)) {
        LazyColumn {
            items(sidebarApps) { appInfo ->
                SidebarAppListItem(appInfo) { isChecked ->
                    if (isChecked) viewModel.addSidebarApp(appInfo)
                    else viewModel.deleteSidebarApp(appInfo)
                }
            }
        }
    }
}

@Composable
fun SidebarAppListItem(appInfo: SidebarAppInfo, onCheckedChange: (Boolean) -> Unit) {
    val appChecked = remember { mutableStateOf(appInfo.isSidebarApp) }
    SwitchPreference(model = object : SwitchPreferenceModel {
        override val title = appInfo.label
        override val icon = @Composable {
            Image(
                painter = rememberDrawablePainter(appInfo.icon),
                contentDescription = appInfo.label,
                modifier = Modifier.size(SettingsDimension.appIconItemSize),
            )
        }
        override val checked = { appChecked.value }
        override val onCheckedChange: (Boolean) -> Unit = {
            appChecked.value = it
            onCheckedChange(it)
        }
    })
}

@Composable
fun SidebarSettingSwitch(
    title: String,
    summary: String?,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val checked = remember { mutableStateOf(isChecked) }
    SwitchPreference(model = object : SwitchPreferenceModel {
        override val title = title
        override val summary = { summary.orEmpty() }
        override val checked = { checked.value }
        override val onCheckedChange: (Boolean) -> Unit = {
            checked.value = it
            onCheckedChange(it)
        }
    })
}
