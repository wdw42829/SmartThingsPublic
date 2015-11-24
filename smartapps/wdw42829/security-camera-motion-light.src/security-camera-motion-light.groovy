/**
 *  Security Camera Motion Light
 *
 *  This prevent them from turning off when the timer expires, if they were already turned on
 *
 *  If the switch is already on, if won't be affected by the timer  (Must be turned off manually)
 *  If the switch is toggled while in timeout-mode, it will remain on and ignore the timer (Must be turned of manually)
 *
 *  The timeout perid begins when the contact is closed, or motion stops, so leaving a door open won't start the timer until it's closed.
 *
 *  Author: andersheie@gmail.com
 *  Date: 2014-08-31
 *  Modifid: 23-Nov-15, Doug Wilson
 *		Add button handler (to support IFTTT virtual momentary buton)
 * 		Wait 30s after off before activation again (to avoid light turn off activating camera motion)
 */

definition(
    name: "Security Camera Motion Light",
    namespace: "wdw42829",
    author: "Doug Wilson",
    description: "Turns on a switch for X minutes, then turns it off. Unless, the switch is already on, in which case it stays on. If the switch is toggled while the timer is running, the timer is canceled.",
    category: "Convenience",
    iconUrl: "http://upload.wikimedia.org/wikipedia/commons/6/6a/Light_bulb_icon_tips.svg",
    iconX2Url: "http://upload.wikimedia.org/wikipedia/commons/6/6a/Light_bulb_icon_tips.svg")

preferences {
	section("Turn on when there's movement..."){
		input "motions", "capability.motionSensor", multiple: true, title: "Select Motion Detectors", required: false
	}
	section("Before a contact is opened"){
		input "contacts", "capability.contactSensor", multiple: true, title: "Select Door Contacts", required: false
	}
	section("Or, turn on when one of these momentary buttons is pushed"){
		input "buttons", "capability.momentary", multiple: true, title: "Select Buttons", required: false
	}
	section("And off after no more triggers after..."){
		input "minutes1", "number", title: "Minutes?", defaultValue: "5"
	}
	section("Turn on/off light(s)..."){
		input "switches", "capability.switch", multiple: true, title: "Select Lights"
	}
}


def installed()
{
	subscribe(switches, "switch", switchChange)
	subscribe(motions, "motion", motionHandler)
	subscribe(contacts, "contact", contactHandler)
    subscribe(buttons, "momentary", buttonHandler)

    state.lastActiveAt = now() - 30*1000
}


def updated()
{
	unsubscribe()
	subscribe(motions, "motion", motionHandler)
    subscribe(switches, "switch", switchChange)
	subscribe(contacts, "contact", contactHandler)
    subscribe(buttons, "momentary", buttonHandler)

	state.lastActiveAt = now() - 30*1000 
}

def switchChange(evt) {
	log.debug "SwitchChange: $evt.name: $evt.value"
	if (evt.value == "off") {
		state.lastActiveAt = now()
        log.debug "Un-Scheduling lights to turn off"
		unschedule (turnOff)
    }
}

def contactHandler(evt) {
	log.debug "contactHandler: $evt.name: $evt.value"

    // only if motion is activate (i.e. motion and door sensor together)
	state.motionStatus = "inactive";
	for (motion in motions) {
    	if (motion.currentState("motion")?.value == "active") 
        	state.motionStatus = "active"
    }
    if ((evt.value == "open") && (state.motionStatus == "active")) {
		log.debug "Turning on lights by contact opening"
        switches.on()
    } else if (evt.value == "closed") {
		log.debug "Scheduling lights to turn off by contact opening"
		runIn(minutes1*60,turnOff)
	}
}

def buttonHandler(evt) {
	log.debug "buttonHandler: $evt.name: $evt.value"
    
    // only activate if 30 seconds have elapsed since switch was turned off
    // when using a camera, this allows for light turning off to not activate motion detection again
    if (state.lastActiveAt == null) state.lastActiveAt = now()
	def elapsed = now() - state.lastActiveAt
	def threshold = 1000 * 30 // 30 seconds
	if (elapsed >= threshold) {
		log.debug "Turning on lights by button push"
		switches.on()
		runIn(minutes1*60,turnOff);
	} else {
    	log.debug "Sufficient time has not elapsed since lights were last turned off"
    }
}

def motionHandler(evt) {
	log.debug "motionHandler: $evt.name: $evt.value"
}

def turnOff() {
	log.debug "turning off lights"
    switches.off()
    state.lastActiveAt = now()
}
    