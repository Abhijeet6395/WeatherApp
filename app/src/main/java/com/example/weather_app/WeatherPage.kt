package com.example.weather_app

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_app.api.NetworkResponse
import com.example.weather_app.api.WeatherModel
import org.jetbrains.annotations.Async

@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    var city by remember { mutableStateOf("") }
    val WeatherOnUi = viewModel.exposedResponse.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.weather),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = city,
                    onValueChange = { city = it },
                    label = {
                        Text(text = "Search for any location")
                    },
                    textStyle = TextStyle(color = Color.White),
                )
                IconButton(onClick = {
                    viewModel.getData(city)
                    keyboardController?.hide()
                }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search for any location",
                        tint = Color.White
                    )
                }
            }

            WeatherContent(viewModel)
        }
    }
}

@Composable
fun GradientText(text: String) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFff7e5f), Color(0xFFfeb47b))
    )

    Text(
        text = text,
        style = TextStyle(
            brush = gradientBrush,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun AnimatedWeatherIcon(iconUrl: String) {
    val rotation by animateFloatAsState(
        targetValue = 360f,
        animationSpec = tween(durationMillis = 2000)
    )

    AsyncImage(
        modifier = Modifier
            .size(160.dp)
            .rotate(rotation),
        model = iconUrl,
        contentDescription = "Condition icon"
    )
}
@Composable
fun WeatherContent(viewModel: WeatherViewModel) {
    val WeatherOnUi = viewModel.exposedResponse.observeAsState()

    Crossfade(targetState = WeatherOnUi.value) { result ->
        when (result) {
            is NetworkResponse.Error -> {
                result.message?.let { Text(text = it, color = Color.White) }
            }
            NetworkResponse.Loading -> CircularProgressIndicator(color = Color.White)
            is NetworkResponse.Success -> {
                WeatherDetails(data = result.value)
            }
            null -> {}
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
            Text(text = data.location.name, fontSize = 30.sp, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.location.country, fontSize = 18.sp, color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))
        GradientText(text = " ${data.current.temp_c} ° c")

        AnimatedWeatherIcon(iconUrl = "https:${data.current.condition.icon}".replace("64x64", "128x128"))

        Text(
            text = data.current.condition.text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Humidity", data.current.humidity.toString())
                    WeatherKeyVal("Wind Speed", "${data.current.wind_kph} km/h")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("UV", data.current.uv.toString())
                    WeatherKeyVal("Participation", "${data.current.precip_mm} mm")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Local Time", data.location.localtime.split(" ")[1])
                    WeatherKeyVal("Local Date", data.location.localtime.split(" ")[0])
                }
            }
        }
    }
}

@Composable
fun WeatherKeyVal(key: String, value: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = key, fontWeight = FontWeight.SemiBold, color = Color.LightGray)
    }
}