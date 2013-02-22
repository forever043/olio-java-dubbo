/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
// define the namespaces
jmaki.namespace("jmaki.widgets.yahoo.geocoder");

/**
 * Yahoo Geocoder Widget
 *      This widget lets you find geocordinates using the XMLHttpProxy using 
 *      the Yahoo Geocoder service
 *
 * @author Greg Murray 
 *          (original author)
 * @author Ahmad M. Zawawi <ahmad.zawawi@gmail.com>  
 *          (Updated to new yahoo jmaki widget standard)
* @constructor
 * @see http://developer.yahoo.com/maps/rest/V1/geocode.html
 */
jmaki.widgets.yahoo.geocoder.Widget = function(wargs) {
    
    var topic = "/yahoo/geocoder";
    var uuid = wargs.uuid;
    var self = this;
    
    // we run on the xhp now
    var service = jmaki.xhp;
    
    //overide topic name if needed
    if (wargs.args && wargs.args.topic) {
        topic = wargs.args.topic;
        jmaki.log("Yahoo geocoder: widget uses deprecated topic. Use publish instead.");
    }
    
    if (wargs.publish) topic = wargs.publish;   
    
    var location; 
    
    /**
     */
    this.getCoordinates = function() {
        location = encodeURIComponent(document.getElementById(uuid + "_location").value);
        var encodedLocation = encodeURIComponent("location=" + location);        
        var url = service + "?id=yahoogeocoder&urlparams=" + encodedLocation;        
        jmaki.doAjax({url: url, callback: function(req) { var _req=req; postProcess(_req);}});
    }
    
    /**
     */
    function postProcess(req) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var v = {success:false};
                if(req.responseText.length > 0) {
                    var response = eval("(" + req.responseText + ")");
                    var coordinates = response.coordinates;
                    v = {success:true,results:coordinates};
                    
                    //for compatibility (deprecated): we leave this one for a while
                    jmaki.publish(topic, coordinates);  
                } 
                //the new format is here (as in v)
                //with status flag sent
                jmaki.publish(topic + "/onGeocode", {id: uuid, value:v} )                
            } 
        }
        
    }
    
}
