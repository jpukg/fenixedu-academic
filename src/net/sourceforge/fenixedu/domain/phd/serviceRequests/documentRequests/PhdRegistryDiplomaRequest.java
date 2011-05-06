package net.sourceforge.fenixedu.domain.phd.serviceRequests.documentRequests;

import java.util.Locale;

import net.sourceforge.fenixedu.dataTransferObject.serviceRequests.AcademicServiceRequestBean;
import net.sourceforge.fenixedu.dataTransferObject.serviceRequests.AcademicServiceRequestCreateBean;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.accounting.EventType;
import net.sourceforge.fenixedu.domain.accounting.events.serviceRequests.PhdRegistryDiplomaRequestEvent;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.degreeStructure.CycleType;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.phd.PhdIndividualProgramProcess;
import net.sourceforge.fenixedu.domain.phd.exceptions.PhdDomainOperationException;
import net.sourceforge.fenixedu.domain.phd.serviceRequests.PhdAcademicServiceRequestCreateBean;
import net.sourceforge.fenixedu.domain.phd.serviceRequests.PhdDocumentRequestCreateBean;
import net.sourceforge.fenixedu.domain.phd.thesis.PhdThesisFinalGrade;
import net.sourceforge.fenixedu.domain.serviceRequests.IRegistryDiplomaRequest;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.DocumentRequestType;
import net.sourceforge.fenixedu.domain.serviceRequests.documentRequests.RegistryDiplomaRequest;

import org.joda.time.LocalDate;

public class PhdRegistryDiplomaRequest extends PhdRegistryDiplomaRequest_Base implements IRegistryDiplomaRequest {

    protected PhdRegistryDiplomaRequest() {
	super();
    }

    protected PhdRegistryDiplomaRequest(final PhdDocumentRequestCreateBean bean) {
	this();
	init(bean);
    }

    @Override
    protected void init(AcademicServiceRequestCreateBean bean) {
	throw new DomainException("invoke init(PhdDocumentRequestCreateBean)");
    }

    @Override
    protected void init(PhdAcademicServiceRequestCreateBean bean) {
	throw new DomainException("invoke init(PhdDocumentRequestCreateBean)");
    }

    protected void init(final PhdDocumentRequestCreateBean bean) {
	checkParameters(bean);
	super.init(bean);

	if (!isFree()) {
	    PhdRegistryDiplomaRequestEvent.create(getAdministrativeOffice(), getPhdIndividualProgramProcess().getPerson(), this);
	}

	setDiplomaSupplement(PhdDiplomaSupplementRequest.create(bean));
    }

    private void checkParameters(final PhdDocumentRequestCreateBean bean) {
	PhdIndividualProgramProcess process = bean.getPhdIndividualProgramProcess();

	if (process.hasDiplomaRequest()) {
	    throw new DomainException("error.phdRegistryDiploma.alreadyHasDiplomaRequest");
	}

	if (process.hasRegistryDiplomaRequest()) {
	    throw new DomainException("error.phdRegistryDiploma.alreadyRequested");
	}
    }

    @Override
    public boolean isPayedUponCreation() {
	return true;
    }

    @Override
    public boolean isToPrint() {
	return false;
    }

    @Override
    public boolean isPossibleToSendToOtherEntity() {
	return true;
    }

    @Override
    public boolean isManagedWithRectorateSubmissionBatch() {
	return true;
    }

    @Override
    public EventType getEventType() {
	return EventType.BOLONHA_PHD_REGISTRY_DIPLOMA_REQUEST;
    }

    @Override
    public boolean hasPersonalInfo() {
	return true;
    }

    @Override
    public CycleType getRequestedCycle() {
	return CycleType.THIRD_CYCLE;
    }

    @Override
    public DocumentRequestType getDocumentRequestType() {
	return DocumentRequestType.REGISTRY_DIPLOMA_REQUEST;
    }

    @Override
    public String getDocumentTemplateKey() {
	return RegistryDiplomaRequest.class.getName();
    }

