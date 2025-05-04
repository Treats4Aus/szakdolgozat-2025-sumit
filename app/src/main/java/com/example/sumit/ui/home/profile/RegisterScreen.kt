package com.example.sumit.ui.home.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.ui.AppViewModelProvider
import com.example.sumit.ui.SumItAppBar
import com.example.sumit.ui.common.CircularLoadingScreenWithBackdrop
import com.example.sumit.ui.common.OutlinedPasswordField
import com.example.sumit.ui.navigation.NavigationDestination

object RegisterDestination : NavigationDestination {
    override val route = "register"
    override val titleRes = R.string.create_an_account
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val registrationUiState by viewModel.registrationUiState.collectAsState()
    val currentMessage by viewModel.currentMessageRes.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(registrationUiState.state) {
        if (registrationUiState.state == RegistrationState.Finished) {
            onBack()
        }
    }

    LaunchedEffect(currentMessage) {
        if (currentMessage.isNotEmpty()) {
            snackBarHostState.showSnackbar(
                message = currentMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.resetMessage()
        }
    }

    Scaffold(
        topBar = {
            SumItAppBar(
                title = stringResource(RegisterDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.medium_padding))
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.sign_up_to_sumit),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center
                )

                RegistrationForm(
                    uiState = registrationUiState.form,
                    onEmailChange = viewModel::updateEmail,
                    onNameChange = viewModel::updateName,
                    onUserNameChange = viewModel::updateUsername,
                    onPasswordChange = viewModel::updatePassword,
                    onPasswordConfirmChange = viewModel::updatePasswordConfirm,
                    onSubmit = viewModel::registerWithEmailAndPassword
                )
            }

            AnimatedVisibility(
                visible = registrationUiState.state == RegistrationState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularLoadingScreenWithBackdrop()
            }

            SnackbarHost(hostState = snackBarHostState)
        }
    }
}

@Composable
fun RegistrationForm(
    uiState: RegistrationFormData,
    onEmailChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
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
            text = stringResource(R.string.name)
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = stringResource(R.string.username)
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUserNameChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
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

        Text(
            text = stringResource(R.string.password_confirm)
        )

        OutlinedPasswordField(
            password = uiState.passwordConfirm,
            onPasswordChange = onPasswordConfirmChange,
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
            Text(stringResource(R.string.create_account))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterFormPreview() {
    val mockUiState = RegistrationFormData(
        email = "valaki@example.com",
        name = "Teszt Elek",
        username = "teszt",
        password = "password123",
        passwordConfirm = "password123"
    )

    RegistrationForm(
        uiState = mockUiState,
        onEmailChange = { },
        onNameChange = { },
        onUserNameChange = { },
        onPasswordChange = { },
        onPasswordConfirmChange = { },
        onSubmit = { }
    )
}
