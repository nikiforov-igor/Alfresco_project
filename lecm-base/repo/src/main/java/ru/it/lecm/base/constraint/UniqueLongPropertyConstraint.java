package ru.it.lecm.base.constraint;

import org.alfresco.repo.dictionary.constraint.AbstractConstraint;

/**
 * Created with IntelliJ IDEA.
 * User: pkotelnikova
 * Date: 20.12.12
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class UniqueLongPropertyConstraint extends AbstractConstraint {

    @Override
    public String getType() {
        return "LECMUNIQUE";
    }

    @Override
    protected void evaluateSingleValue(Object value) {
    }

}

