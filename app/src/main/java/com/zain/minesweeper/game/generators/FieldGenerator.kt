package com.zain.minesweeper.game.generators

import com.zain.minesweeper.game.Field

interface FieldGenerator {
    fun generate(rows: Int, columns: Int, args: FieldGenerationArguments): Field
}

data class FieldGenerationArguments(
    val mines: Int
)
