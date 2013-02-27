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

package org.nuxeo.ecm.platform.faceted.search.api;

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

    /* Properties */

    public static final String FACETED_SEARCH_CONTENT_VIEW_NAME_PROPERTY = "fs:content_view_name";

    /* Page provider names */

    /**
     * @since 5.4.2
     */
    public static final String OTHER_USERS_SAVED_SEARCHES_PAGE_PROVIDER_NAME = "OTHER_USERS_SAVED_SEARCHES";

    /**
     * @since 5.4.2
     */
    public static final String CURRENT_USER_SAVED_SEARCHES_PAGE_PROVIDER_NAME = "USER_SAVED_SEARCHES";

    /**
     * @since 5.4.2
     */
    public static final String FACETED_SEARCH_FLAG = "FACETED_SEARCH";

    /* Faceted types name */

    /**
     * @since 5.7
     */
    public static final String FACETED_SAVED_SEARCH_FOLDER = "FacetedSavedSearchesFolder";

    /* Content view names */

    /**
     * @deprecated There is no more content view registered with that name.
     * Use OTHER_USERS_SAVED_SEARCHES_PAGE_PROVIDER_NAME instead
     */
    @Deprecated
    public static final String ALL_SAVED_SEARCHES_CONTENT_VIEW_NAME = "ALL_SAVED_SEARCHES";

    /**
     * @deprecated There is no more content view registered with that name.
     * Use CURRENT_USER_SAVED_SEARCHES_PAGE_PROVIDER_NAME instead
     */
    @Deprecated
    public static final String CURRENT_USER_SAVED_SEARCHES_CONTENT_VIEW_NAME = "USER_SAVED_SEARCHES";

}
