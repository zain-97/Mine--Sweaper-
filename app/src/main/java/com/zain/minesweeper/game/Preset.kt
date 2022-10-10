package com.zain.minesweeper.game

import com.zain.minesweeper.game.generators.FieldGenerationArguments
import com.zain.minesweeper.game.generators.FieldGenerator
import com.zain.minesweeper.game.generators.RandomFieldGenerator

enum class Preset(val rows: Int, val columns: Int, val mines: Int) {
    EASY(9, 9, 10),
    MEDIUM(16, 16, 40),
    HARD(30, 16, 99);

    fun generate(generator: FieldGenerator = RandomFieldGenerator()): Field {
        return generator.generate(rows, columns, FieldGenerationArguments(mines))
    }
}
