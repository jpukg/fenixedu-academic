drop temporary table TEMP_CP; 

create temporary table TEMP_CP 
select MAX(CONCLUSION_PROCESS_VERSION.CREATION_DATE_TIME) as m, CONCLUSION_PROCESS.ID_INTERNAL as cp 
FROM CONCLUSION_PROCESS_VERSION, CONCLUSION_PROCESS 
where CONCLUSION_PROCESS.ID_INTERNAL = CONCLUSION_PROCESS_VERSION.KEY_CONCLUSION_PROCESS 
group by CONCLUSION_PROCESS_VERSION.KEY_CONCLUSION_PROCESS;  

update TEMP_CP, CONCLUSION_PROCESS_VERSION, CONCLUSION_PROCESS 
set CONCLUSION_PROCESS.KEY_LAST_VERSION = CONCLUSION_PROCESS_VERSION.ID_INTERNAL 
where CONCLUSION_PROCESS_VERSION.CREATION_DATE_TIME = TEMP_CP.m 
and CONCLUSION_PROCESS_VERSION.KEY_CONCLUSION_PROCESS = TEMP_CP.cp 
and CONCLUSION_PROCESS.ID_INTERNAL = TEMP_CP.cp;

drop temporary table TEMP_CP;
