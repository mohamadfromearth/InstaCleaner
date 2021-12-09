package com.example.mohamadkh_instacleaner.data.remote.response.userInfo

data class DonationAmountConfig(
    val default_selected_donation_value: Any,
    val donation_amount_selector_values: List<Any>,
    val maximum_donation_amount: Any,
    val minimum_donation_amount: Any,
    val prefill_amount: Any,
    val user_currency: Any
)