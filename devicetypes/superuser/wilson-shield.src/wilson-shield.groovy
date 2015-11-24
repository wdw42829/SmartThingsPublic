/*
 *  Wilson Shield
 *
 *  Author: Doug Wilson
 *  Date: 2015-Nov-22
 */
 
 
metadata {
	definition (name: "Wilson Shield", author: "Doug Wilson") {
            capability "Contact Sensor"
            capability "Motion Sensor"
            capability "Alarm"
			capability "Refresh"
        	attribute "Armed","string"
	}

	tiles {
		standardTile("Front", "device.FrontDoorContact", width: 1, height: 1) {
			state("open", label:"Front", icon:"st.contact.contact.open", backgroundColor:"#ffa81e")
			state("closed", label:"Front", icon:"st.contact.contact.closed", backgroundColor:"#79b821")
		}
		standardTile("Back", "device.BackDoorContact", width: 1, height: 1) {
			state("open", label:"Back", icon:"st.contact.contact.open", backgroundColor:"#ffa81e")
			state("closed", label:"Back", icon:"st.contact.contact.closed", backgroundColor:"#79b821")
		}
		standardTile("Foyer", "device.FoyerMotion", width: 1, height: 1) {
			state("active", label:"Foyer", icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
			state("inactive", label:"Foyer", icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
		}
		standardTile("Loft", "device.LoftMotion", width: 1, height: 1) {
			state("active", label:"Loft", icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
			state("inactive", label:"Loft", icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
		}
		standardTile("Basement", "device.BasementMotion", width: 1, height: 1) {
			state("active", label:"Basement", icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
			state("inactive", label:"Basement", icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
		}
		valueTile("Armed", "device.Armed", inactiveLabel: false) {
			state "armed", label:'${currentValue}', unit:""
		}
		standardTile("Alarm", "device.Alarm", width: 1, height: 1) {
			state "off", label:'off', icon:"st.alarm.alarm.alarm", backgroundColor:"#ffffff"
            state "strobe", label:'', icon:"st.secondary.strobe", backgroundColor:"#cccccc"
            state "siren", label:'siren!', icon:"st.alarm.beep.beep", backgroundColor:"#e86d13"
			state "both", label:'alarm!', icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13"
		}
	    standardTile("Refresh", "device.Armed", inactiveLabel: false, decoration: "flat") {
    	    state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
	    }
        
		main("Alarm","Foyer")
		details(["Armed","Front","Back","Foyer","Loft","Basement","Alarm","Refresh"])
	}
}

def refresh() {
    zigbee.smartShield(text: "refresh").format()
}

def parse(String description) {

	log.debug "Parsing '${description}'"
    def msg = zigbee.parse(description)?.text
    log.debug "Parse got '${msg}'"

    def parts = msg.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null

    name = value != "ping" ? name : null
    
    def result = createEvent(name: name, value: value)

    log.debug result
    result
}