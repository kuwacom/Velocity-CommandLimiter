package dev.kuwa.commandLimiter.models

data class Permissions(
    var nodeName: String,
    var whiteList: Boolean,
    var commands: Array<String>
)