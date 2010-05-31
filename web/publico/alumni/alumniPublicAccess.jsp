<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<html:xhtml />
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
<%@ taglib uri="/WEB-INF/jcaptcha.tld" prefix="jcaptcha"%>

<!-- alumniPublicAccess.jsp -->

<h1><bean:message key="label.alumni.registration" bundle="ALUMNI_RESOURCES" /></h1>

<h2><bean:message key="label.alumni.registration.form" bundle="ALUMNI_RESOURCES" /> <span class="color777 fwnormal"><bean:message key="label.step.1.3" bundle="ALUMNI_RESOURCES" /></span></h2>

<div class="alumnilogo">
<logic:present name="alumniPublicAccessMessage">
	<span class="error0"><bean:write name="alumniPublicAccessMessage" scope="request" /></span><br/>
</logic:present>

<html:messages id="message" message="true" bundle="ALUMNI_RESOURCES">
<span class="error0"><!-- Error messages go here --><bean:write name="message" /></span>
</html:messages>

<div class="reg_form">	

	<fr:form action="/alumni.do?method=validateFenixAcessData">

		<fieldset style="display: block;">
			<legend>Identificação <%-- <bean:message key="label.alumni.form" bundle="ALUMNI_RESOURCES" /> --%></legend>
			<p>
				<bean:message key="label.alumni.registration.process" bundle="ALUMNI_RESOURCES" />
				
			</p>
		
			<fr:edit id="alumniBean" name="alumniBean" visible="false" />


			<label for="student_number" class="student_number">
				<bean:message key="label.student.number" bundle="ALUMNI_RESOURCES" />:
			</label>
			<fr:edit id="studentNumber-validated" name="alumniBean" slot="studentNumber" validator="pt.ist.fenixWebFramework.renderers.validators.RequiredValidator" >
				<fr:destination name="invalid" path="/alumni.do?method=initFenixPublicAccess&showForm=true"/>
				<fr:layout>
					<fr:property name="size" value="30"/>
					<fr:property name="style" value="display: inline;"/>
				</fr:layout>
			</fr:edit>
			<span class="error0"><fr:message for="studentNumber-validated" /></span>
			<html:link href="<%= request.getContextPath() + "/publico/alumni.do?method=requestIdentityCheck"%>"><bean:message bundle="ALUMNI_RESOURCES" key="link.request.identity.check"/></html:link>
			
			
					
			<label for="bi_number" class="bi_number">
				<bean:message key="label.document.id.number" bundle="ALUMNI_RESOURCES" />:
			</label>
			<fr:edit id="documentIdNumber-validated" name="alumniBean" slot="documentIdNumber" validator="pt.ist.fenixWebFramework.renderers.validators.RequiredValidator">
				<fr:destination name="invalid" path="/alumni.do?method=initFenixPublicAccess&showForm=true"/>
				<fr:layout>
					<fr:property name="size" value="30"/>
					<fr:property name="style" value="display: inline;"/>
				</fr:layout>
			</fr:edit>
			<span class="error0"><fr:message for="documentIdNumber-validated" /></span>

			
			<label for="email">
				<bean:message key="label.email" bundle="ALUMNI_RESOURCES" />:
			</label>
			<fr:edit id="email-validated" name="alumniBean" slot="email" validator="net.sourceforge.fenixedu.presentationTier.renderers.validators.RequiredEmailValidator">
				<fr:destination name="invalid" path="/alumni.do?method=initFenixPublicAccess&showForm=true"/>
				<fr:layout>
					<fr:property name="size" value="40"/>
					<fr:property name="style" value="display: inline;"/>
				</fr:layout>
			</fr:edit>
			<span class="error0"><fr:message for="email-validated" /></span>


			<label for="captcha">
				<bean:message key="label.captcha" bundle="ALUMNI_RESOURCES" />:
			</label>
			<div class="mbottom05"><img src="<%= request.getContextPath() + "/publico/jcaptcha.do" %>"/><br/></div>
			<span class="color777"><bean:message key="label.captcha.process" bundle="ALUMNI_RESOURCES" /></span><br/>
			<input type="text" name="j_captcha_response" size="30" style="margin-bottom: 1em;"/>
			
			<logic:present name="captcha.unknown.error">
				<p style="margin: 0;">
					<span class="error0">
						<bean:message key="captcha.unknown.error" bundle="ALUMNI_RESOURCES" />
					</span>
				</p>
			</logic:present>

			<br/>

			<fr:edit id="privacyPolicy-validated" name="alumniBean" slot="privacyPolicy" validator="pt.ist.fenixWebFramework.renderers.validators.RequiredValidator">
				<fr:layout>
					<fr:property name="style" value="display: inline;"/>
				</fr:layout>
			</fr:edit>
			<label style="display: inline;">
				<bean:message key="label.privacy.policy.a" bundle="ALUMNI_RESOURCES" />
				<html:link href="#" onclick="document.getElementById('policyPrivacy').style.display='block'" >
					<bean:message key="label.privacy.policy.b" bundle="ALUMNI_RESOURCES" />
				</html:link>
			</label>

			<div id="policyPrivacy" class="switchInline mtop1">
				<bean:message key="label.privacy.policy.text" bundle="ALUMNI_RESOURCES" />
			</div>
			
			
			<logic:present name="privacyPolicyPublicAccessMessage">
				<span class="error0">
					<bean:message key="privacy.policy.acceptance" bundle="ALUMNI_RESOURCES" />
				</span>
			</logic:present>
			
			<p class="comment"><bean:message key="label.all.required.fields" bundle="ALUMNI_RESOURCES" /></p>

			<html:submit>
				<bean:message key="label.submit" bundle="ALUMNI_RESOURCES" />
			</html:submit>
	
		</fieldset>
	
	</fr:form>
</div>
</div>

<!-- END CONTENTS -->
</div>





