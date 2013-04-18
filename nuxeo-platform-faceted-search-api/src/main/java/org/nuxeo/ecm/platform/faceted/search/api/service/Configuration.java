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

package org.nuxeo.ecm.platform.faceted.search.api.service;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Configuration object for the {@link FacetedSearchService}.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.4
 */
@XObject("configuration")
public class Configuration {

    @XNode("rootSavedSearchesTitle")
    @Deprecated
    protected String rootSavedSearchesTitle;

    /**
     * @deprecated since 5.7. Saved searches are not stored anymore in a
     *             dedicated folder but in the user workspace.
     */
    @Deprecated
    public String getRootSavedSearchesTitle() {
        return rootSavedSearchesTitle;
    }

    @Deprecated
    public void setRootSavedSearchesTitle(String title) {
        rootSavedSearchesTitle = title;
    }

}
