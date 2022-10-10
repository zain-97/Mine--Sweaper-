package com.zain.minesweeper.game.moves

import com.zain.minesweeper.game.Board

class ToggleFlagMove(val row: Int, val column: Int) : Move {
    override fun execute(board: Board, changeSet: Board.ChangeSet) {
        if (board.isFlagged(row, column) || board.isRevealed(row, column))
            changeSet.unflag(row, column)
        else if (board.flagged < board.mines) changeSet.flag(row, column)
    }
}
