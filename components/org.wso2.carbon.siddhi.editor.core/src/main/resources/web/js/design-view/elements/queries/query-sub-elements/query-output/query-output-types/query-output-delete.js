/**
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

define(
    function () {

        /**
         * @class QueryOutputDelete
         * @constructor
         * @class QueryOutputDelete  Creates a delete type in query output
         * @param {Object} options Rendering options for the view
         */
        var QueryOutputDelete = function (options) {
            /*
             Data storing structure as follows
                eventType: 'current_events|expired_events|all_events',
                on*: ''
            */
            if (options !== undefined) {
                this.eventType = options.eventType;
                this.on = options.on;
            }
        };

        QueryOutputDelete.prototype.getEventType = function () {
            return this.eventType;
        };

        QueryOutputDelete.prototype.getOn = function () {
            return this.on;
        };

        QueryOutputDelete.prototype.setEventType = function (eventType) {
            this.eventType = eventType;
        };

        QueryOutputDelete.prototype.setOn = function (on) {
            this.on = on;
        };

        return QueryOutputDelete;

    });
