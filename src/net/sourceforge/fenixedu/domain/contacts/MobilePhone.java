package net.sourceforge.fenixedu.domain.contacts;

import java.util.Comparator;

import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.organizationalStructure.Party;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class MobilePhone extends MobilePhone_Base {

    public static Comparator<MobilePhone> COMPARATOR_BY_NUMBER = new Comparator<MobilePhone>() {
	public int compare(MobilePhone contact, MobilePhone otherContact) {
	    final String number = contact.getNumber();
	    final String otherNumber = otherContact.getNumber();
	    int result = 0;
	    if (number != null && otherNumber != null) {
		result = number.compareTo(otherNumber);
	    } else if (number != null) {
		result = 1;
	    } else if (otherNumber != null) {
		result = -1;
	    }
	    return (result == 0) ? COMPARATOR_BY_TYPE.compare(contact, otherContact) : result;
	}
    };

    public static MobilePhone createMobilePhone(Party party, String number, PartyContactType type, Boolean isDefault,
	    Boolean visibleToPublic, Boolean visibleToStudents, Boolean visibleToTeachers, Boolean visibleToEmployees,
	    Boolean visibleToAlumni) {
	for (MobilePhone phone : party.getMobilePhones()) {
	    if (phone.getNumber().equals(number))
		return phone;
	}
	return (!StringUtils.isEmpty(number)) ? new MobilePhone(party, type, visibleToPublic, visibleToStudents,
		visibleToTeachers, visibleToEmployees, visibleToAlumni, isDefault, number) : null;
    }

    public static MobilePhone createMobilePhone(Party party, String number, PartyContactType type, boolean isDefault) {
	for (MobilePhone phone : party.getMobilePhones()) {
	    if (phone.getNumber().equals(number))
		return phone;
	}
	return (!StringUtils.isEmpty(number)) ? new MobilePhone(party, type, isDefault, number) : null;
    }

    protected MobilePhone() {
	super();
    }

    protected MobilePhone(final Party party, final PartyContactType type, final boolean defaultContact, final String number) {
	this();
	super.init(party, type, defaultContact);
	checkParameters(number);
	super.setNumber(number);
    }

    protected MobilePhone(final Party party, final PartyContactType type, final boolean visibleToPublic,
	    final boolean visibleToStudents, final boolean visibleToTeachers, final boolean visibleToEmployees,
	    final boolean visibleToAlumni, final boolean defaultContact, final String number) {
	this();
	super.init(party, type, visibleToPublic, visibleToStudents, visibleToTeachers, visibleToEmployees, visibleToAlumni,
		defaultContact);
	checkParameters(number);
	super.setNumber(number);
    }

    private void checkParameters(final String number) {
	if (StringUtils.isEmpty(number)) {
	    throw new DomainException("error.contacts.Phone.invalid.number");
	}
    }

    @Override
    public boolean isMobile() {
	return true;
    }

    public void edit(final String number) {
	super.setNumber(number);
	setLastModifiedDate(new DateTime());
    }

    @Override
    public String getPresentationValue() {
	return getNumber();
    }

    public boolean hasNumber() {
	return getNumber() != null && !getNumber().isEmpty();
    }
}
