/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.console.command

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.Image
import org.junit.jupiter.api.Test

object TestCompositeCommand : CompositeCommand(
    TestCommandOwner,
    "groupManagement", "grpMgn"
) {
    @SubCommand
    suspend fun CommandSender.mute(image: Image, target: Member, seconds: Int): Boolean {
        target.mute(seconds)
        return true
    }
}


internal class TestComposite {

    @Test
    fun testRegister() {
        TestCompositeCommand.register()
    }
}