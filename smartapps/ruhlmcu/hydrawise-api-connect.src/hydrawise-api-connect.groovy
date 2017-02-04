/**
 *  Hydrawise API Connect
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
    name: "Hydrawise API Connect",
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
		dynamicPage(name: "pageOne", title: "Controller Credentials", install: true, uninstall: true){
    		section("Configure your Hydrawise credentials") {
        		input "apiKey", "text", title: "Hydrawise Controller API Key", required: true
 //             sendNotificationEvent("Controller Id: ${resp.data.controller_id}")
    		} 
		}
}

def pageTwo () {
		  dynamicPage(name: "pageTwo", title: "Current Controller Status", uninstall: true)
          sendNotificationEvent( "Controller Id: ${resp.data.controller_id}")
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
def sprinklerGet(evt) {
//    log.info "app event ${evt.name}:${evt.value} received"
    def params = [
        uri: "https://hydrawise.com/api/v1/",
        path: "statusschedule.php",
        query: [
           "api_key": apiKey,
           "tag": "hydrawise_all",
           "hours": 48
        ]
    ]
    log.info "parameters to send ${params}"
    try {
        httpGet(params) { resp ->
//          log.info "running data: ${resp.data.running}"
//          def runningLength = resp.data.running.size  
//         log.info "running length: ${runningLength}"
          if (resp.data.running != null){
              resp.data.running.each {
                  log.info "here"
                  log.info "${it.relay_id}"
              }
           }
           else
           {
               resp.data.relays.each {
                   log.info "${it.name} ${it.relay_id}"
           }
          log.info "Current Controller ID: ${resp.data.controller_id}"

        } 
    }
    } catch (e) {
        log.error "something went wrong: $e"
    }
}
