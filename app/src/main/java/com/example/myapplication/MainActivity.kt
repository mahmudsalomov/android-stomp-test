package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage


private lateinit var mStompClient: StompClient;

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
//        mStompClient=Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.0.110:9999/websocket-connection")
        mStompClient=Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://start-taxi-4f887d3fbda9.herokuapp.com/websocket-connection")

        connectStompClient()
//        subscribeToTest()
        sendDataToServer("hi")
    }




    @SuppressLint("CheckResult")
    private fun connectStompClient() {
        mStompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        mStompClient.connect()

        mStompClient.lifecycle()?.subscribe { lifecycleEvent: LifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> onStompConnected()
                LifecycleEvent.Type.ERROR -> onStompError(lifecycleEvent.exception)

                LifecycleEvent.Type.CLOSED -> onStompClosed()
                else -> {}
            }
        }
    }

    private fun onStompConnected() {
        Log.d("onStompConnected", "connect")
    }

    private fun onStompError(exception: Exception?) {
        Log.d("onStompError", "exception $exception")
    }

    private fun onStompClosed() {
        Log.d("onStompClosed", "closed")

    }

    @SuppressLint("CheckResult")
    private fun subscribeToTest() {
        if (mStompClient.isConnected){
            mStompClient.topic("/topic/ping")?.subscribe { topicMessage: StompMessage ->
                Log.d("@@@@@", "subscribeToTest: ${topicMessage.payload}")
            }
        }

    }

    @SuppressLint("CheckResult")
    fun sendDataToServer(message: String) {
        if (mStompClient.isConnected){
            val destination = "/app/createOrder"

            mStompClient.send(destination, message)?.subscribe(
                {
                    subscribeToFindDriverStatusTopic()
                },
                { error -> }
            )
        }

    }
    @SuppressLint("CheckResult")
    private fun subscribeToFindDriverStatusTopic() {
        mStompClient.topic("/topic/createOrder")?.subscribe { topicMessage: StompMessage ->

//        onTopicGetResponse(topicMessage)
        }
    }
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Button(onClick = { sendDataToServer("asdadadad") }) {
            Text("Filled")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyApplicationTheme {
            Greeting("Android")
        }
    }
}






