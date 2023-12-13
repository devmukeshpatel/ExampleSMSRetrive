package com.mkpateldev.examplesmsretrive.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mkpateldev.examplesmsretrive.model.repository.AppRepository

class AuthViewModelFactory(private val app: Application, private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(app,repository) as T
    }
}