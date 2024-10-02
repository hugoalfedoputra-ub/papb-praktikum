package com.example.ch2p.data.entity

import com.google.gson.annotations.SerializedName

data class Profile(
    @field:SerializedName("avatar_url")
    val avatarUrl: String,
    @field:SerializedName("bio")
    val bio: String,
    @field:SerializedName("blog")
    val blog: String,
    @field:SerializedName("company")
    val company: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("email")
    val email: Any,
    @field:SerializedName("events_url")
    val eventsUrl: String,
    @field:SerializedName("followers")
    val followers: Int,
    @field:SerializedName("followers_url")
    val followersUrl: String,
    @field:SerializedName("following")
    val following: Int,
    @field:SerializedName("following_url")
    val followingUrl: String,
    @field:SerializedName("gists_url")
    val gistsUrl: String,
    @field:SerializedName("gravatar_id")
    val gravatarId: String,
    @field:SerializedName("hireable")
    val hireable: Any,
    @field:SerializedName("html_url")
    val htmlUrl: String,
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("location")
    val location: String,
    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("node_id")
    val nodeId: String,
    @field:SerializedName("organizations_url")
    val organizationsUrl: String,
    @field:SerializedName("public_gists")
    val publicGists: Int,
    @field:SerializedName("public_repos")
    val publicRepos: Int,
    @field:SerializedName("received_events_url")
    val receivedEventsUrl: String,
    @field:SerializedName("repos_url")
    val reposUrl: String,
    @field:SerializedName("site_admin")
    val siteAdmin: Boolean,
    @field:SerializedName("starred_url")
    val starredUrl: String,
    @field:SerializedName("subscriptions_url")
    val subscriptionsUrl: String,
    @field:SerializedName("twitter_username")
    val twitterUsername: Any,
    @field:SerializedName("type")
    val type: String,
    @field:SerializedName("updated_at")
    val updatedAt: String,
    @field:SerializedName("url")
    val url: String
)
