package com.example.mvvmonly

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(RetrofitClient.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchUsers() // Fetch data from API

        lifecycleScope.launch {
            viewModel.users.collectLatest { userList ->
                Toast.makeText(this@MainActivity, "Users: ${userList.size}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
