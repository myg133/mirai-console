/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

@file:Suppress("FunctionName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package net.mamoe.mirai.console.codegen

import org.intellij.lang.annotations.Language

abstract class Replacer(private val name: String) : (String) -> String {
    override fun toString(): String {
        return name
    }
}

fun Codegen.Replacer(block: (String) -> String): Replacer {
    return object : Replacer(this@Replacer::class.simpleName ?: "<unnamed>") {
        override fun invoke(p1: String): String = block(p1)
    }
}

class CodegenScope : MutableList<Replacer> by mutableListOf() {
    fun applyTo(fileContent: String): String {
        return this.fold(fileContent) { acc, replacer -> replacer(acc) }
    }

    @CodegenDsl
    operator fun Codegen.invoke(vararg ktTypes: KtType) {
        if (ktTypes.isEmpty() && this is DefaultInvoke) {
            invoke(defaultInvokeArgs)
        }
        invoke(ktTypes.toList())
    }

    @CodegenDsl
    operator fun Codegen.invoke(ktTypes: Collection<KtType>) {
        add(Replacer {
            it + buildString {
                ktTypes.forEach { applyTo(this, it) }
            }
        })
    }

    @RegionCodegenDsl
    operator fun RegionCodegen.invoke(vararg ktTypes: KtType) = invoke(ktTypes.toList())

    @RegionCodegenDsl
    operator fun RegionCodegen.invoke(ktTypes: Collection<KtType>) {
        add(Replacer {
            it.replace(Regex("""//// region $regionName CODEGEN ////([\s\S]*?)//// endregion $regionName CODEGEN ////""")) {
                val code = CodegenScope().apply { (this@invoke as Codegen).invoke(*ktTypes.toTypedArray()) }.applyTo("")
                """
                        |//// region $regionName CODEGEN ////
                        |
                        |$code
                        |
                        |//// endregion $regionName CODEGEN ////
                    """.trimMargin()
            }
        })
    }

    @DslMarker
    annotation class CodegenDsl
}

@DslMarker
annotation class RegionCodegenDsl

interface DefaultInvoke {
    val defaultInvokeArgs: List<KtType>
}

abstract class Codegen {
    fun applyTo(stringBuilder: StringBuilder, ktType: KtType) = this.run { stringBuilder.apply(ktType) }

    protected abstract fun StringBuilder.apply(ktType: KtType)
}

abstract class RegionCodegen(regionName: String? = null) : Codegen() {
    val regionName: String by lazy {
        regionName ?: this::class.simpleName!!.substringBefore("Codegen")
    }
}

abstract class PrimitiveCodegen : Codegen() {
    protected abstract fun StringBuilder.apply(ktType: KtPrimitive)

    fun StringBuilder.apply(ktType: List<KtPrimitive>) = ktType.forEach { apply(it) }
}

fun StringBuilder.appendKCode(@Language("kt") ktCode: String): StringBuilder = append(kCode(ktCode)).appendLine()
