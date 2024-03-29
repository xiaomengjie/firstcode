package com.example.firstcode.chapter15.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.firstcode.R
import com.example.firstcode.chapter15.logic.model.Weather
import com.example.firstcode.chapter15.logic.model.getSky
import com.example.firstcode.databinding.ActivityListViewBinding
import com.example.firstcode.databinding.ActivityWeatherBinding
import com.example.firstcode.databinding.ForecastItemBinding
import com.example.firstcode.other.toast
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel: WeatherViewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    private val simpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    lateinit var viewBinding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: 2021/9/3 0003 深入到状态栏，被废弃，需更换
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        viewBinding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng")?: ""
        }
        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat")?: ""
        }
        if (viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name")?: ""
        }

        viewModel.weatherLivedata.observe(this){
            val result = it.getOrNull()
            if (result != null){
                showWeatherInfo(result)
            }else{
                "无法成功获取天气信息".toast()
                it.exceptionOrNull()?.printStackTrace()
            }
            viewBinding.swipeRefresh.isRefreshing = false
        }
        viewBinding.swipeRefresh.setColorSchemeResources(R.color.purple_500)
        viewBinding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        viewBinding.nowLayout.navButton.setOnClickListener {
            viewBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
        viewBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

        refreshWeather()
    }

    internal fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        viewBinding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        val realtime = weather.realtime
        val daily = weather.daily

        //城市
        viewBinding.nowLayout.placeName.text = viewModel.placeName
        //当前温度
        val currentTempText = "${realtime.temperature.toInt()}℃"
        viewBinding.nowLayout.currentTemp.text = currentTempText
        //当前天气
        viewBinding.nowLayout.currentSky.text = getSky(realtime.skycon).info
        //空气指数
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        viewBinding.nowLayout.currentAOI.text = currentPM25Text
        //背景图
        viewBinding.nowLayout.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)


        viewBinding.forecastLayout.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days){
            val skyCon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val binding = ForecastItemBinding.inflate(layoutInflater, viewBinding.forecastLayout.forecastLayout, false)
            binding.dateInfo.text = simpleDateFormat.format(skyCon.date)
            binding.skyIcon.setImageResource(getSky(skyCon.value).icon)
            binding.skyInfo.text = getSky(skyCon.value).info
            binding.temperatureInfo.text = "${temperature.min.toInt()} ~ ${temperature.max.toInt()}℃"
            viewBinding.forecastLayout.forecastLayout.addView(binding.root)
        }

        val lifeIndex = daily.lifeIndex
        viewBinding.lifeIndexLayout.coldRiskText.text = lifeIndex.coldRisk[0].desc
        viewBinding.lifeIndexLayout.dressingText.text = lifeIndex.dressing[0].desc
        viewBinding.lifeIndexLayout.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        viewBinding.lifeIndexLayout.carWashingText.text = lifeIndex.carWashing[0].desc

        viewBinding.weatherLayout.visibility = View.VISIBLE
    }
}