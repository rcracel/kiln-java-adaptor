package com.nevermindsoft.kiln.utils;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: rcracel
 * Date: 1/20/13
 * Time: 6:21 PM
 */
public class JSONUtils {

    /**
     * Converts a list into a json representation
     *
     * @param array a list of objects to be converted to json
     * @return the json string representing this list
     */
    public static String array2json(List<?> array) {
        String result = null;

        if ( array != null ) {
            StringBuilder json = new StringBuilder();

            json.append("[");

            Iterator<?> iterator = array.iterator();
            while (iterator.hasNext()) {
                Object o = iterator.next();

                json.append(jsonRepresentation(o));

                if (iterator.hasNext()) {
                    json.append(",");
                }
            }

            json.append("]");

            result = json.toString();
        }

        return result;
    }

    /**
     * Converts a map to a json representation
     *
     * @param map the map to be converted to a json object representation
     * @return the json string
     */
    public static String map2json(Map<String, Object> map) {
        String result = null;

        if ( map != null ) {
            StringBuilder json = new StringBuilder();

            json.append("{");

            Iterator<Map.Entry<String,Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();

                json.append("\"").append(escapeJSON(entry.getKey())).append("\":").append(jsonRepresentation(entry.getValue()));

                if (iterator.hasNext()) {
                    json.append(",");
                }
            }

            json.append("}");

            result = json.toString();
        }

        return result;
    }

    public static String jsonRepresentation( Object o ) {
        String result;

        if ( o == null ) {
            result = "null";
        } else if ( o instanceof JSONArray || o instanceof JSONObject ) {
            result = o.toString();
        } else if ( o instanceof String ) {
            result = String.format("\"%s\"", escapeJSON((String)o));
        } else if ( o instanceof Number ) {
            result = o.toString();
        } else { //- Catch all
            result = escapeJSON(o.toString());
        }

        return result;
    }

    /**
     * Escapes a JSON string
     *
     * @param json the input string
     * @return the escaped json string
     */
    public static String escapeJSON( String json ) {
        return json == null ? null : StringEscapeUtils.escapeJavaScript(json);
    }

}
