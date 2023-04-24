package com.example.kotlin_coroutines


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.kotlin_coroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val debug: String = "Debug message"
    private val parentJob = Job()
    private lateinit var viewModel: ViewModelClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // to make all coroutines with same parent 'parentJob' and thread 'IO'
        val coroutineScope = CoroutineScope(Dispatchers.IO + parentJob)


        viewModel = ViewModelProvider(this)[ViewModelClass::class.java]
        viewModel.startTimer()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect {
                Log.d(debug, it.toString())
            }
        }


//        runBlocking {
//            flow {// producer
//                val listOfProduct = listOf('A', 'B', 'C', 'D')
//
//                for (item in listOfProduct) {
//                    emit(item)
//                    Log.d(debug, item.toString())
//                }
//            }.buffer() // to make it run parallel not sequence like to separate them in two coroutines
//                .collect {// collector
//                    delay(1000)
//                    Log.d(debug, it.toString())
//                }
//
//            val dataFromAPI = flow {
//                val listOfData = listOf("product4", "product5", "product6", "product7")
//
//                for (item in listOfData) {
//                    emit(item)
//                    delay(1000)
//                }
//            }
//
//            val dataFromLocal = flow {
//                val listOfData = listOf("product1", "product2", "product3")
//                for (item in listOfData) {
//                    emit(item)
//                    delay(2000)
//                }
//            }
//
//            dataFromAPI.zip(dataFromLocal) { API, Local -> // to collect two flows in single one
//                "$Local : $API"
//            }.collect {
//                Log.d(debug, it)
//            }
//
//        }

        val coroutineChannel =
            Channel<String>(1) // capacity of 1 means only one value can be in the buffer at a time

        val products = listOf("laptop", "mobile", "keyboard", "mouse")


        coroutineScope.launch {

            for (item in products) {
                coroutineChannel.send(item) // will block the coroutine until the value can be sent
            }

            for (item in products) {
                coroutineChannel.trySend(item) // will try to send the value, but may return false if the buffer is full
            }

            for (item in coroutineChannel) {
                Log.d(debug, item)
            }

        }


        coroutineScope.launch {
            val time = measureTimeMillis {

                val task1 = launch { Log.d(debug, task1()) }
                val task2 = launch { Log.d(debug, task2()) }

                //                Log.d(debug, task1() + " without launch")
                //                Log.d(debug, task2() + " without launch")

                //                joinAll(task1, task2)
                //                Log.d(debug, "task1 & task2 finished")

                task2.cancelAndJoin()
                task1.join()
                Log.d(debug, "task1 finished")

            }

            Log.d(debug, time.toString())
        }


        coroutineScope.launch {
            Log.d(debug, "coroutines scope: ${task1()}")
        }


        coroutineScope.launch {
            Log.d(debug, "coroutines scope: ${task2()}")
        }


        coroutineScope.launch {
            Log.d(debug, "Thread: ${Thread.currentThread().name}")

            withContext(Dispatchers.Main) {
                Log.d(debug, "Thread: ${Thread.currentThread().name}")
            }
        }

    }

    private suspend fun task1(): String {
        delay(2000)
        return "task1"
    }

    private suspend fun task2(): String {
        delay(2000)
        return "task2"
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel() // to cancel all coroutines in this activity when destroyed
    }

}