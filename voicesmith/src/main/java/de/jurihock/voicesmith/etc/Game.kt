package de.jurihock.voicesmith.etc

import android.app.GameManager
import android.app.GameState
import android.content.Context

class Game(context: Context, val interruptible: Boolean? = null) {

  val game: GameManager? = context.getSystemService(GameManager::class.java)

  fun on() {
    if (interruptible != null) {
      game?.setGameState(GameState(false,
        if (interruptible) GameState.MODE_GAMEPLAY_INTERRUPTIBLE
        else GameState.MODE_GAMEPLAY_UNINTERRUPTIBLE))
    }
  }

  fun off() {
    if (interruptible != null) {
      game?.setGameState(GameState(false,
        GameState.MODE_NONE))
    }
  }

}
