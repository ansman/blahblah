package io.spherelabs.blahblahfake.internal

import io.spherelabs.blahblahyaml.parser.yamlParser
import io.spherelabs.blahblahyaml.provider.YamlProvider
import io.spherelabs.blahblahyaml.provider.yamlProvider


interface BlahBlahConfig {
    val locale: String
    val retryLimit: Int
    val yamlProvider: YamlProvider
    val path: String

    data class Builder(
        var locale: String = "en",
        var retryLimit: Int = 0,
        var provider: YamlProvider? = null,
        var path: String = ""
    )
}

inline fun configs(builder: BlahBlahConfig.Builder.() -> Unit): BlahBlahConfig {
    val config = BlahBlahConfig.Builder().apply(builder)

    return object : BlahBlahConfig {
        override val locale: String = config.locale
        override val retryLimit: Int = config.retryLimit
        override val path: String = config.path

        override val yamlProvider: YamlProvider = yamlProvider {
            yamlParser = yamlParser {
                locale = config.locale
                resourcePath { config.path }
                sectionKey = "blah-blah"
            }
        }
    }
}