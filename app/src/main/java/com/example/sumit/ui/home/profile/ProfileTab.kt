package com.example.sumit.ui.home.profile

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.sumit.ui.common.OutlinedPasswordField

@Composable
fun ProfileTab(
    onRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val loginUiState by viewModel.loginUiState.collectAsState()
    val loggedIn = false

    Column(modifier = modifier.padding(dimensionResource(R.dimen.medium_padding))) {
        if (loggedIn) {
            LoggedInScreen()
        } else {
            AnonymousScreen(
                uiState = loginUiState,
                onEmailChange = viewModel::updateEmail,
                onPasswordChange = viewModel::updatePassword,
                onLogin = viewModel::signInWithEmailAndPassword,
                onRegister = onRegister
            )
        }
    }
}

@Composable
fun LoggedInScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ProfileInformation()

        FriendsList()
    }
}

@Composable
fun ProfileInformation(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
    ) {
        Text(
            text = "Username"
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            enabled = false,
            singleLine = true
        )

        Text(
            text = "Name"
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            enabled = false,
            singleLine = true
        )

        Text(
            text = "Email address"
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            enabled = false,
            singleLine = true
        )

        Button(
            onClick = { },
            shape = MaterialTheme.shapes.small
        ) {
            Text("Change password")
        }

        Text(
            text = "Current password"
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            enabled = false,
            singleLine = true
        )

        Text(
            text = "New password"
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            enabled = false,
            singleLine = true
        )

        Text(
            text = "Confirm password"
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            enabled = false,
            singleLine = true
        )

        Button(
            onClick = { },
            shape = MaterialTheme.shapes.small
        ) {
            Text("Confirm")
        }
    }
}

@Composable
fun FriendsList(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Your friends"
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

                Text("Add a friend")
            }
        }

        Text(
            text = "Your don't have any friends",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Click the button to add your first friend to start sharing notes",
            style = MaterialTheme.typography.titleMedium
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
    LoggedInScreen()
}

@Preview(showBackground = true)
@Composable
private fun AnonymousPreview() {
    AnonymousScreen(
        uiState = LoginFormData(
            email = "valaki@example.com",
            password = "valami123"
        ),
        onEmailChange = { },
        onPasswordChange = { },
        onLogin = { },
        onRegister = { }
    )
}
