package edu.ni.abra.view

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.ni.abra.intent.Intent
import edu.ni.abra.repository.CatRepository
import edu.ni.abra.util.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainViewModel
@ViewModelInject
constructor(
    private val catRepository: CatRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
):ViewModel() {
    val userIntent = Channel<Intent>(Channel.UNLIMITED)

    private val _dataState = MutableStateFlow<DataState>(DataState.Idle)

    val dataState:StateFlow<DataState>
        get() = _dataState

    init {
        setStateEvent()
    }

    fun setStateEvent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect { intnt ->
                when(intnt) {
                    is Intent.GetCatEvent -> {
                        catRepository.getCats()
                            .onEach {
                                _dataState.value = it
                            }
                            .launchIn(viewModelScope)
                    }
                    Intent.None -> {

                    }
                }
            }
        }
    }
}