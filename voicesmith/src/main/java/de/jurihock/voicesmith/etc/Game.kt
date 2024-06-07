package de.jurihock.voicesmith.etc

import android.app.GameManager
import android.app.GameState
import android.content.Context

class Game(context: Context, private val interruptible: Boolean? = null) {

  private val game: GameManager? = context.getSystemService(GameManager::class.java)

  fun on() {
    when(interruptible) {
      null -> {}
      true -> game?.setGameState(GameState(false,
        GameState.MODE_GAMEPLAY_INTERRUPTIBLE))
      false -> game?.setGameState(GameState(false,
        GameState.MODE_GAMEPLAY_UNINTERRUPTIBLE))
    }
  }

  fun off() {
    when(interruptible) {
      null -> {}
      else -> game?.setGameState(GameState(false,
        GameState.MODE_NONE))
    }
  }

}
