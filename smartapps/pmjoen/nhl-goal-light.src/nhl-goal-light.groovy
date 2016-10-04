/**
 *  Hockey Goal Red Light
 *  
 *  Description:
 *  This app was built to provide a solution similar to Budweiser Red Light
 *  Select your favorite NHL team to turn on an outlet for 5 seconds then off.
 *  
 *  Version: 1.0 - Initial Version
 */

definition(
    name: "NHL Goal Light",
    namespace: "pmjoen",
    author: "pmjoen@yahoo.com",
    description: "This app was built to provide a solution similar to Budweiser Goal Red Light, select your favorite NHL team and turn on a switch, outlet for 5 seconds then off.",
    category: "My Apps",
    iconUrl: "https://cloud.githubusercontent.com/assets/8125308/19090015/45a923de-8a42-11e6-8ac4-67b48a7e21bc.png",
    iconX2Url: "https://cloud.githubusercontent.com/assets/8125308/19090033/536501dc-8a42-11e6-8006-8e8861deb9fc.png",
    iconX3Url: "https://cloud.githubusercontent.com/assets/8125308/19090033/536501dc-8a42-11e6-8006-8e8861deb9fc.png")

preferences {
     section("NHL Team"){
         input "team", "enum", title: "Team Selection", required: true, displayDuringSetup: true, options: ["Avalanche","Blackhawks","Blue Jackets","Blues","Bruins","Canadiens","Canucks","Capitals","Coyotes","Devils","Ducks","Flames","Flyers","Hurricanes","Islanders","Jets","Kings","Lighting","Maple Leafs","Oilers","Panthers","Penguins","Predators","Rangers","Red Wings","Sabres","Senators","Sharks","Stars","Wild"]
    }
    section (Outlet){
         input "Controlled Devices", "capability.switch", title: "Devices Selection", required: true, multiple: true, displayDuringSetup: true
    }
    section("Debug Logging") {
         input "debug_pref", "bool", title: "Debug Logging", defaultValue: "false", displayDuringSetup: true
    }
}

def installed() {
   if (settings.debug_pref == true) log.debug "Installed with settings: ${settings}"
   initialize()
}

def updated() {
	if (settings.debug_pref == true) log.debug "Updated with settings: ${settings}"
	unschedule()
	unsubscribe()
	initialize()
}

def initialize() {
	log.info "NHL Goal Light ${textVersion()} ${textCopyright()}"
	if (settings.debug_pref == true) log.debug "Initialize with settings: ${settings}"
	subscribe(master, "switch.on", switchOnHandler)
	subscribe(master, "switch.off", switchOffHandler)
    subscribe(location, "sunset", pollRestart)
	subscribe(location, "sunrise", pollRestart)
	schedule("22 4 0/1 1/1 * ? *", pollOff)
	pollOff
}

def params = [
    uri: "http://scores.nbcsports.msnbc.com/ticker/data/gamesMSNBC.js.asp?jsonp=true&sport=NHL&period=%d'",
    path: "/get"
]

try {
    httpGet(params) { resp ->
        // iterate all the headers
        // each header has a name and a value
        resp.headers.each {
           if (settings.debug_pref == true) log.debug "${it.name} : ${it.value}"
        }

        // get an array of all headers with the specified key
        def theHeaders = resp.getHeaders("Content-Length")

        // get the contentType of the response
        if (settings.debug_pref == true) log.debug "response contentType: ${resp.contentType}"

        // get the status code of the response
        if (settings.debug_pref == true) log.debug "response status code: ${resp.status}"

        // get the data from the response body
        if (settings.debug_pref == true) log.debug "response data: ${resp.data}"
    }
} catch (e) {
    log.error "something went wrong: $e"
}

def switchOnHandler(evt) {
	if (settings.debug_pref == true) log.debug "Switching on"
}

def switchOffHandler(evt) {
    if (settings.debug_pref == true) log.debug "Switching off"
}

def pollOff() {
    def offSwitch1 = settings.switch1.currentSwitch.findAll { switchVal ->
		switchVal == "off" ? true : false
	}
	settings.switch1.each() {
		if (it.currentSwitch == "off") {
			state.pollState = now()
			it.poll()
            runIn(3660, pollRestart, [overwrite: true])
		}
	}
	if (offSwitch1.size() == 0) {
		unschedule(pollOff)
	}
}

def pollOn() {
	def onSwitch1 = settings.switch1.currentSwitch.findAll { switchVal ->
		switchVal == "on" ? true : false
	}
	settings.switch1.each() {
		if (it.currentSwitch == "on") {
			state.pollState = now()
			it.poll()
			runIn(80, pollRestartOn, [overwrite: true])
		}
	}
	if (onSwitch1.size() == 0) {
		unschedule(pollOn)
	}
}

def pollErr() {
	def errSwitch1 = settings.switch1.currentStatus.findAll { switchVal ->
		switchVal == "error" ? true : false
	}
	settings.switch1.each() {
		if (it.currentStatus == "error") {
			state.pollState = now()
			it.poll()
		}
	}
	if (errSwitch1.size() == 0) {
		unschedule(pollErr)
	}
}
def pollRestartOn() {
	def t = now() - state.pollState
		if (t > 660000) {
			unschedule(pollOn)
			schedule("16 0/1 * 1/1 * ?", pollOn)
			sendEvent(linkText:app.label, name:"Poll", value:"Restart",descriptionText:"Polling Restarted", eventType:"SOLUTION_EVENT", displayed: true)
			log.trace "Polling Restarted"
        }
}

def pollRestart(evt) {
	def t = now() - state.pollState
		if (t > 4200000) {
			unschedule(pollOff)
			schedule("23 4 0/1 1/1 * ? *", pollOff)
			sendEvent(linkText:app.label, name:"Poll", value:"Restart",descriptionText:"Polling Restarted", eventType:"SOLUTION_EVENT", displayed: true)
			log.trace "Polling Restarted"
        }
}

private def textVersion() {
    def text = "Version 1.0"
}

private def textCopyright() {
    def text = "Copyright © 2016 pmjoen"
}