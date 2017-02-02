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
page(name: "pageOne", title: "Controller Credentials", nextPage: "pageTwo") 
{
    section("Configure your Hydrawise credentials") {
        input "apiKey", "text", title: "Hydrawise Controller API Key", required: true
    }
 }

page(name: "pageTwo", title: "Current Controller Status", uninstall: true) 
{
 // insert
}
}
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
      subscribe(app, sprinklerGet)
}

def sprinklerGet(evt) {
    log.info "app event ${evt.name}:${evt.value} received"
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

/**
 *  Example of passing params via href element to a dynamic page
 *
 *  Copyright 2015 SmartThings
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
 *
definition(
    name: "href params example",
    namespace: "smartthings",
    author: "SmartThings",
    description: "passing params via href element to a dynamic page",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


// ========================================================
// PAGES
// ========================================================

preferences {
    page(name: "firstPage")
    page(name: "secondPage")
    page(name: "thirdPage")
}

def firstPage() {

    def hrefParams = [
        foo: "bar", 
        thisIs: "totally working", 
        install: checkSomeCustomLogic(),
        nextPage: "thirdPage"
    ]
    
    dynamicPage(name: "firstPage", uninstall: true) {
        /* 
        *  The href element accepts a key of `params` and expects a `Map` as the value.
        *  Anything passed in `params` will be sent along with the request but will not be persisted in any way.
        *  The params will be used going to the next page but will not be available when backing out of that page.
        *  This, combined with the fact that pages refresh when navigating back to them, means that your params will not be
        *  available if your user hits the back button.
        *
        section {        
            href(
                name: "toSecondPage", 
                page: "secondPage", 
                params: hrefParams, 
                description: "includes params: ${hrefParams}",
                state: hrefState()
            )
        }
    }
}

def secondPage(params) {
     /* 
     * firstPage included `params` in the href element that navigated to here. 
     * You must specify some variable name in the method declaration. (I used 'params' here, but it can be any variable name you want).
     * If you do not specify a variable name, there is no way to get the params that you specified in your `href` element. 
     *

    log.debug "params: ${params}"

    dynamicPage(name: "secondPage", uninstall: true, install: params?.install) {
        if (params.nextPage) {
            section {
                href(
                    name: "toNextPage", 
                    page: params.nextPage, 
                    description: hrefState() ? "You've already been to the third page": "go checkout the third page",
                    state: hrefState()
                )
            }
        } else {
            section {
                paragraph "There were no params included when fetching this page."
            }
        }
    }
}

def thirdPage() {

    state.reachedThirdPage = true

    dynamicPage(name: "thirdPage") {
        section {
            image "https://placekitten.com/g/600/500"
        }
    }
}

// ========================================================
// HELPERS
// ========================================================


def checkSomeCustomLogic() {
    // ... 
    false
}

def hrefState() {
    /* 
     * `state: "complete"` makes the right side of the href green. 
     * It's a great way to show the user that they've already set up some stuff on that page. 
     * In other words, it's a great way to show state ;) 
     * If you're using hrefs, it's a good idea to set `state` when appropriate. 
     *
     state.reachedThirdPage ? "complete": ""
}

// ========================================================
// HANDLERS
// ========================================================


def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    // TODO: subscribe to attributes, devices, locations, etc.
}

// TODO: implement event handlers
*/