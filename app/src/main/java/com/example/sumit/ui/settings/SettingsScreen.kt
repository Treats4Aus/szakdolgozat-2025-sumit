package com.example.sumit.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings
}

enum class LangOption(@StringRes val displayNameRes: Int) {
    HU(R.string.hungarian),
    EN(R.string.english)
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val syncEnabled by viewModel.syncEnabled.collectAsState()
    val langCode by viewModel.langCode.collectAsState()
    val langDisplayRes = LangOption.valueOf(langCode)

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.medium_padding)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
        ) {
            Card(
                Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = dimensionResource(R.dimen.card_elevation)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.medium_padding))
                ) {
                    Text(
                        text = "Sync notes",
                        style = MaterialTheme.typography.displayMedium
                    )

                    Switch(
                        checked = syncEnabled,
                        onCheckedChange = viewModel::updateSyncPreference
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = dimensionResource(R.dimen.card_elevation)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.medium_padding))
                ) {
                    Text(
                        text = "Language",
                        style = MaterialTheme.typography.displayMedium
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        Row(
                            modifier = Modifier.width(176.dp)
                        ) {
                            OutlinedTextField(
                                value = stringResource(langDisplayRes.displayNameRes),
                                onValueChange = { },
                                modifier = Modifier
                                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                }
                            )
                        }

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            LangOption.entries.forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(lang.displayNameRes))
                                    },
                                    onClick = {
                                        viewModel.updateLangPreference(lang.name)
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
