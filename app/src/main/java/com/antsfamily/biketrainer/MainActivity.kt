package com.antsfamily.biketrainer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antsfamily.biketrainer.MainApplication.Companion.PROGRAM_IMAGES_PATH
import com.antsfamily.biketrainer.util.navigation.FragmentScreens
import com.antsfamily.biketrainer.R
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

        PROGRAM_IMAGES_PATH = "${applicationInfo.dataDir}/programs"

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
