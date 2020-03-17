package com.ap.project.webrtcmobile.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ap.project.webrtcmobile.UserAdapter
import com.ap.project.webrtcmobile.databinding.ActivityMainBinding
import com.ap.project.webrtcmobile.events.CallUserEvent
import com.ap.project.webrtcmobile.models.ClientInfo
import com.ap.project.webrtcmobile.presenters.MainPresenter
import com.ap.project.webrtcmobile.utils.APPreferences
import com.ap.project.webrtcmobile.view_interfaces.MainView
import com.hannesdorfmann.mosby.mvp.MvpActivity
import org.greenrobot.eventbus.EventBus

class MainActivity : MvpActivity<MainView?, MainPresenter>(), MainView, UserAdapter.UserAdapterCallback {

    lateinit var binding: ActivityMainBinding
    lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(presenter)
        presenter.init()
    }

    override fun createPresenter() = MainPresenter()


    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.infoView.text = "Hello,  ${APPreferences.userName}"

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            userAdapter = UserAdapter(this@MainActivity)
            adapter = userAdapter
        }
    }

    override fun showOnlineUsers(users: MutableList<out ClientInfo>) {
        userAdapter.replaceModel(users)
    }

    override fun onUserWentOffline(model: ClientInfo) {
        userAdapter.removeModel(model)
    }

    override fun onUserCameOnline(model: ClientInfo) {
        userAdapter.addModel(model)

    }

    override fun onCallUserInvoked(model: ClientInfo) {
        EventBus.getDefault().post(CallUserEvent(
                model.userId,
                model.username,
                APPreferences.userId,
                APPreferences.userName
        ))
    }

}