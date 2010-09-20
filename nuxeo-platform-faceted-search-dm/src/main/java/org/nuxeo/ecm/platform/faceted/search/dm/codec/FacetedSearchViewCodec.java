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
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.ecm.platform.faceted.search.dm.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentLocation;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentLocationImpl;
import org.nuxeo.ecm.platform.faceted.search.dm.Constants;
import org.nuxeo.ecm.platform.url.DocumentViewImpl;
import org.nuxeo.ecm.platform.url.api.DocumentView;
import org.nuxeo.ecm.platform.url.service.AbstractDocumentViewCodec;

/**
 * Codec handling a permanent link for a faceted search.
 *
 * @author <a href="mailto:qlamerand@nuxeo.com">Quentin Lamerand</a>
 * @since 5.4
 */
public class FacetedSearchViewCodec extends AbstractDocumentViewCodec {

    private static final Log log = LogFactory.getLog(FacetedSearchViewCodec.class);

    public static final String PREFIX = "nxsrch";

    public static final String QUERY_ALL = "SELECT * FROM Document ORDER BY ecm:path";

    // nxsrch/server/?requestParams
    public static final String URL_PATTERN = "/([\\w\\.]+)(/)?(\\?(.*)?)?";

    protected String prefix;

    @Override
    public String getPrefix() {
        if (prefix != null) {
            return prefix;
        }
        return PREFIX;
    }

    @Override
    public DocumentView getDocumentViewFromUrl(String url) {
        final Pattern pattern = Pattern.compile(getPrefix() + URL_PATTERN);
        Matcher m = pattern.matcher(url);
        if (m.matches()) {
            final String server = m.group(1);
            DocumentRef docRef = getFirstAvailableDocumentRef(server);
            if (docRef == null) {
                return null;
            }

            Map<String, String> params = null;
            if (m.groupCount() > 2) {
                String query = m.group(3);
                params = URIUtils.getRequestParameters(query);
            }

            final DocumentLocation docLoc = new DocumentLocationImpl(server,
                    docRef);
            return new DocumentViewImpl(docLoc,
                    Constants.FACETED_SEARCH_RESULTS_VIEW, params);
        }

        return null;
    }

    protected DocumentRef getFirstAvailableDocumentRef(String server) {
        CoreInstance coreInstance = CoreInstance.getInstance();
        CoreSession session = null;
        DocumentRef docRef = null;
        try {
            session = coreInstance.open(server, null);
            DocumentModelList docList = session.query(QUERY_ALL, 1);
            if (!docList.isEmpty()) {
                docRef = docList.get(0).getRef();
            }
        } catch (ClientException e) {
            log.error("Failed to get session", e);
        } finally {
            coreInstance.close(session);
        }
        return docRef;
    }

    @Override
    public String getUrlFromDocumentView(DocumentView docView) {
        DocumentLocation docLoc = docView.getDocumentLocation();
        if (docLoc != null) {
            List<String> items = new ArrayList<String>();
            items.add(getPrefix());
            items.add(docLoc.getServerName());
            String uri = StringUtils.join(items, "/") + "/";
            uri = URIUtils.addParametersToURIQuery(uri, docView.getParameters());
            return uri;
        }
        return null;
    }

}
