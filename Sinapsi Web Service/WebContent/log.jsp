<%@page import="java.io.BufferedReader"%>
<%@page import="com.sinapsi.utils.Pair"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Sinapsi</title>
<link href="assets/css/bootstrap.css" rel="stylesheet" />
<link href="assets/css/font-awesome.css" rel="stylesheet" />
<link href="assets/css/datepicker.css" rel="stylesheet" type="text/css" />
<script src="assets/js/jquery-1.7.1.min.js"></script>
<script src="assets/js/jquery-ui-1.8.18.custom.min.js"></script>
<link href="assets/css/custom-styles.css" rel="stylesheet" />
<link href='http://fonts.googleapis.com/css?family=Lato:300,400,700'
	rel='stylesheet' type='text/css' />
</head>
<%
  String role = (String) session.getAttribute("role");
  if(role == "admin") {
%>
<script type="text/javascript">
	function scrollDown() {
		$(document).scrollTop($(document).height());
	}
	window.onload = scrollDown;
</script>
<%
  }
%>
<body>
	<%
	  String email = null;
	   Cookie[] cookies = request.getCookies();
	   if(cookies != null) {
	      for(Cookie cookie : cookies) {
	         if(cookie.getName().equals("user")) 
	            email = cookie.getValue();
	      }
	   }
	   if(email == null) 
	     response.sendRedirect("login.html");
	 
	   @SuppressWarnings("unchecked")
	   Pair<BufferedReader, String> log = (Pair<BufferedReader, String>) session.getAttribute("log_buffer");
	   session.removeAttribute("log_buffer");
	%>
	<div id="wrapper">
		<nav class="navbar navbar-default top-navbar" role="navigation">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".sidebar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.jsp">Sinapsi</a>
			</div>

			<ul class="nav navbar-top-links navbar-right">
				<li class="dropdown"><a class="dropdown-toggle"
					data-toggle="dropdown" href="#" aria-expanded="false"> <%=email%>
				</a>
					<ul class="dropdown-menu dropdown-user">
						<li class="divider"></li>
						<li><a href="web_logout"><i class="fa fa-sign-out fa-fw"></i>
								Logout</a></li>
					</ul> <!-- /.dropdown-user --></li>
			</ul>
		</nav>

		<nav class="navbar-default navbar-side" role="navigation">
			<div class="sidebar-collapse">
				<ul class="nav" id="main-menu">

					<li><a href="dashboard"><i class="fa fa-dashboard"></i>
							Dashboard</a></li>
					<%
            if (role == "admin") {
          %>
					<li><a href="web_charts"><i class="fa fa-bar-chart-o"></i>
							Charts</a></li>
					<li><a href="web_clients"><i class="fa fa-sitemap"></i>
							Clients Connected </a></li>
					<%
            }
					%>
					<li><a href="web_macro_manager"><i class="fa fa-wrench"></i>
							Macro Manager </a></li>
					<li><a href="web_macro_editor"><i class="fa fa-edit"></i>
							Macro Editor </a></li>
					<li><a href="web_devices"><i class="fa fa-desktop"></i>
              Devices </a></li>
					<%
            if (role == "admin") {
          %>
					<li><a href="#"><i class="glyphicon glyphicon-cog"></i>
							Engine <span class="fa arrow"></span></a>
						<ul class="nav nav-second-level">
							<li><a href="web_log?type=actionlog">Log</a></li>
						</ul></li>
					<li><a class="active-menu" href="#"><i
							class="fa fa-fw fa-file"></i> Log <span class="fa arrow"></span></a>
						<ul class="nav nav-second-level">
							<li><a href="web_log?type=tomcat">Tomcat</a></li>
							<li><a href="web_log?type=catalina">Catalina</a></li>
							<li><a href="web_log?type=db">Database</a></li>
							<li><a href="web_log?type=ws">WebSocket</a></li>
							<li><a href="web_log?type=webs">Web Service</a></li>
						</ul></li>
						<%
              }
						%>
				</ul>
			</div>
		</nav>

		<div id="page-wrapper">
			<div id="page-inner">
				<%
				  if(role == "user") {
				%>
				<div class="row">
					<div class="col-md-6">
						<h1 class="page-header">
							Sinapsi <small>Log</small>
						</h1>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="panel panel-default">
							<div class="panel-heading">Messages</div>
							<div class="panel-body">
								<div class="alert alert-danger">
									You need <strong>administration</strong> permisison to see this
									page.
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<%
			  } if(role == "admin") {
			%>
			<div class="row">
				<div class="col-md-6">
					<h1 class="page-header">
						<%=log.getSecond()%>
						<small>Log</small>
					</h1>
				</div>
		
				<form method="get" action="web_log">
					<div class="col-lg-6">
						<div class=input-group input-group-lg>
							<input name="filter_text" id="filter" type="text"
								class="form-control"> <input name="type"
								value=<%=log.getSecond()%> hidden="true"> <span
								class="input-group-btn">
								<button class="btn btn-default" type="submit">Filter</button>
							</span>
						</div>
						<script>
							$('#filter').datepicker({
								dateFormat : 'yy-mm-dd',
								maxDate : '0'
							}).val();
						</script>
					</div>
				</form>
			</div>
			<!-- /. ROW  -->
			<div class="row">
				<div class="col-md-12">
					<div id="tomcat_log"
						style="height: 100%; overflow: scroll; border: 1px solid rgba(67, 67, 67, 0.36); padding-left: 4px;">

						<%
						  if(log.getFirst() != null) {
						    String line;
						      while((line = log.getFirst().readLine()) != null) {
						%>
						"<%=line%>"<br>
						<%
						  }
						  }
						%>
					</div>
				</div>
			</div>
		</div>
		<script type="application/javascript">
      $(document).ready(function() {
        $('#tomcat_log').scrollTop($('#tomcat_log').height());
      }          
		</script>
		<!-- /. return to top  -->
		<a href="#" id="back-to-top" title="Back to top"
			style="position: fixed; bottom: 20px; left: 20px; z-index: 9999; width: 32px; height: 32px; text-align: center; line-height: 30px; background: #eaeaea; color: #444; cursor: pointer; border: 0; border-radius: 2px; text-decoration: none; transition: opacity 0.2s ease-out; opacity: 0; -webkit-box-shadow: 0px 1px 16px 0px rgba(50, 50, 50, 1); -moz-box-shadow: 0px 1px 16px 0px rgba(50, 50, 50, 1); box-shadow: 0px 1px 16px 0px rgba(50, 50, 50, 1);">Top</a>

		<script type="application/javascript">
      if ($('#back-to-top').length) {
        var scrollTrigger = 100, // px
        backToTop = function () {
          var scrollTop = $(window).scrollTop();
          if (scrollTop > scrollTrigger) {
            $('#back-to-top').css("opacity", "0.7");
          } else {
            $('#back-to-top').css("opacity", "0");
          }
        };
        backToTop();
        $(window).on('scroll', function () {
          backToTop();
        });
        $('#back-to-top').on('click', function (e) {
          e.preventDefault();
          $('html,body').animate({
            scrollTop: 0
          }, 700);
        });
      } 
		</script>
		<%
		  }
		%>
		pre-alpha 1.0 version © 2015 Sinapsi
	</div>
	</div>
	<script src="assets/js/jquery-1.10.2.js"></script>
	<script src="assets/js/bootstrap.min.js"></script>
	<script src="assets/js/jquery.metisMenu.js"></script>
	<script src="assets/js/custom-scripts.js"></script>
</body>
</html>