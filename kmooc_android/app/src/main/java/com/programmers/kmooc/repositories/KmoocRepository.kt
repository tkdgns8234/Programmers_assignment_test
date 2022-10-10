package com.programmers.kmooc.repositories

import android.util.Log
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.network.HttpClient
import com.programmers.kmooc.utils.DateUtil
import org.json.JSONObject

class KmoocRepository {

    /**
     * 국가평생교육진흥원_K-MOOC_강좌정보API
     * https://www.data.go.kr/data/15042355/openapi.do
     */

    private val httpClient = HttpClient("http://apis.data.go.kr/B552881/kmooc")
    private val serviceKey =
        "LwG%2BoHC0C5JRfLyvNtKkR94KYuT2QYNXOT5ONKk65iVxzMXLHF7SMWcuDqKMnT%2BfSMP61nqqh6Nj7cloXRQXLA%3D%3D"

    fun list(completed: (LectureList) -> Unit) {
        httpClient.getJson(
            "/courseList",
            mapOf("serviceKey" to serviceKey, "Mobile" to 1)
        ) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
        }
    }

    fun next(currentPage: LectureList, completed: (LectureList) -> Unit) {
        val nextPageUrl = currentPage.next
        httpClient.getJson(nextPageUrl, emptyMap()) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
        }
    }

    fun detail(courseId: String, completed: (Lecture) -> Unit) {
        httpClient.getJson(
            "/courseDetail",
            mapOf("CourseId" to courseId, "serviceKey" to serviceKey)
        ) { result ->
            result.onSuccess {
                completed(parseLecture(JSONObject(it)))
            }
        }
    }

    private fun parseLectureList(jsonObject: JSONObject): LectureList {
        //TODO: JSONObject -> LectureList 를 구현하세요
        val lectureListObject = jsonObject.getJSONObject("pagination")
        val lecturesArray = jsonObject.getJSONArray("results")
        val lectures = mutableListOf<Lecture>()

        for (i in 0 until lecturesArray.length()) {
            val obj = lecturesArray.getJSONObject(i)
            lectures.add(parseLecture(obj))
        }

        val lectureList = LectureList(
            lectureListObject.getInt("count"),
            lectureListObject.getInt("num_pages"),
            lectureListObject.getString("previous"),
            lectureListObject.getString("next"),
            lectures
        )

        return lectureList
    }

    private fun parseLecture(jsonObject: JSONObject): Lecture {
        val obj = jsonObject

        return Lecture(
            obj.getString("id"),
            obj.getString("number"),
            obj.getString("name"),
            obj.getString("classfy_name"),
            obj.getString("middle_classfy_name"),
            obj.getJSONObject("media").getJSONObject("course_image").getString("uri"),
            obj.getJSONObject("media").getJSONObject("image").getString("large"),
            obj.getString("short_description"),
            obj.getString("org_name"),
            DateUtil.parseDate(obj.getString("start")),
            DateUtil.parseDate(obj.getString("end")),
            obj.getString("teachers"),
            if (obj.has("overview")) obj.getString("overview") else ""
        )
    }
}