<%@ page import="java.util.List"%>
<%@ page import="com.sinapsi.model.MacroInterface"%>
<%@ page import="com.sinapsi.engine.Action"%>
<%@ page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Sinapsi</title>
<link href="assets/css/bootstrap.css" rel="stylesheet" />
<link href="assets/css/font-awesome.css" rel="stylesheet" />
<link href="assets/css/custom-styles.css" rel="stylesheet" />
<link href='http://fonts.googleapis.com/css?family=Lato:300,400,700'
	rel='stylesheet' type='text/css' />
</head>

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
		  
		  String role = (String) session.getAttribute("role");
		  @SuppressWarnings("unchecked")
		  List<MacroInterface> macros = (List<MacroInterface>) session.getAttribute("macros");
		  @SuppressWarnings("unchecked")
		  Map<Integer, String> devices = (Map<Integer, String>) session.getAttribute("devices");
		  @SuppressWarnings("unchecked")
		  Map<Integer, String> triggeredDevices = (Map<Integer, String>) session.getAttribute("triggeredDevice");
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
				<!-- /.dropdown -->
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
					<li><a class="active-menu" href="web_macro_manager"><i
							class="fa fa-wrench"></i> Macro Manager </a></li>
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
					<li><a href="#"><i class="fa fa-fw fa-file"></i> Log <span
							class="fa arrow"></span></a>
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
				<div class="row">
					<div class="col-md-12">
						<h1 class="page-header">
							Sinapsi <small>Macro Manager</small>
						</h1>
					</div>
				</div>
				
				<div class="modal fade" id="confirm-delete" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">Confirm Delete</h4>
							</div>

							<div class="modal-body">
								<p>You are about to delete this Macro, this procedure is
									irreversible.</p>
								<p>Do you want to proceed?</p>
							</div>

							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<a class="btn btn-danger btn-ok">Delete</a>
							</div>
						</div>
					</div>
				</div>
		
				<div class="row">
					<div class="col-md-12">
						<%
						  if(macros.size() == 0) {
						%>
						<h1>
							<center>You have not any macro yet!</center>
						</h1>
						<%
						  } else {
						%>
						<%
						  int counter = 0;
							for(MacroInterface macro : macros) {
												                         
							  String triggerParameters =   macro.getTrigger().getActualParameters();  
								triggerParameters = triggerParameters.replace("{", "");
								triggerParameters = triggerParameters.replace("}", "");
								triggerParameters = triggerParameters.replace("\"", "");
								triggerParameters = triggerParameters.replace("parameters:", "");
								triggerParameters = triggerParameters.replace(":", " : ");
								triggerParameters = triggerParameters.replace("_", " ");
								triggerParameters = triggerParameters.replace("TRIGGER", "");
												                         
								String triggerName = macro.getTrigger().getName();
								triggerName = triggerName.replace("_", " ");
						%>
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3>
									<a data-toggle="collapse" data-parent="#accordion"
										href="#<%=counter%>" class="collapsed"
										style="text-decoration: none; color: <%=macro.getMacroColor()%>"> <%=macro.getName()%> <small>starts
											on device <%=triggeredDevices.get(macro.getTrigger().getExecutionDevice().getId())%>
									</small>
									</a>

									<div class="btn-toolbar">
										<button
											data-href="web_macro_manager?action=delete&macro=<%=macro.getId()%>"
											data-toggle="modal" data-target="#confirm-delete"
											class="btn btn-danger pull-right">
											<i class="fa fa-pencil"></i> Delete
										</button>
										<button class="btn btn-primary pull-right">
											<i class="fa fa-edit "></i> Edit
										</button>
									</div>
								</h3>
							</div>


							<div id="<%=counter++%>" class="panel-collapse collapse"
								style="height: 0px;">
								<div class="panel-body">
									<div class="alert alert-info">
										<h4>
											<strong>Triggered by </strong>
										</h4>
										<%=triggerName%>
										<h5>
											<strong>On parameters </strong>
										</h5>
										<%=triggerParameters%>
									</div>

									<div class="alert alert-warning">
										<h4>
											<strong>Actions executed</strong>
										</h4>
										<%
										  for(Action action : macro.getActions()) {
											  String actionParameters = action.getActualParameters();
												actionParameters = actionParameters.replace("{", "");
												actionParameters = actionParameters.replace("}", "");
												actionParameters = actionParameters.replace("\"", "");
												actionParameters = actionParameters.replace("parameters:", "");
												actionParameters = actionParameters.replace(":", " : ");
												actionParameters = actionParameters.replace("_", " ");
																				                                    
												String actionName = action.getName();
												actionName = actionName.replace("_", " ");
										%>
										<div class="alert alert-success">
											<%=actionName%>
											<h5>
												<strong>On parameters </strong>
											</h5>
											<%=actionParameters%>
											<h5>
												<strong>Executed on device </strong>
											</h5>
											<%=devices.get(action.getExecutionDevice().getId())%>
										</div>
										<%
										  }
										%>
									</div>
								</div>
							</div>
						</div>
						<%
						  }
						  }
						%>
					</div>
				</div>
			</div>

			<!-- /. ROW  -->
			pre-alpha 1.0 version © 2015 Sinapsi
		</div>
	</div>
	<script src="assets/js/jquery-1.10.2.js"></script>
	<script src="assets/js/bootstrap.min.js"></script>
	<script src="assets/js/jquery.metisMenu.js"></script>
	<script src="assets/js/custom-scripts.js"></script>
	<script>
		$('#confirm-delete').on(
				'show.bs.modal',
				function(e) {
					$(this).find('.btn-danger').attr('href',
							$(e.relatedTarget).data('href'));
					$('.debug-url').html(
							'Delete URL: <strong>'
									+ $(this).find('.btn-ok').attr('href')
									+ '</strong>');
				});
	</script>
</body>
</html>