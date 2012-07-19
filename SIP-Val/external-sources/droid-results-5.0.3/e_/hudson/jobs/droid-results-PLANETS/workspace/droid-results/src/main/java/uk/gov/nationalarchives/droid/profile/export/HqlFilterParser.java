/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile.export;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;

import uk.gov.nationalarchives.droid.core.interfaces.filter.Filter;
import uk.gov.nationalarchives.droid.core.interfaces.filter.FilterCriterion;
import uk.gov.nationalarchives.droid.core.interfaces.filter.RestrictionFactory;
import uk.gov.nationalarchives.droid.core.interfaces.filter.expressions.Junction;
import uk.gov.nationalarchives.droid.core.interfaces.filter.expressions.QueryBuilder;
import uk.gov.nationalarchives.droid.core.interfaces.filter.expressions.Restrictions;

/**
 * @author rflitcroft
 *
 */
public class HqlFilterParser {

    private final Log log = LogFactory.getLog(getClass());
    
    /**
     * Parses a filter into HQL.
     * @param filter the filter to parse
     * @param hibernateSession the hibernate session
     * @param baseQuery the base query
     * @return a hibernate query
     */
    public Query parse(Filter filter, Session hibernateSession, String baseQuery) {
        
        QueryBuilder queryBuilder = QueryBuilder.forAlias("profileResourceNode")
            .createAlias("fis");
        
        if (filter.isNarrowed()) {
            for (FilterCriterion criterion : filter.getCriteria()) {
                queryBuilder.add(RestrictionFactory.forFilterCriterion(criterion));
            }
        } else {
            Junction disjunction = Restrictions.disjunction();
            for (FilterCriterion criterion : filter.getCriteria()) {
                disjunction.add(RestrictionFactory.forFilterCriterion(criterion));
            }
            queryBuilder.add(disjunction);
        }
        
        final String queryString = baseQuery + " WHERE " + queryBuilder.toEjbQl();
        log.debug("Query = " + queryString);

        Query query = hibernateSession.createQuery(queryString);
        int i = 0;
        for (Object value : queryBuilder.getValues()) {
            query.setParameter(i++, value);
        }
        
        return query;
    }
    
}
