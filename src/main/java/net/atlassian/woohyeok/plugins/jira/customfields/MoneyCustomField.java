package net.atlassian.woohyeok.plugins.jira.customfields;

import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;

public class MoneyCustomField extends AbstractSingleFieldType<BigDecimal> {
    private static final Logger log = LoggerFactory.getLogger(MoneyCustomField.class);

    protected MoneyCustomField(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager) {
        super(customFieldValuePersister, genericConfigManager);
    }


    @Override
    public String getStringFromSingularObject(final BigDecimal singularObject) {
        if (singularObject == null) {
            return null;
        } else {
            return singularObject.toString();
        }
    }

    //The method takes input from the user, validates it, and puts it into a a transport object.
    //We want to validate that the user has entered a valid number, and that there are no more than two decimal places.
    @Override
    public BigDecimal getSingularObjectFromString(final String string) throws FieldValidationException {
        if (string == null) {
            return null;
        }

        try {
            final BigDecimal decimal = new BigDecimal(string);

            // Check that we don't have too many decimal places
            if (decimal.scale() > 2) {
                throw new FieldValidationException("Maximum of 2 decimal places are allowed.");
            }
            return decimal.setScale(2);

        } catch (NumberFormatException e) {
            throw new FieldValidationException("Not a valid number.");
        }
    }

    @Nonnull
    @Override
    protected PersistenceFieldType getDatabaseType() {
        //add the getDatabaseType() method to tell JIRA what kind of database column to store the data in
        return PersistenceFieldType.TYPE_LIMITED_TEXT;
    }

    //This takes a value from the DB and converts it to our transport object.
    // The value parameter is declared as Object, but will be String, Double, or Date depending on the database type defined above.
    // Because we chose FieldType TEXT, we will get a String and can reuse getSingularObjectFromString().
    @Nullable
    @Override
    protected BigDecimal getObjectFromDbValue(final Object dataBaseValue) throws FieldValidationException {
        return getSingularObjectFromString((String) dataBaseValue);
    }

    //Finally, add the getDbValueFromObject() method.
    // It takes a value as our transport object and converts it to an Object suitable for storing in the DB.
    // In our case we want to convert to String.
    @Nullable
    @Override
    protected Object getDbValueFromObject(final BigDecimal customFieldObject) {
        return getStringFromSingularObject(customFieldObject);
    }
}