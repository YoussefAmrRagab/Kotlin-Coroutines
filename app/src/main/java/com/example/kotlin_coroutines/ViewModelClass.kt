package com.example.kotlin_coroutines


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelClass : ViewModel() {

    private var parentCoroutine = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + parentCoroutine)
    private val viewModel = MutableStateFlow(0)
    var stateFlow: StateFlow<Int> = viewModel

    fun startTimer() {
        val listOfNumber = listOf(1, 1, 2, 2, 2, 3, 4, 5, 5, 6, 6, 7, 8, 9, 9, 10)
        coroutineScope.launch {
            for (i in listOfNumber) {
                viewModel.value = i
                delay(1000)
            }
        }
    }

}