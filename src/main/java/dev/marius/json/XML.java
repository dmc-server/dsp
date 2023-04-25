package dev.marius.json;
/*

Project: Lobby-System
Author: Marius
Created: 17.10.2020, 12:40

*/

import java.util.Iterator;

public class XML {

    public static final Character AMP = '&';
    public static final Character APOS = '\'';
    public static final Character BANG = '!';
    public static final Character EQ = '=';
    public static final Character GT = '>';
    public static final Character LT = '<';
    public static final Character QUEST = '?';
    public static final Character QUOT = '"';
    public static final Character SLASH = '/';

    public static String escape(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        for (int i = 0, length = string.length(); i < length; i++) {
            char c = string.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void noSpace(String string) throws JSONException {
        int i, length = string.length();
        if (length == 0) {
            throw new JSONException("Empty string.");
        }
        for (i = 0; i < length; i += 1) {
            if (Character.isWhitespace(string.charAt(i))) {
                throw new JSONException("'" + string
                        + "' contains a space character.");
            }
        }
    }

    private static boolean parse(XMLTokener x, JSONObject context, String name)
            throws JSONException {
        char c;
        int i;
        JSONObject jsonobject = null;
        String string;
        String tagName;
        Object token;

        token = x.nextToken();

        if (token == BANG) {
            c = x.next();
            if (c == '-') {
                if (x.next() == '-') {
                    x.skipPast("-->");
                    return false;
                }
                x.back();
            } else if (c == '[') {
                token = x.nextToken();
                if ("CDATA".equals(token)) {
                    if (x.next() == '[') {
                        string = x.nextCDATA();
                        if (string.length() > 0) {
                            context.accumulate("content", string);
                        }
                        return false;
                    }
                }
                throw x.syntaxError("Expected 'CDATA['");
            }
            i = 1;
            do {
                token = x.nextMeta();
                if (token == null) {
                    throw x.syntaxError("Missing '>' after '<!'.");
                } else if (token == LT) {
                    i += 1;
                } else if (token == GT) {
                    i -= 1;
                }
            } while (i > 0);
            return false;
        } else if (token == QUEST) {
            x.skipPast("?>");
            return false;
        } else if (token == SLASH) {
            token = x.nextToken();
            if (name == null) {
                throw x.syntaxError("Mismatched close tag " + token);
            }
            if (!token.equals(name)) {
                throw x.syntaxError("Mismatched " + name + " and " + token);
            }
            if (x.nextToken() != GT) {
                throw x.syntaxError("Misshaped close tag");
            }
            return true;
        } else if (token instanceof Character) {
            throw x.syntaxError("Misshaped tag");
        } else {
            tagName = (String) token;
            token = null;
            jsonobject = new JSONObject();
            for (;;) {
                if (token == null) {
                    token = x.nextToken();
                }

                if (token instanceof String) {
                    string = (String) token;
                    token = x.nextToken();
                    if (token == EQ) {
                        token = x.nextToken();
                        if (!(token instanceof String)) {
                            throw x.syntaxError("Missing value");
                        }
                        jsonobject.accumulate(string,
                                JSONObject.stringToValue((String) token));
                        token = null;
                    } else {
                        jsonobject.accumulate(string, "");
                    }


                } else if (token == SLASH) {
                    if (x.nextToken() != GT) {
                        throw x.syntaxError("Misshaped tag");
                    }
                    if (jsonobject.length() > 0) {
                        context.accumulate(tagName, jsonobject);
                    } else {
                        context.accumulate(tagName, "");
                    }
                    return false;

                } else if (token == GT) {
                    for (;;) {
                        token = x.nextContent();
                        if (token == null) {
                            if (tagName != null) {
                                throw x.syntaxError("Unclosed tag " + tagName);
                            }
                            return false;
                        } else if (token instanceof String) {
                            string = (String) token;
                            if (string.length() > 0) {
                                jsonobject.accumulate("content",
                                        JSONObject.stringToValue(string));
                            }

                        } else if (token == LT) {
                            if (parse(x, jsonobject, tagName)) {
                                if (jsonobject.length() == 0) {
                                    context.accumulate(tagName, "");
                                } else if (jsonobject.length() == 1
                                        && jsonobject.opt("content") != null) {
                                    context.accumulate(tagName,
                                            jsonobject.opt("content"));
                                } else {
                                    context.accumulate(tagName, jsonobject);
                                }
                                return false;
                            }
                        }
                    }
                } else {
                    throw x.syntaxError("Misshaped tag");
                }
            }
        }
    }

    public static Object stringToValue(String string) {
        return JSONObject.stringToValue(string);
    }

    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jo = new JSONObject();
        XMLTokener x = new XMLTokener(string);
        while (x.more() && x.skipPast("<")) {
            parse(x, jo, null);
        }
        return jo;
    }

    public static String toString(Object object) throws JSONException {
        return toString(object, null);
    }

    public static String toString(Object object, String tagName)
            throws JSONException {
        StringBuilder sb = new StringBuilder();
        JSONArray ja;
        JSONObject jo;
        String key;
        Iterator<String> keys;
        String string;
        Object value;

        if (object instanceof JSONObject) {

            if (tagName != null) {
                sb.append('<');
                sb.append(tagName);
                sb.append('>');
            }

            jo = (JSONObject) object;
            keys = jo.keys();
            while (keys.hasNext()) {
                key = keys.next();
                value = jo.opt(key);
                if (value == null) {
                    value = "";
                } else if (value.getClass().isArray()) {
                    value = new JSONArray(value);
                }
                string = value instanceof String ? (String) value : null;

                if ("content".equals(key)) {
                    if (value instanceof JSONArray) {
                        ja = (JSONArray) value;
                        int i = 0;
                        for (Object val : ja) {
                            if (i > 0) {
                                sb.append('\n');
                            }
                            sb.append(escape(val.toString()));
                            i++;
                        }
                    } else {
                        sb.append(escape(value.toString()));
                    }

                } else if (value instanceof JSONArray) {
                    ja = (JSONArray) value;
                    for (Object val : ja) {
                        if (val instanceof JSONArray) {
                            sb.append('<');
                            sb.append(key);
                            sb.append('>');
                            sb.append(toString(val));
                            sb.append("</");
                            sb.append(key);
                            sb.append('>');
                        } else {
                            sb.append(toString(val, key));
                        }
                    }
                } else if ("".equals(value)) {
                    sb.append('<');
                    sb.append(key);
                    sb.append("/>");

                } else {
                    sb.append(toString(value, key));
                }
            }
            if (tagName != null) {

                sb.append("</");
                sb.append(tagName);
                sb.append('>');
            }
            return sb.toString();

        }

        if (object != null) {
            if (object.getClass().isArray()) {
                object = new JSONArray(object);
            }

            if (object instanceof JSONArray) {
                ja = (JSONArray) object;
                for (Object val : ja) {
                    sb.append(toString(val, tagName == null ? "array" : tagName));
                }
                return sb.toString();
            }
        }

        string = (object == null) ? "null" : escape(object.toString());
        return (tagName == null) ? "\"" + string + "\""
                : (string.length() == 0) ? "<" + tagName + "/>" : "<" + tagName
                + ">" + string + "</" + tagName + ">";

    }

}
