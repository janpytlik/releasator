package cz.janpytlik.releasator

/**
 * Created by IntelliJ IDEA.
 * User: Jan Pytlik
 * Date: 1/6/11
 * Time: 9:54 PM
 */

import java.awt.Window
import griffon.swing.SwingUtils
import griffon.swing.WindowDisplayHandler
import griffon.core.GriffonApplication
import griffon.effects.Effects

class Dropper implements WindowDisplayHandler {
    void show(Window window, GriffonApplication app) {
        SwingUtils.centerOnScreen(window)
        app.execOutside {
            Effects.dropIn(window, wait: true)
        }
    }

    void hide(Window window, GriffonApplication app) {
        app.execOutside {
            Effects.dropOut(window, wait: true)
        }
    }
}