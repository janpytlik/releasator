application {
    title = 'Releasator'
    startupGroups = ['releasator']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
      // MVC Group for "releasator"
    'releasator' {
        model = 'cz.janpytlik.releasator.ReleasatorModel'
        controller = 'cz.janpytlik.releasator.ReleasatorController'
        view = 'cz.janpytlik.releasator.ReleasatorView'
    }

}
