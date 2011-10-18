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

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.faceted.search.api.service.FacetedSearchService;
import org.nuxeo.runtime.api.Framework;

import static org.jboss.seam.ScopeType.SESSION;

/**
 * Business delegate exposing the
 * {@link org.nuxeo.ecm.platform.faceted.search.api.service.FacetedSearchService}
 * as a seam component.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
@Name("facetedSearchService")
@Scope(SESSION)
public class FacetedSearchBusinessDelegate implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(FacetedSearchBusinessDelegate.class);

    protected FacetedSearchService facetedSearchService;

    /**
     * Acquires a new
     * {@link org.nuxeo.ecm.platform.faceted.search.api.service.FacetedSearchService}
     * reference. The related service may be deployed on a local or remote
     * AppServer.
     *
     * @throws ClientException
     */
    @Unwrap
    public FacetedSearchService getService() throws ClientException {
        if (facetedSearchService == null) {
            try {
                facetedSearchService = Framework.getService(FacetedSearchService.class);
            } catch (Exception e) {
                final String errMsg = "Error connecting to FacetedSearchService. "
                        + e.getMessage();
                throw new ClientException(errMsg, e);
            }
            if (facetedSearchService == null) {
                throw new ClientException(
                        "FacetedSearchService service not bound");
            }
        }
        return facetedSearchService;
    }

    @Destroy
    public void destroy() {
        if (facetedSearchService != null) {
            facetedSearchService = null;
        }
        log.debug("Destroyed the seam component");
    }

}
