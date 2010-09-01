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

package org.nuxeo.ecm.platform.faceted.search.jsf;

/**
 * Constants used by the faceted search module
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
public final class Constants {

    private Constants() {
        // Constants class
    }

    public static final String FACETED_SEARCH_SCHEMA = "faceted_search";

    public static final String FACETED_SEARCH_DOCUMENT_TYPE = "FacetedSearch";

    public static final String FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY = "fs:content_view_name";

    /* Content view names */

    public static final String ALL_SAVED_SEARCHES_CONTENT_VIEW_NAME = "ALL_SAVED_SEARCHES";

    public static final String CURRENT_USER_SAVED_SEARCHES_CONTENT_VIEW_NAME = "USER_SAVED_SEARCHES";

}
