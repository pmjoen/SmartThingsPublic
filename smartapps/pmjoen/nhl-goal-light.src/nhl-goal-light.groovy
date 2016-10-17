/**
 *  Hockey Goal Red Light
 *  
 *  Description:
 *  This app was built to provide a solution similar to Budweiser Red Light.
 *  Select your favorite NHL team to turn on switches when your team scores
 *  And then off a few seconds later. (Use a siren light)
 *
 *  Future solution will be to add Sonos to play your teams goal song.
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
         input "Team", "enum", title: "Team Selection", required: true, displayDuringSetup: true, options: ["Avalanche","Blackhawks","Blue Jackets","Blues","Bruins","Canadiens","Canucks","Capitals","Coyotes","Devils","Ducks","Flames","Flyers","Hurricanes","Islanders","Jets","Kings","Lighting","Maple Leafs","Oilers","Panthers","Penguins","Predators","Rangers","Red Wings","Sabres","Senators","Sharks","Stars","Wild"]
    }
    section(Switches){
         input "Controlled Devices", "capability.switch", title: "Devices Selection", required: true, multiple: true, displayDuringSetup: true
    }
    // implement options speaker for audio later
//    section (Speaker){
//         input "Sound", "capability.audioNotification", title: "Devices Selection", required: false, displayDuringSetup: true
//    }
	section(Delay){
         input "Delay", "enum", title: "Delay Action In Seconds", required: true, multiple: false, displayDuringSetup: true, options: ["1","2","3","4","5","6","7","8","9","10"]
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
	log.debug "Initialize with settings: ${settings}"
    runScoreUpdate()
}

def runScoreUpdate() {
	updateScores()
}

def updateScores() {
    def todaysDate = new Date().format('yyyyMMdd')
    log.debug "Date is: ${todaysDate}"
	if (settings.debug_pref == true) log.debug "Requesting NHL Service"
	def params = [
		uri: "http://scores.nbcsports.msnbc.com/ticker/data/gamesMSNBC.js.asp?jsonp=true&sport=NHL&period=${todaysDate}",
	]
    log.debug "URL: ${params}"
// def dateToday = Date.parse("yyyyMMdd", dateString)

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
        	log.debug "Response data: ${resp.data}" 
            log.debug "Games: ${games}"
    	}
 	}catch (e) {
    	log.error "something went wrong: $e"
	}
}

def goalScoredOn() {
	if (settings.debug_pref == true) log.debug "Switching on"
    switches.on()
}

def goalScoredOff() {
    if (settings.debug_pref == true) log.debug "Switching off"
    switches.off()
}

private def textVersion() {
    def text = "Version 1.0"
}

private def textCopyright() {
    def text = "Copyright © 2016 pmjoen"
}