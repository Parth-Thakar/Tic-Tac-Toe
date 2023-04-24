package com.example.ticktactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.ticktactoe.customView.TicTacToeView

class MainActivity : AppCompatActivity(), TicTacToe.TicTacToeListener, TicTacToeView.SquarePressedListener {
    // DECALRING THE WIDGET AND CUSTOM VIEWS OBJECTS
    lateinit var ticTacToe: TicTacToe
    lateinit var ticTacToeView : TicTacToeView
    lateinit var information : TextView
    lateinit var clearButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // INTIALIZING THE WIDGETS OBJECTS
        ticTacToe = TicTacToe()
        ticTacToe.setTicTacToeListener(this)
        ticTacToeView = findViewById(R.id.ticTacToeView)
        information = findViewById(R.id.information)
        ticTacToeView.squarePressListener = this
        clearButton= findViewById(R.id.clearButton)
        clearButton.setOnClickListener {
            ticTacToe.resetGame()
            resetGameUi()
            clearButton.visibility = View.GONE
        }
    }
    // ON SQUAREPRESSED IMPLEMENTATION OF THE INTERFACE FUNCTION IN MAINACTIVITY
    override fun onSquarePressed(i: Int, j: Int) {
        ticTacToe.moveAt(i, j)
    }
    // MOVEDAT IMPLEMENTATION OF THE INTERFACE FUNCTION IN MAINACTIVITY
    override fun movedAt(x: Int, y: Int, z: Int) {
        if (z == TicTacToe.BoardState.MOVE_X)
            ticTacToeView.drawXAtPosition(x, y)
        else
            ticTacToeView.drawOAtPosition(x, y)
    }
    // gameEndsWithATie() IMPLEMENTATION OF THE INTERFACE FUNCTION IN MAINACTIVITY
    override fun gameEndsWithATie() {
        information.visibility = View.VISIBLE
        information.text = getString(R.string.game_ends_draw)
        clearButton.visibility = View.VISIBLE
        ticTacToeView.isEnabled = false
    }
    // ON CLEAR BUTTON CLICKED RESET GAMEUI CALLED FOR CLEARING OUT THE EXIST
    private fun resetGameUi() {
        ticTacToeView.reset()
        ticTacToeView.isEnabled = true
        information.visibility = View.GONE
        clearButton.visibility = View.GONE
    }
    // SETTING UP THE INFORMATION TEXT VIEW ONCE ANY USER WON WITH CLEAR BUTTON VISIBLE
    override fun gameWonBy(boardPlayer: TicTacToe.BoardPlayer, winCoords: Array<TicTacToe.SquareCoordinates>) {
        information.visibility = View.VISIBLE
        information.text = "Winner is ${if (boardPlayer.move == TicTacToe.BoardState.MOVE_X) "X" else "O"}"
        ticTacToeView.animateWin(winCoords[0].i, winCoords[0].j, winCoords[2].i, winCoords[2].j)
        ticTacToeView.isEnabled = false
        clearButton.visibility = View.VISIBLE
    }

}
