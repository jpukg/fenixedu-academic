package net.sourceforge.fenixedu.presentationTier.Action.delegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.dataTransferObject.delegate.DelegateCurricularCourseBean;
import net.sourceforge.fenixedu.dataTransferObject.research.result.ExecutionYearBean;
import net.sourceforge.fenixedu.domain.Coordinator;
import net.sourceforge.fenixedu.domain.CurricularCourse;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.DegreeModuleScope;
import net.sourceforge.fenixedu.domain.DomainObject;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.accessControl.DelegateCurricularCourseStudentsGroup;
import net.sourceforge.fenixedu.domain.accessControl.DelegateStudentsGroup;
import net.sourceforge.fenixedu.domain.accessControl.Group;
import net.sourceforge.fenixedu.domain.organizationalStructure.FunctionType;
import net.sourceforge.fenixedu.domain.organizationalStructure.PersonFunction;
import net.sourceforge.fenixedu.domain.student.Student;
import net.sourceforge.fenixedu.domain.util.email.Recipient;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;
import net.sourceforge.fenixedu.presentationTier.Action.messaging.EmailsDA;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/sendEmailToDelegateStudents", module = "delegate")
@Forwards( { @Forward(name = "choose-receivers", path = "/delegate/chooseReceivers.jsp"),
	@Forward(name = "choose-student-receivers", path = "/delegate/chooseStudentReceivers.jsp") })
public class SendEmailToDelegateStudents extends FenixDispatchAction {

    @SuppressWarnings("unchecked")
    protected List<Group> getPossibleReceivers(HttpServletRequest request, ExecutionYear executionYear) {
	List<Group> groups = new ArrayList<Group>();

	final Person person = getLoggedPerson(request);

	PersonFunction delegateFunction = null;
	if (person.hasStudent()) {
	    final Student student = person.getStudent();
	    final Degree degree = student.getLastActiveRegistration().getDegree();
	    delegateFunction = degree.getMostSignificantDelegateFunctionForStudent(student, executionYear);
	} else {
	    delegateFunction = person.getActiveGGAEDelegatePersonFunction();
	}

	if (delegateFunction != null) {
	    if (request.getAttribute("curricularCoursesList") != null) {
		executionYear = executionYear == null ? ExecutionYear.getExecutionYearByDate(delegateFunction.getBeginDate())
			: executionYear;

		List<CurricularCourse> curricularCourses = (List<CurricularCourse>) request.getAttribute("curricularCoursesList");
		for (CurricularCourse curricularCourse : curricularCourses) {
		    groups.add(new DelegateCurricularCourseStudentsGroup(curricularCourse, executionYear));
		}
	    } else {
		if (delegateFunction != null && delegateFunction.getFunction().isOfFunctionType(FunctionType.DELEGATE_OF_GGAE)) {
		    groups.add(new DelegateStudentsGroup(delegateFunction));
		} else if (delegateFunction != null
			&& delegateFunction.getFunction().isOfFunctionType(FunctionType.DELEGATE_OF_YEAR)) {
		    groups.add(new DelegateStudentsGroup(delegateFunction));
		} else {
		    groups.add(new DelegateStudentsGroup(delegateFunction, FunctionType.DELEGATE_OF_YEAR));
		    groups.add(new DelegateStudentsGroup(delegateFunction));
		}
	    }
	}

	return groups;
    }

    public ActionForward prepareSendEmail(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) {
	GroupsBean recipientsBean = (GroupsBean) getRenderedObject("recipientsBean");
	String year = request.getParameter("year");
	ExecutionYear executionYear = year == null ? ExecutionYear.readCurrentExecutionYear() : (ExecutionYear) DomainObject
		.fromExternalId(year);
	List<Recipient> recipients;
	List<Group> recipientsGroups;
	if (request.getAttribute("curricularCoursesList") != null) {
	    recipientsGroups = getPossibleReceivers(request, executionYear);
	} else {
	    recipientsGroups = recipientsBean.getSelected();
	}
	recipients = Recipient.newInstance(recipientsGroups);
	return EmailsDA.sendEmail(request, null, recipients.toArray(new Recipient[] {}));
    }

