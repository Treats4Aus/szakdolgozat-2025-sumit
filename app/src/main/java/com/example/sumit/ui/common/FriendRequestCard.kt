package com.example.sumit.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumit.R
import com.example.sumit.data.users.FriendData
import com.example.sumit.data.users.FriendshipData
import com.example.sumit.data.users.FriendshipStatus
import com.example.sumit.data.users.UserData
import com.example.sumit.utils.DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A card to respond to a pending friend request.
 * @param friendData The user data of the user making the request
 * @param onAccept Called when accepting the friend request
 * @param onReject Called when rejecting the friend request
 * @param onBlock Called when blocking the user sending the request
 * @param modifier The [Modifier] to be applied to this card
 */
@Composable
fun FriendRequestCard(
    friendData: FriendData,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requestDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        .format(Date(friendData.friendshipData.requestTime))

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        )
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))) {
            Text(
                text = friendData.userData.username,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = friendData.userData.email,
                modifier = Modifier
                    .padding(bottom = dimensionResource(R.dimen.small_padding)),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(R.string.request_sent_on, requestDate),
                modifier = Modifier
                    .padding(bottom = dimensionResource(R.dimen.small_padding)),
                fontSize = 14.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding))
            ) {
                ActionButton(
                    icon = Icons.Default.Check,
                    text = stringResource(R.string.accept),
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                )

                ActionButton(
                    icon = Icons.Default.Close,
                    text = stringResource(R.string.reject),
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors()
                )

                ActionButton(
                    icon = Icons.Default.Block,
                    text = stringResource(R.string.block),
                    onClick = onBlock,
                    modifier = Modifier.weight(1f),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.small_padding))
            )

            Text(text)
        }
    }
}

@Preview
@Composable
private fun FriendRequestCardPreview() {
    val previewFriend = FriendData(
        friendshipData = FriendshipData(
            id = "id",
            requesterId = "user1",
            responderId = "user2",
            requestTime = Date().time,
            status = FriendshipStatus.Accepted.toString()
        ),
        userData = UserData(
            email = "friend@example.com",
            username = "Friend"
        )
    )

    FriendRequestCard(
        friendData = previewFriend,
        onAccept = { },
        onReject = { },
        onBlock = { }
    )
}
