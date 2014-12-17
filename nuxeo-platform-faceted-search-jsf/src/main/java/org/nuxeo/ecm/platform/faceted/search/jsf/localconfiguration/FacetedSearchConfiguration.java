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

package org.nuxeo.ecm.platform.faceted.search.jsf.localconfiguration;

import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.localconfiguration.LocalConfiguration;

/**
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @since 5.4.2
 */
public interface FacetedSearchConfiguration extends LocalConfiguration<FacetedSearchConfiguration> {

    /**
     * Return a list of content views name that are associated with the local configuration
     *
     * @return an unmodifiable list of String or null.
     */
    List<String> getAllowedContentViewNames();

    /**
     * Return a list of content views name that are denied with the local configuration
     *
     * @return an unmodifiable list of String or null.
     */
    List<String> getDeniedContentViewNames();

    /**
     * Provide a filter to remove un authorised content views name.
     *
     * @param names set of possible content views name
     * @return a set without unauthorised content views, it should be empty.
     */
    Set<String> filterAllowedContentViewNames(Set<String> names);
}
