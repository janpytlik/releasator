package cz.janpytlik.releasator

import javax.swing.JFileChooser
import java.awt.event.KeyEvent
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

/**
 * Created by IntelliJ IDEA.
 * User: Jan Pytlik
 * Date: 12/21/10
 * Time: 8:23 PM
 */
class ReleasatorController {

    private static final String CONFIG_FILE = "configuration.xml"

    def model
    def view

    /**
     * Load last paths
     */
    def onReadyEnd = { app ->

        def ignoreFile = new File(CONFIG_FILE)
        if (ignoreFile.exists()) {
            def root = new XmlSlurper().parse(ignoreFile)
            model.actualVersion = root.actualVersion.text()
            model.oldVersion = root.oldVersion.text()
        }
    }

    def void mvcGroupDestroy() {

        if (model.actualVersion != "" && model.oldVersion != null) {
            def configFile = new File(CONFIG_FILE)
            if (configFile.exists()) {
                print("config file existuje")
                def root = new XmlParser().parse(configFile)

                if (root.actualVersion.size() != 0) {
                    root.remove(root?.actualVersion)
                }

                if (root.oldVersion.size() != 0) {
                    root.remove(root?.oldVersion)
                }
                def newNode = new Node(root, 'actualVersion', model.actualVersion)
                newNode = new Node(root, 'oldVersion', model.oldVersion)
//                new XmlNodePrinter(new PrintWriter(CONFIG_FILE), preserveWhitespace: true).print(root)

                def a = XmlUtil.serialize(root)
                new File(CONFIG_FILE).text = a
            } else {
                print("config file NEexistuje")
                def writer = new StringWriter()
                def xml = new MarkupBuilder(writer)
                xml.config() {
                    ignoreList() {
                        actualVersion(model.actualVersion)
                        oldVersion(model.oldVersion)
                    }
                }
                new File(CONFIG_FILE).text = writer.toString()
            }
        }
    }

    def runAction = {evt ->
        execOutside {

            execAsync {
                model.enabled = false
                model.visible = true
                model.newFiles = ''
                model.changedFiles = ''
                model.removedFiles = ''
            }

            def ignoreList = loadIgnoreList()
            execAsync {model.progress = 3}

            //calculate step for progressbar
            def counter = 0
            new File(view.actualVersion.text).eachFileRecurse() { counter++ }
            def step = (counter / 30).toInteger()
            if (step == 0) {
                step = 1
            }
            execAsync {model.progress = 5}

            //first step -> actual version
            def sourceDirectoryList = prepareFiles(view.actualVersion.text, ignoreList, step)

            //second step -> previous version
            def destDirectoryList = prepareFiles(view.oldVersion.text, ignoreList, step)

            //compare both directories
            def changedFiles = []
            def newFiles = []

            sourceDirectoryList.each {key, value ->
                def destValue = destDirectoryList[key]

                if (destValue == null) {
                    newFiles.add(value.getPath())
                } else if (value.hash != destValue.hash) {
                    changedFiles.add(value.getPath())
                }
            }
            execAsync {model.progress = 71}

            //find deleted files
            def removedFiles = []

            destDirectoryList.each {key, value ->
                def sourceValue = sourceDirectoryList[key]

                if (sourceValue == null) {
                    removedFiles.add(value.getPath())
                }
            }

            createZipAndReport(newFiles, changedFiles, removedFiles, view.actualVersion.text)

            execAsync {
                model.progress = 100
                model.enabled = true
                model.visible = false
            }
        }
    }

    /**
     * Process file chooser event
     */
    def browseOne = {evt = null ->

        def openResult = view.fileChooserWindow.showOpenDialog(view.rootFrame)
        if (JFileChooser.APPROVE_OPTION == openResult) {
            model.actualVersion = view.fileChooserWindow.selectedFile.toString()
            model.enabled = model.actualVersion && model.oldVersion ? true : false
        }
    }

    //todo: merge browseOne and browseTwo
    def browseTwo = {evt = null ->

        def openResult = view.fileChooserWindow.showOpenDialog(view.rootFrame)
        if (JFileChooser.APPROVE_OPTION == openResult) {
            model.oldVersion = view.fileChooserWindow.selectedFile.toString()
            model.enabled = model.actualVersion && model.oldVersion ? true : false
        }
    }

    /**
     * Open configuration dialog
     */
    def configAction = {evt = null ->
        if (!view.configurationDialog.visible) {
            model.serverListModel.removeAllElements()
            loadIgnoreList().each() {
                model.serverListModel.addElement(it)
            }

            showDialog("configurationDialog", false)
        }
    }

