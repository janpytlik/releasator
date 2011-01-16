import cz.janpytlik.releasator.Dropper
/**
 * Created by IntelliJ IDEA.
 * User: Jan Pytlik
 * Date: 1/6/11
 * Time: 10:02 PM
 */

onBootstrapEnd = { app ->
    app.windowDisplayHandler = new Dropper()
}