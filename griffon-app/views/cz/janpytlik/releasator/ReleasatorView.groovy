package cz.janpytlik.releasator

import net.miginfocom.swing.MigLayout
import javax.swing.JFileChooser

/**
 * Created by IntelliJ IDEA.
 * User: Jan Pytlik
 * Date: 12/21/10
 * Time: 8:23 PM
 */

runAction = action(closure: controller.runAction, name: 'Run')


rootFrame = application(title: 'releasator',
        pack: true,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {

    borderLayout()

    panel(layout: new MigLayout(), constraints: context.NORTH) {
      label('Configure Paths', constraints: 'split, span')
      separator(constraints: 'growx, wrap')

      label('Actual Version*:')
      textField(id: 'actualVersion', text: bind {model.actualVersion}, enabled: false, columns: 20, constraints: 'growx, push')
      button('...', actionPerformed: controller.browseOne, constraints: 'wrap')

      label('Old Version*:')
      textField(id: 'oldVersion', text: bind {model.oldVersion}, enabled: false, columns: 20, constraints: 'growx, push')
      button('...', actionPerformed: controller.browseTwo, constraints: 'wrap')

      progressBar(id:'aaa', value: bind {model.progress}, minimum:0, maximum:100, visible: bind{model.visible },  constraints: 'span, push, growx, wrap')

      button(runAction, enabled: bind{model.enabled}, constraints: 'align right, wrap, span')
    }

    panel(layout: new MigLayout(), constraints: context.CENTER) {
      label('New Files', constraints: 'split, span')
      separator(constraints: 'growx, wrap')
      scrollPane(constraints: 'grow, push, span, wrap') {
        textArea(id: 'newFiles',
                rows: 3,
                text: bind {model.newFiles},
                editable: false, autoscrolls: true,
                wrapStyleWord: true, lineWrap: true,
                background: javax.swing.UIManager.getDefaults().getColor("TextArea.selectionBackground"))
      }

      label('Changed Files', constraints: 'split, span')
      separator(constraints: 'growx, wrap')
      scrollPane(constraints: 'grow, push, span, wrap') {
        textArea(id: 'changedFiles',
                rows: 3,
                text: bind {model.changedFiles},
                editable: false, autoscrolls: true,
                wrapStyleWord: true, lineWrap: true,
                background: javax.swing.UIManager.getDefaults().getColor("TextArea.selectionBackground"),
                constraints: 'grow, push, span')
      }

      label('Removed Files', constraints: 'split, span')
      separator(constraints: 'growx, wrap')
      scrollPane(constraints: 'grow, push, span, wrap') {
        textArea(id: 'removedFiles',
                rows: 3,
                text: bind {model.removedFiles},
                editable: false, autoscrolls: true,
                wrapStyleWord: true, lineWrap: true,
                background: javax.swing.UIManager.getDefaults().getColor("TextArea.selectionBackground"),
                constraints: 'grow, push, span')
      }
    }
}

fileChooserWindow = fileChooser(
        dialogTitle: 'Choose a directory',
        id: 'rootFileChooser',
        fileSelectionMode: JFileChooser.DIRECTORIES_ONLY,
)