    public ActionForward prepare(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	RenderUtils.invalidateViewState();
	String year = request.getParameter("year");
	ExecutionYear executionYear = year == null ? ExecutionYear.readCurrentExecutionYear() : (ExecutionYear) DomainObject
		.fromExternalId(year);
	return prepare(mapping, actionForm, request, response, executionYear);
    }

    private ActionForward prepare(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response, ExecutionYear executionYear) throws Exception {
	GroupsBean recipients = new GroupsBean(getPossibleReceivers(request, executionYear));
	request.setAttribute("recipients", recipients);
	ExecutionYearBean executionYearBean = new ExecutionYearBean(executionYear);
	request.setAttribute("currentExecutionYear", executionYearBean);
	return mapping.findForward("choose-student-receivers");
    }

    private ActionForward prepareSendToStudentsFromSelectedCurricularCourses(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response, ExecutionYear executionYear) throws Exception {
	final Person person = getLoggedPerson(request);
	PersonFunction delegateFunction = null;
	if (person.hasStudent()) {
	    final Student student = person.getStudent();
	    final Degree degree = student.getLastActiveRegistration().getDegree();
	    delegateFunction = degree.getMostSignificantDelegateFunctionForStudent(student, executionYear);
	} else {
	    delegateFunction = person.getActiveGGAEDelegatePersonFunction();
	}

	if (delegateFunction != null) {
	    request.setAttribute("curricularCoursesList", getCurricularCourses(person, executionYear));
	} else {
	    addActionMessage(request, "error.delegates.sendMail.notExistentDelegateFunction");
	}

	ExecutionYearBean executionYearBean = new ExecutionYearBean(executionYear);
	request.setAttribute("currentExecutionYear", executionYearBean);
	return mapping.findForward("choose-receivers");
    }

    public ActionForward prepareSendToStudentsFromSelectedCurricularCourses(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) throws Exception {

	String year = request.getParameter("year");
	ExecutionYear executionYear = (ExecutionYear) (year != null ? DomainObject.fromExternalId(year) : ExecutionYear
		.readCurrentExecutionYear());
	return prepareSendToStudentsFromSelectedCurricularCourses(mapping, actionForm, request, response, executionYear);
    }

    public ActionForward sendToStudentsFromSelectedCurricularCourses(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) throws Exception {

	final List<String> selectedCurricularCourses = Arrays.asList(request.getParameterValues("selectedCurricularCourses"));
	List<CurricularCourse> curricularCourses = new ArrayList<CurricularCourse>();
	for (String curricularCourseId : selectedCurricularCourses) {
	    Integer curricularId = Integer.parseInt(curricularCourseId);
	    CurricularCourse curricularCourse = (CurricularCourse) rootDomainObject.readDegreeModuleByOID(curricularId);
	    curricularCourses.add(curricularCourse);
	}

	if (!curricularCourses.isEmpty()) {
	    request.setAttribute("curricularCoursesList", curricularCourses);
	    return prepareSendEmail(mapping, actionForm, request, response);

	} else {
	    addActionMessage(request, "error.delegates.sendMail.curricularCoursesNotSelected");
	    RenderUtils.invalidateViewState("selectedCurricularCourses");
	    return prepareSendToStudentsFromSelectedCurricularCourses(mapping, actionForm, request, response);
	}
    }

    /*
     * AUXILIARY METHODS
     */

    private PersonFunction getPersonFunction(Person person, ExecutionYear executionYear) {
	if (person.hasStudent()) {
	    final Student student = person.getStudent();
	    final Degree degree = student.getLastActiveRegistration().getDegree();
	    return degree.getMostSignificantDelegateFunctionForStudent(student, executionYear);
	} else {
	    return person.getActiveGGAEDelegatePersonFunction();
	}
    }

