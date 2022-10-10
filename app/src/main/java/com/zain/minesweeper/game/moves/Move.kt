package com.zain.minesweeper.game.moves

import com.zain.minesweeper.game.Board

interface Move {
    enum class Type {
        Reveal,
        Flag,
        RemoveFlag
    }

    fun execute(board: Board, changeSet: Board.ChangeSet)
}
