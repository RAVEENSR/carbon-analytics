/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
define(['require', 'log', 'jquery', 'backbone', 'tool_palette/tool', 'tool_palette/tool-view'],
    function (require, log, $, Backbone, Tool, ToolView) {

    var ToolPalette = Backbone.View.extend({
        initialize: function (options) {
            var errMsg;
            if (!_.has(options, 'container')) {
                errMsg = 'unable to find configuration for container';
                log.error(errMsg);
                throw errMsg;
            }
            var container = $(_.get(options, 'container'));
            // check whether container element exists in dom
            if (!container.length > 0) {
                errMsg = 'unable to find container for tab list with selector: ' + _.get(options, 'container');
                log.error(errMsg);
                throw errMsg;
            }
            this._$parent_el = container;
            this._options = options;
            this._initTools();
        },

        _initTools: function(){

            var definitionTools = [
                {
                    id : "source",
                    className : "source-drag",
                    title : "Source",
                    icon : "/editor/images/source.svg"
                },
                {
                    id : "sink",
                    className : "sink-drag",
                    title : "Sink",
                    icon : "/editor/images/sink.svg"
                },
                {
                    id : "stream",
                    className : "stream-drag",
                    title : "Stream",
                    icon : "/editor/images/streams.svg"
                },
                {
                    id : "table",
                    className : "table-drag",
                    title : "Table",
                    icon : "/editor/images/table.svg"
                },
                {
                    id : "window",
                    className : "window-drag",
                    title : "Window",
                    icon : "/editor/images/window.svg"
                },
                {
                    id : "trigger",
                    className : "trigger-drag",
                    title : "Trigger",
                    icon : "/editor/images/trigger.svg"
                },
                {
                    id : "aggregation",
                    className : "aggregation-drag",
                    title : "Aggregation",
                    icon : "/editor/images/aggregate.svg"
                },
                {
                    id : "function",
                    className : "function-drag",
                    title : "Function",
                    icon : "/editor/images/function.svg"
                },
                {
                    id : "projection-query",
                    className : "projection-query-drag",
                    title : "Projection Query",
                    icon : "/editor/images/query.svg"
                },
                {
                    id : "filter-query",
                    className : "filter-query-drag",
                    title : "Filter Query",
                    icon : "/editor/images/filter-query.svg"
                },
                {
                    id : "window-query",
                    className : "window-query-drag",
                    title : "Window Query",
                    icon : "/editor/images/window-query.svg"
                },
                {
                    id : "join-query",
                    className : "join-query-drag",
                    title : "Join Query",
                    icon : "/editor/images/join-query.svg"
                }//, TODO: Uncomment these lines
                // {
                //     id : "pattern-query",
                //     className : "pattern-query-drag",
                //     title : "Pattern Query",
                //     icon : "/editor/images/pattern-query.svg"
                // },
                // {
                //     id : "sequence-query",
                //     className : "sequence-query-drag",
                //     title : "Sequence Query",
                //     icon : "/editor/images/sequence-query.svg"
                // },
                // {
                //     id : "partition",
                //     className : "partition-drag",
                //     title : "Partition",
                //     icon : "/editor/images/partition.svg"
                // }
            ];

            this.tools = [];
            var self = this;
            _.forEach(definitionTools,  function(toolDefinition){
                    var tool = new Tool(toolDefinition);
                    self.tools.push(tool);
                }
            );
        },

        render: function () {
            var self = this;
            self._$parent_el.addClass('non-user-selectable');

            var groupDiv = $('<div></div>');
            self._$parent_el.append(groupDiv);
            groupDiv.attr('id', "tool-group-elements");
            groupDiv.attr('class', "tool-group");

            this.tools.forEach(function (tool) {
                var toolView = new ToolView({model: tool, toolPalette: self.toolPalette});
                toolView.render(groupDiv);
            });
        },

        hideToolPalette: function () {
            this._$parent_el.hide();
        },

        showToolPalette: function () {
            this._$parent_el.show();
        }
    });

    return ToolPalette;
});
