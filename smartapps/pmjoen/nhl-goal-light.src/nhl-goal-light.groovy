/**
 *  NHL Goal Light
 *  
 *  Description:
 *  This app was built to provide a solution similar to Budweiser Red Light.
 *
 *  When your hockey team scores, The app will turn on/of switches. (Use a siren light)
 *  Flash a set of switches.  Play your teams goal score song (Lets go Blackhawks).
 *
 * 	Thanks to the following for their help and input in the development:
 *  Patrick Stuart
 *  Jim Anderson (Smartthings)
 * 
 *  Version: 1.0 - Initial Version
 */

include 'asynchttp_v1'
import groovy.json.JsonSlurper

definition(
    name: "NHL Goal Light",
    namespace: "pmjoen",
    author: "pmjoen@yahoo.com",
    description: "Select your favorite NHL team and turn on/off a switch, flash a switch, and play their song when they score.",
    category: "My Apps",
    iconUrl: "https://cloud.githubusercontent.com/assets/8125308/19090015/45a923de-8a42-11e6-8ac4-67b48a7e21bc.png",
    iconX2Url: "https://cloud.githubusercontent.com/assets/8125308/19090033/536501dc-8a42-11e6-8006-8e8861deb9fc.png",
    iconX3Url: "https://cloud.githubusercontent.com/assets/8125308/19090033/536501dc-8a42-11e6-8006-8e8861deb9fc.png")

preferences {
     section("NHL Team"){
         input "team", "enum", title: "Team Selection", required: true, displayDuringSetup: true, options: ["Avalanche","Blackhawks","Blue Jackets","Blues","Bruins","Canadiens","Canucks","Capitals","Coyotes","Devils","Ducks","Flames","Flyers","Hurricanes","Islanders","Jets","Kings","Lighting","Maple Leafs","Oilers","Panthers","Penguins","Predators","Rangers","Red Wings","Sabres","Senators","Sharks","Stars","Wild"]
    }
    // Goal Light(s) on/off
    section("Switches To Turn On"){
         input "controlledDevices", "capability.switch", title: "Devices Selection", required: false, multiple: true, displayDuringSetup: true
         input "delay", "enum", title: "Delay turning on (seconds)", required: true, defaultValue: "5", multiple: false, displayDuringSetup: true, options: ["0","1","2","3","4","5","6","7","8","9","10"]
         input "lengthOn", "enum", title: "Turn Off After (seconds)", required: true, defaultValue: "5", multiple: false, displayDuringSetup: true, options: ["1","2","3","4","5","6","7","8","9","10"]
    }
    // Goal Flashing Light(s)
	section("Switches To Flash"){
		input "flashDevices", "capability.switch", title: "These lights", multiple: true
		input "numFlashes", "number", title: "This number of times", defaultValue: "3", required: false
        input "onFor", "number", title: "Flash On for", defaultValue: "1000", required: false
		input "offFor", "number", title: "Flash Off for", defaultValue: "1000", required: false
	}
    // Goal Song speaker
    section (Speaker){
         input "sound", "capability.musicPlayer", title: "Music Devices Selection", required: false, displayDuringSetup: true
         input "soundDuration", "enum", title: "Sound duration(seconds)", required: false, defaultValue: "5", multiple: false, displayDuringSetup: true, options: ["1","2","3","4","5","6","7","8","9","10"]
    }
//    section("Run in modes"){
//    	input "modes", "mode", title: "Only when mode is", multiple: true, required: false
//    }
    section("Debug Logging") {
         input "debug_pref", "bool", title: "Debug Logging", defaultValue: "false", displayDuringSetup: true
    }
}

private getModeOk() {
	def result = !modes || modes.contains(location.mode)
//	log.trace "modeOk = $result"
	result
}

def installed() {
   if (settings.debug_pref == true) log.debug "Installed with settings: ${settings}"
   initialize()
   	subscribeToEvents()
//   schedule("0 0 5 * * ? *")
}

def updated() {
	if (settings.debug_pref == true) log.debug "Updated with settings: ${settings}"
	unschedule()
	unsubscribe()
    subscribeToEvents()
    initialize()
}

def subscribeToEvents() {
	loadText()
}

def initialize() {
	log.info "NHL Goal Light ${textVersion()} ${textCopyright()}"
	log.debug "Initialize with settings: ${settings}"
    runScoreUpdate()
}

def runScoreUpdate() {
	updateScores()
}

	// Call service for today's NHL scores
def updateScores() {
    def todaysDate = new Date().format('yyyyMMdd')
    def params = [uri: "http://scores.nbcsports.msnbc.com/ticker/data/gamesMSNBC.js.asp?jsonp=true&sport=NHL&period=${todaysDate}"] 
    if (settings.debug_pref == true) log.debug "Requesting ${settings.team} scores for ${todaysDate}"
    asynchttp_v1.get(handler, params)
}

	// Handle response data
