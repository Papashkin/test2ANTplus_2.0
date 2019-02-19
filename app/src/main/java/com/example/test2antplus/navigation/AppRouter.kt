package com.example.test2antplus.navigation

import com.example.test2antplus.navigation.commands.Back
import com.example.test2antplus.navigation.commands.BackTo
import com.example.test2antplus.navigation.commands.NavigateTo
import com.example.test2antplus.navigation.commands.StartChain
import ru.terrakok.cicerone.BaseRouter
import javax.inject.Inject

class AppRouter @Inject constructor(): BaseRouter() {

    fun startChain(tag: String) {
        executeCommands(StartChain(tag))
    }

    fun navigateTo(tag: String, data: Any? = null) {
        executeCommands(NavigateTo(tag, data))
    }

    fun backTo(tag: String?) {
        executeCommands(BackTo(tag))
    }

    fun exit() {
        executeCommands(Back())
    }
}