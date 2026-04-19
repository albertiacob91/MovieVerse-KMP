package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.remote.CommentDto

@Composable
fun CommentItem(
    comment: CommentDto
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = comment.username,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}