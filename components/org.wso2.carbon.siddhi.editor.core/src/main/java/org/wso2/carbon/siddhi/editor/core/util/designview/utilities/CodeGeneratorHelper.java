/*
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

package org.wso2.carbon.siddhi.editor.core.util.designview.utilities;

import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.AttributeConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.StoreConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.attributesselection.AttributesSelectionConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.attributesselection.SelectedAttribute;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.attributesselection.UserDefinedSelectionConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.QueryOrderByConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.input.QueryInputConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.input.join.JoinConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.input.join.JoinElementConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.input.patternsequence.PatternSequenceConditionConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.input.patternsequence.PatternSequenceConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.input.windowfilterprojection.WindowFilterProjectionConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.output.QueryOutputConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.output.types.DeleteOutputConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.output.types.InsertOutputConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.output.types.UpdateInsertIntoOutputConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.output.types.setattribute.SetAttributeConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.streamhandler.FilterConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.streamhandler.FunctionWindowConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.query.streamhandler.StreamHandlerConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.sourcesink.mapper.MapperConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.constants.AttributeSelection;
import org.wso2.carbon.siddhi.editor.core.util.designview.constants.CodeGeneratorConstants;
import org.wso2.carbon.siddhi.editor.core.util.designview.constants.SiddhiStringBuilderConstants;
import org.wso2.carbon.siddhi.editor.core.util.designview.exceptions.CodeGenerationException;

import java.util.List;
import java.util.Map;

/**
 * Helper that contains generic reusable utility methods
 * for the CodeGenerator class to build a Siddhi app string
 */
public class CodeGeneratorHelper {

    /**
     * Generates a string representation of a list of attributes for a definition
     * Attributes Example - (name string, age int, ...)
     *
     * @param attributes The list of AttributeConfig objects to be converted
     * @return The converted attributes as a string
     * @throws CodeGenerationException Error while generating code
     */
    public static String getAttributes(List<AttributeConfig> attributes) throws CodeGenerationException {
        if (attributes == null || attributes.isEmpty()) {
            throw new CodeGenerationException("The given AttributeConfig list is null/empty");
        }

        StringBuilder stringBuilder = new StringBuilder();
        int attributesLeft = attributes.size();
        for (AttributeConfig attribute : attributes) {
            if (attribute == null) {
                throw new CodeGenerationException("The given AttributeConfig object is null");
            } else if (attribute.getName() == null || attribute.getName().isEmpty()) {
                throw new CodeGenerationException("The name of the given AttributeConfig object is null/empty");
            } else if (attribute.getType() == null || attribute.getType().isEmpty()) {
                throw new CodeGenerationException("The type of the given AttributeConfig object is null/empty");
            }
            stringBuilder.append(attribute.getName())
                    .append(SiddhiStringBuilderConstants.SPACE)
                    .append(attribute.getType().toLowerCase());
            if (attributesLeft != 1) {
                stringBuilder.append(SiddhiStringBuilderConstants.COMMA)
                        .append(SiddhiStringBuilderConstants.SPACE);
            }
            attributesLeft--;
        }
        return stringBuilder.toString();
    }

    /**
     * Generates a string representation of the annotations of a Siddhi element
     *
     * @param annotations The list of AnnotationConfig objects to be converted
     * @return The string representation of all the annotations
     */
    public static String getAnnotations(List<String> annotations) {
        if (annotations == null || annotations.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }

        StringBuilder annotationsStringBuilder = new StringBuilder();
        for (String annotation : annotations) {
            annotationsStringBuilder.append(annotation)
                    .append(SiddhiStringBuilderConstants.NEW_LINE);
        }

        return annotationsStringBuilder.toString();
    }

    /**
     * Generates a string representation of a Siddhi store annotation
     *
     * @param store The StoreConfig instance to be converted
     * @return The string representation of a store annotation
     * @throws CodeGenerationException Error while generating code
     */
    public static String getStore(StoreConfig store) throws CodeGenerationException {
        if (store == null) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        } else if (store.getType() == null || store.getType().isEmpty()) {
            throw new CodeGenerationException("The type for the given StoreConfig object is null/empty");
        } else if (store.getOptions() == null || store.getOptions().isEmpty()) {
            throw new CodeGenerationException("The options map for the given StoreConfig object is null/empty");
        }

        StringBuilder storeStringBuilder = new StringBuilder();

        storeStringBuilder.append(SiddhiStringBuilderConstants.STORE_ANNOTATION)
                .append(store.getType())
                .append(SiddhiStringBuilderConstants.SINGLE_QUOTE)
                .append(SiddhiStringBuilderConstants.COMMA)
                .append(SiddhiStringBuilderConstants.SPACE);
        Map<String, String> options = store.getOptions();
        int optionsLeft = options.size();
        for (Map.Entry<String, String> entry : options.entrySet()) {
            storeStringBuilder.append(entry.getKey())
                    .append(SiddhiStringBuilderConstants.EQUAL)
                    .append(SiddhiStringBuilderConstants.SINGLE_QUOTE)
                    .append(entry.getValue())
                    .append(SiddhiStringBuilderConstants.SINGLE_QUOTE);
            if (optionsLeft != 1) {
                storeStringBuilder.append(SiddhiStringBuilderConstants.COMMA)
                        .append(SiddhiStringBuilderConstants.SPACE);
            }
            optionsLeft--;
        }
        storeStringBuilder.append(SiddhiStringBuilderConstants.CLOSE_BRACKET)
                .append(SiddhiStringBuilderConstants.NEW_LINE);

