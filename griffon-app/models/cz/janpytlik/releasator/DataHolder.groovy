package cz.janpytlik.releasator

/**
 * Created by IntelliJ IDEA.
 * User: Jan Pytlik
 * Date: 1/1/11
 * Time: 7:44 PM
 */

class DataHolder {
  String name
  String path
  String hash

  def result() {
    println name + "|" + path + " -- >" + hash
  }
}
