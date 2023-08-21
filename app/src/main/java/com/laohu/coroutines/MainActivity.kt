package com.laohu.coroutines

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.laohu.coroutines.base.BasePresenter
import com.laohu.coroutines.base.MvpPresenter
import com.laohu.coroutines.base.MvpView
import com.laohu.coroutines.model.repository.Repository
import com.laohu.coroutines.model.repository.TAG
import com.laohu.coroutines.pojo.Gank
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch

class MainContract {
    interface View: MvpView {
        fun showLoadingView()
        fun showLoadingSuccessView(granks: List<Gank>)
        fun showLoadingErrorView()
    }

    interface Presenter: MvpPresenter<View> {
        fun syncWithContext()
        fun syncNoneWithContext()
        fun asyncWithContextForAwait()
        fun asyncWithContextForNoAwait()
        fun adapterCoroutineQuery()
        fun retrofitCoroutine()
    }
}

class MainPresenter: MainContract.Presenter, BasePresenter<MainContract.View>() {

    override fun syncWithContext() {
        presenterScope.launch {
            val time = System.currentTimeMillis()
            view.showLoadingView()
            try {
                val ganks = Repository.querySyncWithContext()
                view.showLoadingSuccessView(ganks)
            } catch (e: Throwable) {
                e.printStackTrace()
                view.showLoadingErrorView()
            } finally {
                Log.d(TAG, "Spend: ${System.currentTimeMillis() - time}")
            }
        }
    }

    override fun syncNoneWithContext() {
        presenterScope.launch {
            val time = System.currentTimeMillis()
            view.showLoadingView()
            try {
                val ganks = Repository.querySyncNoneWithContext()
                view.showLoadingSuccessView(ganks)
            } catch (e: Throwable) {
                e.printStackTrace()
                view.showLoadingErrorView()
            } finally {
                Log.d(TAG, "Spend: ${System.currentTimeMillis() - time}")
            }
        }
    }

    override fun asyncWithContextForAwait() {
        presenterScope.launch {
            val time = System.currentTimeMillis()
            view.showLoadingView()
            try {
                val ganks = Repository.queryAsyncWithContextForAwait()
                view.showLoadingSuccessView(ganks)
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.d(TAG, "error: ${e.message}")
                view.showLoadingErrorView()
            } finally {
                Log.d(TAG, "耗时：${System.currentTimeMillis() - time}")
            }
        }
    }

    override fun asyncWithContextForNoAwait() {
        presenterScope.launch {
            val time = System.currentTimeMillis()
            view.showLoadingView()
            try {
                val ganks = Repository.queryAsyncWithContextForNoAwait()
                view.showLoadingSuccessView(ganks)
            } catch (e: Throwable) {
                e.printStackTrace()
                view.showLoadingErrorView()
            } finally {
                Log.d(TAG, "Spend: ${System.currentTimeMillis() - time}")
            }
        }
    }

    override fun adapterCoroutineQuery() {
        presenterScope.launch {
            val time = System.currentTimeMillis()
            view.showLoadingView()
            try {
                val ganks = Repository.adapterCoroutineQuery()
                view.showLoadingSuccessView(ganks)
            } catch (e: Throwable) {
                e.printStackTrace()
                view.showLoadingErrorView()
            } finally {
                Log.d(TAG, "Spend: ${System.currentTimeMillis() - time}")
            }
        }
    }

    override fun retrofitCoroutine() {
        presenterScope.launch {
            val time = System.currentTimeMillis()
            view.showLoadingView()
            try {
                val ganks = Repository.retrofitSuspendQuery()
                view.showLoadingSuccessView(ganks)
            } catch (e: Throwable) {
                e.printStackTrace()
                view.showLoadingErrorView()
            } finally {
                Log.d(TAG, "Spend: ${System.currentTimeMillis() - time}")
            }
        }
    }
}

class MainActivity : AppCompatActivity(0), MainContract.View {
    private val presenter = MainPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.attachView(this)
        syncWithContextBtn.setOnClickListener {
            presenter.syncWithContext()
        }
        syncNoneWithContext.setOnClickListener {
            presenter.syncNoneWithContext()
        }
        asyncWithContextForAwait.setOnClickListener {
            presenter.asyncWithContextForAwait()
        }
        asyncWithContextForNoAwait.setOnClickListener {
            presenter.asyncWithContextForNoAwait()
        }
        adapterBtn.setOnClickListener {
            presenter.adapterCoroutineQuery()
        }
        retrofitBtn.setOnClickListener {
            presenter.retrofitCoroutine()
        }
        cancelBtn.setOnClickListener {
            presenter.detachView()
        }
    }

    override fun showLoadingView() {
        loadingBar.showSelf()
    }

    override fun showLoadingSuccessView(granks: List<Gank>) {
        loadingBar.hideSelf()
        textView.text = "Request Over, the number of results: ${granks.size}"
        Toast.makeText(this, "load successfully", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "The result of request：$granks")
    }

    override fun showLoadingErrorView() {
        loadingBar.hideSelf()
        Toast.makeText(this, "Fail to load", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onBackPressed() {
        finish()
    }
}

fun View.showSelf() {
    this.visibility = View.VISIBLE
}

fun View.hideSelf() {
    this.visibility = View.GONE
}
