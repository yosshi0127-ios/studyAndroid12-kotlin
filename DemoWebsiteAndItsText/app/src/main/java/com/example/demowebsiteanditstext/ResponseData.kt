package com.example.demowebsiteanditstext

data class ResponseData (
    val message: String,
    val user_id: Int,
    val name: String,
    val email: String,
    val mobile: String,
    val profile_details: ProfileDetails,
    val data_list: List<DataListDetail>
)

data class ProfileDetails(
    val is_profile_completed: Boolean,
    val rating: Double
)

data class DataListDetail(
    val id: Int,
    val value: String
)