package cz.janpytlik.releasator

import groovy.beans.Bindable

/**
 * Created by IntelliJ IDEA.
 * User: Jan Pytlik
 * Date: 12/21/10
 * Time: 8:23 PM
 */

class ReleasatorModel {

  @Bindable def actualVersion
  @Bindable def oldVersion

  @Bindable def newFiles
  @Bindable def changedFiles
  @Bindable def removedFiles

  @Bindable def progress
  @Bindable def enabled = false
  @Bindable def visible = false

}