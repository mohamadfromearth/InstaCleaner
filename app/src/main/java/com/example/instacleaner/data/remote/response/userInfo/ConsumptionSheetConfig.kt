package com.example.mohamadkh_instacleaner.data.remote.response.userInfo

data class ConsumptionSheetConfig(
    val can_viewer_donate: Boolean,
    val currency: Any,
    val donation_amount_config: DonationAmountConfig,
    val donation_disabled_message: String,
    val donation_url: Any,
    val has_viewer_donated: Any,
    val privacy_disclaimer: Any,
    val profile_fundraiser_id: Any,
    val you_donated_message: Any
)