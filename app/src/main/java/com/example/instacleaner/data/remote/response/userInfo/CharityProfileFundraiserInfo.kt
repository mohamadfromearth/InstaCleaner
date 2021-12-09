package com.example.mohamadkh_instacleaner.data.remote.response.userInfo

data class CharityProfileFundraiserInfo(
    val consumption_sheet_config: ConsumptionSheetConfig,
    val has_active_fundraiser: Boolean,
    val is_facebook_onboarded_charity: Any,
    val pk: Long
)