/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.console.setting

import org.junit.jupiter.api.Test

internal class SettingTest {

    class MySetting : Setting() {
        val int by value(1)
        val map by valueReified(mapOf("" to ""))
        val map2 by valueReified(mapOf("" to mapOf("" to mapOf("" to ""))))
    }

    @Test
    fun testPrimitive() {

    }
}