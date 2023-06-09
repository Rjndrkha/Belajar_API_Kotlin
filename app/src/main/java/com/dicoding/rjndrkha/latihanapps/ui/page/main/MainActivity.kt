package com.dicoding.rjndrkha.latihanapps.ui.page.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.rjndrkha.latihanapps.adapter.OnItemClickCallback
import com.dicoding.rjndrkha.latihanapps.adapter.UserAdapter
import com.dicoding.rjndrkha.latihanapps.databinding.ActivityMainBinding
import com.dicoding.rjndrkha.latihanapps.networking.NetworkConnCheck
import com.dicoding.rjndrkha.latihanapps.networking.UserResponse
import com.dicoding.rjndrkha.latihanapps.ui.page.detail.DetailActivity

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

   // private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityMainBinding

    private val adapter: UserAdapter by lazy {
        UserAdapter()
    }
    private lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbar()
        setViewModel()
        observeAnimationAndProgressBar()
        //        darkModeCheck()
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        checkInternetConnection()
        //setUpSearchView()
    }

    private fun setViewModel(){
       // val pref = SettingPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, MainViewModelFactory())[MainViewModel::class.java]
    }
//    private fun darkModeCheck(){
//        mainViewModel.getThemeSettings().observe(this@MainActivity,{isDarkModeActive ->
//            if (isDarkModeActive) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        })
//
//    }
//
    private fun setUpToolbar() {
        binding.toolbar.setOnMenuItemClickListener(this)
    }

//    private fun setUpSearchView() {
//
//        with(binding) {
//            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String): Boolean {
//                    showFailedLoadData(false)
//                    showProgressBar(true)
//                    UserRepository.getUserBySearch(query)
//                    mainViewModel.searchUser.observe(this@MainActivity) { searchUserResponse ->
//                        if (searchUserResponse != null) {
//                            adapter.addDataToList(searchUserResponse)
//                            searchView.clearFocus()
//                        }
//                    }
//                    return true
//                }
//
//                override fun onQueryTextChange(p0: String?): Boolean {
//                    return false
//                }
//            })
//
//        }
//    }

    private fun observeAnimationAndProgressBar() {
        mainViewModel.isLoading.observe(this, {
            showProgressBar(it)
        })
        mainViewModel.isDataFailed.observe(this, {
            showFailedLoadData(it)
        })
    }

    private fun checkInternetConnection() {
        val networkConnection = NetworkConnCheck(applicationContext)
        networkConnection.observe(this, { isConnected ->
            if (isConnected) {
                showFailedLoadData(false)
                mainViewModel.user.observe(this, { userResponse ->
                    if (userResponse != null) {
                        adapter.addDataToList(userResponse)
                        setUserData()
                    }
                })
                mainViewModel.searchUser.observe(this@MainActivity) { searchUserResponse ->
                    if (searchUserResponse != null) {
                        adapter.addDataToList(searchUserResponse)
                        binding.rvMain.visibility = View.VISIBLE
                    }
                }
            } else {
                mainViewModel.user.observe(this, { userResponse ->
                    if (userResponse != null) {
                        adapter.addDataToList(userResponse)
                        setUserData()
                    }
                })
                Toast.makeText(this@MainActivity, "Tidak ada koneksi internet", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun hideUserList() {
        binding.rvMain.layoutManager = null
        binding.rvMain.adapter = null
    }

    private fun showProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @Suppress("SameParameterValue")
    private fun showFailedLoadData(isFailed: Boolean) {
        //binding.animFailedDataLoad.visibility = if (isFailed) View.VISIBLE else View.GONE
        binding.tvFailed.visibility = if (isFailed) View.VISIBLE else View.GONE

    }

    private fun setUserData() {
        with(binding) {
            val layoutManager =
                GridLayoutManager(this@MainActivity, 1, GridLayoutManager.VERTICAL, false)
            rvMain.layoutManager = layoutManager
            rvMain.adapter = adapter
            adapter.setOnItemClickCallback(object : OnItemClickCallback {
                override fun onItemClicked(user: UserResponse) {
                    hideUserList()
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.KEY_USER, user)
                    intent.putExtra(DetailActivity.KEY_USERNAME, user.login)
                    intent.putExtra(DetailActivity.KEY_ID, user.id)
                    startActivity(intent)
                }
            })
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return false
    }

//    override fun onMenuItemClick(item: MenuItem?): Boolean {
//        return when (item?.itemId) {
//            R.id.btn_setting -> {
//                val setting = Intent(this, SettingActivity::class.java)
//                startActivity(setting)
//                true
//            }
//            R.id.btn_favorite -> {
//                val favorite = Intent(this, FavoriteActivity::class.java)
//                startActivity(favorite)
//                true
//            }
//            else -> false
//        }
//    }


}
