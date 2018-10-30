package com.epitech.flatot.epicture.Model

import com.google.gson.annotations.SerializedName

class AccessToken {
    @SerializedName("access_token")
    private val accessToken: String? = null

    @SerializedName("token_type")
    private val tokenType: String? = null

    fun getAccessToken(): String? {
        return accessToken;
    }

    fun getTokenType(): String? {
        return tokenType;
    }
}
