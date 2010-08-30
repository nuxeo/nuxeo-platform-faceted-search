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
