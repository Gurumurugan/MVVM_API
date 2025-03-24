# MVI_API
1️⃣ Dependencies

Add the required dependencies in your build.gradle.kts:

dependencies {
implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.6.2"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2"
implementation "androidx.compose.runtime:runtime-livedata:1.5.0"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1"
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.retrofit2:converter-gson:2.9.0"
}


2️⃣ Define API Interface (Retrofit)

import retrofit2.http.GET

interface ApiService {
@GET("users")
suspend fun getUsers(): List<User>
}

3️⃣ Create Data Model

data class User(
val id: Int,
val name: String,
val email: String
)

4️⃣ Define UIState
sealed class UIState {
object Loading : UIState()
data class Success(val data: List<User>) : UIState()
data class Error(val message: String) : UIState()
}

5️⃣ Define UIIntent (User Actions)
sealed class UIIntent {
object FetchUsers : UIIntent()
}

6️⃣ Create Repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRepository {
private val api: ApiService = Retrofit.Builder()
.baseUrl("https://jsonplaceholder.typicode.com/")
.addConverterFactory(GsonConverterFactory.create())
.build()
.create(ApiService::class.java)

suspend fun fetchUsers(): List<User> = api.getUsers()
}


7️⃣ ViewModel with MVI + StateFlow

 import androidx.lifecycle.ViewModel
 import androidx.lifecycle.viewModelScope
 import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.StateFlow
 import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
private val _state = MutableStateFlow<UIState>(UIState.Loading)
val state: StateFlow<UIState> = _state

    fun handleIntent(intent: UIIntent) {
        when (intent) {
            is UIIntent.FetchUsers -> fetchUsers()
        }
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _state.value = UIState.Loading
            try {
                val users = repository.fetchUsers()
                _state.value = UIState.Success(users)
            } catch (e: Exception) {
                _state.value = UIState.Error("Failed to fetch users")
            }
        }
    }
}

8️⃣ UI in Jetpack Compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserScreen(viewModel: UserViewModel = viewModel()) {
val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(UIIntent.FetchUsers)
    }

    when (uiState) {
        is UIState.Loading -> CircularProgressIndicator()
        is UIState.Success -> {
            val users = (uiState as UIState.Success).data
            LazyColumn {
                items(users) { user ->
                    Text(text = "${user.name} - ${user.email}",
                        modifier = Modifier.padding(16.dp).clickable { })
                }
            }
        }
        is UIState.Error -> Text(text = "Error: ${(uiState as UIState.Error).message}")
    }
}

1️⃣ Modify MainActivity to Use Jetpack Compose
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)

        // Initialize ViewModel
        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        setContent {
            UserScreen(viewModel)
        }
    }
}