    @Override
    public String getFinalAverage(final Locale locale) {
	PhdThesisFinalGrade finalGrade = getPhdIndividualProgramProcess().getFinalGrade();
	return finalGrade.getLocalizedName(locale);
    }

    @Override
    public String getQualifiedAverageGrade(final Locale locale) {
	String qualifiedAverageGrade;

	PhdThesisFinalGrade grade = getPhdIndividualProgramProcess().getFinalGrade();

	switch (grade) {
	case APPROVED:
	case PRE_BOLONHA_APPROVED:
	    qualifiedAverageGrade = "sufficient";
	    break;
	case APPROVED_WITH_PLUS:
	case PRE_BOLONHA_APPROVED_WITH_PLUS:
	    qualifiedAverageGrade = "good";
	    break;
	case APPROVED_WITH_PLUS_PLUS:
	case PRE_BOLONHA_APPROVED_WITH_PLUS_PLUS:
	    qualifiedAverageGrade = "verygood";
	    break;
	default:
	    throw new DomainException("docs.academicAdministrativeOffice.RegistryDiploma.unknown.grade");
	}

	return "diploma.supplement.qualifiedgrade." + qualifiedAverageGrade;
    }

    @Override
    public LocalDate getConclusionDate() {
	return getPhdIndividualProgramProcess().getConclusionDate();
    }

    @Override
    public ExecutionYear getConclusionYear() {
	return ExecutionYear.readByDateTime(getPhdIndividualProgramProcess().getConclusionDate());
    }

    @Override
    public String getGraduateTitle(Locale locale) {
	return getPhdIndividualProgramProcess().getGraduateTitle(locale);
    }

    @Override
    protected void internalChangeState(AcademicServiceRequestBean academicServiceRequestBean) {
	try {
	    verifyIsToProcessAndHasPersonalInfo(academicServiceRequestBean);
	    verifyIsToDeliveredAndIsPayed(academicServiceRequestBean);
	} catch (DomainException e) {
	    throw new PhdDomainOperationException(e.getKey(), e, e.getArgs());
	}

	super.internalChangeState(academicServiceRequestBean);
	if (academicServiceRequestBean.isToProcess()) {
	    if (!getPhdIndividualProgramProcess().isConclusionProcessed()) {
		throw new PhdDomainOperationException("error.registryDiploma.registrationNotSubmitedToConclusionProcess");
	    }

	    if (isPayable() && !isPayed()) {
		throw new PhdDomainOperationException("AcademicServiceRequest.hasnt.been.payed");
	    }

	    if (getRegistryCode() == null) {
		getRootDomainObject().getInstitutionUnit().getRegistryCodeGenerator().createRegistryFor(this);
		getAdministrativeOffice().getCurrentRectorateSubmissionBatch().addDocumentRequest(this);
	    }

	    if (getLastGeneratedDocument() == null) {
		generateDocument();
	    }

	    getDiplomaSupplement().process();
	} else if (academicServiceRequestBean.isToConclude()) {
	    if (getDiplomaSupplement().isConcludedSituationAccepted()) {
		getDiplomaSupplement().conclude();
	    }
	} else if (academicServiceRequestBean.isToCancelOrReject()) {
	    if (hasEvent()) {
		getEvent().cancel(academicServiceRequestBean.getEmployee());
	    }

	    getDiplomaSupplement().cancel(academicServiceRequestBean.getJustification());

	    if (academicServiceRequestBean.isToReject()) {
		getDiplomaSupplement().reject(academicServiceRequestBean.getJustification());
	    }
	}
    }

    @Override
    public String getDescription() {
	return getDescription(getAcademicServiceRequestType(), getDocumentRequestType().getQualifiedName() + "."
		+ DegreeType.BOLONHA_ADVANCED_SPECIALIZATION_DIPLOMA.name());
    }

    public static PhdRegistryDiplomaRequest create(final PhdDocumentRequestCreateBean bean) {
	return new PhdRegistryDiplomaRequest(bean);
    }

}