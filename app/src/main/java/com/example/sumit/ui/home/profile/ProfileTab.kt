package com.example.sumit.ui.home.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumit.R

@Composable
fun ProfileTab(modifier: Modifier = Modifier) {
    val loggedIn = false

    Column(modifier = modifier.padding(dimensionResource(R.dimen.medium_padding))) {
        if (loggedIn) {
            LoggedInScreen()
        } else {
            AnonymousScreen()
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
fun AnonymousScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign in",
            style = MaterialTheme.typography.displayLarge
        )

        LoginForm()

        Text(
            text = "Don't have an account?"
        )

        Button(
            onClick = { },
            shape = MaterialTheme.shapes.small
        ) {
            Text("Create an account")
        }

        Button(
            onClick = { },
            shape = MaterialTheme.shapes.small
        ) {
            Text("Sign in with Google")
        }
    }
}

@Composable
fun LoginForm(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Email address"
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            singleLine = true
        )

        Text(
            text = "Password"
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = Icons.Filled.VisibilityOff
                val description = "Show password"

                IconButton(onClick = { }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )

        Button(
            onClick = { },
            shape = MaterialTheme.shapes.small
        ) {
            Text("Sign in")
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
    AnonymousScreen()
}
