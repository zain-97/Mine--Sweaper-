package com.zain.minesweeper.ui.game

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_game.*
import com.zain.minesweeper.KEY_BOARD
import com.zain.minesweeper.PREFS_NAME
import com.zain.minesweeper.R
import com.zain.minesweeper.game.Board
import com.zain.minesweeper.game.generators.FieldGenerationArguments
import com.zain.minesweeper.game.generators.RandomFieldGenerator
import com.zain.minesweeper.ui.settings.Settings

class GameFragment : Fragment() {
    private val prefs by lazy {
        requireContext().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    private var minesList= mutableListOf<String>()
    private val generator = RandomFieldGenerator()

    private lateinit var board: Board
    private var started = false
    private var remainingMines=10
    private var startTime: Long = 0

    companion object {
        var switchMark = false
        var gameLose = false
    }

    var timerHandler: Handler = Handler(Looper.getMainLooper())
    var timerRunnable: Runnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds = seconds % 60
            timerText.setText(String.format("%d:%02d", minutes, seconds))
            timerHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setRetainInstance(true)
        initialSetup(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        resetLayout()

        updateMarkedMines(remainingMines)

        board_view.board = board

        board_view.moveListener = object : BoardView.OnMoveListener {
            override fun onMove(board: Board, state: Board.State,row:Int,column:Int) {
                when (state) {
                    Board.State.Win -> {
                        board_view.isEnabled = false

                        AlertDialog.Builder(requireContext())
                            .setMessage(R.string.dialog_win)
                            .setCancelable(false)
                            .setNegativeButton(R.string.action_new_game) { _, _ -> newGame() }
                            .create()
                            .show()
                        timerHandler.removeCallbacks(timerRunnable);
                    }
                    Board.State.Loss -> {
                        gameLose=true
                        board_view.isEnabled = false

                        AlertDialog.Builder(requireContext())
                            .setMessage(R.string.dialog_lose)
                            .setCancelable(false)
                            .setNegativeButton(R.string.action_new_game) { _, _ -> newGame() }
                            .create()
                            .show()
                        timerHandler.removeCallbacks(timerRunnable);
                    }
                    Board.State.Neutral -> {
                        board_view.isEnabled = true

                        if(board.isMine(row,column)){
                            if(!minesList.contains(""+row+column)){
                                minesList.add(""+row+column)
                                remainingMines--
                            }

                        }

                        updateMarkedMines(remainingMines)

                    }
                }
            }
        }

        switchButton.setOnClickListener {
            if(switchMark){
                switchMark=false
                switchButton.text="Switch to mark"
            }
            else
            {
                switchMark=true
                switchButton.text="Switch to uncover"
            }
        }
        restartButton.setOnClickListener { newGame() }

        startTime = System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerHandler.removeCallbacks(timerRunnable)
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        with(outState) {
            putParcelable(KEY_BOARD, board)
        }
    }

    private fun updateMarkedMines(remainingMines:Int) {
        text_marked_mines.text=("Marked mines= $remainingMines").toString()
    }

    private fun initialSetup(bundle: Bundle?) {
        started = false

        val stored = bundle?.getParcelable<Board>(KEY_BOARD)
        Log.v("Minesweeper", stored.toString())

        val field = generator.generate(
            Settings.columns,
            Settings.rows,
            FieldGenerationArguments(Settings.mines)
        )

        started = false
        board = Board(field)
    }

    fun restartGame() {
        board.restart()
        minesList.clear()
        resetLayout()
    }

    fun newGame() {
        startTime=System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable, 0)
        gameLose=false
        minesList.clear()
        initialSetup(null)
        resetLayout()
    }

    fun resetLayout() {
        board_view.board = board
        remainingMines=10
        updateMarkedMines(remainingMines)
    }


}
