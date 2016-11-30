/**
 *  Thinking Cleaner
 *
 *	Smartthings Devicetype
 *
 *  If you like this device, please donate the developers perfered local charity "RMHC of Chicagoland & Northwest Indiana":
 *  https://secure2.convio.net/rmhci/site/Donation2;jsessionid=00000000.app261a?idb=2061823896&DONATION_LEVEL_ID_SELECTED=1&df_id=1663&mfc_pref=T&1663.donation=form1&NONCE_TOKEN=43EC125CCF455C2098CE98B9235BF56D&idb=0
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
 *  Version 2.0 - Re-release of application with new ownership and community support
 */
 
import groovy.json.JsonSlurper

metadata {
	definition (name: "Thinking Cleaner", namespace: "pmjoen", author: "Patrick Mjoen") {
		capability "Battery"
		capability "Polling"
		capability "Refresh"
		capability "Switch"
		capability "Tone"
        capability "Sensor"
        capability "Actuator"
        
		command "refresh"      

		attribute "network","string"
		attribute "bin","string"
	}
    
	preferences {
    	section("Roomba Network Information") {
			input("ip", "text", title: "IP Address", description: "Your Thinking Cleaner Address", required: true, displayDuringSetup: true)
			input("port", "number", title: "Port Number", description: "Your Thinking Cleaner Port Number (Default:80)", defaultValue: "80", required: true, displayDuringSetup: true)
        }
        section("Cleaning Mode") {
        	input "cleanmode", "enum", title: "Clean Mode", defaultValue: "Clean", required: true, displayDuringSetup: true, options: ["Clean","Max Clean"]
        }
        section("Debug Logging") {
        	input "debug_pref", "bool", title: "Debug Logging", defaultValue: "false", displayDuringSetup: true
        }
	}

	tiles {
		multiAttributeTile(name:"status", type: "lighting", width: 3, height: 2) {
			tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
				attributeState "default", label:'checking', icon: "https://cloud.githubusercontent.com/assets/8125308/18171190/a1846212-7026-11e6-8cd9-b9540ca93720.png"
				attributeState "charging", label:'${currentValue}', action:"switch.on", icon: "https://cloud.githubusercontent.com/assets/8125308/18171429/97278a78-7027-11e6-840e-a4cc87642bd0.png", backgroundColor: "#D3D3D3", nextState:"cleaning"
				attributeState "pluggedcharge", label:'charging', icon: "https://cloud.githubusercontent.com/assets/8125308/18554014/65587d16-7b28-11e6-86e5-40c9b0c3ce81.png", backgroundColor: "#ffffff"
				attributeState "plugged", label:'plugged in', icon: "https://cloud.githubusercontent.com/assets/8125308/18553988/52cef396-7b28-11e6-8230-362334f8ead4.png", backgroundColor: "#ffffff"
				attributeState "cleaning", label:'${currentValue}', action:"switch.off", icon: "https://cloud.githubusercontent.com/assets/8125308/18171190/a1846212-7026-11e6-8cd9-b9540ca93720.png", backgroundColor: "#79b821", nextState:"docking"
				attributeState "docked", label:'${currentValue}', action:"switch.on", icon: "https://cloud.githubusercontent.com/assets/8125308/18171190/a1846212-7026-11e6-8cd9-b9540ca93720.png", backgroundColor: "#ffffff", nextState:"cleaning"
				attributeState "docking", label:'${currentValue}', action:"switch.on", icon: "https://cloud.githubusercontent.com/assets/8125308/18171190/a1846212-7026-11e6-8cd9-b9540ca93720.png", backgroundColor: "#D3D3D3", nextState:"docked"
				attributeState "off", label:'${currentValue}', action:"switch.on", icon: "https://cloud.githubusercontent.com/assets/8125308/18171190/a1846212-7026-11e6-8cd9-b9540ca93720.png", backgroundColor: "#D3D3D3", nextState:"cleaning"				
                attributeState "error", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18171468/c406f4e8-7027-11e6-9ead-d133313cc0f2.png", backgroundColor: "#FF0000"
				attributeState "paused", label:'${currentValue}', action:"switch.on", icon: "https://cloud.githubusercontent.com/assets/8125308/18171484/d9535a80-7027-11e6-8bba-f90341eb4a86.png", backgroundColor: "#D3D3D3", nextState:"cleaning"
				attributeState "delayed", label:'${currentValue}', action:"switch.on", icon: "https://cloud.githubusercontent.com/assets/8125308/18171514/ed458b94-7027-11e6-9cf9-3060d3d4743a.png", backgroundColor: "#D3D3D3", nextState:"cleaning"
				attributeState "findme", label:'${currentValue}', icon: "https://cloud.githubusercontent.com/assets/8125308/18171531/010647e0-7028-11e6-92c3-46ecc2a8353e.png", backgroundColor: "#D3D3D3"
			 }
             tileAttribute ("device.battery", key: "SECONDARY_CONTROL") {
				attributeState "batery", label:'${currentValue}% Battery'
			}
         }
        standardTile("beep", "device.beep", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
			state "inactive", label:'disabled', icon:"https://cloud.githubusercontent.com/assets/8125308/18181294/36550e18-7050-11e6-83f8-b926fefe329e.png", backgroundColor:"#D3D3D3"
			state "beep", label:'find', action:"tone.beep", icon:"https://cloud.githubusercontent.com/assets/8125308/18181294/36550e18-7050-11e6-83f8-b926fefe329e.png", backgroundColor:"#ffffff"
		}
        standardTile("network", "device.network", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
			state ("default", label:'Link', icon: "st.unknown.unknown.unknown")
			state ("Connected", label:'Conected', icon: "https://cloud.githubusercontent.com/assets/8125308/18180862/59c9a3c4-704e-11e6-872e-5b1230bc4a5f.png", backgroundColor: "#79b821")
			state ("Not Connected", label:'No Link', icon: "https://cloud.githubusercontent.com/assets/8125308/18180948/bfc7744e-704e-11e6-9c5f-c17b75e32f87.png", backgroundColor: "#ff0000")
		}
        standardTile("bin", "device.bin", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
			state ("default", label:'Bin', icon: "st.unknown.unknown.unknown")
			state ("empty", label:'Bin Empty', icon: "https://cloud.githubusercontent.com/assets/8125308/18180772/eb692f80-704d-11e6-9a10-76f63cf69c95.png", backgroundColor: "#79b821")
			state ("full", label:'Bin Full', icon: "https://cloud.githubusercontent.com/assets/8125308/18180772/eb692f80-704d-11e6-9a10-76f63cf69c95.png", backgroundColor: "#bc2323")
		}
		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false, decoration: "flat") {
			state("default", action:"refresh.refresh", icon:"st.secondary.refresh")
		}
        standardTile("clean", "device.switch", width: 1, height: 1, inactiveLabel: false, canChangeIcon: false, decoration: "flat") {
			state("on", label: 'dock', action: "switch.off", icon: "st.Appliances.appliances13", backgroundColor: "#79b821", nextState:"off")
			state("off", label: 'clean', action: "switch.on", icon: "st.Appliances.appliances13", backgroundColor: "#79b821", nextState:"on")
		}

        main("status")
			details(["status","network","bin","beep","refresh"])        
		}
}

