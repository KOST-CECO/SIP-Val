/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.filter.expressions;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


/**
 * @author rflitcroft
 *
 */
public final class Restrictions {

    /**
     * 
     */
    private static final String PERIOD = ".";
    private static final char SPACE = ' ';

    private Restrictions() { }
    
    /**
     * An equals expression.
     * @param propertyName the property name
     * @param value the value
     * @return an expression
     */
    public static Criterion eq(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "=");
    }
    
    /**
     * A not-equals expression.
     * @param propertyName the property name
     * @param value the value
     * @return an expression
     */
    public static Criterion neq(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "!=");
    }

    /**
     * A greater than expression.
     * @param propertyName the property name
     * @param value the value
     * @return an equals expression
     */
    public static Criterion gt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ">");
    }

    /**
     * A less than expression.
     * @param propertyName the property name
     * @param value the value
     * @return an equals expression
     */
    public static Criterion lt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<");
    }

    /**
     * A greater than or equals expression.
     * @param propertyName the property name
     * @param value the value
     * @return an equals expression
     */
    public static Criterion gte(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ">=");
    }

    /**
     * A less than or equals expression.
     * @param propertyName the property name
     * @param value the value
     * @return an equals expression
     */
    public static Criterion lte(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<=");
    }

    /**
     * An like expression.
     * @param propertyName the property name
     * @param value the value
     * @return an equals expression
     */
    public static Criterion like(String propertyName, String value) {
        return new SimpleExpression(propertyName, value, "LIKE");
    }

    /**
     * An IN expression.
     * @param propertyName the property name
     * @param values the values
     * @return an equals expression
     */
    public static Criterion in(String propertyName, Object[] values) {
        return new InExpression(propertyName, values, false);
    }

    /**
     * A NOT IN expression.
     * @param propertyName the property name
     * @param values the values
     * @return an equals expression
     */
    public static Criterion notIn(String propertyName, Object[] values) {
        return new InExpression(propertyName, values, true);
    }
    
    /**
     * An OR expression.
     * @param lhs the left hand side
     * @param rhs the right hand side
     * @return an OR expression
     */
    public static Criterion or(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, "OR");
    }

    /**
     * An AND expression.
     * @param lhs the left hand side
     * @param rhs the right hand side
     * @return an AND expression
     */
    public static Criterion and(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, "AND");
    }
    
    /**
     * A simple expression.
     * @author rflitcroft
     *
     */
    private static final class SimpleExpression implements Criterion {

        private String op;
        private String propertyName;
        private Object[] values;
        
        /**
         * 
         * @param propertyName the property name
         * @param value the property value
         * @param op the operation
         */
        SimpleExpression(String propertyName, Object value, String op) {
            this.propertyName = propertyName;
            this.values = new Object[] {value};
            this.op = op;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toEjbQl(QueryBuilder parent) {
            return getAliasedQualifier(propertyName, parent) + SPACE + op + " ?"; 
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getValues() {
            return values;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return propertyName + op + values;
        }
        
    }
    
    private static String getAliasedQualifier(String propertyName, QueryBuilder parent) {
        
        if (propertyName.indexOf('.') > 0) {
            String root = StringUtils.substringBefore(propertyName, PERIOD);
            String name = StringUtils.substringAfter(propertyName, PERIOD);
            if (parent.getAliases().contains(root)) {
                return root + '.' + name;
            }
        }
        return parent.getAlias() + '.' + propertyName;
        
    }
    
    private static final class InExpression implements Criterion {
        
        private String propertyName;
        private Object[] values;
        private boolean inverse;
        
        public InExpression(String propertyName, Object[] values, boolean inverse) {
            this.propertyName = propertyName;
            this.values = values;
            this.inverse = inverse;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getValues() {
            return values;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toEjbQl(QueryBuilder parent) {
            return getAliasedQualifier(propertyName, parent) + (inverse ? " NOT " : SPACE) + "IN ("
                    + repeat("?, ", values.length - 1) + "?)";
        }
        
        private static String repeat(String string, int times) {
            StringBuilder buf = new StringBuilder(string.length() * times);
            for (int i = 0; i < times; i++) {
                buf.append(string);
            }
            return buf.toString();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return propertyName + " IN (" + values + ')';
        }

    }
    
    private static final class LogicalExpression implements Criterion {
        
        private Criterion lhs;
        private Criterion rhs;
        private String op;
        
        LogicalExpression(Criterion lhs, Criterion rhs, String op) {
            this.lhs = lhs;
            this.rhs = rhs;
            this.op = op;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getValues() {
            return ArrayUtils.addAll(lhs.getValues(), rhs.getValues());
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toEjbQl(QueryBuilder parent) {
            return '(' + lhs.toEjbQl(parent) + ' ' + op + ' ' + rhs.toEjbQl(parent) + ')';
        }
    }
    
    /**
     * @return a conjunction (AND)
     */
    public static Junction conjunction() {
        return new Conjunction();
    }

    /**
     * @return a disjunction (OR)
     */
    public static Junction disjunction() {
        return new Disjunction();
    }
}
