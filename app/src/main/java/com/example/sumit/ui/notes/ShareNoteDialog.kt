package com.example.sumit.ui.notes

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.example.sumit.R
import com.example.sumit.data.users.FriendData

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ShareNoteDialog(
    friendList: List<FriendData>,
    onShareWithFriends: (List<String>) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedFriendIds: HashSet<String> by remember { mutableStateOf(hashSetOf()) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier
                .width(300.dp)
                .height(400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.large_padding)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.select_friends_to_share),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center
                )

                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 180.dp)
                        .padding(vertical = dimensionResource(R.dimen.medium_padding))
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.White)
                ) {
                    itemsIndexed(friendList, { _, data -> data.friendshipData.id }) { index, data ->
                        ShareOption(
                            friendData = data,
                            onClick = { selected ->
                                if (selected) {
                                    selectedFriendIds.add(data.userData.id)
                                } else {
                                    selectedFriendIds.remove(
                                        data.userData.id
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (index < friendList.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = dimensionResource(R.dimen.small_padding)),
                                color = Color.Gray
                            )
                        }
                    }
                }

                Button(onClick = { onShareWithFriends(selectedFriendIds.toList()) }) {
                    Text(stringResource(R.string.share_with_selected))
                }

                Button(onClick = { }) {
                    Text(stringResource(R.string.other_sharing_options))
                }

                Button(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

@Composable
fun ShareOption(
    friendData: FriendData,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        if (selected)
            Color("#d7ffd4".toColorInt())
        else
            Color.White
    )

    Row(
        modifier = modifier
            .background(backgroundColor)
            .clickable {
                selected = !selected
                onClick(selected)
            }
            .padding(dimensionResource(R.dimen.medium_padding)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = friendData.userData.username
            )

            Text(
                text = friendData.userData.email
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(R.string.selected_for_share),
                modifier = Modifier.size(24.dp),
                tint = Color("#0c6307".toColorInt())
            )
        }
    }
}
