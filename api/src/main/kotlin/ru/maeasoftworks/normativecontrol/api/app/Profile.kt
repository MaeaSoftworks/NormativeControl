package ru.maeasoftworks.normativecontrol.api.app

enum class Profile {
    DEV,
    STANDALONE,
    PRODUCTION;

    override fun toString(): String {
        return super.toString().lowercase()
    }

    companion object {
        const val ARGUMENT_NAME = "normativecontrol.profile"

        operator fun invoke(value: String?): Profile? {
            return value?.uppercase()?.let { Profile.valueOf(it) }
        }
    }
}