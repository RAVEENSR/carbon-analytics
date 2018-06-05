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

define(['require', 'log', 'jquery', 'lodash', 'attribute', 'aggregation', 'aggregateByTimePeriod', 'querySelect',
        'elementUtils', 'storeAnnotation'],
    function (require, log, $, _, Attribute, Aggregation, AggregateByTimePeriod, QuerySelect, ElementUtils, 
              StoreAnnotation) {

        /**
         * @class AggregationForm Creates a forms to collect data from a aggregation
         * @constructor
         * @param {Object} options Rendering options for the view
         */
        var AggregationForm = function (options) {
            if (options !== undefined) {
                this.configurationData = options.configurationData;
                this.application = options.application;
                this.formUtils = options.formUtils;
                this.consoleListManager = options.application.outputController;
                var currentTabId = this.application.tabController.activeTab.cid;
                this.designViewContainer = $('#design-container-' + currentTabId);
                this.toggleViewButton = $('#toggle-view-button-' + currentTabId);
            }
        };

        /**
         * @function generate form when defining a form
         * @param i id for the element
         * @param formConsole Console which holds the form
         * @param formContainer Container which holds the form
         */
        AggregationForm.prototype.generateDefineForm = function (i, formConsole, formContainer) {
            var self = this;
            var propertyDiv = $('<div id="property-header"><h3>Define Aggregation </h3></div>' +
                '<div id="define-aggregation" class="define-aggregation"></div>');
            formContainer.append(propertyDiv);
            formContainer.append('<div class="row"><div id="form-aggregation-annotation" class="col-md-12"></div></div>' +
                '<div class="row"><div id="form-aggregation-input" class="col-md-4"></div>' +
                '<div id="form-aggregation-select" class="col-md-4"></div>' +
                '<div id="form-aggregation-aggregate" class="col-md-4"></div></div>');

            var possibleFromSources = [];
            _.forEach(self.configurationData.getSiddhiAppConfig().streamList, function (stream) {
                possibleFromSources.push(stream.getName());
            });
            _.forEach(self.configurationData.getSiddhiAppConfig().triggerList, function (trigger) {
                possibleFromSources.push(trigger.getName());
            });
            var possibleGroupByAttributes = [];
            var firstPossibleSourceName = possibleFromSources[0];

            var firstPossibleSource =
                self.configurationData.getSiddhiAppConfig().getDefinitionElementByName(firstPossibleSourceName);
            if (firstPossibleSource !== undefined) {
                if (firstPossibleSource.type !== undefined && (firstPossibleSource.type === 'STREAM')) {
                    var timestampAttributeFound = false;
                    if (firstPossibleSource.element !== undefined) {
                        _.forEach(firstPossibleSource.element.getAttributeList(), function (attribute) {
                            if (attribute.getName().toLowerCase() === 'timestamp') {
                                timestampAttributeFound = true;
                            }
                            possibleGroupByAttributes.push(attribute.getName());
                        });
                    }
                    if (!timestampAttributeFound) {
                        possibleGroupByAttributes.push('timestamp');
                    }
                } else if (firstPossibleSource.type !== undefined && (firstPossibleSource.type === 'TRIGGER')) {
                    possibleGroupByAttributes.push('triggered_time');
                }
            }

            // generate the form to define a aggregation
            var editorAnnotation = new JSONEditor($(formContainer).find('#form-aggregation-annotation')[0], {
                schema: {
                    type: "object",
                    title: "Aggregation Annotations",
                    properties: {
                        annotations: {
                            propertyOrder: 1,
                            type: "array",
                            format: "table",
                            title: "Add Annotations",
                            uniqueItems: true,
                            minItems: 1,
                            items: {
                                type: "object",
                                title: "Annotation",
                                options: {
                                    disable_properties: true
                                },
                                properties: {
                                    annotation: {
                                        title: "Annotation",
                                        type: "string",
                                        minLength: 1
                                    }
                                }
                            }
                        },
                        storeAnnotation: {
                            propertyOrder: 2,
                            title: "Store Annotation",
                            type: "object",
                            options: {
                                disable_properties: true
                            },
                            properties: {
                                annotationType: {
                                    propertyOrder: 1,
                                    required: true,
                                    title: "Type",
                                    type: "string",
                                    minLength: 1
                                 },
                                storeOptions: {
                                    propertyOrder: 2,
                                    required: true,
                                    type: "array",
                                    format: "table",
                                    title: "Options",
                                    uniqueItems: true,
                                    minItems: 1,
                                    items: {
                                        type: "object",
                                        title: "Option",
                                        options: {
                                            disable_properties: true
                                        },
                                        properties: {
                                            key: {
                                                required: true,
                                                title: "Key",
                                                type: "string",
                                                minLength: 1
                                            },
                                            value: {
                                                required: true,
                                                title: "value",
                                                type: "string",
                                                minLength: 1
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                show_errors: "always",
                display_required_only: true,
                no_additional_properties: true,
                disable_array_delete_all_rows: true,
                disable_array_delete_last_row: true
            });
            var editorInput = new JSONEditor($(formContainer).find('#form-aggregation-input')[0], {
                schema: {
                    type: "object",
                    title: "Aggregation Input",
                    properties: {
                        name: {
                            type: "string",
                            title: "Name",
                            minLength: 1,
                            required: true,
                            propertyOrder: 1
                        },
                        from: {
                            type: "string",
                            title: "From",
                            enum: possibleFromSources,
                            required: true,
                            propertyOrder: 2
                        }
                    }
                },
                show_errors: "always",
                disable_properties: true,
                display_required_only: true,
                no_additional_properties: true
            });
            var selectScheme = {
                schema: {
                    options: {
                        disable_properties: false
                    },
                    type: "object",
                    title: "Aggregation Select",
                    properties: {
                        select: {
                            propertyOrder: 1,
                            title: "Select",
                            required: true,
                            oneOf: [
                                {
                                    $ref: "#/definitions/selectAll",
                                    title: "All Attributes"
                                },
                                {
                                    $ref: "#/definitions/selectUserDefined",
                                    title: "User Defined Attributes"
                                }
                            ]
                        },
                        groupBy: {
                            propertyOrder: 2,
                            type: "array",
                            format: "table",
                            title: "Group By Attributes",
                            uniqueItems: true,
                            minItems: 1,
                            items: {
                                type: "object",
                                title: 'Attribute',
                                properties: {
                                    attribute: {
                                        type: 'string',
                                        title: 'Attribute Name',
                                        enum: possibleGroupByAttributes
                                    }
                                }
                            }
                        }
                    },
                    definitions: {
                        selectUserDefined: {
                            required: true,
                            type: "array",
                            format: "table",
                            title: "Select Attributes",
                            uniqueItems: true,
                            minItems: 1,
                            items: {
                                title: "Value Set",
                                type: "object",
                                properties: {
                                    expression: {
                                        title: "Expression",
                                        type: "string",
                                        minLength: 1
                                    },
                                    as: {
                                        title: "As",
                                        type: "string"
                                    }
                                }
                            }
                        },
                        selectAll: {
                            type: "string",
                            title: "Select All Attributes",
                            template: '*'
                        }
                    }
                },
                show_errors: "always",
                disable_properties: false,
                disable_array_delete_all_rows: true,
                disable_array_delete_last_row: true,
                display_required_only: true,
                no_additional_properties: true
            };
            var editorSelect = new JSONEditor($(formContainer).find('#form-aggregation-select')[0], selectScheme);
            var aggregateScheme = {
                schema: {
                    type: "object",
                    title: "Aggregate By",
                    properties: {
                        aggregateByAttribute: {
                            type: "object",
                            title: "Aggregate by Attribute",
                            propertyOrder: 1,
                            properties: {
                                attribute: {
                                    required: true,
                                    type: "string",
                                    title: "Attribute Name",
                                    enum: possibleGroupByAttributes
                                }
                            }
                        },
                        aggregateByTimePeriod: {
                            required: true,
                            type: "object",
                            title: "Aggregate by Time Period",
                            propertyOrder: 2,
                            options: {
                                disable_properties: false
                            },
                            properties: {
                                minValue: {
                                    type: "string",
                                    title: "Starting Time Value",
                                    required: true,
                                    enum: [
                                        "seconds",
                                        "minutes",
                                        "hours",
                                        "days",
                                        "weeks",
                                        "months",
                                        "years"
                                    ],
                                    default: "seconds"
                                },
                                maxValue: {
                                    type: "string",
                                    title: "Ending Time Value",
                                    enum: [
                                        "seconds",
                                        "minutes",
                                        "hours",
                                        "days",
                                        "weeks",
                                        "months",
                                        "years"
                                    ],
                                    default: "seconds"
                                }
                            }
                        }
                    }
                },
                show_errors: "always",
                disable_properties: false,
                display_required_only: true,
                no_additional_properties: true
            };
            var editorAggregate =
                new JSONEditor($(formContainer).find('#form-aggregation-aggregate')[0], aggregateScheme);

            var fromNode = editorInput.getEditor('root.from');
            editorInput.watch('root.from',function() {
                var fromValue = fromNode.getValue();
                var inputElement = self.configurationData.getSiddhiAppConfig().getDefinitionElementByName(fromValue);
                ElementUtils.prototype.removeAllElements(possibleGroupByAttributes);
                if (inputElement !== undefined) {
                    if (inputElement.type !== undefined && (inputElement.type === 'STREAM')) {
                        var timestampAttributeFound = false;
                        if (inputElement.element !== undefined) {
                            _.forEach(inputElement.element.getAttributeList(), function (attribute) {
                                if (attribute.getName().toLowerCase() === 'timestamp') {
                                    timestampAttributeFound = true;
                                }
                                possibleGroupByAttributes.push(attribute.getName());
                            });
                        }
                        if (!timestampAttributeFound) {
                            possibleGroupByAttributes.push('timestamp');
                        }
                    } else if (inputElement.type !== undefined && (inputElement.type === 'TRIGGER')) {
                        possibleGroupByAttributes.push('triggered_time');
                    }
                }

                // setting the previously user entered data for select section
                var selectStartValue = {
                    select: editorSelect.getValue().select
                };
                // if the user has activated the group by field in the form, then the new form will also activate the
                // group by field
                if (editorSelect.getValue().groupBy !== undefined) {
                    _.set(selectStartValue, 'groupBy', "");
                }
                _.set(selectScheme, 'startval', selectStartValue);
                $(formContainer).find('#form-aggregation-select').empty();
                editorSelect = new JSONEditor($(formContainer).find('#form-aggregation-select')[0], selectScheme);

                // setting the previously user entered data for aggregate section
                var aggregateStartValue = {
                    aggregateByTimePeriod: editorAggregate.getValue().aggregateByTimePeriod
                };
                _.set(aggregateScheme, 'startval', aggregateStartValue);
                $(formContainer).find('#form-aggregation-aggregate').empty();
                editorAggregate = new JSONEditor($(formContainer).find('#form-aggregation-aggregate')[0], aggregateScheme);
            });

            formContainer.append('<div id="submit"><button type="button" class="btn btn-default">Submit</button></div>');

            // 'Submit' button action
            var submitButtonElement = $(formContainer).find('#submit')[0];
            submitButtonElement.addEventListener('click', function () {
                var annotationErrors = editorAnnotation.validate();
                var inputErrors = editorInput.validate();
                var selectErrors = editorSelect.validate();
                var aggregateErrors = editorAggregate.validate();
                if(annotationErrors.length || inputErrors.length || selectErrors.length || aggregateErrors.length) {
                    return;
                }
                var isAggregationNameUsed = self.formUtils.isDefinitionElementNameUnique(editorInput.getValue().name);
                if(isAggregationNameUsed) {
                    alert("Aggregation name \"" + editorInput.getValue().name + "\" is already used.");
                    return;
                }
                // add the new aggregation to the aggregation array
                var aggregationOptions = {};
                _.set(aggregationOptions, 'id', i);
                _.set(aggregationOptions, 'name', editorInput.getValue().name);
                _.set(aggregationOptions, 'from', editorInput.getValue().from);

                // add the store annotation for aggregation
                if (editorAnnotation.getValue().storeAnnotation !== undefined) {
                    var optionsMap = {};
                    _.forEach(editorAnnotation.getValue().storeAnnotation.storeOptions, function (option) {
                        optionsMap[option.key] = option.value;
                    });
                    
                    var storeAnnotationOptions = {};
                    _.set(storeAnnotationOptions, 'type', editorAnnotation.getValue().storeAnnotation.annotationType);
                    _.set(storeAnnotationOptions, 'options', optionsMap);

                    var storeAnnotation = new StoreAnnotation(storeAnnotationOptions);
                    _.set(aggregationOptions, 'store', storeAnnotation);
                } else {
                    _.set(aggregationOptions, 'store', undefined);
                }

                var selectAttributeOptions = {};
                if (editorSelect.getValue().select instanceof Array) {
                    _.set(selectAttributeOptions, 'type', 'USER_DEFINED');
                    _.set(selectAttributeOptions, 'value', editorSelect.getValue().select);
                } else if (editorSelect.getValue().select === "*") {
                    _.set(selectAttributeOptions, 'type', 'ALL');
                    _.set(selectAttributeOptions, 'value', editorSelect.getValue().select);
                } else {
                    console.log("Value other than \"USER_DEFINED\" and \"ALL\" received!");
                }
                var selectObject = new QuerySelect(selectAttributeOptions);
                _.set(aggregationOptions, 'select', selectObject);

                if (editorSelect.getValue().groupBy !== undefined) {
                    var groupByAttributes = [];
                    _.forEach(editorSelect.getValue().groupBy, function (groupByAttribute) {
                        groupByAttributes.push(groupByAttribute.attribute);
                    });
                    _.set(aggregationOptions, 'groupBy', groupByAttributes);
                } else {
                    _.set(aggregationOptions, 'groupBy', undefined);
                }

                if (editorAggregate.getValue().aggregateByAttribute !== undefined
                    && editorAggregate.getValue().aggregateByAttribute.attribute !== undefined) {
                    _.set(aggregationOptions, 'aggregateByAttribute',
                        editorAggregate.getValue().aggregateByAttribute.attribute);
                } else {
                    _.set(aggregationOptions, 'aggregateByAttribute', undefined);
                }

                var aggregateByTimePeriodOptions = {};
                _.set(aggregateByTimePeriodOptions, 'minValue',
                    (editorAggregate.getValue().aggregateByTimePeriod.minValue).toUpperCase());
                if (editorAggregate.getValue().aggregateByTimePeriod.maxValue !== undefined) {
                    _.set(aggregateByTimePeriodOptions, 'maxValue',
                        (editorAggregate.getValue().aggregateByTimePeriod.maxValue).toUpperCase());
                } else {
                    _.set(aggregateByTimePeriodOptions, 'maxValue', undefined);
                }
                var aggregateByTimePeriod = new AggregateByTimePeriod(aggregateByTimePeriodOptions);
                _.set(aggregationOptions, 'aggregateByTimePeriod', aggregateByTimePeriod);

                var aggregation = new Aggregation(aggregationOptions);

                _.forEach(editorAnnotation.getValue().annotations, function (annotation) {
                    aggregation.addAnnotation(annotation.annotation);
                });
                self.configurationData.getSiddhiAppConfig().addAggregation(aggregation);

                var textNode = $('#'+i).find('.aggregationNameNode');
                textNode.html(editorInput.getValue().name);

                // close the form aggregation
                self.consoleListManager.removeFormConsole(formConsole);

                // design view container and toggle view button are enabled
                self.designViewContainer.removeClass('disableContainer');
                self.toggleViewButton.removeClass('disableContainer');

            });
            return editorInput.getValue().name;
        };

        /**
         * @function generate properties form for a aggregation
         * @param element selected element(aggregation)
         * @param formConsole Console which holds the form
         * @param formContainer Container which holds the form
         */
        AggregationForm.prototype.generatePropertiesForm = function (element, formConsole, formContainer) {
            var self = this;
            // The design view container is disabled to prevent the user from dropping any elements
            self.designViewContainer.addClass('disableContainer');
            self.toggleViewButton.addClass('disableContainer');

            var id = $(element).parent().attr('id');
            // retrieve the aggregation information from the collection
            var clickedElement = self.configurationData.getSiddhiAppConfig().getAggregation(id);
            if(clickedElement === undefined) {
                var errorMessage = 'unable to find clicked element';
                log.error(errorMessage);
            }

            var savedAnnotations = clickedElement.getAnnotationList();
            var annotations = [];
            _.forEach(savedAnnotations, function (savedAnnotation) {
                annotations.push({annotation: savedAnnotation});
            });

            var storeAnnotation = {};
            if (clickedElement.getStore() !== undefined) {
                var savedStoreAnnotation = clickedElement.getStore();
                var savedStoreAnnotationOptions = savedStoreAnnotation.getOptions();
                var storeOptions = [];
                for (var key in savedStoreAnnotationOptions) {
                    if (savedStoreAnnotationOptions.hasOwnProperty(key)) {
                        storeOptions.push({
                            key: key,
                            value: savedStoreAnnotationOptions[key]
                        });
                    }
                }
                storeAnnotation = {
                    annotationType: savedStoreAnnotation.getType(),
                    storeOptions: storeOptions
                };
            }

            var name = clickedElement.getName();
            var from = clickedElement.getFrom();
            var select = clickedElement.getSelect().getValue();
            var savedGroupByAttributes = clickedElement.getGroupBy();
            var aggregateByAttribute = clickedElement.getAggregateByAttribute();
            var aggregateByTimePeriod = clickedElement.getAggregateByTimePeriod();

            var groupBy = [];
            _.forEach(savedGroupByAttributes, function (savedGroupByAttribute) {
                var groupByAttributeObject = {
                    attribute: savedGroupByAttribute
                };
                groupBy.push(groupByAttributeObject);
            });

            var fillAnnotation = {
                annotations: annotations,
                storeAnnotation: storeAnnotation
            };
            fillAnnotation = self.formUtils.cleanJSONObject(fillAnnotation);

            var fillInput = {
                name : name,
                from : from
            };

            var fillSelect = {};
            if (groupBy.length === 0) {
                fillSelect = {
                    select: select
                };
            } else {
                fillSelect = {
                    select: select,
                    groupBy : groupBy
                };
            }

            var fillAggregate = {};
            if (aggregateByTimePeriod.getMaxValue() === undefined) {
                fillAggregate = {
                    aggregateByAttribute: {
                        attribute : aggregateByAttribute
                    },
                    aggregateByTimePeriod : {
                        minValue: (aggregateByTimePeriod.getMinValue()).toLowerCase()
                    }
                };
            } else {
                fillAggregate = {
                    aggregateByAttribute: {
                        attribute : aggregateByAttribute
                    },
                    aggregateByTimePeriod : {
                        minValue: (aggregateByTimePeriod.getMinValue()).toLowerCase(),
                        maxValue: (aggregateByTimePeriod.getMaxValue()).toLowerCase()
                    }
                };
            }

            formContainer.append('<div class="row"><div id="form-aggregation-annotation" class="col-md-12"></div></div>' +
                '<div class="row"><div id="form-aggregation-input" class="col-md-4"></div>' +
                '<div id="form-aggregation-select" class="col-md-4"></div>' +
                '<div id="form-aggregation-aggregate" class="col-md-4"></div></div>');

            var possibleFromSources = [];
            _.forEach(self.configurationData.getSiddhiAppConfig().streamList, function (stream) {
                possibleFromSources.push(stream.getName());
            });
            _.forEach(self.configurationData.getSiddhiAppConfig().triggerList, function (trigger) {
                possibleFromSources.push(trigger.getName());
            });

            var possibleGroupByAttributes = [];
            var savedSource = self.configurationData.getSiddhiAppConfig().getDefinitionElementByName(from);
            if (savedSource !== undefined) {
                if (savedSource.type !== undefined && (savedSource.type === 'STREAM')) {
                    var timestampAttributeFound = false;
                    if (savedSource.element !== undefined) {
                        _.forEach(savedSource.element.getAttributeList(), function (attribute) {
                            if (attribute.getName().toLowerCase() === 'timestamp') {
                                timestampAttributeFound = true;
                            }
                            possibleGroupByAttributes.push(attribute.getName());
                        });
                    }
                    if (!timestampAttributeFound) {
                        possibleGroupByAttributes.push('timestamp');
                    }
                } else if (savedSource.type !== undefined && (savedSource.type === 'TRIGGER')) {
                    possibleGroupByAttributes.push('triggered_time');
                }
            }

            // generate the form to define a aggregation
            var editorAnnotation = new JSONEditor($(formContainer).find('#form-aggregation-annotation')[0], {
                schema: {
                    type: "object",
                    title: "Aggregation Annotations",
                    properties: {
                        annotations: {
                            propertyOrder: 1,
                            type: "array",
                            format: "table",
                            title: "Add Annotations",
                            uniqueItems: true,
                            minItems: 1,
                            items: {
                                type: "object",
                                title: "Annotation",
                                options: {
                                    disable_properties: true
                                },
                                properties: {
                                    annotation: {
                                        title: "Annotation",
                                        type: "string",
                                        minLength: 1
                                    }
                                }
                            }
                        },
                        storeAnnotation: {
                            propertyOrder: 2,
                            title: "Store Annotation",
                            type: "object",
                            options: {
                                disable_properties: true
                            },
                            properties: {
                                annotationType: {
                                    propertyOrder: 1,
                                    required: true,
                                    title: "Type",
                                    type: "string",
                                    minLength: 1
                                },
                                storeOptions: {
                                    propertyOrder: 2,
                                    required: true,
                                    type: "array",
                                    format: "table",
                                    title: "Options",
                                    uniqueItems: true,
                                    minItems: 1,
                                    items: {
                                        type: "object",
                                        title: "Option",
                                        options: {
                                            disable_properties: true
                                        },
                                        properties: {
                                            key: {
                                                required: true,
                                                title: "Key",
                                                type: "string",
                                                minLength: 1
                                            },
                                            value: {
                                                required: true,
                                                title: "value",
                                                type: "string",
                                                minLength: 1
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                startval: fillAnnotation,
                show_errors: "always",
                display_required_only: true,
                no_additional_properties: true,
                disable_array_delete_all_rows: true,
                disable_array_delete_last_row: true
            });
            var editorInput = new JSONEditor($(formContainer).find('#form-aggregation-input')[0], {
                schema: {
                    type: "object",
                    title: "Aggregation Input",
                    properties: {
                        name: {
                            type: "string",
                            title: "Name",
                            minLength: 1,
                            required: true,
                            propertyOrder: 1
                        },
                        from: {
                            type: "string",
                            title: "From",
                            enum: possibleFromSources,
                            required: true,
                            propertyOrder: 2
                        }
                    }
                },
                startval: fillInput,
                show_errors: "always",
                disable_properties: true,
                display_required_only: true,
                no_additional_properties: true
            });
            var selectScheme = {
                schema: {
                    options: {
                        disable_properties: false
                    },
                    type: "object",
                    title: "Aggregation Select",
                    properties: {
                        select: {
                            propertyOrder: 1,
                            title: "Select",
                            required: true,
                            oneOf: [
                                {
                                    $ref: "#/definitions/selectAll",
                                    title: "All Attributes"
                                },
                                {
                                    $ref: "#/definitions/selectUserDefined",
                                    title: "User Defined Attributes"
                                }
                            ]
                        },
                        groupBy: {
                            propertyOrder: 2,
                            type: "array",
                            format: "table",
                            title: "Group By Attributes",
                            uniqueItems: true,
                            minItems: 1,
                            items: {
                                type: "object",
                                title: 'Attribute',
                                properties: {
                                    attribute: {
                                        type: 'string',
                                        title: 'Attribute Name',
                                        enum: possibleGroupByAttributes
                                    }
                                }
                            }
                        }
                    },
                    definitions: {
                        selectUserDefined: {
                            required: true,
                            type: "array",
                            format: "table",
                            title: "Select Attributes",
                            uniqueItems: true,
                            minItems: 1,
                            items: {
                                title: "Value Set",
                                type: "object",
                                properties: {
                                    expression: {
                                        title: "Expression",
                                        type: "string",
                                        minLength: 1
                                    },
                                    as: {
                                        title: "As",
                                        type: "string"
                                    }
                                }
                            }
                        },
                        selectAll: {
                            type: "string",
                            title: "Select All Attributes",
                            template: '*'
                        }
                    }
                },
                show_errors: "always",
                startval: fillSelect,
                disable_properties: false,
                disable_array_delete_all_rows: true,
                disable_array_delete_last_row: true,
                display_required_only: true,
                no_additional_properties: true
            };
            var editorSelect = new JSONEditor($(formContainer).find('#form-aggregation-select')[0], selectScheme);
            var aggregateScheme = {
                schema: {
                    type: "object",
                    title: "Aggregate By",
                    properties: {
                        aggregateByAttribute: {
                            type: "object",
                            title: "Aggregate by Attribute",
                            propertyOrder: 1,
                            properties: {
                                attribute: {
                                    required: true,
                                    type: "string",
                                    title: "Attribute Name",
                                    enum: possibleGroupByAttributes
                                }
                            }
                        },
                        aggregateByTimePeriod: {
                            required: true,
                            type: "object",
                            title: "Aggregate by Time Period",
                            propertyOrder: 2,
                            options: {
                                disable_properties: false
                            },
                            properties: {
                                minValue: {
                                    type: "string",
                                    title: "Starting Time Value",
                                    required: true,
                                    enum: [
                                        "seconds",
                                        "minutes",
                                        "hours",
                                        "days",
                                        "weeks",
                                        "months",
                                        "years"
                                    ],
                                    default: "seconds"
                                },
                                maxValue: {
                                    type: "string",
                                    title: "Ending Time Value",
                                    enum: [
                                        "seconds",
                                        "minutes",
                                        "hours",
                                        "days",
                                        "weeks",
                                        "months",
                                        "years"
                                    ],
                                    default: "seconds"
                                }
                            }
                        }
                    }
                },
                startval: fillAggregate,
                show_errors: "always",
                disable_properties: false,
                display_required_only: true,
                no_additional_properties: true
            };
            var editorAggregate =
                new JSONEditor($(formContainer).find('#form-aggregation-aggregate')[0], aggregateScheme);

            var fromNode = editorInput.getEditor('root.from');
            editorInput.watch('root.from',function() {
                var fromValue = fromNode.getValue();
                var inputElement = self.configurationData.getSiddhiAppConfig().getDefinitionElementByName(fromValue);
                ElementUtils.prototype.removeAllElements(possibleGroupByAttributes);
                if (inputElement !== undefined) {
                    if (inputElement.type !== undefined && (inputElement.type === 'STREAM')) {
                        var timestampAttributeFound = false;
                        if (inputElement.element !== undefined) {
                            _.forEach(inputElement.element.getAttributeList(), function (attribute) {
                                if (attribute.getName().toLowerCase() === 'timestamp') {
                                    timestampAttributeFound = true;
                                }
                                possibleGroupByAttributes.push(attribute.getName());
                            });
                        }
                        if (!timestampAttributeFound) {
                            possibleGroupByAttributes.push('timestamp');
                        }
                    } else if (inputElement.type !== undefined && (inputElement.type === 'TRIGGER')) {
                        possibleGroupByAttributes.push('triggered_time');
                    }
                }

                $(formContainer).find('#form-aggregation-select').empty();
                editorSelect = new JSONEditor($(formContainer).find('#form-aggregation-select')[0], selectScheme);
                $(formContainer).find('#form-aggregation-aggregate').empty();
                editorAggregate =
                    new JSONEditor($(formContainer).find('#form-aggregation-aggregate')[0], aggregateScheme);
                editorAggregate.setValue({aggregateByTimePeriod: editorAggregate.getValue().aggregateByTimePeriod});
            });

            formContainer.append('<div id="form-submit"><button type="button" ' +
                'class="btn btn-default">Submit</button></div>' +
                '<div id="form-cancel"><button type="button" class="btn btn-default">Cancel</button></div>');

            // 'Submit' button action
            var submitButtonElement = $(formContainer).find('#form-submit')[0];
            submitButtonElement.addEventListener('click', function () {
                var annotationErrors = editorAnnotation.validate();
                var inputErrors = editorInput.validate();
                var selectErrors = editorSelect.validate();
                var aggregateErrors = editorAggregate.validate();
                if(annotationErrors.length || inputErrors.length || selectErrors.length || aggregateErrors.length) {
                    return;
                }
                var isAggregationNameUsed =
                    self.formUtils.isDefinitionElementNameUnique(editorInput.getValue().name, clickedElement.getId());
                if(isAggregationNameUsed) {
                    alert("Aggregation name \"" + editorInput.getValue().name + "\" is already used.");
                    return;
                }

                var configAnnotation = editorAnnotation.getValue();
                var configInput = editorInput.getValue();
                var configSelect = editorSelect.getValue();
                var configAggregate = editorAggregate.getValue();

                clickedElement.clearAnnotationList();
                _.forEach(configAnnotation.annotations, function (annotation) {
                    clickedElement.addAnnotation(annotation.annotation);
                });

                // update selected aggregation model
                clickedElement.setName(configInput.name);
                clickedElement.setFrom(configInput.from);

                // add the store annotation for aggregation
                if (configAnnotation.storeAnnotation !== undefined) {
                    var optionsMap = {};
                    _.forEach(configAnnotation.storeAnnotation.storeOptions, function (option) {
                        optionsMap[option.key] = option.value;
                    });

                    var storeAnnotationOptions = {};
                    _.set(storeAnnotationOptions, 'type', configAnnotation.storeAnnotation.annotationType);
                    _.set(storeAnnotationOptions, 'options', optionsMap);

                    var storeAnnotation = new StoreAnnotation(storeAnnotationOptions);
                    clickedElement.setStore(storeAnnotation);
                } else {
                    clickedElement.setStore(undefined);
                }
                
                var selectAttributeOptions = {};
                if (configSelect.select instanceof Array) {
                    _.set(selectAttributeOptions, 'type', 'USER_DEFINED');
                    _.set(selectAttributeOptions, 'value', configSelect.select);
                } else if (configSelect.select === "*") {
                    _.set(selectAttributeOptions, 'type', 'ALL');
                    _.set(selectAttributeOptions, 'value', configSelect.select);
                } else {
                    console.log("Value other than \"USER_DEFINED\" and \"ALL\" received!");
                }
                var selectObject = new QuerySelect(selectAttributeOptions);
                clickedElement.setSelect(selectObject);

                if (configSelect.groupBy !== undefined) {
                    var groupByAttributes = [];
                    _.forEach(configSelect.groupBy, function (groupByAttribute) {
                        groupByAttributes.push(groupByAttribute.attribute);
                    });
                    clickedElement.setGroupBy(groupByAttributes);
                } else {
                    clickedElement.setGroupBy(undefined);
                }

                if (configAggregate.aggregateByAttribute !== undefined
                    && configAggregate.aggregateByAttribute.attribute !== undefined) {
                    clickedElement.setAggregateByAttribute(configAggregate.aggregateByAttribute.attribute);
                } else {
                    clickedElement.setAggregateByAttribute(undefined);
                }

                var aggregateByTimePeriodOptions = {};
                _.set(aggregateByTimePeriodOptions, 'minValue',
                    (configAggregate.aggregateByTimePeriod.minValue).toUpperCase());
                if (configAggregate.aggregateByTimePeriod.maxValue !== undefined) {
                    _.set(aggregateByTimePeriodOptions, 'maxValue',
                        (configAggregate.aggregateByTimePeriod.maxValue).toUpperCase());
                } else {
                    _.set(aggregateByTimePeriodOptions, 'maxValue', undefined);
                }
                var aggregateByTimePeriod = new AggregateByTimePeriod(aggregateByTimePeriodOptions);
                clickedElement.setAggregateByTimePeriod(aggregateByTimePeriod);

                var textNode = $(element).parent().find('.aggregationNameNode');
                textNode.html(configInput.name);

                // design view container and toggle view button are enabled
                self.designViewContainer.removeClass('disableContainer');
                self.toggleViewButton.removeClass('disableContainer');

                // close the form aggregation
                self.consoleListManager.removeFormConsole(formConsole);
            });

            // 'Cancel' button action
            var cancelButtonElement = $(formContainer).find('#form-cancel')[0];
            cancelButtonElement.addEventListener('click', function () {
                // design view container and toggle view button are enabled
                self.designViewContainer.removeClass('disableContainer');
                self.toggleViewButton.removeClass('disableContainer');

                // close the form aggregation
                self.consoleListManager.removeFormConsole(formConsole);
            });
        };

        return AggregationForm;
    });
