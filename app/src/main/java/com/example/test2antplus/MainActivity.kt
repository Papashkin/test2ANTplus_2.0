package com.example.test2antplus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test2antplus.navigation.FragmentScreens
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var cicerone: Cicerone<Router>
    @Inject
    lateinit var router: Router
    @Inject
    lateinit var navHolder: NavigatorHolder

    private val navigator by lazy { SupportAppNavigator(this, R.id.fragmentLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainApplication.graph.inject(this)

        MainApplication.APP_FOLDER_PATH = this.filesDir.absolutePath

        router.newRootScreen(FragmentScreens.StartScreen())
    }

    override fun onPause() {
        super.onPause()
        navHolder.removeNavigator()
    }

    override fun onResume() {
        super.onResume()
        navHolder.setNavigator(navigator)
    }

    override fun onBackPressed() {
        router.exit()
    }

}