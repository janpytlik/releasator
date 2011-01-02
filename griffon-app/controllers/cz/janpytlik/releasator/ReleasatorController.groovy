package cz.janpytlik.releasator

import javax.swing.JFileChooser

/**
 * Created by IntelliJ IDEA.
 * User: Jan Pytlik
 * Date: 12/21/10
 * Time: 8:23 PM
 */
class ReleasatorController {

  private static final String IGNORELIST_FILE = "ignorelist.config"

  def model
  def view

  def runAction = { evt ->
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
      new File(view.actualVersion.text).eachFileRecurse() {
        counter++
      }
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
  def browseOne = { evt = null ->

    def openResult = view.fileChooserWindow.showOpenDialog(view.rootFrame)
    if (JFileChooser.APPROVE_OPTION == openResult) {
      model.actualVersion = view.fileChooserWindow.selectedFile.toString()
      model.enabled = model.actualVersion && model.oldVersion ? true : false
    }
  }

  //todo: merge browseOne and browseTwo
  def browseTwo = { evt = null ->

    def openResult = view.fileChooserWindow.showOpenDialog(view.rootFrame)
    if (JFileChooser.APPROVE_OPTION == openResult) {
      model.oldVersion = view.fileChooserWindow.selectedFile.toString()
      model.enabled = model.actualVersion && model.oldVersion ? true : false
    }
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
  def createZipAndReport(newFiles, changedFiles, removedFiles, sourcePath) {
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
    def ignoreFile = new File(IGNORELIST_FILE)
    def ignoreList = []

    if (ignoreFile.exists()) {
      ignoreFile.eachLine {
        ignoreList.add(it)
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