def handler(resp, data) {
    // get the status code of the response
    if (settings.debug_pref == true) log.debug "response status code: ${resp.status}"
    log.debug "data passed to response handler: $data"
    if (resp.status == 200){
    	log.debug "raw response: ${resp.getData()}"
        
//    	log.debug "Games: ${resp.games}"
//    	log.debug "${settings.team} game time (gamestate.gametime)"
//    	log.debug "${settings.team} game time DST (gamestate.is-dst) and (gamestate.is-world-dst)"
//    	log.debug "${settings.team} game status (gamestate.status)
//    	log.debug "(ticker-entry.gamecode)"
//		def gamecode = (ticker-entry.gamecode)
//    	log.debug "(ticker-entry.gametype)"	
//		def visitorcity = (visiting-team.display_name)
//    	log.debug "(visiting-team.display_name)"
//		def visitorname = (visiting-team.nickname)
//    	log.debug "(visiting-team.nickname)"
//		def homecity = (home-team.display_name)	
//    	log.debug "(home-team.display_name)"	
//		def homename = (home-team.nickname)
//    	log.debug "(home-team.nickname)"
//    	log.debug "(score.???)"
//    	log.debug "${settings.team} score ${resp.games}"
    }
    else {
		log.debug "Request Failed To Return Data"
	}
}

def parse(String description) {
    log.debug "Parsing string"
	def responsedata = resp.toString()
//    log.debug "Response string: ${responsedata}"
}

//def gameDay{
	// If team plays today get updates every 30 minutes
//    schedule("0 30 * * * ?", updateScores)
//}

//def preGame{
	// If game starts in 1 hour get updates every 10 minutes
//    schedule("0 10 * * * ?", updateScores)
//}

//def gameTime{
	// If game starts in Get updates every 5 seconds
    // runin(5,(updateScores())
//}

//def postGame{
	// Stop getting updates
//    unschedule(scheduledHandler)
//}

	// Goal scored action handler
def goalScored() {
	//  Test the events
//	if (settings.testEvent == true)
	if (settings.controlledDevices != null) {
        if (settings.debug_pref == true) log.debug "$Delay on for {settings.delay} seconds"
    	runIn({settings.delay},(goalScoredLightOn()))
    }
    if (settings.flashDevices != null){
    goalFlashLights()
    }
//    if (settings.sound != null){
//    takeAction()
//    }
}

def goalScoredLightOn() {
    if (settings.debug_pref == true) log.debug "Switching on"
    switches.on()
    runIn({settings.lengthOn},(goalScoredLightOff()))
}

def goalScoredLightOff() {
    if (settings.debug_pref == true) log.debug "Switching off"
    switches.off()
}

private goalFlashLights() {
    if (settings.debug_pref == true) log.debug "Flashing lights"
	def doFlash = true
	def onFor = onFor ?: 1000
	def offFor = offFor ?: 1000
	def numFlashes = numFlashes ?: 3

	log.debug "LAST ACTIVATED IS: ${state.lastActivated}"
	if (state.lastActivated) {
		def elapsed = now() - state.lastActivated
		def sequenceTime = (numFlashes + 1) * (onFor + offFor)
		doFlash = elapsed > sequenceTime
		log.debug "DO FLASH: $doFlash, ELAPSED: $elapsed, LAST ACTIVATED: ${state.lastActivated}"
	}

	if (doFlash) {
		log.debug "FLASHING $numFlashes times"
		state.lastActivated = now()
		log.debug "LAST ACTIVATED SET TO: ${state.lastActivated}"
		def initialActionOn = flashDevices.collect{it.currentSwitch != "on"}
		def delay = 0L
		numFlashes.times {
			log.trace "Switch on after  $delay msec"
			flashDevices.eachWithIndex {s, i ->
				if (initialActionOn[i]) {
					s.on(delay: delay)
				}
				else {
					s.off(delay:delay)
				}
			}
			delay += onFor
			log.trace "Switch off after $delay msec"
			flashDevices.eachWithIndex {s, i ->
				if (initialActionOn[i]) {
					s.off(delay: delay)
				}
				else {
					s.on(delay:delay)
				}
			}
			delay += offFor
		}
	}
}

private takeAction(evt) {
		sonos.playSoundAndTrack(state.sound.uri, state.sound.duration)
}

private loadText() {
	switch ( actionType) {
		case "Goal Scored":
        	state.sound = [uri: "http://s3.amazonaws.com/nhlgoallightsong/${settings.team}.mp3", duration: "${settings.soundDuration}"]
            if (settings.debug_pref == true) log.debug "Playing goal song for ${settings.team} for ${settings.soundDuration} seconds"
			break;
		default:
			if (message) {
				state.sound = textToSpeech(message instanceof List ? message[0] : message) // not sure why this is (sometimes) needed)
			}
			else {
            	log.debug "No ${settings.team} song available for $app.label Smart App"
        		state.sound = [uri: "http://s3.amazonaws.com/nhlgoallightsong/Siren.mp3${settings.team}", duration: "${settings.soundDuration}"]
			}
			break;
	}
}

private def textVersion() {
    def text = "Version 1.0"
}

private def textCopyright() {
    def text = "Copyright © 2016 pmjoen"
}