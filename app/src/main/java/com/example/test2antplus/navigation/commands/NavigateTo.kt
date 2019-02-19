package com.example.test2antplus.navigation.commands

import ru.terrakok.cicerone.commands.Command

class NavigateTo(
    val screenKey: String,
    val data: Any?
): Command