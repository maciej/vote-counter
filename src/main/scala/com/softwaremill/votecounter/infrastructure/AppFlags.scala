package com.softwaremill.votecounter.infrastructure

import com.softwaremill.votecounter.db.{Flag, FlagsDao}

class AppFlags(flagDao: FlagsDao) {

  import AppFlags._

  private def isBooleanFlagSet(flagName: String, defaultValue: Boolean = false) = {
    val flagOpt = flagDao.find(flagName)
    flagOpt match {
      case Some(flag) => flag.value.toBoolean
      case None => defaultValue
    }
  }

  def isTestDataInserted = isBooleanFlagSet(TestDataInserted)

  def flagTestDataInserted() = {
    flagDao.set(Flag(TestDataInserted, "true"))
  }

  def isConferenceDataInitialized = isBooleanFlagSet(ConferenceDataInitialized)

  def flagConferenceDataInserted() = {
    flagDao.set(Flag(ConferenceDataInitialized, "true"))
  }

}

private[infrastructure] object AppFlags {
  val TestDataInserted = "test.data"
  val ConferenceDataInitialized = "conference.data"
}
