package com.example.firstcode.other

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.firstcode.chapter14.FirstCodeApplication
import com.example.firstcode.chapter15.WeatherApplication
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun <T: Activity> actionStart(context: Context, clazz: Class<T>){
    context.startActivity(Intent(context, clazz))
}

fun showToast(context: Context, msg: String){
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun String.toast(context: Context, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(context, this, duration).show()
}

fun String.toast(duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(WeatherApplication.context, this, duration).show()
}

operator fun String.times(n: Int) = repeat(n)

infix fun Int.to(name: String) = ButtonBean(this, name)

inline fun <reified T: Activity> startActivity(context: Context){
    context.startActivity(Intent(context, T::class.java))
}

inline fun JSONArray.forEach(block: (JSONObject) -> Unit){
    for (i in 0 until length()){
        block(getJSONObject(i))
    }
}

suspend fun <T> Call<T>.customAwait(): T{
    return suspendCoroutine {
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body != null){
                    it.resume(body)
                }else{
                    it.resumeWithException(RuntimeException("response body is null"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                it.resumeWithException(t)
            }
        })
    }
}

//求取一系列数中的最大值
fun <T: Comparable<T>> max(vararg numbers: T): T{
    if (numbers.isEmpty()) throw RuntimeException("Params can not be empty")
    var maxNum = numbers[0]
    for (num in numbers){
        if (num > maxNum) maxNum = num
    }
    return maxNum
}

//求取一系列数中的最小值
fun <T: Comparable<T>> min(vararg numbers: T): T{
    if (numbers.isEmpty()) throw RuntimeException("Params can not be empty")
    var minNum = numbers[0]
    for (num in numbers){
        if (num < minNum) minNum = num
    }
    return minNum
}