package com.mkpateldev.examplesmsretrive.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mkpateldev.examplesmsretrive.viewModel.AuthViewModel
import com.sk.user.agent.data.repository.AppRepository

class AuthViewModelFactory(val app: Application, private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(app,repository) as T
    }
}