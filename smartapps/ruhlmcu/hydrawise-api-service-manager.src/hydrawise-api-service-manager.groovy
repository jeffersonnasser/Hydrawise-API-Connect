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
    iconX2Url: "http://icons.wxug.com/i/c/k/cloudy.gif",
    iconX3Url: "http://icons.wxug.com/i/c/k/cloudy.gif",
    singleInstance: true,
    oauth: true)

preferences {
    page(name: "pageOne") 
    page(name: "pageTwo") 
}
//Pages

def pageOne() {
	dynamicPage(name: "pageOne", title: "Controller Credentials", nextPage: "pageTwo", install: true, uninstall: true){
    	section("Configure your Hydrawise credentials") {
        	input "apiKey", "text", title: "Hydrawise Controller API Key", required: true
        }
        section("Set-up Instructions") {            
        	paragraph title: "Set-up Instructions",
            required: true,
					"This is the set-up page to allow you to access your Hydrawise Controller. The API Key is found in the Account section of your Hydrawise Dashboard."
    	}
	}
}
def pageTwo () {
	dynamicPage(name: "pageTwo", title: "Current Controller Status", uninstall: true){
        	section("Enter Your Name") {
        	input "ownerName", "text", title: "Owner Name", required: true
        }
    	section("paragraph") {
        	paragraph "This is how you can make a paragraph element"
            paragraph title: "paragraph title",
            required: true,
            	"This is a long description that rambles on and on and on..."
    	}
	}
        sendNotificationEvent("Controller Id: ${resp.data.controller_id}")
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
//   runEvery5Minutes(sprinklerGet(evt))
	subscribe(app, sprinklerGet())
}
//Methods
//This is the Discovery method for my Hydrawise Controller
def sprinklerGet(evt) {
    def params = [
        uri: "https://hydrawise.com/api/v1/",
        path: "customerdetails.php",
        query: [
           "api_key": apiKey,
           "type": "controllers"
        ]
    ]
    log.info "parameters to send ${params}"
    try {
          httpGet(params) { resp ->
          log.info "Current Controller ID: ${resp.data.controller_id}"
          log.info "Current Customer ID: ${resp.data.customer_id}"
          log.info "Current Controller Name: ${resp.data.current_controller}"

          }         
    } catch (e) {
        log.error "something went wrong: $e"
    }
}

// Example success method
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