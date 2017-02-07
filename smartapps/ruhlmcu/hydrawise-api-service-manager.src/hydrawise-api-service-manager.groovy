/**
 *  Hydrawise API Connect - A Service Manager for Hydrawise
 *
 *  Copyright 2017 Curt Ruhlman
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
 */
definition(
    name: "Hydrawise API (Service Manager)",
    namespace: "ruhlmcu",
    author: "Curt Ruhlman",
    description: "Connect your Smartthings Controller to your Hydrawise Irrigation Controller.",
    category: "Green Living",
    iconUrl: "http://cdn.device-icons.smartthings.com/Outdoor/outdoor12-icn@2x.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Outdoor/outdoor12-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Outdoor/outdoor12-icn@2x.png",
    singleInstance: true,
    oauth: true)

preferences {
    page name:"pageOne"
    page name:"pageStatus"
    page name:"pageSettings"
}
def pageOne(){
dynamicPage (name: "pageOne", title: "Controller Credentials", install: true, uninstall: true){
    	section("Configure your Hydrawise credentials") {
        	input "apiKey", "text", title: "Hydrawise Controller API Key", required: true
        }
        section("Set-up Instructions") {            
        	paragraph title: "Set-up Instructions",
            required: true,
					"This is the set-up page to allow you to access your Hydrawise Controller. The API Key is found in the Account section of your Hydrawise Dashboard."
    	}
        section("page") {
            href(name: "SetPage",
                 title: "Settings Page",
                 required: false,
                 page: "pageSettings")
        }
        section("StatPage") {
            href(name: "href",
                 title: "Status Page",
                 required: false,
                 page: "pageStatus")
        }
}
}
def pageStatus(){
dynamicPage (name: "pageStatus", title: "Controller Status", uninstall: false){
        section ("Current Controller") {
            paragraph title: "Controller Status",
            required: true
            def apiValid = validateAPI()
            if (apiValid) {
                "The current controller is"
            }
        }
} 
}

def pageSettings(){
dynamicPage (name: "pageSettings", title: "App Settings", uninstall: false){
    	section("Configure your Hydrawise Settings") {
            input "debugOn", "bool", title: "Turn on to activate debug messages", defaultValue: false
            input "notifyOn", "bool", title: "Turn on to activate notifications", defaultValue: false
            input "SMSOn", "bool", title: "Turn on to activate SMS messages", defaultValue: false
        }
	}
}
//Handlers
def installed() {
    log.info "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.info "Updated with settings: ${settings}"
    initialize()
}

def initialize() {
//    runEvery1Hour(sprinklerGet)
	subscribe(app, sprinklerGet)
}
//Methods
//This is the Discovery method for my Hydrawise Controller
def sprinklerGet(evt) {
    def params = [
        uri: "https://hydrawise.com/api/v1/",
        path: "customerdetails.php",
        query: [
           "api_key": apiKey, "type": "controllers"
        ]
    ]
    if (debugOn){
           log.info "parameters to send ${params}"
    }
    try {
          httpGet(params) { resp ->
               if (debugOn){
               log.debug "$resp.data"             
               log.debug "$resp.data.error_msg"
               }
               if (resp.data.controller_id != ""){
                    log.info( "Current Controller ID: ${resp.data.controller_id}")
                    sendNotificationEvent( "Current Customer ID: ${resp.data.customer_id}")
                    sendNotificationEvent( "Current Controller Name: ${resp.data.current_controller}")                  
               }
               else {
                   if (debugOn){
                        log.debug ("$resp.data")
                   }
                   sendNotificationEvent("API Key Error: ${resp.data}")
               }
          }
      }
      catch (e) {
        log.error "something went wrong: $e"
    }
}

// Example success method
/*
def validateAPI(result) {
    sendNotificationEvent("API Valid")
    result = true
}
*/
def success() {
        def message = """
                <p>Success</p>
                <p>Click 'Done' to finish setup.</p>
        """
        displayMessageAsHtml(message)
}

// Example fail method
def fail() {
    def message = """
        <p>There was an error connecting your account with SmartThings</p>
        <p>Please try again.</p>
    """
    displayMessageAsHtml(message)
}

def displayMessageAsHtml(message) {
    def html = """
        <!DOCTYPE html>
        <html>
            <head>
            </head>
            <body>
                <div>
                    ${message}
                </div>
            </body>
        </html>
    """
    render contentType: 'text/html', data: html
}