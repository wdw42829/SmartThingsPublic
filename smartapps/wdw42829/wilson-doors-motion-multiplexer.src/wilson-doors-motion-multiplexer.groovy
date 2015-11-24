/**
 *  ST_Anything Doors Multiplexer - ST_Anything_Doors_Multiplexer.smartapp.groovy
 *
 *  Copyright 2015 Daniel Ogorchock
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2015-01-10  Dan Ogorchock  Original Creation
 *    2015-01-11  Dan Ogorchock  Reduced unnecessary chatter to the virtual devices
 *    2015-01-18  Dan Ogorchock  Added support for Virtual Temperature/Humidity Device
 *    2015-01-18  Gareth Jeanne  Modified code to only use my temperature sensors
 *	  2025-11-22  Doug Wilson    Modified to work with my alarm system
 */
 
definition(
    name: "Wilson Doors/Motion Multiplexer",
    namespace: "wdw42829",
    author: "Doug Wilson",
    description: "Connects single Arduino with multiple ContactSensor devices to their virtual device counterparts.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {

	section("Select the House Doors (Virtual Contact Sensor devices)") {
		input ("frontdoor", title: "Virtual Contact Sensor for Front Door", "capability.contactSensor")
		input ("backdoor", title: "Virtual Contact Sensor for Back Door", "capability.contactSensor")
	}

	section("Select the House Motion (Virtual Motion Sensor devices)") {
		input ("foyermotion", title: "Virtual Motion Sensor for Front Door", "capability.motionSensor")
	}

	section("Select the Arduino ST_Anything_Doors device") {
		input "arduino", "capability.contactSensor"
    }    
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	subscribe()
}

def subscribe() {
    
    subscribe(arduino, "FrontDoorContact.open", frontDoorOpen)
    subscribe(arduino, "FrontDoorContact.closed", frontDoorClosed)
    
    subscribe(arduino, "BackDoorContact.open", backDoorOpen)
    subscribe(arduino, "BackDoorContact.closed", backDoorClosed)

    subscribe(arduino, "FoyerMotion.active", foyerMotion)
    subscribe(arduino, "FoyerMotion.inactive", foyerMotion)

}

// --- Front Door --- 
def frontDoorOpen(evt)
{
    if (frontdoor.currentValue("contact") != "open") {
    	log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	frontdoor.open()
    }
}

def frontDoorClosed(evt)
{
    if (frontdoor.currentValue("contact") != "closed") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	frontdoor.close()
    }
}

// --- Back Door --- 
def backDoorOpen(evt)
{
    if (backdoor.currentValue("contact") != "open") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	backdoor.open()
    }
}

def backDoorClosed(evt)
{
    if (backdoor.currentValue("contact") != "closed") {
		log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"
    	backdoor.close()
	}
}

// --- Foyer Motion --- 
def foyerMotion(evt)
{
	log.debug "arduinoevent($evt.name: $evt.value: $evt.deviceId)"

    if (evt.value == "active")
    	foyermotion.active()
    if (evt.value == "inactive")
    	foyermotion.inactive()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}