        return storeStringBuilder.toString();
    }

    /**
     * Generate a string of parameters for a specific window/function
     * Example of parameter: (10 min, 4, 'regex', ...)
     *
     * @param parameters The list of parameters to be converted
     * @return The string of the parameters separated by a comma(,)
     */
    public static String getParameterList(List<String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }

        StringBuilder parametersStringBuilder = new StringBuilder();
        int parametersLeft = parameters.size();
        for (String parameter : parameters) {
            parametersStringBuilder.append(parameter);
            if (parametersLeft != 1) {
                parametersStringBuilder.append(SiddhiStringBuilderConstants.COMMA)
                        .append(SiddhiStringBuilderConstants.SPACE);
            }
            parametersLeft--;
        }

        return parametersStringBuilder.toString();
    }

    /**
     * Generates a string representation of a list of annotations for an aggregation definition
     *
     * @param annotations The list of annotations to be converted
     * @return The string representation of the annotations for an aggregation defintion
     */
    public static String getAggregationAnnotations(List<String> annotations) {
        if (annotations == null || annotations.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }

        StringBuilder annotationsStringBuilder = new StringBuilder();
        for (String annotation : annotations) {
            // The reason why generating annotations for aggregations is in a different
            // method is because the '@PrimaryKey' annotation is automatically generated
            // in Siddhi runtime for Aggregation Definitions. This is done to avoid that.
            if (annotation.toLowerCase().contains(CodeGeneratorConstants.PRIMARY_KEY_ANNOTATION)) {
                continue;
            }
            annotationsStringBuilder.append(annotation)
                    .append(SiddhiStringBuilderConstants.NEW_LINE);
        }

        return annotationsStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a map annotation from a MapperConfig object
     *
     * @param mapper         The MapperConfig object to be converted
     * @param annotationType The annotation type the MapperConfig object belongs to (SINK or SOURCE)
     * @return The string representation of the MapperConfig object
     * @throws CodeGenerationException Error while generating code
     */
    public static String getMapper(MapperConfig mapper, String annotationType) throws CodeGenerationException {
        if (mapper.getType() == null || mapper.getType().isEmpty()) {
            throw new CodeGenerationException("The map type of the given MapperConfig object is null/empty");
        }

        StringBuilder mapperStringBuilder = new StringBuilder();
        mapperStringBuilder.append(SiddhiStringBuilderConstants.MAP_ANNOTATION)
                .append(mapper.getType())
                .append(SiddhiStringBuilderConstants.SINGLE_QUOTE);

        if (mapper.getOptions() != null && !mapper.getOptions().isEmpty()) {
            mapperStringBuilder.append(SiddhiStringBuilderConstants.COMMA)
                    .append(SiddhiStringBuilderConstants.SPACE)
                    .append(getParameterList(mapper.getOptions()));
        }

        if (mapper.getAttributes() != null && !mapper.getAttributes().isEmpty()) {
            mapperStringBuilder.append(SiddhiStringBuilderConstants.COMMA)
                    .append(SiddhiStringBuilderConstants.SPACE);
            if (annotationType.equalsIgnoreCase(CodeGeneratorConstants.SOURCE)) {
                mapperStringBuilder.append(SiddhiStringBuilderConstants.ATTRIBUTES_ANNOTATION);
            } else if (annotationType.equalsIgnoreCase(CodeGeneratorConstants.SINK)) {
                mapperStringBuilder.append(SiddhiStringBuilderConstants.PAYLOAD_ANNOTATION);
            }

            mapperStringBuilder.append(getParameterList(mapper.getAttributes()))
                    .append(SiddhiStringBuilderConstants.CLOSE_BRACKET);
        }

        mapperStringBuilder.append(SiddhiStringBuilderConstants.CLOSE_BRACKET);

        return mapperStringBuilder.toString();
    }

    /**
     * Generates a string representation of a query input using the given QueryInputConfig object
     *
     * @param queryInput The QueryInputConfig object to be converted
     * @return The string representation of the given QueryInputConfig object
     * @throws CodeGenerationException Error while generating code
     */
    public static String getQueryInput(QueryInputConfig queryInput) throws CodeGenerationException {
        if (queryInput == null) {
            throw new CodeGenerationException("The given QueryInputConfig object is null");
        } else if (queryInput.getType() == null || queryInput.getType().isEmpty()) {
            throw new CodeGenerationException("The type value of the given QueryInputConfig object is null/empty");
        }

        StringBuilder queryInputStringBuilder = new StringBuilder();

        switch (queryInput.getType().toUpperCase()) {
            case CodeGeneratorConstants.WINDOW:
            case CodeGeneratorConstants.FILTER:
            case CodeGeneratorConstants.PROJECTION:
                WindowFilterProjectionConfig windowFilterProjectionQuery = (WindowFilterProjectionConfig) queryInput;
                queryInputStringBuilder.append(getWindowFilterProjectionQueryInput(windowFilterProjectionQuery));
                break;
            case CodeGeneratorConstants.JOIN:
                JoinConfig joinQuery = (JoinConfig) queryInput;
                queryInputStringBuilder.append(getJoinQueryInput(joinQuery));
                break;
            case CodeGeneratorConstants.PATTERN:
            case CodeGeneratorConstants.SEQUENCE:
                PatternSequenceConfig patternSequence = (PatternSequenceConfig) queryInput;
                queryInputStringBuilder.append(getPatternSequenceInput(patternSequence));
                break;
            default:
                throw new CodeGenerationException("Unidentified QueryInputConfig type: " + queryInput.getType());
        }

        return queryInputStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of the given WindowFilterProjectionConfig object
     *
     * @param windowFilterProjection The WindowFilterProjectCofig to be converted
     * @return The string representation of the given WindowFilterProjectionConfig object
     * @throws CodeGenerationException Error while generating code
     */
    private static String getWindowFilterProjectionQueryInput(WindowFilterProjectionConfig windowFilterProjection)
            throws CodeGenerationException {
        if (windowFilterProjection == null) {
            throw new CodeGenerationException("The given WindowFilterProjection object is null");
        } else if (windowFilterProjection.getFrom() == null || windowFilterProjection.getFrom().isEmpty()) {
            throw new CodeGenerationException("The 'from' value of the given WindowFilterProjection object is null");
        }

        StringBuilder windowFilterProjectionStringBuilder = new StringBuilder();
        windowFilterProjectionStringBuilder.append(SiddhiStringBuilderConstants.FROM)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(windowFilterProjection.getFrom())
                .append(getStreamHandlerList(windowFilterProjection.getStreamHandlerList()));

        return windowFilterProjectionStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a JoinConfig object
     *
     * @param join The JoinConfig object to be converted
     * @return The string representation of the given JoinConfig object
     * @throws CodeGenerationException Error while generating code
     */
    private static String getJoinQueryInput(JoinConfig join) throws CodeGenerationException {
        if (join == null) {
            throw new CodeGenerationException("The given JoinConfig object is null");
        } else if (join.getJoinWith() == null || join.getJoinType().isEmpty()) {
            throw new CodeGenerationException("The 'joinWith' value of the given JoinConfig object is null/empty");
        } else if (join.getJoinType() == null || join.getJoinType().isEmpty()) {
            throw new CodeGenerationException("The 'joinType' value for the given JoinConfig object is null/empty");
        } else if (join.getOn() == null || join.getOn().isEmpty()) {
            throw new CodeGenerationException("The 'on' value for the given JoinConfig object is null/empty");
        } else if (join.getLeft() == null || join.getRight() == null) {
            throw new CodeGenerationException("The left/right JoinElementConfig for the given" +
                    " JoinConfig object is null");
        } else if (join.getLeft().getType() == null || join.getLeft().getType().isEmpty()) {
            throw new CodeGenerationException("The type value of the left JoinElementConfig given is null/empty");
        } else if (join.getRight().getType() == null || join.getRight().getType().isEmpty()) {
            throw new CodeGenerationException("The type value of the right JoinElementConfig given is null/empty");
        }

        StringBuilder joinStringBuilder = new StringBuilder();
        joinStringBuilder.append(SiddhiStringBuilderConstants.FROM)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(getJoinElement(join.getLeft()))
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(getJoinType(join.getJoinType()))
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(getJoinElement(join.getRight()))
                .append(SiddhiStringBuilderConstants.NEW_LINE)
                .append(SiddhiStringBuilderConstants.TAB_SPACE)
                .append(SiddhiStringBuilderConstants.ON)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(join.getOn());

        if (join.getJoinWith().equalsIgnoreCase(CodeGeneratorConstants.AGGREGATION)) {
            if (join.getWithin() == null || join.getWithin().isEmpty()) {
                throw new CodeGenerationException("The 'within' attribute for the given join" +
                        " aggregation query is null/empty");
            } else if (join.getPer() == null || join.getPer().isEmpty()) {
                throw new CodeGenerationException("The 'per' attribute for the given join " +
                        "aggregation query is null/empty");
            }

            joinStringBuilder.append(SiddhiStringBuilderConstants.NEW_LINE)
                    .append(SiddhiStringBuilderConstants.TAB_SPACE)
                    .append(SiddhiStringBuilderConstants.WITHIN)
                    .append(SiddhiStringBuilderConstants.SPACE)
                    .append(join.getWithin())
                    .append(SiddhiStringBuilderConstants.NEW_LINE)
                    .append(SiddhiStringBuilderConstants.TAB_SPACE)
                    .append(SiddhiStringBuilderConstants.PER)
                    .append(SiddhiStringBuilderConstants.SPACE)
                    .append(join.getPer());
        }

        return joinStringBuilder.toString();
    }

    /**
     * Generates a string representation of the given JoinElementConfig object
     *
     * @param joinElement The JoinElementConfig object to be converted
     * @return The string representation of the given JoinElementConfig object
     * @throws CodeGenerationException Error while generating code
     */
    private static String getJoinElement(JoinElementConfig joinElement) throws CodeGenerationException {
        if (joinElement == null) {
            throw new CodeGenerationException("The JoinElementConfig object given is null");
        } else if (joinElement.getFrom() == null || joinElement.getFrom().isEmpty()) {
            throw new CodeGenerationException("The 'from' value for the given JoinElementConfig object is null/empty");
        }

        StringBuilder joinElementStringBuilder = new StringBuilder();

        joinElementStringBuilder.append(joinElement.getFrom())
                .append(getStreamHandlerList(joinElement.getStreamHandlerList()));

        if (joinElement.getAs() != null && !joinElement.getAs().isEmpty()) {
            joinElementStringBuilder.append(SiddhiStringBuilderConstants.SPACE)
                    .append(SiddhiStringBuilderConstants.AS)
                    .append(SiddhiStringBuilderConstants.SPACE)
                    .append(joinElement.getAs());
        }

        if (joinElement.isUnidirectional()) {
            joinElementStringBuilder.append(SiddhiStringBuilderConstants.SPACE)
                    .append(SiddhiStringBuilderConstants.UNIDIRECTIONAL);
        }

        return joinElementStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a list of StreamHandlerConfig objects
     *
     * @param streamHandlerList The list of StreamHandlerConfig objects to be converted
     * @return The result string representation of the list of StreamHandlerConfig objects
     * @throws CodeGenerationException Error while generating code
     */
    private static String getStreamHandlerList(List<StreamHandlerConfig> streamHandlerList)
            throws CodeGenerationException {
        if (streamHandlerList == null || streamHandlerList.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }

        StringBuilder streamhandlerListStringBuilder = new StringBuilder();

        for (StreamHandlerConfig streamHandler : streamHandlerList) {
            streamhandlerListStringBuilder.append(getStreamHandler(streamHandler));
        }

        return streamhandlerListStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a given StreamHandlerConfig object
     *
     * @param streamHandler The StreamHandlerConfig object to be converted
     * @return The string representation of the given StreamHandlerConfig object
     * @throws CodeGenerationException Error while generating code
     */
    private static String getStreamHandler(StreamHandlerConfig streamHandler) throws CodeGenerationException {
        if (streamHandler == null) {
            throw new CodeGenerationException("The given StreamHandlerConfig object is null");
        } else if (streamHandler.getType() == null || streamHandler.getType().isEmpty()) {
            throw new CodeGenerationException("The type variable for the given StreamHandlerConfig" +
                    " object is null/empty");
        }

        StringBuilder streamHandlerStringBuilder = new StringBuilder();

        switch (streamHandler.getType().toUpperCase()) {
            case CodeGeneratorConstants.FILTER:
                FilterConfig filter = (FilterConfig) streamHandler;
                streamHandlerStringBuilder.append(SiddhiStringBuilderConstants.OPEN_SQUARE_BRACKET)
                        .append(filter.getValue())
                        .append(SiddhiStringBuilderConstants.CLOSE_SQUARE_BRACKET);
                break;
            case CodeGeneratorConstants.FUNCTION:
                FunctionWindowConfig function = (FunctionWindowConfig) streamHandler;
                streamHandlerStringBuilder.append(SiddhiStringBuilderConstants.HASH)
                        .append(function.getValue().getFunction())
                        .append(SiddhiStringBuilderConstants.OPEN_BRACKET)
                        .append(getParameterList(function.getValue().getParameters()))
                        .append(SiddhiStringBuilderConstants.CLOSE_BRACKET);
                break;
            case CodeGeneratorConstants.WINDOW:
                FunctionWindowConfig window = (FunctionWindowConfig) streamHandler;
                streamHandlerStringBuilder.append(SiddhiStringBuilderConstants.HASH)
                        .append(SiddhiStringBuilderConstants.WINDOW)
                        .append(SiddhiStringBuilderConstants.FULL_STOP)
                        .append(window.getValue().getFunction())
                        .append(SiddhiStringBuilderConstants.OPEN_BRACKET)
                        .append(getParameterList(window.getValue().getParameters()))
                        .append(SiddhiStringBuilderConstants.CLOSE_BRACKET);
                break;
            default:
                throw new CodeGenerationException("Unidentified StreamHandlerConfig type: "
                        + streamHandler.getType());
        }

        return streamHandlerStringBuilder.toString();
    }

    /**
     * Generates a 'Siddhi' string representation of the given joinType string
     *
     * @param joinType The join type as a string
     * @return The Siddhi string representation of the given join type
     * @throws CodeGenerationException Error while generating code
     */
    private static String getJoinType(String joinType) throws CodeGenerationException {
        if (joinType == null || joinType.isEmpty()) {
            throw new CodeGenerationException("The joinType value for the given join query is null/empty");
        }

        switch (joinType.toUpperCase()) {
            case CodeGeneratorConstants.JOIN:
                return SiddhiStringBuilderConstants.JOIN;
            case CodeGeneratorConstants.LEFT_OUTER:
                return SiddhiStringBuilderConstants.LEFT_OUTER_JOIN;
            case CodeGeneratorConstants.RIGHT_OUTER:
                return SiddhiStringBuilderConstants.RIGHT_OUTER_JOIN;
            case CodeGeneratorConstants.FULL_OUTER:
                return SiddhiStringBuilderConstants.FULL_OUTER_JOIN;
            default:
                throw new CodeGenerationException("Invalid Join Type: " + joinType);
        }
    }

    /**
     * Generates a Siddhi string representation of a given PatternSequenceConfig object
     *
     * @param patternSequence The PatterSequenceConfig object to be converted
     * @return The string representation of the given PatternSequenceConfig object
     * @throws CodeGenerationException Error while generating code
     */
    private static String getPatternSequenceInput(PatternSequenceConfig patternSequence)
            throws CodeGenerationException {
        if (patternSequence == null) {
            throw new CodeGenerationException("The given PatternSequenceConfig object is null");
        } else if (patternSequence.getLogic() == null || patternSequence.getLogic().isEmpty()) {
            throw new CodeGenerationException("The 'logic' attribute for the given " +
                    "PatternSequenceConfig object is null/empty");
        } else if (patternSequence.getConditionList() == null || patternSequence.getConditionList().isEmpty()) {
            throw new CodeGenerationException("The condition list for the given PatternSequenceConfig" +
                    " object is null/empty");
        }

        String logic = patternSequence.getLogic();

        for (PatternSequenceConditionConfig condition : patternSequence.getConditionList()) {
            logic = logic.replace(condition.getConditionId(), getPatternSequenceConditionLogic(condition));
        }

        return logic;
    }

    /**
     * Generates a Siddhi string representation of a given PatternSequenceConditionConfig object
     *
     * @param condition The PatternSequenceConditionConfig object to be converted
     * @return The Siddhi string representation of the given PatternSequenceConfig object
     * @throws CodeGenerationException Error while generating code
     */
    private static String getPatternSequenceConditionLogic(PatternSequenceConditionConfig condition)
            throws CodeGenerationException {
        if (condition == null) {
            throw new CodeGenerationException("The given PatternSequenceConditionConfig object is null");
        } else if (condition.getStreamName() == null || condition.getStreamName().isEmpty()) {
            throw new CodeGenerationException("The stream name of the given PatternSequenceConditionConfig" +
                    " object is null/empty");
        }

        StringBuilder patternSequenceConditionStringBuilder = new StringBuilder();

        patternSequenceConditionStringBuilder.append(condition.getStreamName())
                .append(getStreamHandlerList(condition.getStreamHandlerList()));

        return patternSequenceConditionStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of an AttributesSelectionConfig object
     * Example For Query Select - select * ...
     *
     * @param attributesSelection The AttributesSelectionConfig object to be converted
     * @return The converted Siddhi string representation of the given AttributesSelectionConfig object
     * @throws CodeGenerationException Error while generating code
     */
    public static String getQuerySelect(AttributesSelectionConfig attributesSelection) throws CodeGenerationException {
        if (attributesSelection == null) {
            throw new CodeGenerationException("The given AttributeSelectionConfig object is null");
        }

        StringBuilder attributesSelectionStringBuilder = new StringBuilder();

        attributesSelectionStringBuilder.append(SiddhiStringBuilderConstants.SELECT)
                .append(SiddhiStringBuilderConstants.SPACE);

        if (attributesSelection.getType() == null || attributesSelection.getType().isEmpty()) {
            throw new CodeGenerationException("The type value of the given AttributeSelectionConfig is null/empty");
        }

        switch (attributesSelection.getType().toUpperCase()) {
            case AttributeSelection.TYPE_USER_DEFINED:
                UserDefinedSelectionConfig userDefinedSelection = (UserDefinedSelectionConfig) attributesSelection;
                attributesSelectionStringBuilder.append(getUserDefinedSelection(userDefinedSelection));
                break;
            case AttributeSelection.TYPE_ALL:
                attributesSelectionStringBuilder.append(SiddhiStringBuilderConstants.ALL);
                break;
            default:
                throw new CodeGenerationException("Undefined Attribute Selection Type:"
                        + attributesSelection.getType());
        }

        return attributesSelectionStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a UserDefinedSelectionConfig object
     * Example of the UserDefinedSelectionConfig - select name, avg(age) as avgAge, ...
     *
     * @param userDefinedSelection The UserDefinedSelectionConfig object to be converted
     * @return The Siddhi string representation of the given UserDefinedSelectionConfig
     * @throws CodeGenerationException Error while generating code
     */
    private static String getUserDefinedSelection(UserDefinedSelectionConfig userDefinedSelection)
            throws CodeGenerationException {
        if (userDefinedSelection == null || userDefinedSelection.getValue() == null ||
                userDefinedSelection.getValue().isEmpty()) {
            throw new CodeGenerationException("The given UserDefinedSelection object is null/empty");
        }

        StringBuilder userDefinedSelectionStringBuilder = new StringBuilder();

        int attributesLeft = userDefinedSelection.getValue().size();
        for (SelectedAttribute attribute : userDefinedSelection.getValue()) {
            if (attribute.getExpression() == null || attribute.getExpression().isEmpty()) {
                throw new CodeGenerationException("The 'expression' value for the given" +
                        " SelectedAttribute object is null/empty");
            }
            userDefinedSelectionStringBuilder.append(attribute.getExpression());
            if (attribute.getAs() != null && !attribute.getAs().isEmpty() &&
                    !attribute.getAs().equals(attribute.getExpression())) {
                userDefinedSelectionStringBuilder.append(SiddhiStringBuilderConstants.SPACE)
                        .append(SiddhiStringBuilderConstants.AS)
                        .append(SiddhiStringBuilderConstants.SPACE)
                        .append(attribute.getAs());
            }
            if (attributesLeft != 1) {
                userDefinedSelectionStringBuilder.append(SiddhiStringBuilderConstants.COMMA)
                        .append(SiddhiStringBuilderConstants.SPACE);
            }
            attributesLeft--;
        }

        return userDefinedSelectionStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a group by list of a query
     *
     * @param groupByList The list of group by attributes to be converted
     * @return The Siddhi string representation of the given group by list
     */
    public static String getQueryGroupBy(List<String> groupByList) {
        if (groupByList == null || groupByList.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }

        StringBuilder groupByListStringBuilder = new StringBuilder();
        groupByListStringBuilder.append(SiddhiStringBuilderConstants.GROUP)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(SiddhiStringBuilderConstants.BY)
                .append(SiddhiStringBuilderConstants.SPACE);
        groupByListStringBuilder.append(getParameterList(groupByList));
        return groupByListStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a order by list of a query
     *
     * @param orderByList The order by list to be converted
     * @return The Siddhi string representation of the given order by config list
     * @throws CodeGenerationException Error while generating code
     */
    public static String getQueryOrderBy(List<QueryOrderByConfig> orderByList) throws CodeGenerationException {
        if (orderByList == null || orderByList.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }

        StringBuilder orderByListStringBuilder = new StringBuilder();
        orderByListStringBuilder.append(SiddhiStringBuilderConstants.ORDER)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(SiddhiStringBuilderConstants.BY)
                .append(SiddhiStringBuilderConstants.SPACE);

        int orderByAttributesLeft = orderByList.size();
        for (QueryOrderByConfig orderByAttribute : orderByList) {
            if (orderByAttribute == null) {
                throw new CodeGenerationException("The given QueryOrderByConfig is null");
            } else if (orderByAttribute.getValue() == null || orderByAttribute.getValue().isEmpty()) {
                throw new CodeGenerationException("The 'value' for the QueryOrderByConfig object given is null/empty");
            }

            orderByListStringBuilder.append(orderByAttribute.getValue());
            if (orderByAttribute.getOrder() != null && !orderByAttribute.getOrder().isEmpty()) {
                orderByListStringBuilder.append(SiddhiStringBuilderConstants.SPACE)
                        .append(orderByAttribute.getOrder());
            }

            if (orderByAttributesLeft != 1) {
                orderByListStringBuilder.append(SiddhiStringBuilderConstants.COMMA)
                        .append(SiddhiStringBuilderConstants.SPACE);
            }
            orderByAttributesLeft--;
        }

        return orderByListStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of the given limit value
     *
     * @param limit The limit value to be converted
     * @return The Siddhi representation of the given query limit
     */
    public static String getQueryLimit(long limit) {
        if (limit != 0) {
            StringBuilder limitStringBuilder = new StringBuilder();
            limitStringBuilder.append(SiddhiStringBuilderConstants.LIMIT)
                    .append(SiddhiStringBuilderConstants.SPACE)
                    .append(limit);
            return limitStringBuilder.toString();
        }
        return SiddhiStringBuilderConstants.EMPTY_STRING;
    }

    /**
     * Generates a Siddhi string representation of the given having value
     *
     * @param having The having value given
     * @return The Siddhi string representation of the query 'having' value
     */
    public static String getQueryHaving(String having) {
        if (having == null || having.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }

        StringBuilder havingStringBuilder = new StringBuilder();
        havingStringBuilder.append(SiddhiStringBuilderConstants.HAVING)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(having);

        return havingStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a given query output rate limit
     *
     * @param outputRateLimit The output rate limit value given
     * @return The Siddhi representation of the given output rate limit value
     */
    public static String getQueryOutputRateLimit(String outputRateLimit) {
        if (outputRateLimit == null || outputRateLimit.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }
        StringBuilder outputRateLimitStringBuilder = new StringBuilder();
        outputRateLimitStringBuilder.append(SiddhiStringBuilderConstants.OUTPUT)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(outputRateLimit);

        return outputRateLimitStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a QueryOutputConfig object
     *
     * @param queryOutput The QueryOutputConfig object to be converted
     * @return The converted string representation of the given QueryOutputConfig object
     * @throws CodeGenerationException Error while generating code
     */
    public static String getQueryOutput(QueryOutputConfig queryOutput) throws CodeGenerationException {
        if (queryOutput == null) {
            throw new CodeGenerationException("The QueryOutputConfig given is null");
        } else if (queryOutput.getType() == null || queryOutput.getType().isEmpty()) {
            throw new CodeGenerationException("The type value for the given QueryOutputConfig object is null/empty");
        }

        StringBuilder queryOutputStringBuilder = new StringBuilder();

        switch (queryOutput.getType().toUpperCase()) {
            case CodeGeneratorConstants.INSERT:
                InsertOutputConfig insertOutputConfig = (InsertOutputConfig) queryOutput.getOutput();
                queryOutputStringBuilder.append(getInsertOutput(insertOutputConfig, queryOutput.getTarget()));
                break;
            case CodeGeneratorConstants.DELETE:
                DeleteOutputConfig deleteOutputConfig = (DeleteOutputConfig) queryOutput.getOutput();
                queryOutputStringBuilder.append(getDeleteOutput(deleteOutputConfig, queryOutput.getTarget()));
                break;
            case CodeGeneratorConstants.UPDATE:
            case CodeGeneratorConstants.UPDATE_OR_INSERT_INTO:
                UpdateInsertIntoOutputConfig updateInsertIntoOutput =
                        (UpdateInsertIntoOutputConfig) queryOutput.getOutput();
                queryOutputStringBuilder.append(getUpdateOutput(queryOutput.getType(),
                        updateInsertIntoOutput, queryOutput.getTarget()));
                break;
            default:
                throw new CodeGenerationException("Unidentified query output type: " + queryOutput.getType());
        }

        return queryOutputStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a InsertOutputConfig object
     *
     * @param insertOutput The InsertOutputConfig object to be converted
     * @param target       The name of the target output definition
     * @return The Siddhi string representation of the InsertOutputConfig to respective target
     * @throws CodeGenerationException Error while generating code
     */
    private static String getInsertOutput(InsertOutputConfig insertOutput, String target)
            throws CodeGenerationException {
        if (insertOutput == null) {
            throw new CodeGenerationException("The given InsertOutputConfig object is null");
        } else if (target == null || target.isEmpty()) {
            throw new CodeGenerationException("The target value for the given query output is null/empty");
        }

        StringBuilder insertOutputStringBuilder = new StringBuilder();

        insertOutputStringBuilder.append(SiddhiStringBuilderConstants.INSERT)
                .append(SiddhiStringBuilderConstants.SPACE);

        if (insertOutput.getEventType() != null && !insertOutput.getEventType().isEmpty()) {
            switch (insertOutput.getEventType().toUpperCase()) {
                case CodeGeneratorConstants.CURRENT_EVENTS:
                    insertOutputStringBuilder.append(SiddhiStringBuilderConstants.CURRENT_EVENTS)
                            .append(SiddhiStringBuilderConstants.SPACE);
                    break;
                case CodeGeneratorConstants.EXPIRED_EVENTS:
                    insertOutputStringBuilder.append(SiddhiStringBuilderConstants.EXPIRED_EVENTS)
                            .append(SiddhiStringBuilderConstants.SPACE);
                    break;
                case CodeGeneratorConstants.ALL_EVENTS:
                    insertOutputStringBuilder.append(SiddhiStringBuilderConstants.ALL_EVENTS)
                            .append(SiddhiStringBuilderConstants.SPACE);
                    break;
                default:
                    throw new CodeGenerationException("Unidentified event type: " + insertOutput.getEventType());
            }
        }

        insertOutputStringBuilder.append(SiddhiStringBuilderConstants.INTO)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(target)
                .append(SiddhiStringBuilderConstants.SEMI_COLON);

        return insertOutputStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a DeleteOutputConfig object
     *
     * @param deleteOutput The DeleteOutputConfig object to be converted
     * @param target       The name of the target output definition
     * @return The Siddhi string representation of the DeleteOutputConfig object to the respective target
     * @throws CodeGenerationException Error while generating code
     */
    private static String getDeleteOutput(DeleteOutputConfig deleteOutput, String target)
            throws CodeGenerationException {
        if (deleteOutput == null) {
            throw new CodeGenerationException("The given DeleteOutputConfig object is null");
        } else if (deleteOutput.getOn() == null || deleteOutput.getOn().isEmpty()) {
            throw new CodeGenerationException("The 'on' statement of the given DeleteOutputConfig" +
                    " object is null/empty");
        } else if (target == null || target.isEmpty()) {
            throw new CodeGenerationException("The target value for the given delete query is null/empty");
        }

        StringBuilder deleteOutputStringBuilder = new StringBuilder();

        deleteOutputStringBuilder.append(SiddhiStringBuilderConstants.DELETE)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(target)
                .append(getForEventType(deleteOutput.getEventType()))
                .append(SiddhiStringBuilderConstants.NEW_LINE)
                .append(SiddhiStringBuilderConstants.TAB_SPACE)
                .append(SiddhiStringBuilderConstants.ON)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(deleteOutput.getOn())
                .append(SiddhiStringBuilderConstants.SEMI_COLON);

        return deleteOutputStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a UpdateInsertIntoOutputConfig object
     *
     * @param type                   The type of output object the one is (i.e. Either update|update or insert into)
     * @param updateInsertIntoOutput The UpdateInsertIntoConfig object to be converted
     * @param target                 The name of the target output definition
     * @return The Siddhi string representation of the UpdateInsertIntoOutputConfig object to the respective target
     * @throws CodeGenerationException Error while generating code
     */
    private static String getUpdateOutput(String type, UpdateInsertIntoOutputConfig updateInsertIntoOutput,
                                          String target) throws CodeGenerationException {
        if (updateInsertIntoOutput == null) {
            throw new CodeGenerationException("The given UpdateInsertIntoOutputConfig object is null");
        } else if (updateInsertIntoOutput.getSet() == null || updateInsertIntoOutput.getSet().isEmpty()) {
            throw new CodeGenerationException("The SetAttributeConfig list for the given" +
                    " update/insert query is null/empty");
        } else if (updateInsertIntoOutput.getOn() == null || updateInsertIntoOutput.getOn().isEmpty()) {
            throw new CodeGenerationException("The 'on' value for the UpdateInsertIntoOutputConfig" +
                    " object is null/empty");
        } else if (target == null || target.isEmpty()) {
            throw new CodeGenerationException("The given target for the update/insert into query is null/empty");
        }

        StringBuilder updateInsertIntoOutputStringBuilder = new StringBuilder();
        if (type.equalsIgnoreCase(CodeGeneratorConstants.UPDATE)) {
            updateInsertIntoOutputStringBuilder.append(SiddhiStringBuilderConstants.UPDATE);
        } else if (type.equalsIgnoreCase(CodeGeneratorConstants.UPDATE_OR_INSERT_INTO)) {
            updateInsertIntoOutputStringBuilder.append(SiddhiStringBuilderConstants.UPDATE_OR_INSERT_INTO);
        }

        updateInsertIntoOutputStringBuilder.append(SiddhiStringBuilderConstants.SPACE)
                .append(target)
                .append(getForEventType(updateInsertIntoOutput.getEventType()))
                .append(SiddhiStringBuilderConstants.NEW_LINE)
                .append(SiddhiStringBuilderConstants.TAB_SPACE)
                .append(SiddhiStringBuilderConstants.SET)
                .append(SiddhiStringBuilderConstants.SPACE);

        int setAttributesLeft = updateInsertIntoOutput.getSet().size();
        for (SetAttributeConfig setAttribute : updateInsertIntoOutput.getSet()) {
            updateInsertIntoOutputStringBuilder.append(getSetAttribute(setAttribute));

            if (setAttributesLeft != 1) {
                updateInsertIntoOutputStringBuilder.append(SiddhiStringBuilderConstants.COMMA)
                        .append(SiddhiStringBuilderConstants.SPACE);
            }
            setAttributesLeft--;
        }

        updateInsertIntoOutputStringBuilder.append(SiddhiStringBuilderConstants.NEW_LINE)
                .append(SiddhiStringBuilderConstants.TAB_SPACE)
                .append(SiddhiStringBuilderConstants.ON)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(updateInsertIntoOutput.getOn())
                .append(SiddhiStringBuilderConstants.SEMI_COLON);

        return updateInsertIntoOutputStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation for a 'for <eventType>' output,
     * where the <eventType> can be 'CURRENT_EVENTS', 'EXPIRED_EVENTS' or 'ALL_EVENTS'
     *
     * @param eventType The event type that is used
     * @return The string representation for the event type in Siddhi
     * @throws CodeGenerationException Error while generating code
     */
    private static String getForEventType(String eventType) throws CodeGenerationException {
        if (eventType == null || eventType.isEmpty()) {
            return SiddhiStringBuilderConstants.EMPTY_STRING;
        }

        StringBuilder forEventTypeStringBuilder = new StringBuilder();
        forEventTypeStringBuilder.append(SiddhiStringBuilderConstants.SPACE)
                .append(SiddhiStringBuilderConstants.FOR)
                .append(SiddhiStringBuilderConstants.SPACE);

        switch (eventType.toUpperCase()) {
            case CodeGeneratorConstants.CURRENT_EVENTS:
                forEventTypeStringBuilder.append(SiddhiStringBuilderConstants.CURRENT_EVENTS);
                break;
            case CodeGeneratorConstants.EXPIRED_EVENTS:
                forEventTypeStringBuilder.append(SiddhiStringBuilderConstants.EXPIRED_EVENTS);
                break;
            case CodeGeneratorConstants.ALL_EVENTS:
                forEventTypeStringBuilder.append(SiddhiStringBuilderConstants.ALL_EVENTS);
                break;
            default:
                throw new CodeGenerationException("Unidentified event type: " + eventType);
        }

        return forEventTypeStringBuilder.toString();
    }

    /**
     * Generates a Siddhi string representation of a SetAttributeConfig object
     *
     * @param setAttribute The given SetAttributeConfig object to be converted
     * @return The converted Siddhi string representation of the SetAttributeConfig object
     * @throws CodeGenerationException Error while generating code
     */
    private static String getSetAttribute(SetAttributeConfig setAttribute) throws CodeGenerationException {
        if (setAttribute == null) {
            throw new CodeGenerationException("The SetAttributeConfig object given is null in the" +
                    " update output query type");
        } else if (setAttribute.getAttribute() == null || setAttribute.getAttribute().isEmpty()) {
            throw new CodeGenerationException("The 'attribute' value of the given SetAttributeConfig" +
                    " object is null/empty");
        } else if (setAttribute.getValue() == null || setAttribute.getValue().isEmpty()) {
            throw new CodeGenerationException("The 'value' attribute of the given SetAttributeConfig" +
                    " object is null/empty");
        }

        StringBuilder setAttributeStringBuilder = new StringBuilder();

        setAttributeStringBuilder.append(setAttribute.getAttribute())
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(SiddhiStringBuilderConstants.EQUAL)
                .append(SiddhiStringBuilderConstants.SPACE)
                .append(setAttribute.getValue());

        return setAttributeStringBuilder.toString();
    }

    private CodeGeneratorHelper() {
    }

}
