package gis.data.survey.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenu(
    title: String,
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (Int, T) -> Unit,
    displayText: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (error != null) Color.Red.copy(alpha = 0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (error != null) Color.Red else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = enabled) { expanded = true }
                .padding(16.dp)
        ) {
            Text(
                text = selectedItem?.let { displayText(it) } ?: label,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(displayText(item)) },
                    onClick = {
                        onItemSelected(index, item)
                        expanded = false
                    }
                )
            }
        }
        error?.let {
            Text(
                text = it,
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

object Dropdown {
    @Composable
    fun <T> ExposedDropdown(
        title: String,
        label: String = "Select Item",
        items: List<T>,
        selectedItem: T?,
        onItemSelected: (Int, T) -> Unit,
        displayText: (T) -> String = { it.toString() },
        modifier: Modifier = Modifier
    ) = DropdownMenu(title, label, items, selectedItem, onItemSelected, displayText, modifier)

    fun <T> selectByValue(
        current: T?,
        newValue: T,
        items: List<T>,
        onItemSelected: (Int, T) -> Unit
    ): T? = items.indexOf(newValue).takeIf { it >= 0 }?.let { index ->
        onItemSelected(index, newValue)
        newValue
    } ?: current

    fun <T> selectByIndex(
        current: T?,
        index: Int,
        items: List<T>,
        onItemSelected: (Int, T) -> Unit
    ): T? = items.getOrNull(index)?.also { onItemSelected(index, it) } ?: current
}