    def doCancel = { hideDialog("configurationDialog") }

    /**
     * Save configuration
     */
    def doSaveConfig = {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.config() {
            ignoreList() {
                model.serverListModel.toArray().each { ignoreItem it }
            }
        }
        new File(CONFIG_FILE).text = writer.toString()
        doCancel()
    }

    /**
     * Handle Enter key on textfield
     */
    def addItemEvent = {evt ->
        if (evt.keyCode == KeyEvent.VK_ENTER) {
            addAction();
        }
    }

    /**
     * Add item to the list
     */
    def addAction = {
        model.serverListModel.addElement(view.ignoreItem.text)
        view.ignoreItem.text = ''
    }

    /**
     * Remove item from the list
     */
    def removeAction = {evt = null ->
        view.ignoreList.selectedValues.each {value ->
            model.serverListModel.removeElement(value)
        }
    }


    private void hideDialog(dialogName) {
        def dialog = view."$dialogName"
        dialog.hide()
    }

    private void showDialog(dialogName, pack = true) {
        def dialog = view."$dialogName"
        if (pack) dialog.pack()
        int x = app.windowManager.windows[0].x + (app.windowManager.windows[0].width - dialog.width) / 2
        int y = app.windowManager.windows[0].y + (app.windowManager.windows[0].height - dialog.height) / 2
        dialog.setLocation(x, y)
        dialog.show()
    }

    /**
     * Check if the file is in ignore list
     * @param ignoreList
     * @param data
     * @return
     */
    def boolean isInIgnoreList(ignoreList, data) {

        for (ignore in ignoreList) {
            if (data.contains(ignore)) {
                return true
            }
        }

        false
    }

    /**
     * Create temporary directory, copy all new and modified files into this directory and finally ZIP this directory.
     * Also fills given text areas in GUI.
     *
     * @param newFiles list with added files
     * @param changedFiles list with changed files
     * @param removedFiles list with removed files
     * @param sourcePath directory with actual version
     * @return
     */
    private void createZipAndReport(newFiles, changedFiles, removedFiles, sourcePath) {
        //create result directory
        def resultDirPath = System.getProperty("user.dir") + "\\result";
        (new AntBuilder()).delete(dir: resultDirPath, failonerror: false)
        def resultDir = new File(resultDirPath);
        resultDir.mkdir();
        execAsync {model.progress = 80}

        //new files
        newFiles.each {item ->
            (new AntBuilder()).copy(file: item, tofile: resultDir.getPath() + item.minus(sourcePath))
            execAsync {
                model.newFiles = model.newFiles + item + '\n'
                model.progress = 85
            }
        }

        //changed files
        changedFiles.each {item ->
            (new AntBuilder()).copy(file: item, tofile: resultDir.getPath() + item.minus(sourcePath))
            execAsync {
                model.changedFiles = model.changedFiles + item + '\n'
                model.progress = 90
            }
        }

        //removed files
        removedFiles.each {item ->
            execAsync {
                model.removedFiles = model.removedFiles + item + '\n'
                model.progress = 95
            }
        }

        //zip directory
        (new AntBuilder()).zip(destfile: "result.zip", basedir: resultDirPath)
    }

    /**
     * Load ignore list from file
     * @return file names which will be ignored
     */
    def loadIgnoreList() {
        def ignoreList = []

        def ignoreFile = new File(CONFIG_FILE)
        if (ignoreFile.exists()) {
            def records = new XmlSlurper().parse(ignoreFile)
            records.ignoreList.ignoreItem.each {
                ignoreList.add(it.text())
            }
        }
        ignoreList
    }

    /**
     * Prepares files for comparing - compute hash of each file and store it
     * @param path root directory(actual or old version path)
     * @param ignoreList list with ignored file names
     * @param step step for progress bar
     * @return how many files needs to be processed to increase progress bar
     */
    def prepareFiles(path, ignoreList, step) {

        def list = [:]

        def counter = 0
        new File(path).eachFileRecurse() {file ->
            if (file.isFile() && !isInIgnoreList(ignoreList, file.getPath())) {
                list[file.getPath().minus(path).replace("\\", "")] = new DataHolder(name: file.getName(), path: file.getPath(), hash: file.md5())
            }

            if (counter.mod(step) == 0) {
                execAsync {model.progress = model.progress + 1}
            }
            counter++
        }
        list
    }
}