def parse(String description) {
	def map
	def headerString
	def bodyString
	def slurper
	def result
        
	map = stringToMap(description)
	headerString = new String(map.headers.decodeBase64())
	if (headerString.contains("200 OK")) {
		bodyString = new String(map.body.decodeBase64())
		slurper = new JsonSlurper()
		result = slurper.parseText(bodyString)
// Shows JSON response
		if (settings.debug_pref == true) log.debug "Clean state ${result.power_status.cleaner_state} status ${result.tc_status.cleaning}"
        if (settings.debug_pref == true) log.info "Firmware Version ${result.firmware.version}"
        if (settings.debug_pref == true) log.info "Model Number ${result.tc_status.modelnr}"
        if (settings.debug_pref == true) log.debug bodyString
		switch (result.action) {
			case "command":
				sendEvent(name: 'network', value: "Connected" as String)
			break;
			case "full_status":
				sendEvent(name: 'network', value: "Connected" as String)
				sendEvent(name: 'battery', value: result.power_status.battery_charge as Integer)
        		if (settings.debug_pref == true) log.debug "Bin status value ${result.tc_status.bin_status}"
			switch (result.tc_status.bin_status) { 
				case "0":
					sendEvent(name: 'bin', value: "empty" as String) 
                    if (settings.debug_pref == true) log.debug "Bin status 'Empty'"
				break;
				case "1":
					sendEvent(name: 'bin', value: "full" as String)
                    if (settings.debug_pref == true) log.debug "Bin status 'Full'"
				break;
			}
			switch (result.power_status.cleaner_state) {
				case "st_base":
					sendEvent(name: 'status', value: "docked" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_base_recon":
					sendEvent(name: 'status', value: "charging" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_base_full":
					sendEvent(name: 'status', value: "charging" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_base_trickle":
					sendEvent(name: 'status', value: "docked" as String)
                    sendEvent(name: 'beep', value: "inactive" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_base_wait":
					sendEvent(name: 'status', value: "docked" as String)
                    sendEvent(name: 'beep', value: "inactive" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
                case "st_wait":
					sendEvent(name: 'status', value: "paused" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;                
				case "st_plug":
					sendEvent(name: 'status', value: "plugged" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug_recon":
					sendEvent(name: 'status', value: "pluggedcharge" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug_full":
					sendEvent(name: 'status', value: "pluggedcharge" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug_trickle":
					sendEvent(name: 'status', value: "plugged" as String)
                    sendEvent(name: 'beep', value: "inactive" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_plug_wait":
					sendEvent(name: 'status', value: "off" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_stopped":
					sendEvent(name: 'status', value: "paused" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_cleanstop":
					sendEvent(name: 'status', value: "paused" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_delayed":
					sendEvent(name: 'status', value: "delayed" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_pickup":
					sendEvent(name: 'status', value: "paused" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				case "st_locate":
					sendEvent(name: 'status', value: "findme" as String)
				break;
				case "st_clean":
                   	if (settings.debug_pref == true) log.debug "Clean state ${result.power_status.cleaner_state} status ${result.tc_status.cleaning}"
					if (result.tc_status.cleaning == 1){
						sendEvent(name: 'status', value: "cleaning" as String)
                        sendEvent(name: 'beep', value: "beep" as String)
						sendEvent(name: 'switch', value: "on" as String)
					}
					else {
                        sendEvent(name: 'status', value: "default" as String)
                        sendEvent(name: 'beep', value: "beep" as String)
						sendEvent(name: 'switch', value: "off" as String)
					}
				break;
				case "st_clean_spot":
                    if (settings.debug_pref == true) log.debug "Clean state ${result.power_status.cleaner_state} status ${result.tc_status.cleaning}"
					if (result.tc_status.cleaning == 1){
						sendEvent(name: 'status', value: "cleaning" as String)
                        sendEvent(name: 'beep', value: "beep" as String)
						sendEvent(name: 'switch', value: "on" as String)
					}
					else {
                        sendEvent(name: 'status', value: "default" as String)
                        sendEvent(name: 'beep', value: "beep" as String)
						sendEvent(name: 'switch', value: "off" as String)                   
					}
				break;
				case "st_clean_max":
                   	if (settings.debug_pref == true) log.debug "Clean state ${result.power_status.cleaner_state} status ${result.tc_status.cleaning}"
					if (result.tc_status.cleaning == 1){
						sendEvent(name: 'status', value: "cleaning" as String)
                        sendEvent(name: 'beep', value: "beep" as String)
						sendEvent(name: 'switch', value: "on" as String)
					}
					else {
                        sendEvent(name: 'status', value: "default" as String)
                        sendEvent(name: 'beep', value: "beep" as String)
						sendEvent(name: 'switch', value: "off" as String)
					}
				break;
				case "st_dock":
                    if (settings.debug_pref == true) log.debug "Clean state ${result.power_status.cleaner_state} status ${result.tc_status.cleaning}"
					if (result.tc_status.cleaning == 1){
						sendEvent(name: 'status', value: "docking" as String)
						sendEvent(name: 'switch', value: "on" as String)
					}
					else {
						sendEvent(name: 'status', value: "error" as String)
						sendEvent(name: 'switch', value: "off" as String)
					}
				break;
				case "st_off":
					sendEvent(name: 'status', value: "error" as String)
                    sendEvent(name: 'beep', value: "beep" as String)
					sendEvent(name: 'switch', value: "off" as String)
				break;
				default:
					sendEvent(name: 'status', value: "default" as String)
                    sendEvent(name: 'beep', value: "inactive" as String)
                    sendEvent(name: 'bin', value: "default" as String)
				break;
			}
			break;
		}
	}
	else {
		sendEvent(name: 'status', value: "error" as String)
		sendEvent(name: 'network', value: "default" as String)
        sendEvent(name: 'beep', value: "inactive" as String)
        sendEvent(name: 'bin', value: "default" as String)
		if (settings.debug_pref == true) log.debug headerString
	}
	parse
}

// handle commands

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
	initialize()
}

def initialize() {
	log.info "Thinking Cleaner ${textVersion()} ${textCopyright()}"
	ipSetup()
	poll()
}

def on() {
	log.debug "Executing 'on'"
	ipSetup()
	api('on')
}

def off() {
	log.debug "Executing 'off'"
	api('off')
}

def poll() {
	if (settings.debug_pref == true) log.debug "Executing 'poll'"
    
	if (device.deviceNetworkId != null) {
		api('refresh')
	}
	else {
		sendEvent(name: 'status', value: "error" as String)
		sendEvent(name: 'network', value: "Not Connected" as String)
        sendEvent(name: 'bin', value: "default" as String)
		if (settings.debug_pref == true) log.debug "Device Network ID Not set"
	}
}

def refresh() {
	if (settings.debug_pref == true) log.debug "Executing 'refresh'"
	ipSetup()
	api('refresh')
}

def beep() {
	if (settings.debug_pref == true) log.debug "Executing 'beep'"
	ipSetup()
	api('beep')
}

def api(String rooCommand, success = {}) {
	def rooPath
	def hubAction
	def cleanmodevalue = "${settings.cleanmode}"
	if (device.currentValue('network') == "unknown"){
		sendEvent(name: 'network', value: "default" as String)
        sendEvent(name: 'bin', value: "default" as String)
		if (settings.debug_pref == true) log.debug "Network is not connected"
	}
	else {
		sendEvent(name: 'network', value: "unknown" as String, displayed:false)
	}
	switch (rooCommand) {
		case "on":
        if (settings.debug_pref == true) log.debug "Clean mode is ${settings.cleanmode}"
    	if (settings.debug_pref == true) log.debug "Clean mode vlaue is ${cleanmodevalue}"
        sendEvent(name: 'status', value: "cleaning" as String)
        if (cleanmodevalue == null){
			rooPath = "/command.json?command=clean"
			if (settings.debug_pref == true) log.debug "The Clean Command was sent"
        }else{
        	if (cleanmodevalue == "Clean"){
			rooPath = "/command.json?command=clean"
			if (settings.debug_pref == true) log.debug "The Clean Command was sent"
		}else{
        	if (cleanmodevalue == "Max Clean"){
            rooPath = "/command.json?command=max"
            if (settings.debug_pref == true) log.debug "The Max Clean Command was sent"
            	}
        	}
        }
		break;
		case "off":
			rooPath = "/command.json?command=dock"
			log.debug "The Dock Command was sent"
            sendEvent(name: 'status', value: "docking" as String)
		break;
		case "refresh":
			rooPath = "/full_status.json"
			if (settings.debug_pref == true) log.debug "The Refresh Status Command was sent"
        	sendEvent(name: 'status', value: "default" as String)
		break;
		case "beep":
			rooPath = "/command.json?command=find_me"
			log.debug "The Beep Command was sent"
            sendEvent(name: 'status', value: "default" as String)
		break;
	}
    
	switch (rooCommand) {
		case "refresh":
		case "beep":
			try {
				hubAction = new physicalgraph.device.HubAction(
				method: "GET",
				path: rooPath,
				headers: [HOST: "${settings.ip}:${settings.port}", Accept: "application/json"])
			}
			catch (Exception e) {
				if (settings.debug_pref == true) log.debug "Hit Exception $e on $hubAction"
			}
			break;
		default:
			try {
				hubAction = [new physicalgraph.device.HubAction(
				method: "GET",
				path: rooPath,
				headers: [HOST: "${settings.ip}:${settings.port}", Accept: "application/json"]
				), delayAction(9800), api('refresh')]
			}
			catch (Exception e) {
				if (settings.debug_pref == true) log.debug "Hit Exception $e on $hubAction"
			}
			break;
	}
	return hubAction
}

def ipSetup() {
	def hosthex
	def porthex
	if (settings.ip) {
		hosthex = convertIPtoHex(settings.ip)
	}
	if (settings.port) {
		porthex = convertPortToHex(settings.port)
	}
	if (settings.ip && settings.port) {
		device.deviceNetworkId = "$hosthex:$porthex"
	}
}

private String convertIPtoHex(ip) { 
	String hexip = ip.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
	return hexip
}
private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
	return hexport
}
private delayAction(long time) {
	new physicalgraph.device.HubAction("delay $time")
}

private def textVersion() {
	def text = "Version 2.0"
}

private def textCopyright() {
	def text = "Copyright © 2016 pmjoen"
}