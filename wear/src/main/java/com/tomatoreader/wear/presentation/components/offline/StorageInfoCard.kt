package com.tomatoreader.wear.presentation.components.offline

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.tomatoreader.wear.presentation.theme.Shapes

@Composable
fun StorageInfoCard(
    totalSize: String,
    usedSize: String,
    bookCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "存储信息",
                style = WearMaterialTheme.typography.title3,
                color = WearMaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = usedSize,
                        style = WearMaterialTheme.typography.body2,
                        color = WearMaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "已用",
                        style = WearMaterialTheme.typography.caption1,
                        color = WearMaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = totalSize,
                        style = WearMaterialTheme.typography.body2,
                        color = WearMaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "总计",
                        style = WearMaterialTheme.typography.caption1,
                        color = WearMaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = bookCount.toString(),
                        style = WearMaterialTheme.typography.body2,
                        color = WearMaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "书籍",
                        style = WearMaterialTheme.typography.caption1,
                        color = WearMaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}