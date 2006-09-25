<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
<html:xhtml/>

<logic:present role="RESEARCHER">
	<em><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.ResultPublication.management.title"/></em>
	<h3><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex"/></h3>
	
	<fr:form action="/bibtexManagement/ignorePublication.do">
		<h3><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.bibtexToProcess"/>&nbsp;
		(<fr:view name="importBibtexBean" property="currentPublicationPosition"/>/<fr:view name="importBibtexBean" property="totalPublicationsReaded"/>)
		</h3>
		<fr:edit id="importBibtexBean" visible="false" name="importBibtexBean" type="net.sourceforge.fenixedu.dataTransferObject.research.result.publication.bibtex.ImportBibtexBean"/>
		<fr:view name="importBibtexBean" property="currentBibtex"/>
		<html:submit><bean:message bundle="RESEARCHER_RESOURCES" key="button.ignoreBibtexPublication"/></html:submit>
		<br /><br />
	</fr:form>
	
	<logic:messagesPresent message="true">
		<html:messages id="messages" message="true" bundle="RESEARCHER_RESOURCES">
			<span class="error"><!-- Error messages go here --><bean:write name="messages" /></span>
		</html:messages>
		<br/><br/>
	</logic:messagesPresent>
	
	<logic:equal name="importBibtexBean" property="currentProcessedParticipators" value="false">
		<fr:form action="/bibtexManagement/setParticipations.do">
			<fr:edit id="importBibtexBean" visible="false" name="importBibtexBean" type="net.sourceforge.fenixedu.dataTransferObject.research.result.publication.bibtex.ImportBibtexBean"/>

			<b><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.step"/> 1 : </b>
			<u><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.setAuthorsAndEditors"/></u> >
 			<b><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.step"/> 2 : </b>
		 	<bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.setPublicationData"/>
		
			<!-- Set Participations -->
 			<logic:notEmpty name="importBibtexBean" property="currentAuthors">
				<h3><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.authors"/></h3>
				<logic:iterate id="author" name="importBibtexBean" property="currentAuthors">
					<bean:define id="participatorBean" name="author" type="net.sourceforge.fenixedu.dataTransferObject.research.result.publication.bibtex.BibtexParticipatorBean"/>
					<fr:edit id="<%=participatorBean.getBibtexPerson()%>" nested="true" name="author" schema="<%=participatorBean.getActiveSchema()%>">
				   		<fr:destination name="invalid" path="/bibtexManagement/invalidSubmit.do"/>
			   		</fr:edit>
					<hr/>
				</logic:iterate>
 			</logic:notEmpty>
			
			<logic:notEmpty name="importBibtexBean" property="currentEditors">
				<h3><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.editors"/></h3>
				<logic:iterate id="editor" name="importBibtexBean" property="currentEditors">
					<bean:define id="participatorBean" name="editor" type="net.sourceforge.fenixedu.dataTransferObject.research.result.publication.bibtex.BibtexParticipatorBean"/>
					<fr:edit id="<%=participatorBean.getBibtexPerson()%>" nested="true" name="editor" schema="<%=participatorBean.getActiveSchema()%>">
						<fr:destination name="invalid" path="/bibtexManagement/invalidSubmit.do"/>
			   		</fr:edit>
					<hr/>
				</logic:iterate>
			</logic:notEmpty>
	
			<i><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.warningParticipants"/></i><br/>
			<html:submit><bean:message bundle="RESEARCHER_RESOURCES" key="button.continue"/></html:submit>
		</fr:form>
	</logic:equal>
		
	<logic:equal name="importBibtexBean" property="currentProcessedParticipators" value="true">
		<fr:form action="/bibtexManagement/createPublication.do">
			<fr:edit id="importBibtexBean" visible="false" name="importBibtexBean" type="net.sourceforge.fenixedu.dataTransferObject.research.result.publication.bibtex.ImportBibtexBean"/>
			
			<b><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.step"/> 1 : </b>
			<bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.setAuthorsAndEditors"/> >
 			<b><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.step"/> 2 : </b>
		 	<u><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.setPublicationData"/></u>

			<!-- Participations -->
<!--			<h3><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.participations"/></h3>
			<fr:view name="importBibtexBean" layout="tabular-list" property="currentParticipators">
				<fr:layout>
					<fr:property name="subLayout" value="values"/>
					<fr:property name="subSchema" value="bibtex.participatorDescription"/>
				</fr:layout>
			</fr:view>
-->
			<!-- Set Publication data -->
	 		<h3><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.result.publication.importBibtex.publicationData"/></h3>
			<bean:define id="publicationData" name="importBibtexBean" property="currentPublicationBean" type="net.sourceforge.fenixedu.dataTransferObject.research.result.publication.ResultPublicationBean"/>
			<fr:edit nested="true" id="publicationData" name="importBibtexBean" property="currentPublicationBean" schema="<%=publicationData.getActiveSchema()%>">
				<fr:destination name="invalid" path="/bibtexManagement/invalidSubmit.do"/>
	   		</fr:edit>
	   		
	   		<!-- Create event in case of inproceedings or proceedings -->
			<logic:equal name="publicationData" property="createEvent" value="true">
				<br/>
				<bean:message bundle="RESEARCHER_RESOURCES" key="researcher.ResultPublication.createEvent"/>
				<fr:edit id="createEvent" name="importBibtexBean" property="currentPublicationBean" schema="result.publication.create.Event" nested="true">
			   		<fr:destination name="invalid" path="/bibtexManagement/invalidSubmit.do"/>
				</fr:edit>
			</logic:equal>
			
			<html:submit><bean:message bundle="RESEARCHER_RESOURCES" key="button.submit"/></html:submit>
		</fr:form>
	</logic:equal>
</logic:present>