    private Set<CurricularCourse> getDegreesCurricularCoursesFromCoordinatorRoles(List<Coordinator> coordinators,
	    ExecutionYear executionYear) {
	Set<CurricularCourse> curricularCourses = new HashSet<CurricularCourse>();
	for (Coordinator coordinator : coordinators) {
	    final Degree degree = coordinator.getExecutionDegree().getDegree();
	    curricularCourses.addAll(degree.getAllCurricularCourses(executionYear));
	}
	return curricularCourses;
    }

    private List<DelegateCurricularCourseBean> getCurricularCourses(final Person person, ExecutionYear executionYear) {
	List<DelegateCurricularCourseBean> result = new ArrayList<DelegateCurricularCourseBean>();
	executionYear = executionYear == null ? ExecutionYear.readCurrentExecutionYear() : executionYear;
	final PersonFunction delegateFunction = getPersonFunction(person, executionYear);
	if (delegateFunction != null) {
	    if (person.hasStudent()) {
		Set<CurricularCourse> curricularCourses = person.getStudent().getCurricularCoursesResponsibleForByFunctionType(
			delegateFunction.getFunction().getFunctionType(), executionYear);
		return getCurricularCoursesBeans(delegateFunction, curricularCourses, executionYear);
	    } else if (person.hasAnyCoordinators()) {
		Set<CurricularCourse> curricularCourses = getDegreesCurricularCoursesFromCoordinatorRoles(person
			.getCoordinators(), executionYear);
		return getCurricularCoursesBeans(delegateFunction, curricularCourses, executionYear);
	    }
	}
	return result;
    }

    @SuppressWarnings("unchecked")
    private List<DelegateCurricularCourseBean> getCurricularCoursesBeans(PersonFunction delegateFunction,
	    Set<CurricularCourse> curricularCourses, ExecutionYear executionYear) {
	final FunctionType delegateFunctionType = delegateFunction.getFunction().getFunctionType();

	List<DelegateCurricularCourseBean> result = new ArrayList<DelegateCurricularCourseBean>();

	for (CurricularCourse curricularCourse : curricularCourses) {
	    for (ExecutionSemester executionSemester : executionYear.getExecutionPeriods()) {
		if (curricularCourse.hasAnyExecutionCourseIn(executionSemester)) {
		    for (DegreeModuleScope scope : curricularCourse.getDegreeModuleScopes()) {
			if (!scope.getCurricularSemester().equals(executionSemester.getSemester())) {
			    continue;
			}

			if (delegateFunctionType.equals(FunctionType.DELEGATE_OF_YEAR)
				&& !scopeBelongsToDelegateCurricularYear(scope, delegateFunction.getCurricularYear().getYear())) {
			    continue;
			}
			DelegateCurricularCourseBean bean = new DelegateCurricularCourseBean(curricularCourse, executionYear,
				scope.getCurricularYear(), executionSemester);
			bean.calculateEnrolledStudents();
			result.add(bean);
		    }
		}

	    }
	}
	Collections.sort(result,
		DelegateCurricularCourseBean.CURRICULAR_COURSE_COMPARATOR_BY_CURRICULAR_YEAR_AND_CURRICULAR_SEMESTER);

	return result;
    }

    private boolean scopeBelongsToDelegateCurricularYear(DegreeModuleScope scope, Integer curricularYear) {
	if (scope.getCurricularYear().equals(curricularYear)) {
	    return true;
	}
	return false;
    }

    public ActionForward chooseExecutionYear(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	ExecutionYearBean executionYearBean = (ExecutionYearBean) getRenderedObject("chooseExecutionYear");
	RenderUtils.invalidateViewState();
	return prepare(mapping, actionForm, request, response, executionYearBean.getExecutionYear());
    }

    public ActionForward chooseExecutionYearCurricularCourseList(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) throws Exception {
	ExecutionYearBean executionYearBean = (ExecutionYearBean) getRenderedObject("chooseExecutionYear");
	RenderUtils.invalidateViewState();
	return prepareSendToStudentsFromSelectedCurricularCourses(mapping, actionForm, request, response, executionYearBean
		.getExecutionYear());
    }

}
