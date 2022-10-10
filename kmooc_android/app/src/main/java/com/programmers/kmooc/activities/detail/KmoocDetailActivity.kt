package com.programmers.kmooc.activities.detail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.databinding.ActivityKmookDetailBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil
import com.programmers.kmooc.utils.toVisibility
import com.programmers.kmooc.viewmodels.KmoocDetailViewModel
import com.programmers.kmooc.viewmodels.KmoocDetailViewModelFactory

class KmoocDetailActivity : AppCompatActivity() {

    companion object {
        const val INTENT_PARAM_COURSE_ID = "param_course_id"
    }

    private lateinit var binding: ActivityKmookDetailBinding
    private lateinit var viewModel: KmoocDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocDetailViewModelFactory(kmoocRepository)).get(
            KmoocDetailViewModel::class.java
        )

        binding = ActivityKmookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.lectureLiveData.observe(this) {
            with(binding) {
                Log.e("mytest", it.courseImageLarge)
                ImageLoader.loadImage(it.courseImageLarge) {
                    lectureImage.setImageBitmap(it)
                }
                lectureNumber.setDescription("강좌번호", it.number)
                lectureType.setDescription("분류", it.classfyName)
                lectureOrg.setDescription("운영기관", it.orgName)
                lectureTeachers.setDescription("교수정보", it.teachers ?: "")
                lectureDue.setDescription("운영기간", "${DateUtil.formatDate(it.start)} ~ ${DateUtil.formatDate(it.end)}")
                webView.loadData(it.overview, "text/html; charset=utf-8", "utf-8")
            }
        }

        viewModel.progressLiveData.observe(this) {
            binding.progressBar.visibility = it.toVisibility()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val id = intent.getStringExtra(INTENT_PARAM_COURSE_ID)
        viewModel.detail(id)
    }
}