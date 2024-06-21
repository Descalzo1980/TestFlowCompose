package dev.stas.testflowcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.stas.testflowcompose.ui.theme.TestFlowComposeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestFlowComposeTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    var input by remember { mutableStateOf("") }
    val results = remember { mutableStateListOf<Pair<Int, SnapshotStateList<Int>>>() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        input = it
                    }
                },
                label = { Text("Enter N") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val n = input.toIntOrNull() ?: 0
                input = ""
                val resultList = mutableStateListOf<Int>()
                results.add(n to resultList)
                scope.launch {
                    val resultFlow = combineFlowSummator(n)
                    resultFlow.collect { number ->
                        resultList.add(number)
                    }
                }
            }) {
                Text("Start")
            }
            Spacer(modifier = Modifier.height(8.dp))
            results.forEach { (inputValue, resultList) ->
                Text("Введенное количество: $inputValue")
                Text("Результат: ${resultList.joinToString(" ")}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

fun combineFlowSummator(n: Int): Flow<Int> {
    return flow {
        var currentSum = 0
        for (i in 1..n) {
            currentSum += i
            emit(currentSum)
            delay(100L)
        }
    }
}