package us.jamesmorrisstudios.rrm2.controller

import us.jamesmorrisstudios.rrm2.util.Guid

private const val AlarmRegular = "AlarmRegular"
private const val AlarmSnooze = "AlarmSnooze"
private const val AlarmAutoSnooze = "AlarmAutoSnooze"

/**
 * Returns if this is a guid with the Alarm Regular Modifier.
 */
fun Guid.isModifierAlarmRegular(): Boolean {
    return this.isModifier(AlarmRegular)
}

/**
 * Returns this guid with the Alarm Regular Modifier.
 */
fun Guid.withModifierAlarmRegular(): Guid {
    return this.withModifier(AlarmRegular)
}

/**
 * Returns if this is a guid with the Alarm Snooze Modifier.
 */
fun Guid.isModifierAlarmSnooze(): Boolean {
    return this.isModifier(AlarmSnooze)
}

/**
 * Returns this guid with the Alarm Snooze Modifier.
 */
fun Guid.withModifierAlarmSnooze(): Guid {
    return this.withModifier(AlarmSnooze)
}

/**
 * Returns if this is a guid with the Alarm Auto Snooze Modifier.
 */
fun Guid.isModifierAlarmAutoSnooze(): Boolean {
    return this.isModifier(AlarmAutoSnooze)
}

/**
 * Returns this guid with the Alarm Auto Snooze Modifier.
 */
fun Guid.withModifierAlarmAutoSnooze(): Guid {
    return this.withModifier(AlarmAutoSnooze)
}
