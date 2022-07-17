package com.example.bmob.viewmodels

import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.STUDENT_HAS_SELECTED_THESIS
import com.example.bmob.data.entity.STUDENT_NOT_SELECT_THESIS
import com.example.bmob.data.entity.User
import com.example.bmob.utils.*

class StudentThesisViewModel:ViewModel() {

    /**
     * 学生退选
     */
    fun studentOutThesis(student: User,callback:(isSuccess:Boolean,student:User?,message:String)->Unit){
        student.studentSelectState = STUDENT_NOT_SELECT_THESIS
        student.studentThesis = null
        student.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if (p0 == null){
                    callback.invoke(true,student,"已成功退选")
                }else{
                    callback.invoke(false,null,"${p0.message}")
                }
            }
        })
    }

    /**
     * 不用这样查
     * 学生登录了他就知道他的属性 studentThesis
     */
    private fun queryStudentHasChosenThesis(student:User){
        val addWhereEqualToStudentSelectState =
            BmobQuery<User>().addWhereEqualTo(StudentSelectState, STUDENT_HAS_SELECTED_THESIS)
        val equalToSchool = BmobQuery<User>()
            .addWhereEqualTo(School, student.school)
        val equalToDepartment = BmobQuery<User>()
            .addWhereEqualTo(Department, student.department)
        val equalToCollege = BmobQuery<User>()
            .addWhereEqualTo(College, student.college)
        val equalToIdentification = BmobQuery<User>()
            .addWhereEqualTo(Identification, IDENTIFICATION_STUDENT)
        val addWhereEqualTo = BmobQuery<User>()
            .addWhereEqualTo(ObjectId, student.objectId)

        val queryList = ArrayList<BmobQuery<User>>().run {
            add(addWhereEqualToStudentSelectState)
            add(equalToSchool)
            add(equalToDepartment)
            add(equalToCollege)
            add(equalToIdentification)
            add(addWhereEqualTo)
            this@run
        }
    }
}