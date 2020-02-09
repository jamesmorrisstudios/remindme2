package us.jamesmorrisstudios.rrm2.storage.schedule

import android.content.Context

/**
 * Schedules a series of wake times for a given reminder guid.
 *
 * Can support a variety of scheduling types.
 */
interface Schedule

/**
 *
 */
class ScheduleImpl(private val context: Context) : Schedule