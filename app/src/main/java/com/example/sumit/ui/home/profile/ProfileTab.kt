package com.example.sumit.ui.home.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.common.CircularLoadingScreenWithBackdrop
import com.example.sumit.ui.common.OutlinedPasswordField

@Composable
fun ProfileTab(
    onRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val loginUiState by viewModel.loginUiState.collectAsState()
    val profileUiState by viewModel.profileUiState.collectAsState()
    val currentErrorMessage by viewModel.currentErrorMessage.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentErrorMessage) {
        if (currentErrorMessage.isNotEmpty()) {
            snackBarHostState.showSnackbar(
                message = currentErrorMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.resetErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { contentPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))
            ) {
                if (profileUiState.loggedIn) {
                    LoggedInScreen(
                        uiState = profileUiState,
                        onCurrentPasswordChange = viewModel::updateCurrentPassword,
                        onNewPasswordChange = viewModel::updateNewPassword,
                        onNewPasswordConfirmChange = viewModel::updateNewPasswordConfirm,
                        onSubmit = viewModel::changePassword,
                        onSignOut = viewModel::signOut
                    )
                } else {
                    AnonymousScreen(
                        uiState = loginUiState.form,
                        onEmailChange = viewModel::updateEmail,
                        onPasswordChange = viewModel::updatePassword,
                        onLogin = viewModel::signInWithEmailAndPassword,
                        onRegister = onRegister
                    )
                }
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularLoadingScreenWithBackdrop()
            }
        }
    }
}

@Composable
fun LoggedInScreen(
    uiState: ProfileUiState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onNewPasswordConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.large_padding))
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.your_info),
            style = MaterialTheme.typography.displayLarge
        )

        ProfileInformation(
            uiState = uiState,
            onCurrentPasswordChange = onCurrentPasswordChange,
            onNewPasswordChange = onNewPasswordChange,
            onNewPasswordConfirmChange = onNewPasswordConfirmChange,
            onSubmit = onSubmit,
            onSignOut = onSignOut
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.your_friends),
                style = MaterialTheme.typography.displayLarge
            )

            TextButton(
                onClick = { }
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }

                Text(stringResource(R.string.add_a_friend))
            }
        }

        FriendsList()
    }
}

@Composable
fun ProfileInformation(
    uiState: ProfileUiState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onNewPasswordConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordChangeVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
    ) {
        Text(
            text = stringResource(R.string.username)
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )

        Text(
            text = stringResource(R.string.name)
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )

        Text(
            text = stringResource(R.string.email_address)
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )

        Button(
            onClick = { passwordChangeVisible = !passwordChangeVisible },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(dimensionResource(R.dimen.form_button_width)),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.change_password))
        }

        AnimatedVisibility(
            visible = passwordChangeVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
            ) {
                Text(
                    text = stringResource(R.string.current_password)
                )

                OutlinedPasswordField(
                    password = uiState.form.currentPassword,
                    onPasswordChange = onCurrentPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.new_password)
                )

                OutlinedPasswordField(
                    password = uiState.form.newPassword,
                    onPasswordChange = onNewPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.confirm_password)
                )

                OutlinedPasswordField(
                    password = uiState.form.newPasswordConfirm,
                    onPasswordChange = onNewPasswordConfirmChange,
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    onClick = {
                        onSubmit()
                        passwordChangeVisible = false
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(dimensionResource(R.dimen.form_button_width)),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }

        Button(
            onClick = onSignOut,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(dimensionResource(R.dimen.form_button_width)),
            colors = ButtonDefaults.filledTonalButtonColors(),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.sign_out))
        }
    }
}

@Composable
fun FriendsList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = dimensionResource(R.dimen.large_padding))
    ) {
        Text(
            text = stringResource(R.string.no_friends),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.medium_padding)),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.click_button_to_add_friend),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun AnonymousScreen(
    uiState: LoginFormData,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.sign_in),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )

        LoginForm(
            uiState = uiState,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onSubmit = onLogin
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.no_account_question),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.small_padding)),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onRegister,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(dimensionResource(R.dimen.form_button_width))
                .padding(bottom = dimensionResource(R.dimen.small_padding)),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.create_an_account))
        }

        Button(
            onClick = { },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(dimensionResource(R.dimen.form_button_width)),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.sign_in_with_google))
        }
    }
}

@Composable
fun LoginForm(
    uiState: LoginFormData,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.large_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
    ) {
        Text(
            text = stringResource(R.string.email_address)
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Text(
            text = stringResource(R.string.password)
        )

        OutlinedPasswordField(
            password = uiState.password,
            onPasswordChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(dimensionResource(R.dimen.form_button_width)),
            shape = MaterialTheme.shapes.small
        ) {
            Text(stringResource(R.string.sign_in))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoggedInPreview() {
    val mockUiState = ProfileUiState(
        loggedIn = true,
        email = "joe@example.com",
        name = "John Doe",
        username = "johnny"
    )

    LoggedInScreen(
        uiState = mockUiState,
        onCurrentPasswordChange = { },
        onNewPasswordChange = { },
        onNewPasswordConfirmChange = { },
        onSubmit = { },
        onSignOut = { }
    )
}

@Preview(showBackground = true)
@Composable
private fun AnonymousPreview() {
    val mockUiState = LoginFormData(
        email = "valaki@example.com",
        password = "valami123"
    )

    AnonymousScreen(
        uiState = mockUiState,
        onEmailChange = { },
        onPasswordChange = { },
        onLogin = { },
        onRegister = { }
    )
}
