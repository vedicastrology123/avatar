<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<%-- Inside error.jsp --%>
<%
    Throwable t = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
    if (t == null) t = (Throwable) request.getAttribute("javax.servlet.error.exception");
    
    if (t != null) {
        System.err.println("--- ACTUAL ERROR REDIRECTED TO ERROR.JSP ---");
        t.printStackTrace(System.err);
    } else {
        System.err.println("--- ERROR.JSP reached, but no Exception object found ---");
    }
%>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width">
  <title>Error Page</title>
  <link rel="icon" type="image/x-icon" href="img/favicon.ico">
  
</head>
<body>
	<div class="row">
		<div class="large-10 columns">
			<br></br>
			Errors occurred, Contact the web site developer for your problem, at vedicastrology123@gmail.com, Thanks.
			<br></br>
		</div>
	</div>
</body>
</html>

