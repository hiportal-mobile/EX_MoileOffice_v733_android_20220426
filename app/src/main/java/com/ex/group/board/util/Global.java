package com.ex.group.board.util;

public class Global
{
	
	//	도로공사 운영
	public final static String SERVER_URL 		= 	"http://128.200.121.68:9000/emp_ex/service.pe";
	public final static String FILE_SERVER_URL 	= 	"http://128.200.121.68:9000/emp_ex/upload.pe";
	public final static String DZCSURL			=	"http://128.200.121.68:9000";

	public final static String DZCSURLPREFIX	=	"toiphoneapp://callDocumentFunction?";
	public final static String PRIMITIVE 				= 		"primitive"					;
	public final static String JSP_DZC					=		"COMMON_MO_ISSCONVERT"		;
	public final static String JSP_STCHRT 				= 		"AW_MO_AWSMSTCHRTR"			;		//"AwSm_StChrt_R.jsp";					//	좌석배치도		o
	public final static String JSP_SMQNA 				= 		"AW_MO_AWSMQALISTR"			;		//"AwSm_QAList_R.jsp";					//	의원질의답변		o
	public final static String JSP_CONSULTING_LIST 		= 		"COMMON_AUDIT_LISTCONSULTING"		;		//"listconsulting.jsp";			// 	감사컨설팅 리스트
		
	public final static String MSG_RESULT_CODE 			= 		"result";
	public final static String MSG_RESULT_MESSAGE 		= 		"resultMessage";
	
	//	Handler Message
	
public final static int MSG_FAILED = 0;
	public final static int MSG_SUCEEDED = 1;
	public final static int MSG_SUCEEDED_2 = 2;
	public final static int MSG_RESULT_SUCCEEDED 	= 1000;
	public final static int HM_ERR_NETWORK 			= 1002;
	public final static int HM_ERR_XML_PARSING 		= 1003;
	public final static int HM_ERR_XML_RESULT 		= 1004;
	
	public final static int MSG_IMAGE_DOWNLOAD_COMPLETE = 200;
	public final static int MSG_IMAGE_DOWNLOAD_FAIL = 201;
	//login 정보 
	public static String LOGIN_ID	=	"";			//점속자 ID
}
