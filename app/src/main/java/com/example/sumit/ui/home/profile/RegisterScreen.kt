package com.example.sumit.ui.home.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sumit.R
import com.example.sumit.ui.SumItAppBar
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
    viewModel: RegistrationViewModel = viewModel()
) {
    val registrationUiState by viewModel.registrationUiState.collectAsState()

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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.medium_padding))
        ) {
            Text(
                text = "Sign up to SumIt",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )

            RegistrationForm(
                uiState = registrationUiState,
                onEmailChange = viewModel::updateEmail,
                onNameChange = viewModel::updateName,
                onUserNameChange = viewModel::updateUsername,
                onPasswordChange = viewModel::updatePassword,
                onPasswordConfirmChange = viewModel::updatePasswordConfirm
            )
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
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.email_address)
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            singleLine = true
        )

        Text(
            text = stringResource(R.string.name)
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            singleLine = true
        )

        Text(
            text = stringResource(R.string.username)
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUserNameChange,
            singleLine = true
        )

        Text(
            text = stringResource(R.string.password)
        )

        OutlinedPasswordField(
            password = uiState.password,
            onPasswordChange = onPasswordChange,
            singleLine = true
        )

        Text(
            text = stringResource(R.string.password_confirm)
        )

        OutlinedPasswordField(
            password = uiState.passwordConfirm,
            onPasswordChange = onPasswordConfirmChange,
            singleLine = true
        )
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
        onPasswordConfirmChange = { }
    )
}
