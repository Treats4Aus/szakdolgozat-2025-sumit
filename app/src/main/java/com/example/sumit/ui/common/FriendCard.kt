package com.example.sumit.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun FriendCard(
    friendData: FriendData,
    onRemoveFriend: () -> Unit,
    onBlockFriend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val friendsSinceDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        .format(Date(friendData.friendshipData.requestTime))

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = dimensionResource(R.dimen.card_elevation)
            )
        ) {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = friendData.userData.username,
                        style = MaterialTheme.typography.displayMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.manage_friend)
                        )
                    }
                }

                Text(
                    text = friendData.userData.email,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.small_padding)),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(R.string.friends_since, friendsSinceDate),
                    fontSize = 14.sp
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.remove_friend)) },
                onClick = onRemoveFriend
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.block_friend)) },
                onClick = onBlockFriend
            )
        }
    }
}

@Preview
@Composable
private fun FriendCardPreview() {
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

    FriendCard(
        friendData = previewFriend,
        onRemoveFriend = { },
        onBlockFriend = { }
    )
}
