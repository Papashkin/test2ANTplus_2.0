package com.example.test2antplus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.test2antplus.navigation.AppNavigator
import com.example.test2antplus.navigation.AppRouter
import ru.terrakok.cicerone.Cicerone
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var cicerone: Cicerone<AppRouter>
    @Inject
    lateinit var router: AppRouter

    private lateinit var navigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainApplication.graph.inject(this)

        navigator = AppNavigator(this)

        router.startChain("profiles screen")
    }

    override fun onPause() {
        super.onPause()
        cicerone.navigatorHolder.removeNavigator()
    }

    override fun onResume() {
        super.onResume()
        cicerone.navigatorHolder.setNavigator(navigator)
    }

    override fun onBackPressed() {
        router.exit()
    }

}