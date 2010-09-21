/*
 * (C) Copyright 2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.platform.faceted.search.api.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

/**
 * Utility class to set a {@code DocumentModel}'s metadata from a JSON string
 *
 * @see org.nuxeo.ecm.platform.faceted.search.api.util.JSONMetadataExporter
 *
 * @author <a href="mailto:qlamerand@nuxeo.com">Quentin Lamerand</a>
 * @since 5.4
 */
public class JSONMetadataHelper {

    @SuppressWarnings("unchecked")
    public static DocumentModel setPropertiesFromJson(DocumentModel doc,
            String json) throws JSONException, PropertyException,
            ClientException {
        JSONObject jsonObject = new JSONObject(new JSONTokener(json));
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            doc.setPropertyValue(key, getValue(jsonObject.get(key)));
        }
        return doc;
    }

    @SuppressWarnings("unchecked")
    protected static Serializable getValue(Object o) throws JSONException {
        if (o instanceof String) {
            Calendar calendar = null;
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
                Date date = df.parse((String) o);
                calendar = Calendar.getInstance();
                calendar.setTime(date);
            } catch (ParseException e) {
            }

            if (calendar != null) {
                return calendar;
            } else {
                return (Serializable) o;
            }
        } else if (o instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) o;
            ArrayList<Serializable> list = new ArrayList<Serializable>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(getValue(jsonArray.get(i)));
            }
            return list;
        } else if (o instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) o;
            HashMap<String, Serializable> map = new HashMap<String, Serializable>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, getValue(jsonObject.get(key)));
            }
            return map;
        } else {
            return (Serializable) o;
        }
    }

}
